package com.cboe.interfaces.businessServicesClient;

/**
 * 	Generic Reply handler class for use with asynchronous messaging
 *	@author	David De La Vega
 *	@date 	December 18, 2008
 */
public 	interface ReplyHandlerClient
{

public  void	 initialize();
public  int  	 getNumberOfExceptions();
public	int	 getNumberOfResponses();
public	int	 getNumberOfPendingResponses();
public  int  	 getNumberOfRequests();
public	void 	 setNumberOfRequests( int requests );

}
