package com.cboe.infrastructureServices.securityService;

public class InvalidIPException extends Exception {
	
	public String invalidIP;
	
	public InvalidIPException(String ip) {
		invalidIP = ip;
	}
}
