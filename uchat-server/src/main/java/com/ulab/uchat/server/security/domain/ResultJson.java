package com.ulab.uchat.server.security.domain;

import java.io.IOException;
import java.io.Serializable;

import com.ulab.util.JsonUtil;

public class ResultJson<T> implements Serializable{

    private static final long serialVersionUID = 783015033603078674L;
    private int code;
    private String msg;
    private T data;

    public static ResultJson ok() {
        return ok("");
    }

    public static ResultJson ok(Object o) {
        return new ResultJson(ErrorStatus.OK, o);
    }

    public static ResultJson failure(ErrorStatus code) {
        return failure(code, "");
    }

    public static ResultJson failure(ErrorStatus code, Object o) {
    	return new ResultJson(code, o);
    }

    public ResultJson (ErrorStatus resultCode) {
    	code = resultCode.getCode();
    }

    public ResultJson (ErrorStatus resultCode,T data) {
    	code = resultCode.getCode();
        this.data = data;
    }

    @Override
    public String toString() {
        return "{" +
                "\"code\":" + code +
                ", \"msg\":\"" + msg + '\"' +
                ", \"data\":\"" + data + '\"'+
                '}';
    }
}
