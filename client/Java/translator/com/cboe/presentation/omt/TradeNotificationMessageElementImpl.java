//
// -----------------------------------------------------------------------------------
// Source file: TradeNotificationMessageElementImpl.java
//
// PACKAGE: com.cboe.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.omt;

import com.cboe.idl.cmiConstants.TradeNotificationStates;
import com.cboe.idl.cmiTradeNotification.TradeNotificationStruct;
import com.cboe.idl.util.RouteReasonStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;

import com.cboe.interfaces.presentation.omt.MessageElement;
import com.cboe.interfaces.presentation.omt.TradeNotificationMessageElement;

import com.cboe.presentation.common.formatters.TradeNotificationFormatter;

public class TradeNotificationMessageElementImpl extends InfoMessageElementImpl implements TradeNotificationMessageElement
{
    private TradeNotificationStruct tradeNotificationStruct = null;
    private TradeNotificationFormatter formatter = new TradeNotificationFormatter();

    protected TradeNotificationMessageElementImpl(TradeNotificationStruct struct,
                                                  RouteReasonStruct routeReasonStruct,
                                                  RoutingParameterV2Struct routingParameterV2Struct)
    {
        super(MessageElement.MessageType.TRADE_NOTIFICATION, routingParameterV2Struct);
        this.tradeNotificationStruct = struct;
        setRouteReasonStruct(routeReasonStruct);
        setClassPostStation(getProductClass(tradeNotificationStruct.prodKeysStruct.classKey));
        setRestAsString(formatter.format(struct));
    }

    public TradeNotificationStruct getTradeNotificationStruct()
    {
        return tradeNotificationStruct;
    }

    public void setTradeNotificationStruct(TradeNotificationStruct tradeNotificationStruct)
    {
        this.tradeNotificationStruct = tradeNotificationStruct;
    }

    public boolean equals(Object o)
    {
        boolean isEqual = super.equals(o);

        if(isEqual)
        {
            if(o != null && o instanceof TradeNotificationMessageElement)
            {
                TradeNotificationMessageElement that = (TradeNotificationMessageElement) o;

                isEqual = getTradeNotificationStruct().tradeNotificationId.equals(that.getTradeNotificationStruct().tradeNotificationId);
            }
        }

        return isEqual;
    }


    @Override
    public short getRouteReason()
    {
    	if(getRouteReasonStruct()!= null)
    	{
    		return getRouteReasonStruct().routeReason;
    	}
    	else
    	{
            return MessageElement.NO_ROUTING_REASON_AVAILABLE;
    	}
    }

    @Override
    public String getRouteDescription()
    {
        String result = "Unknown";
        switch(getRouteReasonStruct().routeReason)
        {
            case TradeNotificationStates.REJECT:
                result = "ETN Reject";
                break;
            case TradeNotificationStates.ACK_TIMEOUT:
            case TradeNotificationStates.BUNDLE_TIMEOUT:
                result = "ETN Timeout";
                break;

        }
        return result;
    }

    @Override
    public int getProductKeyValue()
    {
        return tradeNotificationStruct.prodKeysStruct.productKey;
    }

    @Override
    public String getSessionName()
    {
        return tradeNotificationStruct.sessionName;
    }

}
