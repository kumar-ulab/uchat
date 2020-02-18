package com.ulab.uchat.pojo;

public class ChatMsg {
	int type;
	String data;
	
	public ChatMsg() {}
	public ChatMsg(int type, String message) {
		this.type = type;
		this.data = message;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getData() {
		return data;
	}
	public void setData(String message) {
		this.data = message;
	}
}
