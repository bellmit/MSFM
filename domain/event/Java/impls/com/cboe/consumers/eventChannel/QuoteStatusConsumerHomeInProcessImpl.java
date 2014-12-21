// $Workfile$ com.cboe.consumers.eventChannel.QuoteStatusConsumerHomeInProcessImpl.java
// $Revision$
/* $Log$
*   Initial Version                             Jeff Illian
*   Revision        Increment 6     12/1/00     desaik
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.

package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.quote.QuoteAcknowledgeStruct;
import com.cboe.interfaces.events.IECQuoteStatusConsumerV2Home;
import com.cboe.interfaces.events.QuoteStatusConsumerV2;
import com.cboe.util.ChannelKey;

    /**
     * <b> Description </b>
     * <p>
     *      The Quote Status Listener class.
     * </p>
     *
     * @author Jeff Illian
     * @author Keval Desai
     * @author Gijo Joseph 
     */
public class QuoteStatusConsumerHomeInProcessImpl extends ClientBOHome implements IECQuoteStatusConsumerV2Home {
    private QuoteStatusConsumerIECImpl quoteStatusConsumer;

    /**
     * OrderStatusConsumerHomeEventImpl constructor comment.
     */
    public QuoteStatusConsumerHomeInProcessImpl() {
        super();
    }

    public QuoteStatusConsumerV2 create() {
        return find();
    }
    /**
     * @author Jeff Illian
     * @return OrderStatusConsumer
     */
    public QuoteStatusConsumerV2 find()
    {
        return quoteStatusConsumer;
    }// end of find

    public void clientStart ()
        throws Exception
    {
        quoteStatusConsumer.create(String.valueOf(quoteStatusConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(quoteStatusConsumer);
    }

    public void clientInitialize()
    {
        quoteStatusConsumer = new QuoteStatusConsumerIECImpl();
    }

    /**
     * Adds a  Filter to the internal event channel. Constraints based on the
     * ChannelKey will be added as well. Do not make call to addConstraints when this method has
     * already being called.
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     * @author Keval Desai
     * @version 12/1/00
     */
    public void addFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    }

    /**
     * Removes the event channel Filter from the CBOE event channel.
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     * @author Keval Desai
     * @version 12/1/00
     */
    public void removeFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    }

    // Unused methods declared in home interface for server usage.
    public void resubscribeQuoteStatus(String userId)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {}
    public void publishUnackedQuoteStatusByClass(String userId, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {}
    public void ackQuoteStatus(QuoteAcknowledgeStruct quoteAcknowledge)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {}
    public void addConsumer(QuoteStatusConsumerV2 consumer, ChannelKey key) {}
    public void removeConsumer(QuoteStatusConsumerV2 consumer, ChannelKey key) {}
    public void removeConsumer(QuoteStatusConsumerV2 consumer) {}

    // Unused method
	public QuoteStatusConsumerV2 find(String userId) 
	{
		return find();
	}

    // Unused method
	public QuoteStatusConsumerV2 create(String userId) 
	{
		return find();
	}
    
}// EOF
