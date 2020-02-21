package com.ulab.uchat.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ulab.uchat.constant.Constants;
import com.ulab.uchat.server.helper.SpringBeanHelper;
import com.ulab.uchat.server.service.ChatService;
import com.ulab.uchat.types.ClientType;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class WebsocketFrameHandler extends ChannelInboundHandlerAdapter {
	private static final Logger log = LoggerFactory.getLogger(WebsocketFrameHandler.class);
    private ChatService chatService = SpringBeanHelper.getBean(ChatService.class);
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	Channel channel = ctx.channel();
    	ClientType clientType = channel.attr(Constants.Client.CLIENT_TYPE).get();
    	if (clientType == null) {
    		channel.attr(Constants.Client.CLIENT_TYPE).set(ClientType.Web);
            chatService.pingClientAck(channel, "login successfully");
            chatService.notifyAll(channel, "join");
    	}
    	if (msg instanceof TextWebSocketFrame) {
    		log.info("websocket message: channel=" + ctx.channel().id().asShortText());
    		TextWebSocketFrame webMsg = (TextWebSocketFrame) msg;
    		String text = webMsg.text();
    		ctx.fireChannelRead(text);
    	}
	}
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    	if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
        	Channel channel = ctx.channel();
			ctx.channel().attr(Constants.Client.CLIENT_TYPE).set(ClientType.Web);
	        chatService.pingClientAck(channel, "login successfully");
	        chatService.notifyAll(ctx.channel(), "join");
	        chatService.addChannel(channel);
	        chatService.removeHandler(channel, UchatHttpRequestHandler.class);
    	} else {
    		super.userEventTriggered(ctx, evt);
    	}
    }
}