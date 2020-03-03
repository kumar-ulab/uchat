package com.ulab.uchat.model.pojo;

import java.sql.Timestamp;

public class ChatToken {
	private String token;
	private String userId;
	private Timestamp createTime;
	private Timestamp modifyedTime;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public Timestamp getModifyedTime() {
		return modifyedTime;
	}
	public void setModifyedTime(Timestamp modifyedTime) {
		this.modifyedTime = modifyedTime;
	}
}
