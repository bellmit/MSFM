//
// ------------------------------------------------------------------------
// Source file: AlertSearchCriteriaFactory.java
//
// PACKAGE: com.cboe.internalPresentation.alert
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------

package com.cboe.internalPresentation.alert;

import com.cboe.interfaces.internalPresentation.alert.AlertSearchCriteria;
import com.cboe.idl.alert.AlertSearchCriteriaStruct;

public class AlertSearchCriteriaFactory
{
    public static AlertSearchCriteria createAlertSearchCriteria(AlertSearchCriteriaStruct alertSearchCriteriaStruct)
    {
        return new AlertSearchCriteriaImpl(alertSearchCriteriaStruct);
    }
    public static AlertSearchCriteria createAlertSearchCriteria()
    {
        return new AlertSearchCriteriaImpl();
    }
}