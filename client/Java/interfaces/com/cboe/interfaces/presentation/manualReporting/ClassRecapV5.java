package com.cboe.interfaces.presentation.manualReporting;

import com.cboe.idl.cmiMarketData.ClassRecapStructV5;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

/**
 * Defines the contract a ClassRecapV5 wrapper for a ClassRecapStructV5
 */
public interface ClassRecapV5 extends BusinessModel {

    // helper methods to struct attributes
    public ClassRecap getProductRecap();
    public DateTimeStruct getLowPriceTime();
    public DateTimeStruct getHighPriceTime();
    public DateTimeStruct getOpeningPriceTime();
    public String getRecapSuffix();
    
    public Price getUnderlyingHighPrice();
    public Price getUnderlyingLowPrice();
    public Price getUnderlyingLastSalePrice();
    
    public ClassRecapStructV5 getStruct();
    public int getProductKey();
    public void setProductKey(int productKey);
}
