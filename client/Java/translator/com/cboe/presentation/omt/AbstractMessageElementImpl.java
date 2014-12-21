//
// -----------------------------------------------------------------------------------
// Source file: AbstractMessageElementImpl.java
//
// PACKAGE: com.cboe.presentation.omt;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.omt;

import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.util.RouteReasonStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;

import com.cboe.interfaces.presentation.common.formatters.ProductClassFormatStrategy;
import com.cboe.interfaces.presentation.omt.FormattableDateTime;
import com.cboe.interfaces.presentation.omt.MessageElement;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.SessionProduct;

import com.cboe.presentation.common.dateTime.FormattableDateTimeImpl;
import com.cboe.presentation.common.formatters.FormatFactory;
import com.cboe.presentation.common.formatters.Utility;

/**
 * An abstract implementation of the <code>MessageElement</code> interface,
 * which allows access to common parts of all OMT messages, such date-time and message type
 */
public abstract class AbstractMessageElementImpl implements MessageElement
{
    private MessageType msgType;
    private FormattableDateTime receivedAt;
    private RoutingParameterV2Struct routingParameterV2Struct;
    private RouteReasonStruct routeReasonStruct;
    private String classPostStation = NO_CLASS_POST_STATION_AVAILABLE;
    private int messageNumber;

    protected AbstractMessageElementImpl(MessageType type)
    {
        this();
        msgType = type;
    }

    protected AbstractMessageElementImpl(MessageType type, RoutingParameterV2Struct routingParameterV2Struct)
    {
        this(type);
        this.routingParameterV2Struct = routingParameterV2Struct;
        this.messageNumber =  0;
    }

    protected AbstractMessageElementImpl()
    {
        receivedAt = new FormattableDateTimeImpl();
    }

    public boolean equals(Object o)
    {
        boolean isEqual = super.equals(o);

        if(!isEqual)
        {
            if(o != null && o instanceof MessageElement)
            {
                MessageElement that = (MessageElement) o;

                isEqual = getType() == that.getType();
                if (isEqual)
                {

                    isEqual = getSessionName().equals(that.getSessionName());
                    if (isEqual)
                    {
                        isEqual = getProductKeyValue() == that.getProductKeyValue();
                        if (isEqual)
                        {
                            isEqual = getMessageId() == that.getMessageId();
                        }
                    }
                }
            }
        }
        return isEqual;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(50);
        sb.append("AbstractMessageElementImpl");
        sb.append("{receivedDate=").append(getReceivedDate());
        sb.append(", type=").append(getType());
        sb.append('}');
        return sb.toString();
    }

    public FormattableDateTime getReceivedDate()
    {
        return receivedAt;
    }

    public MessageType getType()
    {
        return msgType;
    }

    public abstract String getSessionName();
    public abstract int getProductKeyValue();
    
    @SuppressWarnings({"MethodWithMultipleReturnPoints"})
    public String getRouteSource()
    {
        if(routingParameterV2Struct != null)
        {
            return routingParameterV2Struct.source;
        }
        else
        {
            return MessageElement.NO_SOURCE_DESCRIPTION_AVAILABLE;
        }
    }

    @SuppressWarnings({"MethodWithMultipleReturnPoints"})
    public short getRouteSourceType()
    {
        if(routingParameterV2Struct != null)
        {
            return routingParameterV2Struct.sourceType;
        }
        else
        {
            return MessageElement.NO_SOURCE_AVAILABLE;
        }
    }

    public DateTimeStruct getRouteTime()
    {
        DateTimeStruct routeTime = NO_ROUTE_TIME_AVAILABLE;
        if (routeReasonStruct != null)
        {
            routeTime = routeReasonStruct.routeTime;
        }
        return routeTime;
    }

    public long getMessageId()
    {
        long messageId = NO_MESSAGE_ID_AVAILABLE;
        if(routeReasonStruct != null)
        {
            messageId = routeReasonStruct.messageId;
        }
        return messageId;
    }

    public short getRouteReason()
    {
        short routeReason = MessageElement.NO_ROUTING_REASON_AVAILABLE;
        if (routeReasonStruct != null)
        {
            routeReason = routeReasonStruct.routeReason;
        }
        return routeReason;
    }

    public String getRouteDescription()
    {
        String reasonDescription  = MessageElement.NO_ROUTING_DESCRIPTION_AVAILABLE;
        if(routeReasonStruct != null)
        {
            reasonDescription = routeReasonStruct.routeDescription;
        }
        return reasonDescription;
    }

    public RouteReasonStruct getRouteReasonStruct()
    {
        return routeReasonStruct;
    }

    public void setRouteReasonStruct(RouteReasonStruct source)
    {
        if(source == null)
        {
            throw new IllegalArgumentException(
                    "Null RouteReasonStruct in setRouteReasonStruct method");
        }
        routeReasonStruct = source;
    }

    public String getClassPostStation()
    {
        return classPostStation;
    }

    protected void setClassPostStation(ProductClass productClass)
    {
        if (productClass != null)
        {
            classPostStation = FormatFactory.getProductClassFormatStrategy()
                .format(productClass, ProductClassFormatStrategy.CLASS_POST_STN_NAME);
        }
    }

    protected ProductClass getProductClass(String sessionName, int productKey)
    {
        ProductClass productClass = null;
        SessionProduct sp = Utility.getProductByKeyForSession(sessionName, productKey);
        if (sp != null)
        {
            int classKey = sp.getProductKeysStruct().classKey;
            productClass = getProductClass(classKey);
        }    
        return productClass;
    }

    protected ProductClass getProductClass(int classKey)
    {
        return Utility.getProductClass(classKey);  
    }

    public String getGiveUpFirm()
    {
        return NOT_AVAILABLE;
    }

    public String getCorrespondentFirm()
    {
        return NOT_AVAILABLE;
    }

    public String getBranchSeqNum()
    {
        return NOT_AVAILABLE;
    }

    public String getExpiration(boolean fullFormat)
    {
        return NOT_AVAILABLE;    
    }

    public String getOrsId()
    {
        return NOT_AVAILABLE;
    }

    public String getDisplayOrsId()
    {
        return NOT_AVAILABLE;
    }

    public String getProductName()
    {
        return NOT_AVAILABLE;
    }

    public int getMessageNumber()
    {
        return messageNumber;
    }

    public void setMessageNumber(int msgNumber)
    {
        messageNumber = msgNumber;
    }
}
