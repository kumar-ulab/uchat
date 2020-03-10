package com.ulab.uchat.constant;

import com.ulab.uchat.model.pojo.User;
import com.ulab.uchat.pojo.ChatPair;
import com.ulab.uchat.pojo.ChatPairGroup;
import com.ulab.uchat.types.ClientType;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public class Constants {
	private Constants() {}
	public static class Chat {
		private Chat() {}
		public static final int TOKEN_EXPIRATION_SECONDS = 30 * 60;	//30 minutes
	}
	public static class Client {
		public static final AttributeKey<ClientType> CLIENT_TYPE = AttributeKey.valueOf("ClientType"); 
		public static final AttributeKey<User> CLIENT_USER = AttributeKey.valueOf("User");
		public static final AttributeKey<ChatPairGroup> PAIR_USERS = AttributeKey.valueOf("chatPairs"); 
	}
}
