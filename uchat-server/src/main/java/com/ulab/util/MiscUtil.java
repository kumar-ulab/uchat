package com.ulab.util;

public class MiscUtil {
	public static int integer2Bytes(byte[] bytes, int i, int v) {
	    //High to low
		bytes[i++] = (byte) ((v >> 24) & 0xFF);  
		bytes[i++] = (byte) ((v >> 16) & 0xFF);  
		bytes[i++] = (byte) ((v >> 8) & 0xFF);  
		bytes[i++] = (byte) (v & 0xFF);
		return 4;
	}

	public static int bytes2Integer(byte[] bytes, int i) {
	    //High to low
	    int v = 0;
	    for (int n = 0; n < 4; n++) {  
	        int shift = (4 - 1 - n) * 8;  
	        v += (bytes[i + n] & 0x000000FF) << shift;
	    }
	    return v;
	}
	
	public static void main(String[] args) {
		byte[] bytes = new byte[10];
		integer2Bytes(bytes, 0, 0);
		System.out.println(bytes2Integer(bytes, 0));
		integer2Bytes(bytes, 0, 1);
		System.out.println(bytes2Integer(bytes, 0));
		integer2Bytes(bytes, 1, 0);
		System.out.println(bytes2Integer(bytes, 1));
		integer2Bytes(bytes, 1, 1);
		System.out.println(bytes2Integer(bytes, 1));
		integer2Bytes(bytes, 1, 9);
		System.out.println(bytes2Integer(bytes, 1));
		integer2Bytes(bytes, 2, 0);
		System.out.println(bytes2Integer(bytes, 2));
		integer2Bytes(bytes, 2, 1);
		System.out.println(bytes2Integer(bytes, 2));
		integer2Bytes(bytes, 2, 1976);
		System.out.println(bytes2Integer(bytes, 2));
	}
}
