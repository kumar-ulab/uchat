package com.ulab.uchat.types;

public enum UserType {
	Doctor(1,"D", "Doctor"), Patient(2,"P", "Friend");
	
	private int val;
	String flag;
	String title;
	
	private UserType(int val, String flag, String title) {
		this.val = val;
		this.flag = flag;
		this.title = title;
	}
	
	public int getVal() {
		return val;
	}
	
	public String getFlag() {
		return flag;
	}
	
	public String getTitle() {
		return title;
	}

	public static UserType parse(int val) {
		switch(val) {
		case 1: return Doctor;
		default: return Patient;
		}
	}
}
