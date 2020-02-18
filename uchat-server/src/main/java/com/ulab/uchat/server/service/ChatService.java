package com.ulab.uchat.server.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.mybatis.generator.logging.log4j2.Log4j2AbstractLoggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ulab.uchat.constant.Constants;
import com.ulab.uchat.pojo.ChatData;
import com.ulab.uchat.pojo.ServerMsg;
import com.ulab.uchat.server.config.AppConfig;
import com.ulab.uchat.server.exception.AppException;
import com.ulab.uchat.server.handler.ConnectionHandler;
import com.ulab.uchat.server.handler.UchatTextHandler;
import com.ulab.uchat.server.handler.UchatHttpRequestHandler;
import com.ulab.uchat.server.handler.WebsocketFrameHandler;
import com.ulab.uchat.types.ClientType;
import com.ulab.util.FileUtil;
import com.ulab.util.JsonUtil;
import com.ulab.util.SslUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

@Service
public class ChatService {
    private static final Logger log = LoggerFactory.getLogger(ConnectionHandler.class);
	private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);	

	@Autowired AppConfig appConfig;
	
	public void sendText(String text) {
		System.err.println(text);
	}

	public ChannelGroup getChannels() {
		return channels;
	}
	
	public void addChannel(Channel channel) {
		channels.add(channel);
	}
	
	public void removeChannel(Channel channel) {
		channels.remove(channel);
	}
	
	public void addWebSocketHandlers(Channel ch) {
		ChannelPipeline pipeline = ch.pipeline();
//		SSLContext sslContext = SslUtil.createSSLContext();
//		SSLEngine sslEngine = sslContext.createSSLEngine();
//		sslEngine.setUseClientMode(false);
//		sslEngine.setNeedClientAuth(false);
//		pipeline.addBefore("UchatText", "SslHandler", new SslHandler(sslEngine));		
		pipeline.addBefore("UchatText", "HttpServerCodec", new HttpServerCodec());
		pipeline.addBefore("UchatText", "HttpObjectAggregator", new HttpObjectAggregator(64*1024));
		pipeline.addBefore("UchatText", "ChunkedWriteHandler", new UchatHttpRequestHandler(appConfig.getPort(), "/ws"));
		pipeline.addBefore("UchatText", "UchatHttpRequestHandler", new WebSocketServerProtocolHandler("/ws"));
		pipeline.addBefore("UchatText", "WebSocketServerProtocolHandler", new WebsocketFrameHandler());
	}

	public void addSocketHandlers(Channel ch) {
		ChannelPipeline pipeline = ch.pipeline();
//		pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
		pipeline.addBefore("UchatText", "StringDecoder", new StringDecoder());
		pipeline.addBefore("UchatText", "StringEncoder", new StringEncoder());
	}	
	
	public void removeHandler(Channel ch, Class<? extends ChannelHandler> handlerType) {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.remove(handlerType);
	}
	
	public void sendData(Channel channel, String inputText) {
		String filePath = inputText.trim();
		String info;
		try {
			File file = new File(filePath);
			byte[] data;
			if (file.exists()) {
				data = FileUtil.readFile2Bytes(file);
				info = filePath;
			} else {
				info = "";
				data = inputText.getBytes();
			}
			ChatData chatData = new ChatData(Constants.Chat.CHAT_MSG_PIC, info, data);
			sendData(channel, chatData);
		} catch (IOException e) {
			throw new AppException(e);
		}
	}

	public void sendData(Channel channel, ChatData data) {
		ByteBuf byteBuf = Unpooled.wrappedBuffer(data.getBytes());
        String channelId = channel.id().asShortText();
        System.out.println("send chat data on channel " + channelId);
		channel.writeAndFlush(byteBuf);
	}
	
	public void sendWebMsg(Channel channel, String msg) {
		channel.writeAndFlush(new TextWebSocketFrame(msg));
	}
	
	public String getChannelAttr(Channel channel) {
		ClientType clientType = channel.attr(Constants.Client.CLIENT_TYPE).get();
		return "[SERVER] - " + clientType + "(" + channel.remoteAddress() + ")";
	}
	
	public void pingClientAck(Channel channel, String msg) {	
		try {
			sendMsg(channel, Constants.Chat.CHAT_MSG_LOGIN, msg);
		} catch (Exception e) {
			log.error("failed to notify message: channel=" + channel.id().asShortText());
			return;
		}
	}
	
	public void notify(Channel channel, String msg) {
		try {
			sendMsg(channel, Constants.Chat.CHAT_MSG_NOTIFY, msg);
		} catch (Exception e) {
			log.error("failed to notify message: channel=" + channel.id().asShortText());
			return;
		}
	}
	
	private void sendMsg(Channel channel, byte type, String msg) throws Exception {
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.setType(type);
		serverMsg.setChannel(channel.id().asShortText());
		serverMsg.setData(msg);
		ClientType clientType = channel.attr(Constants.Client.CLIENT_TYPE).get();
		serverMsg.setDevice(clientType.getVal());
		String json = JsonUtil.Object2Json(serverMsg);
		if (clientType == ClientType.Web) {
			sendWebMsg(channel, json);
		} else {
			channel.writeAndFlush(json);
		}
	}
	
	public void notifyAll(Channel selfChannel, String msg) {
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.setType(Constants.Chat.CHAT_MSG_NOTIFY);
		serverMsg.setChannel(selfChannel.id().asShortText());
		ClientType myClientType = selfChannel.attr(Constants.Client.CLIENT_TYPE).get();
		serverMsg.setDevice(myClientType.getVal());
		serverMsg.setData(msg);
		try {
			String json = JsonUtil.Object2Json(serverMsg);
			for (Channel channel : channels) {
				ClientType clientType = channel.attr(Constants.Client.CLIENT_TYPE).get();
				if (clientType == ClientType.Web) {
					sendWebMsg(channel, json);
				} else {
					channel.writeAndFlush(json);
				}
			}
		} catch (IOException e) {
			log.error("failed to notify message");
			return;
		}
	}

	public void sendMsgToAll(Channel selfChannel, ServerMsg srvMsg) {
		String text;
		try {
			text = JsonUtil.Object2Json(srvMsg);
		} catch (IOException e) {
			log.error("failed to send message", e);
			return;
		}
		for (Channel channel : channels) {
    		ClientType clientType = channel.attr(Constants.Client.CLIENT_TYPE).get();
    		if (ClientType.Web == clientType) {
		        channel.writeAndFlush(new TextWebSocketFrame(text));
    		} else {
    			channel.writeAndFlush(text);
    		}
		}
	}
}
