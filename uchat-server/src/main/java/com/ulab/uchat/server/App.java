package com.ulab.uchat.server;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;

@EnableAutoConfiguration
@SpringBootApplication
public class App 
{
    public static void main( String[] args ) throws Exception {
		SpringApplication app = new SpringApplication(App.class);
		app.run(args);
//		UchatServer uchatServer = SpringUtil.getBean(UchatServer.class);
//		uchatServer.run(9090);
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //单个文件最大
        factory.setMaxFileSize("2000MB"); //KB,MB
        factory.setMaxRequestSize("2000MB");
        return factory.createMultipartConfig();
    }
}
