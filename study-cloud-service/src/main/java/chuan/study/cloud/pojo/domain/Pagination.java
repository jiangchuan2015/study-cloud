package chuan.study.cloud.pojo.domain;

import chuan.study.cloud.util.NumberUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Data
@ToString
@NoArgsConstructor
public class Pagination {
    @ApiModelProperty(notes = "总行数")
    private Integer totalRow;

    @ApiModelProperty(notes = "每页行数")
    private Integer pageSize;

    @ApiModelProperty(notes = "当前页码")
    private Integer pageIndex;

    @ApiModelProperty(notes = "总页数")
    private Integer pageCount;

    @Builder
    public Pagination(Integer totalRow, Integer pageSize, Integer pageIndex) {
        this.totalRow = totalRow;
        this.pageSize = pageSize;
        this.pageIndex = pageIndex;
        if (NumberUtils.isPositive(totalRow) && NumberUtils.isPositive(pageSize)) {
            this.pageCount = (int) (Math.ceil(1.0 * totalRow / pageSize));
        }
    }
}
