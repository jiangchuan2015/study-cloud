package chuan.study.cloud.exception;

import chuan.study.cloud.pojo.enums.ResponseCodeEnum;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
public class DataNotFoundException extends BusinessException {
    private static final long serialVersionUID = -7465441277582472456L;

    public DataNotFoundException(ResponseCodeEnum responseCode) {
        super(responseCode);
    }

    public DataNotFoundException(ResponseCodeEnum responseCode, Object... objects) {
        super(responseCode, objects);
    }

    public DataNotFoundException(String msg) {
        super(msg);
    }

    public DataNotFoundException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
