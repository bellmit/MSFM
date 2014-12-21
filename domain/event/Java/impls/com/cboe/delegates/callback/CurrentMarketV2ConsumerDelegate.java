//
// ------------------------------------------------------------------------
// FILE: CurrentMarketV2ConsumerDelegate.java
// 
// PACKAGE: com.cboe.delegates.callback
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//

package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.CurrentMarketV2Consumer;
import com.cboe.idl.cmiCallbackV2.POA_CMICurrentMarketConsumer_tie;

public class CurrentMarketV2ConsumerDelegate extends POA_CMICurrentMarketConsumer_tie
{
    public CurrentMarketV2ConsumerDelegate(CurrentMarketV2Consumer delegate)
    {
        super(delegate);
    }
}
