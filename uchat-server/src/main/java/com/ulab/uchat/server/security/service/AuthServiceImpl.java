package com.ulab.uchat.server.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ulab.uchat.server.dao.AuthDao;
import com.ulab.uchat.server.exception.AppException;
import com.ulab.uchat.server.security.JwtUtils;
import com.ulab.uchat.server.security.auth.ResponseUserToken;
import com.ulab.uchat.server.security.auth.UserAuthInfo;
import com.ulab.uchat.server.security.domain.ErrorStatus;

import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtTokenUtil;
    private final AuthDao authMapper;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager, @Qualifier("CustomUserDetailsService") UserDetailsService userDetailsService, JwtUtils jwtTokenUtil, AuthDao authMapper) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authMapper = authMapper;
    }

    @Override
    public UserAuthInfo login(String username, String password) {
        final Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final UserAuthInfo userAuthInfo = (UserAuthInfo) authentication.getPrincipal();
        final String token = jwtTokenUtil.generateAccessToken(userAuthInfo);
        userAuthInfo.setToken(token);
        return userAuthInfo;

    }

    @Override
    public void logout(String token) {
    }

    @Override
    public ResponseUserToken refresh(String oldToken) {
        String token = oldToken.substring(tokenHead.length());
        String username = jwtTokenUtil.getUsernameFromToken(token);
        UserAuthInfo userAuthInfo = (UserAuthInfo) userDetailsService.loadUserByUsername(username);
        if (jwtTokenUtil.canTokenBeRefreshed(token, userAuthInfo.getLastPasswordResetDate())){
            token =  jwtTokenUtil.refreshToken(token);
            return new ResponseUserToken(token, userAuthInfo);
        }
        return null;
    }

    @Override
    public UserAuthInfo getUserByToken(String token) {
        token = token.substring(tokenHead.length());
        return jwtTokenUtil.getUserFromToken(token);
    }

    private Authentication authenticate(String username, String password) {
        try {
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException | BadCredentialsException e) {
            throw new AppException(ErrorStatus.Password_mismatch, e.getMessage());
        }
    }
}
