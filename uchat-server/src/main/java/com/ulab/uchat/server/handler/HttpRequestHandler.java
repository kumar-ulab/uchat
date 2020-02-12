package com.ulab.uchat.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ulab.uchat.server.types.ClientType;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AttributeKey;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> { //1
	private static AttributeKey<ClientType> AttrClientType = AttributeKey.valueOf("ClientType"); 
    private static final Logger log = LoggerFactory.getLogger(HttpRequestHandler.class);
    private final String wsUri;

    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    protected ClientType assignChannleType(Channel channel, FullHttpRequest request) {
    	String uri = request.getUri();
        if (wsUri.equalsIgnoreCase(uri)) {
        	channel.attr(AttrClientType).set(ClientType.WebSocket);
            return ClientType.WebSocket;
        } else {
        	channel.attr(AttrClientType).set(ClientType.WinApp);
            return ClientType.WinApp;
        }
    }    	
    
    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
    	ClientType clientType;
    	Channel channel = ctx.channel();
    	if (channel.hasAttr(AttrClientType)) {
    		clientType = channel.attr(AttrClientType).get();
    	} else {
    		clientType = assignChannleType(channel, request);
    	}
		if (clientType == ClientType.WebSocket) {
            ctx.fireChannelRead(request.retain());
        } else {
            if (HttpHeaders.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }
        }
    }
    
    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    	log.info("send response");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        Channel incoming = ctx.channel();
        log.error("Client:"+incoming.remoteAddress() + " error", cause);
        ctx.close();
    }
}