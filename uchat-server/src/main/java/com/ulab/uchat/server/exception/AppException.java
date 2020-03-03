package com.ulab.uchat.server.exception;

import com.ulab.uchat.server.security.domain.ErrorStatus;

public class AppException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private ErrorStatus status;
	
	public ErrorStatus getStatus() {
		return status;
	}

	public void setStatus(ErrorStatus status) {
		this.status = status;
	}

	public AppException(String err) {
		this(ErrorStatus.Internal_Error, err);
	}
	
	public AppException(ErrorStatus status, String err) {
		super(err);
		this.status = status;
	}
	
	public AppException(Exception e) {
		this(ErrorStatus.Internal_Error, e.getMessage(), e);
	}

	public AppException(String err, Exception e) {
		this(ErrorStatus.Internal_Error, err, e);
	}

	public AppException(ErrorStatus code, String err, Exception e) {
		super(err, e);
		this.status = code;
	}
}
