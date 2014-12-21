package com.cboe.interfaces.domain;

import com.cboe.exceptions.OrderBookTradableNotFoundException;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.orderBook.TradableStruct;


public interface ManualQuoteBook 
{
    public boolean manualQuoteExists();
    
    public boolean bidSideManualQuoteExists();
    
    public boolean askSideManualQuoteExists();
    
    public ManualQuote getBidSideManualQuote();

    public ManualQuote getAskSideManualQuote();
    
    public void acceptManualQuote(ManualQuote quote);
    
    public void cancelManualQuote(Side sideToCancel);
    
    public void acceptCancelManualQuote(Side sideToCancel, char cancelReason);
    
    public void cancelManualQuote(ManualQuote quote) throws OrderBookTradableNotFoundException;
    
    public CurrentMarketStruct toManualQuoteMarket(short volumeType, String exchange);
    
    public MarketVolumeStruct[] toBidMarketVolumeSequence(short volumeType);
    
    public MarketVolumeStruct[] toAskMarketVolumeSequence(short volumeType);
    
    public boolean isBuySideAggregated();
    
    public boolean isSellSideAggregated();
    
    public boolean refreshManualMarket();
    
    public void manualMarketRefreshed();
    
    public char getBidSideCancelReason();
    
    public char getAskSideCancelReason();
    
    /**
     * This methods checks whether tradable is marketable with the opposite side manual quote.
     * If opposite side manual quote does not exists, It will return false.
     */
    public boolean crossesManualMarket(Price tradablePrice, Side tradableSide);
    
    /**
     * Check wheather given tradable is worse then the manual book on the same side.
     *  @param aTradable
     * @return true if tradable is worse than the  manual book. 
     * If manual quote does not exists, it will return false.
     */
    public boolean offManualMarket(Price tradablePrice, Side tradableSide);
    
    /**
     * Check wheather given tradable improves the manual book on the same side.
     *  @param aTradablePrice - 
     *  @param tradableSide - incomingTradableSide
     * @return true if tradable improves the manual book. 
     * If manual quote does not exists, it will return true.
     */
    public boolean improvesManualMarket(Price tradablePrice, Side tradableSide);

    public TradableStruct toManualQuoteTradableStruct(ManualQuote quote);

    public boolean getManualQuoteChanged();
    public void setManualQuoteChanged(boolean flag);
    public void refreshManualQuoteChanged();
    
    public boolean sameSideManualQuoteExists(Side aSide);
    
    public boolean oppositeSideManualQuoteExists(Side aSide);
    
    public ManualQuote getManualQuote(Side aSide);
}
