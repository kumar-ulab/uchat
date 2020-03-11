package com.ulab.uchat.types;

public enum DeviceType {
	Http(3), Web(0), Win(1), Ios(2), Sys(4);
	
	private byte val;
	
	private DeviceType(int val) {
		this.val = (byte)val;
	}
	
	public int getVal() {
		return val;
	}
	
	public static DeviceType Byte2ClientType(byte val) {
		switch(val) {
		case 0: return Web;
		case 1: return Win;
		case 2: return Ios;
		case 3: return Http;
		case 4: 
		default: return Sys;
		}
	}
}
