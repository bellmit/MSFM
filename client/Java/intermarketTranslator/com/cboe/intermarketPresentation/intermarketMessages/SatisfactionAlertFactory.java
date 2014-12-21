package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.SatisfactionAlertStruct;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.SatisfactionAlert;

//
// ------------------------------------------------------------------------
// Source file: SatisfactionAlertFactory.java
// 
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//package com.cboe.intermarketPresentation.intermarketMessages;

public class SatisfactionAlertFactory
{
    public static SatisfactionAlert createSatisfactionAlert(SatisfactionAlertStruct satisfactionAlertStruct)
    {
        return new SatisfactionAlertImpl(satisfactionAlertStruct);
    }
}