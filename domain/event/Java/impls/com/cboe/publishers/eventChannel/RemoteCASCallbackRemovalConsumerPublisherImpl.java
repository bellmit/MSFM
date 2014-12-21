package com.cboe.publishers.eventChannel;

/**
 * RemoteCASCallbackRemovalConsumerPublisherImpl.
 *
 * @author Eric J. Fredericks
 */

import com.cboe.idl.events.RemoteCASCallbackRemovalEventConsumer;
import com.cboe.idl.cmiUtil.CallbackInformationStruct;
import com.cboe.interfaces.events.RemoteCASCallbackRemovalConsumer;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class RemoteCASCallbackRemovalConsumerPublisherImpl extends BObject implements RemoteCASCallbackRemovalConsumer
{
    private RemoteCASCallbackRemovalEventConsumer delegate;


    public RemoteCASCallbackRemovalConsumerPublisherImpl(RemoteCASCallbackRemovalEventConsumer delegate)
    {
        super();
        this.delegate = delegate;
    }
    
    public void acceptCallbackRemoval(
            String casOrigin,
            String userId,
            String userSessionIOR,
            String reason,
            int errorCode,
            CallbackInformationStruct callbackInfo)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "publishing acceptCallbackRemoval" +
                    "; casOrigin=" + casOrigin +
                    "; userId=" + userId +
                    "; reason=" + reason +
                    "; errorCode=" + errorCode);
        }
        
        if(delegate != null)
        {
            delegate.acceptCallbackRemoval(
                 casOrigin,
                 userId,
                 userSessionIOR,
                 reason,
                 errorCode,
                 callbackInfo);
        }
    }
    
}
