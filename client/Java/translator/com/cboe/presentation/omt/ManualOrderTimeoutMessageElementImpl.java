//
// -----------------------------------------------------------------------------------
// Source file: ManualOrderTimeoutMessageElementImpl.java
//
// PACKAGE: com.cboe.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.omt;

import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.order.ManualOrderTimeoutRoutingStruct;
import com.cboe.idl.order.OrderHandlingInstructionStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;

import com.cboe.interfaces.presentation.common.formatters.VolumeFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.OrderFormatStrategy;
import com.cboe.interfaces.presentation.omt.ManualOrderTimeoutMessageElement;

import com.cboe.presentation.common.formatters.FormatFactory;
import com.cboe.presentation.common.formatters.Utility;

public class ManualOrderTimeoutMessageElementImpl extends InfoMessageElementImpl
    implements ManualOrderTimeoutMessageElement
{
    public static final String DATE_TIME_FORMAT = "yyyy/MM/dd hh:mm:ss aa";
    private VolumeFormatStrategy volumeFormatter = FormatFactory.getVolumeFormatStrategy();
    private ManualOrderTimeoutRoutingStruct manualOrderTimeoutInfo;
    private OrderHandlingInstructionStruct orderHandlingStruct;

    protected ManualOrderTimeoutMessageElementImpl(
            RoutingParameterV2Struct routingParameterV2Struct,
            ManualOrderTimeoutRoutingStruct manualOrderTimeoutRoutingStruct)
    {
        super(MessageType.MANUAL_ORDER_TIMEOUT, routingParameterV2Struct);
        manualOrderTimeoutInfo = manualOrderTimeoutRoutingStruct;
        setRouteReasonStruct(manualOrderTimeoutRoutingStruct.orderRoutingStruct.routeReason);
        setOrder(manualOrderTimeoutRoutingStruct.orderRoutingStruct.order);
        orderHandlingStruct = manualOrderTimeoutRoutingStruct.orderHandlingStruct;
        setClassPostStation(getOrder().getSessionProductClass());
        setRestAsString(formatRestOfString());
    }

    private String formatRestOfString()
    {
        StringBuffer sb = new StringBuffer(100);
        sb.append("Timeout Type=[").append(getTimeoutRequestType()).append("], Time=[").append(
                Utility.toString(getTimeoutTime(), DATE_TIME_FORMAT)).append("] ");
        sb.append(getFormattedPrice());
        sb.append("Qty=[").append(volumeFormatter.format(getQuantity())).append("] ");
        int[] legs = getLegQuantities();
        int numLegs = legs.length;
        if (numLegs > 0)
        {
            sb.append("LegQtys=");
            for (int i = 0; i < numLegs; i++)
            {
                sb.append("[").append(volumeFormatter.format(legs[i])).append("]");
                sb.append(" ");
            }
        }
        sb.append("CBOEId=[").append(getCboeId()).append("] ");
        return sb.toString();
    }

    public int getProductKeyValue()
    {
        return manualOrderTimeoutInfo.orderRoutingStruct.order.productKey;
    }

    public String getSessionName()
    {
        return manualOrderTimeoutInfo.orderRoutingStruct.order.sessionNames[0];
    }

    public int getTimeoutRequestType()
    {
        return manualOrderTimeoutInfo.timeoutRequestType;
    }

    public ManualOrderTimeoutRoutingStruct getManualOrderTimeoutInfo()
    {
        return manualOrderTimeoutInfo;
    }

    public OrderHandlingInstructionStruct getOrderHandlingInstruction()
    {
        return orderHandlingStruct;
    }

    public int getQuantity()
    {
        return manualOrderTimeoutInfo.quantity;
    }

    public int[] getLegQuantities()
    {
        return manualOrderTimeoutInfo.legQuantities;
    }

    public DateTimeStruct getTimeoutTime()
    {
        return manualOrderTimeoutInfo.timeoutTime;
    }


    public String getFormattedPrice()
    {
        PriceStruct priceStruct = manualOrderTimeoutInfo.orderHandlingStruct.executionPrice;
        StringBuffer sb = new StringBuffer(20);
        sb.append("Price=[").append(priceStruct.whole).append('.')
                .append(priceStruct.fraction).append("] ");
        return sb.toString();
    }

    public boolean equals(Object o)
    {
        boolean isEqual = super.equals(o);

        if(isEqual)
        {
            if(o != null && o instanceof ManualOrderTimeoutMessageElement)
            {
                ManualOrderTimeoutMessageElement that = (ManualOrderTimeoutMessageElement) o;

                isEqual = getCboeId().getHighId() ==
                          that.getCboeId().getHighId()
                          && getCboeId().getLowId() ==
                             that.getCboeId().getLowId();
            }
        }

        return isEqual;
    }
}