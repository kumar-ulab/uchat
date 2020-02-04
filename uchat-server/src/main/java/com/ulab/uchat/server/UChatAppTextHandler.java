package com.ulab.uchat.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ulab.uchat.server.types.ClientType;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class UChatAppTextHandler extends UlabChannelInboundHandler<String> {
    private static final Logger log = LoggerFactory.getLogger(ConnectionHandler.class);
	protected UChatAppTextHandler() {
		super(ClientType.WinApp);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel incoming = ctx.channel();
        String attr = ChannelUtil.getChannelAttr(incoming);
        log.info(attr + " " + msg);
        ChannelUtil.sendMsgToAll(incoming, msg);
	}
}