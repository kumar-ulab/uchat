package com.ulab.uchat.server.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationAccessDeniedHandler implements AccessDeniedHandler {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationAccessDeniedHandler.class);

    @Override
	public void handle(HttpServletRequest httpServletRequest, HttpServletResponse resp, AccessDeniedException e)
			throws IOException {
		resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
		resp.setContentType("application/json;charset=UTF-8");
		log.info("----------------------" + 2222);
	}
}
