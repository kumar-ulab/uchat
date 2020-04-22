package com.ulab.uchat.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ulab.uchat.constant.Constants;
import com.ulab.uchat.pojo.ClientMsg;
import com.ulab.uchat.pojo.ServerMsg;
import com.ulab.uchat.server.helper.SpringBeanHelper;
import com.ulab.uchat.server.service.ChatService;
import com.ulab.uchat.types.ChannelType;
import com.ulab.util.JsonUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

public class UchatTextHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(UchatTextHandler.class);
    
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String text = String.valueOf(msg);
		try {
			ClientMsg chatMsg = JsonUtil.json2Object(text, ClientMsg.class);
			ctx.fireChannelRead(chatMsg);
		} catch (Exception e) {
			log.error("invalid chat Message: " + text, e);
            ReferenceCountUtil.release(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		Channel channel = ctx.channel();
		log.error("Client(" + channel.id().asShortText() + "):" + channel.remoteAddress(), cause);
	}
}