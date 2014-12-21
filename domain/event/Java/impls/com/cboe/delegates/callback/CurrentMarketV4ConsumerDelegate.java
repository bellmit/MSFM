//
// ------------------------------------------------------------------------
// FILE: CurrentMarketV4ConsumerDelegate.java
//
// PACKAGE: com.cboe.delegates.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 2005 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.CurrentMarketV4Consumer;

import com.cboe.idl.cmiCallbackV4.POA_CMICurrentMarketConsumer_tie;

public class CurrentMarketV4ConsumerDelegate extends POA_CMICurrentMarketConsumer_tie
{
    public CurrentMarketV4ConsumerDelegate(CurrentMarketV4Consumer delegate)
    {
        super(delegate);
    }
}
