package com.ulab.uchat.server.push;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import com.turo.pushy.apns.ApnsClient;
import com.turo.pushy.apns.PushNotificationResponse;
import com.turo.pushy.apns.util.ApnsPayloadBuilder;
import com.turo.pushy.apns.util.SimpleApnsPushNotification;
import com.turo.pushy.apns.util.TokenUtil;
import com.turo.pushy.apns.util.concurrent.PushNotificationFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class PushMsg {
	
	private static final Logger logger = LoggerFactory.getLogger(PushMsg.class);
	
	private static final String TEAM_ID = "35382Z9Q8G";
	
	private static final String KEY_ID = "G9MBG6X2CT";
	
	private static final String TOPIC = "com.ulab.uChat";
	
	private static final String CERTIFICATE_FILE_DIR = "certificate";
	
	private static final String CERTIFICATE_FILE_NAME_PREFIX = "AuthKey_G9MBG6X2CT";
	
	private static final String CERTIFICATE_FILE_NAME_SUFFIX = ".p8";
		
	/**
	 * build Apns payload
	 * @param title
	 * @param content
	 * @return
	 */
	public static ApnsPayloadBuilder buildApnsPayload(String title, String content) {
		ApnsPayloadBuilder payloadBuilder = new ApnsPayloadBuilder();
		payloadBuilder.setSound("default");
		payloadBuilder.setBadgeNumber(1);
		payloadBuilder.setContentAvailable(false);
		if (title != null && content != null) {
			payloadBuilder.setAlertTitle(title);
			payloadBuilder.setAlertBody(content);
		}
		return payloadBuilder;
	}
	
	@SuppressWarnings("rawtypes")
	public static void send(List<String> deviceTokens, String title, String content) {
		ApnsClient apnsClient = APNSConnect.getConnect(getCertificateInputStream(), TEAM_ID, KEY_ID, false);
		if (apnsClient != null) {
			if (deviceTokens != null && deviceTokens.size() > 0) {
				for (String deviceToken : deviceTokens) {
					ApnsPayloadBuilder payloadBuilder = buildApnsPayload(title, content);
					String payload = payloadBuilder.buildWithDefaultMaximumLength();
					String token = TokenUtil.sanitizeTokenString(deviceToken);
					SimpleApnsPushNotification pushNotification = new SimpleApnsPushNotification(token, TOPIC, payload);
					PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>>
					sendNotificationFuture = apnsClient.sendNotification(pushNotification);
					sendNotificationFuture.addListener(new GenericFutureListener<Future<PushNotificationResponse>>() {
					    @Override
					    public void operationComplete(final Future<PushNotificationResponse> future) throws Exception {
					        final PushNotificationResponse response = future.getNow();
					        if (response.isAccepted()) {
					        	logger.info("Send Success...");
					        } else {
					            Date invalidTime = response.getTokenInvalidationTimestamp();
					            logger.error("Notification rejected by the APNs gateway: " + response.getRejectionReason());
					            if (invalidTime != null) {
					            	logger.error("The token is invalid as of " + response.getTokenInvalidationTimestamp());
					            }
					        }
					    }
					});
				}
			}
		} else {
			logger.error("ApnsClient should not be null");
		}
	}
	
	public static InputStream getCertificateInputStream() {
		InputStream is = null;
		String certificatePath = CERTIFICATE_FILE_DIR + File.separator + CERTIFICATE_FILE_NAME_PREFIX + CERTIFICATE_FILE_NAME_SUFFIX;
		try {
			ClassPathResource classPathResource = new ClassPathResource(certificatePath);
			is = classPathResource.getInputStream();
		} catch (Exception e) {
			logger.error("File not found:" + certificatePath);
			logger.error("Exception Message:{}", e.getMessage());
		} 
		return is;
	}

}
