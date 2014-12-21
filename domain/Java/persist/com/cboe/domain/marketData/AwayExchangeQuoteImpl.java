package com.cboe.domain.marketData;

import com.cboe.domain.util.MarketDataStructBuilder;
import com.cboe.domain.util.PriceFactory;
import com.cboe.idl.cmiMarketData.ExchangeVolumeStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.marketData.AwayExchangeQuote;
import com.cboe.interfaces.domain.tradingProperty.MarketDataAwayExchanges;

public class AwayExchangeQuoteImpl implements AwayExchangeQuote
{
    //Exchange Name (e.g. AMEX, BOX etc.)
    private String exchangeName;
    
    //Bid Side 
    private Price bidPrice;
    private int bidVolume;
    
    //Ask Side
    private Price askPrice;
    private int   askVolume;
    
    private char  marketIndicator;
    
    //TODO: BOTR-CHANGES If exchange is Part of the BOTR. (We may not need this ??)
    private int  preferredTieExchangeNumber;

    private TimeStruct sentTime = new TimeStruct();

    public final static Price NO_PRICE = PriceFactory.create(Price.NO_PRICE_STRING);
   
    public AwayExchangeQuoteImpl(char exchangeCode)
    {
        this.exchangeName = MarketDataAwayExchanges.findLinkageExchange(exchangeCode).exchangeString;
        this.askPrice = NO_PRICE;
        this.bidPrice = NO_PRICE;
    }

    
    public AwayExchangeQuoteImpl(String exchangeString)
    {
        this.exchangeName = exchangeString;
        this.askPrice = NO_PRICE;
        this.bidPrice = NO_PRICE;
    }

    
    public AwayExchangeQuoteImpl(char exchangeCode, Price bidPrice, int bidVolume,
                             Price askPrice, int askVolume, char marketIndicator)
    {
        this.exchangeName = MarketDataAwayExchanges.findLinkageExchange(exchangeCode).exchangeString;
        this.bidPrice     = bidPrice;
        this.bidVolume    = bidVolume;
        this.askPrice     = askPrice;
        this.askVolume    = askVolume;
        this.preferredTieExchangeNumber = 1;
        this.marketIndicator = marketIndicator;
    }
    
    /**
     * Gets the exchange String Name for Linkage Use.
     * @return
     */
    public String getExchangeName()
    {
        return exchangeName;
    }


    /**
     * Sets the Exchange Name String (e.g. Mapping A - AMEX, B - BOX etc.)
     * @param p_exchangeName
     */
    public void setExchangeName(String p_exchangeName)
    {
        exchangeName = p_exchangeName;
    }


    /**
     * Get the Exchange Bid Price
     * @return
     */
    public Price getBidPrice()
    {
        return bidPrice == null ? NO_PRICE : bidPrice;
    }


    /**
     * Set the Exchange Bid Price
     * @param p_bidPrice
     */
    public void setBidPrice(Price p_bidPrice)
    {
        bidPrice = p_bidPrice;
    }


    /**
     * Get the Exchange Bid Volume
     * @return
     */
    public int getBidVolume()
    {
        return bidVolume;
    }


    /**
     * Set the Exchange Bid Volume
     * @param p_bidVolume
     */
    public void setBidVolume(int p_bidVolume)
    {
        bidVolume = p_bidVolume;
    }


    /**
     * Get the Exchange Ask Price
     * @return
     */
    public Price getAskPrice()
    {
        return askPrice == null ? NO_PRICE : askPrice;
    }


    /**
     * Set the Exchange Ask Price
     * @param p_askPrice
     */
    public void setAskPrice(Price p_askPrice)
    {
        askPrice = p_askPrice;
    }


    /**
     * Get the Exchange Ask Volume.
     * @return
     */
    public int getAskVolume()
    {
        return askVolume;
    }


    /**
     * Set the Exchange Ask Volume.
     * @param p_askVolume
     */
    public void setAskVolume(int p_askVolume)
    {
        askVolume = p_askVolume;
    }


    /**
     * Is This exchange Quote part of the BOTR.
     * @return
     */
    public int getPreferredTieExchangeNumber()
    {
        return preferredTieExchangeNumber;
    }


    /**
     * Set if Quote for this Exchange is part of the BOTR.
     * @param p_exchangeBOTR
     */
    public void setPreferredTieExchangeNumber(int p_preferredSeq)
    {
        preferredTieExchangeNumber = p_preferredSeq;
    }
    
    
    public TimeStruct getSentTime()
    {
        return sentTime;
    }
    public void setSentTime(TimeStruct p_sentTime)
    {
        sentTime = p_sentTime;
    }

    public void setMarketIndicator(char p_marketIndicator)
    {
        marketIndicator = p_marketIndicator;
    }

    public char getMarketIndicator()
    {
        return(marketIndicator);
    }


    public ExchangeVolumeStruct getExchangeAskVolumeStruct()
    {
        ExchangeVolumeStruct exchangeAskVolumeStruct = MarketDataStructBuilder.getExchangeVolumeStruct(this.exchangeName, this.askVolume) ;
        //exchangeAskVolumeStruct.exchange = this.exchangeName;
        //exchangeAskVolumeStruct.volume = this.askVolume;
        return exchangeAskVolumeStruct;
    }
    public ExchangeVolumeStruct getExchangeBidVolumeStruct()
    {
        //ExchangeVolumeStruct exchangeBidVolumeStruct = new ExchangeVolumeStruct();
        ExchangeVolumeStruct exchangeBidVolumeStruct = MarketDataStructBuilder.getExchangeVolumeStruct(this.exchangeName, this.bidVolume) ;
        //exchangeBidVolumeStruct.exchange = this.exchangeName;
        //exchangeBidVolumeStruct.volume = this.bidVolume;
        return exchangeBidVolumeStruct;
    }

}
