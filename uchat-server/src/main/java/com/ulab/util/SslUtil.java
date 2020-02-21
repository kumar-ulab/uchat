package com.ulab.util;

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
						KeyStore ks = KeyStore.getInstance("PKCS12");
						InputStream ksInputStream = ClassLoader.getSystemResourceAsStream("ulab.jks");
						ks.load(ksInputStream, "ulab123".toCharArray());
						String alg = KeyManagerFactory.getDefaultAlgorithm();
						KeyManagerFactory kmf = KeyManagerFactory.getInstance(alg);

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
