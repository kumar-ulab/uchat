package com.ulab.uchat.server.helper;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.ulab.uchat.server.helper.CodeHelper.Code;

@Component
public class CodeHelper {
	public static class Code {
		public String userId;		
		public long ts;
	}
	
    private Map<String, int[]> dictionaries = new ConcurrentHashMap<>();
    private int[] generalDict = randomArray(0,25,26); 

    private String encrypt(int[] dictionary, long val) {
		String digitString = String.valueOf(val);
		return encrypt(dictionary, digitString);
    }
    
    private String encrypt(int[] dictionary, String digitString) {
		StringBuffer sbu = new StringBuffer();
		for (int i=0; i<digitString.length(); i++) {
			char c = digitString.charAt(i);
			String d = String.valueOf(c);
			int n = Integer.parseInt(d);
			int v = dictionary[n];
			c = (char)(65 + v);
			sbu.append(c);
		}
		return sbu.toString();
    }
    
    public String encryptCode(String userId, long ts) {
		refreshCode(userId);
		StringBuffer sbu = new StringBuffer();
		sbu.append(userId.charAt(0));
		sbu.append(encrypt(generalDict, userId.substring(1)));
		char lenChar = (char)(sbu.length() + 65);
		sbu.insert(0, lenChar);		
		int hash = userId.hashCode();

		int[] dictionary = dictionaries.get(userId);
		sbu.append(encrypt(dictionary, ts + hash * 47));
		return sbu.toString();
	}
	
	public void refreshCode(String userId) {
		int[] dictionary = randomArray(0,25,26);
		dictionaries.put(userId, dictionary);
	}
	
	public Code devryptCode(String code) {
		int userIdlen = code.charAt(0) - 65;
		int userEndIndex = userIdlen + 1;
		char userType = code.charAt(1);		
		String id = decryptDigit(generalDict, code.substring(2, userEndIndex));
		if (id == null) {
			return null;
		}
		String userId = userType + id;

		int[] dictionary = dictionaries.get(userId);
		if (dictionary == null) {
			return null;
		}
		String tsStr = decryptDigit(dictionary, code.substring(userEndIndex));
		Long ts = 0L;
		try {
			Long v = Long.parseLong(tsStr);
			ts = v - userId.hashCode() * 47;
		} catch (Exception e) {
		}
		Code c = new Code();
		c.userId = userId;
		c.ts = ts;
		return c;
	}
	
	private String decryptDigit(int[] dictionary, String code) {
		StringBuffer sbu = new StringBuffer();
		for (int i=0; i<code.length(); i++) {
			char d = code.charAt(i);
			int v = d - 65;
			int c;
			for (c=0; c<26; c++) {
				if (dictionary[c]==v && c<10) {
					break;
				}
			}
			if (c > 9) {
				return null;
			}
			sbu.append(String.valueOf(c));
		}
		return sbu.toString();
	}
	
	private boolean isCodeValid(Code c) {
		if (c.userId != null && c.ts > 0) {
			return System.currentTimeMillis() - c.ts < 24 * 60 * 60 * 1000;
		}
		return false;
	}
	
	public boolean refreshCodeIfValid(Code c) {
		if (isCodeValid(c)) {
			refreshCode(c.userId);
			return true;
		}
		return false;
	}
	
	public static int[] randomArray(int min,int max,int n){  
	    int len = max-min+1;  
	      
	    if(max < min || n > len){  
	        return null;  
	    }  
	    int[] source = new int[len];  
	       for (int i = min; i < min+len; i++){  
	        source[i-min] = i;  
	       }  
	         
	       int[] result = new int[n];  
	       Random rd = new Random();
	       int index = 0;  
	       for (int i = 0; i < result.length; i++) {  
	           index = Math.abs(rd.nextInt() % len--);  
	           result[i] = source[index];  
	           source[index] = source[len];  
	       }  
	       return result;
	}

	public static void main(String[] args) {
		long ts = System.currentTimeMillis();
		System.out.println("ts=" + ts);
		CodeHelper appConfig = new CodeHelper();
		String code = appConfig.encryptCode("D12345", ts);
		System.out.println("code=" + code);
		Code c = appConfig.devryptCode(code);
		System.out.println("userId=" + c.userId + ", ts=" + c.ts);
		System.out.println(appConfig.isCodeValid(c));
		appConfig.refreshCode("D12345");
		c = appConfig.devryptCode(code);
		System.out.println("userId=" + c.userId + ", ts=" + c.ts);
		System.out.println(appConfig.isCodeValid(c));
	}
}
