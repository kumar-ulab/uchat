package com.ulab.uchat.constant;

import com.ulab.uchat.types.ClientType;

import io.netty.util.AttributeKey;

public class Constants {
	private Constants() {}
	public static class Chat {
		private Chat() {}
		public static final byte CHAT_MSG_LOGIN = 0;	//first message after connection
		public static final byte CHAT_MSG_TEXT = 1;		//text message
		public static final byte CHAT_MSG_PIC = 2;		//image message
		public static final byte CHAT_MSG_NOTIFY = 3;	//system notification
	}
	public static class Client {
		public static final AttributeKey<ClientType> CLIENT_TYPE = AttributeKey.valueOf("ClientType"); 
		public static final AttributeKey<ClientType> CLIENT_USER = AttributeKey.valueOf("User"); 
	}
	public static class User {
		public static final int PATIENT = 1; 
		public static final int DOCTOR = 0; 
	}
}
