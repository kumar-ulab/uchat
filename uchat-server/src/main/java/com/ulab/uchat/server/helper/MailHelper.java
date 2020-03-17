package com.ulab.uchat.server.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.ulab.uchat.server.exception.AppException;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Properties;

@Component
public class MailHelper {
	@Value("${mail.protocol}")
    public String protocol;
	@Value("${mail.host}")
    public String host;
	@Value("${mail.port}")
    public Integer port;
	@Value("${mail.username}")
    public String userName;
	@Value("${mail.password}")
    public String passWord;
	@Value("${mail.from.email}")
    public String fromEmail;
	@Value("${mail.from.name}")
    public String fromMame;

	private JavaMailSenderImpl mailSender;
	
	@PostConstruct
    private void createMailSender() {
		mailSender = new JavaMailSenderImpl();
		mailSender.setProtocol(protocol);
		mailSender.setHost(host);
		mailSender.setPort(port);
        mailSender.setUsername(userName);
        mailSender.setPassword(passWord);
        mailSender.setDefaultEncoding("Utf-8");
        Properties p = new Properties();
        p.setProperty("mail.smtp.auth", "false");
        p.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        mailSender.setJavaMailProperties(p);
    }

    public void sendMail(String to, String subject, String html) {
    	try {
	        MimeMessage mimeMessage = mailSender.createMimeMessage();
	        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
	        messageHelper.setFrom(fromEmail, fromMame);
	        messageHelper.setTo(to);
	        messageHelper.setSubject(subject);
	        messageHelper.setText(html, true);
	        mailSender.send(mimeMessage);
    	} catch (Exception e) {
    		throw new AppException(e);
    	}
    }

    public void sendAttachmentMail(String to, String subject, String html, String filePath) {
    	try {
	        MimeMessage mimeMessage = mailSender.createMimeMessage();
	        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
	        messageHelper.setFrom(fromEmail, fromMame);
	        messageHelper.setTo(to);
	        messageHelper.setSubject(subject);
	        messageHelper.setText(html, true);
	        FileSystemResource file=new FileSystemResource(new File(filePath));
	        String fileName=filePath.substring(filePath.lastIndexOf(File.separator));
	        messageHelper.addAttachment(fileName,file);
	        mailSender.send(mimeMessage);
    	} catch (Exception e) {
    		throw new AppException(e);
    	}
    }
}
