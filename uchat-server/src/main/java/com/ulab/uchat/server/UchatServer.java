package com.ulab.uchat.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ulab.uchat.server.web.controller.WebChatController;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

@Component
public class UchatServer implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(UchatServer.class);

    @Value("${netty.port}")
    private int port;

    public UchatServer() {
    }

    private void start() {
    	Thread.currentThread().setName("UchatServer");
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new UchatServerInitializer())
             .option(ChannelOption.SO_BACKLOG, 128)
             .childOption(ChannelOption.SO_KEEPALIVE, true);
//            SslUtil.createSSLContext();
            log.info("UchatServer started");

            ChannelFuture f = b.bind(port).sync();

            f.channel().closeFuture().sync();

        } catch(Exception e) {
        	log.error("Uchat server got serial problem, exit !", e);
        	System.exit(1);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            log.info("UchatServer stopped");
        }
    }

	@Override
	public void run() {
		start();
	}
}