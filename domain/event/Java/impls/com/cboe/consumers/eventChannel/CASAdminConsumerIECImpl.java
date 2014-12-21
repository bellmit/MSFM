package com.cboe.consumers.eventChannel;

/**
 * AcceptCASAdminConsumer listener object listens on the CBOE event channel as an AcceptCASAdminConsumer.
 * There will only be a single best book listener per CAS.
 *
 * @author Connie Feng
 */

import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiAdmin.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.infrastructureServices.orbService.*;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.infrastructureServices.traderService.*;
import org.omg.CORBA.Object;
import org.omg.CORBA.Any;

import com.cboe.domain.util.*;

public class CASAdminConsumerIECImpl extends BObject implements com.cboe.interfaces.events.CASAdminConsumer
{
    private EventChannelAdapter internalEventChannel;

    /**
     * AcceptCASAdminConsumerIECImpl constructor comment.
     */
    public CASAdminConsumerIECImpl()
    {
        super();
        internalEventChannel = EventChannelAdapterFactory.find();
    }

    /**
     * This method is called by the CORBA event channel when a addCASUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Keith A. Korecky
     */
    public void addCASUser( String originator, String casPairName, String userId)
    {
        if (Log.isDebugOn())
        {
            StringBuilder eventReceived = new StringBuilder(70);
            eventReceived.append("event received -> addCASUser : ").append(casPairName).append("::").append(userId);
            Log.debug( this, eventReceived.toString() );
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.CASADMIN_ADD_USER, Integer.valueOf(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, userId);
        internalEventChannel.dispatch(event);
    }

    /**
     * This method is called by the CORBA event channel when a removeCASUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Keith A. Korecky
     */
    public void removeCASUser( String originator, String casPairName, String userId )
    {
        if (Log.isDebugOn())
        {
            StringBuilder eventReceived = new StringBuilder(70);
            eventReceived.append("event received -> removeCASUser : ").append(casPairName).append("::").append(userId);
            Log.debug( this, eventReceived.toString() );
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.CASADMIN_REMOVE_USER, Integer.valueOf(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, userId);
        internalEventChannel.dispatch(event);
    }

    /**
     * This method is called by the CORBA event channel when a addCASFirm event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Keith A. Korecky
     */
    public void addCASFirm( String originator, String casPairName, String userId, ExchangeFirmStruct firmKey )
    {
        if (Log.isDebugOn())
        {
            StringBuilder eventReceived = new StringBuilder(80);
            eventReceived.append("event received -> addCASFirm : ").append(casPairName)
                         .append("::").append(userId).append("::").append(firmKey);
            Log.debug( this, eventReceived.toString() );
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.CASADMIN_ADD_FIRM, Integer.valueOf(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, new UserIdFirmKeyContainer(userId, firmKey) );
        internalEventChannel.dispatch(event);
    }

    /**
     * This method is called by the CORBA event channel when a removeCASFirm event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Keith A. Korecky
     */
    public void removeCASFirm( String originator, String casPairName, String userId, ExchangeFirmStruct firmKey )
    {
        if (Log.isDebugOn())
        {
            StringBuilder eventReceived = new StringBuilder(80);
            eventReceived.append("event received -> removeCASFirm : ").append(casPairName)
                         .append("::").append(userId).append("::").append(firmKey);
            Log.debug( this, eventReceived.toString() );
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.CASADMIN_REMOVE_FIRM, Integer.valueOf(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, new UserIdFirmKeyContainer(userId, firmKey) );
        internalEventChannel.dispatch(event);
    }

    /**
     * This method is called by the CORBA event channel when a addCASClassForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Keith A. Korecky
     */
    public void addCASClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey)
    {
        //NOT IMPLEMENTED
    }

    /**
     * This method is called by the CORBA event channel when a removeCASClassForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Keith A. Korecky
     */
    public void removeCASClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey)
    {
        //NOT IMPLEMENTED
    }

    /**
     * This method is called by the CORBA event channel when a addCASClassForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Keith A. Korecky
     */
    public void addCASRFQClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey)
    {
        if (Log.isDebugOn())
        {
            StringBuilder eventReceived = new StringBuilder(100);
            eventReceived.append("event received -> addCASRFQClassForUser : ").append(casPairName)
                         .append("::").append(userId).append("::").append(sessionName).append("::").append(classKey);
            Log.debug( this, eventReceived.toString() );
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.CASADMIN_ADD_RFQ_CLASS_FOR_USER, Integer.valueOf(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, new UserSessionClassContainer( userId, sessionName, classKey ) );
        internalEventChannel.dispatch(event);
    }

    /**
     * This method is called by the CORBA event channel when a removeCASClassForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Keith A. Korecky
     */
    public void removeCASRFQClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey)
    {
        if (Log.isDebugOn())
        {
            StringBuilder eventReceived = new StringBuilder(100);
            eventReceived.append("event received -> removeCASRFQClassForUser : ").append(casPairName)
                         .append("::").append(userId).append("::").append(sessionName).append("::").append(classKey);
            Log.debug( this, eventReceived.toString() );
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.CASADMIN_REMOVE_RFQ_CLASS_FOR_USER, Integer.valueOf(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, new UserSessionClassContainer( userId, sessionName, classKey ) );
        internalEventChannel.dispatch(event);
    }

    /**
     * This method is called by the CORBA event channel when a addCASClassForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Keith A. Korecky
     */

     public void addCASCurrentMarketClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey)
    {
        if (Log.isDebugOn())
        {
            StringBuilder eventReceived = new StringBuilder(110);
            eventReceived.append("event received -> addCASCurrentMarketClassForUser : ").append(casPairName)
                         .append("::").append(userId).append("::").append(sessionName).append("::").append(classKey);
            Log.debug( this, eventReceived.toString() );
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.CASADMIN_ADD_CURRENTMARKET_CLASS_FOR_USER, Integer.valueOf(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, new UserSessionClassContainer( userId, sessionName, classKey ) );
        internalEventChannel.dispatch(event);
    }

    /**
     * This method is called by the CORBA event channel when a removeCASClassForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Keith A. Korecky
     */
    public void removeCASCurrentMarketClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey)
    {
        if (Log.isDebugOn())
        {
            StringBuilder eventReceived = new StringBuilder(110);
            eventReceived.append("event received -> removeCASCurrentMarketClassForUser : ").append(casPairName)
                         .append("::").append(userId).append("::").append(sessionName).append("::").append(classKey);
            Log.debug( this, eventReceived.toString() );
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.CASADMIN_REMOVE_CURRENTMARKET_CLASS_FOR_USER, Integer.valueOf(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, new UserSessionClassContainer( userId, sessionName, classKey ) );
        internalEventChannel.dispatch(event);
    }

    /**
     * This method is called by the CORBA event channel when a addCASClassForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Keith A. Korecky
     */
    public void addCASOpeningPriceClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey)
    {
        if (Log.isDebugOn())
        {
            StringBuilder eventReceived = new StringBuilder(110);
            eventReceived.append("event received -> addCASOpeningPriceClassForUser : ").append(casPairName)
                         .append("::").append(userId).append("::").append(sessionName).append("::").append(classKey);
            Log.debug( this, eventReceived.toString() );
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.CASADMIN_ADD_OPENINGPRICE_CLASS_FOR_USER, Integer.valueOf(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, new UserSessionClassContainer( userId, sessionName, classKey ) );
        internalEventChannel.dispatch(event);
    }

    /**
     * This method is called by the CORBA event channel when a removeCASClassForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Keith A. Korecky
     */
    public void removeCASOpeningPriceClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey)
    {
        if (Log.isDebugOn())
        {
            StringBuilder eventReceived = new StringBuilder(110);
            eventReceived.append("event received -> removeCASOpeningPriceClassForUser : ").append(casPairName)
                         .append("::").append(userId).append("::").append(sessionName).append("::").append(classKey);
            Log.debug( this, eventReceived.toString() );
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.CASADMIN_REMOVE_OPENINGPRICE_CLASS_FOR_USER, Integer.valueOf(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, new UserSessionClassContainer( userId, sessionName, classKey ) );
        internalEventChannel.dispatch(event);
    }

    /**
     * This method is called by the CORBA event channel when a addCASClassForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Keith A. Korecky
     */
    public void addCASTickerClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey)
    {
        if (Log.isDebugOn())
        {
            StringBuilder eventReceived = new StringBuilder(100);
            eventReceived.append("event received -> addCASTickerClassForUser : ").append(casPairName)
                         .append("::").append(userId).append("::").append(sessionName).append("::").append(classKey);
            Log.debug( this, eventReceived.toString() );
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.CASADMIN_ADD_TICKER_CLASS_FOR_USER, Integer.valueOf(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, new UserSessionClassContainer( userId, sessionName, classKey ) );
        internalEventChannel.dispatch(event);
    }

    /**
     * This method is called by the CORBA event channel when a removeCASClassForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Keith A. Korecky
     */
    public void removeCASTickerClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey)
    {
        if (Log.isDebugOn())
        {
            StringBuilder eventReceived = new StringBuilder(110);
            eventReceived.append("event received -> removeCASTickerClassForUser : ").append(casPairName)
                         .append("::").append(userId).append("::").append(sessionName).append("::").append(classKey);
            Log.debug( this, eventReceived.toString() );
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.CASADMIN_REMOVE_TICKER_CLASS_FOR_USER, Integer.valueOf(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, new UserSessionClassContainer( userId, sessionName, classKey ) );
        internalEventChannel.dispatch(event);
    }

    /**
     * This method is called by the CORBA event channel when a addCASClassForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Keith A. Korecky
     */
    public void addCASRecapClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey)
    {
        if (Log.isDebugOn())
        {
            StringBuilder eventReceived = new StringBuilder(100);
            eventReceived.append("event received -> addCASRecapClassForUser : ").append(casPairName)
                         .append("::").append(userId).append("::").append(sessionName).append("::").append(classKey);
            Log.debug( this, eventReceived.toString() );
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.CASADMIN_ADD_RECAP_CLASS_FOR_USER, Integer.valueOf(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, new UserSessionClassContainer( userId, sessionName, classKey ) );
        internalEventChannel.dispatch(event);
    }

    /**
     * This method is called by the CORBA event channel when a removeCASClassForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Keith A. Korecky
     */
    public void removeCASRecapClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey)
    {
        if (Log.isDebugOn())
        {
            StringBuilder eventReceived = new StringBuilder(100);
            eventReceived.append("event received -> removeCASRecapClassForUser : ").append(casPairName)
                         .append("::").append(userId).append("::").append(sessionName).append("::").append(classKey);
            Log.debug( this, eventReceived.toString() );
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.CASADMIN_REMOVE_RECAP_CLASS_FOR_USER, Integer.valueOf(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, new UserSessionClassContainer( userId, sessionName, classKey ) );
        internalEventChannel.dispatch(event);
    }

    /**
     * This method is called by the CORBA event channel when a addCASProductForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Keith A. Korecky
     */
    public void addCASBookDepthProductForUser( String originator, String casPairName, String userId, String sessionName, int productKey)
    {
        if (Log.isDebugOn())
        {
            StringBuilder eventReceived = new StringBuilder(80);
            eventReceived.append("event received -> addCASBookDepthProductForUser : ").append(casPairName)
                         .append("::").append(productKey);
            Log.debug( this, eventReceived.toString() );
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.CASADMIN_ADD_BOOKDEPTH_PRODUCT_FOR_USER, Integer.valueOf(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, new UserSessionProductContainer(userId, sessionName, productKey) );
        internalEventChannel.dispatch(event);
    }

    /**
     * This method is called by the CORBA event channel when a removeCASProductForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Keith A. Korecky
     */
    public void removeCASBookDepthProductForUser( String originator, String casPairName, String userId, String sessionName, int productKey)
    {
        if (Log.isDebugOn())
        {
            StringBuilder eventReceived = new StringBuilder(90);
            eventReceived.append("event received -> removeCASBookDepthProductForUser : ").append(casPairName)
                         .append("::").append(productKey);
            Log.debug( this, eventReceived.toString() );
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.CASADMIN_REMOVE_BOOKDEPTH_PRODUCT_FOR_USER, Integer.valueOf(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, new UserSessionProductContainer(userId, sessionName, productKey) );
        internalEventChannel.dispatch(event);
    }


    /**
     * This method is called by the CORBA event channel when a addCASLockLockedNotification event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     */
    public void addCASQuoteLockedNotification( String originator, String casPairName, String userId, String sessionName, int productKey)
    {
        if (Log.isDebugOn())
        {
            StringBuilder eventReceived = new StringBuilder(80);
            eventReceived.append("event received -> addCASQuoteLockedNotification : ").append(casPairName)
                         .append("::").append(productKey);
            Log.debug( this, eventReceived.toString() );
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.CASADMIN_ADD_QUOTE_LOCKED_NOTIFICATION, Integer.valueOf(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, new UserSessionProductContainer(userId, sessionName, productKey) );
        internalEventChannel.dispatch(event);
    }

    /**
     * This method is called by the CORBA event channel when a removeCASProductForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     */
    public void removeCASQuoteLockedNotification( String originator, String casPairName, String userId, String sessionName, int productKey)
    {
        if (Log.isDebugOn())
        {
            StringBuilder eventReceived = new StringBuilder(90);
            eventReceived.append("event received -> removeCASQuoteLockedNotification : ").append(casPairName)
                         .append("::").append(productKey);
            Log.debug( this, eventReceived.toString() );
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.CASADMIN_REMOVE_QUOTE_LOCKED_NOTIFICATION, Integer.valueOf(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, new UserSessionProductContainer(userId, sessionName, productKey) );
        internalEventChannel.dispatch(event);
    }


    /**
     * This method is called by the CORBA event channel when a addCASProductForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Keith A. Korecky
     */
    public void addCASProductForUser( String originator, String casPairName, String userId, String sessionName, int productKey)
    {
        // NOT IMPLEMENTED
    }

    /**
     * This method is called by the CORBA event channel when a removeCASProductForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Keith A. Korecky
     */
    public void removeCASProductForUser( String originator, String casPairName, String userId, String sessionName, int productKey)
    {
        // NOT IMPLEMENTED
    }
}
