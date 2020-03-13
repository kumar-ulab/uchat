package com.ulab.uchat.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class UserChangeRequest {
	@ApiModelProperty("user's current apssword")
	private String currentPassword;

	@ApiModelProperty("user's new password")
	private String newPassword;
	
	@ApiModelProperty("user's new email")
	private String email;
	
	@ApiModelProperty("user's new first Name")
	private String firstName;
	
	@ApiModelProperty("user's new last Name")
	private String lastName;

	@ApiModelProperty("user's new phone")
	private String phone;

	public String getCurrentPassword() {
		return currentPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPhone() {
		return phone;
	}
	
}
