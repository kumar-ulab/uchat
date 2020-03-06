package com.ulab.uchat.server.security.domain;

public enum ErrorStatus {
    OK(0),
    Internal_Error(1),
    Bad_request(2),
    Duplicat_User(3),
    No_Such_User(4),
    Password_mismatch(5),
    No_Permission(6);
	
    private int code;

    ErrorStatus(int code) {
        this.code = code;
    }
    
    public int getCode() {
    	return code;
    }
}
