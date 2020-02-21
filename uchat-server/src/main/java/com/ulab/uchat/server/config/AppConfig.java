package com.ulab.uchat.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.ulab.uchat.server.handler.UchatHttpRequestHandler;

import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

@Component
public class AppConfig {
	@Value("${server.port}")
	int port;
	
	public int getPort() {
		return port;
	}
}