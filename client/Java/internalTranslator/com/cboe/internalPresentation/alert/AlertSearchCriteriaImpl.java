//
// ------------------------------------------------------------------------
// Source file: AlertSearchCriteriaImpl.java
//
// PACKAGE: com.cboe.internalPresentation.alert
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------

package com.cboe.internalPresentation.alert;

import com.cboe.interfaces.internalPresentation.alert.AlertSearchCriteria;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.idl.alert.AlertSearchCriteriaStruct;
import com.cboe.presentation.common.dateTime.DateTimeImpl;

/**
 * @author torresl@cboe.com
 */
class AlertSearchCriteriaImpl implements AlertSearchCriteria
{
    String sessionName;
    int classKey;
    int productKey;
    DateTime fromDateTime;
    DateTime toDateTime;
    AlertSearchCriteriaStruct alertSearchCriteriaStruct;
    public AlertSearchCriteriaImpl(AlertSearchCriteriaStruct alertSearchCriteriaStruct)
    {
        this.alertSearchCriteriaStruct = alertSearchCriteriaStruct;
        initialize();
    }
    public AlertSearchCriteriaImpl()
    {

    }
    private void initialize()
    {
        this.sessionName = new String(alertSearchCriteriaStruct.sessionName);
        this.classKey = alertSearchCriteriaStruct.classKey;
        this.productKey = alertSearchCriteriaStruct.productKey;
        this.fromDateTime = new DateTimeImpl(alertSearchCriteriaStruct.fromDateTime);
        this.toDateTime = new DateTimeImpl(alertSearchCriteriaStruct.toDateTime);
    }

    public String getSessionName()
    {
        return sessionName;
    }

    public void setSessionName(String sessionName)
    {
        this.sessionName = sessionName;
    }

    public int getClassKey()
    {
        return classKey;
    }

    public void setClassKey(int classKey)
    {
        this.classKey = classKey;
    }

    public int getProductKey()
    {
        return productKey;
    }

    public void setProductKey(int productKey)
    {
        this.productKey = productKey;
    }

    public DateTime getFromDateTime()
    {
        return fromDateTime;
    }

    public void setFromDateTime(DateTime fromDateTime)
    {
        this.fromDateTime = fromDateTime;
    }

    public DateTime getToDateTime()
    {
        return toDateTime;
    }

    public void setToDateTime(DateTime toDateTime)
    {
        this.toDateTime = toDateTime;
    }

    public AlertSearchCriteriaStruct toStruct()
    {
        AlertSearchCriteriaStruct struct = new AlertSearchCriteriaStruct();
        struct.sessionName = getSessionName();
        struct.classKey = getClassKey();
        struct.productKey = getProductKey();
        struct.fromDateTime = getFromDateTime().getDateTimeStruct();
        struct.toDateTime = getToDateTime().getDateTimeStruct();
        return struct;
    }
}
