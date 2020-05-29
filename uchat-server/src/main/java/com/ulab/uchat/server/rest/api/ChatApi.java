package com.ulab.uchat.server.rest.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ulab.uchat.server.config.AppConfig;
import com.ulab.uchat.server.exception.AppException;

import io.swagger.annotations.Api;

@Api
@Controller
@RequestMapping(value="/api/chat")
public class ChatApi {
    @Autowired AppConfig appConfig;
    private static final Logger log = LoggerFactory.getLogger(ChatApi.class);

    @ResponseBody
    @RequestMapping(value="/picture/channel/{id}", 
    				produces= "application/json", 
    				method= RequestMethod.POST,
    				consumes={"miltipart/mixed", 
    						  "multipart/form-data", 
    						  "application/json", 
    						  "application/octet-stream", 
    						  "application/x-binary"})
    public Object uploadPicture(@PathVariable(value="id") String channelId,
				        @RequestPart(value="file") MultipartFile file,
				        HttpServletResponse response,
				        HttpServletRequest request) {
        log.info("REST API: " + request.getRequestURI() + ", channel=" + channelId);
        if (file.isEmpty()) {
            log.info("no file attached");
            throw new AppException("no file attached"); 
        }
        String filePath = appConfig.getPictureDirPath() + File.separator + channelId + File.separator;
        try {
        	String fileName = transferFile(file, filePath);
            log.info("upload done: " + filePath + fileName);
            return new HashMap<String, Object>() {
            	{
            		put("pic", fileName);
            	}
            };
        } catch (IOException e) {
            log.error(e.toString(), e);
            throw new AppException("uploading picture failed");
        }
    }
    
    @ResponseBody
    @RequestMapping(value="/file/channel/{channel}/pic/{pic}", 
	produces= MediaType.APPLICATION_OCTET_STREAM_VALUE, 
	method= RequestMethod.GET)
	public void downloadPicture(
			@PathVariable(value="channel") String channelId,
			@PathVariable(value="pic") String picName,
			@RequestParam(value="chatToken") String chatToken,
            HttpServletResponse response,
            HttpServletRequest request) throws IOException {
        log.info("REST API: " + request.getRequestURI() + ", pic=" + picName);
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
    	response.setHeader("Content-disposition", "attachment; filename=\"" + picName + "\"");
        String filePath = appConfig.getPictureDirPath() + File.separator + channelId + File.separator + picName;
		OutputStream os = response.getOutputStream();
        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);
        IOUtils.copyLarge(inputStream, os);
	}

    @ResponseBody
    @RequestMapping(value="/picture/channel/{channel}/pic/{pic}", 
	method= RequestMethod.GET)
	public void showPicture(
			@PathVariable(value="channel") String channelId,
			@PathVariable(value="pic") String picName,
			@RequestParam(value="chatToken") String chatToken,
            HttpServletResponse response,
            HttpServletRequest request) throws IOException {
        log.info("REST API: " + request.getRequestURI() + ", pic=" + picName);
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setContentType("image/jpeg");
        String filePath = appConfig.getPictureDirPath() + File.separator + channelId + File.separator + picName;
		OutputStream os = response.getOutputStream();
        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);
        IOUtils.copyLarge(inputStream, os);
	}
    
    @ResponseBody
    @RequestMapping(value="/file/channel/{id}", 
    				produces= "application/json", 
    				method= RequestMethod.POST,
    				consumes={"miltipart/mixed", 
    						  "multipart/form-data", 
    						  "application/json", 
    						  "application/octet-stream", 
    						  "application/x-binary"})
    public Object uploadFile(@PathVariable(value="id") String channelId,
				        @RequestPart(value="file") MultipartFile file,
				        HttpServletResponse response,
				        HttpServletRequest request) {
        log.info("REST API: " + request.getRequestURI() + ", channel=" + channelId);
        if (file.isEmpty()) {
            log.info("no file attached");
            throw new AppException("no file attached"); 
        }
        String filePath = appConfig.getFileDirPath() + File.separator + channelId + File.separator;
        try {
        	String fileName = transferFile(file, filePath);
            log.info("upload done: " + filePath + fileName);
            return new HashMap<String, Object>() {
            	{
            		put("fileName", fileName);
            	}
            };
        } catch (IOException e) {
            log.error(e.toString(), e);
            throw new AppException("Uploading file failed");
        }
    }
    
    @ResponseBody
    @RequestMapping(value="/file/channel/{channel}/file/{fileName}", 
	produces= MediaType.APPLICATION_OCTET_STREAM_VALUE, 
	method= RequestMethod.GET)
	public void downloadFile(
			@PathVariable(value="channel") String channelId,
			@PathVariable(value="fileName") String fileName,
			@RequestParam(value="chatToken") String chatToken,
            HttpServletResponse response,
            HttpServletRequest request) throws IOException {
        log.info("REST API: " + request.getRequestURI() + ", fileName=" + fileName);
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
    	response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
        String filePath = appConfig.getFileDirPath() + File.separator + channelId + File.separator + fileName;
        FileInputStream inputStream = null;
        try {
        	OutputStream os = response.getOutputStream();
            File file = new File(filePath);
            inputStream = new FileInputStream(file);
            IOUtils.copyLarge(inputStream, os);
		} catch (Exception e) {
			log.error("Download file exception", e);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
		
	}
    
    private String transferFile(MultipartFile file, String filePath) throws IOException {
    	String fileName = "";
    	if (file != null) {
    		String originalFileName = file.getOriginalFilename();
            int suffixIndex = originalFileName.lastIndexOf('.');
            String suffix = originalFileName.substring(suffixIndex);
            File fileFolder = new File(filePath);
            if (!fileFolder.exists()) {
            	fileFolder.mkdirs();
    		}
            fileName = System.currentTimeMillis() + suffix;
            File dest = new File(filePath + fileName);
            file.transferTo(dest);
		}
    	return fileName;
    }
}
