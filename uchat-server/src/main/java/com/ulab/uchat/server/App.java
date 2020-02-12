package com.ulab.uchat.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoConfiguration
@SpringBootApplication
public class App 
{
    public static void main( String[] args ) throws Exception {
		SpringApplication app = new SpringApplication(App.class);
//		app.setWebApplicationType(WebApplicationType.NONE);
		app.run(args);
		new UchatServer(9090).run();
    }
}
