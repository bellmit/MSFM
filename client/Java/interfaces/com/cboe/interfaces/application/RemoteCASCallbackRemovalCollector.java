package com.cboe.interfaces.application;

import com.cboe.interfaces.domain.session.CallbackDeregistrationInfo;

public interface RemoteCASCallbackRemovalCollector
{
    public void acceptRemoteCASCallbackRemoval(CallbackDeregistrationInfo eventData);
}
