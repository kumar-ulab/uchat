package com.ulab.uchat.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.ulab.uchat.server.online.UChatConnections;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.util.ReferenceCountUtil;

@Controller
public class ConnectionHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(ConnectionHandler.class);
    private UChatConnections connections = UChatConnections.getInstance();
    
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		ChannelGroup channels = connections.getChannels();
		Channel channel = ctx.channel();
		channels.add(channel);
		log.info("Client(" + channel.id().asShortText() + "):" + channel.remoteAddress() + " join");
	}
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		ChannelGroup channels = connections.getChannels();
		Channel channel = ctx.channel();
		ChannelUtil.notifyAll(ctx.channel(), "leave");
		log.info("Client(" + channel.id().asShortText() + "):" + channel.remoteAddress() + " leave");
		channels.remove(ctx.channel());
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		log.info("Client(" + channel.id().asShortText() + "):" + channel.remoteAddress() + " online");
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		log.info("Client(" + channel.id().asShortText() + "):" + channel.remoteAddress() + " inactive");
	}
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	Channel channel = ctx.channel();
		log.info("Client(" + channel.id().asShortText() + "):" + channel.remoteAddress() + " msg:" + msg);
    	super.channelRead(ctx,msg);
    }
	
}