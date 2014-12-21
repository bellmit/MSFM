package com.cboe.interfaces.domain;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;

public interface DerivedQuote extends Quote
{
    public void calculateQuote() throws SystemException, DataValidationException;
      
    public void calculateQuote(BestPriceStruct[] definedSideLegMarkets, BestPriceStruct[] oppositeSideLegMarkets) throws SystemException, DataValidationException;
      
    public void refreshQuoteWithEquityLegsUpdate() throws SystemException, DataValidationException;
      
    public String getClearingFirmKey();
    
    public Tradable getMaxContingencyAsk();
    
    public Tradable getMaxContingencyBid();
       
    public Tradable getMinContingencyAsk();
    
    public Tradable getMinContingencyBid();
        
    public ExchangeAcronymStruct getUserAcronym();
    
    public boolean isAtDerivedQuote(Price aPrice, Side aSide);
    
    public boolean isInsideDerivedQuote(Price aPrice, Side aSide);
    
    public boolean isInsideDerivedQuoteWithinNTicks(Price aPrice, int ticks, Side aSide);
    
    public boolean isOutDerivedQuote(Price aPrice, Side aSide);
    
    public Price getDerivedQuoteWidth();

    
    ///////////////////////////////////
    public boolean isVCOnlyOnTop();
    public BestPriceStruct[] getDQBidSideLegMarkets();
    public BestPriceStruct[] getDQAskSideLegMarkets();
    public BestPriceStruct[][] getDQLegMarkets();
    
    /////////////////////////////////////////
    public Price[] getMaxContingencyLegPrices(Side aSide);
    public Price[] getMinContingencyLegPrices(Side aSide);    
    public Price[] getNonContingencyLegPrices(Side aSide);
    
    public BestPriceStruct[] getBestLetMarketsAtDefinedSide();
    public BestPriceStruct[] getBestLetMarketsAtOppositeSide();

    public Price getAdjustedDerivedQuotePrice(Side side);
}
