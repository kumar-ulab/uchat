package com.ulab.uchat.server.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ulab.uchat.constant.Constants;
import com.ulab.uchat.model.pojo.ChatDevice;
import com.ulab.uchat.model.pojo.Message;
import com.ulab.uchat.model.pojo.User;
import com.ulab.uchat.pojo.ClientMsg;
import com.ulab.uchat.pojo.ServerMsg;
import com.ulab.uchat.server.config.AppConfig;
import com.ulab.uchat.server.dao.mapper.MapperMessage;
import com.ulab.uchat.server.dao.mapper.MapperUser;
import com.ulab.uchat.server.exception.AppException;
import com.ulab.uchat.server.handler.ConnectionHandler;
import com.ulab.uchat.server.handler.UchatHttpRequestHandler;
import com.ulab.uchat.server.handler.WebsocketFrameHandler;
import com.ulab.uchat.server.push.PushMsg;
import com.ulab.uchat.server.security.JwtUtils;
import com.ulab.uchat.server.security.auth.UserAuthInfo;
import com.ulab.uchat.server.task.PushMessageTask;
import com.ulab.uchat.server.task.TaskManager;
import com.ulab.uchat.types.ChannelType;
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
	
	@Autowired
	private MapperMessage mapperMessage;
	
	@Autowired
	private TaskManager taskManager;
	
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
		serverMsg.setDevice(ChannelType.Sys.name());
		serverMsg.setCreateTime(new Date());
		sendMsg(channel, ChannelType.Sys, serverMsg);
	}
	
	private void sendMsg(Set<Channel> toChannels, final ServerMsg serverMsg) {
		toChannels.forEach(channel -> {
			sendMsg(channel, serverMsg);
		});
	}
	
	private void sendMsg(Channel channel, ServerMsg serverMsg) {
		ChannelType device = channel.attr(Constants.Client.CHANNEL_TYPE).get();
		sendMsg(channel, device, serverMsg);
	}
	
	private void sendMsg(Channel channel, ChannelType device, ServerMsg serverMsg) {
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
		String deviceType = selfChannel.attr(Constants.Client.DEVICE_TYPE).get();
		serverMsg.setDevice(deviceType);
		serverMsg.setData(msg);
		serverMsg.setCreateTime(new Date());
		try {
			String json = JsonUtil.Object2Json(serverMsg);
			for (Channel channel : channels) {
				ChannelType clientType = channel.attr(Constants.Client.CHANNEL_TYPE).get();
				sendMsg(channel, clientType, json);
			}
		} catch (IOException e) {
			log.error("failed to notify message");
			return;
		}
	}

	private void sendMsg(Channel channel, ChannelType clientType, String msg) {
		if (clientType == ChannelType.Web) {
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
		case File:
			handleFileMsg(channel, chatMsg);
			break;
		default:
			log.error("unsupported message: type=" + chatMsg.getType());
		}
		channel.attr(Constants.Client.ACTIVE_TIME).set(System.currentTimeMillis());		
	}
	
	private void handleConnectMsg(Channel channel, ClientMsg chatMsg) {
		String chatToken = chatMsg.getData();
		ServerMsg serverMsg = new ServerMsg();
		ChatDevice dev = getDeviceFromDeviceInfo(chatMsg.getDevice());
		serverMsg.setType(MsgType.Connect.getVal());
		if (!jwt.isTokenValidate(chatToken)) {
			serverMsg.setChannel(null);	
			serverMsg.setFromUserId(null);
			serverMsg.setData("invalid token");
			serverMsg.setCreateTime(new Date());
	        sendMsg(channel, serverMsg);
		} else {
			UserAuthInfo userAuthInfo = jwt.getUserFromToken(chatToken);
			String username = userAuthInfo.getUsername();
			User user = mapperUser.selectUserByLogin(username);
			channel.attr(Constants.Client.CLIENT_USER).set(user);
			channel.attr(Constants.Client.DEVICE_TYPE).set(dev.getDeviceType());		
			mapperUser.updateAndInsertDevice(user.getId(), dev.getDeviceType(), dev.getPushAddress());
			serverMsg.setFromUserId(null);
			serverMsg.setChannel(channel.id().asShortText());
			serverMsg.setData("welcome " + UserType.parse(user.getType()).getTitle() + " " + user.getFirstName() + " " + user.getLastName());
			serverMsg.setDevice(dev.getDeviceType());
			serverMsg.setCreateTime(new Date());
	        sendMsg(channel, serverMsg);
			pushLeaveMessage(channel);
		}
	}
	
	private void handleTextMsg(Channel channel, ClientMsg chatMsg) {
		handleChatMsg(channel, MsgType.Text, chatMsg);
	}

	private void handlePicMsg(Channel channel, ClientMsg chatMsg) {
		handleChatMsg(channel, MsgType.Picture, chatMsg);
	}
	
	private void handleFileMsg(Channel channel, ClientMsg chatMsg) {
		handleChatMsg(channel, MsgType.File, chatMsg);
	}
	
	private void handleChatMsg(Channel channel, MsgType msgType, ClientMsg chatMsg) {
		String toUserId = chatMsg.getToUserId();
		User fromUser = channel.attr(Constants.Client.CLIENT_USER).get();
		Set<Channel> toChannels = findChannelByUserId(toUserId);
		if (toChannels == null || toChannels.isEmpty()) {
			List<String> deviceTokens = new ArrayList<String>();
			Message message = new Message();
			message.setId(UUID.randomUUID().toString().replace("-", ""));
			message.setFromUserId(fromUser.getId());
			message.setToUserId(toUserId);
			message.setType(String.valueOf(chatMsg.getType()));
			message.setContent(chatMsg.getData());
			message.setCreateTime(new Date());
			message.setUpdateTime(new Date());
			message.setStatus(Constants.MESSAGE_STATUS_SEND);
			int index = mapperMessage.saveMessage(message);
			if (index == 0) {
				log.error("failed to save message");
			}
			String deviceToken = getDeviceTokenByUserId(toUserId);
			deviceTokens.add(deviceToken);
			PushMessageTask pushMessageTask = taskManager.createPushMessageTask(fromUser.getFirstName() + fromUser.getLastName(), chatMsg.getData(), deviceTokens);
			taskManager.submitTask(pushMessageTask, fromUser.getId());
		} else {
			String data = chatMsg.getData();
			ServerMsg serverMsg = new ServerMsg();
			serverMsg.setType(msgType.getVal());
			serverMsg.setChannel(channel.id().asShortText());
			serverMsg.setFromUserId(fromUser.getId());
			String fromDeviceType = channel.attr(Constants.Client.DEVICE_TYPE).get();
			serverMsg.setDevice(fromDeviceType);
			serverMsg.setData(data);
			serverMsg.setCreateTime(new Date());

			sendMsg(toChannels, serverMsg);
		}
	}
	
	public void sendNotify(Channel channel, User toUser, String msg) {
		String toUserId = toUser.getId();
		Set<Channel> toChannels = findChannelByUserId(toUserId);
		
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.setType(MsgType.Notify.getVal());
		serverMsg.setChannel(channel.id().asShortText());
		serverMsg.setFromUserId(null);
		serverMsg.setDevice(ChannelType.Sys.name());
		serverMsg.setData(msg);
		serverMsg.setCreateTime(new Date());

		sendMsg(toChannels, serverMsg);
	}
	
	private Set<Channel> findChannelByUserId(String userId) {
		final Set<Channel> toChannels = new HashSet<>();
		channels.forEach(channel -> {
			User user = channel.attr(Constants.Client.CLIENT_USER).get();
			if (user != null && user.getId() != null && user.getId().equals(userId)) {
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
			taskManager.shutdown(user.getId());
		});
	}
	
	private ChatDevice getDeviceFromDeviceInfo(String deviceInfo) {
		ChatDevice dev = new ChatDevice();
		try {
			String[] parts = deviceInfo.split("@");
			dev.setDeviceType(parts[0]);
			dev.setPushAddress(parts[1]);
		} catch (Exception e) {
			log.error("Invalid device info: deviceInfo", e);
		}
		return dev;
	}
	
	private void pushLeaveMessage(Channel channel) {
		ServerMsg serverMsg = null;
		List<String> ids = new ArrayList<String>();
		User toUser = channel.attr(Constants.Client.CLIENT_USER).get();
		if (toUser != null) {
			List<Message> messageList = mapperMessage.findLeaveMessageByToUserId(toUser.getId());
			if (messageList != null && messageList.size() > 0) {
				for (Message message : messageList) {
					serverMsg = new ServerMsg();
					serverMsg.setType(Integer.parseInt(message.getType()));
					serverMsg.setChannel(channel.id().asShortText());
					serverMsg.setFromUserId(message.getFromUserId());
					String fromDeviceType = channel.attr(Constants.Client.DEVICE_TYPE).get();
					serverMsg.setDevice(fromDeviceType);
					serverMsg.setData(message.getContent());
					serverMsg.setCreateTime(message.getCreateTime());
					sendMsg(channel, serverMsg);
					ids.add(message.getId());
				}
				mapperMessage.batchUpdateStatus(Constants.MESSAGE_STATUS_RECEIVE, ids);
			}
		} else {
			log.error("The user is not online");
		}
	}
	
	public String getDeviceTokenByUserId(String userId) {
		ChatDevice chatDevice = mapperUser.selectDevice(userId);
		return chatDevice.getPushAddress();
	}
}
