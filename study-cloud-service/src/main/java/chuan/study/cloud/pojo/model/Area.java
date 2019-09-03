package chuan.study.cloud.pojo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@TableName("base_area")
public class Area extends BaseModel {
    private static final long serialVersionUID = 6545751452680215897L;

    @TableId(value = "area_id", type = IdType.AUTO)
    private Integer id;
    private Integer parentId;
    private String name;
    private String mergedName;
    private String initial;
    private Integer level;
    private String cityCode;
    private String zipCode;

    @Override
    public Area clone() {
        try {
            return (Area) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
