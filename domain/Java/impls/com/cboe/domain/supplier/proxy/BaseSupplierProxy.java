package com.cboe.domain.supplier.proxy;

import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.idl.cmiUtil.*;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.channel.ChannelAdapter;
import com.cboe.util.channel.ChannelListener;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.domain.util.CallbackDeregistrationInfoStruct;
import com.cboe.domain.util.InstrumentorNameHelper;
import org.omg.CORBA.UserException;

/**
 * BaseConsumerProxy serves as the abstract base proxy to all of the callback
 * consumer proxies.
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/25/1999
 */

public abstract class BaseSupplierProxy extends BObject implements ChannelListener
{
    protected BaseSessionManager sessionManager;
    protected Object hashKey;
    protected boolean isConnectionLostFatal = true;
    protected ChannelAdapter adapter;
    protected short queuePolicy;

    protected int flushProxyQueueDepth;
    protected int noActionProxyQueueDepth;
    protected String name;


    /**
     * BaseConsumerProxy constructor.
     *
     */
    public BaseSupplierProxy(BaseSessionManager sessionManager, ChannelAdapter adapter)
    {
        super();
        this.sessionManager = sessionManager;
        this.adapter = adapter;
        queuePolicy = 0;

    }

    public String getName()
    {
        if (name == null && sessionManager != null)
        {
            try
            {
                name = InstrumentorNameHelper.createInstrumentorName(new String[]{sessionManager.getInstrumentorName(),this.getClass().getName()}, this);
            }
            catch(UserException e)
            {
                Log.exception(e);
            }
        }
        return name;
    }

    public BaseSupplierProxy(BaseSessionManager sessionManager, ChannelAdapter adapter, short queuePolicy)
    {
        super();
        this.sessionManager = sessionManager;
        this.adapter = adapter;
        this.queuePolicy = queuePolicy;
    }

    public ChannelAdapter getChannelAdapter()
    {
        return adapter;
    }

    public short getQueuePolicy()
    {
        return queuePolicy;
    }

    public void setHashKey(Object hashKey)
    {
        this.hashKey = hashKey;
    }

    public BaseSessionManager getSessionManager()
    {
        return this.sessionManager;
    }

    public void cleanUp()
    {
        Log.debug(this, "Cleaning up proxy for " + sessionManager);
        sessionManager = null;
        stopMethodInstrumentation();
    }

    /**
     * <code>hashCode()</code> returns the hash code for this object.
     *
     * @return the int value of the hash code.
     */
    public int hashCode()
    {
        return hashKey.hashCode();
    }

    /**
     * <code>equals()</code> returns a boolean value representing the truth of the
     * equality of this object and the passed object.
     *
     * @param obj the Object to check equality with.
     * @return true if the passed Object is equivalent to this instance; false otherwise.
     */
    public boolean equals(Object obj)
    {
        // check the equivalence of the IOR strings.
        if (obj instanceof BaseSupplierProxy)
        {
            return hashKey.equals(((BaseSupplierProxy)obj).getHashKey());
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns the string used as the hashable object.
     */
    protected Object getHashKey()
    {
        return hashKey;
    }

    public void setConnectionLostFatal(boolean b)
    {
        isConnectionLostFatal = b;
    }

    public void initConnectionProperty(boolean connection)
    {
        isConnectionLostFatal = connection;
    }

    public void initFlushProxyQueueDepthProperty(int flushQueueDepth)
    {
        flushProxyQueueDepth = flushQueueDepth;
    }

    public void initNoActionProxyQueueDepthProperty(int noActionQueueDepth)
    {
        noActionProxyQueueDepth = noActionQueueDepth;
    }

    public boolean isConnectionLostFatal()
    {
        return isConnectionLostFatal;
    }

    public int getFlushProxyQueueDepthLimit()
    {
        return flushProxyQueueDepth;
    }

    public int getNoActionProxyQueueDepthLimit()
    {
        return noActionProxyQueueDepth;
    }

    public CallbackDeregistrationInfoStruct getCallbackDeregistrationInfoStruct(ChannelEvent event)
    {
        CallbackDeregistrationInfoStruct deregistrationInfo = null;
        CallbackInformationStruct callbackInformationStruct = getCallbackInformationStruct(event);
        if(callbackInformationStruct != null)
        {
            deregistrationInfo = new CallbackDeregistrationInfoStruct(
                    callbackInformationStruct,
                    "Connection lost.",
                    1);
        }
        return deregistrationInfo;
    }

    /**
     * This is the implementation of the base class abstract method.  It calls
     * the session manager for the current user and notifies it of a lost or
     * invalid connection to the consumer callback object.
     *
     */
    public void lostConnection(ChannelEvent event)
    {
        try
        {
            synchronized(this)
            {
                if (sessionManager != null) {
                    if (isConnectionLostFatal())
                    {
                        sessionManager.lostConnection(this);
                    }
                    else
                    {
                        sessionManager.unregisterNotification(getCallbackDeregistrationInfoStruct(event));
                    }
                }
            }
        }
        catch(Exception e)
        {
            Log.exception(this, "session :" + sessionManager, e);
        }
        finally
        {
            // Warning: if you change this behavior you might break MDX and non-overlay proxies will not be removed from
            // suppliers (both in CBOEdirect and MDX)! -- Eric 4/27/06
            throw new LostConnectionException();
        }
    }

    /**
     * This abstract method is called by this class.  It should be implemented by
     * decendents to return the CallbackDeregistrationInfoStruct in case there is
     * an exception because of lost connection.
     */
    public abstract CallbackInformationStruct getCallbackInformationStruct(ChannelEvent event);

    /**
     * This abstract method is called by ChannelThreadCommand.  It takes
     * the passed EventChannelEvent, parses out the relevant data for the proxied
     * object, and calls the proxied objects callback method passing in the
     * appropriate data.
     *
     * @param event the ChannelEvent containing the data to send the listener.
     */
    public abstract void channelUpdate(ChannelEvent event);
    public abstract void startMethodInstrumentation(boolean privateOnly);
    public abstract void stopMethodInstrumentation();
}
