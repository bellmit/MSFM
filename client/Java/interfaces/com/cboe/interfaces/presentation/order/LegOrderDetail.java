//
// -----------------------------------------------------------------------------------
// Source file: LegOrderDetail.java
//
// PACKAGE: com.cboe.interfaces.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.order;

import com.cboe.idl.cmiOrder.LegOrderDetailStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;
import com.cboe.interfaces.presentation.user.ExchangeFirm;

public interface LegOrderDetail extends BusinessModel
{
    /**
     * Gets the underlying struct
     * @return LegOrderDetailStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public LegOrderDetailStruct getStruct();

    public Integer getProductKey();

    public Price getMustUsePrice();

    public ExchangeFirm getClearingFirm();

    public Character getCoverage();

    public Character getPositionEffect();

    public Character getSide();

    public Integer getOriginalQuantity();

    public Integer getTradedQuantity();

    public Integer getCancelledQuantity();

    public Integer getLeavesQuantity();
}