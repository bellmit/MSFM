package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.FillRejectRequest;
import com.cboe.idl.cmiIntermarketMessages.FillRejectRequestStruct;

//
// ------------------------------------------------------------------------
// Source file: FillRejectRequestFactory.java
// 
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//package com.cboe.intermarketPresentation.intermarketMessages;

public class FillRejectRequestFactory
{
    public static FillRejectRequest createFillRejectRequest(FillRejectRequestStruct fillRejectRequestStruct)
    {
        return new FillRejectRequestImpl(fillRejectRequestStruct);
    }

    public static FillRejectRequest createFillRejectRequest()
    {
        return new FillRejectRequestImpl();
    }
}