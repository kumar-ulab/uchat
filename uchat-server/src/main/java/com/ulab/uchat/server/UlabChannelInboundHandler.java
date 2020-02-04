package com.ulab.uchat.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ulab.uchat.server.online.UChatConnections;
import com.ulab.uchat.server.types.ClientType;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;

public abstract class UlabChannelInboundHandler<I> extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(UlabChannelInboundHandler.class);
	private ClientType clientType;
	private static AttributeKey<ClientType> AttrClientType = AttributeKey.valueOf("ClientType"); 
    private final boolean autoRelease;
    protected UChatConnections connections = UChatConnections.getInstance();

    protected UlabChannelInboundHandler(ClientType clientType) {
    	this(clientType, true);
    }
    
    protected UlabChannelInboundHandler(ClientType clientType, boolean autoRelease) {
    	this.clientType = clientType;
    	this.autoRelease = autoRelease;
    }
    
    protected boolean acceptChannelMessage(Channel channel) {
    	ClientType msgClientType = channel.attr(AttrClientType).get();
    	if (msgClientType == ClientType.WebSocket) {
    		return clientType == ClientType.WebSocket;
    	} else {
    		return clientType != ClientType.WebSocket;
    	}
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        boolean release = true;
        try {
            if (acceptChannelMessage(ctx.channel())) {
                @SuppressWarnings("unchecked")
                I imsg = (I) msg;
                channelRead0(ctx, imsg);
            } else {
                release = false;
                ctx.fireChannelRead(msg);
            }
        } finally {
            if (autoRelease && release) {
                ReferenceCountUtil.release(msg);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        Channel incoming = ctx.channel();
        log.error("Client:"+incoming.remoteAddress() + " error", cause);
        ctx.close();
    }

    protected abstract void channelRead0(ChannelHandlerContext ctx, I msg) throws Exception;

}

