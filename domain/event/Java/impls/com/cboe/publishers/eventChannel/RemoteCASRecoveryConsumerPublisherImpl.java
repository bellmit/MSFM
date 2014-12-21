package com.cboe.publishers.eventChannel;

/**
 * RemoteCASRecoveryConsumerPublisherImpl.
 *
 * @author Jing Chen
 */

import com.cboe.idl.events.RemoteCASRecoveryEventConsumer;
import com.cboe.interfaces.events.RemoteCASRecoveryConsumer;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class RemoteCASRecoveryConsumerPublisherImpl extends BObject implements RemoteCASRecoveryConsumer
{
    private RemoteCASRecoveryEventConsumer delegate;


    public RemoteCASRecoveryConsumerPublisherImpl(RemoteCASRecoveryEventConsumer delegate)
    {
        super();
        this.delegate = delegate;
    }

    public void acceptMarketDataRecoveryForGroup(int groupKey)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing acceptMarketDataGroupRecovery" +
                    "; groupKey= " + groupKey +".");
        }

        if (delegate != null)
        {
           delegate.acceptMarketDataRecoveryForGroup(groupKey);
        }
    }

    public void acceptMDXRecoveryForGroup(int mdxGroupKey)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing acceptMDXRecoveryForGroup" +
                            "; groupKey= " + mdxGroupKey + ".");
        }

        if(delegate != null)
        {
            delegate.acceptMDXRecoveryForGroup(mdxGroupKey);
        }
    }
}
