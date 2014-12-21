package com.cboe.infrastructureServices.securityService;

public class WeakPasswordException extends Exception {

	public String message;

	public WeakPasswordException(String message) {
		this.message = message;
	}
}

