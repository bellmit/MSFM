package com.cboe.domain.bestQuote;

import com.cboe.domain.util.DateWrapper;
import com.cboe.domain.util.NoPrice;
import com.cboe.domain.util.PriceSqlType;
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

/**
 * A persistent implementation of best quote.
 *
 * @author John Wickberg
 */
public class BestQuoteImpl extends BObject implements BestQuote
{
    
	/**
	 * Bid price of the quote.
	 */
	private PriceSqlType bidPrice;
	/**
	 * Quantities available (and originating exchanges) at bid price.
	 */
	private ExchangeVolumeHolder bidExchangeVolumes;
	/**
	 * Ask price of the quote.
	 */
	private PriceSqlType askPrice;
	/**
	 * Quantity available (and originating exchanges) at ask price.
	 */
	private ExchangeVolumeHolder askExchangeVolumes;
	/**
	 * Time of quote.
	 */
	private long quoteTime;

/**
 * Creates an uninitialized instance.
 */
public BestQuoteImpl()
{
	super();
}
/**
 * Performs instance initialization.
 *
 * @author John Wickberg
 */
public void create()
{
	setBidPrice(new PriceSqlType(new NoPrice()));
	setBidExchangeVolumeHolder(new ExchangeVolumeHolder());
	setAskPrice(new PriceSqlType(new NoPrice()));
	setAskExchangeVolumeHolder(new ExchangeVolumeHolder());
}
/**
 * Getter for ask exchange volumes
 */
public synchronized ExchangeVolumeStruct[] getAskExchangeVolumes()
{
	return getAskExchangeVolumeHolder().getExchangeVolumes();
}
/**
 * Getter for ask exchange volumes holder
 */
protected ExchangeVolumeHolder getAskExchangeVolumeHolder()
{
	return askExchangeVolumes;
}
/**
 * Getter for ask price.
 */
public synchronized Price getAskPrice()
{
	return askPrice;
}
/**
 * Getter for bid exchange volumes
 */
public synchronized ExchangeVolumeStruct[] getBidExchangeVolumes()
{
	return getBidExchangeVolumeHolder().getExchangeVolumes();
}
/**
 * Getter for bid exchange volumes holder
 */
protected ExchangeVolumeHolder getBidExchangeVolumeHolder()
{
	return bidExchangeVolumes;
}
/**
 * Getter for bid price.
 */
public synchronized Price getBidPrice()
{
	return bidPrice;
}
/**
 * Getter for quote time.
 */
private long getQuoteTime()
{
	return quoteTime;
}

/**
 * Setter for ask exchange volumes.
 */
public synchronized void setAskExchangeVolumes(ExchangeVolumeStruct[] aValue)
{
    setAskExchangeVolumeHolder(new ExchangeVolumeHolder(aValue));
}
/**
 * Setter for ask exchange volumes holder.
 */
private void setAskExchangeVolumeHolder(ExchangeVolumeHolder aValue)
{
	askExchangeVolumes= aValue;
}
/**
 * Setter for ask price.
 */
private void setAskPrice(PriceSqlType aValue)
{
	askPrice= aValue;
}
/**
 * Setter for bid exchange volumes.
 */
public synchronized void setBidExchangeVolumes(ExchangeVolumeStruct[] aValue)
{
    setBidExchangeVolumeHolder(new ExchangeVolumeHolder(aValue));
}
/**
 * Setter for bid exchange volumes holder.
 */
private void setBidExchangeVolumeHolder(ExchangeVolumeHolder aValue)
{
	bidExchangeVolumes= aValue;
}
/**
 * Setter for bid price.
 */
private void setBidPrice(PriceSqlType aValue)
{
	bidPrice= aValue;
}
/**
 * Setter for quote time.
 */
private void setQuoteTime(long aValue)
{
	quoteTime= aValue;
}
/**
 * NOTE: productKey and sessionName are not defined here.
 */
public synchronized ExternalQuoteSideStruct toStruct(char side)
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
 * @author Alex Torres
 */
public synchronized NBBOStruct toNBBOStruct()
{
	NBBOStruct struct = new NBBOStruct();
    struct.bidPrice             = createPriceStruct(getBidPrice());
    struct.bidExchangeVolume    = getBidExchangeVolumes();
    struct.askPrice             = createPriceStruct(getAskPrice());
    struct.askExchangeVolume    = getAskExchangeVolumes();
    struct.sentTime             = toTimeStruct();
	return struct;
}

protected PriceSqlType createPrice(Price newPrice)
{
	PriceSqlType result;
	if (newPrice == null)
	{
		result = null;
	}
	else if (newPrice instanceof PriceSqlType)
	{
		result = (PriceSqlType) newPrice;
	}
	else
	{
		result = new PriceSqlType(newPrice.toStruct());
	}
    return result;
}

protected PriceSqlType createPrice(PriceStruct price)
{
    return (StructBuilder.isDefault(price))
        ? null
        : new PriceSqlType(price);
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
public synchronized void update(ExternalQuoteSideStruct newQuote)
{
    if (newQuote.side == Sides.BID)
    {
	    updateBidSide(createPrice(newQuote.price), newQuote.exchangeVolume);
    }
    else
    {
	    updateAskSide(createPrice(newQuote.price), newQuote.exchangeVolume);
    }
	setQuoteTime(DateWrapper.convertToMillis(newQuote.sentTime));
}

/**
 */
public synchronized void update(NBBOStruct newQuote)
{
    updateBidSide(createPrice(newQuote.bidPrice), newQuote.bidExchangeVolume);
    updateAskSide(createPrice(newQuote.askPrice), newQuote.askExchangeVolume);
	setQuoteTime(DateWrapper.convertToMillis(newQuote.sentTime));
}

public synchronized void updateAskSide(Price newPrice, ExchangeVolumeStruct[] newExchangeVolume)
{
    updateAskSide(newPrice, newExchangeVolume, null);
}
public synchronized void updateBidSide(Price newPrice, ExchangeVolumeStruct[] newExchangeVolume)
{
    updateBidSide(newPrice, newExchangeVolume, null);
}
/**
 * @see BestQuote#updateAskSide
 *
 * @author John Wickberg
 */
public synchronized void updateAskSide(Price newPrice, ExchangeVolumeStruct[] newExchangeVolume, ExchangeVolumeStruct extraVol)
{
	setAskPrice(createPrice(newPrice));
	setAskExchangeVolumes(extraVol==null ? newExchangeVolume : addVol(newExchangeVolume, extraVol));
}

private ExchangeVolumeStruct[] addVol(ExchangeVolumeStruct[] p_newExchangeVolume,
        ExchangeVolumeStruct p_extraVol)
{
    ExchangeVolumeStruct[] result = new ExchangeVolumeStruct[p_newExchangeVolume.length+1];
    System.arraycopy(p_newExchangeVolume, 0, result, 0, p_newExchangeVolume.length);
    result[p_newExchangeVolume.length] = p_extraVol;
    return result;
}

/**
 * @see BestQuote#updateBidSide
 *
 * @author John Wickberg
 */
public synchronized void updateBidSide(Price newPrice, ExchangeVolumeStruct[] newExchangeVolume, ExchangeVolumeStruct extraVol)
{
	setBidPrice(createPrice(newPrice));
	setBidExchangeVolumes(extraVol==null ? newExchangeVolume : addVol(newExchangeVolume, extraVol));
}

    /**
     * Updates the Bid Side.
     * @param newPriceStruct
     * @param newExchangeVolume
     */
    public synchronized void updateBidSide(PriceStruct newPriceStruct, ExchangeVolumeStruct[] newExchangeVolume)
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
    public synchronized void updateAskSide(PriceStruct newPriceStruct, ExchangeVolumeStruct[] newExchangeVolume)
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
public synchronized boolean isCrossed()
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
public boolean isAskDirty()
{
    // TODO Auto-generated method stub
    return false;
}
public boolean isBidDirty()
{
    // TODO Auto-generated method stub
    return false;
}
public void setAskDirty(boolean p_askDirty)
{
    // TODO Auto-generated method stub
    
}
public void setBidDirty(boolean p_bidDirty)
{
    // TODO Auto-generated method stub
    
}

/**
 * Method to get the total volume by side.
 */
public int getBotrQuantityBySide(Side side)
{
    int botrSideVolume = 0;
    
    if (side.isBuySide())
    {
        ExchangeVolumeStruct[] bidExchangeVolumes = this.getBidExchangeVolumes();
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
        ExchangeVolumeStruct[] askExchangeVolumes = this.getAskExchangeVolumes();
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
