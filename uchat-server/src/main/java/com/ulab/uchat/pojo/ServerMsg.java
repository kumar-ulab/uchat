package com.ulab.uchat.pojo;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("server -> client message")
public class ServerMsg {
	@ApiModelProperty("Message Type:  0-connect, 1-text msg, 2-picture message, 3-system notification, 4-file message")
	int type;
	@ApiModelProperty("the chat channelassigned to you")
	String channel;
	@ApiModelProperty("device the message is sent from")
	String device;
	@ApiModelProperty("userId who the message is sent from")
	String fromUserId;
	@ApiModelProperty("type0 - welcome message, type1 - text message, type2 - picture filename, type3 - system message, type4 - filename message")
	String data;
	@ApiModelProperty("time the message is sent from")
	Date createTime;
	
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
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getFromUserId() {
		return fromUserId;
	}
	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
