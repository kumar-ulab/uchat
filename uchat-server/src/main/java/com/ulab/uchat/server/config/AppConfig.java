package com.ulab.uchat.server.config;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ulab.util.MiscUtil;

@Component
public class AppConfig {
    @Value("${uchat.root}") 
    private String uchatRoot; 
    
    @Value("${netty.port}") 
    private int nettyPort; 
    
    @Value("${server.port}") 
    private int httpPort; 

    @Value("${jwt.token.expiration}")
    private int expirationSeconds;
    		
    @Value("${auth.remote.url}") 
    private String remoteAuth;
    
    private static final String IMAGES_DIR = "images";
    
    private static final String FILE_DIR = "file";

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
	
	
	public int getExpirationSeconds() {
		return expirationSeconds;
	}

	public long getExpirationMs() {
		return expirationSeconds * 1000;
	}
	
	
	public String getRemoteAuth() {
		return remoteAuth;
	}

	@PostConstruct
	private void init() {
		converPathSeperator();
	}
	
	private void converPathSeperator() {
		if (File.separatorChar == '\\') {
			String usrHome = System.getProperty("user.home");
			uchatRoot = usrHome + uchatRoot.replace('/', File.separatorChar);
		}
	}
	
	public String getPictureDirPath() {
		return getUchatRoot() + File.separator + IMAGES_DIR;
	}
	
	public String getFileDirPath() {
		return getUchatRoot() + File.separator + FILE_DIR;
	}
}