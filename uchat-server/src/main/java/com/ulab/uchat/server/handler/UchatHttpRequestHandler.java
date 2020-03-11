package com.ulab.uchat.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ulab.uchat.constant.Constants;
import com.ulab.uchat.server.helper.SpringBeanHelper;
import com.ulab.uchat.server.service.ChatService;
import com.ulab.uchat.types.DeviceType;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class UchatHttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger log = LoggerFactory.getLogger(UchatHttpRequestHandler.class);
    private final String wsUri;
    private ChatService chatService = SpringBeanHelper.getBean(ChatService.class);

    int httpPort;
    
    public UchatHttpRequestHandler(int httpPort, String wsUri) {
    	this.httpPort = httpPort;
        this.wsUri = wsUri;
    }

    protected boolean isChatRequest(FullHttpRequest request) {
    	String uri = request.getUri();
        return wsUri.equalsIgnoreCase(uri);
    }    	
    
    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
    	Channel channel = ctx.channel();
    	if (channel.hasAttr(Constants.Client.DEVICE_TYPE)) {
    		if (isChatRequest(request)) {
                ctx.fireChannelRead(request.retain());
    		} else {							//normal http but not ws request
    			HttpHeaders headers = request.headers();
    			String host = headers.get("HOST");
    			int pos = host.indexOf(':');
    			if (pos > 0) {
    				host = host.substring(0, pos);
    			}
            	String newUrl = "https://" + host + ":" + httpPort + "/";
            	FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
    			response.headers().set(HttpHeaderNames.LOCATION, newUrl);
//            	FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
//                channel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    		}
    	}
    }
}