//
// -----------------------------------------------------------------------------------
// Source file: FillReportRejectMessageElementImpl.java
//
// PACKAGE: com.cboe.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.omt;

import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.order.ManualFillRoutingStruct;
import com.cboe.idl.order.ManualFillStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;

import com.cboe.interfaces.presentation.omt.FillReportRejectMessageElement;
import com.cboe.interfaces.presentation.omt.MessageElement;
import com.cboe.interfaces.presentation.util.CBOEId;

import com.cboe.presentation.util.CBOEIdImpl;

public class FillReportRejectMessageElementImpl
        extends InfoMessageElementImpl
        implements FillReportRejectMessageElement
{
    private CboeIdStruct cboeIdStruct;
    private ManualFillStruct manualFillInfo;
    private String rejectReason;
    private CBOEId wrappedId;

    protected FillReportRejectMessageElementImpl(ManualFillRoutingStruct manualFillRoutingStruct,
                                                 RoutingParameterV2Struct routingParameterV2Struct,
                                                 CboeIdStruct cboeIdStruct,
                                                 String rejectReason)
    {
        super(MessageElement.MessageType.ORDER_FILL_REPORT_REJECT, routingParameterV2Struct);
        this.cboeIdStruct = cboeIdStruct;
        this.manualFillInfo = manualFillRoutingStruct.manualFillInfo;
        this.rejectReason = rejectReason;
        setOrderId(manualFillInfo.orderId);
        setRouteReasonStruct(manualFillRoutingStruct.routeReason);
        setClassPostStation(getProductClass(getSessionName(), getProductKeyValue()));
        setRestAsString(formatRestOfString());
    }

    public boolean equals(Object o)
    {
        boolean isEqual = super.equals(o);

        if(isEqual)
        {
            if(o != null && o instanceof FillReportRejectMessageElement)
            {
                FillReportRejectMessageElement that = (FillReportRejectMessageElement) o;

                isEqual = getCboeId().equals(that.getCboeId());
            }
        }

        return isEqual;
    }

    public CBOEId getCboeId()
    {
        if(wrappedId == null)
        {
            wrappedId = new CBOEIdImpl(cboeIdStruct);
        }
        return wrappedId;
    }

    public ManualFillStruct getFillReport()
    {
        return manualFillInfo;
    }

    public String getRejectReason()
    {
        return rejectReason;
    }

    private String formatRestOfString()
    {
        StringBuilder sb = new StringBuilder(30);
        sb.append("CBOEId=[").append(getCboeId()).append("] RejectReason=[").append(getRejectReason()).append("]  ");
        sb.append(getFormattedPrice()).append(getFormattedQuantity());

        return sb.toString();
    }

    private String getFormattedPrice()
    {
        PriceStruct priceStruct = manualFillInfo.executionPrice;
        StringBuilder sb = new StringBuilder(20);
        sb.append("Execution Price: ").append(priceStruct.whole).append('.').append(priceStruct.fraction).append(' ');
        return sb.toString();
    }

    private String getFormattedQuantity()
    {
        StringBuilder sb = new StringBuilder(25);
        sb.append("Quantity Traded: ").append(manualFillInfo.tradedQuantity).append(' ');
        return sb.toString();
    }

    public int getProductKeyValue()
    {
        return manualFillInfo.productKey;
    }

    public String getSessionName()
    {
        return manualFillInfo.sessionName;
    }

}
