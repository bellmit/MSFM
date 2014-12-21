package com.cboe.domain.bestQuote;

import com.cboe.domain.util.PriceSqlType;
import com.cboe.idl.cmiMarketData.CurrentMarketViewStruct;
import com.cboe.idl.cmiMarketData.MarketVolumeStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.bestQuote.CurrentMarket;
import com.cboe.interfaces.domain.bestQuote.CurrentMarketView;

/**
 * This class is designed to implement the new idea on CurrentMarket. CurrentMarket now has 
 * a collection of current market views, such as market best, best limit and best public
 */
public class CurrentMarketViewImpl extends BObject implements CurrentMarketView{
    
    private short type;
	private PriceSqlType bidPrice;
	private MarketVolumeHolder bidVolumes;
	private PriceSqlType askPrice;
	private MarketVolumeHolder askVolumes;    
    private CurrentMarket currentMarket;
    
    
    public CurrentMarketViewImpl(){
        super();
    }

    /**
    * Getter for view type.
    */
    public synchronized short getType()
    {
    	return type;
    }    
    
    /**
    * Getter for bid price.
    */
    public synchronized Price getBidPrice()
    {
    	return bidPrice;
    }     
    
    /**
    * Getter for ask price.
    */
    public synchronized Price getAskPrice()
    {
    	return askPrice;
    }   
    
    /**
    * Getter for bid volumes.
    */
    private MarketVolume[] getBidVolumes()
    {
    	MarketVolumeHolder holder = bidVolumes;
        return holder.getMarketVolumes();
    } 

    /**
     * return the volume holder 
     */ 
    private MarketVolumeHolder getBidVolumeHolder(){
        return bidVolumes;
    }
    
    /**
    * Getter for ask volumes.
    */
    private MarketVolume[] getAskVolumes()
    {
    	MarketVolumeHolder holder = askVolumes;
        return holder.getMarketVolumes();
    }  
    
    /**
     * return the volume holder 
     */ 
    private MarketVolumeHolder getAskVolumeHolder(){
        return askVolumes;
    }
    
    /**
    * Getter for currentMarket.
    */
    public synchronized CurrentMarket getCurrentMarket()
    {
    	return currentMarket;
    }      

   /**
    * Setter for type.
    */
    private void setType(short aValue)
    {
    	type= aValue;
    }
    
   /**
    * Setter for bid piice.
    */
    private void setBidPrice(PriceSqlType aValue)
    {
    	bidPrice= aValue;
    }
    
    /**
    * Setter for ask price.
    */
    private void setAskPrice(PriceSqlType aValue)
    {
    	askPrice= aValue;
    }

    /**
    * Setter for bid volumes.
    */
    private void setBidVolumes(MarketVolume[] aValue)
    {
        MarketVolumeHolder holder = new MarketVolumeHolder(aValue);
    	bidVolumes= holder;
    }     
    
    /**
    * Setter for ask volumes.
    */
    private void setAskVolumes(MarketVolume[] aValue)
    {
        MarketVolumeHolder holder = new MarketVolumeHolder(aValue);
    	askVolumes= holder;
    }   
    
    /**
    * Setter for currentMarket.
    */
    public synchronized void setCurrentMarket(CurrentMarket aValue)
    {
    	currentMarket= aValue;
    }     
    

    /**
     * Convert this object to CurrentMarketViewStruct
     */ 
    public synchronized CurrentMarketViewStruct toStruct(){
        CurrentMarketViewStruct struct = new CurrentMarketViewStruct();
        struct.currentMarketViewType = getType();
        struct.bidPrice = getBidPrice().toStruct();
        struct.bidSizeSequence = toMarketVolumeStructs(getBidVolumes());
        struct.askPrice = getAskPrice().toStruct();
        struct.askSizeSequence = toMarketVolumeStructs(getAskVolumes());
        return struct;
    }
    
    /**
     * Convert MarketVolume into MarketVolumeStruct
     */ 
    private MarketVolumeStruct[] toMarketVolumeStructs(MarketVolume[] marketVolumes){
        MarketVolumeStruct[] result;
        if (marketVolumes != null)
        {
            result = new MarketVolumeStruct[marketVolumes.length];
            for (int i = 0; i < marketVolumes.length; i++) {
                result[i] = marketVolumes[i].toStruct();
            }
        }
        else {
            result = new MarketVolumeStruct[0];
        }
        return result;        
    }
    
    /**
     * update the current object with the new values from struct
     */ 
    public synchronized void update(CurrentMarketViewStruct newView){
        if (getType() == newView.currentMarketViewType){
            setBidPrice(new PriceSqlType(newView.bidPrice));
            setAskPrice(new PriceSqlType(newView.askPrice));
            setBidVolumes(createMarketVolumes(newView.bidSizeSequence));
            setAskVolumes(createMarketVolumes(newView.askSizeSequence));
        }
    }
    
    /**
     * create a sequence of MarektVolume object
     */ 
    private MarketVolume[] createMarketVolumes(MarketVolumeStruct[] volumeStructs){
        MarketVolume[] volumes = new MarketVolume[volumeStructs.length];
        for (int i = 0; i < volumeStructs.length; i++){
            volumes[i] = new MarketVolume(volumeStructs[i]);
        }
        return volumes;
    }
    
    /**
     * populate the fields with a new view struct
     */ 
    public synchronized void setMarketView(CurrentMarketViewStruct newView){
        setType(newView.currentMarketViewType);
        update(newView);
    }
    
    /**
     * return the total quantity on the bid side
     */ 
    public synchronized int getBidSize(){
        return getBidVolumeHolder().getTotalVolumes();
    }
    
    /**
     * return the total non volume contingenct quantity on the bid side
     */ 
    public synchronized int getNonVolumeContingentBidSize(){
        return getBidVolumeHolder().getNonVolumeContingentVolumes();
    }    
    
    /**
     * return the total quantity on the ask side
     */ 
    public synchronized int getAskSize(){
        return getAskVolumeHolder().getTotalVolumes();
    } 
    
    /**
     * return the total non volume contingenct quantity on the ask side
     */ 
    public synchronized int getNonVolumeContingentAskSize(){
        return getAskVolumeHolder().getNonVolumeContingentVolumes();
    }      
}
