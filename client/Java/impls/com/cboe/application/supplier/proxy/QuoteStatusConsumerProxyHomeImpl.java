package com.cboe.application.supplier.proxy;

import com.cboe.application.supplier.proxy.GMDConsumerProxyHomeImpl;
import com.cboe.domain.supplier.proxy.GMDSupplierProxy;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.application.QuoteStatusConsumerProxyHome;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedChannelListener;
import com.cboe.util.channel.ChannelListener;

/**
 * QuoteStatusConsumerProxyHomeImpl.
 */
public class QuoteStatusConsumerProxyHomeImpl
    extends GMDConsumerProxyHomeImpl
    implements QuoteStatusConsumerProxyHome
{

    // QuoteStatusConsumerProxyHome methods
    //--------------------------------------------------------------------------
    /**
      * Follows the proscribed method for creating and generating a impl class.
      * Sets the Session Manager parent class and initializes the Quote Query.
      */
    public ChannelListener create(
            com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer consumer,
            SessionManager sessionManager,
            boolean gmd)
        throws  DataValidationException,
                SystemException,
                CommunicationException,
                AuthorizationException
    {
        QuoteStatusConsumerProxy proxy = new QuoteStatusConsumerProxy(
            consumer, sessionManager, gmd, this);
        processProxy(proxy, sessionManager);
        if(getInstrumentationEnablementProperty())
        {
            proxy.startMethodInstrumentation(getInstrumentationProperty());
        }
        return proxy;
    }

    /**
      * Follows the proscribed method for creating and generating an impl class.
      * Sets the Session Manager parent class and initializes the Quote Query.
      */
    public ChannelListener create(
            com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer consumer,
            SessionManager sessionManager,
            boolean gmd)
        throws  DataValidationException,
                SystemException,
                CommunicationException,
                AuthorizationException
    {
        QuoteStatusV2ConsumerProxy proxy = new QuoteStatusV2ConsumerProxy(
            consumer, sessionManager, gmd, this);

        processProxy(proxy, sessionManager);
        if(getInstrumentationEnablementProperty())
        {
            proxy.startMethodInstrumentation(getInstrumentationProperty());
        }
        return proxy;
    }

    /**
     * Adds this proxy to the GMD maps (if the proxy's GMD flag is 'true').
     */
    public void addGMDProxy(
            ChannelListener proxy,
            boolean forUser,
            Integer classKey)
        throws DataValidationException
    {
        if (proxy instanceof GMDSupplierProxy)
        {
            GMDSupplierProxy gmdProxy = (GMDSupplierProxy) proxy;

            if (gmdProxy.getGMDStatus())
            {
                super.addGMDProxy(forUser, gmdProxy, classKey);
            }
        }
    }

    /**
     * Cleans up the given consumer from the home's maps of registered
     * consumers.
     */
    public void removeGMDProxy(
            ChannelListener proxy,
            boolean forUser,
            Integer classKey)
    {
        if (proxy instanceof GMDSupplierProxy)
        {
            removeGMDProxy(forUser, (GMDSupplierProxy) proxy, classKey);
        }
    }


    //--------------------------------------------------------------------------
    // private methods
    //--------------------------------------------------------------------------
    /**
     * Handles the common processing of a proxy (things that have to be done
     * regardless of what CMI version the proxy is for).
     */
    private void processProxy(GMDSupplierProxy proxy, SessionManager sessionManager)
    {
        // Every business object must be added to the container BEFORE anything
        // else.
        addToContainer(proxy);

        // Every BObject created MUST have a name if the object is to be a
        // managed object.
        proxy.create(String.valueOf(proxy.hashCode()));
        proxy.initConnectionProperty(getConnectionProperty(sessionManager));
    }

}
