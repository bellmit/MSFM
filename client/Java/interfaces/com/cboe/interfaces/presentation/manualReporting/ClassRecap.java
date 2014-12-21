package com.cboe.interfaces.presentation.manualReporting;

import com.cboe.idl.cmiMarketData.RecapStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.interfaces.domain.Price;

public interface ClassRecap{
    public Price    getLastSalePrice();
    public Price    getHighPrice();
    public Price    getLowPrice();
    public Price    getNetChange();
    public Integer  getLastSaleQty();
    public String   getLastSalePrefix();
    public Integer  getTotalQtyTraded();
    public char     getTickDirection();
    public Price    getOpenPrice();
    public Price    getTick();
    
    public String getSessionName();
    public Integer getOpenInterest();
    public TimeStruct  getTradeTime();
    public Price getBidPrice();
    public TimeStruct getBidTime();
    public Price getAskPrice();
    public TimeStruct getAskTime();
    public Price getClosePrice();
    public String getReportingClass();
    
    public RecapStruct getStruct();
    
}
