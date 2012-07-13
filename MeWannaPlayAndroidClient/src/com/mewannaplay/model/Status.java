package com.mewannaplay.model;

public class Status {
	private String error;
	private int code;
	private String description;
	private String message;
	
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public final boolean isNotSuccess()
	{
		return code != 200 || error.equalsIgnoreCase("yes") || error.equalsIgnoreCase("true");
	}
}
