package com.ulab.uchat.model.pojo;

import com.ulab.uchat.types.UserType;

import io.swagger.annotations.ApiModel;

@ApiModel("Doctor User")

public class Doctor extends User{
	public Doctor() {
		super(UserType.Doctor.getVal());
	}
}
