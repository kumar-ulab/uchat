package com.ulab.uchat.server.security.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ulab.uchat.server.security.JwtUtils;
import com.ulab.uchat.server.security.auth.UserAuthInfo;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class UchatAuthTokenFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(UchatAuthTokenFilter.class);

    @Value("${jwt.token.header}")
    private String tokenHeader;

    @Resource
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {    	String token = getTokenFromRequest(request);
        if (jwtUtils.isTokenValidate(token)) {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
            	UserAuthInfo userAuthInfo = jwtUtils.getUserFromToken(token);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userAuthInfo, null, userAuthInfo.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                log.info(String.format("Authenticated user %s, setting security context", userAuthInfo.getUsername()));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        };

        chain.doFilter(request, response);
    }
    
    private String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(this.tokenHeader);
        if (token != null) {
        	return token;
        }
    	String query = request.getQueryString();
    	if (query == null) {
    		return null;
    	}
    	int start = query.indexOf("Chat-Token=");
    	if (start < 0) {
    		return null;
    	}
    	start += "Chat-Token=".length();
    	if (start < 0) {
    		return null;
    	}
    	int end = query.lastIndexOf('&', start);
    	if (end > 0) {
    		token = query.substring(start, end);
    	} else {
    		token = query.substring(start);
    	}
        return token;
    }
}
