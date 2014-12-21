package com.cboe.infrastructureServices.securityService;

import org.omg.security.AuthenticationStatus;

public class AuthenticationException extends Exception {
	static final long serialVersionUID = 0;
	
	public static int ACESS_DENIED = 1;
	public static int NEW_PIN_REQUIRED = 2;
	public static int NEXT_CODE_REQUIRED = 3;
	
	public int errorCode;
	
	public AuthenticationException(AuthenticationStatus status) {
		errorCode = getErrorCode(status);
	}
	
	private int getErrorCode(AuthenticationStatus status) {
		if (status == AuthenticationStatus.SecAuthFailureNewPinRequired) {
			return NEW_PIN_REQUIRED;
		}
		else if (status == AuthenticationStatus.SecAuthFailureNextCodeRequired) {
			return NEXT_CODE_REQUIRED;
		}
		else {
			return ACESS_DENIED;
		}
	}
}
