//
// -----------------------------------------------------------------------------------
// Source file: Order.java
//
// PACKAGE: com.cboe.interfaces.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.order;

import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.common.exchange.Exchange;
import com.cboe.interfaces.presentation.validation.ValidationResult;

public interface Order extends BusinessModel
{
    /**
     * Gets the underlying struct
     * @return OrderStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public OrderStruct getStruct();

    public boolean isNewOrder();

    /**
     * Returns the away exchange for P orders (originType = 'P')
     * @return
     */
    public Exchange getAwayExchange();

    public OrderId getOrderId();

    public ExchangeAcronym getOriginator();

    public Integer getOriginalQuantity();

    public Integer getProductKey();

    public Character getSide();

    public Price getPrice();

    public Character getTimeInForce();

    public DateTime getExpireTime();

    public OrderContingency getContingency();

    public ExchangeFirm getCmta();

    public String getExtensions();

    public String getAccount();

    public String getSubaccount();

    public Character getPositionEffect();

    public Boolean isCross();

    public Character getOrderOriginType();

    public Character getCoverage();

    public Short getOrderNBBOProtectionType();

    public String getOptionalData();

    public String getUserId();

    public ExchangeAcronym getUserAcronym();

    public Short getProductType();

    public Integer getClassKey();

    public DateTime getReceivedTime();

    public Short getState();

    public Integer getTradedQuantity();

    public Integer getCancelledQuantity();

    public Integer getLeavesQuantity();

    public Price getAveragePrice();

    public Integer getSessionTradedQuantity();

    public Integer getSessionCancelledQuantity();

    public Price getSessionAveragePrice();

    public String getOrsId();

    public String getDisplayOrsId();

    public Character getSource();

    public OrderId getCrossedOrder();

    public String getUserAssignedId();

    public String[] getSessionNames();

    public String getActiveSession();

    public LegOrderDetail[] getLegOrderDetails();

    public Integer getTransactionSequenceNumber();

    public SessionProductClass getSessionProductClass();

    public SessionProduct getSessionProduct();

    public ValidationResult validate();

    public String getExtensionValue(String key);
    

}