package com.sgk.joker.websoket.model;

public class ServerError {

	private String message;
	private String code;
	
	public ServerError() {
		super();
	}
	
	public ServerError(String message, String code) {
		super();
		this.message = message;
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
