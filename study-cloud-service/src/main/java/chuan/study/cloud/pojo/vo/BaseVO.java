package chuan.study.cloud.pojo.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Data
@NoArgsConstructor
public abstract class BaseVO<VO> {
    @ApiModelProperty(notes = "子节点", hidden = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<VO> children;

    public void addChild(VO child) {
        if (null == this.children) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
    }
}
