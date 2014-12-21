/**
 * 
 */
package com.cboe.interfaces.domain;

import com.cboe.idl.cmiMarketData.CurrentMarketStruct;

/**
 * @author misbahud
 *
 */
public interface ConsolidatedManualBook
{
    public void reconcileWithOrderBook();
    
    public boolean manualQuoteExists();
    
    public boolean bidSideManualQuoteExists();
    
    public boolean askSideManualQuoteExists();
    
    public ManualQuote getBidSideManualQuote();

    public ManualQuote getAskSideManualQuote();
    
    public boolean refreshManualMarket();
    
    public void manualMarketRefreshed();
    
    public char getBidSideCancelReason();
    
    public void setBidSideCancelReason(char cancelReason);
    
    public char getAskSideCancelReason();
    
    public void setAskSideCancelReason(char cancelReason);
    
    public boolean sameSideManualQuoteExists(Side tradableSide);
    
    public boolean oppositeSideManualQuoteExists(Side tradableSide);
    
    public ManualQuote getManualQuote(Side aSide);
    
    public void cleanManualBook();
    
    public void setCancelReason(Side aSide, char cancelReason);
    
    public char getCancelReason(Side aSide);
    
    public CurrentMarketStruct toManualQuoteMarket(short volumeType, String exchange);

}
