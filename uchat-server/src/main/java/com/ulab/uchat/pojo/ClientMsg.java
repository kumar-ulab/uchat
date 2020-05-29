package com.ulab.uchat.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("client -> server message")
public class ClientMsg {
	@ApiModelProperty("Message Type:  0-connect, 1-text msg, 2-picture message,  4-file message")
	int type;
	@ApiModelProperty("type0 - deviceType@deviceToken, like Iphone8@18F179F34B328341BE26F1A23698961BB56189E777FBD413106D1CDB78C6770F:"
			+ ",  type1-2: userId, message will send to this user")
	String toUserId;
	@ApiModelProperty("type1 - token, type2 - text message, type3 - picture filename, type4 - file filename")
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
	public String getDevice() {
		return toUserId;
	}
	public void setDevice(String toUserId) {
		this.toUserId = toUserId;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}	
}
