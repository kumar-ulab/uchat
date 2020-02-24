package com.ulab.uchat.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class LoginInfo {
	@ApiModelProperty("could be use-id, social-identity, or email-name")
	private String login;			//could be id, email or identity
	@ApiModelProperty("user's apssword")
	private String password;

	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
