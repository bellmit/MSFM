//
// -----------------------------------------------------------------------------------
// Source file: LegOrderEntry.java
//
// PACKAGE: com.cboe.interfaces.presentation.order
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.order;

import com.cboe.idl.cmiOrder.LegOrderEntryStructV2;

import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.domain.Price;

public interface LegOrderEntry extends BusinessModel
{
    /**
     * Gets the underlying struct
     * @return LegOrderDetailStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public LegOrderEntryStructV2 getStruct();

    public Integer getProductKey();

    public Price getMustUsePrice();

    public ExchangeFirm getClearingFirm();

    public Character getCoverage();

    public Character getPositionEffect();
    
    public Character getSellShortIndicator();
}