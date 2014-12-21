//
// ------------------------------------------------------------------------
// FILE: ExpectedOpeningPriceV2ConsumerDelegate.java
// 
// PACKAGE: com.cboe.delegates.callback
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//

package com.cboe.delegates.callback;

import com.cboe.idl.cmiCallbackV2.POA_CMIExpectedOpeningPriceConsumer_tie;
import com.cboe.interfaces.callback.ExpectedOpeningPriceV2Consumer;

public class ExpectedOpeningPriceV2ConsumerDelegate extends POA_CMIExpectedOpeningPriceConsumer_tie
{
    public ExpectedOpeningPriceV2ConsumerDelegate(ExpectedOpeningPriceV2Consumer delegate)
    {
        super(delegate);
    }
}
