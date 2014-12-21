package com.cboe.delegates.application;

import com.cboe.interfaces.application.OMTSessionManager;

public class OMTSessionManagerDelegate extends com.cboe.idl.omt.POA_OMTSessionManager_tie
{
    public OMTSessionManagerDelegate(OMTSessionManager delegate)
    {
        super(delegate);
    }
}
