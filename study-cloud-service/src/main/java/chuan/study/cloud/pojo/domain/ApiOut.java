package chuan.study.cloud.pojo.domain;

import chuan.study.cloud.pojo.enums.ResponseCodeEnum;
import chuan.study.cloud.util.NumberUtils;
import chuan.study.cloud.util.SystemClock;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.pagehelper.Page;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Optional;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Data
@ToString
@NoArgsConstructor
public class ApiOut<T> {
    @ApiModelProperty(notes = "状态名称")
    private String state;

    @ApiModelProperty(notes = "状态编码")
    private Integer code;

    @ApiModelProperty(notes = "状态信息")
    private String message;

    @ApiModelProperty(notes = "调试信息")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String debugMessage;

    @ApiModelProperty(notes = "花费时间")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long elapsedTime;

    @ApiModelProperty(notes = "服务器时间")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date serverTime;

    @ApiModelProperty(notes = "分页信息")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Pagination pagination;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    /**
     * 以快捷的方式创建参数校验响应
     *
     * @param message 提示的消息
     * @param <T>     返回的类型
     * @return ApiOut
     */
    public static <T> ApiOut<T> newParameterRequiredResponse(String message) {
        return newResponse(ResponseCodeEnum.PARAMETER_REQUIRED, message);
    }

    /**
     * 以快捷的方式创建成功的响应
     *
     * @param <T> 返回的类型
     * @return ApiOut
     */
    public static <T> ApiOut<T> newSuccessResponse(T data) {
        return null == data
                ? new Builder<T>().code(ResponseCodeEnum.SUCCESS).build()
                : new Builder<T>().code(ResponseCodeEnum.SUCCESS).data(data).build();
    }


    /**
     * 以快捷的方式创建响应
     *
     * @param code    返回码
     * @param message 提示的消息
     * @param <T>     返回的类型
     * @return ApiOut
     */
    public static <T> ApiOut<T> newResponse(ResponseCodeEnum code, String message) {
        if (StringUtils.isBlank(message)) {
            return new Builder<T>().code(code).build();
        }

        if (StringUtils.isBlank(code.getTemplate())) {
            return new Builder<T>().code(code).message(message).build();
        }

        return new Builder<T>().code(code).message(String.format(code.getTemplate(), message)).build();
    }

    public static final class Builder<T> {
        private boolean debug = false;
        private ResponseCodeEnum responseCode = ResponseCodeEnum.SUCCESS;
        private Integer code;
        private String message;
        private String debugMessage;
        private Long elapsedTime;
        private Long startTime;
        private Integer totalRow;
        private Integer pageSize;
        private Integer pageIndex;
        private T data;

        public Builder() {
            if (this.debug) {
                startTime = System.currentTimeMillis();
            }
        }

        public Builder(boolean debug) {
            this.debug = debug;
            if (this.debug) {
                startTime = System.currentTimeMillis();
            }
        }

        public Builder<T> code(Integer code) {
            this.code = code;
            return this;
        }

        public Builder<T> code(ResponseCodeEnum responseCode) {
            this.responseCode = responseCode;
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> debugMessage(String debugMessage) {
            this.debugMessage = debugMessage;
            return this;
        }

        public Builder<T> elapsedTime(long elapsedTime) {
            if (elapsedTime > 0) {
                this.elapsedTime = elapsedTime;
            }
            return this;
        }

        public Builder<T> totalRow(int totalRow) {
            if (totalRow > 0) {
                this.totalRow = totalRow;
            }
            return this;
        }

        public Builder<T> pageSize(int pageSize) {
            if (pageSize > 0) {
                this.pageSize = pageSize;
            }
            return this;
        }

        public Builder<T> pageIndex(int pageIndex) {
            if (pageIndex > 0) {
                this.pageIndex = pageIndex;
            }
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public ApiOut<T> build() {
            if (data instanceof Page) {
                Page page = (Page) data;
                this.totalRow = (int) page.getTotal();
                this.pageSize = page.getPageSize();
                this.pageIndex = page.getPageNum();
            }
            return new ApiOut<>(this);
        }
    }

    private ApiOut(Builder<T> builder) {
        this.code = Optional.ofNullable(builder.code).orElse(builder.responseCode.getCode());
        this.state = Optional.of(builder.responseCode).orElse(ResponseCodeEnum.SUCCESS).name();
        this.message = StringUtils.isBlank(builder.message) ? builder.responseCode.getDesc() : builder.message;
        this.debugMessage = StringUtils.isBlank(builder.debugMessage) ? null : builder.debugMessage;

        if (null != builder.elapsedTime) {
            this.elapsedTime = builder.elapsedTime;
        } else if (builder.debug) {
            this.elapsedTime = System.currentTimeMillis() - builder.startTime;
        }

        if (NumberUtils.isPositive(builder.totalRow)
                || NumberUtils.isPositive(builder.pageSize)
                || NumberUtils.isPositive(builder.pageIndex)) {
            this.pagination = new Pagination(builder.totalRow, builder.pageSize, builder.pageIndex);
        }
        this.data = builder.data;
        this.serverTime = SystemClock.nowDate();
    }
}
