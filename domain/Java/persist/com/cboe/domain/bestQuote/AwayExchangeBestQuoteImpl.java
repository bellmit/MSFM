package com.cboe.domain.bestQuote;

import com.cboe.domain.util.DateWrapper;
import com.cboe.domain.util.MarketDataStructBuilder;
import com.cboe.domain.util.PriceFactory;
import com.cboe.domain.util.StructBuilder;
import com.cboe.idl.cmiConstants.Sides;
import com.cboe.idl.cmiMarketData.ExchangeVolumeStruct;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.quote.ExternalQuoteSideStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.Side;
import com.cboe.interfaces.domain.bestQuote.BestQuote;
import com.cboe.interfaces.domain.marketData.MarketData;

/**
 * A persistent implementation of best quote.
 *
 * @author
 */
public class AwayExchangeBestQuoteImpl extends BObject implements BestQuote
{
    
    private static final ExchangeVolumeStruct[] EMPTY_VOLS = new ExchangeVolumeStruct[0];
	/**
	 * Bid price of the quote.
	 */
	private Price bidPrice;

    /**
	 * Quantities available (and originating exchanges) at bid price.
	 */
	private ExchangeVolumeStruct[]  bidExchangeVolumes = EMPTY_VOLS;
	
	private volatile boolean bidDirty = true;
	
	/**
	 * Ask price of the quote.
	 */
	private Price askPrice;
	/**
	 * Quantity available (and originating exchanges) at ask price.
	 */
	private ExchangeVolumeStruct[]  askExchangeVolumes = EMPTY_VOLS;
	
    private volatile boolean askDirty = true;

    /**
	 * Time of quote.
	 */
	private long quoteTime;
	
	private MarketData marketData;
	
    private final static Price NO_PRICE = PriceFactory.create(Price.NO_PRICE_STRING);

    /**
     * Creates an uninitialized instance.
     */
    public AwayExchangeBestQuoteImpl(MarketData marketData)
    {
    	super();
    	this.marketData = marketData;
    }
    /**
     * Performs instance initialization.
     *
     * @author John Wickberg
     */
    public void create()
    {
//    	setBidPrice(new NoPrice());
//    	setAskPrice(new NoPrice());
    }
    
    public Price getBidPrice()
    {
        if(bidPrice == null)
        {
            return NO_PRICE;
        }
        return bidPrice;
    }
    public void setBidPrice(Price p_bidPrice)
    {
        if(p_bidPrice != null && p_bidPrice.isValuedPrice() && p_bidPrice.toLong() == 0L)
        {
           bidPrice = NO_PRICE;
        }
        else
        {
            bidPrice = p_bidPrice;
        }
    }
    public ExchangeVolumeStruct[] getBidExchangeVolumes()
    {
        if(bidExchangeVolumes == null)
        {
            bidExchangeVolumes = EMPTY_VOLS;
        }
        return bidExchangeVolumes;
    }
    public void setBidExchangeVolumes(ExchangeVolumeStruct[] p_bidExchangeVolumes)
    {
        bidExchangeVolumes = p_bidExchangeVolumes;
    }
    public Price getAskPrice()
    {
        if(askPrice == null)
        {
            return NO_PRICE;
        }
        return askPrice;
    }
    public void setAskPrice(Price p_askPrice)
    {
        if(p_askPrice != null && p_askPrice.isValuedPrice() && p_askPrice.toLong() == 0L)
        {
            askPrice = NO_PRICE;
        }
        else
        {
            askPrice = p_askPrice;
        }
    }
    public ExchangeVolumeStruct[] getAskExchangeVolumes()
    {
        if(askExchangeVolumes == null)
        {
            askExchangeVolumes = EMPTY_VOLS;
        }
        return askExchangeVolumes;
    }
    public void setAskExchangeVolumes(ExchangeVolumeStruct[] p_askExchangeVolumes)
    {
        askExchangeVolumes = p_askExchangeVolumes;
    }
    public long getQuoteTime()
    {
        return quoteTime;
    }
    public void setQuoteTime(long p_quoteTime)
    {
        quoteTime = p_quoteTime;
    }
    
    
    /**
     * NOTE: productKey and sessionName are not defined here.
     */
    public  ExternalQuoteSideStruct toStruct(char side)
    {
    	ExternalQuoteSideStruct struct = new ExternalQuoteSideStruct();
        struct.side = side;
        if (side == Sides.BID)
        {
            struct.exchangeVolume = getBidExchangeVolumes();
        }
        else
        {
            struct.exchangeVolume = getAskExchangeVolumes();
        }
        struct.sentTime = toTimeStruct();
    	return struct;
    }
    
    /**
     * @see BestQuote#toNBBOStruct
     *
     * @author
     */
    public  NBBOStruct toNBBOStruct()
    {
    	NBBOStruct struct = new NBBOStruct();
        struct.bidPrice             = createPriceStruct(getBidPrice());
        struct.bidExchangeVolume    = getBidExchangeVolumes();
        struct.askPrice             = createPriceStruct(getAskPrice());
        struct.askExchangeVolume    = getAskExchangeVolumes();
        struct.sentTime             = toTimeStruct();
        struct.sessionName          = marketData.getSessionName();
        struct.productKeys          = marketData.getProductKeys();
    	return struct;
    }
    
    protected PriceStruct createPriceStruct(Price price)
    {
        return (price == null)
            ? StructBuilder.buildPriceStruct()
            : price.toStruct();
    }
    
    protected int totalVolume(ExchangeVolumeStruct[] structs)
    {
        int total = 0;
        for (int i=0; i < structs.length; i++)
        {
            total += structs[i].volume;
        }
        return total;
    }
    
    protected TimeStruct toTimeStruct()
    {
    	if (getQuoteTime() != 0)
    	{
    		return DateWrapper.convertToTime(getQuoteTime());
    	}
    	else
    	{
    		return DateWrapper.convertToTime(System.currentTimeMillis());
    	}
    }
  
    /**
     * @see BestQuote#update
     *
     * @author John Wickberg
     * @param newQuote ExternalQuoteSideStruct
     */
    public  void update(ExternalQuoteSideStruct newQuote)
    {
        if (newQuote.side == Sides.BID)
        {
    	    updateBidSide(PriceFactory.create(newQuote.price), newQuote.exchangeVolume);
        }
        else
        {
    	    updateAskSide(PriceFactory.create(newQuote.price), newQuote.exchangeVolume);
        }
    	setQuoteTime(DateWrapper.convertToMillis(newQuote.sentTime));
    }
    
    /**
     */
    public  void update(NBBOStruct newQuote)
    {
        updateBidSide(PriceFactory.create(newQuote.bidPrice), newQuote.bidExchangeVolume);
        updateAskSide(PriceFactory.create(newQuote.askPrice), newQuote.askExchangeVolume);
    	setQuoteTime(DateWrapper.convertToMillis(newQuote.sentTime));
    }
    
    public  void updateAskSide(Price newPrice, ExchangeVolumeStruct[] newExchangeVolume)
    {
        updateAskSide(newPrice, newExchangeVolume, null);
    }
    public  void updateBidSide(Price newPrice, ExchangeVolumeStruct[] newExchangeVolume)
    {
        updateBidSide(newPrice, newExchangeVolume, null);
    }
    /**
     * @see BestQuote#updateAskSide
     *
     * @author John Wickberg
     */
    public  void updateAskSide(Price newPrice, ExchangeVolumeStruct[] newExchangeVolume, ExchangeVolumeStruct extraVol)
    {
    	setAskPrice(newPrice);
    	setAskExchangeVolumes((extraVol !=null && extraVol.volume > 0) ? addVol(newExchangeVolume, extraVol) : newExchangeVolume );
    }
    
    private ExchangeVolumeStruct[] addVol(ExchangeVolumeStruct[] p_newExchangeVolume,
            ExchangeVolumeStruct p_extraVol)
    {
        ExchangeVolumeStruct[] result = new ExchangeVolumeStruct[p_newExchangeVolume.length+1];
        System.arraycopy(p_newExchangeVolume, 0, result, 0, p_newExchangeVolume.length);
        result[p_newExchangeVolume.length] = MarketDataStructBuilder.getExchangeVolumeStruct(p_extraVol.exchange, p_extraVol.volume);
        //result[p_newExchangeVolume.length].exchange = p_extraVol.exchange;
        //result[p_newExchangeVolume.length].volume = p_extraVol.volume;
        return result;
    }
    
    /**
     * @see BestQuote#updateBidSide
     *
     * @author John Wickberg
     */
    public  void updateBidSide(Price newPrice, ExchangeVolumeStruct[] newExchangeVolume, ExchangeVolumeStruct extraVol)
    {
    	setBidPrice(newPrice);
    	setBidExchangeVolumes( (extraVol != null && extraVol.volume > 0) ? addVol(newExchangeVolume, extraVol) :  newExchangeVolume );
    }

    /**
     * Updates the Bid Side.
     * @param newPriceStruct
     * @param newExchangeVolume
     */
    public  void updateBidSide(PriceStruct newPriceStruct, ExchangeVolumeStruct[] newExchangeVolume)
    {
        UnsupportedOperationException usoe = new UnsupportedOperationException("This method is not supported, cannot be called for BestQuoteImpl");
        Log.exception(this, "Not supported Method", usoe);
        throw usoe;
    }

    /**
     * Updates the ask side.
     * @param newPriceStruct
     * @param newExchangeVolume
     */
    public  void updateAskSide(PriceStruct newPriceStruct, ExchangeVolumeStruct[] newExchangeVolume)
    {
        UnsupportedOperationException usoe = new UnsupportedOperationException("This method is not supported, cannot be called for BestQuoteImpl");
        Log.exception(this, "Not supported Method", usoe);
        throw usoe;
    }
    
    
    /**
     * @see BestQuote#isCrossed
     *
     * @author MattSochacki
     */
    public  boolean isCrossed()
    {
        Price bid = getBidPrice();
        Price ask = getAskPrice();
        //if neither bid or ask is no price
        if( bid != null && !bid.isNoPrice() && ask != null && !ask.isNoPrice() )
        {
            //When the ask is less than the bid the market is crossed
            if( ask.lessThan( bid ) )
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        //if either or both are no price, then the market is not crossed.
        else
        {
            return false;
        }
    }
    
    
    public boolean isBidDirty()
    {
        return bidDirty;
    }
   
    /**
     * 
     * @param p_bidDirty
     */
    public void setBidDirty(boolean p_bidDirty)
    {
        bidDirty = p_bidDirty;
    }
    public boolean isAskDirty()
    {
        return askDirty;
    }
    public void setAskDirty(boolean p_askDirty)
    {
        askDirty = p_askDirty;
    }

    
    /**
     * Gets the total volume by the side, it loops through all the exchanges and accumulate the
     * total volume to return (Mostly used in HAL Helper).
     */
    public int getBotrQuantityBySide(Side side)
    {
        int botrSideVolume = 0;
        
        if (side.isBuySide())
        {
            if(bidExchangeVolumes != null)
            {
                for(int i = 0; i < bidExchangeVolumes.length; i++)
                {
                    if(bidExchangeVolumes[i] != null)
                    {
                        botrSideVolume += bidExchangeVolumes[i].volume;
                    }
                }
            }
        }
        else
        {
            if(askExchangeVolumes != null)
            {
                for(int i = 0; i < askExchangeVolumes.length; i++)
                {
                    if(askExchangeVolumes[i] != null)
                    {
                        botrSideVolume += askExchangeVolumes[i].volume;
                    }
                }
            }
        }
        
        return botrSideVolume;
    }
    


}
