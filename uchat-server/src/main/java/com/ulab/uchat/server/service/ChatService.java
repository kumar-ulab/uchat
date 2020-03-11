package com.ulab.uchat.server.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ulab.uchat.constant.Constants;
import com.ulab.uchat.model.pojo.User;
import com.ulab.uchat.pojo.ClientMsg;
import com.ulab.uchat.pojo.ServerMsg;
import com.ulab.uchat.server.config.AppConfig;
import com.ulab.uchat.server.dao.mapper.MapperUser;
import com.ulab.uchat.server.exception.AppException;
import com.ulab.uchat.server.handler.ConnectionHandler;
import com.ulab.uchat.server.handler.UchatHttpRequestHandler;
import com.ulab.uchat.server.handler.WebsocketFrameHandler;
import com.ulab.uchat.server.security.JwtUtils;
import com.ulab.uchat.server.security.auth.UserAuthInfo;
import com.ulab.uchat.types.DeviceType;
import com.ulab.uchat.types.MsgType;
import com.ulab.uchat.types.UserType;
import com.ulab.util.JsonUtil;
import com.ulab.util.SslUtil;

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
import io.netty.util.concurrent.GlobalEventExecutor;

@Service
public class ChatService {
    private static final Logger log = LoggerFactory.getLogger(ConnectionHandler.class);
	private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);	

	@Autowired AppConfig appConfig;
	@Autowired JwtUtils jwt;
	@Autowired MapperUser mapperUser;
	
	public void sendText(String text) {
		System.err.println(text);
	}

	public ChannelGroup getChannels() {
		return channels;
	}
	
	public void addChannel(Channel channel) {
		channel.attr(Constants.Client.ACTIVE_TIME).set(System.currentTimeMillis());
		channels.add(channel);
	}
	
	public void removeChannel(Channel channel) {
		channels.remove(channel);
	}
	
	public void addWebSocketHandlers(Channel ch) {
		ChannelPipeline pipeline = ch.pipeline();
		if (appConfig.isSslEnabled()) {
			SSLContext sslContext = SslUtil.createSSLContext();
			SSLEngine sslEngine = sslContext.createSSLEngine();
			sslEngine.setUseClientMode(false);
			sslEngine.setNeedClientAuth(false);
			pipeline.addBefore("UchatText", "SslHandler", new SslHandler(sslEngine));		
		}
		pipeline.addBefore("UchatText", "HttpServerCodec", new HttpServerCodec());
		pipeline.addBefore("UchatText", "HttpObjectAggregator", new HttpObjectAggregator(64*1024));
		pipeline.addBefore("UchatText", "ChunkedWriteHandler", new UchatHttpRequestHandler(appConfig.getHttpPort(), "/ws"));
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
	
	public void sendDenyMsg(Channel channel, String msg) {	
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.setType(MsgType.Connect.getVal());		
		serverMsg.setChannel(null);
		serverMsg.setFromUserId(null);
		serverMsg.setData(msg);
		serverMsg.setDevice(DeviceType.Sys.getVal());
		sendMsg(channel, DeviceType.Sys, serverMsg);
	}
	
	private void sendMsg(Set<Channel> toChannels, final ServerMsg serverMsg) {
		toChannels.forEach(channel -> {
			sendMsg(channel, serverMsg);
		});
	}
	
	private void sendMsg(Channel channel, ServerMsg serverMsg) {
		DeviceType device = channel.attr(Constants.Client.DEVICE_TYPE).get();
		sendMsg(channel, device, serverMsg);
	}
	
	private void sendMsg(Channel channel, DeviceType device, ServerMsg serverMsg) {
		String json;
		try {
			json = JsonUtil.Object2Json(serverMsg);
		} catch (IOException e) {
			throw new AppException(e);
		}
		sendMsg(channel, device, json);
	}
	
	public void notifyAll(Channel selfChannel, String msg) {
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.setType(MsgType.Notify.getVal());
		serverMsg.setChannel(selfChannel.id().asShortText());
		DeviceType myClientType = selfChannel.attr(Constants.Client.DEVICE_TYPE).get();
		serverMsg.setDevice(myClientType.getVal());
		serverMsg.setData(msg);
		try {
			String json = JsonUtil.Object2Json(serverMsg);
			for (Channel channel : channels) {
				DeviceType clientType = channel.attr(Constants.Client.DEVICE_TYPE).get();
				sendMsg(channel, clientType, json);
			}
		} catch (IOException e) {
			log.error("failed to notify message");
			return;
		}
	}

	private void sendMsg(Channel channel, DeviceType clientType, String msg) {
		if (clientType == DeviceType.Web) {
			channel.writeAndFlush(new TextWebSocketFrame(msg));
		} else {
			channel.writeAndFlush(msg);
		}
	}
	
	public void handleMessage(Channel channel, ClientMsg chatMsg) {
		MsgType msgType = MsgType.Int2MsgType(chatMsg.getType());
		switch(msgType) {
		case Connect:
			handleConnectMsg(channel, chatMsg);
			break;
		case Text:
			handleTextMsg(channel, chatMsg);
			break;
		case Picture:
			handlePicMsg(channel, chatMsg);
			break;
		default:
			log.error("unsupported message: type=" + chatMsg.getType());
		}
		channel.attr(Constants.Client.ACTIVE_TIME).set(System.currentTimeMillis());		
	}
	
	private void handleConnectMsg(Channel channel, ClientMsg chatMsg) {
		String chatToken = chatMsg.getData();
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.setType(MsgType.Connect.getVal());
		DeviceType clientType = channel.attr(Constants.Client.DEVICE_TYPE).get();
		serverMsg.setDevice(clientType.getVal());
		if (!jwt.isTokenValidate(chatToken)) {
			serverMsg.setChannel(null);
			serverMsg.setFromUserId(null);
			serverMsg.setData("invalid token");
		} else {
			UserAuthInfo userAuthInfo = jwt.getUserFromToken(chatToken);
			String username = userAuthInfo.getUsername();
			User user = mapperUser.selectUserByLogin(username);
			channel.attr(Constants.Client.CLIENT_USER).set(user);
			serverMsg.setFromUserId(null);
			serverMsg.setChannel(channel.id().asShortText());
			serverMsg.setData("welcome " + UserType.parse(user.getType()).getTitle() + " " + user.getFirstName() + " " + user.getLastName());
		}
        sendMsg(channel, serverMsg);
	}
	
	private void handleTextMsg(Channel channel, ClientMsg chatMsg) {
		handleChatMsg(channel, MsgType.Text, chatMsg);
	}

	private void handlePicMsg(Channel channel, ClientMsg chatMsg) {
		handleChatMsg(channel, MsgType.Picture, chatMsg);
	}
	
	private void handleChatMsg(Channel channel, MsgType msgType, ClientMsg chatMsg) {
		String toUserId = chatMsg.getToUserId();
		Set<Channel> toChannels = findChannelByUserId(toUserId);

		String data = chatMsg.getData();
		User fromUser = channel.attr(Constants.Client.CLIENT_USER).get();
		DeviceType fromDevice = channel.attr(Constants.Client.DEVICE_TYPE).get();
		
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.setType(msgType.getVal());
		serverMsg.setChannel(channel.id().asShortText());
		serverMsg.setFromUserId(fromUser.getId());
		serverMsg.setDevice(fromDevice.getVal());
		serverMsg.setData(data);

		sendMsg(toChannels, serverMsg);
	}
	
	public void sendNotify(Channel channel, User toUser, String msg) {
		String toUserId = toUser.getId();
		Set<Channel> toChannels = findChannelByUserId(toUserId);
		
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.setType(MsgType.Notify.getVal());
		serverMsg.setChannel(channel.id().asShortText());
		serverMsg.setFromUserId(null);
		serverMsg.setDevice(DeviceType.Sys.getVal());
		serverMsg.setData(msg);

		sendMsg(toChannels, serverMsg);
	}
	
	private Set<Channel> findChannelByUserId(String userId) {
		final Set<Channel> toChannels = new HashSet<>();
		channels.forEach(channel -> {
			User user = channel.attr(Constants.Client.CLIENT_USER).get();
			if (user.getId().equals(userId)) {
				toChannels.add(channel);
			}
		});
		return toChannels;
	}
	
	public void freeInactiveChannel() {
		final Set<Channel> inactiveChannels = new HashSet<>();
		channels.forEach(channel -> {
			Long activeTime = channel.attr(Constants.Client.ACTIVE_TIME).get();
			Long expireTs = activeTime + appConfig.getExpirationMs();
			if (expireTs < System.currentTimeMillis()) {
				inactiveChannels.add(channel);
			}
		});
		inactiveChannels.forEach(channel -> {
			User user = channel.attr(Constants.Client.CLIENT_USER).get();
			log.info("disconnect inactive user:" + user.getId() + "(" + user.getEmail() + ")");
			channel.close();
		});
	}
}
