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
import com.ulab.uchat.model.pojo.User;
import com.ulab.uchat.pojo.ChatData;
import com.ulab.uchat.pojo.ChatPair;
import com.ulab.uchat.pojo.ChatPairGroup;
import com.ulab.uchat.pojo.ClientMsg;
import com.ulab.uchat.pojo.ServerMsg;
import com.ulab.uchat.server.config.AppConfig;
import com.ulab.uchat.server.exception.AppException;
import com.ulab.uchat.server.handler.ConnectionHandler;
import com.ulab.uchat.server.handler.UchatTextHandler;
import com.ulab.uchat.server.handler.UchatHttpRequestHandler;
import com.ulab.uchat.server.handler.WebsocketFrameHandler;
import com.ulab.uchat.server.security.JwtUtils;
import com.ulab.uchat.server.security.domain.ErrorStatus;
import com.ulab.uchat.types.ClientType;
import com.ulab.uchat.types.MsgType;
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
import io.netty.util.concurrent.GlobalEventExecutor;

@Service
public class ChatService {
    private static final Logger log = LoggerFactory.getLogger(ConnectionHandler.class);
	private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);	

	@Autowired AppConfig appConfig;
	@Autowired JwtUtils jwt;
	
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
			ChatData chatData = new ChatData((byte)MsgType.Picture.getVal(), info, data);
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
			sendMsg(channel, MsgType.Connect, msg);
		} catch (Exception e) {
			log.error("failed to notify message: channel=" + channel.id().asShortText());
			return;
		}
	}
	
	public void notify(Channel channel, String msg) {
		try {
			sendMsg(channel, MsgType.Notify, msg);
		} catch (Exception e) {
			log.error("failed to notify message: channel=" + channel.id().asShortText());
			return;
		}
	}
	
	private void sendMsg(Channel channel, MsgType type, String msg) {
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.setType(type.getVal());
		serverMsg.setChannel(channel.id().asShortText());
		serverMsg.setData(msg);
		ClientType clientType = channel.attr(Constants.Client.CLIENT_TYPE).get();
		serverMsg.setDevice(clientType.getVal());
		String json;
		try {
			json = JsonUtil.Object2Json(serverMsg);
		} catch (IOException e) {
			throw new AppException(e);
		}
		if (clientType == ClientType.Web) {
			sendWebMsg(channel, json);
		} else {
			channel.writeAndFlush(json);
		}
	}
	
	private void sendMsg(Channel channel, ServerMsg serverMsg) {
		ClientType clientType = channel.attr(Constants.Client.CLIENT_TYPE).get();
		serverMsg.setDevice(clientType.getVal());
		String json;
		try {
			json = JsonUtil.Object2Json(serverMsg);
		} catch (IOException e) {
			throw new AppException(e);
		}
		if (clientType == ClientType.Web) {
			sendWebMsg(channel, json);
		} else {
			channel.writeAndFlush(json);
		}
	}
	
	public void notifyAll(Channel selfChannel, String msg) {
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.setType(MsgType.Notify.getVal());
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
	
	public void notify(Channel channel, ServerMsg srvMsg) {
		String text;
		try {
			text = JsonUtil.Object2Json(srvMsg);
		} catch (IOException e) {
			log.error("failed to send message", e);
			return;
		}
		ClientType clientType = channel.attr(Constants.Client.CLIENT_TYPE).get();
		if (ClientType.Web == clientType) {
	        channel.writeAndFlush(new TextWebSocketFrame(text));
		} else {
			channel.writeAndFlush(text);
		}
	}
	
	public Channel findPairChannel(String userId) {
		for (Channel channel : channels) {
			User user = (User) (channel.attr(Constants.Client.CLIENT_USER).get());
			if (userId.equals(user.getId())) {
				return channel;
			}
		}
		return null;
	}
	
	public void sendPairResult(Channel channel, boolean isPaired) {
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.setType(MsgType.Select.getVal());
		serverMsg.setChannel(channel.id().asShortText());
		serverMsg.setData(String.valueOf(isPaired));
		notify(channel, serverMsg);
	}
	
	public void handleMessage(Channel channel, ClientMsg chatMsg) {
		MsgType msgType = MsgType.Int2MsgType(chatMsg.getType());
		switch(msgType) {
		case Connect:
			handleConnectMsg(channel, chatMsg.getData());
			break;
		case Select:
			handleSelectMsg(channel, chatMsg.getData());
			break;
		case Text:
			handleTextMsg(channel, chatMsg.getData());
			break;
		case Picture:
			handlePicMsg(channel, chatMsg.getData());
			break;
		default:
			log.error("unsupported message: type=" + chatMsg.getType());
		}
	}
	
	private void handleConnectMsg(Channel channel, String chatToken) {
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.setType(MsgType.Connect.getVal());
		ClientType clientType = channel.attr(Constants.Client.CLIENT_TYPE).get();
		serverMsg.setDevice(clientType.getVal());
		if (!jwt.isTokenValidate(chatToken)) {
			serverMsg.setChannel("");
			serverMsg.setData("invalid token");
		} else {
			serverMsg.setChannel(null);
			serverMsg.setData("welcome");
		}
        sendMsg(channel, serverMsg);
	}
	
	private void handleSelectMsg(Channel channel, String pairUserId) {
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.setType(MsgType.Select.getVal());		
		Channel pairChannel = findPairChannel(pairUserId);		
		if (pairChannel == null) {
			serverMsg.setChannel(null);
			serverMsg.setDevice(0);
			serverMsg.setData("offline");
			sendPairResult(pairChannel, false);
		} else {
			addPairChennl(channel, pairChannel);
			addPairChennl(pairChannel, channel);			
			sendPairResult(pairChannel, true);
		}
		ClientType clientType = pairChannel.attr(Constants.Client.CLIENT_TYPE).get();
		serverMsg.setChannel(pairChannel.id().asShortText());
		serverMsg.setDevice(clientType.getVal());
		serverMsg.setData("online");
		sendMsg(channel, serverMsg);
	}
	
	private void addPairChennl(Channel channel, Channel pairChannel) {
		User pairUser = (User) (pairChannel.attr(Constants.Client.CLIENT_USER).get());
		ChatPair thisChatPair = new ChatPair(pairUser, pairChannel);
		ChatPairGroup thisPairGroup = (ChatPairGroup) channel.attr(Constants.Client.PAIR_USERS).get();
		thisPairGroup.addPair(thisChatPair);
	}

	private void handleTextMsg(Channel channel, String text) {		
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.setType(MsgType.Text.getVal());
		serverMsg.setChannel(channel.id().asShortText());
		ClientType clientType = channel.attr(Constants.Client.CLIENT_TYPE).get();
		serverMsg.setDevice(clientType.getVal());
		serverMsg.setData(text);
        sendMsgToAll(channel, serverMsg);
	}

	private void handlePicMsg(Channel channel, String picId) {		
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.setType(MsgType.Picture.getVal());
		serverMsg.setChannel(channel.id().asShortText());
		ClientType clientType = channel.attr(Constants.Client.CLIENT_TYPE).get();
		serverMsg.setDevice(clientType.getVal());
		serverMsg.setData(picId);
        sendMsgToAll(channel, serverMsg);
	}	
}
