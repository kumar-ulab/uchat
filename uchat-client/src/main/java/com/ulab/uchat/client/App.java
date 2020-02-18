package com.ulab.uchat.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.ulab.util.SpringUtil;

@SpringBootApplication
public class App {
	public static String host = "127.0.0.1";
	public static int port = 9090;

	public static void main(String[] args) {
    	if (args.length > 0) {
    		host = args[0];
    	}
    	if (args.length > 1) {
        	port = Integer.parseInt(args[1]);
    	}
        SpringApplication.run(App.class, args);
//    	PictureChatClient pictureChatClient = SpringUtil.getBean(PictureChatClient.class);
//    	pictureChatClient.init(host, port);
//    	try {
//            new PictureChatClient().run(host, port);
//			pictureChatClient.run();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
    }
}
//public class App {
//    public static void main( String[] args ) throws Exception {
//    	SpringUtil.initSpringIoc(App.class);
//        String host = "52.81.17.213";
//    	int port = 9090;
//    	if (args.length > 0) {
//    		host = args[0];
//    	}
//    	if (args.length > 1) {
//        	port = Integer.parseInt(args[1]);
//    	}
//    	PictureChatClient pictureChatClient = SpringUtil.getBean(PictureChatClient.class);
//    	pictureChatClient.init(host, port);
//    	pictureChatClient.run();
//    }
//}