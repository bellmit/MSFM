//
// ------------------------------------------------------------------------
// FILE: CurrentMarketV3ConsumerDelegate.java
// 
// PACKAGE: com.cboe.delegates.callback
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//

package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.CurrentMarketV3Consumer;
import com.cboe.idl.cmiCallbackV3.POA_CMICurrentMarketConsumer_tie;

public class CurrentMarketV3ConsumerDelegate extends POA_CMICurrentMarketConsumer_tie
{
    public CurrentMarketV3ConsumerDelegate(CurrentMarketV3Consumer delegate)
    {
        super(delegate);
    }
}
