//
// -----------------------------------------------------------------------------------
// Source file: FillReportDropCopyMessageElementImpl.java
//
// PACKAGE: com.cboe.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.omt;

import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiOrder.OrderFilledReportStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.util.RouteReasonStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;

import com.cboe.interfaces.presentation.common.formatters.OrderFormatStrategy;
import com.cboe.interfaces.presentation.omt.FillReportDropCopyMessageElement;
import com.cboe.interfaces.presentation.omt.MessageElement;
import com.cboe.interfaces.presentation.order.FilledReport;

import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.formatters.FormatFactory;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.order.FilledReportFactory;
import com.cboe.presentation.util.StructBuilder;

public class FillReportDropCopyMessageElementImpl extends DropCopyMessageElementImpl
    implements FillReportDropCopyMessageElement
{
    private FilledReport fillReport;
    private FilledReportStruct filledReportStruct;
    private OrderFilledReportStruct orderFilledReportStruct;

    protected FillReportDropCopyMessageElementImpl (
            RoutingParameterV2Struct routingParameterV2Struct, RouteReasonStruct routeReasonStruct,
            OrderStruct orderStruct, FilledReportStruct filledReportStruct)
    {
        super(MessageElement.MessageType.FILL_REPORT_DROP_COPY, routingParameterV2Struct);
        this.filledReportStruct = StructBuilder.cloneFilledReportStruct(filledReportStruct);
        setOrder(orderStruct);
        setClassPostStation(getProductClass(orderStruct.classKey));
        OrderFormatStrategy formatter = FormatFactory.getOrderFormatStrategy();
        setRestAsString(formatter.format(filledReportStruct, null,
                                         OrderFormatStrategy.BRIEF_INFO_NAME_OMT));
        setRouteReasonStruct(routeReasonStruct);
        
        /*
         * we are experiencing some errors with the printing of FillReportDropCopy messages. As part
         * of this investigation this code checks the price of the filledReport struct.
         */
        if (GUILoggerHome.find().isDebugOn()
                && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.REPORT_GENERATION)) 
        {
            // safety first
            try
            {
                String str = String.format("FillReportDropCopyMessageElementImpl.CTOR(): fillprice=%s orderprice=%s",
                        DisplayPriceFactory.create(filledReportStruct.price).toString(), 
                        DisplayPriceFactory.create(orderStruct.price).toString());
                GUILoggerHome.find().debug(str, GUILoggerBusinessProperty.REPORT_GENERATION);
            }
            catch (Throwable t)
            {
                GUILoggerHome.find().exception("Error in accessing details of drop copy", t);
            }
        }
    }

    public int getProductKeyValue()
    {
        return filledReportStruct.productKey;
    }

    public String getSessionName()
    {
        return filledReportStruct.sessionName;
    }

    public short getRouteReason()
    {
        return getRouteReasonStruct().routeReason;
    }

    public String getRouteDescription()
    {
        return getRouteReasonStruct().routeDescription;
    }

    public FilledReportStruct getFilledReportStruct()
    {
        return filledReportStruct;
    }

    public FilledReport getFillReport()
    {
        if (fillReport == null && filledReportStruct != null)
        {
            fillReport = FilledReportFactory.createFilledReport(filledReportStruct);
        }
        return fillReport;
    }

    public OrderFilledReportStruct getOrderFilledReportStruct() throws CommunicationException,
            AuthorizationException, DataValidationException, NotFoundException, SystemException
    {
        if(orderFilledReportStruct == null)
        {
            FilledReportStruct[] fillReports = new FilledReportStruct[1];
            fillReports[0] = getFilledReportStruct();
            orderFilledReportStruct =
                    new OrderFilledReportStruct(getOrderDetailStruct(), fillReports);
        }
        return orderFilledReportStruct;
    }

    public boolean equals(Object o)
    {
        boolean isEqual = super.equals(o);

        if(isEqual)
        {
            if(o != null && o instanceof FillReportDropCopyMessageElement)
            {
                FillReportDropCopyMessageElement that = (FillReportDropCopyMessageElement) o;

                isEqual = getFilledReportStruct().tradeId.highCboeId ==
                          that.getFilledReportStruct().tradeId.highCboeId
                          && getFilledReportStruct().tradeId.lowCboeId ==
                             that.getFilledReportStruct().tradeId.lowCboeId;
            }
        }

        return isEqual;
    }
}
