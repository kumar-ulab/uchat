package com.ulab.uchat.server.task;

import java.util.List;

import com.ulab.uchat.server.push.PushMsg;

public class PushMessageTask implements Runnable {
	
	private String title;
	
	private String content;
	
	private List<String> deviceTokens;
	
	public PushMessageTask(String title, String content, List<String> deviceTokens) {
		this.title = title;
		this.content = content;
		this.deviceTokens = deviceTokens;
	}

	@Override
	public void run() {
		PushMsg.send(deviceTokens, title, content);
	}

}
