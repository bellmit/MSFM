package com.cboe.publishers.eventChannel;

/**
 * RemoteCASSessionManagerConsumerPublisherImpl.
 *
 * @author Eric J. Fredericks
 */

import com.cboe.idl.events.RemoteCASSessionManagerEventConsumer;
import com.cboe.interfaces.events.RemoteCASSessionManagerConsumer;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class RemoteCASSessionManagerConsumerPublisherImpl extends BObject implements RemoteCASSessionManagerConsumer
{
    private RemoteCASSessionManagerEventConsumer delegate;


    public RemoteCASSessionManagerConsumerPublisherImpl(RemoteCASSessionManagerEventConsumer delegate)
    {
        super();
        this.delegate = delegate;
    }

    public void logout(String casOrigin, String userSessionIOR, String userId)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Logout for user=" + userId + " on CAS=" + casOrigin);
        }
        
        if(delegate != null)
        {
            delegate.logout(casOrigin, userSessionIOR, userId);
        }
    }
}
