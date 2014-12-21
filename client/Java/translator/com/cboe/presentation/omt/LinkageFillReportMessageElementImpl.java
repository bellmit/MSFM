//
// -----------------------------------------------------------------------------------
// Source file: LinkageFillReportMessageElementImpl.java
//
// PACKAGE: com.cboe.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.omt;

import com.cboe.idl.order.FilledReportRoutingStruct;
import com.cboe.idl.order.LinkageExtensionsStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;

import com.cboe.interfaces.presentation.common.formatters.OrderFormatStrategy;
import com.cboe.interfaces.presentation.omt.LinkageFillReportMessageElement;
import com.cboe.interfaces.presentation.omt.MessageElement;
import com.cboe.interfaces.presentation.order.FilledReport;
import com.cboe.interfaces.presentation.util.CBOEId;

import com.cboe.presentation.common.formatters.FormatFactory;
import com.cboe.presentation.common.formatters.OrderFormatter;
import com.cboe.presentation.order.FilledReportFactory;

public class LinkageFillReportMessageElementImpl
        extends InfoMessageElementImpl
        implements LinkageFillReportMessageElement
{
    private FilledReport fillReport;
    private LinkageExtensionsStruct linkageExtensions;
    private String displayOrsId;

    protected LinkageFillReportMessageElementImpl(FilledReportRoutingStruct fillReportRoutingStruct,
                                                  LinkageExtensionsStruct linkageExtensions,
                                                  RoutingParameterV2Struct routingParameterV2Struct)
    {
        super(MessageElement.MessageType.LINKAGE_FILL_REPORT, routingParameterV2Struct);
        fillReport = FilledReportFactory.createFilledReport(fillReportRoutingStruct.fillReport);
        this.linkageExtensions = linkageExtensions;
        setRouteReasonStruct(fillReportRoutingStruct.routeReason);
        setClassPostStation(getProductClass(getSessionName(), getProductKeyValue()));

        OrderFormatStrategy formatter = FormatFactory.getOrderFormatStrategy();
        setRestAsString(formatter.format(fillReportRoutingStruct.fillReport, null,
                                         OrderFormatStrategy.BRIEF_INFO_LEAVES_NAME));
    }

    public boolean equals(Object o)
    {
        boolean isEqual = super.equals(o);

        if(isEqual)
        {
            if(o != null && o instanceof LinkageFillReportMessageElement)
            {
                LinkageFillReportMessageElement that = (LinkageFillReportMessageElement) o;

                isEqual = getFillReport().getTradeId().equals(that.getFillReport().getTradeId());
            }
        }

        return isEqual;
    }

    public FilledReport getFillReport()
    {
        return fillReport;
    }

    public LinkageExtensionsStruct getLinkageExtensions()
    {
        return linkageExtensions;
    }

    public int getProductKeyValue()
    {
        return fillReport.getProductKey();
    }

    public String getSessionName()
    {
        return fillReport.getSessionName();
    }

    public CBOEId getCboeId()
    {
        return fillReport.getTradeId();
    }

    public String getGiveUpFirm()
    {
        return fillReport.getExecutingOrGiveUpFirm().getFirm();
    }

    public String getOrsId()
    {
        return fillReport.getOrsId();
    }

    public String getDisplayOrsId()
    {
        if(displayOrsId == null)
        {
            displayOrsId = OrderFormatter.formatOrsIdForDisplay(fillReport.getOrsId());
        }
        return displayOrsId;
    }
}