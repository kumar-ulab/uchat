package com.ulab.uchat.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ContextListener  implements ApplicationListener<ContextRefreshedEvent> {
    Logger logger = LoggerFactory.getLogger(ContextListener.class);
    @Autowired UchatClient chatClient;
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
    	try {
			chatClient.run(App.host, App.port);
		} catch (Exception e) {
			logger.error("start client failed", e);
			System.exit(1);
		}
    }
}
