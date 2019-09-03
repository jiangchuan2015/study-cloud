package chuan.study.cloud.pojo.domain.user;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Data
@ToString
@NoArgsConstructor
public class UserContext implements Serializable, Cloneable {
    private static final long serialVersionUID = 2821713151093928036L;

    /**
     * 使用服务的主机
     */
    private String host;

    /**
     * 登录成功的用户ID
     */
    private Integer userId;

    /**
     * 用户的真名 | 昵称 | 手机号 | 登录名
     */
    private String userName;

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 用户手机号
     */
    private String mobile;

    /**
     * 登录时的授权令牌
     */
    private String token;

    /**
     * 授权令牌是否已过期
     */
    private boolean tokenExpired = false;

    @Builder
    public UserContext(String host, Integer userId, String userName, Integer userType, String mobile, String token, boolean tokenExpired) {
        this.host = host;
        this.userId = userId;
        this.userName = userName;
        this.userType = userType;
        this.mobile = mobile;
        this.token = token;
        this.tokenExpired = tokenExpired;
    }
}
