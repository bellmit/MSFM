//
// ------------------------------------------------------------------------
// FILE: AlertHeaderImpl.java
//
// PACKAGE: com.cboe.internalPresentation.alert
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.AlertHdrStruct;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.AlertHeader;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.presentation.util.CBOEId;
import com.cboe.presentation.common.dateTime.DateTimeImpl;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.util.CBOEIdImpl;
import com.cboe.domain.util.ExtensionsHelper;

/**
 * @author torresl@cboe.com
 */
class AlertHeaderImpl implements AlertHeader
{
    protected CBOEId alertId;
    protected DateTime alertCreationTime;
    protected short alertType;
    protected String sessionName;
    protected AlertHdrStruct alertHdrStruct;
    protected ExtensionsHelper extensionsHelper;
    public AlertHeaderImpl(AlertHdrStruct alertHdrStruct)
    {
        super();
        this.alertHdrStruct = alertHdrStruct;
        initialize();
    }

    private void initialize()
    {
        alertId = new CBOEIdImpl(alertHdrStruct.alertId);
        alertCreationTime = new DateTimeImpl(alertHdrStruct.alertCreationTime);
        alertType = alertHdrStruct.alertType;
        sessionName = alertHdrStruct.sessionName;
        try
        {
            getExtensionsHelper().setExtensions(alertHdrStruct.hdrExtensions);
        }
        catch (java.text.ParseException e)
        {
            GUILoggerHome.find().exception(e, e.getMessage());
        }
    }

    public CBOEId getAlertId()
    {
        return alertId;
    }

    public DateTime getAlertCreationTime()
    {
        return alertCreationTime;
    }

    public short getAlertType()
    {
        return alertType;
    }

    public String getSessionName()
    {
        return sessionName;
    }

    public String getExtensions()
    {
        return getExtensionsHelper().toString();
    }

    public AlertHdrStruct getStruct()
    {
        AlertHdrStruct headerStruct = new AlertHdrStruct();
        headerStruct.alertCreationTime = getAlertCreationTime().getDateTimeStruct();
        headerStruct.alertId = getAlertId().getStruct();
        headerStruct.alertType = getAlertType();
        headerStruct.sessionName = getSessionName();
        headerStruct.hdrExtensions = getExtensions();
        return headerStruct;
    }

    protected ExtensionsHelper getExtensionsHelper()
    {
        if(extensionsHelper == null)
        {
            extensionsHelper = new ExtensionsHelper();
        }
        return extensionsHelper;
    }

    public String getExtensionField(String fieldName)
    {
        return getExtensionsHelper().getValue(fieldName);
    }
}
