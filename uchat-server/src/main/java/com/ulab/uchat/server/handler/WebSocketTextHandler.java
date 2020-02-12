package com.ulab.uchat.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ulab.uchat.server.types.ClientType;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebSocketTextHandler extends UlabChannelInboundHandler<TextWebSocketFrame> {
    protected WebSocketTextHandler() {
		super(ClientType.WebSocket);
	}

	private static final Logger log = LoggerFactory.getLogger(WebSocketTextHandler.class);
    
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
		String text = msg.text();
		log.info("got msg: " + text);
		ChannelGroup channels = connections.getChannels();
		Channel incoming = ctx.channel();
		for (Channel channel : channels) {
		    if (channel != incoming){
		    	String client = channel.id().asShortText() + "(" + incoming.remoteAddress() + ")";
		        channel.writeAndFlush(new TextWebSocketFrame("[" + client + "]" + text));
		    } else {
		        channel.writeAndFlush(new TextWebSocketFrame("[you]" + text ));
		    }
		}
	}
}