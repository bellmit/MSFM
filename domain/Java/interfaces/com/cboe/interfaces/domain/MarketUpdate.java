package com.cboe.interfaces.domain;

import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.marketData.ManualQuoteDetailInternalStruct;
import com.cboe.interfaces.domain.bestQuote.CurrentMarket;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;

public interface MarketUpdate
{
    public void copyFrom(MarketUpdate p_other);
    
    public void copyIntoStruct(MarketUpdate update, CurrentMarket market, short viewType);
    
    public void copyIntoStruct(CurrentMarketStruct market, short viewType);
    
    public MarketUpdateSide getBid();
    
    public MarketUpdateSide getAsk();
    
    public MarketUpdateSide getBidManual();
    
    public MarketUpdateSide getAskManual();
    
    public void setSentTime(long sentTime);

    public void setLegalMarket(boolean p_isLegalMarket);

    public boolean isLegalMarket();

    public void setProductKeys(ProductKeysStruct p_prodKeys);

    public void clear();

    public ManualQuoteDetailInternalStruct getManualQuoteDetails();

    public void createManualSides();

    public ManualQuoteDetailInternalStruct getOrCreateManualQuoteDetails();

    public void setManualQuoteDetails(ManualQuoteDetailInternalStruct p_manualQuoteDetails);

    public Object copy();
    
    public String getSessionName();
    
    public void setSessionName(String sessionName);

    public long getAcquiringThreadId();

    public void setAcquiringThreadId(long acquiringThreadId);

    public int getProdClassKey();

    public int getProdReportingClassKey();

    public int getProductKey();

    public short getProdType();

    public void setProdClassKey(int p_val);

    public void setProdReportingClassKey(int p_val);

    public void setProductKey(int p_val);

    public void setProdType(short p_val);

    public void setOldProductState(short p_oldState);

    public boolean isProductStateChange();

    public short getOldProductState();

    public short getNewProductState();

    public void setNewProductState(short p_newState);

    public boolean hasPublicPrice();

    public boolean hasManualQuote();

    public boolean isPublicPriceBest();
    
    public boolean isPublicBidPriceBest();
    
    public boolean isLimitBidPriceBest();
    
    public boolean isPublicAskPriceBest();
    
    public boolean isLimitAskPriceBest();

    public int getProdStateSeqNum();

    public void setProdStateSeqNum(int p_val);

    public long getSentTime();

    public void setIsProductStateChange(boolean p_b);
    
    public boolean hasRoundLotLimitMarket();
    
    public boolean isRoundLotMarketChange();
    public void setRoundLotMarketChange(boolean p_roundLotMarketChange);
    
}