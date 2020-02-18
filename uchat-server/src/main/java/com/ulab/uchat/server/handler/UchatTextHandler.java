package com.ulab.uchat.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ulab.uchat.constant.Constants;
import com.ulab.uchat.pojo.ClientMsg;
import com.ulab.uchat.pojo.ServerMsg;
import com.ulab.uchat.server.helper.SpringBeanHelper;
import com.ulab.uchat.server.service.ChatService;
import com.ulab.uchat.types.ClientType;
import com.ulab.util.JsonUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class UchatTextHandler extends SimpleChannelInboundHandler<String> {
    private static final Logger log = LoggerFactory.getLogger(UchatTextHandler.class);
    private ChatService chatService = SpringBeanHelper.getBean(ChatService.class);
    
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String text) throws Exception {
        log.info("ChatMsg: " + text);
		Channel channel = ctx.channel();
		ClientMsg chatMsg = JsonUtil.json2Object(text, ClientMsg.class);
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.setType(chatMsg.getType());
		serverMsg.setChannel(channel.id().asShortText());
		ClientType clientType = channel.attr(Constants.Client.CLIENT_TYPE).get();
		serverMsg.setDevice(clientType.getVal());
		serverMsg.setData(chatMsg.getData());
        chatService.sendMsgToAll(channel, serverMsg);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		Channel channel = ctx.channel();
		log.error("Client(" + channel.id().asShortText() + "):" + channel.remoteAddress(), cause);
	}
}