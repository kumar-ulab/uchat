package com.ulab.uchat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ulab.uchat.client.service.ChatService;

@Component
public class UchatClient {
//    private String host;
//    private int port;
    
    @Autowired ChatService chatService;
        
    public UchatClient() {
    }

//    public void init(String host, int port) {
//        this.host = host;
//        this.port = port;
//    }
    
    public void run(String host, int port) throws Exception{
        try {
        	chatService.connectServer(host, port);
        	goChat();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	chatService.close();
        }
    }
    
//    public void run() throws Exception{
//        try {
//        	chatService.connectServer(host, port);
//        	goChat();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//        	chatService.close();
//        }
//    }
    
    public void goChat() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while(true){
        	String text = in.readLine();
//        	chatService.sendData(filePath);
        	chatService.sendChatMsg(text);
        }
    }
}
