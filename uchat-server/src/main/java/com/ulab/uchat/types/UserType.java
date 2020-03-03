package com.ulab.uchat.types;

public enum UserType {
	Doctor(1,"D"), Patient(2,"P");
	
	private int val;
	String flag;
	
	private UserType(int val, String flag) {
		this.val = val;
		this.flag = flag;
	}
	
	public int getVal() {
		return val;
	}
	
	public String getFlag() {
		return flag;
	}
	
	public static UserType parse(int val) {
		switch(val) {
		case 1: return Doctor;
		default: return Patient;
		}
	}
}
