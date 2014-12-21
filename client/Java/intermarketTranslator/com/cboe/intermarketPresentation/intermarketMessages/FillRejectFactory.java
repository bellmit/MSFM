/**
 * Copyright 2000-2002 (C) Chicago Board Options Exchange
 * Package: com.cboe.intermarketPresentation.intermarketMessages
 * User: torresl
 * Date: Jan 2, 2003 10:18:30 AM
 */
package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.FillReject;
import com.cboe.idl.cmiIntermarketMessages.FillRejectStruct;

public class FillRejectFactory
{
    public static FillReject createFillReject(FillRejectStruct fillRejectStruct)
    {
        return new FillRejectImpl(fillRejectStruct);
    }
}