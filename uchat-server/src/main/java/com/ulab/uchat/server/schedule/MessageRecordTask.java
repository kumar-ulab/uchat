package com.ulab.uchat.server.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ulab.uchat.server.dao.mapper.MapperMessage;

@EnableScheduling
@Component
public class MessageRecordTask {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageRecordTask.class);
	
	@Autowired
	private MapperMessage mapperMessage;
	
	@Scheduled(cron = "0 0 1 * * ?")
	public void deleteMessageRecord() {
		try {
			logger.info("Schedule ->>Delete message start...");
			mapperMessage.deleteMessage();
		} catch (Exception e) {
			logger.error("Delete message fail by schedul");
			logger.error("Method ->> deleteMessageRecord ->> Exception Message:" + e.getMessage());
		}
		
	}

}
