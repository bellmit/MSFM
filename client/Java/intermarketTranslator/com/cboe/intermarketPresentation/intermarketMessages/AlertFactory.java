//
// ------------------------------------------------------------------------
// Source file: AlertFactory.java
//
// PACKAGE: com.cboe.internalPresentation.alert
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------

package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.AlertStruct;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.Alert;

public class AlertFactory
{
    public static Alert createAlert(AlertStruct alertStruct)
    {
        return new AlertImpl(alertStruct);
    }
    public static Alert createAlert()
    {
        return createAlert(false);
    }

    public static Alert createAlert(boolean populateWithDefaultData)
    {
        return new AlertImpl(populateWithDefaultData);
    }

}