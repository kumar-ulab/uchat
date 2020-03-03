package com.ulab.uchat.model.pojo;

import com.ulab.uchat.types.UserType;

import io.swagger.annotations.ApiModel;

@ApiModel("Patient User")

public class Patient extends User{
	public Patient() {
		super(UserType.Patient.getVal());
	}
}
