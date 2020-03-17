package com.ulab.uchat.server.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ulab.uchat.pojo.GeneralRsp;
import com.ulab.uchat.server.security.domain.ErrorStatus;

@RestControllerAdvice
	public class AppExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(AppExceptionHandler.class);
    @ExceptionHandler(AppException.class)
    public ResponseEntity<GeneralRsp> handleCustomException(AppException e){
    	GeneralRsp errRsp = new GeneralRsp();
    	errRsp.setStatus(ErrorStatus.Bad_request.getCode());
    	errRsp.setMessage(e.getMessage());
    	ErrorStatus status = e.getStatus();
    	HttpStatus httpStatus = status==ErrorStatus.Internal_Error ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.BAD_REQUEST;
        log.error("Application error: status=" + e.getStatus().getCode() + ", message=\"" + e.getMessage() + "\"");
        return new ResponseEntity<GeneralRsp>(errRsp, httpStatus);
//    	GeneralRsp errRsp = new GeneralRsp();
//    	errRsp.setStatus(e.getStatus().getCode());
//    	errRsp.setMessage(e.getMessage());
//        log.error("Application error: status=" + e.getStatus(), e);
//        return errRsp;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GeneralRsp> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error(e.getBindingResult().getFieldError().getField() + e.getBindingResult().getFieldError().getDefaultMessage());
    	GeneralRsp errRsp = new GeneralRsp();
    	errRsp.setStatus(ErrorStatus.Bad_request.getCode());
    	errRsp.setMessage(e.getMessage());
        log.error("Application error: status=" + ErrorStatus.Bad_request + ", message=\"" + e.getMessage() + "\"");
        return new ResponseEntity<GeneralRsp>(errRsp, HttpStatus.BAD_REQUEST);
    }

}
