package chuan.study.cloud.pojo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseModel implements Serializable, Cloneable {
    private static final long serialVersionUID = 5865257677377600799L;
    private Date createdTime;
    private Date updatedTime;
}
