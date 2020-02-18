package com.ulab.uchat.client.service;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ulab.uchat.client.AppException;
import com.ulab.uchat.client.UchatMessageHandler;
import com.ulab.uchat.client.UchatClientInitializer;
import com.ulab.uchat.constant.Constants;
import com.ulab.uchat.pojo.ChatData;
import com.ulab.uchat.pojo.ChatMsg;
import com.ulab.uchat.pojo.PingMsg;
import com.ulab.util.FileUtil;
import com.ulab.util.JsonUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

@Service
public class ChatService {
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
	private @Autowired HttpService httpService;
	
	private EventLoopGroup group;
    private Channel channel;
    private String serverChannel = null;

    public void connectServer(String host, int port) throws InterruptedException {
        group = new NioEventLoopGroup();
        Bootstrap bootstrap  = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
        		.handler(new UchatClientInitializer());
        channel = bootstrap.connect(host, port).sync().channel();
    	pingServerWithClientType();
    }
    
    public void close() {
    	group.shutdownGracefully(30, 30, TimeUnit.SECONDS);
    }
    
	public void sendPingMsg(PingMsg pingMsg) {
		ByteBuf buf = conver2ByteBuf(pingMsg);
        System.out.println("auto ping server");
		channel.writeAndFlush(buf);
	}
	
	public void sendChatMsg(String inputText) {
		String filePath = inputText.trim();
		try {
			File file = new File(filePath);
			ChatMsg msg;
			if (file.exists()) {
				Map rt = httpService.uploadpicture(serverChannel, filePath);
				String picName = String.valueOf(rt.get("pic"));
				log.info("upload file down:" + picName);
				msg = new ChatMsg(Constants.Chat.CHAT_MSG_PIC, picName);
			} else {
				msg = new ChatMsg(Constants.Chat.CHAT_MSG_TEXT, inputText);
			}
			String json = JsonUtil.Object2Json(msg);
	        log.info("send message: channel=" + serverChannel + ", msg=" + json);
			channel.writeAndFlush(json);
		} catch (IOException e) {
			throw new AppException(e);
		}
	}
	
	public void sendData(String inputText) {
		String filePath = inputText.trim();
		String info;
		try {
			File file = new File(filePath);
			byte[] data;
			byte type;
			if (file.exists()) {
				data = FileUtil.readFile2Bytes(file);
				info = filePath;
				type = Constants.Chat.CHAT_MSG_PIC;
				Map rt = httpService.uploadpicture(channel.id().asShortText(), filePath);
				String picName = String.valueOf(rt.get("pic"));
				log.info("upload file down:" + picName);
			} else {
				info = "";
				data = inputText.getBytes();
				type = Constants.Chat.CHAT_MSG_TEXT;
				ChatData chatData = new ChatData(type, info, data);
				sendData(chatData);
				log.info("send bytes: length=" + chatData.getBytes().length);
			}
		} catch (IOException e) {
			throw new AppException(e);
		}
	}

	public void sendData(ChatData data) {
		ByteBuf byteBuf = Unpooled.wrappedBuffer(data.getBytes());
        String channelId = channel.id().asShortText();
        log.info("send chat data on channel " + channelId);
		channel.writeAndFlush(byteBuf);
	}

    private void pingServerWithClientType() {
        String channelId = channel.id().asShortText();
        System.out.println("login to uChat server...");
        log.info("ping server, channle=" + channelId);
        PingMsg msg = new PingMsg(0, 1, "admin123");
        sendPingMsg(msg);
    }

	private ByteBuf conver2ByteBuf(PingMsg msg) {
		byte[] tokenBytes = msg.getToken().getBytes();
		byte[] bytes = new byte[tokenBytes.length + 2];
		bytes[0] = msg.getMsgType();
		bytes[1] = msg.getClientType();
		for (int i=0; i<tokenBytes.length; i++) {
			bytes[2+i] = tokenBytes[i];
		}
		return Unpooled.wrappedBuffer(bytes);
	}

	public String getServerChannel() {
		return serverChannel;
	}

	public void setServerChannel(String serverChannel) {
		this.serverChannel = serverChannel;
	}

}
