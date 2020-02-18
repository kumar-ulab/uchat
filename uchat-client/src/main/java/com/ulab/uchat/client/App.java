package com.ulab.uchat.client;

import org.springframework.boot.Banner.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.ulab.util.SpringUtil;

@SpringBootApplication
public class App {
	public static String host = "52.81.17.213";
	public static int port = 9091;
	public static int httpPort = 7080;

	public static void main(String[] args) {
    	if (args.length > 0) {
    		host = args[0];
    	}
    	if (args.length > 1) {
        	port = Integer.parseInt(args[1]);
    	}
    	if (args.length > 2) {
    		httpPort = Integer.parseInt(args[1]);
    	}
        System.out.println("connecting to server(" + host + ":" + port + ")...");
    	SpringApplication app = new SpringApplication(App.class);
    	app.setBannerMode(Mode.OFF);
    	app.run(args);
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