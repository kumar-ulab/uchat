package com.ulab.uchat.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ulab.uchat.constant.Constants;
import com.ulab.uchat.server.helper.SpringBeanHelper;
import com.ulab.uchat.server.service.ChatService;
import com.ulab.uchat.types.ClientType;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class ConnectionHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(ConnectionHandler.class);
    private ChatService chatService = SpringBeanHelper.getBean(ChatService.class);
    	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		chatService.addChannel(channel);
		log.info("Client(" + channel.id().asShortText() + "):" + channel.remoteAddress() + " join");
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		chatService.notifyAll(ctx.channel(), "leave");
		log.info("Client(" + channel.id().asShortText() + "):" + channel.remoteAddress() + " leave");
		chatService.removeChannel(channel);
	}
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	Channel channel = ctx.channel();
    	ClientType clientType = channel.attr(Constants.Client.CLIENT_TYPE).get();
    	if (clientType != null) {
			log.info("Client(" + channel.id().asShortText() + "):" + channel.remoteAddress() + " msg:" + msg);
	    	super.channelRead(ctx,msg);
	    	return;
    	}
		try {
			ByteBuf in = (ByteBuf) msg;
	    	byte msgType = in.getByte(0);
	    	byte clientVal = in.getByte(1);
	    	int size = in.readableBytes() - 2;
	    	byte[] token = new byte[size];
	    	in.getBytes(2, token);
	    	String tokenStr = new String(token);
	    	if (tokenStr.isEmpty()) {
		        chatService.pingClientAck(channel, "login failed, username or password incorrect");
		        log.info("user not login, channel close");
		        Thread.sleep(1000);
		        ctx.close();
			} else {
				log.info("Client(" + channel.id().asShortText() + "):" + channel.remoteAddress() + " login");
			}
	    	if (msgType == 0) {
	    		clientType = ClientType.Byte2ClientType(clientVal);
		        log.info("client " + ctx.channel() + ", type=" + clientType.name());
	    	}
	        if (clientType == null) {
	        	//Websocket need further check by http handler
				chatService.addWebSocketHandlers(ctx.channel());
	        } else {
				ctx.channel().attr(Constants.Client.CLIENT_TYPE).set(clientType);
				chatService.addSocketHandlers(ctx.channel());
		        chatService.pingClientAck(channel, "login successfully");
		        chatService.notifyAll(ctx.channel(), "join");
	        }
		} finally {
	        if (clientType == null) {
		    	super.channelRead(ctx,msg);
	        } else {
	            ReferenceCountUtil.release(msg);
	        }
//	        chatService.removeHandler(channel, ConnectionHandler.class);
		}		
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        Channel incoming = ctx.channel();
        log.error("Client:"+incoming.remoteAddress() + cause.getMessage(), cause);
        ctx.close();
    }
}