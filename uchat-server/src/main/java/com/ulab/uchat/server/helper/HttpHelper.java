package com.ulab.uchat.server.helper;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.ulab.uchat.server.config.AppConfig;

@Component
public class HttpHelper {
	@Autowired AppConfig appConfig;
    private static final Logger logger = LoggerFactory.getLogger(HttpHelper.class);
    @Autowired RestTemplate restTemplate;
    
	public boolean verifyUserByRemoteAuth(String username, String password) {
		Map<String, String> param = new HashMap<String, String>() {
			{
				put("j_username", username);
				put("j_password", password);
			}
		};
		String url = getUrl(appConfig.getRemoteAuth(), param);
		try {
			ResponseEntity<Object> rspEntity = restTemplate.postForEntity(url, null, Object.class);
			logger.info("remote verify user succeed: user=" + username + ", url=" + appConfig.getRemoteAuth());
			return rspEntity.getStatusCode().is2xxSuccessful();
		} catch (Exception e) {
			logger.info("remote verify user failed: user=" + username + ", url=" + appConfig.getRemoteAuth());
			return false;
		}
	}
	
	private String getUrl(String baseUrl, Map<String, ?> urlParams) {
		String url = baseUrl;
		if (urlParams != null && !urlParams.isEmpty()) {
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
			for (String paramName: urlParams.keySet()) {
				builder = builder.queryParam(paramName, urlParams.get(paramName));
			}
			url = builder.build().encode().toUri().toString();
		}
		return url;
	}
}
