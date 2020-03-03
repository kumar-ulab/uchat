package com.ulab.uchat.server.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ulab.uchat.server.security.JwtUtils;
import com.ulab.uchat.server.security.auth.UserAuthInfo;

import io.netty.util.internal.StringUtil;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Value("${jwt.header}")
    private String token_header;

    @Resource
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String auth_token = request.getHeader(this.token_header);
        final String auth_token_start = "Bearer ";
        if (!StringUtil.isNullOrEmpty(auth_token) && auth_token.startsWith(auth_token_start)) {
            auth_token = auth_token.substring(auth_token_start.length());
            String username = jwtUtils.getUsernameFromToken(auth_token);
            logger.info(String.format("Checking authentication for userDetail %s.", username));

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserAuthInfo userAuthInfo = jwtUtils.getUserFromToken(auth_token);
                if (jwtUtils.validateToken(auth_token, userAuthInfo)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userAuthInfo, null, userAuthInfo.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    logger.info(String.format("Authenticated userDetail %s, setting security context", username));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } else {
            auth_token = null;
        }

        chain.doFilter(request, response);
    }
}
