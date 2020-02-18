package com.ulab.uchat.client.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpService {
	@Autowired RestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(HttpService.class);
	
	public Map uploadpicture(String channelId, String filePath) {
		MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
		Resource sliceFile = new FileSystemResource(filePath);
		HttpHeaders httpHeader = new HttpHeaders();
		httpHeader.set("Content-Type", "application/x-binary");
		HttpEntity<Resource> sliceFilePart = new HttpEntity<Resource>(sliceFile, httpHeader);

		multipartRequest.add("file", sliceFilePart);
		
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartRequest, header);
		String url = "https://localhost:9443/picture/channel/" + channelId;
		log.info("upload picture : " + filePath);
		try {
			ResponseEntity<Map> rsp =  restTemplate.exchange(
					url,
					HttpMethod.POST, requestEntity, Map.class);
			if (rsp.getStatusCode() == HttpStatus.OK) {
				log.info("upload done: " + url);
				System.out.println(rsp.getBody());
				return rsp.getBody();
			} else {				
				log.error("upload error, status=" + rsp.getStatusCode() + ": " + url);
			}
		} catch (Exception e) {
			log.error("upload failed: " + url, e);
		}
		return null;
	}
}
