package com.cboe.publishers.eventChannel;

import com.cboe.interfaces.events.QuoteStatusAdminConsumer;
import com.cboe.idl.internalEvents.QuoteStatusAdminEventConsumer;
import com.cboe.idl.quote.QuoteAcknowledgeStruct;
import com.cboe.idl.quote.QuoteAcknowledgeStructV3;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * @author Emily Huang
 */ 
public class QuoteStatusAdminConsumerPublisherImpl extends BObject implements QuoteStatusAdminConsumer
{
    QuoteStatusAdminEventConsumer eventChannel;
  
    protected QuoteStatusAdminConsumerPublisherImpl(QuoteStatusAdminEventConsumer stub)
    {
        super();
        eventChannel = stub;
    }
  
    public void subscribeQuoteStatus(String userName)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Subscribing QuoteStatus for userName = " + userName);
        }
        if (eventChannel != null)
        {
            eventChannel.subscribeQuoteStatus(userName);
        }
    }
  
    public void ackQuoteStatus(int[] groups, QuoteAcknowledgeStruct quoteAcknowledge)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing QuoteStatus Ack, transactionSequenceNumber= " + quoteAcknowledge.transactionSequenceNumber);
        }
        if (eventChannel != null)
        {
            eventChannel.ackQuoteStatus(groups, quoteAcknowledge);
        }
    }

    public void ackQuoteStatusV3(RoutingParameterStruct routingParameterStruct, QuoteAcknowledgeStructV3 quoteAcknowledge)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing ackQuoteStatusV3 Ack, transactionSequenceNumber= " + quoteAcknowledge.transactionSequenceNumber);
        }
        if (eventChannel != null)
        {
            eventChannel.ackQuoteStatusV3(routingParameterStruct, quoteAcknowledge);
        }
    }

    public void publishUnackedQuoteStatus(RoutingParameterStruct routingParameterStruct, String userId)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Requesting publishing of unacked QuoteStatus, userId = " + userId);
        }
        if (eventChannel != null)
        {
            eventChannel.publishUnackedQuoteStatus(routingParameterStruct, userId);
        }
    }
}

