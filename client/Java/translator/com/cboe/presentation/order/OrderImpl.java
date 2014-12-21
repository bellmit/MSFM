//
// -----------------------------------------------------------------------------------
// Source file: OrderImpl.java
//
// PACKAGE: com.cboe.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.order;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiConstants.ContingencyTypes;
import com.cboe.idl.cmiConstants.OrderNBBOProtectionTypes;
import com.cboe.idl.cmiConstants.OrderOrigins;
import com.cboe.idl.cmiConstants.StatusUpdateReasons;
import com.cboe.idl.cmiOrder.OrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.presentation.common.exchange.Exchange;
import com.cboe.interfaces.presentation.order.MutableOrder;
import com.cboe.interfaces.presentation.order.MutableOrderId;
import com.cboe.interfaces.presentation.order.Order;
import com.cboe.interfaces.presentation.order.OrderContingency;
import com.cboe.interfaces.presentation.order.OrderId;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

class OrderImpl extends AbstractOrder implements MutableOrder
{
    public OrderImpl()
    {
        this(OrderFactory.createDefaultOrderStruct());
        newOrder = true;
    }

    public OrderImpl(OrderStruct orderStruct)
    {
        super(orderStruct);
    }

    /**
     * Implements Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        Order newImpl;
        if(isCreatedFromStruct())
        {
            newImpl = OrderFactory.createOrder(getStruct());
        }
        else
        {
            newImpl = OrderFactory.createOrder();
        }
        return newImpl;
    }

    public MutableOrderId getMutableOrderId()
    {
        return OrderIdFactory.convertToMutable(getOrderId());
    }

    public void setAwayExchange(Exchange exchange)
    {
        Exchange oldValue = getAwayExchange();
        _setAwayExchange(exchange);
        setModified();
        firePropertyChange(PROPERTY_AWAY_EXCHANGE, oldValue, exchange);
    }

    public void setOrderId(OrderId newValue)
    {
        Object oldValue = getOrderId();
        _setOrderId(newValue);
        setModified();
        firePropertyChange(PROPERTY_ORDER_ID, oldValue, newValue);
    }

    public void setOriginator(ExchangeAcronym newValue)
    {
        ExchangeAcronym oldValue = getOriginator();
        _setOriginator(newValue);
        setModified();
        firePropertyChange(PROPERTY_ORIGINATOR, oldValue, newValue);
    }

    public void setOriginalQuantity(Integer newValue)
    {
        Integer oldValue = getOriginalQuantity();
        _setOriginalQuantity(newValue);
        setModified();
        firePropertyChange(PROPERTY_ORIGINAL_QUANTITY, oldValue, newValue);
    }

    public void setProductKey(Integer newValue)
    {
        Integer oldValue = getProductKey();
        _setProductKey(newValue);
        setModified();
        firePropertyChange(PROPERTY_PRODUCT_KEY, oldValue, newValue);
    }

    public void setSide(Character newValue)
    {
        Character oldValue = getSide();
        _setSide(newValue);
        setModified();
        firePropertyChange(PROPERTY_SIDE, oldValue, newValue);
    }

    public void setPrice(Price newValue)
    {
        Price oldValue = getPrice();
        _setPrice(newValue);
        setModified();
        firePropertyChange(PROPERTY_PRICE, oldValue, newValue);
    }

    public void setTimeInForce(Character newValue)
    {
        Character oldValue = getTimeInForce();
        _setTimeInForce(newValue);
        setModified();
        firePropertyChange(PROPERTY_TIME_IN_FORCE, oldValue, newValue);
    }

    public void setExpireTime(DateTime newValue)
    {
        DateTime oldValue = getExpireTime();
        _setExpireTime(newValue);
        setModified();
        firePropertyChange(PROPERTY_EXPIRE_TIME, oldValue, newValue);
    }

    public void setContingency(OrderContingency newValue)
    {
        OrderContingency oldValue = getContingency();
        _setContingency(newValue);
        setModified();
        firePropertyChange(PROPERTY_CONTINGENCY, oldValue, newValue);
        updateOrderNBBOProtectionType();
    }

    public void setCmta(ExchangeFirm newValue)
    {
        ExchangeFirm oldValue = getCmta();
        _setCmta(newValue);
        setModified();
        firePropertyChange(PROPERTY_CMTA, oldValue, newValue);
    }

    public void setExtensions(String newValue)
    {
        String oldValue = getExtensions();
        _setExtensions(newValue);
        setModified();
        firePropertyChange(PROPERTY_EXTENSIONS, oldValue, newValue);
    }

    public void setExtensionValue(String key, String value)
    {
        String oldValue = getExtensions();
        setExtensionFieldValue(key, value);
        String newValue = getExtensions();
        setModified();
        firePropertyChange(PROPERTY_EXTENSIONS, oldValue, newValue);
    }

    public void setAccount(String newValue)
    {
        String oldValue = getAccount();
        _setAccount(newValue);
        setModified();
        firePropertyChange(PROPERTY_ACCOUNT, oldValue, newValue);
    }

    public void setSubaccount(String newValue)
    {
        String oldValue = getSubaccount();
        _setSubaccount(newValue);
        setModified();
        firePropertyChange(PROPERTY_SUB_ACCOUNT, oldValue, newValue);
    }

    public void setPositionEffect(Character newValue)
    {
        Character oldValue = getPositionEffect();
        _setPositionEffect(newValue);
        setModified();
        firePropertyChange(PROPERTY_POSITION_EFFECT, oldValue, newValue);
    }

    public void setCross(Boolean newValue)
    {
        Boolean oldValue = isCross();
        _setCross(newValue);
        setModified();
        firePropertyChange(PROPERTY_CROSS, oldValue, newValue);
    }

    public void setOrderOriginType(Character newValue)
    {
        Character oldValue = getOrderOriginType();
        _setOrderOriginType(newValue);
        setModified();
        firePropertyChange(PROPERTY_ORDER_ORIGIN_TYPE, oldValue, newValue);
        updateOrderNBBOProtectionType();
    }

    public void setCoverage(Character newValue)
    {
        Character oldValue = getCoverage();
        _setCoverage(newValue);
        setModified();
        firePropertyChange(PROPERTY_COVERAGE, oldValue, newValue);
    }

    public void setOptionalData(String newValue)
    {
        String oldValue = getOptionalData();
        _setOptionalData(newValue);
        setModified();
        firePropertyChange(PROPERTY_OPTIONAL_DATA, oldValue, newValue);
    }

    public void setUserId(String newValue)
    {
        String oldValue = getUserId();
        _setUserId(newValue);
        setModified();
        firePropertyChange(PROPERTY_USER_ID, oldValue, newValue);
    }

    public void setUserAcronym(ExchangeAcronym newValue)
    {
        ExchangeAcronym oldValue = getUserAcronym();
        _setUserAcronym(newValue);
        setModified();
        firePropertyChange(PROPERTY_USER_ACRONYM, oldValue, newValue);
    }

    public void setProductType(Short newValue)
    {
        Short oldValue = getProductType();
        _setProductType(newValue);
        setModified();
        firePropertyChange(PROPERTY_PRODUCT_TYPE, oldValue, newValue);
    }

    public void setClassKey(Integer newValue)
    {
        Integer oldValue = getClassKey();
        _setClassKey(newValue);
        setModified();
        firePropertyChange(PROPERTY_CLASS_KEY, oldValue, newValue);
    }

    public void setReceivedTime(DateTime newValue)
    {
        DateTime oldValue = getReceivedTime();
        _setReceivedTime(newValue);
        setModified();
        firePropertyChange(PROPERTY_RECEIVED_TIME, oldValue, newValue);
    }

    public void setState(Short newValue)
    {
        Short oldValue = getState();
        _setState(newValue);
        setModified();
        firePropertyChange(PROPERTY_STATE, oldValue, newValue);
    }

    public void setTradedQuantity(Integer newValue)
    {
        Integer oldValue = getTradedQuantity();
        _setTradedQuantity(newValue);
        setModified();
        firePropertyChange(PROPERTY_TRADED_QUANTITY, oldValue, newValue);
    }

    public void setCancelledQuantity(Integer newValue)
    {
        Integer oldValue = getCancelledQuantity();
        _setCancelledQuantity(newValue);
        setModified();
        firePropertyChange(PROPERTY_CANCELLED_QUANTITY, oldValue, newValue);
    }

    public void setLeavesQuantity(Integer newValue)
    {
        Integer oldValue = getLeavesQuantity();
        _setLeavesQuantity(newValue);
        setModified();
        firePropertyChange(PROPERTY_LEAVES_QUANTITY, oldValue, newValue);
    }

    public void setAveragePrice(Price newValue)
    {
        Price oldValue = getAveragePrice();
        _setAveragePrice(newValue);
        setModified();
        firePropertyChange(PROPERTY_AVERAGE_PRICE, oldValue, newValue);
    }

    public void setSessionTradedQuantity(Integer newValue)
    {
        Integer oldValue = getSessionTradedQuantity();
        _setSessionTradedQuantity(newValue);
        setModified();
        firePropertyChange(PROPERTY_SESSION_TRADED_QUANTITY, oldValue, newValue);
    }

    public void setSessionCancelledQuantity(Integer newValue)
    {
        Integer oldValue = getSessionCancelledQuantity();
        _setSessionCancelledQuantity(newValue);
        setModified();
        firePropertyChange(PROPERTY_SESSION_CANCELLED_QUANTITY, oldValue, newValue);
    }

    public void setSessionAveragePrice(Price newValue)
    {
        Price oldValue = getSessionAveragePrice();
        _setSessionAveragePrice(newValue);
        setModified();
        firePropertyChange(PROPERTY_SESSION_AVERAGE_PRICE, oldValue, newValue);
    }

    public void setOrsId(String newValue)
    {
        String oldValue = getOrsId();
        _setOrsId(newValue);
        setModified();
        firePropertyChange(PROPERTY_ORS_ID, oldValue, newValue);
    }

    public void setSource(Character newValue)
    {
        Character oldValue = getSource();
        _setSource(newValue);
        setModified();
        firePropertyChange(PROPERTY_SOURCE, oldValue, newValue);
    }

    public void setCrossedOrder(OrderId newValue)
    {
        OrderId oldValue = getCrossedOrder();
        _setCrossedOrder(newValue);
        setModified();
        firePropertyChange(PROPERTY_CROSSED_ORDER, oldValue, newValue);
    }

    public void setUserAssignedId(String newValue)
    {
        String oldValue = getUserAssignedId();
        _setUserAssignedId(newValue);
        setModified();
        firePropertyChange(PROPERTY_USER_ASSIGNED_ID, oldValue, newValue);
    }

    public void setActiveSession(String newValue)
    {
        String oldValue = getActiveSession();
        _setActiveSession(newValue);
        setModified();
        firePropertyChange(PROPERTY_ACTIVE_SESSION, oldValue, newValue);

        String[] sessionNames = new String[1];
        sessionNames[0] = activeSession;
        setSessionNames(sessionNames);
    }

    public void setSessionNames(String[] newValue)
    {
        String[] oldValue = getSessionNames();
        _setSessionNames(newValue);
        setModified();
        firePropertyChange(PROPERTY_SESSION_NAMES, oldValue, newValue);
    }

    public void setSessionProduct(SessionProduct newValue)
    {
        SessionProduct oldValue = getSessionProduct();
        _setSessionProduct(newValue);
        setModified();
        firePropertyChange(PROPERTY_SESSION_PRODUCT, oldValue, newValue);

        try
        {
            SessionProductClass newClass = APIHome.findProductQueryAPI().getClassByKeyForSession(
                    getActiveSession(), getSessionProduct().getProductKeysStruct().classKey);
            setSessionProductClass(newClass);
        }
        catch(UserException e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Could not obtain class for product.");
        }
    }

    public void setSessionProductClass(SessionProductClass newValue)
    {
        SessionProductClass oldValue = getSessionProductClass();
        _setSessionProductClass(newValue);
        setModified();
        firePropertyChange(PROPERTY_SESSION_PRODUCT_CLASS, oldValue, newValue);
    }

    /**
     * Gets the underlying struct
     * @return OrderDetailStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public OrderDetailStruct getOrderDetailStruct()
    {
        checkState(getSessionProduct());
        ProductNameStruct productStruct = getSessionProduct().getProductNameStruct();
        OrderStruct orderStruct = getStruct();
        OrderDetailStruct struct = new OrderDetailStruct(productStruct, StatusUpdateReasons.QUERY, orderStruct);
        return struct;
    }

    protected void setModified()
    {
        setModified(true);
    }

    /**
     * As per the NBBO business requirements, the only orders that will have NBBO protection enabled are
     * 'simple customer orders' -- orders with orderOriginType of customer, and no contingency.
     */
    private void updateOrderNBBOProtectionType()
    {
        // only simple (no contingency) Customer orders get NBBO protection
        if(getOrderOriginType().charValue() == OrderOrigins.CUSTOMER &&
                getContingency().getType() == ContingencyTypes.NONE)
        {
            setOrderNBBOProtectionType(new Short(OrderNBBOProtectionTypes.FULL));
        }
        else
        {
            setOrderNBBOProtectionType(new Short(OrderNBBOProtectionTypes.NONE));
        }
    }

    private void setOrderNBBOProtectionType(Short newValue)
    {
        Short oldValue = getOrderNBBOProtectionType();
        _setOrderNBBOProtectionType(newValue);
        setModified();
        firePropertyChange(PROPERTY_ORDER_NBBO_PROTECTION_TYPE, oldValue, newValue);
    }
}