// $Workfile$ com.cboe.consumers.eventChannel.QuoteStatusConsumerHomeCallbackImpl.java
// $Revision$
/* $Log$
*   Initial Version                             Jeff Illian
*   Revision        Increment 6     12/1/00     desaik
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.

package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.domain.util.ExchangeFirmStructContainer;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.quote.QuoteAcknowledgeStruct;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.POAHelper;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.businessServices.QuoteStatusSubscriptionService;
import com.cboe.interfaces.businessServices.QuoteStatusSubscriptionServiceHome;
import com.cboe.interfaces.events.IECQuoteStatusConsumerV2Home;
import com.cboe.interfaces.events.QuoteStatusConsumerV2;
import com.cboe.interfaces.domain.TradingFirmGroupWrapper;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

import java.util.HashMap;
import java.util.List;

/**
 * <b> Description </b>
 * <p>
 *      The Quote Status Listener class.
 * </p>
 *
 * @author Jeff Illian
 * @author Keval Desai
 * @author Gijo Joseph
 *
 * @version 4/19/2006
 */
public class QuoteStatusConsumerHomeCallbackImpl extends ClientBOHome implements IECQuoteStatusConsumerV2Home
{
private QuoteStatusSubscriptionService              quoteStatusService;
private QuoteStatusSubscriptionService              quoteSubscriptionObject;

private String casSource;
private HashMap consumers = new HashMap(11);
private HashMap callbacks = new HashMap(11);
public static final String GENERAL_USER = "GENERAL";

/**
 * QuoteStatusListenerFactory constructor comment.
 */
public QuoteStatusConsumerHomeCallbackImpl() {
    super();
}

/**
 * Return the QuoteStatusService (If first time, find home and get object).
 * @author Jeff Illian
 * @return QuoteStatusService
 */
private QuoteStatusSubscriptionService getQuoteStatusService()
{
    if (quoteStatusService == null) {
        //This code was copied from ServicesHelper to remove an unwanted dependency.
        try {
            QuoteStatusSubscriptionServiceHome home = (QuoteStatusSubscriptionServiceHome)HomeFactory.getInstance().findHome(QuoteStatusSubscriptionServiceHome.HOME_NAME);
            quoteStatusService = (QuoteStatusSubscriptionService)home.find();
        }
        catch (CBOELoggableException e) {
            Log.exception(this, e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find QuoteStatusSubscriptionServiceHome (UOQ)");
        }
    }

    return quoteStatusService;
}

/**
 * Get the object for making and revoking subscriptions.
 * @return An object which adds and removes filters for a channel in this process.
 */
private QuoteStatusSubscriptionService getQuoteSubscriptionObject()
{
    if (null == quoteSubscriptionObject)
    {
        quoteSubscriptionObject = new QuoteStatusSubscriptionServiceClientChannelImpl();
    }
    return quoteSubscriptionObject;
}

/**
 * Return the QuoteStatus Listener (If first time, create it and bind it to the orb).
 * @author Jeff Illian
 * @return QuoteStatusConsumerV2
 */
public QuoteStatusConsumerV2 create()
{
    return find();
}

/**
 * Return the QuoteStatusConsumerV2
 * @author Jeff Illian
 * @return QuoteStatusConsumerV2
 */
public QuoteStatusConsumerV2 find()
{
    return find(GENERAL_USER);
}// end of find

/**
 * Return the QuoteStatusConsumerV2
 * @param userId
 * @return QuoteStatusConsumerV2 corresponding to the specified user.
 * @author Gijo Joseph
 * @version 4/19/2006
 */
public QuoteStatusConsumerV2 create(String userId)
{
    return find(userId);
}

/**
 * Return the QuoteStatusConsumerV2
 * @param userId
 * @return QuoteStatusConsumerV2 corresponding to the specified user.
 * @author Gijo Joseph
 * @version 4/19/2006
 */
public QuoteStatusConsumerV2 find(String userId)
{
       synchronized (callbacks)
    {
        QuoteStatusConsumerV2 consumer = (QuoteStatusConsumerV2)consumers.get(userId);
        if (consumer == null)
        {
            FoundationFramework ff = FoundationFramework.getInstance();

            // Create the object that publishes on the IEC
            QuoteStatusConsumerIECImpl quoteStatusConsumer = new QuoteStatusConsumerIECImpl();
            quoteStatusConsumer.create(String.valueOf(quoteStatusConsumer.hashCode()));
            addToContainer(quoteStatusConsumer);
            QuoteStatusEventConsumerInterceptor quoteStatusEventConsumerInterceptor =
                                new QuoteStatusEventConsumerInterceptor(quoteStatusConsumer);
            if(getInstrumentationEnablementProperty())
            {
                quoteStatusEventConsumerInterceptor.startInstrumentation(getInstrumentationProperty());
            }
            consumer = quoteStatusEventConsumerInterceptor;
            consumers.put(userId, consumer);
        }
        return consumer;
    }
}



public void clientStart()
    throws Exception
{
}

public void clientInitialize()
    throws Exception
{
    casSource = FoundationFramework.getInstance().getConfigService().getProperty("Process.name()");
}

/**
 * @param userId
 *@return QuoteStatusConsumerV2 corresponding to the specified user.
 * @author Gijo Joseph
 * @version 4/19/2006
 */
protected com.cboe.idl.consumers.QuoteStatusConsumerV2 getQuoteStatusCallback(String userId) throws SystemException
{
    synchronized (callbacks)
    {
        com.cboe.idl.consumers.QuoteStatusConsumerV2 quoteStatusCallback = (com.cboe.idl.consumers.QuoteStatusConsumerV2)callbacks.get(userId);
        if (quoteStatusCallback == null)
        {
            try
            {
                // bind to orb so that is ready for callbacks
                org.omg.CORBA.Object obj =
                    POAHelper.connect(new com.cboe.idl.consumers.POA_QuoteStatusConsumerV2_tie(find(userId)), this);
                quoteStatusCallback = com.cboe.idl.consumers.QuoteStatusConsumerV2Helper.narrow(obj);
                callbacks.put(userId, quoteStatusCallback);
            }
            catch (Exception e)
            {
                Log.exception(this, "Exception while creating orderstatus callback for user:" + userId, e);
                throw new SystemException();
            }
        }
        return quoteStatusCallback;
    }
}

/**
 * Adds a  Filter to the internal event channel and QuoteStatusService
 *
 * @param channelKey the event channel key
 *
 * @author Connie Feng
 * @author Keval Desai
 * @author Gijo Joseph
 * @version 4/19/2006
 */
public void addFilter ( ChannelKey channelKey )
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException
{
    // Register tie object with the Order Status Service if by memberKey
    // else register by firmKey
    if (Log.isDebugOn())
    {
        Log.debug(this, "->addFilter");
    }
    if (    channelKey.channelType == ChannelType.QUOTE_FILL_REPORT_BY_FIRM
        ||  channelKey.channelType == ChannelType.QUOTE_BUST_REPORT_BY_FIRM)
    {
        ExchangeFirmStructContainer id = (ExchangeFirmStructContainer) channelKey.key;
        getQuoteSubscriptionObject().subscribeQuoteStatusForFirmV2(new ExchangeFirmStruct(id.getExchange(), id.getFirmNumber()),
                getQuoteStatusCallback(id.getExchange()+id.getFirmNumber()), casSource, (short)0);
    }
    else if (channelKey.channelType == ChannelType.QUOTE_FILL_REPORT_BY_TRADING_FIRM
        ||  channelKey.channelType == ChannelType.QUOTE_BUST_REPORT_BY_TRADING_FIRM)
    {
        TradingFirmGroupWrapper groupContainer = (TradingFirmGroupWrapper) channelKey.key;
        String id = groupContainer.getTradingFirmId();
        List<String> users = groupContainer.getUsers();
        for(String user: users)
        {
            getQuoteSubscriptionObject().subscribeQuoteStatusForTradingFirmUser(user, id, getQuoteStatusCallback(id), casSource, (short)0);
        }
    }
    else // otherwise assume the channel key is a userId
    {
        String id = (String)channelKey.key;
        getQuoteSubscriptionObject().subscribeQuoteStatusV2(id, getQuoteStatusCallback(id), casSource, (short)0);
    }
}

/**
 * Removes the event channel Filter from the CBOE event channel and QuoteStatusService
 *
 * @param channelKey the event channel key
 *
 * @author Connie Feng
 * @author Keval Desai
 * @author Gijo Joseph
 * @version 4/19/2006
 */
public void removeFilter ( ChannelKey channelKey )
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException
{
    // Register tie object with the Order Status Service if by memberKey
    // else register by firmKey
    if (Log.isDebugOn())
    {
        Log.debug(this, "->removeFilter");
    }
    if (    channelKey.channelType == ChannelType.QUOTE_FILL_REPORT_BY_FIRM
        ||  channelKey.channelType == ChannelType.QUOTE_BUST_REPORT_BY_FIRM)
    {
        ExchangeFirmStructContainer id = (ExchangeFirmStructContainer) channelKey.key;;
        getQuoteSubscriptionObject().unsubscribeQuoteStatusForFirmV2(new ExchangeFirmStruct(id.getExchange(), id.getFirmNumber()),
                getQuoteStatusCallback(id.getExchange()+id.getFirmNumber()), casSource);
    }
    else if (channelKey.channelType == ChannelType.QUOTE_FILL_REPORT_BY_TRADING_FIRM
        ||  channelKey.channelType == ChannelType.QUOTE_BUST_REPORT_BY_TRADING_FIRM)
    {

        TradingFirmGroupWrapper groupContainer = (TradingFirmGroupWrapper) channelKey.key;
        String id = groupContainer.getTradingFirmId();
        List<String> users = groupContainer.getUsers();
        for(String user: users)
        {
            getQuoteSubscriptionObject().unsubscribeQuoteStatusForTradingFirmUser(user, id, getQuoteStatusCallback(id), casSource);
        }
    }
    else
    {
        String id = (String)channelKey.key;
        getQuoteSubscriptionObject().unsubscribeQuoteStatusV2(id, getQuoteStatusCallback(id), casSource);
    }
    // I think it is probably better not to remove the callback object for the user from the map.
    // This eliminates the need to recreate them for each user re-login. Moreover, with unsubscribe,
    // this callback will be removed from the server side anyway. --Gijo.
}

/**
* Have QSSS/QSS send any unAcknowledged events.
*
* @param userId of logged in user
*/
public void resubscribeQuoteStatus(String userId)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException
{
    getQuoteStatusService().publishQuoteStatus(userId);
}

public void publishUnackedQuoteStatusByClass(String userId, int classKey)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException
{
    getQuoteStatusService().publishQuoteStatusByClass(userId, classKey);
}

public void ackQuoteStatus(QuoteAcknowledgeStruct quoteAcknowledge)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException
{
    getQuoteStatusService().ackQuoteStatus( quoteAcknowledge );
}

// Unused methods declared in home interface for server usage.
public void addConsumer(QuoteStatusConsumerV2 consumer, ChannelKey key) {}
public void removeConsumer(QuoteStatusConsumerV2 consumer, ChannelKey key) {}
public void removeConsumer(QuoteStatusConsumerV2 consumer) {}

}// EOF
