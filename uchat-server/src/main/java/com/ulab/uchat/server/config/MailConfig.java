package com.ulab.uchat.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailConfig {
	@Value("${mail.host}")
    public static String host;
	@Value("${mail.port}")
    public static Integer port;
	@Value("${mail.userName}")
    public static String userName;
	@Value("${mail.passWord}")
    public static String passWord;
	@Value("${mail.form.email}")
    public static String fromEmail;
	@Value("${mail.from.name}")
    public static String fromMame;
	
	public static String getHost() {
		return host;
	}
	public static Integer getPort() {
		return port;
	}
	public static String getUserName() {
		return userName;
	}
	public static String getPassWord() {
		return passWord;
	}
	public static String getFromEmail() {
		return fromEmail;
	}
	public static String getFromMame() {
		return fromMame;
	}
}
