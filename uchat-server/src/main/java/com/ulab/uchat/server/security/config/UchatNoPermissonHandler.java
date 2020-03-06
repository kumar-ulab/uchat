package com.ulab.uchat.server.security.config;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.ulab.uchat.server.security.domain.ErrorStatus;
import com.ulab.uchat.server.security.domain.ResultJson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component("RestAuthenticationAccessDeniedHandler")
public class UchatNoPermissonHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        System.out.println("权限不足：" + e.getMessage());
        response.setStatus(200);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter printWriter = response.getWriter();
        String body = ResultJson.failure(ErrorStatus.Internal_Error, e.getMessage()).toString();
        printWriter.write(body);
        printWriter.flush();
    }
}
