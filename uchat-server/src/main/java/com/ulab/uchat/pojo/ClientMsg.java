package com.ulab.uchat.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("client -> server message")
public class ClientMsg {
	@ApiModelProperty("Message Type:  0-connect, 1-text msg, 2-picture message")
	int type;
	@ApiModelProperty("message send to this userId, no use for connect type")
	String toUserId;
	@ApiModelProperty("type1 - token, type2 - text message, type3 - picture filename")
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
