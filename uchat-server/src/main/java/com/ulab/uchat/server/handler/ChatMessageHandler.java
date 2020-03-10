package com.ulab.uchat.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ulab.uchat.constant.Constants;
import com.ulab.uchat.model.pojo.User;
import com.ulab.uchat.pojo.ChatPair;
import com.ulab.uchat.pojo.ChatPairGroup;
import com.ulab.uchat.pojo.ClientMsg;
import com.ulab.uchat.pojo.ServerMsg;
import com.ulab.uchat.server.helper.SpringBeanHelper;
import com.ulab.uchat.server.service.ChatService;
import com.ulab.uchat.types.ClientType;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatMessageHandler extends SimpleChannelInboundHandler<ClientMsg> {
    private static final Logger log = LoggerFactory.getLogger(ChatMessageHandler.class);
    private ChatService chatService = SpringBeanHelper.getBean(ChatService.class);
    
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ClientMsg clientMsg) throws Exception {
        log.info("ChatMsg Type: " + clientMsg.getType());
        Channel channel = ctx.channel();
        chatService.handleMessage(channel, clientMsg);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		Channel channel = ctx.channel();
		log.error("Client(" + channel.id().asShortText() + "):" + channel.remoteAddress(), cause);
	}
	
	private void addPairChennl(Channel channel, Channel pairChannel) {
		User pairUser = (User) (pairChannel.attr(Constants.Client.CLIENT_USER).get());
		ChatPair thisChatPair = new ChatPair(pairUser, pairChannel);
		ChatPairGroup thisPairGroup = (ChatPairGroup) channel.attr(Constants.Client.PAIR_USERS).get();
		thisPairGroup.addPair(thisChatPair);
	}
}