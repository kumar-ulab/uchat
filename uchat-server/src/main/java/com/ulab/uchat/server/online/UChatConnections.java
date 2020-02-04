package com.ulab.uchat.server.online;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class UChatConnections {
	private static final UChatConnections instance = new UChatConnections();
	private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	private UChatConnections() {		
	}

	public static UChatConnections getInstance() {
		return instance;
	}
	
	public ChannelGroup getChannels() {
		return channels;
	}
}
