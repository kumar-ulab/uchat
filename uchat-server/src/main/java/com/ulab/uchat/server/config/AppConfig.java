package com.ulab.uchat.server.config;

import java.io.File;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.ulab.uchat.server.handler.UchatHttpRequestHandler;

import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

@Component
public class AppConfig {
    @Value("${uchat.root}") 
    private String uchatRoot; 
    
    @Value("${netty.port}") 
    private int nettyPort; 
    
    @Value("${server.port}") 
    private int httpPort; 

	public int getHttpPort() {
		return httpPort;
	}
	
	public boolean isSslEnabled() {
		return (httpPort % 1000 == 443);
	}

	public String getUchatRoot() {
		return uchatRoot;
	}

	public int getNettyPort() {
		return nettyPort;
	}
	
	@PostConstruct
	private void init() {
		if (File.separatorChar == '\\') {
			String usrHome = System.getProperty("user.home");
			uchatRoot = usrHome + uchatRoot.replace('/', File.separatorChar);
		}
	}
}