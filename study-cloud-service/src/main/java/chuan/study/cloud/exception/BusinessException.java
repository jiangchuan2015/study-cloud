package chuan.study.cloud.exception;

import chuan.study.cloud.pojo.enums.ResponseCodeEnum;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
public class BusinessException extends SystemException {
    private static final long serialVersionUID = 2793005704280074619L;

    public BusinessException(ResponseCodeEnum responseCode) {
        super(responseCode);
    }

    public BusinessException(ResponseCodeEnum responseCode, Object... objects) {
        super(responseCode, objects);
    }

    public BusinessException(final boolean recordStackTrace, ResponseCodeEnum responseCode, Object... objects) {
        super(recordStackTrace, responseCode, objects);
    }

    public BusinessException(String msg) {
        super(msg);
    }

    public BusinessException(String msg, Throwable ex) {
        super(msg, ex);
    }
}