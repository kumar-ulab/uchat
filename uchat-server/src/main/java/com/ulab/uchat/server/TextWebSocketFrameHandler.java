package com.ulab.uchat.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.ulab.uchat.server.online.UChatConnections;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
//import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private static final Logger log = LoggerFactory.getLogger(TextWebSocketFrameHandler.class);
//    private UChatConnections connections = UChatConnections.getInstance();
    
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
		String text = msg.text();
//		log.info("got msg: " + text);
//		ChannelGroup channels = connections.getChannels();
		Channel incoming = ctx.channel();
        String attr = ChannelUtil.getChannelAttr(incoming);
        log.info(attr + " " + msg);
        ChannelUtil.sendMsgToAll(incoming, text);
//		for (Channel channel : channels) {
//		    if (channel != incoming){
//		    	String client = channel.id().asShortText() + "(" + incoming.remoteAddress() + ")";
//		        channel.writeAndFlush(new TextWebSocketFrame("[" + client + "]" + text));
//		    } else {
//		        channel.writeAndFlush(new TextWebSocketFrame("[you]" + text ));
//		    }
//		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		Channel channel = ctx.channel();
		log.info("Client(" + channel.id().asShortText() + "):" + channel.remoteAddress() + " error");
		cause.printStackTrace();
		ctx.close();
	}
}