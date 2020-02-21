package com.ulab.uchat.types;

public enum ClientType {
	Http(3), Web(0), Win(1), Ios(2);
	
	private byte val;
	
	private ClientType(int val) {
		this.val = (byte)val;
	}
	
	public int getVal() {
		return val;
	}
	
	public static ClientType Byte2ClientType(byte val) {
		switch(val) {
		case 0: return Web;
		case 1: return Win;
		case 2: return Ios;
		default: return Http;
		}
	}
}
