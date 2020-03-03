package com.ulab.uchat.model.pojo;

import java.sql.Timestamp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Abstrct User")
public class User {
	@ApiModelProperty("user id")
	private String id;
	@ApiModelProperty("social identity")
	private String identity;
	@ApiModelProperty("user's email")
	private String email;
	@ApiModelProperty("first name")
	private String firstName;
	@ApiModelProperty("last name")
	private String lastName;
	@ApiModelProperty("user's phone")
	private String phone;
	@ApiModelProperty("user's password")
	private String password;
	@ApiModelProperty("user type:  1-Doctor, 2-Patient")
	private int type;
	
	private Timestamp createTime;
	private Timestamp modifyTime;	
		
	public User() {		
	}
	
	public User(int type) {
		this.type = type;
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

	public String pickIdentity() {
		return identity;
	}
	
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String pickPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Timestamp pickCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp pickModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Timestamp modifyTime) {
		this.modifyTime = modifyTime;
	}

	public void setPerson(Person person) {
		this.email = person.getEmail();
		this.firstName = person.getFirstName();
		this.lastName = person.getLastName();
		this.identity = person.getIdentity();
		this.phone = person.getPhone();
	}
}
