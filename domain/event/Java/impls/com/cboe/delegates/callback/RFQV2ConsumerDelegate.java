//
// ------------------------------------------------------------------------
// FILE: RFQV2ConsumerDelegate.java
// 
// PACKAGE: com.cboe.delegates.callback
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//

package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.RFQV2Consumer;
import com.cboe.idl.cmiCallbackV2.POA_CMIRFQConsumer_tie;

public class RFQV2ConsumerDelegate extends POA_CMIRFQConsumer_tie
{
    public RFQV2ConsumerDelegate(RFQV2Consumer delegate)
    {
        super(delegate);
    }
}
