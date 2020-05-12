package com.ulab.uchat.constant;

import com.ulab.uchat.model.pojo.User;
import com.ulab.uchat.types.ChannelType;

import io.netty.util.AttributeKey;

public class Constants {
	private Constants() {}
	public static class Client {
		public static final AttributeKey<ChannelType> CHANNEL_TYPE = AttributeKey.valueOf("ChannelType"); 
		public static final AttributeKey<String> DEVICE_TYPE = AttributeKey.valueOf("DeviceType"); 
		public static final AttributeKey<User> CLIENT_USER = AttributeKey.valueOf("User");
		public static final AttributeKey<Long> ACTIVE_TIME = AttributeKey.valueOf("ActiveTime");
		public static final AttributeKey<ChannelType> DEVICE = AttributeKey.valueOf("Device"); 
	}
	
	public static final int MESSAGE_STATUS_SEND = 0;
	
	public static final int MESSAGE_STATUS_RECEIVE = 1; 
}
