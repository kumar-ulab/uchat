package com.ulab.uchat.model.pojo;

import com.ulab.uchat.constant.Constants;

import io.swagger.annotations.ApiModel;

@ApiModel("Doctor User")

public class Doctor extends User{
	public Doctor() {
		super(Constants.User.DOCTOR);
	}
}
