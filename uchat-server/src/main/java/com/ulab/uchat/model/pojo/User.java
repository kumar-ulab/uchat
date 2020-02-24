package com.ulab.uchat.model.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Abstrct User")
public abstract class User {
	@ApiModelProperty("user id in uChat system")
	private String id;
	@ApiModelProperty("user's personal information")
	private Person person;
	private String password;
	@ApiModelProperty("user tyep:  0-Doctor, 1-Patient")
	private int type;
	
	public User(int type) {
		person = new Person();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}

	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	public String pickPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
