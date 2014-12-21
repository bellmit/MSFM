//
// ------------------------------------------------------------------------
// FILE: AlertHeader.java
//
// PACKAGE: com.cboe.interfaces.internalPresentation.alert
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.AlertHdrStruct;
import com.cboe.interfaces.presentation.util.CBOEId;
import com.cboe.interfaces.domain.dateTime.DateTime;

public interface AlertHeader
{
    CBOEId getAlertId();
    DateTime getAlertCreationTime();
    short getAlertType();
    String getSessionName();
    String getExtensions();
    String getExtensionField(String fieldName);

    AlertHdrStruct getStruct();

}
