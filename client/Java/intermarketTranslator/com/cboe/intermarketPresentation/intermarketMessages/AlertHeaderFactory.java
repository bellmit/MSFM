package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.AlertHdrStruct;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.AlertHeader;

//
// ------------------------------------------------------------------------
// Source file: AlertHeader.java
// 
// PACKAGE: com.cboe.internalPresentation.alert
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//package com.cboe.internalPresentation.alert;

public class AlertHeaderFactory
{
    public static AlertHeader createAlertHeader(AlertHdrStruct alertHdrStruct)
    {
        return new AlertHeaderImpl(alertHdrStruct);
    }
}