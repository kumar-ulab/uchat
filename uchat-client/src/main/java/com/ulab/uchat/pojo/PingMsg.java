package com.ulab.uchat.pojo;

public class PingMsg {
	byte msgType;
	byte clientType;
	String token;
	
	public PingMsg(int msgType, int clientType, String token) {
		this.msgType = (byte)msgType;
		this.clientType = (byte)clientType;
		this.token = token;
	}
	
	public byte getMsgType() {
		return msgType;
	}
	public void setMsgType(byte msgType) {
		this.msgType = msgType;
	}
	public byte getClientType() {
		return clientType;
	}
	public void setClientType(byte clientType) {
		this.clientType = clientType;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
