package com.cboe.proxy.businessServicesClient;

import com.cboe.interfaces.businessServicesClient.*;
import com.cboe.proxy.businessServicesClient.*;
import java.util.*;

/**
 *  Implementation of the ReplyHandlerManager specific to OrderHandlingServiceClient
 *  
 *  @date January 12, 2009
 *  
 */
public 	class OrderHandlingServiceClientReplyHandlerManager
		implements ReplyHandlerManagerClient
{

    private	Stack replyHandlerPool;

    /**
     * OrderHandlingServiceClientReplyHandlerManager constructor comment.
     */
    public OrderHandlingServiceClientReplyHandlerManager()
    {
    	super();
    	replyHandlerPool = new Stack();
    }
    public	synchronized ReplyHandlerClient	findReplyHandler()
    {
		OrderHandlingServiceClientReplyHandler replyHandler;
			
		if ( replyHandlerPool.empty() )
		{
			replyHandler  =  new OrderHandlingServiceClientReplyHandler(this);
			replyHandler.initialize();
		}
		else
		{
			replyHandler = (OrderHandlingServiceClientReplyHandler) replyHandlerPool.pop();
			replyHandler.reset();
		}

		return replyHandler;
    }
    public	void returnReplyHandler( ReplyHandlerClient handler )
    {
    	replyHandlerPool.push( handler );
    }
}//EOF
