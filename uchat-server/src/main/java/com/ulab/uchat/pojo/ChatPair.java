package com.ulab.uchat.pojo;

import com.ulab.uchat.model.pojo.User;

import io.netty.channel.Channel;

public class ChatPair {
	private User user;
	private Channel channel;
	
	public ChatPair(User user, Channel channel) {
		this.user = user;
		this.channel = channel;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
}
