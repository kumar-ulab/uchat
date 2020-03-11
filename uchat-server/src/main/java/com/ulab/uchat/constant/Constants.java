package com.ulab.uchat.constant;

import com.ulab.uchat.model.pojo.User;
import com.ulab.uchat.pojo.ChatPair;
import com.ulab.uchat.pojo.ChatPairGroup;
import com.ulab.uchat.types.DeviceType;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public class Constants {
	private Constants() {}
	public static class Client {
		public static final AttributeKey<DeviceType> DEVICE_TYPE = AttributeKey.valueOf("ClientType"); 
		public static final AttributeKey<User> CLIENT_USER = AttributeKey.valueOf("User");
		public static final AttributeKey<Long> ACTIVE_TIME = AttributeKey.valueOf("ActiveTime");
	}
}
