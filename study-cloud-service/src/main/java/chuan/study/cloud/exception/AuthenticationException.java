package chuan.study.cloud.exception;

import chuan.study.cloud.pojo.enums.ResponseCodeEnum;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
public class AuthenticationException extends SystemException {
    private static final long serialVersionUID = 7132125363768518519L;

    public AuthenticationException(ResponseCodeEnum responseCode) {
        super(responseCode);
    }

    public AuthenticationException(ResponseCodeEnum responseCode, Object... objects) {
        super(responseCode, objects);
    }

    public AuthenticationException(final boolean recordStackTrace, ResponseCodeEnum responseCode, Object... objects) {
        super(recordStackTrace, responseCode, objects);
    }

    public AuthenticationException(String msg) {
        super(msg);
    }

    public AuthenticationException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
