package com.ulab.uchat.server.security.auth;

public class ResponseUserToken {
    private String token;
    int expiration;
    private UserAuthInfo userAuthInfo;
    
    public ResponseUserToken(String token, UserAuthInfo userAuthInfo) {
		this.token = token;
		this.userAuthInfo = userAuthInfo;
	}
    
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public UserAuthInfo getUserAuthInfo() {
		return userAuthInfo;
	}
	public void setUserAuthInfo(UserAuthInfo userAuthInfo) {
		this.userAuthInfo = userAuthInfo;
	}
}
