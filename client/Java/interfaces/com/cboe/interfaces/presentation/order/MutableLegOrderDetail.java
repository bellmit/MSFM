//
// -----------------------------------------------------------------------------------
// Source file: MutableLegOrderDetail.java
//
// PACKAGE: com.cboe.interfaces.presentation.order
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.order;

import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.domain.Price;

public interface MutableLegOrderDetail extends MutableBusinessModel, LegOrderDetail
{
    public static final String PROPERTY_PRODUCT_KEY = "PROPERTY_PRODUCT_KEY";
    public static final String PROPERTY_MUST_USE_PRICE = "PROPERTY_MUST_USE_PRICE";
    public static final String PROPERTY_CLEARING_FIRM = "PROPERTY_CLEARING_FIRM";
    public static final String PROPERTY_COVERAGE = "PROPERTY_COVERAGE";
    public static final String PROPERTY_POSITION_EFFECT = "PROPERTY_POSITION_EFFECT";
    public static final String PROPERTY_SIDE = "PROPERTY_SIDE";
    public static final String PROPERTY_ORIGINAL_QUANTITY = "PROPERTY_ORIGINAL_QUANTITY";
    public static final String PROPERTY_TRADED_QUANTITY = "PROPERTY_TRADED_QUANTITY";
    public static final String PROPERTY_CANCELLED_QUANTITY = "PROPERTY_CANCELLED_QUANTITY";
    public static final String PROPERTY_LEAVES_QUANTITY = "PROPERTY_LEAVES_QUANTITY";

    public void setProductKey(Integer newValue);

    public void setMustUsePrice(Price newValue);

    public void setClearingFirm(ExchangeFirm newValue);

    public void setCoverage(Character newValue);

    public void setPositionEffect(Character newValue);

    public void setSide(Character newValue);

    public void setOriginalQuantity(Integer newValue);

    public void setTradedQuantity(Integer newValue);

    public void setCancelledQuantity(Integer newValue);

    public void setLeavesQuantity(Integer newValue);
    
    
}