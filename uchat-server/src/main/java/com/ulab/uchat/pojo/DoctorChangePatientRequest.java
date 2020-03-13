package com.ulab.uchat.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class DoctorChangePatientRequest {
	@ApiModelProperty("doctor's Id")
	private String doctorId;
	
	@ApiModelProperty("doctor's password")
	private String doctorPassword;

	@ApiModelProperty("patient's new email")
	private String email;
	
	@ApiModelProperty("patient's new first Name")
	private String firstName;
	
	@ApiModelProperty("patient's new last Name")
	private String lastName;

	@ApiModelProperty("patient's new phone")
	private String phone;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getDoctorPassword() {
		return doctorPassword;
	}

	public void setDoctorPassword(String doctorPassword) {
		this.doctorPassword = doctorPassword;
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
