package com.cboe.publishers.eventChannel;

/**
 * AcceptCASAdminConsumer listener object listens on the CBOE event channel as an AcceptCASAdminConsumer.
 * There will only be a single best book listener per CAS.
 *
 * @author Connie Feng
 */

import com.cboe.idl.events.CASAdminEventConsumer;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.interfaces.events.CASAdminConsumer;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;

public class CASAdminConsumerPublisherImpl extends BObject implements CASAdminConsumer
{
    private CASAdminEventConsumer delegate;

    /**
     * AcceptCASAdminConsumerPublisherImpl constructor comment.
     */
    public CASAdminConsumerPublisherImpl(CASAdminEventConsumer delegate)
    {
        super();
        this.delegate = delegate;
    }

    /**
     * This method is called by the CORBA event channel when a addCASUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Jeff Illian
     */
    public void addCASUser( String originator, String casPairName, String userId)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Publishing addCASUser " + originator + ", " + casPairName + ", " + userId);
        }
        if (delegate != null)
        {
            delegate.addCASUser(originator, casPairName, userId);
        }
    }

    /**
     * This method is called by the CORBA event channel when a removeCASUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Jeff Illian
     */
    public void removeCASUser( String originator, String casPairName, String userId )
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Publishing removeCASUser " + originator + ", " + casPairName + ", " + userId);
        }
        if (delegate != null)
        {
            delegate.removeCASUser(originator, casPairName, userId);
        }
    }

    /**
     * This method is called by the CORBA event channel when a addCASFirm event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Jeff Illian
     */
    public void addCASFirm( String originator, String casPairName, String userId, ExchangeFirmStruct firmKey )
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Publishing addCASFirm " + originator + ", " + casPairName + ", " + userId + ", " + firmKey);
        }
        if (delegate != null)
        {
            delegate.addCASFirm(originator, casPairName, userId, firmKey);
        }
    }

    /**
     * This method is called by the CORBA event channel when a removeCASFirm event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Jeff Illian
     */
    public void removeCASFirm( String originator, String casPairName, String userId, ExchangeFirmStruct firmKey )
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Publishing removeCASFirm " + originator + ", " + casPairName + ", " + userId + ", " + firmKey);
        }
        if (delegate != null)
        {
            delegate.removeCASFirm(originator, casPairName, userId, firmKey);
        }
    }

    /**
     * This method is called by the CORBA event channel when a addCASClassForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Jeff Illian
     */
    public void addCASClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey)
    {
       // NOT IMPLEMENTED
    }

    /**
     * This method is called by the CORBA event channel when a removeCASClassForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Jeff Illian
     */
    public void removeCASClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey)
    {
        // NOT IMPLEMENTED
    }

    /**
     * This method is called by the CORBA event channel when a addCASClassForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Jeff Illian
     */
    public void addCASRFQClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Publishing addCASRFQClassForUser " + originator + ", " + casPairName + ", " + userId + ", " + sessionName + ", " + classKey);
        }
        if (delegate != null)
        {
            delegate.addCASRFQClassForUser(originator, casPairName, userId, sessionName, classKey);
        }
    }

    /**
     * This method is called by the CORBA event channel when a removeCASClassForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Jeff Illian
     */
    public void removeCASRFQClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Publishing removeCASRFQClassForUser " + originator + ", " + casPairName + ", " + userId + ", " + sessionName + ", " + classKey);
        }
        if (delegate != null)
        {
            delegate.removeCASRFQClassForUser(originator, casPairName, userId, sessionName, classKey);
        }
    }

    /**
     * This method is called by the CORBA event channel when a addCASClassForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Jeff Illian
     */
    public void addCASCurrentMarketClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Publishing addCASCurrentMarketClassForUser " + originator + ", " + casPairName + ", " + userId + ", " + sessionName + ", " + classKey);
        }
        if (delegate != null)
        {
            delegate.addCASCurrentMarketClassForUser(originator, casPairName, userId, sessionName, classKey);
        }
    }

    /**
     * This method is called by the CORBA event channel when a removeCASClassForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Jeff Illian
     */
    public void removeCASCurrentMarketClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Publishing removeCASCurrentMarketClassForUser " + originator + ", " + casPairName + ", " + userId + ", " + sessionName + ", " + classKey);
        }
        if (delegate != null)
        {
            delegate.removeCASCurrentMarketClassForUser(originator, casPairName, userId, sessionName, classKey);
        }
    }

    /**
     * This method is called by the CORBA event channel when a addCASClassForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Jeff Illian
     */
    public void addCASOpeningPriceClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Publishing addCASOpeningPriceClassForUser " + originator + ", " + casPairName + ", " + userId + ", " + sessionName + ", " + classKey);
        }
        if (delegate != null)
        {
            delegate.addCASOpeningPriceClassForUser(originator, casPairName, userId, sessionName, classKey);
        }
    }

    /**
     * This method is called by the CORBA event channel when a removeCASClassForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Jeff Illian
     */
    public void removeCASOpeningPriceClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Publishing removeCASOpeningPriceClassForUser " + originator + ", " + casPairName + ", " + userId + ", " + sessionName + ", " + classKey);
        }
        if (delegate != null)
        {
            delegate.removeCASOpeningPriceClassForUser(originator, casPairName, userId, sessionName, classKey);
        }
    }

    /**
     * This method is called by the CORBA event channel when a addCASClassForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Jeff Illian
     */
    public void addCASTickerClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Publishing addCASTickerClassForUser " + originator + ", " + casPairName + ", " + userId + ", " + sessionName + ", " + classKey);
        }
        if (delegate != null)
        {
            delegate.addCASTickerClassForUser(originator, casPairName, userId, sessionName, classKey);
        }
    }

    /**
     * This method is called by the CORBA event channel when a removeCASClassForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Jeff Illian
     */
    public void removeCASTickerClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Publishing removeCASTickerClassForUser " + originator + ", " + casPairName + ", " + userId + ", " + sessionName + ", " + classKey);
        }
        if (delegate != null)
        {
            delegate.removeCASTickerClassForUser(originator, casPairName, userId, sessionName, classKey);
        }
    }
    /**
     * This method is called by the CORBA event channel when a addCASClassForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Jeff Illian
     */
    public void addCASRecapClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Publishing addCASRecapClassForUser " + originator + ", " + casPairName + ", " + userId + ", " + sessionName + ", " + classKey);
        }
        if (delegate != null)
        {
            delegate.addCASRecapClassForUser(originator, casPairName, userId, sessionName, classKey);
        }
    }

    /**
     * This method is called by the CORBA event channel when a removeCASClassForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Jeff Illian
     */
    public void removeCASRecapClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Publishing removeCASRecapClassForUser " + originator + ", " + casPairName + ", " + userId + ", " + sessionName + ", " + classKey);
        }
        if (delegate != null)
        {
            delegate.removeCASRecapClassForUser(originator, casPairName, userId, sessionName, classKey);
        }
    }

     /**
     * This method is called by the CORBA event channel when a addCASProductForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Jeff Illian
     */
    public void addCASBookDepthProductForUser( String originator, String casPairName, String userId, String sessionName, int productKey)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Publishing addCASBookDepthProductForUser " + originator + ", " + casPairName + ", " + userId + ", " + sessionName + ", " + productKey);
        }
        if (delegate != null)
        {
            delegate.addCASBookDepthProductForUser(originator, casPairName, userId, sessionName, productKey);
        }
    }

    /**
     * This method is called by the CORBA event channel when a removeCASProductForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Jeff Illian
     */
    public void removeCASBookDepthProductForUser( String originator, String casPairName, String userId, String sessionName, int productKey)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Publishing removeCASBookDepthProductForUser " + originator + ", " + casPairName + ", " + userId + ", " + sessionName + ", " + productKey);
        }
        if (delegate != null)
        {
            delegate.removeCASBookDepthProductForUser(originator, casPairName, userId, sessionName, productKey);
        }
    }

     /**
     * This method is called by the CORBA event channel when a addCASQuoteLockedNotification event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Jeff Illian
     */
    public void addCASQuoteLockedNotification( String originator, String casPairName, String userId, String sessionName, int productKey)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Publishing addCASQuoteLockedNotification " + originator + ", " + casPairName + ", " + userId + ", " + sessionName + ", " + productKey);
        }
        if (delegate != null)
        {
            delegate.addCASQuoteLockedNotification(originator, casPairName, userId, sessionName, productKey);
        }
    }

    /**
     * This method is called by the CORBA event channel when a removeCASProductForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Jeff Illian
     */
    public void removeCASQuoteLockedNotification( String originator, String casPairName, String userId, String sessionName, int productKey)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Publishing removeCASQuoteLockedNotification " + originator + ", " + casPairName + ", " + userId + ", " + sessionName + ", " + productKey);
        }
        if (delegate != null)
        {
            delegate.removeCASQuoteLockedNotification(originator, casPairName, userId, sessionName, productKey);
        }
    }

    /**
     * This method is called by the CORBA event channel when a addCASProductForUser event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Jeff Illian
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
     * @author Jeff Illian
     */
    public void removeCASProductForUser( String originator, String casPairName, String userId, String sessionName, int productKey)
    {
        // NOT IMPLEMENTED
    }
}
