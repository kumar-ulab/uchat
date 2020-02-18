package com.ulab.uchat.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ulab.uchat.client.helper.SpringBeanHelper;
import com.ulab.uchat.client.service.ChatService;
import com.ulab.uchat.constant.Constants;
import com.ulab.uchat.pojo.ServerMsg;
import com.ulab.uchat.types.ClientType;
import com.ulab.util.JsonUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class UchatMessageHandler extends  SimpleChannelInboundHandler<String> {
    private static final Logger log = LoggerFactory.getLogger(UchatMessageHandler.class);
	ChatService chatService = SpringBeanHelper.getBean(ChatService.class);
	
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
    	log.info(s);
    	ServerMsg rsp = JsonUtil.json2Object(s, ServerMsg.class);
    	String srvChannel = rsp.getChannel();
    	String myChannel = chatService.getServerChannel();
    	String client = srvChannel.equals(myChannel) ? "you" : srvChannel;
    	int clientType = rsp.getDevice();
    	String device = ClientType.Byte2ClientType((byte)clientType).name();
    	switch (rsp.getType()) {
    	case Constants.Chat.CHAT_MSG_LOGIN:
    		chatService.setServerChannel(rsp.getChannel());
            System.out.println("[you(" + device + ")]: " + rsp.getData());
    		break;
    	case Constants.Chat.CHAT_MSG_TEXT:
            System.out.println("[" + client+ "(" + device + ")] " + rsp.getData());
            break;
    	case Constants.Chat.CHAT_MSG_PIC:
    		String picName = rsp.getData();
            System.out.println("[" + client + "(" + device + ")]: /picture/channel/" + srvChannel + "/pic/" + picName);
            break;
    	case Constants.Chat.CHAT_MSG_NOTIFY:
		default:
            System.out.println("[" + client + "(" + device + ")] " + rsp.getData());
    		break;
    	}
    	
    }
}