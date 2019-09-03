package chuan.study.cloud.exception;

import chuan.study.cloud.pojo.enums.ResponseCodeEnum;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
public class SystemException extends RuntimeException {
    private static final long serialVersionUID = 8730577614813897337L;

    private ResponseCodeEnum responseCode;
    private String message;

    /**
     * 创建系统异常
     *
     * @param responseCode 异常状态码
     */
    public SystemException(final ResponseCodeEnum responseCode) {
        super(String.valueOf(responseCode.getCode()));
        this.responseCode = responseCode;
        this.message = responseCode.getDesc();
    }

    /**
     * 创建系统异常
     *
     * @param responseCode 异常状态码
     * @param objects      模板格式化数据
     */
    public SystemException(final ResponseCodeEnum responseCode, Object... objects) {
        super(String.valueOf(responseCode.getCode()));
        this.responseCode = responseCode;
        message = ArrayUtils.isEmpty(objects) || StringUtils.isBlank(responseCode.getTemplate())
                ? responseCode.getDesc()
                : String.format(this.responseCode.getTemplate(), objects);
    }

    /**
     * 创建系统异常
     *
     * @param recordStackTrace 是否记录堆栈信息
     * @param responseCode     异常状态码
     * @param objects          模板格式化数据
     */
    public SystemException(final boolean recordStackTrace, final ResponseCodeEnum responseCode, Object... objects) {
        super(String.valueOf(responseCode.getCode()), null, false, recordStackTrace);
        this.responseCode = responseCode;
        message = ArrayUtils.isEmpty(objects) || StringUtils.isBlank(responseCode.getTemplate())
                ? responseCode.getDesc()
                : String.format(this.responseCode.getTemplate(), objects);
    }


    /**
     * 创建系统异常
     *
     * @param msg 异常信息
     */
    public SystemException(final String msg) {
        super(msg);
    }

    /**
     * 创建系统异常
     *
     * @param msg 异常信息
     * @param ex  异常根源
     */
    public SystemException(final String msg, final Throwable ex) {
        super(msg, ex);
    }

    public ResponseCodeEnum getResponseCode() {
        return responseCode;
    }

    @Override
    public String getMessage() {
        return Optional.ofNullable(message).orElse(super.getMessage());
    }
}