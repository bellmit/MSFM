package com.cboe.proxy.businessServicesClient;

import com.cboe.interfaces.businessServicesClient.*;
import com.cboe.proxy.businessServicesClient.*;
import java.util.*;


/**
 *  Implementation of the ReplyHandlerManager specific to MarketMakerQuoteServiceClient
 *  
 *  @date January 07, 2009
 *  
 */
public class MarketMakerQuoteServiceClientReplyHandlerManager implements ReplyHandlerManagerClient
{

    private	Stack replyHandlerPool;

    /**
     * MarketMakerQuoteServiceReplyHandlerManager constructor comment.
     */
    public MarketMakerQuoteServiceClientReplyHandlerManager()
    {
        super();
        replyHandlerPool = new Stack();
    }
    public	synchronized ReplyHandlerClient	findReplyHandler()
    {
		MarketMakerQuoteServiceClientReplyHandler replyHandler;
			
		if ( replyHandlerPool.empty() )
		{
			replyHandler  =  new MarketMakerQuoteServiceClientReplyHandler(this);
			replyHandler.initialize();
		}
		else
		{
			replyHandler = (MarketMakerQuoteServiceClientReplyHandler) replyHandlerPool.pop();
			replyHandler.reset();
		}

		return replyHandler;
    }
    public	void returnReplyHandler( ReplyHandlerClient handler )
    {
		replyHandlerPool.push( handler );
    }
 
}//EOF
