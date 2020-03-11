package com.ulab.uchat.pojo;

import io.swagger.annotations.ApiModel;

@ApiModel("chat message for send")
public class ClientMsg {
	int type;
	String toUserId;
	String data;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getToUserId() {
		return toUserId;
	}
	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}	
}
