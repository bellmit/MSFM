//
// ------------------------------------------------------------------------
// FILE: LockedQuoteStatusV2ConsumerDelegate.java
// 
// PACKAGE: com.cboe.delegates.callback
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//

package com.cboe.delegates.callback;

import com.cboe.idl.cmiCallbackV2.POA_CMILockedQuoteStatusConsumer_tie;
import com.cboe.interfaces.callback.LockedQuoteStatusV2Consumer;

public class LockedQuoteStatusV2ConsumerDelegate extends POA_CMILockedQuoteStatusConsumer_tie
{

    public LockedQuoteStatusV2ConsumerDelegate(LockedQuoteStatusV2Consumer delegate)
    {
        super(delegate);
    }
}
