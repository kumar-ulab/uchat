package com.ulab.uchat.server.util;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import com.ulab.uchat.server.handler.HttpRequestHandler;
import com.ulab.uchat.server.handler.TextWebSocketFrameHandler;
import com.ulab.uchat.server.handler.UChatAppTextHandler;
import com.ulab.uchat.server.online.UChatConnections;
import com.ulab.uchat.server.types.ClientType;
import com.ulab.util.SslUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.AttributeKey;

public class ChannelUtil {
	private ChannelUtil() {}
	private static AttributeKey<ClientType> AttrClientType = AttributeKey.valueOf("ClientType"); 
    private static UChatConnections  connections= UChatConnections.getInstance();	
	public static void addWebSocketHandlers(Channel ch) {
		ChannelPipeline pipeline = ch.pipeline();
		SSLContext sslContext = SslUtil.createSSLContext();
		SSLEngine sslEngine = sslContext.createSSLEngine();
		sslEngine.setUseClientMode(false);
		sslEngine.setNeedClientAuth(false);
		pipeline.addLast("ssl", new SslHandler(sslEngine));		
		pipeline.addLast(new HttpServerCodec());
		pipeline.addLast(new HttpObjectAggregator(64*1024));
		pipeline.addLast(new ChunkedWriteHandler());
		pipeline.addLast(new HttpRequestHandler("/ws"));
		pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
		pipeline.addLast(new TextWebSocketFrameHandler());
	}

	public static String getChannelAttr(Channel channel) {
		ClientType clientType = channel.attr(AttrClientType).get();
		return "[SERVER] - " + clientType + "(" + channel.remoteAddress() + ")";
	}
	
	public static void addSocketHandlers(Channel ch) {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
		pipeline.addLast("decoder", new StringDecoder());
		pipeline.addLast("encoder", new StringEncoder());
		pipeline.addLast("text", new UChatAppTextHandler());
	}
	
	public static void notifyAll(Channel selfChannel, String msg) {
		ChannelGroup channels = connections.getChannels();
		for (Channel channel : channels) {
		    channel.writeAndFlush(new TextWebSocketFrame(getChannelAttr(selfChannel) + " " + msg));
		}
	}

	public static void sendMsgToAll(Channel selfChannel, String msg) {
		ChannelGroup channels = connections.getChannels();
		String attr = getChannelAttr(selfChannel);
		for (Channel channel : channels) {
			String text; 
            if (channel != selfChannel){
            	text = attr + " " + msg + "\n";
            } else {
            	text = "[you]" + msg + "\n";
            }
    		ClientType clientType = channel.attr(AttrClientType).get();
    		if (ClientType.WebSocket == clientType) {
		        channel.writeAndFlush(new TextWebSocketFrame(text));
    		} else {
    			channel.writeAndFlush(text);
    		}
		}
	}
}
