package com.ulab.uchat.pojo;

import com.ulab.uchat.client.AppException;
import com.ulab.util.MiscUtil;

public class ChatData {
	public static final int HEAD_TYPE_SIZE = 1;		//Type field length, 1 byte
	public static final int HEAD_INFO_SIZE = 1;		//Info field length, 1 byte
	public static final int HEAD_BYTES_SIZE = 4;	//Data field length, 4 bytes
	public static final int HEAD_SIZE = HEAD_TYPE_SIZE + HEAD_INFO_SIZE + HEAD_BYTES_SIZE;
	public static final int HEAD_TYPE_INDEX = 0;	//Head Type filed positon
	public static final int HEAD_INFO_SIZE_INDEX = 1;	//Head: Info-size position 
	public static final int HEAD_DATA_SIZE_INDEX = 2;	//Head: data-size position
	
	private byte[] bytes;
	
	public ChatData(int size) {
		bytes = new byte[size];
		bytes[HEAD_TYPE_INDEX] = -1;
		bytes[HEAD_INFO_SIZE_INDEX] = -1;
		MiscUtil.integer2Bytes(bytes, HEAD_DATA_SIZE_INDEX, -1);
	}
	
	public ChatData(byte type, String info, byte[] data) {
		byte[] infoBytes = info.getBytes();
		int size = HEAD_SIZE + infoBytes.length + data.length;
		bytes = new byte[size];		
		bytes[HEAD_TYPE_INDEX] = type;
		bytes[HEAD_INFO_SIZE_INDEX] = (byte)(infoBytes.length);
		bytes[HEAD_DATA_SIZE_INDEX] = type;
		MiscUtil.integer2Bytes(bytes, HEAD_DATA_SIZE_INDEX, data.length);
		copyBytes(bytes, HEAD_SIZE, infoBytes, 0, infoBytes.length);
		copyBytes(bytes, HEAD_SIZE + infoBytes.length, data, 0, data.length);
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
	
	public String getInfo() {
		int infoSize = bytes[HEAD_INFO_SIZE_INDEX];
		byte[] infoBytes = new byte[infoSize];
		copyBytes(infoBytes, 0, bytes, HEAD_SIZE, infoBytes.length);
		return new String(infoBytes);
	}
	
	public void setInfo(String info) {
		byte[] infoBytes = info.getBytes();
		bytes[HEAD_INFO_SIZE_INDEX] = (byte)(infoBytes.length);
		copyBytes(bytes, HEAD_SIZE, infoBytes, 0, infoBytes.length);
	}
	
	public byte[] getData() {
		int infoSize = bytes[HEAD_INFO_SIZE_INDEX];
		if (infoSize < 0) {
			throw new AppException("byte info is not initialized");
		};
		int dataSize = bytes[HEAD_DATA_SIZE_INDEX];
		byte[] data = new byte[dataSize];
		int startIndex = HEAD_SIZE + infoSize;
		copyBytes(data, 0, bytes, startIndex, data.length);
		return data;
	}
	
	public void setData(byte[] data) {
		int infoSize = bytes[HEAD_INFO_SIZE_INDEX];
		if (infoSize < 0) {
			throw new AppException("byte info is not initialized");
		};
		MiscUtil.integer2Bytes(bytes, HEAD_DATA_SIZE_INDEX, data.length);
		copyBytes(bytes, HEAD_SIZE + infoSize, data, 0, data.length);
	}
	
	public byte[] getBytes() {
		return bytes;
	}

	public byte[] setBytes(byte type, String info, byte[] data) {
		bytes[HEAD_TYPE_INDEX] = type;
		byte[] infoBytes = info.getBytes();
		bytes[HEAD_INFO_SIZE_INDEX] = (byte)(infoBytes.length);
		bytes[HEAD_DATA_SIZE_INDEX] = type;
		MiscUtil.integer2Bytes(bytes, HEAD_DATA_SIZE_INDEX, data.length);
		copyBytes(bytes, HEAD_SIZE, infoBytes, 0, infoBytes.length);
		copyBytes(bytes, HEAD_SIZE + infoBytes.length, data, 0, data.length);
		return bytes;
	}
	
	private void copyBytes(byte[] targetBytes, int targetStartIndex, 
						byte[] srcBytes, int srcStartIndex, int copyLength) {
		for (int i=0; i<copyLength; i++) {
			targetBytes[targetStartIndex + i] = srcBytes[srcStartIndex + i];
		}
	}
}
