package com.ulab.uchat.server.security.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.ulab.uchat.server.handler.ConnectionHandler;
import com.ulab.uchat.server.security.domain.ErrorStatus;
import com.ulab.uchat.server.security.domain.ResultJson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

@Component
public class UchatAuthFailHandler implements AuthenticationEntryPoint, Serializable {
    private static final Logger log = LoggerFactory.getLogger(UchatAuthFailHandler.class);

    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.info("Authentication Failed", authException);
        response.setStatus(200);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter printWriter = response.getWriter();
        String body = ResultJson.failure(ErrorStatus.Password_mismatch, authException.getMessage()).toString();
        printWriter.write(body);
        printWriter.flush();
    }
}
