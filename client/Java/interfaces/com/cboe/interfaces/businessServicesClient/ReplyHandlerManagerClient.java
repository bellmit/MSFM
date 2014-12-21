package com.cboe.interfaces.businessServicesClient;

import java.util.*;


/**
 *  	An interface for creating and managing a pool of  various reply handlers for asynchronous messaging
 *	@author	David De La Vega
 *	@date	December 18, 2008
 **/
 
public interface ReplyHandlerManagerClient 
{

/**
 *	Locates the reply handler for the type specified
 *   If none available, creates a new one
 **/	

public	ReplyHandlerClient	findReplyHandler( );
/**
 * Returns the reply handler back to the pool
 **/

public	void	returnReplyHandler(  ReplyHandlerClient handler );
}
