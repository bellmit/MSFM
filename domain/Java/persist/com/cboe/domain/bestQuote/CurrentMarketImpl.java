package com.cboe.domain.bestQuote;



import com.cboe.domain.util.DateWrapper; 
import com.cboe.domain.util.MarketDataStructBuilder;
import com.cboe.domain.util.PriceFactory;
import com.cboe.domain.util.StructBuilder;

import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.MarketVolumeStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.orderBook.BestBookStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.interfaces.domain.MarketUpdate;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.bestQuote.CurrentMarket;
 
/**
 * A persistent implementation of <code>MarketBest</code>.
 *
 * @author John Wickberg
 */
public class CurrentMarketImpl extends BObject implements CurrentMarket
{
    private static final Price NO_PRICE = PriceFactory.getNoPrice();
    /**
     * Best bid price for product.
     */
    private Price bidPrice;
    /**
     * Volume totals for bid price.
     */
    //private MarketVolumeHolder bidVolumes;
    private MarketVolumeStruct[] bidVolumes={
            new MarketVolumeStruct(),
            new MarketVolumeStruct(),
            new MarketVolumeStruct()};
    private int nBidVolumes=0;
    
    /**
     * Indicator set to true if bid is part of NBBO.
     */
    private boolean bidInNBBO;
    /**
     * Best ask price for product.
     */
    private Price askPrice;
    /**
     * Volume totals for ask price.
     */
    //private MarketVolumeHolder askVolumes; 
    private MarketVolumeStruct[] askVolumes ={
        new MarketVolumeStruct(),
        new MarketVolumeStruct(),
        new MarketVolumeStruct()}; 
        
    private int nAskVolumes=0;
        
    /**
     * Indicator set to true if bid is part of NBBO.
     */
    private boolean askInNBBO;
    /**
     * Time that last update occurred.
     */
    private long updateTime;
    /**
     * Time that last update occurred.
     */
    private boolean legalMarket;
    /**
     * Exchange
     */
    private String exchange;
    

    private String sessionName;
    private ProductKeysStruct productKeys;
    
//    private static MarketVolumeHolder defaultVolumeHolder 
//    = new MarketVolumeHolder (new MarketVolume[0]);
//    private static MarketVolumeStruct[] defaultVolumes=new MarketVolumeStruct[0];

    private CurrentMarketStruct cachedCurrentMarketStruct;
        
    
	

    //private String sessionName;
    //private ProductKeysStruct productKeys;
/**
 * Creates an uninitialized instance.
 */
public CurrentMarketImpl()
{
    super();
    init ();
}

public synchronized void create ()
{
    init ();
}

private void init ()
{
    this.bidPrice = NO_PRICE;
    //this.bidVolumes = defaultVolumes;
    this.askPrice = NO_PRICE;
    //this.askVolumes = defaultVolumes;    
}

///**
// * Converts an array of volume structs to an array of volumes.
// *
// * @author John Wickberg
// */
//private MarketVolume[] createVolumes(MarketVolumeStruct[] volumeStructs)
//{
//    //MarketVolume[] volumes = new MarketVolume[volumeStructs.length];
//    MarketVolume[] volumes;
//    if(volumeStructs.length<=cachedVolumes.length)
//        volumes= cachedVolumes[volumeStructs.length];
//    else
//        volumes= new MarketVolume[volumeStructs.length];
//        
//    for (int i = 0; i < volumeStructs.length; i++)
//    {
//        if(volumeStructs.length<=cachedVolumes.length){//size<=cached, update cached
//        }
//        else{// size larger than cached, create new
//            volumes[i] = new MarketVolume(volumeStructs[i]);
//            
//    }
//    return volumes;
//}
/**
 * Getter for ask price in NBBO indicator.
 */
private boolean getAskInNBBO()
{
    return askInNBBO;
}
/**
 * Getter for legal market
 */
public synchronized boolean isLegalMarket()
{
    return legalMarket;
}

/**
 * Setter for legal market
 */
public synchronized void setLegalMarket(boolean aBoolean)
{
    legalMarket = aBoolean;
}

/**
 * Getter for ask exchange
 */
public synchronized String getExchange()
{
    return exchange;
}

/**
 * Setter for ask exchange
 */
public synchronized void setExchange(String newExchange)
{
    exchange = newExchange;
}

/**
 * Getter for ask price.
 */
public synchronized Price getAskPrice()
{
    return askPrice;
}
/**
 * Returns total of all ask volumes.
 *
 * @author John Wickberg
 */
public synchronized int getAskSize()
{
    //return sumVolumes(getAskVolumes());
    int total = 0;
    for (int i = 0; i < nAskVolumes; i++)
    {
        total += askVolumes[i].quantity;
    }
    return total;
    
}
/**
 * Getter for ask volumes.
 */
private MarketVolumeStruct[] getAskVolumes()
{
//    MarketVolumeHolder holder = askVolumes;
//    return holder.getMarketVolumes();
    MarketVolumeStruct[] rslt= new MarketVolumeStruct[nAskVolumes];
    for (int i = 0;i < rslt.length;i++)
    {
        rslt[i]= MarketDataStructBuilder.getMarketVolumeStruct(
                askVolumes[i].volumeType,
                askVolumes[i].quantity,
                askVolumes[i].multipleParties);
    }
    return rslt;
}
/**
 * Getter for bid price in NBBO indicator.
 */
private boolean getBidInNBBO()
{
    return bidInNBBO;
}
/**
 * Getter for bid price.
 */
public synchronized Price getBidPrice()
{
    return bidPrice;
}
/**
 * Returns total of all bid volumes.
 *
 * @author John Wickberg
 */
public synchronized int getBidSize()
{
    //return sumVolumes(getBidVolumes());
    int total = 0;
    for (int i = 0; i < nBidVolumes; i++)
    {
        total += bidVolumes[i].quantity;
    }
    return total;
}
/**
 * Getter for bid volumes.
 */
private MarketVolumeStruct[] getBidVolumes()
{
//    MarketVolumeHolder holder = bidVolumes;
//    return holder.getMarketVolumes();
    MarketVolumeStruct[] rslt= new MarketVolumeStruct[nBidVolumes];
    for (int i=0;i<rslt.length;i++)
    {
        rslt[i]= MarketDataStructBuilder.getMarketVolumeStruct(
                bidVolumes[i].volumeType,
                bidVolumes[i].quantity,
                bidVolumes[i].multipleParties);
    }
    
    return rslt;
}
/**
 * Getter for update time.
 */
private long getUpdateTime()
{
    return updateTime;
}
/**
 * Setter for ask price in NBBO indicator.
 */
public synchronized void setAskInNBBO(boolean aValue)
{
    askInNBBO = aValue;
}
/**
 * Setter for ask price.
 */
public synchronized void setAskPrice(Price aValue)
{
    askPrice = aValue;
}
/**
 * Setter for ask volumes.
 */
public synchronized void setAskVolumes(MarketVolumeStruct[] aValue)
{
//    MarketVolumeHolder holder = new MarketVolumeHolder(aValue);
//    askVolumes = holder;
    if (aValue==null){
        return;
    }
    nAskVolumes=aValue.length;
    for(int i=0;i<nAskVolumes;i++){
        askVolumes[i].volumeType=aValue[i].volumeType;
        askVolumes[i].quantity=aValue[i].quantity;
        askVolumes[i].multipleParties=aValue[i].multipleParties;
    }
}
/**
 * Setter for bid price in NBBO indicator.
 */
public synchronized void setBidInNBBO(boolean aValue)
{
    bidInNBBO = aValue;
}
/**
 * Setter for bid price.
 */
public synchronized void setBidPrice(Price aValue)
{
    bidPrice = aValue;
}
/**
 * Setter for bid volumes.
 */
public synchronized void setBidVolumes(MarketVolumeStruct[] aValue)
{
//    MarketVolumeHolder holder = new MarketVolumeHolder(aValue);
//    bidVolumes = holder;
    if (aValue==null){
        return;
    }
    nBidVolumes=aValue.length;
    for(int i=0;i<nBidVolumes;i++){
        bidVolumes[i].volumeType=aValue[i].volumeType;
        bidVolumes[i].quantity=aValue[i].quantity;
        bidVolumes[i].multipleParties=aValue[i].multipleParties;
    }

}
/**
 * Setter update time.
 */
public synchronized void setUpdateTime(long aValue)
{
    updateTime = aValue;
}
///**
// * Sums all quantities in the given volumes.  When contingencies are added, some contingencies may need
// * to be excluded from the total.
// *
// * @author John Wickberg
// */
//private int sumVolumes(MarketVolumeStruct[] volumes)
//{
//    int total = 0;
//    for (int i = 0; i < volumes.length; i++)
//    {
//        total += volumes[i].quantity;
//    }
//    return total;
//}
/**
 * Converts this market best to a CORBA struct.
 *
 * @see CurrentMarket#toStruct
 */
public synchronized CurrentMarketStruct toStruct()
{
    //CurrentMarketStruct struct = new CurrentMarketStruct();
    if(cachedCurrentMarketStruct==null)
    {
        cachedCurrentMarketStruct=new CurrentMarketStruct();
     
    }
    
    CurrentMarketStruct struct =cachedCurrentMarketStruct;
    
    
    if (getBidPrice() != null)
    {
        struct.bidPrice = getBidPrice().toStruct();
    }
    else
    {
        struct.bidPrice = StructBuilder.buildPriceStruct();
    }
    struct.bidSizeSequence = getBidVolumes();
    struct.bidIsMarketBest = getBidInNBBO();
    struct.legalMarket = isLegalMarket();
    struct.exchange = getExchange();
    if (getAskPrice() != null)
    {
        struct.askPrice = getAskPrice().toStruct();
    }
    else
    {
        struct.askPrice = StructBuilder.buildPriceStruct();
    }
    struct.askSizeSequence = getAskVolumes();
    struct.askIsMarketBest = getAskInNBBO();
    struct.sentTime = DateWrapper.convertToTime(getUpdateTime());
    
    struct.productKeys = new ProductKeysStruct();
    struct.productKeys.classKey =  this.productKeys.classKey;
    struct.productKeys.productKey =  this.productKeys.productKey;
    struct.productKeys.reportingClass =  this.productKeys.reportingClass;
    struct.productKeys.productType = this.productKeys.productType;
    struct.sessionName=this.sessionName;
    
    return struct;
}
///**
// * Converts an array of MarketBestVolume's to an array of
// * MarketBestVolumeStruct's.
// *
// * @author John Wickberg
// */
//private MarketVolumeStruct[] toStruct(MarketVolume[] volumes)
//{
//    MarketVolumeStruct[] result;
//    if (volumes != null)
//    {
//        result = new MarketVolumeStruct[volumes.length];
//        for (int i = 0; i < volumes.length; i++)
//        {
//            result[i] = volumes[i].toStruct();
//        }
//    }
//    else
//    {
//        result = new MarketVolumeStruct[0];
//    }
//    return result;
//}
	//struct.productKeys=this.productKeys;
	//struct.sessionName=this.sessionName;
	
/**
 * Updates market best with data from a market best struct.
 *
 * @author John Wickberg
 */
public synchronized void update(CurrentMarketStruct newBest)
{
        if (newBest == null) {
            return;
        }
    if (!StructBuilder.isDefault(newBest.bidPrice))
    {
        setBidPrice(PriceFactory.create(newBest.bidPrice));
    }
    else
    {
        setBidPrice(NO_PRICE);
    }
    setBidVolumes(newBest.bidSizeSequence);
    setBidInNBBO(newBest.bidIsMarketBest);
    if (!StructBuilder.isDefault(newBest.askPrice))
    {
        setAskPrice(PriceFactory.create(newBest.askPrice));
    }
    else
    {
        setAskPrice(NO_PRICE);
    }
    setAskVolumes(newBest.askSizeSequence);
    setAskInNBBO(newBest.askIsMarketBest);
    setLegalMarket (newBest.legalMarket);
    setExchange(newBest.exchange);
    if (!StructBuilder.isDefault(newBest.sentTime))
    {
        
//      DateWrapper sentTime = new DateWrapper();
//      sentTime.setTime(newBest.sentTime);
//      setUpdateTime(sentTime.getTimeInMillis());
        // use convertToMillis instead
        setUpdateTime(DateWrapper.convertToMillis(newBest.sentTime));
    }
    else
    {
        setUpdateTime(System.currentTimeMillis());
    }
    
    this.sessionName=newBest.sessionName;
	
}

public synchronized void update(MarketUpdate update, short viewType)
{
    update.copyIntoStruct(update, this, viewType);
}

public synchronized void setProductKeys(int key, int classKey, short type, int reportingClsssKey)
{
    if(productKeys == null)
    {
        productKeys = new ProductKeysStruct(key,  classKey,  type,  reportingClsssKey);
    }
    else
    {   
        productKeys.productKey = key;
        productKeys.classKey = classKey;
        productKeys.productType = type;
        productKeys.reportingClass = reportingClsssKey;

    }

}

/**
 * Updates this market best from values in a best book struct.
 *
 * @author John Wickberg
 */
public synchronized void update(BestBookStruct bestBook, String exchange)
{
    if (!StructBuilder.isDefault(bestBook.bidPrice))
    {
        setBidPrice(PriceFactory.create(bestBook.bidPrice));
    }
    else
    {
        setBidPrice(NO_PRICE);
    }
    setBidVolumes(bestBook.bidSizeSequence);
    setExchange(exchange);
    if (!StructBuilder.isDefault(bestBook.askPrice))
    {
        setAskPrice(PriceFactory.create(bestBook.askPrice));
    }
    else
    {
        setAskPrice(NO_PRICE);
    }
    setLegalMarket (bestBook.legalMarket);
    setAskVolumes(bestBook.askSizeSequence);
    setUpdateTime(System.currentTimeMillis());
}

    public String getSessionName()
    {
        return sessionName;
    }
    
    public void setSessionName(String p_sessionName)
    {
        sessionName = p_sessionName;
    }
    
    public ProductKeysStruct getProductKeys()
    {
        return productKeys;
    }
    
    public void setProductKeys(ProductKeysStruct p_productKeys)
    {
        productKeys = p_productKeys;
    }
}
