package com.cboe.domain.supplier.proxy;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.interfaces.domain.session.BaseSessionManager;

/**
 * ClassStatusConsumerProxyHomeImpl.
 * @author Jimmy Wang
 */
public class BaseConsumerProxyHomeImpl extends ClientBOHome
{
    Boolean isConnectionLostFatal = null;
    Integer flushQueueDepth = null;
    Integer noActionQueueDepth = null;

    public final static String CONNECTION_PROPERTY = "connectionLostFatal";
    public final static String FlUSH_PROXY_QUEUE_DEPTH = "flushProxyQueueDepth";
    public final static String NO_ACTION_PROXY_QUEUE_DEPTH = "noActionProxyQueueDepth";

    boolean defaultValue = true;
    int defaultFlushQueueDepthValue = 0;
    int defaultNoActionQueueDepthValue = 0;

    public BaseConsumerProxyHomeImpl()
    {
        super();
        setSmaType( "GlobalBaseConsumerProxyHome.BaseConsumerProxyHomeImpl" );
    }

    public void clientStart()
    {
        Log.debug(this, "SMA Type = " + this.getSmaType());
    }

    protected boolean getConnectionProperty(BaseSessionManager session)
    {
        if(isConnectionLostFatal == null)
        {
            try {
                String connectionProperty = getProperty(CONNECTION_PROPERTY);
                isConnectionLostFatal = new Boolean(connectionProperty);
            } catch (Exception e)
            {
                Log.exception(this, "Could not get " + CONNECTION_PROPERTY
                    + " property.  Using default [session: " + session+"]", e);
                isConnectionLostFatal = new Boolean(defaultValue);
            }
        }
        return isConnectionLostFatal.booleanValue();
    }

    protected int getFlushQueueDepth(BaseSessionManager session)
    {
        if(flushQueueDepth == null)
        {
            try {
                String queueProperty = getProperty(FlUSH_PROXY_QUEUE_DEPTH);
                flushQueueDepth = new Integer(queueProperty);
            } catch (Exception e)
            {
                Log.exception(this, "Could not get "  + FlUSH_PROXY_QUEUE_DEPTH
                    + " property.  Using default [session: " + session+"]", e);
                flushQueueDepth = new Integer(defaultFlushQueueDepthValue);
            }
        }
        return flushQueueDepth.intValue();

    }

    protected int getNoActionQueueDepth(BaseSessionManager session)
    {
        if(noActionQueueDepth == null)
        {
            try {
                String queueProperty = getProperty(NO_ACTION_PROXY_QUEUE_DEPTH);
                noActionQueueDepth = new Integer(queueProperty);
            } catch (Exception e)
            {
                Log.exception(this, "Could not get " + NO_ACTION_PROXY_QUEUE_DEPTH
                    + " property.  Using default [session: " + session+"]", e);
                noActionQueueDepth = new Integer(defaultNoActionQueueDepthValue);
            }
        }
        return noActionQueueDepth.intValue();

    }

}
