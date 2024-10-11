package com.cct.codebox.exception;



import com.cct.codebox.domain.BaseResponse;
import com.cct.codebox.domain.enums.RespCodeEnum;
import com.cct.codebox.utils.RespUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * @author cct
 *
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return RespUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public BaseResponse<?> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("httpMessageNotReadableException", e);
        return RespUtils.error(RespCodeEnum.PARAMS_ERROR.getCode(), "参数错误，请求体不能为空");
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return RespUtils.error(RespCodeEnum.SYSTEM_ERROR.getCode(), "系统错误");
    }

    /**
     * 参数校验处理
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        StringBuilder errorMsg = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(x -> errorMsg.append(x.getField()).append(x.getDefaultMessage()).append(","));
        String msg = errorMsg.toString();
        return RespUtils.error(RespCodeEnum.PARAMS_ERROR.getCode(), msg.substring(0,msg.length()-1));
    }



}
