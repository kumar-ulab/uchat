package com.ulab.uchat.pojo;

import io.swagger.annotations.ApiModel;

@ApiModel("chat message for receive")
public class ServerMsg {
	int type;
	int device;
	String channel;
	String data;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}	
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}	
	public int getDevice() {
		return device;
	}
	public void setDevice(int device) {
		this.device = device;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}	
}
