package com.ulab.uchat.server.push;

import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.turo.pushy.apns.ApnsClient;
import com.turo.pushy.apns.ApnsClientBuilder;
import com.turo.pushy.apns.auth.ApnsSigningKey;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class APNSConnect {
	
	private static final Logger logger = LoggerFactory.getLogger(APNSConnect.class);
	
	private static ApnsClient apnsClient = null;
	
	private static final int THREADS_COUNT = 4;
	
	private static final int CONCURRENT_CONNECTIONS_COUNT = 4;
	
	private APNSConnect() {}
	
	public static ApnsClient getConnect(InputStream inputStream, String teamId, String keyId, boolean isProduction) {
		String envHost = ApnsClientBuilder.DEVELOPMENT_APNS_HOST;
		if (apnsClient == null) {
			try {
				if (isProduction) {
					envHost = ApnsClientBuilder.PRODUCTION_APNS_HOST;
				} else {
					envHost = ApnsClientBuilder.DEVELOPMENT_APNS_HOST;
				}
				EventLoopGroup eventLoopGroup = new NioEventLoopGroup(THREADS_COUNT);
				if (inputStream != null) {
					apnsClient = new ApnsClientBuilder().setApnsServer(envHost)
							.setSigningKey(ApnsSigningKey.loadFromInputStream(inputStream, teamId, keyId))
							.setConcurrentConnections(CONCURRENT_CONNECTIONS_COUNT).setEventLoopGroup(eventLoopGroup).build();
				}
			} catch (Exception e) {
				logger.error("Create client connection fail");
				logger.error("Exception Message:{}", e.getMessage());
			}
		}
		return apnsClient;
	}
	
}
