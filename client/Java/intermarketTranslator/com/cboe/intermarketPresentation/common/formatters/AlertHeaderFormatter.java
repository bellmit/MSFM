//
// ------------------------------------------------------------------------
// FILE: AlertFormatter.java
//
// PACKAGE: com.cboe.intermarketPresentation.common.formatters
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.intermarketPresentation.common.formatters;

import com.cboe.idl.cmiIntermarketMessages.AlertHdrStruct;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.AlertHeader;
import com.cboe.interfaces.presentation.common.formatters.AlertHeaderFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.DateFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ExtensionsFormatStrategy;

import com.cboe.presentation.common.formatters.*;
import com.cboe.presentation.common.formatters.FormatFactory;

import com.cboe.intermarketPresentation.intermarketMessages.AlertHeaderFactory;

/**
 * @author torresl@cboe.com
 */
class AlertHeaderFormatter extends AbstractCommonStylesFormatter implements AlertHeaderFormatStrategy
{

    public AlertHeaderFormatter()
    {
        super();
        initialize();
    }

    private void initialize()
    {
        setDefaultStyle(FULL_STYLE_NAME);
    }

    public String format(AlertHdrStruct alertHeaderStruct)
    {
        return format(alertHeaderStruct, getDefaultStyle());
    }

    public String format(AlertHdrStruct alertHeaderStruct, String style)
    {
        return format(AlertHeaderFactory.createAlertHeader(alertHeaderStruct), style);
    }

    public String format(AlertHeader alertHeader)
    {
        return format(alertHeader, getDefaultStyle());
    }

    public String format(AlertHeader alertHeader, String style)
    {
        // TODO: implement formatter
        validateStyle(style);
        StringBuffer buffer = new StringBuffer(200);
        String delimiter = getDelimiterForStyle(style);
        boolean brief = isBrief(style);
        String prefix = "Alert ";
        if(brief)
        {
            prefix = "";
        }
        buffer.append(prefix).append("Type: ");
        buffer.append(AlertTypes.toString(alertHeader.getAlertType()));
        buffer.append(delimiter);
        buffer.append(prefix).append("ID: ");
        buffer.append(alertHeader.getAlertId().getHighId()).append(":").append(alertHeader.getAlertId().getLowId());
        buffer.append(" ");
        buffer.append(prefix).append("Creation Time: ");
        buffer.append(
                CommonFormatFactory.getDateFormatStrategy().format(
                        alertHeader.getAlertCreationTime(),
                        DateFormatStrategy.DATE_FORMAT_24_HOURS_STYLE));
        buffer.append(delimiter);
        buffer.append("Session: ").append(alertHeader.getSessionName());
        buffer.append(delimiter);
        if(!brief) // only add the extensions in FULL mode
        {
            buffer.append(
                    FormatFactory.getExtensionsFormatStrategy().format(
                            alertHeader.getExtensions(),
                            ExtensionsFormatStrategy.FULL_STYLE_NAME));
        }
        return buffer.toString();
    }
}
