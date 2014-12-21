package com.cboe.infrastructureServices.securityService;

public class PasswordUpdateException extends Exception {

	public String message;

	public PasswordUpdateException(String message) {
		this.message = message;
	}
}

