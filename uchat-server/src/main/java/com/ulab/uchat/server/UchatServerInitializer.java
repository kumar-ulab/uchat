package com.ulab.uchat.server;

import com.ulab.uchat.server.handler.ChatMessageHandler;
import com.ulab.uchat.server.handler.ConnectionHandler;
import com.ulab.uchat.server.handler.UchatTextHandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class UchatServerInitializer extends ChannelInitializer<SocketChannel> {
	
	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("connection", new ConnectionHandler());
		pipeline.addLast("UchatText", new UchatTextHandler());
		pipeline.addLast("ChatMessageHandler", new ChatMessageHandler());
	}
}