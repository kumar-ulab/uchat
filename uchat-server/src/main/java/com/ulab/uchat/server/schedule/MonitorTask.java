package com.ulab.uchat.server.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ulab.uchat.server.handler.ConnectionHandler;
import com.ulab.uchat.server.service.ChatService;

@EnableScheduling
@Component
public class MonitorTask {
    private static final Logger log = LoggerFactory.getLogger(MonitorTask.class);
	@Autowired ChatService chatService;
	
    @Scheduled(initialDelay = 300000, fixedRate = 300000)
    private void freeInactiveChannel() {
    	log.info("start: free inactive channels ...");
    	chatService.freeInactiveChannel();
    	log.info("end: free inactive channels");
    }
}
