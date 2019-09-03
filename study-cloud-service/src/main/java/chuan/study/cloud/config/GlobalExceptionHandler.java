package chuan.study.cloud.config;

import chuan.study.cloud.common.Constants;
import chuan.study.cloud.exception.BusinessException;
import chuan.study.cloud.pojo.domain.ApiOut;
import chuan.study.cloud.pojo.enums.ResponseCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String PACKAGE_PREFIX = "chuan.study.abc";

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiOut handleError(MissingServletRequestParameterException ex) {
        log.error(ex.getMessage(), ex);
        return ApiOut.newParameterRequiredResponse(ex.getParameterName() + ", " + ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiOut handleError(MethodArgumentTypeMismatchException ex) {
        log.error(ex.getMessage(), ex);
        return ApiOut.newParameterRequiredResponse(ex.getName() + ", " + ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiOut handleError(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage(), ex);
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("%s(%s)", error.getDefaultMessage(), error.getField()))
                .collect(Collectors.joining(" | "));

        return ApiOut.newParameterRequiredResponse(errorMessage);
    }

    @ExceptionHandler(BindException.class)
    public ApiOut handleError(BindException ex) {
        log.error(ex.getMessage(), ex);
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("%s(%s)", error.getDefaultMessage(), error.getField()))
                .collect(Collectors.joining(" | "));

        return ApiOut.newParameterRequiredResponse(errorMessage);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiOut handleError(ConstraintViolationException ex) {
        log.error(ex.getMessage(), ex);
        String errorMessage = ex.getConstraintViolations().stream()
                .map(error -> String.format("%s(%s)", error.getMessage(), error.getPropertyPath()))
                .collect(Collectors.joining(" | "));

        return ApiOut.newParameterRequiredResponse(errorMessage);
    }


    @ResponseBody
    @ExceptionHandler(value = {Throwable.class, NoHandlerFoundException.class})
    public ApiOut handleThrowable(HttpServletRequest request, Throwable ex) {
        log.error(ex.getMessage(), ex);
        ResponseCodeEnum responseCode = ResponseCodeEnum.SYSTEM_ERROR;
        String message = ex.getMessage();
        Pair<String, String> debugMsgPair;
        if (ex instanceof BusinessException) {
            BusinessException exception = (BusinessException) ex;
            if (null != exception.getResponseCode()) {
                responseCode = exception.getResponseCode();
                message = exception.getMessage();
            }
            debugMsgPair = getCause(ex);
            log.warn(String.format("Code:%s-%s, Msg:%s", responseCode.name(), responseCode.getCode(), exception.getMessage()), exception);
        } else if (ex instanceof ServletRequestBindingException && StringUtils.contains(ex.getMessage(), Constants.TOKEN_PARA_NAME)) {
            debugMsgPair = Pair.of(ex.getMessage(), StringUtils.EMPTY);
            responseCode = ResponseCodeEnum.LOGIN_FAILED;
            message = "请登录";
        } else {
            debugMsgPair = getCause(ex);
        }

        log.error(debugMsgPair.getLeft());
        return new ApiOut.Builder<>().code(responseCode).message(message).debugMessage(debugMsgPair.getLeft()).build();
    }


    /**
     * 获取错误日志堆栈
     *
     * @param throwable 异常
     * @return left: 异常消息, right: 文件名
     */
    private Pair<String, String> getCause(Throwable throwable) {
        if (null == throwable) {
            return Pair.of(StringUtils.EMPTY, StringUtils.EMPTY);
        }

        StringBuilder detailInfo = new StringBuilder();
        String message = throwable.getMessage();
        if (Strings.isBlank(message)) {
            message = throwable.toString();
        }

        detailInfo.append(message);
        String fileName = "";
        StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        if (ArrayUtils.isNotEmpty(stackTraceElements)) {
            for (StackTraceElement stackTraceElement : stackTraceElements) {
                if (StringUtils.startsWithIgnoreCase(stackTraceElement.getClassName(), PACKAGE_PREFIX)) {
                    fileName = stackTraceElement.getFileName();
                    detailInfo.append("@").append(stackTraceElement.toString());
                    break;
                }
            }
        }
        return Pair.of(detailInfo.toString(), fileName);
    }
}
