package com.ulab.uchat.client;

public class AppException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public AppException(String err) {
		super(err);
	}
	
	public AppException(Exception e) {
		super(e);
	}

	public AppException(String err, Exception e) {
		super(err, e);
	}
}
