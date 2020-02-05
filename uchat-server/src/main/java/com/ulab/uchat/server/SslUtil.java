package com.ulab.uchat.server;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

public class SslUtil {
	private static volatile SSLContext sslContext = null;

	public static SSLContext createSSLContext() {
		try {
			if(null == sslContext) {
				synchronized (SslUtil.class) {
					if(null == sslContext){
						KeyStore ks = KeyStore.getInstance("JKS");
						URL url = SslUtil.class.getClassLoader().getResource("uchat.jks");						
						InputStream ksInputStream = new FileInputStream(url.getFile());
						ks.load(ksInputStream, "ulab123".toCharArray());
						KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
						kmf.init(ks, "ulab123".toCharArray());
						sslContext = SSLContext.getInstance("TLS");
						sslContext.init(kmf.getKeyManagers(), null, null);
					}
				}
			}
	        return sslContext;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
