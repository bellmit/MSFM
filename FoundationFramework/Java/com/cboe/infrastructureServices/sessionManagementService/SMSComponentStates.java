package com.cboe.infrastructureServices.sessionManagementService;

public interface SMSComponentStates {
	
	//No information is available
	public int UNKNOWN = 100;
	
	//process is gone.
	public int FAILED = 101;
	
	//process is up, but not handling any request ( such as in slave mode )
	public int INACTIVE = 102;
	
	//process is up and running ( such as in master mode )
	public int ACTIVE = 103;
}
