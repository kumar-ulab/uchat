package com.ulab.util;

import java.io.IOException;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
	public static <T> String Object2Json(T t) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(t);
        return json;
	}

	public static <T> T json2Object(String json, Class<T> clazz) throws IOException {
		if (StringUtils.isEmpty(json)){
            return null;
        }
		T t;
    	if (clazz == String.class) {
    		t = (T) json;
    	} else {
            ObjectMapper mapper = new ObjectMapper();
            t = mapper.readValue(json, clazz);
    	}
    	return t;
	}
}
