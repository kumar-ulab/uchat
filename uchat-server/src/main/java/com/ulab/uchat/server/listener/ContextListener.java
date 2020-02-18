package com.ulab.uchat.server.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.ulab.uchat.server.UchatServer;
import com.ulab.uchat.server.exception.AppException;

@Component
public class ContextListener  implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger log = LoggerFactory.getLogger(ContextListener.class);

    @Autowired private UchatServer uchatServer;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent){
		try {
			new Thread(uchatServer).start();
		} catch (Exception e) {
			log.error("start Uchat server failed", e);
			System.exit(1);
		}
    }
}
