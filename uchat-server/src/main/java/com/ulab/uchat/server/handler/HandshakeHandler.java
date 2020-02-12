package com.ulab.uchat.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ulab.uchat.server.types.ClientType;
import com.ulab.uchat.server.util.ChannelUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;

public class HandshakeHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(UlabChannelInboundHandler.class);
	private static AttributeKey<ClientType> AttrClientType = AttributeKey.valueOf("ClientType"); 

	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	ClientType clientType = ctx.channel().attr(AttrClientType).get();
    	switch (clientType) {
    	case WebSocket:
        	ChannelUtil.addWebSocketHandlers(ctx.channel());    		
            ctx.fireChannelRead(msg);
    		break;
    	case WinApp:
    	case IosApp:
		default:
        	ChannelUtil.addSocketHandlers(ctx.channel());			
    	}
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.remove(this.getClass());
    }
}
