//
// -----------------------------------------------------------------------------------
// Source file: OrderMessageElementImpl.java
//
// PACKAGE: com.cboe.presentation.omt;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.omt;

import java.util.*;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiConstants.ContingencyTypes;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.order.OrderRoutingStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.presentation.common.exchange.Exchange;
import com.cboe.interfaces.presentation.common.formatters.OrderFormatStrategy;
import com.cboe.interfaces.presentation.omt.MarketabilityIndicator;
import com.cboe.interfaces.presentation.omt.MessageCollection;
import com.cboe.interfaces.presentation.omt.MessageElement;
import com.cboe.interfaces.presentation.omt.OrderCancelMessageElement;
import com.cboe.interfaces.presentation.omt.OrderCancelReplaceMessageElement;
import com.cboe.interfaces.presentation.omt.OrderMessageElement;
import com.cboe.interfaces.presentation.order.LegOrderDetail;
import com.cboe.interfaces.presentation.order.Order;
import com.cboe.interfaces.presentation.order.OrderContingency;
import com.cboe.interfaces.presentation.order.OrderId;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.util.CBOEId;
import com.cboe.interfaces.presentation.validation.ValidationResult;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.formatters.FormatFactory;
import com.cboe.presentation.order.OrderFactory;

/**
 * An implementation of the <code>OrderMessageElement</code> interface, which allows access to details of any
 * order-related OMT message, such as order accepted or order canceled
 */
@SuppressWarnings({"BooleanMethodNameMustStartWithQuestion"})
public class OrderMessageElementImpl extends AbstractMessageElementImpl implements OrderMessageElement
{
    /**
     * Things that we do not include in our marketability assessment
     */
    public static final Set<Short> EXCLUDED_CONTINGENCIES;

    static
    {
        EXCLUDED_CONTINGENCIES = new HashSet<Short>(6);
        EXCLUDED_CONTINGENCIES.add(ContingencyTypes.OPG);
        EXCLUDED_CONTINGENCIES.add(ContingencyTypes.MIT);
        EXCLUDED_CONTINGENCIES.add(ContingencyTypes.STP);
        EXCLUDED_CONTINGENCIES.add(ContingencyTypes.STP_LOSS);
        EXCLUDED_CONTINGENCIES.add(ContingencyTypes.CLOSE);
        EXCLUDED_CONTINGENCIES.add(ContingencyTypes.STP_LIMIT);
    }

    
    private final Object cancelLockObject = new Object();
    private Order order;
    private final List<OrderCancelMessageElement> pendingCancelOperations = new ArrayList<OrderCancelMessageElement>(5);
    private String logString;
    private boolean infoMessageIndicator;
    private MarketabilityIndicator marketabilityIndicator = MarketabilityIndicator.UNKOWN;
    private boolean cancelBlockingInfoMessageIndicator; 
    private CurrentMarketStruct currentMarket;
    /**
     * Constructor invoked upon direct instantiation of this class by the Message Element Factory
     *
     * @param orderRoutingStruct
     * @param routingParameterV2Struct
     */
    protected OrderMessageElementImpl(OrderRoutingStruct orderRoutingStruct, RoutingParameterV2Struct routingParameterV2Struct)
    {
        this(orderRoutingStruct.order, routingParameterV2Struct, MessageElement.MessageType.ORDER_ACCEPTED);
        setRouteReasonStruct(orderRoutingStruct.routeReason);
        setClassPostStation(getOrder().getSessionProductClass());
    }

    /**
     * Constructor invoked as super from subclasses to send order info.
     *
     * @param order
     * @param routingParameterV2Struct
     * @param mType
     */
    protected OrderMessageElementImpl(OrderStruct order,
                                      RoutingParameterV2Struct routingParameterV2Struct,
                                      MessageType mType)
    {
        super(mType, routingParameterV2Struct);
        setOrder(order);
        infoMessageIndicator = false;
    }

    public boolean equals(Object o)
    {
        boolean isEqual = super.equals(o);

        if (isEqual)
        {
            if (o != null && o instanceof OrderMessageElement)
            {
                OrderMessageElement that = (OrderMessageElement)o;

                isEqual = getOrderId().equals(that.getOrderId());
            }
        }

        return isEqual;
    }

    @Override
    @SuppressWarnings({"CloneDoesntCallSuperClone"})
    public Object clone() throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException("OrderMessageElementImpl cannot be cloned.");
    }

    public Order getOrder()
    {
        return order;
    }

    public String getRestAsString()
    {
        return FormatFactory.getOrderFormatStrategy()
                .format(getOrder(), OrderFormatStrategy.BRIEF_INFO_NAME);
    }

    public OrderId getOrderId()
    {
        return getOrder().getOrderId();
    }

    public Object getKey()
    {
        return getOrder().getKey();
    }

    public String getActiveSession()
    {
        return getOrder().getActiveSession();
    }

    public SessionProductClass getSessionProductClass()
    {
        return getOrder().getSessionProductClass();
    }

    public SessionProduct getSessionProduct()
    {
        return getOrder().getSessionProduct();
    }

    public Character getSide()
    {
        return getOrder().getSide();
    }

    public Integer getOriginalQuantity()
    {
        return getOrder().getOriginalQuantity();
    }

    public Integer getTradedQuantity()
    {
        return getOrder().getTradedQuantity();
    }

    public Integer getCancelledQuantity()
    {
        return getOrder().getCancelledQuantity();
    }

    public Integer getLeavesQuantity()
    {
        return getOrder().getLeavesQuantity();
    }

    public Price getPrice()
    {
        return getOrder().getPrice();
    }

    public Character getTimeInForce()
    {
        return getOrder().getTimeInForce();
    }

    public OrderContingency getContingency()
    {
        return getOrder().getContingency();
    }

    public Price getAveragePrice()
    {
        return getOrder().getAveragePrice();
    }

    public Price getSessionAveragePrice()
    {
        return getOrder().getSessionAveragePrice();
    }

    public Integer getSessionTradedQuantity()
    {
        return getOrder().getSessionTradedQuantity();
    }

    public Character getPositionEffect()
    {
        return getOrder().getPositionEffect();
    }

    public String getUserAssignedId()
    {
        return getOrder().getUserAssignedId();
    }

    public ExchangeAcronym getOriginator()
    {
        return getOrder().getOriginator();
    }

    public String getAccount()
    {
        return getOrder().getAccount();
    }

    public String getSubaccount()
    {
        return getOrder().getSubaccount();
    }

    public String getUserId()
    {
        return getOrder().getUserId();
    }

    public Exchange getAwayExchange()
    {
        return getOrder().getAwayExchange();
    }

    public Integer getClassKey()
    {
        return getOrder().getClassKey();
    }

    public ExchangeFirm getCmta()
    {
        return getOrder().getCmta();
    }

    public Character getCoverage()
    {
        return getOrder().getCoverage();
    }

    public OrderId getCrossedOrder()
    {
        return getOrder().getCrossedOrder();
    }

    public DateTime getExpireTime()
    {
        return getOrder().getExpireTime();
    }

    public String getExtensions()
    {
        return getOrder().getExtensions();
    }

    public String getExtensionValue(String key)
    {
        return getOrder().getExtensionValue(key);
    }

    public LegOrderDetail[] getLegOrderDetails()
    {
        return getOrder().getLegOrderDetails();
    }

    public String getOptionalData()
    {
        return getOrder().getOptionalData();
    }

    public Short getOrderNBBOProtectionType()
    {
        return getOrder().getOrderNBBOProtectionType();
    }

    public Character getOrderOriginType()
    {
        return getOrder().getOrderOriginType();
    }

    public String getOrsId()
    {
        return getOrder().getOrsId();
    }

    public String getDisplayOrsId()
    {
        return getOrder().getDisplayOrsId();
    }

    public Integer getProductKey()
    {
        return getOrder().getProductKey();
    }

    public Short getProductType()
    {
        return getOrder().getProductType();
    }

    public DateTime getReceivedTime()
    {
        return getOrder().getReceivedTime();
    }

    public Integer getSessionCancelledQuantity()
    {
        return getOrder().getSessionCancelledQuantity();
    }

    public String[] getSessionNames()
    {
        return getOrder().getSessionNames();
    }

    public Character getSource()
    {
        return getOrder().getSource();
    }

    public Short getState()
    {
        return getOrder().getState();
    }

    @SuppressWarnings({"deprecation"})
    public OrderStruct getStruct()
    {
        return getOrder().getStruct();
    }

    public Integer getTransactionSequenceNumber()
    {
        return getOrder().getTransactionSequenceNumber();
    }

    public ExchangeAcronym getUserAcronym()
    {
        return getOrder().getUserAcronym();
    }

    @SuppressWarnings({"NonBooleanMethodNameMayNotStartWithQuestion"})
    public Boolean isCross()
    {
        return getOrder().isCross();
    }

    public boolean isCreatedFromStruct()
    {
        return true;
    }

    public boolean isNewOrder()
    {
        return false;
    }

    public ValidationResult validate()
    {
        throw new UnsupportedOperationException("Not valid for an OrderMessageElement.");
    }

    protected void setOrder(OrderStruct orderStruct)
    {
        if (orderStruct == null)
        {
            throw new IllegalArgumentException();
        }
        order = OrderFactory.createOrder(orderStruct);

    }

    public OrderCancelMessageElement[] getPendingCancelOperations()
    {
        synchronized(pendingCancelOperations)
        {
            return pendingCancelOperations.toArray(new OrderCancelMessageElement[pendingCancelOperations.size()]);
        }
    }


    public void addCancelRequest(OrderCancelMessageElement element)
    {
        synchronized(pendingCancelOperations)
        {
            pendingCancelOperations.add(element);
        }
    }

    public void addCancelReplaceRequest(OrderCancelReplaceMessageElement element)
    {
        synchronized(pendingCancelOperations)
        {
            pendingCancelOperations.add(element);
        }
    }

    public boolean hasAnyPendingCancelOperations()
    {
        synchronized(pendingCancelOperations)
        {
            return pendingCancelOperations.size() > 0;
        }
    }

    @SuppressWarnings({"MethodWithMultipleReturnPoints"})
    public boolean hasCancelPending()
    {
        synchronized(pendingCancelOperations)
        {
            for(OrderCancelMessageElement element : pendingCancelOperations)
            {
                if(!(element instanceof OrderCancelReplaceMessageElement))
                {
                    return true;
                }
            }
            return false;
        }
    }

    @SuppressWarnings({"MethodWithMultipleReturnPoints"})
    public boolean hasCancelReplacePending()
    {
        synchronized(pendingCancelOperations)
        {
            for(OrderCancelMessageElement element : pendingCancelOperations)
            {
                if(element instanceof OrderCancelReplaceMessageElement)
                {
                    return true;
                }
            }
            return false;
        }
    }

    public OrderCancelMessageElement[] getCancels()
    {
        List<OrderCancelMessageElement> list = new ArrayList<OrderCancelMessageElement>(1);

        synchronized(pendingCancelOperations)
        {
            for(OrderCancelMessageElement element : pendingCancelOperations)
            {
                if(!(element instanceof OrderCancelReplaceMessageElement))
                {
                    list.add((OrderCancelMessageElement)element);
                }
            }
            return list.toArray(new OrderCancelMessageElement[list.size()]);
        }
    }

    public OrderCancelReplaceMessageElement[] getCancelReplaces()
    {
        List<OrderCancelReplaceMessageElement> list = new ArrayList<OrderCancelReplaceMessageElement>(1);

        synchronized(pendingCancelOperations)
        {
            for(OrderCancelMessageElement element : pendingCancelOperations)
            {
                if(element instanceof OrderCancelReplaceMessageElement)
                {
                    list.add((OrderCancelReplaceMessageElement)element);
                }
            }
            return list.toArray(new OrderCancelReplaceMessageElement[list.size()]);
        }
    }

    /**
     * Copies all pending cancels and cancel/replaces from source order to this order
     * @param source
     */
    public void copyPendingCancelOperations(OrderMessageElement source)
    {
        synchronized(pendingCancelOperations)
        {
            pendingCancelOperations.clear();
            for(OrderCancelMessageElement cancelElement : source.getPendingCancelOperations())
            {
                addCancelRequest(cancelElement);
            }
        }
    }

    /**
     * To remove either a cancel or a cancel/re from the collection of pending cxl/cxlre operations
     */
    public boolean removeCancelOperation(OrderCancelMessageElement element)
    {
        synchronized(pendingCancelOperations)
        {
            return pendingCancelOperations.remove(element);
        }
    }

    public void applyCancelOperation(OrderCancelMessageElement aCancelElement) throws UserException
    {
            if(aCancelElement instanceof OrderCancelReplaceMessageElement)
            {
                APIHome.findOrderManagementTerminalAPI().acceptManualCancelReplace((OrderCancelReplaceMessageElement) aCancelElement);
            }
            else
            {
                APIHome.findOrderManagementTerminalAPI().acceptManualCancel(aCancelElement);
            }
            removeCancelOperation(aCancelElement);
    }

    public boolean setAllInfoMessageIndicators()
    {
        MessageCollection infoMessageCollection = MessageCollectionFactory.getInfoMessagesCollection();
        boolean indicatorOnEntry = infoMessageIndicator;  //save current value for comparison later

        infoMessageIndicator = false;
        cancelBlockingInfoMessageIndicator = false;

        if(!(infoMessageCollection.findElements(getCboeId()).isEmpty()))
        {
            infoMessageIndicator = true;
            //Look for any info messages that might cause auto cancel to be invalid
            /*
            if(!(MessageCollectionHelper.getFillRejectMessages(getOrder()).isEmpty()))
            {
                cancelBlockingInfoMessageIndicator = true;
            }
            else if(!(MessageCollectionHelper.getOrderTimeoutMessages(getOrder()).isEmpty()))
            {
                cancelBlockingInfoMessageIndicator = true;
            }
            */
            // SEDL SYS007700: auto cancel is invalid if there are ANY info messages, regardless of type.
            cancelBlockingInfoMessageIndicator = true;
        }
        return indicatorOnEntry != infoMessageIndicator;  //returns true if indicator changed
    }

    public boolean hasInfoMessages()
    {
        return MessageCollectionHelper.hasInfoMessages(getCboeId());
    }

    public boolean getInfoMessageIndicator()
    {
        return infoMessageIndicator;
    }

    public void setInfoMessageIndicator(boolean indicator){
        infoMessageIndicator = indicator;
    }

    public boolean hasCancelBlockingInfoMessages()
    {
        return cancelBlockingInfoMessageIndicator;
    }

    public void applyCancels() throws UserException
    {
        /**
         * Synchronize on cancel lock object to ensure all API calls complete first. This ensures all
         * pending cancels are removed from the order element's list before the list is copied into the new
         * updated order that results from the API call.
         */
        synchronized(getCancelLockObject())
        {
            for(OrderCancelMessageElement cancelElement : getPendingCancelOperations())
            {

                if(cancelElement instanceof OrderCancelReplaceMessageElement)
                {
                    APIHome.findOrderManagementTerminalAPI().acceptManualCancelReplace(
                            (OrderCancelReplaceMessageElement) cancelElement);
                }
                else
                {
                    APIHome.findOrderManagementTerminalAPI().acceptManualCancel(cancelElement);
                }
                removeCancelOperation(cancelElement);
            }
        }
    }

    public Object getCancelLockObject()
    {
        return cancelLockObject;
    }

    public String getSessionName()
    {
        return getSessionProduct().getTradingSessionName();
    }


    public int getProductKeyValue()
    {
        return getProductKey();
    }

    public CBOEId getCboeId()
    {
        return getOrderId().getCboeId();
    }

    public String getGiveUpFirm()
    {
        return getOrderId().getExecutingOrGiveUpFirm().getFirm();
    }

    public String getCorrespondentFirm()
    {
        return getOrderId().getCorrespondentFirm();
    }

    public String getBranchSeqNum()
    {
        return getOrderId().getFormattedBranchSequence();
    }

    public String getExpiration(boolean fullFormat) {
        if (getOrder().getSessionProduct().getLeapIndicator()) {
            if(fullFormat){
                return "Leap";
            }
            else{
                return "L";
            }

        } else {
            if (fullFormat) {
                return getOrder().getSessionProduct().getExpirationType().toString();
            } else {
                return getOrder().getSessionProduct().getExpirationType().toChar() + "";
            }

        }
    }

    public String getProductName()
    {
        return FormatFactory.getFormattedProduct(getOrder().getSessionProduct());
    }

    @SuppressWarnings({"HardcodedFileSeparator"})

    public MarketabilityIndicator getMarketabilityIndicator()
    {
        return marketabilityIndicator;
    }
    
    public void setMarketabilityIndicator(MarketabilityIndicator indicator) {
        marketabilityIndicator = indicator;
    }
    
    public CurrentMarketStruct getCurrentMarket() {
        return currentMarket;
    }

    

    /**
     * sets the current market value for this message.
     * 
     * @see com.cboe.interfaces.presentation.omt.OrderMessageElement#setCurrentMarket(com.cboe.idl.marketData.InternalCurrentMarketStruct)
     */
    public void setCurrentMarket(CurrentMarketStruct currentMarket) {
        this.currentMarket = currentMarket;
    }


    public String getLogString()
    {
        if (logString == null)
        {
            StringBuilder buffer = new StringBuilder(getType().toString());
            buffer.append(" Msg Id: [").append(String.valueOf(getMessageId())).append("] ");
            buffer.append("CBOE Id: [").append(String.valueOf(getCboeId().getHighId())).
                    append(":").append(String.valueOf(getCboeId().getLowId())).append("] ");
            buffer.append("ORSID: [").append(getOrsId()).append("] ");
            buffer.append("Br/Seq: [").append(getOrderId().getFormattedBranchSequence())
                    .append("] ");
            buffer.append("GiveUp Firm: [").append(getGiveUpFirm()).append("] ");
            buffer.append("Corr. Firm: [").append(getCorrespondentFirm()).append("] ");
            buffer.append("Date: [").append(getOrderId().getOrderDate()).append("] ");
            logString = buffer.toString();
        }
        return logString;
    }

}
