//
// -----------------------------------------------------------------------------------
// Source file: MutableOrder.java
//
// PACKAGE: com.cboe.interfaces.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.order;

import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.common.exchange.Exchange;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.domain.Price;
import com.cboe.idl.cmiOrder.OrderDetailStruct;

/**
 * for order entry - order detail
 */
public interface MutableOrder extends Order, MutableBusinessModel
{
    public static final String PROPERTY_ORDER_ID = "PROPERTY_ORDER_ID";
    public static final String PROPERTY_ORIGINATOR = "PROPERTY_ORIGINATOR";
    public static final String PROPERTY_ORIGINAL_QUANTITY = "PROPERTY_ORIGINAL_QUANTITY";
    public static final String PROPERTY_PRODUCT_KEY = "PROPERTY_PRODUCT_KEY";
    public static final String PROPERTY_SIDE = "PROPERTY_SIDE";
    public static final String PROPERTY_PRICE = "PROPERTY_PRICE";
    public static final String PROPERTY_TIME_IN_FORCE = "PROPERTY_TIME_IN_FORCE";
    public static final String PROPERTY_EXPIRE_TIME = "PROPERTY_EXPIRE_TIME";
    public static final String PROPERTY_CONTINGENCY = "PROPERTY_CONTINGENCY";
    public static final String PROPERTY_CMTA = "PROPERTY_CMTA";
    public static final String PROPERTY_EXTENSIONS = "PROPERTY_EXTENSIONS";
    public static final String PROPERTY_ACCOUNT = "PROPERTY_ACCOUNT";
    public static final String PROPERTY_SUB_ACCOUNT = "PROPERTY_SUB_ACCOUNT";
    public static final String PROPERTY_POSITION_EFFECT = "PROPERTY_POSITION_EFFECT";
    public static final String PROPERTY_CROSS = "PROPERTY_CROSS";
    public static final String PROPERTY_ORDER_ORIGIN_TYPE = "PROPERTY_ORDER_ORIGIN_TYPE";
    public static final String PROPERTY_COVERAGE = "PROPERTY_COVERAGE";
    public static final String PROPERTY_ORDER_NBBO_PROTECTION_TYPE = "PROPERTY_ORDER_NBBO_PROTECTION_TYPE";
    public static final String PROPERTY_OPTIONAL_DATA = "PROPERTY_OPTIONAL_DATA";
    public static final String PROPERTY_USER_ID = "PROPERTY_USER_ID";
    public static final String PROPERTY_USER_ACRONYM = "PROPERTY_USER_ACRONYM";
    public static final String PROPERTY_PRODUCT_TYPE = "PROPERTY_PRODUCT_TYPE";
    public static final String PROPERTY_CLASS_KEY = "PROPERTY_CLASS_KEY";
    public static final String PROPERTY_RECEIVED_TIME = "PROPERTY_RECEIVED_TIME";
    public static final String PROPERTY_STATE = "PROPERTY_STATE";
    public static final String PROPERTY_TRADED_QUANTITY = "PROPERTY_TRADED_QUANTITY";
    public static final String PROPERTY_CANCELLED_QUANTITY = "PROPERTY_CANCELLED_QUANTITY";
    public static final String PROPERTY_LEAVES_QUANTITY = "PROPERTY_LEAVES_QUANTITY";
    public static final String PROPERTY_AVERAGE_PRICE = "PROPERTY_AVERAGE_PRICE";
    public static final String PROPERTY_SESSION_TRADED_QUANTITY = "PROPERTY_SESSION_TRADED_QUANTITY";
    public static final String PROPERTY_SESSION_CANCELLED_QUANTITY = "PROPERTY_SESSION_CANCELLED_QUANTITY";
    public static final String PROPERTY_SESSION_AVERAGE_PRICE = "PROPERTY_SESSION_AVERAGE_PRICE";
    public static final String PROPERTY_ORS_ID = "PROPERTY_ORS_ID";
    public static final String PROPERTY_SOURCE = "PROPERTY_SOURCE";
    public static final String PROPERTY_CROSSED_ORDER = "PROPERTY_CROSSED_ORDER";
    public static final String PROPERTY_USER_ASSIGNED_ID = "PROPERTY_USER_ASSIGNED_ID";
    public static final String PROPERTY_ACTIVE_SESSION = "PROPERTY_ACTIVE_SESSION";
    public static final String PROPERTY_SESSION_NAMES = "PROPERTY_SESSION_NAMES";
    public static final String PROPERTY_SESSION_PRODUCT = "PROPERTY_SESSION_PRODUCT";
    public static final String PROPERTY_SESSION_PRODUCT_CLASS = "PROPERTY_SESSION_PRODUCT_CLASS";
    public static final String PROPERTY_AWAY_EXCHANGE = "PROPERTY_AWAY_EXCHANGE";

    /**
     * Gets the underlying struct
     * @return OrderDetailStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public OrderDetailStruct getOrderDetailStruct();

    public MutableOrderId getMutableOrderId();

    public void setOrderId(OrderId value);

    /**
     * sets the away exchange for P orders (originType = 'P' for P orders)
     * @param exchange
     */
    public void setAwayExchange(Exchange exchange);

    public void setOriginator(ExchangeAcronym value);

    public void setOriginalQuantity(Integer value);

    public void setProductKey(Integer value);

    public void setSide(Character value);

    public void setPrice(Price value);

    public void setTimeInForce(Character value);

    public void setExpireTime(DateTime value);

    public void setContingency(OrderContingency value);

    public void setCmta(ExchangeFirm value);

    public void setExtensions(String value);

    public void setAccount(String value);

    public void setSubaccount(String value);

    public void setPositionEffect(Character value);

    public void setCross(Boolean value);

    public void setOrderOriginType(Character value);

    public void setCoverage(Character value);

    public void setOptionalData(String value);

    public void setUserId(String value);

    public void setUserAcronym(ExchangeAcronym value);

    public void setProductType(Short value);

    public void setClassKey(Integer value);

    public void setReceivedTime(DateTime value);

    public void setState(Short value);

    public void setTradedQuantity(Integer value);

    public void setCancelledQuantity(Integer value);

    public void setLeavesQuantity(Integer value);

    public void setAveragePrice(Price value);

    public void setSessionTradedQuantity(Integer value);

    public void setSessionCancelledQuantity(Integer value);

    public void setSessionAveragePrice(Price value);

    public void setOrsId(String value);

    public void setSource(Character value);

    public void setCrossedOrder(OrderId value);

    public void setUserAssignedId(String value);

    public void setActiveSession(String value);

    public void setSessionNames(String[] newValue);

    public void setSessionProductClass(SessionProductClass newValue);

    public void setSessionProduct(SessionProduct newValue);

    public void setExtensionValue(String key, String value);
}