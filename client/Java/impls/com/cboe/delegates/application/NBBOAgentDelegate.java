package com.cboe.delegates.application;

import com.cboe.interfaces.application.NBBOAgent;

public class NBBOAgentDelegate extends com.cboe.idl.cmiIntermarket.POA_NBBOAgent_tie {
    public NBBOAgentDelegate(NBBOAgent delegate) {
        super(delegate);
    }
}