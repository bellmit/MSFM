//
// -----------------------------------------------------------------------------------
// Source file: InfoMessageElementImpl.java
//
// PACKAGE: com.cboe.presentation.omt;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.omt;

import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiOrder.OrderDetailStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;
import com.cboe.interfaces.presentation.omt.InfoMessageElement;
import com.cboe.interfaces.presentation.omt.MessageElement;
import com.cboe.interfaces.presentation.order.Order;
import com.cboe.interfaces.presentation.order.OrderId;
import com.cboe.interfaces.presentation.order.OrderDetail;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.util.CBOEId;
import com.cboe.presentation.common.formatters.FormatFactory;
import com.cboe.presentation.common.formatters.Utility;
import com.cboe.presentation.order.OrderFactory;
import com.cboe.presentation.order.OrderIdFactory;
import com.cboe.presentation.order.OrderDetailFactory;
import com.cboe.presentation.util.CBOEIdImpl;
import com.cboe.exceptions.*;

/**
 * An implementation of the <code>InfoMessageElement</code> interface,
 * which formats the message content as a String concatenated to other Strings created
 * by all OMT message types (like date-time and type)
 */
public class InfoMessageElementImpl
        extends AbstractMessageElementImpl
        implements InfoMessageElement
{
    private String restAsString;
    private String logString;
    private OrderId orderId;
    private Order order;
    private OrderDetail orderDetail;
    private String displayOrsId;

    //super constructor for subclasses created with message type and routing structure
    protected InfoMessageElementImpl(MessageType msgType, RoutingParameterV2Struct routingParameterV2Struct)
    {
        super(msgType, routingParameterV2Struct);
    }

    protected InfoMessageElementImpl(String text)
    {
        super(MessageElement.MessageType.TEXT);
        restAsString = text;
    }

    public String getRestAsString()
    {
        return restAsString;
    }

    protected void setRestAsString(String restAsString)
    {
        this.restAsString = restAsString;
    }

    public short getRouteReason()
    {
        return MessageElement.NO_ROUTING_REASON_AVAILABLE;
    }

    public String getRouteDescription()
    {
        return MessageElement.NO_ROUTING_DESCRIPTION_AVAILABLE;
    }

    public String getSessionName()
    {
        return MessageElement.NO_SESSION_NAME_AVAILABLE;
    }

    public int getProductKeyValue()
    {
        return MessageElement.NO_PRODUCT_KEY_AVAILABLE;
    }

    public CBOEId getCboeId()
    {
        if (orderId != null)
        {
            return orderId.getCboeId();
        }
        else
        {
            return new CBOEIdImpl(new CboeIdStruct(0,0));
        }    
    }

    public String getLogString()
    {
        if (logString == null)
        {
            StringBuilder buffer = new StringBuilder(getType().toString());
            buffer.append(" Msg Id: [").append(String.valueOf(getMessageId())).append("] ");
            buffer.append("CBOE Id: [").append(String.valueOf(getCboeId().getHighId())).
                    append(':').append(String.valueOf(getCboeId().getLowId())).append("] ");
            buffer.append(getRestAsString());
            logString = buffer.toString();
        }
        return logString;
    }

    /**
     * Overridden in info message subclasses that have access to an order ID to get BR/SEQ
     */
    public String getBranchSeqNum()
    {
        if(orderId != null)
        {
            return orderId.getFormattedBranchSequence();
        }
        else
        {
            return NOT_AVAILABLE;

        }
    }

    public String getExpiration(boolean fullFormat) {
        if (order != null) {
            if(order.getSessionProduct().getLeapIndicator()){
                if(fullFormat){
                    return "Leap";
                }
                else{
                    return "L";
                }
            }
            else {
                if (fullFormat) {
                    return order.getSessionProduct().getExpirationType().toString();                    
                }
                else{
                    return order.getSessionProduct().getExpirationType().toChar() + "";
                }
            }
        } else {
            return NOT_AVAILABLE;
        }
    }

    /**
     * Overridden in info message subclasses that have access to an order ID to get BR/SEQ
     */
    public String getGiveUpFirm()
    {
        if (orderId != null)
        {
            return orderId.getExecutingOrGiveUpFirm().getFirm();
        }
        else
        {
            return NOT_AVAILABLE;
        }    
    }

    public String getCorrespondentFirm()
    {
        if(orderId != null)
        {
            return orderId.getCorrespondentFirm();
        }
        else
        {
            return NOT_AVAILABLE;
        }
    }


    public String getOrsId()
    {
        if(order != null)
        {
            return order.getOrsId();
        }
        else
        {
            return NOT_AVAILABLE;
        }
    }

    public String getDisplayOrsId()
    {
        if(displayOrsId == null)
        {
            if(order != null)
            {
                displayOrsId = order.getDisplayOrsId();
            }
            else
            {
                displayOrsId = NOT_AVAILABLE;
            }
        }
        return displayOrsId;
    }

    public String getProductName()
    {
        SessionProduct sp;
        if(order != null)
        {
            sp = order.getSessionProduct();
        }
        else
        {
            sp = Utility.getProductByKeyForSession(getSessionName(), getProductKeyValue());
        }
        if(sp != null)
        {
            return FormatFactory.getFormattedProduct(sp);
        }
        else
        {
            return NOT_AVAILABLE;
        }
    }

    public final OrderId getOrderId()
    {
        return orderId;
    }


    protected final void setOrderId(OrderId orderId)
    {
        this.orderId = orderId;
    }

    protected final void setOrderId(OrderIdStruct orderIdStruct)
    {
        setOrderId(OrderIdFactory.createOrderId(orderIdStruct));
    }

    protected final void setOrder(Order order)
    {
        this.order = order;
        this.orderId = order.getOrderId();
    }

    protected final void setOrder(OrderStruct orderStruct)
    {
        setOrder(OrderFactory.createOrder(orderStruct));
    }

    public OrderDetail getOrderDetail() throws CommunicationException,
            AuthorizationException, DataValidationException, NotFoundException, SystemException
    {
        if (orderDetail == null)
        {
            orderDetail = OrderDetailFactory.createOrderDetail(getOrderDetailStruct());
            // this is kind of ugly, but resetting this.order to the instance
            // contained in the orderDetail so we don't waste memory keeping
            // two identical instances of the order
            setOrder(orderDetail.getOrder());
        }
        return orderDetail;
    }

    public OrderDetailStruct getOrderDetailStruct() throws CommunicationException,
            AuthorizationException, DataValidationException, NotFoundException, SystemException
    {
        OrderDetailStruct orderDetailStruct = OrderFactory.buildOrderDetailStruct(getOrderStruct());
        return orderDetailStruct;
    }

    public OrderStruct getOrderStruct()
    {
        OrderStruct retVal = null;
        if (getOrder() != null)
        {
            retVal = getOrder().getStruct();
        }
        return retVal;
    }

    public final Order getOrder()
    {
        return order;
    }
}
