// -----------------------------------------------------------------------------------
// Source file: ServiceNotAvailableException.java
//
// PACKAGE:  com.cboe.infra.presentation.network
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.infra.presentation.network;

/**
 * This exception is thrown when an attempt to obtain information from a remote service
 * (be it the extent map service, Talarian, or command console) fails.
 * This exception does not differentiate between the service being entirely unavailable
 * and the case in which it does not provide information for a single request.
 */
public class ServiceNotAvailableException extends Exception 
{
	public ServiceNotAvailableException(String msg) 
	{
		super(msg);
	}
	
	public ServiceNotAvailableException()
	{
		super();	
	}
}