package com.cboe.application.supplier.proxy;

import com.cboe.application.supplier.proxy.GMDConsumerProxyHomeImpl;
import com.cboe.domain.supplier.proxy.GMDSupplierProxy;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.application.OrderStatusConsumerProxyHome;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.util.channel.ChannelListener;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException;
import com.cboe.application.shared.IOrderAckConstraints;

/**
 * OrderStatusConsumerProxyHomeImpl.
 */
public class OrderStatusConsumerProxyHomeImpl extends GMDConsumerProxyHomeImpl
    implements OrderStatusConsumerProxyHome
{
    private IOrderAckConstraints iOrderAckConstraints = null;
    private static final String IORDER_ACK_SESSIONS = "IOrderSessionsNoAck";

    public OrderStatusConsumerProxyHomeImpl()
    {
        super();
    }

    //--------------------------------------------------------------------------
    // OrderStatusConsumerProxyHome methods
    //--------------------------------------------------------------------------
    /**
      * Follows the proscribed method for creating and generating a impl class.
      * Sets the Session Manager parent class and initializes the Order Query.
      */
    public ChannelListener create(
            com.cboe.idl.cmiCallback.CMIOrderStatusConsumer consumer,
            SessionManager sessionManager,
            boolean gmd)
        throws  DataValidationException,
                SystemException,
                CommunicationException,
                AuthorizationException
    {
        OrderStatusConsumerProxy proxy = new OrderStatusConsumerProxy(
            consumer, sessionManager, gmd, this, iOrderAckConstraints);

        processProxy(proxy, sessionManager);
        if(getInstrumentationEnablementProperty())
        {
            proxy.startMethodInstrumentation(getInstrumentationProperty());
        }
        return proxy;
    }

    /**
      * Follows the proscribed method for creating and generating an impl class.
      * Sets the Session Manager parent class and initializes the Order Query.
      */
    public ChannelListener create(
            com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer consumer,
            SessionManager sessionManager,
            boolean gmd)
        throws  DataValidationException,
                SystemException,
                CommunicationException,
                AuthorizationException
    {
        OrderStatusV2ConsumerProxy proxy = new OrderStatusV2ConsumerProxy(
            consumer, sessionManager, gmd, this, iOrderAckConstraints);

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

    public void clientInitialize() throws Exception
    {
        String sessions = null;
        try
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "SMA Type = " + this.getSmaType());
            }
            sessions = getProperty(IORDER_ACK_SESSIONS);
        }
        catch (NoSuchPropertyException nspe)
        {
            Log.information(this, "Received NoSuchPropertyException, no suppressed I Order sessions defined in the xml file.");
        }

        iOrderAckConstraints = IOrderAckConstraints.getInstance(sessions);
        String constraints = iOrderAckConstraints.toString();
        StringBuilder sb = new StringBuilder(constraints.length()+25);
        sb.append("I order no ack sessions= ").append(constraints);
        Log.information(this, sb.toString());
    }
}
