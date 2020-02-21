package com.ulab.uchat.server.web.controller;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ulab.uchat.server.config.AppConfig;
import com.ulab.uchat.server.exception.AppException;
import com.ulab.util.FileUtil;

@Controller
public class WebChatController {
    private static final Logger log = LoggerFactory.getLogger(WebChatController.class);
    @Autowired AppConfig appConfig;
    
    @RequestMapping("/")
    public String index(Model model) {
    	model.addAttribute("chatPort", appConfig.getNettyPort());
    	if (appConfig.isSslEnabled()) {
        	model.addAttribute("ws", "wss");
    	} else {
        	model.addAttribute("ws", "ws");
    	}
        return "index";
    }

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

        String fileName = file.getOriginalFilename();
        int suffixIndex = fileName.lastIndexOf('.');
        String suffix = fileName.substring(suffixIndex);
        String filePath = appConfig.getUchatRoot() + File.separator + channelId + File.separator;
        File picFolder = new File(filePath);
        picFolder.mkdirs();
        String picName = System.currentTimeMillis() + suffix;
        File dest = new File(filePath + picName);
        try {
            file.transferTo(dest);
            log.info("upload done: " + dest);
            return new HashMap<String, Object>() {
            	{
            		put("pic", picName );
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
            HttpServletResponse response,
            HttpServletRequest request) throws IOException {
        log.info("REST API: " + request.getRequestURI() + ", pic=" + picName);
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
    	response.setHeader("Content-disposition", "attachment; filename=\"" + picName + "\"");
        String filePath = appConfig.getUchatRoot() + File.separator + channelId + File.separator + picName;
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
            HttpServletResponse response,
            HttpServletRequest request) throws IOException {
        log.info("REST API: " + request.getRequestURI() + ", pic=" + picName);
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setContentType("image/jpeg");
        String filePath = appConfig.getUchatRoot() + File.separator + channelId + File.separator + picName;
		OutputStream os = response.getOutputStream();
        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);
        IOUtils.copyLarge(inputStream, os);
	}
}
