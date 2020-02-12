package com.ulab.uchat.server.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ulab.uchat.server.types.ClientType;
import com.ulab.uchat.server.util.ChannelUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.AttributeKey;

public class ClientTypeJudgeHandler extends ByteToMessageDecoder {
    private static final int MAX_LENGTH = 23;

	private static AttributeKey<ClientType> AttrClientType = AttributeKey.valueOf("ClientType"); 
    private static final Logger log = LoggerFactory.getLogger(ClientTypeJudgeHandler.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        String protocol = getBufStart(in);
        ChannelPipeline pipeline = ctx.pipeline();
        if (protocol.startsWith("win") || protocol.startsWith(" ")) {
        	ctx.channel().attr(AttrClientType).set(ClientType.WinApp);
//        	ChannelUtil.addSocketHandlers(ctx.channel());
        } else if(protocol.startsWith("ios")) {
        	ctx.channel().attr(AttrClientType).set(ClientType.IosApp);
//        	ChannelUtil.addSocketHandlers(ctx.channel());
        } else {
        	ctx.channel().attr(AttrClientType).set(ClientType.WebSocket);
//        	ChannelUtil.addWebSocketHandlers(ctx.channel());
        }
        in.resetReaderIndex();
        pipeline.remove(this.getClass());
		ChannelUtil.notifyAll(ctx.channel(), "join");
    }

    private String getBufStart(ByteBuf in){
        int length = in.readableBytes();
        if (length > MAX_LENGTH) {
            length = MAX_LENGTH;
        }

        in.markReaderIndex();
        byte[] content = new byte[length];
        in.readBytes(content);
        return new String(content);
    }
}
