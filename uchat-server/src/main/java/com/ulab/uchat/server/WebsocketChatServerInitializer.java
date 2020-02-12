package com.ulab.uchat.server;

import com.ulab.uchat.server.handler.ClientTypeJudgeHandler;
import com.ulab.uchat.server.handler.ConnectionHandler;
import com.ulab.uchat.server.handler.HandshakeHandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class WebsocketChatServerInitializer extends ChannelInitializer<SocketChannel> {
	@Override
	public void initChannel(SocketChannel ch) throws Exception {//2
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("connection", new ConnectionHandler());
		pipeline.addLast("clientTypeJudge", new ClientTypeJudgeHandler());
		pipeline.addLast("handshake", new HandshakeHandler());
	}
}