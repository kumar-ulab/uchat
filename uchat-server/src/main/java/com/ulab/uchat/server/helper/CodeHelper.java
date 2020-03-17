package com.ulab.uchat.server.helper;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class CodeHelper {
	
    private Map<String, int[]> dictionaries = new ConcurrentHashMap<>();

    public String createCodeFromTs(String userId, long ts) {
		int hash = userId.hashCode();
		refreshCode(userId);
		int[] dictionary = dictionaries.get(userId);
		String digitString = String.valueOf(ts + hash);
		StringBuffer sbu = new StringBuffer();
		for (int i=0; i<digitString.length(); i++) {
			char c = digitString.charAt(i);
			String d = String.valueOf(c);
			int n = Integer.parseInt(d);
			int v = dictionary[n];
			char t = (char)(65 + v);
			sbu.append(t);
		}
		return sbu.toString();
	}
	
	public void refreshCode(String userId) {
		int[] dictionary = randomArray(0,25,26);
		dictionaries.put(userId, dictionary);
	}
	
	public long getTsFromCode(String userId, String code) {
		int[] dictionary = dictionaries.get(userId);
		if (dictionary == null) {
			return 0;
		}
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
				return 0;
			}
			sbu.append(String.valueOf(c));
		}
		String digitString = sbu.toString();
		Long v = Long.parseLong(digitString);
		Long ts = v - userId.hashCode() * 47;
		return ts;
	}
	
	public boolean isCodeValid(String userId, String code) {
		long ts = getTsFromCode(userId, code);
		return System.currentTimeMillis() - ts < 24 * 60 * 60 * 1000;
	}
	
	public boolean refreshCodeIfValid(String userId, String code) {
		if (isCodeValid(userId, code)) {
			refreshCode(userId);
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
		String code = appConfig.createCodeFromTs("D12345", ts);
		System.out.println("code=" + code);
		long ts1 = appConfig.getTsFromCode("D12345", code);
		System.out.println(appConfig.isCodeValid("D12345", code));
		appConfig.refreshCode("D12345");
		System.out.println(appConfig.isCodeValid("D12345", code));
		System.out.println("ts=" + ts1);
	}
}
