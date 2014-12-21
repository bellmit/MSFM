//
// -----------------------------------------------------------------------------------
// Source file: OrderMessageElement.java
//
// PACKAGE: com.cboe.interfaces.presentation.omt;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.omt;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiQuote.QuoteStruct;
import com.cboe.idl.marketData.InternalCurrentMarketStruct;
import com.cboe.idl.util.RouteReasonStruct;
import com.cboe.interfaces.presentation.order.Order;

/**
 * Interface for access to various parts of all order-related OMT messages.
 */

@SuppressWarnings({"BooleanMethodNameMustStartWithQuestion"})
public interface OrderMessageElement extends MessageElement, Order
{
    Order getOrder();

    void addCancelRequest(OrderCancelMessageElement element);

    void addCancelReplaceRequest(OrderCancelReplaceMessageElement element);

    void copyPendingCancelOperations(OrderMessageElement source);
    boolean hasCancelPending();

    boolean hasCancelReplacePending();

    boolean hasAnyPendingCancelOperations();
    boolean removeCancelOperation(OrderCancelMessageElement element);

    boolean setAllInfoMessageIndicators();
    boolean hasInfoMessages();
    boolean getInfoMessageIndicator();
    void setInfoMessageIndicator(boolean indicator);
    //info messages that prevent auto cancel from being applied
    boolean hasCancelBlockingInfoMessages();

    void applyCancels() throws UserException;
    OrderCancelMessageElement[] getPendingCancelOperations();
    OrderCancelMessageElement[] getCancels();

    OrderCancelReplaceMessageElement[] getCancelReplaces();

    Object getCancelLockObject();

    RouteReasonStruct getRouteReasonStruct();

    void setRouteReasonStruct(RouteReasonStruct source);

    MarketabilityIndicator getMarketabilityIndicator();
    
    void setMarketabilityIndicator(MarketabilityIndicator indicator);
    
    
    CurrentMarketStruct getCurrentMarket();
    
    /**
     * Sets the current market for {@link MarketabilityIndicator} and {@link QuoteStruct}
     * 
     * @param internalCurrentMarket
     *            a non-null current market structure
     */
    void setCurrentMarket(CurrentMarketStruct internalCurrentMarket);
    
}
