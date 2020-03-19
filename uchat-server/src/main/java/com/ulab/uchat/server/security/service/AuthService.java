package com.ulab.uchat.server.security.service;

import com.ulab.uchat.server.security.auth.ResponseUserToken;
import com.ulab.uchat.server.security.auth.UserAuthInfo;

public interface AuthService {
	UserAuthInfo login(String username, String password);
    boolean remoteAuthentication(String username, String password);
    void logout(String token);
    ResponseUserToken refresh(String oldToken);
    UserAuthInfo getUserByToken(String token);
	void validatePermission(String username);
	void validatePermission(String username, String password);
}
