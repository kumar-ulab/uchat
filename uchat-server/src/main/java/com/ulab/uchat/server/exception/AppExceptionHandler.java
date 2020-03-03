package com.ulab.uchat.server.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ulab.uchat.pojo.GeneralRsp;
import com.ulab.uchat.server.security.domain.ErrorStatus;

@RestControllerAdvice
	public class AppExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(AppExceptionHandler.class);
    @ExceptionHandler(AppException.class)
    public GeneralRsp handleCustomException(AppException e){
    	GeneralRsp errRsp = new GeneralRsp();
    	errRsp.setStatus(e.getStatus().getCode());
    	errRsp.setMessage(e.getMessage());
        log.error("Application error: status=" + e.getStatus(), e);
        return errRsp;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public GeneralRsp handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error(e.getBindingResult().getFieldError().getField() + e.getBindingResult().getFieldError().getDefaultMessage());
    	GeneralRsp errRsp = new GeneralRsp();
    	errRsp.setStatus(ErrorStatus.Bad_request.getCode());
    	errRsp.setMessage(e.getMessage());
        log.error("Application error: status=" + ErrorStatus.Bad_request + ", message=\"" + e.getMessage() + "\"");
        return errRsp;
    }

}
