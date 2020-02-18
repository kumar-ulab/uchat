package com.ulab.uchat.pojo;

import com.ulab.uchat.server.exception.AppException;
import com.ulab.util.MiscUtil;

public class ChatData {
	public static final int HEAD_TYPE_SIZE = 1;		//Type field length, 1 byte
	public static final int HEAD_TOKEN_SIZE = 1;		//Token field length, 1 byte
	public static final int HEAD_BYTES_SIZE = 4;	//Data field length, 4 bytes
	public static final int HEAD_SIZE = HEAD_TYPE_SIZE + HEAD_TOKEN_SIZE + HEAD_BYTES_SIZE;
	public static final int HEAD_TYPE_INDEX = 0;	//Head Type filed positon
	public static final int HEAD_TOEKEN_SIZE_INDEX = 1;	//Head: Info-size position 
	public static final int HEAD_DATA_SIZE_INDEX = 2;	//Head: data-size position
	
	private byte[] bytes;
	
	public ChatData(int size) {
		bytes = new byte[size];
		bytes[HEAD_TYPE_INDEX] = -1;
		bytes[HEAD_TOEKEN_SIZE_INDEX] = -1;
		MiscUtil.integer2Bytes(bytes, HEAD_DATA_SIZE_INDEX, -1);
	}
	
	public ChatData(byte type, String token, byte[] data) {
		byte[] tokenBytes = token.getBytes();
		int size = HEAD_SIZE + tokenBytes.length + data.length;
		bytes = new byte[size];		
		bytes[HEAD_TYPE_INDEX] = type;
		bytes[HEAD_TOEKEN_SIZE_INDEX] = (byte)(tokenBytes.length);
		bytes[HEAD_DATA_SIZE_INDEX] = type;
		MiscUtil.integer2Bytes(bytes, HEAD_DATA_SIZE_INDEX, data.length);
		copyBytes(bytes, HEAD_SIZE, tokenBytes, 0, tokenBytes.length);
		copyBytes(bytes, HEAD_SIZE + tokenBytes.length, data, 0, data.length);
	}
	
	public ChatData(byte[] bytes) {
		this.bytes = bytes;
	}

	public byte getType() {
		return bytes[HEAD_TYPE_INDEX];
	}

	public void setType(byte type) {
		bytes[HEAD_TYPE_INDEX] = type;
	}
	
	public String getToken() {
		int tokenSize = bytes[HEAD_TOEKEN_SIZE_INDEX];
		byte[] tokenBytes = new byte[tokenSize];
		copyBytes(tokenBytes, 0, bytes, HEAD_SIZE, tokenBytes.length);
		return new String(tokenBytes);
	}
	
	public void setToken(String token) {
		byte[] tokenBytes = token.getBytes();
		bytes[HEAD_TOEKEN_SIZE_INDEX] = (byte)(tokenBytes.length);
		copyBytes(bytes, HEAD_SIZE, tokenBytes, 0, tokenBytes.length);
	}
	
	public String getMsg() {
		int tokenSize = bytes[HEAD_TOEKEN_SIZE_INDEX];
		if (tokenSize < 0) {
			throw new AppException("token is not initialized");
		};
		int dataSize = MiscUtil.bytes2Integer(bytes, HEAD_DATA_SIZE_INDEX);
		byte[] data = new byte[dataSize];
		int startIndex = HEAD_SIZE + tokenSize;
		copyBytes(data, 0, bytes, startIndex, data.length);
		return new String(data);
	}
	
	public void setData(byte[] data) {
		int tokenSize = bytes[HEAD_TOEKEN_SIZE_INDEX];
		if (tokenSize < 0) {
			throw new AppException("token is not initialized");
		};
		MiscUtil.integer2Bytes(bytes, HEAD_DATA_SIZE_INDEX, data.length);
		copyBytes(bytes, HEAD_SIZE + tokenSize, data, 0, data.length);
	}
	
	public byte[] getBytes() {
		return bytes;
	}

	public byte[] setBytes(byte type, String token, byte[] data) {
		bytes[HEAD_TYPE_INDEX] = type;
		byte[] tokenBytes = token.getBytes();
		bytes[HEAD_TOEKEN_SIZE_INDEX] = (byte)(tokenBytes.length);
		bytes[HEAD_DATA_SIZE_INDEX] = type;
		MiscUtil.integer2Bytes(bytes, HEAD_DATA_SIZE_INDEX, data.length);
		copyBytes(bytes, HEAD_SIZE, tokenBytes, 0, tokenBytes.length);
		copyBytes(bytes, HEAD_SIZE + tokenBytes.length, data, 0, data.length);
		return bytes;
	}
	
	public static void copyBytes(byte[] targetBytes, int targetStartIndex, 
						byte[] srcBytes, int srcStartIndex, int copyLength) {
		for (int i=0; i<copyLength; i++) {
			targetBytes[targetStartIndex + i] = srcBytes[srcStartIndex + i];
		}
	}
}
