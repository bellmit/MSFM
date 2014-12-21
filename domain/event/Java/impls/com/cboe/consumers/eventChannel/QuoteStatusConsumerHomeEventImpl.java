// $Workfile$ com.cboe.consumers.eventChannel.QuoteStatusConsumerHomeEventImpl.java
// $Revision$
/* $Log$
*   Initial Version                             Jeff Illian
*   Revision        Increment 6     12/1/00     desaik
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.

package com.cboe.consumers.eventChannel;

import com.cboe.idl.quote.QuoteAcknowledgeStruct;
import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.domain.util.ExchangeFirmStructContainer;

import com.cboe.infrastructureServices.orbService.*;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.infrastructureServices.traderService.*;
import com.cboe.exceptions.*;

    /**
     * <b> Description </b>
     * <p>
     *      The Quote Status Listener class.
     * </p>
     *
     * @author Jeff Illian
     * @author Keval Desai
     */
public class QuoteStatusConsumerHomeEventImpl extends ClientBOHome implements IECQuoteStatusConsumerHome {
    protected QuoteStatusConsumer quoteStatusConsumer;
    protected QuoteStatusEventConsumerImpl quoteStatusEvent;
    protected EventService eventService;
    protected EventChannelFilterHelper eventChannelFilterHelper;
    protected final String CHANNEL_NAME = "QuoteStatus";

    /**
     * OrderStatusConsumerHomeEventImpl constructor comment.
     */
    public QuoteStatusConsumerHomeEventImpl() {
        super();
    }

    public QuoteStatusConsumer create() {
        return find();
    }
    /**
     * @author Jeff Illian
     * @return OrderStatusConsumer
     */
    public QuoteStatusConsumer find()
    {
        return quoteStatusConsumer;
    }// end of find

    protected void createConsumer()
    {
        ((QuoteStatusConsumerIECImpl)quoteStatusConsumer).create(String.valueOf(quoteStatusConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer((QuoteStatusConsumerIECImpl)quoteStatusConsumer);
    }

    public void clientStart ()
        throws Exception
    {
        createConsumer();
        if (eventService == null){
            eventService = eventChannelFilterHelper.connectEventService();
        }
        String interfaceRepId = com.cboe.idl.events.QuoteStatusEventConsumerHelper.id();
        // connect to the event channel without filter and add the constraint filter later on
        // using addConstraint - Connie Feng
        eventChannelFilterHelper.connectConsumer( CHANNEL_NAME, interfaceRepId, quoteStatusEvent );
    }

    protected void intializeConsumer()
    {
        quoteStatusConsumer = new QuoteStatusConsumerIECImpl();
    }

    public void clientInitialize()
        throws Exception
    {
        intializeConsumer();
        eventChannelFilterHelper = new EventChannelFilterHelper();
        quoteStatusEvent = new QuoteStatusEventConsumerImpl(quoteStatusConsumer);
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
        addConstraint(channelKey);
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
        removeConstraint(channelKey);
    }

    /**
     * Adds constraint based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    protected void addConstraint(ChannelKey channelKey)
        throws SystemException
    {
        if ( find() != null ) {
            String constraintString = getConstraintString(channelKey);
            if (Log.isDebugOn())
            {
                Log.debug( "constraintString::" + constraintString );
            }

            eventChannelFilterHelper.addEventFilter( quoteStatusEvent, channelKey,
                    eventChannelFilterHelper.getChannelName(CHANNEL_NAME), constraintString);
        }
    }// end of addConstraint

    /**
     * Removes constraint based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    protected void removeConstraint(ChannelKey channelKey)
        throws SystemException
    {
        if ( find() != null ) {
            String constraintString = getConstraintString(channelKey);
            eventChannelFilterHelper.removeEventFilter( channelKey, constraintString);
        }
    }// end of addConstraint

        /**
         * Returns the constraint string based on the channel key
         *
         * @param channelKey the event channel key
         *
         * @author Connie Feng
         */
        protected String getConstraintString(ChannelKey channelKey)
        {
            String parm = getParmName(channelKey);

            if(parm.equals(EventChannelFilterHelper.ALL_EVENTS_CONSTRAINT) ||
               parm.equals(EventChannelFilterHelper.NO_EVENTS_CONSTRAINT))
            {
                return parm;
            }
            
            StringBuilder buf = new StringBuilder(parm.length()+2);
            buf.append("$.").append(parm);
            return buf.toString();
        }

    /**
     * Returns the constraint parameter string based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    protected String getParmName(ChannelKey channelKey)
    {
        ExchangeFirmStructContainer firmKeyContainer;
        StringBuilder name = new StringBuilder(120);
        switch (channelKey.channelType)
        {
            case ChannelType.QUOTE_FILL_REPORT :
                name.append("acceptQuoteFillReport.quoteInfo.userId=='").append(channelKey.key).append("'");
                return name.toString();
            case ChannelType.QUOTE_FILL_REPORT_BY_FIRM :
                firmKeyContainer = (ExchangeFirmStructContainer)channelKey.key;
                name.append("acceptQuoteFillReport.quoteInfo.firm.exchange=='").append(firmKeyContainer.getExchange())
                    .append("' and $.acceptQuoteFillReport.quoteInfo.firm.firmNumber=='").append(firmKeyContainer.getFirmNumber()).append("'");
                return name.toString();
            case ChannelType.QUOTE_BUST_REPORT :
                name.append("acceptQuoteBustReport.quoteInfo.userId=='").append(channelKey.key).append("'");
                return name.toString();
            case ChannelType.QUOTE_BUST_REPORT_BY_FIRM :
                firmKeyContainer = (com.cboe.domain.util.ExchangeFirmStructContainer)channelKey.key;
                name.append("acceptQuoteBustReport.quoteInfo.firm.exchange=='").append(firmKeyContainer.getExchange())
                    .append("' and $.acceptQuoteBustReport.quoteInfo.firm.firmNumber=='").append(firmKeyContainer.getFirmNumber()).append("'");
                return name.toString();
            case ChannelType.QUOTE_DELETE_REPORT :
                name.append("acceptQuoteDeleteReport.userId=='").append(channelKey.key).append("'");
                return name.toString();
            case ChannelType.QUOTES_DELETE_REPORTV2 :
                name.append("acceptQuoteDeleteReportV2.userId=='").append(channelKey.key).append("'");
                return name.toString();
            case ChannelType.QUOTE_STATUS_UPDATE :
                name.append("acceptQuoteStatusUpdate.quote.userId=='").append(channelKey.key).append("'");
                return name.toString();
            default :
                Log.alarm(this, "Invalid Channel Type for filtering for context: " + channelKey.channelType);
                return EventChannelFilterHelper.NO_EVENTS_CONSTRAINT;
        }
    } // end of getParmName

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
    public void addConsumer(QuoteStatusConsumer consumer, ChannelKey key) {}
    public void removeConsumer(QuoteStatusConsumer consumer, ChannelKey key) {}
    public void removeConsumer(QuoteStatusConsumer consumer) {}

    // Unused method
	public QuoteStatusConsumer find(String userId) 
	{
		return find();
	}

    // Unused method
	public QuoteStatusConsumer create(String userId) 
	{
		return find();
	}
    
}// EOF

