package com.cboe.presentation.manualReporting;

import com.cboe.idl.cmiMarketData.RecapStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.manualReporting.ClassRecap;

public class ClassRecapImpl implements ClassRecap	{
     private RecapStruct         recap;
     private DisplayPriceWrapper lastSalePrice;
     private DisplayPriceWrapper netChange;
     private DisplayPriceWrapper openPrice;
     private DisplayPriceWrapper lowPrice;
     private DisplayPriceWrapper highPrice;
     private DisplayPriceWrapper tick;
     private Integer             lastSaleQty;
     private Integer             totalQtyTraded;
     private String              lastSalePrefix;
     
     private String 				sessionName;
     private Integer 				openInterest;
     private TimeStruct 			tradeTime;
     private DisplayPriceWrapper 	bidPrice;
     private TimeStruct 			bidTime;
     private DisplayPriceWrapper 	askPrice;
     private TimeStruct 			askTime;
     private DisplayPriceWrapper 	closePrice;
     private String 				reportingClass;
     
     /**
     * 
     */
    public ClassRecapImpl() {
        super();
        lastSalePrice = new DisplayPriceWrapper();
        openPrice     = new DisplayPriceWrapper();
        netChange     = new DisplayPriceWrapper();
        lowPrice      = new DisplayPriceWrapper();
        highPrice     = new DisplayPriceWrapper();
        tick          = new DisplayPriceWrapper();
        bidPrice 	  = new DisplayPriceWrapper();
        askPrice 	  = new DisplayPriceWrapper();
        closePrice    = new DisplayPriceWrapper();

        lastSaleQty   = null;
        totalQtyTraded= null;
        lastSalePrefix= null;
        sessionName = null;
        openInterest = null;
        tradeTime = null;
        bidTime = null;
        askTime = null;
        reportingClass = null;
    }
     
    public void setValue(RecapStruct recap){
        this.recap = recap;
        if (recap == null){
            lastSalePrice.setPrice(null);
            openPrice.setPrice(null);
            netChange.setPrice(null);
            lowPrice.setPrice(null);
            highPrice.setPrice(null);
            tick.setPrice(null);
            bidPrice.setPrice(null);
            askPrice.setPrice(null);
            closePrice.setPrice(null);
            
            lastSaleQty     = null;
            totalQtyTraded  = null;
            lastSalePrefix  = null;
            sessionName = null;
            openInterest = null;
            tradeTime = null;
            bidTime = null;
            askTime = null;
            reportingClass = null;
        }
        else {
            lastSalePrice.setPrice(recap.lastSalePrice);
            openPrice.setPrice(recap.openPrice);
            netChange.setPrice(recap.netChange);
            lowPrice.setPrice(recap.lowPrice);
            highPrice.setPrice(recap.highPrice);
            tick.setPrice(recap.tick);
            
            bidPrice.setPrice(recap.bidPrice);
            askPrice.setPrice(recap.askPrice);
            closePrice.setPrice(recap.closePrice);
            
            lastSaleQty     = recap.lastSaleVolume;
            totalQtyTraded  = recap.totalVolume;
            lastSalePrefix  = recap.recapPrefix;
            sessionName = recap.sessionName;
            openInterest = recap.openInterest;
            
            tradeTime = recap.tradeTime;
            bidTime = recap.bidTime;
            askTime = recap.askTime;
            reportingClass = recap.productInformation.reportingClass;
        }
    }
    
    /* (non-Javadoc)
     * @see com.cboe.presentation.marketDisplay.interfaces.Recap#getHighPrice()
     */
    public Price getHighPrice() {
        return highPrice.getPrice();
    }

    /* (non-Javadoc)
     * @see com.cboe.presentation.marketDisplay.interfaces.Recap#getLastSalePrefix()
     */
    public String getLastSalePrefix() {
        if(lastSalePrefix == null)
        {
            lastSalePrefix = "none" ;
            if (recap != null && recap.recapPrefix != null && recap.recapPrefix.length() > 0 )
            {
                lastSalePrefix = recap.recapPrefix ;
            }
        }
        
        return lastSalePrefix;
    }

    /* (non-Javadoc)
     * @see com.cboe.presentation.marketDisplay.interfaces.Recap#getLastSalePrice()
     */
    public Price getLastSalePrice() {
        return lastSalePrice.getPrice();
    }

    /* (non-Javadoc)
     * @see com.cboe.presentation.marketDisplay.interfaces.Recap#getLastSaleQty()
     */
    public Integer getLastSaleQty() {
        if (lastSaleQty == null){
            if (recap != null) {
                lastSaleQty = new Integer(recap.lastSaleVolume);
            }
            else{
                lastSaleQty = new Integer(0);
            }
        }
        return lastSaleQty;
    }

    /* (non-Javadoc)
     * @see com.cboe.presentation.marketDisplay.interfaces.Recap#getLowPrice()
     */
    public Price getLowPrice() {
        return lowPrice.getPrice();
    }

    /* (non-Javadoc)
     * @see com.cboe.presentation.marketDisplay.interfaces.Recap#getNetChange()
     */
    public Price getNetChange() {
        return netChange.getPrice();
    }

    /* (non-Javadoc)
     * @see com.cboe.presentation.marketDisplay.interfaces.Recap#getOpenPrice()
     */
    public Price getOpenPrice() {
        return openPrice.getPrice();
    }

    /* (non-Javadoc)
     * @see com.cboe.presentation.marketDisplay.interfaces.Recap#getTickDirection()
     */
    public char getTickDirection() {
        if (recap != null){
            return recap.tickDirection;
        }
        else{
            return ' ';
        }
    }

    /* (non-Javadoc)
     * @see com.cboe.presentation.marketDisplay.interfaces.Recap#getTotalQtyTraded()
     */
    public Integer getTotalQtyTraded() {
        if (totalQtyTraded == null){
            if (recap != null){
                totalQtyTraded = new Integer(recap.totalVolume);
            }
            else{
                totalQtyTraded = new Integer(0);
            }
        }
        return totalQtyTraded;
    }

    public Price getTick(){
        return tick.getPrice();
    }

    public String getSessionName()
    {
        if (sessionName == null){
            if (recap != null){
                sessionName = recap.sessionName;
            }
            else{
                sessionName = "W_MAIN";
            }
        }
        return sessionName;
    }
    
    public Integer getOpenInterest()
    {
        if (openInterest == null){
            if (recap != null){
            	openInterest = new Integer(recap.openInterest);
            }
            else{
            	openInterest = new Integer(0);
            }
        }
        return openInterest;
    }
    
    public TimeStruct  getTradeTime()
    {
        if (tradeTime == null){
            if (recap != null){
            	tradeTime = recap.tradeTime;
            }
            else{
            	tradeTime = null;
            }
        }
        return tradeTime;
    }
    
    public Price getBidPrice()
    {
    	return bidPrice.getPrice();
    }
    
    public TimeStruct getBidTime()
    {
    	return bidTime;
    }

    public Price getAskPrice()
    {
    	return askPrice.getPrice();
    }
    
    public TimeStruct getAskTime()
    {
    	return askTime;
    }

    public Price getClosePrice()
    {
    	return closePrice.getPrice();
    }
    
    public String getReportingClass()
    {
    	return reportingClass;
    }
    
    public RecapStruct getStruct(){
        return this.recap;
    }
}