package com.ulab.uchat.pojo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatPairGroup {
	private Map<String, ChatPair> group = new ConcurrentHashMap<>();
	
	public void addPair(ChatPair pairUser) {
		group.put(pairUser.getUser().getId(), pairUser);
	}

	public void removePair(String userId) {
		group.remove(userId);
	}
	
	public ChatPair getPair(String userId) {
		return group.get(userId);
	}
}
