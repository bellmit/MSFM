//
// -----------------------------------------------------------------------------------
// Source file: OrderEntry.java
//
// PACKAGE: com.cboe.interfaces.presentation.order
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.order;

import com.cboe.idl.cmiOrder.OrderEntryStruct;

import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;
import com.cboe.interfaces.domain.Price;

/**
 * Provides a contract for a wrapper of OrderEntryStruct
 */
public interface OrderEntry extends BusinessModel
{
    /**
     * Gets the underlying struct
     * @return OrderEntryStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public OrderEntryStruct getStruct();

    public ExchangeFirm getExecutingOrGiveUpFirm();

    public String getBranch();

    public Integer getBranchSequenceNumber();

    public String getCorrespondentFirm();

    public String getOrderDate();

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

    public Boolean getCross();

    public Character getOrderOriginType();

    public Character getCoverage();

    public Short getOrderNBBOProtectionType();

    public String getOptionalData();

    public String getUserAssignedId();

    public String[] getSessionNames();
}