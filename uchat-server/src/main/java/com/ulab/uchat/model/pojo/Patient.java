package com.ulab.uchat.model.pojo;

import com.ulab.uchat.constant.Constants;

import io.swagger.annotations.ApiModel;

@ApiModel("Patient User")

public class Patient extends User{
	public Patient() {
		super(Constants.User.PATIENT);
	}
}
