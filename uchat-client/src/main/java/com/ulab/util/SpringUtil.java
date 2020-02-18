package com.ulab.util;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringUtil {
	private SpringUtil() {		
	}
	
    private static AnnotationConfigApplicationContext applicationContext;
	public static <T> void initSpringIoc(Class<T> clazz) {
        applicationContext = new AnnotationConfigApplicationContext(clazz);
	}
	
	public static void closeSpringIoc() {
        if (applicationContext != null){
            applicationContext.close();
        }
	}
	
	public static <B> B getBean(Class<B> clazz) {
		return applicationContext.getBean(clazz);
	}
//    private static AnnotationConfigApplicationContext applicationContext;
//	public static <T> void initSpringIoc(Class<T> clazz) {
//        applicationContext = new AnnotationConfigApplicationContext(clazz);
//	}
//	
//	public static void closeSpringIoc() {
//        if (applicationContext != null){
//            applicationContext.close();
//        }
//	}
//	
//	public static <B> B getBean(Class<B> clazz) {
//		return applicationContext.getBean(clazz);
//	}
}
