//
// -----------------------------------------------------------------------------------
// Source file: ManualFillTimeoutMessageElementImpl.java
//
// PACKAGE: com.cboe.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.omt;

import com.cboe.idl.order.ManualFillRoutingStruct;
import com.cboe.idl.order.ManualFillStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;

import com.cboe.interfaces.presentation.omt.ManualFillTimeoutMessageElement;

@SuppressWarnings({"HardcodedFileSeparator"})
public class ManualFillTimeoutMessageElementImpl extends InfoMessageElementImpl
    implements ManualFillTimeoutMessageElement
{
    private ManualFillStruct manualFillInfo;
    private short timeoutRequestType;

    protected ManualFillTimeoutMessageElementImpl(
            RoutingParameterV2Struct routingParameterV2Struct,
            ManualFillRoutingStruct fillRoutingStruct, short timeoutRequestType)
    {
        super(MessageType.MANUAL_FILL_TIMEOUT, routingParameterV2Struct);
        manualFillInfo = fillRoutingStruct.manualFillInfo;
        this.timeoutRequestType = timeoutRequestType;
        setRouteReasonStruct(fillRoutingStruct.routeReason);
        setOrderId(manualFillInfo.orderId);
        setClassPostStation(getProductClass(getSessionName(), getProductKeyValue()));
        setRestAsString(formatRestOfString());
    }

    private String formatRestOfString()
    {
        StringBuilder sb = new StringBuilder(30);
        sb.append("TimeoutType=[").append(getTimeoutRequestType()).append("] ");
        sb.append("CBOEId=[").append(getCboeId()).append("] ");
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

    public short getRouteReason()
    {
        return getRouteReasonStruct().routeReason;
    }

    public String getRouteDescription()
    {
        return getRouteReasonStruct().routeDescription;
    }

    public int getTimeoutRequestType()
    {
        return timeoutRequestType;
    }

    public ManualFillStruct getManualFillInfo()
    {
        return manualFillInfo;
    }

    public boolean equals(Object o)
    {
        boolean isEqual = super.equals(o);

        if(isEqual)
        {
            if(o != null && o instanceof ManualFillTimeoutMessageElement)
            {
                ManualFillTimeoutMessageElement that = (ManualFillTimeoutMessageElement) o;

                isEqual = getCboeId().getHighId() ==
                          that.getCboeId().getHighId()
                          && getCboeId().getLowId() ==
                             that.getCboeId().getLowId();
            }
        }
        return isEqual;
    }
}