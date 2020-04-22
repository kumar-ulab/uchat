package com.ulab.uchat.model.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Device")
public class ChatDevice {
	@ApiModelProperty("device type")
	private String deviceType;
	@ApiModelProperty("push Address")
	private String pushAddress;

	public ChatDevice() {
		deviceType = "web";
		pushAddress = null;
	}
	
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getPushAddress() {
		return pushAddress;
	}
	public void setPushAddress(String pushAddress) {
		this.pushAddress = pushAddress;
	}
}
