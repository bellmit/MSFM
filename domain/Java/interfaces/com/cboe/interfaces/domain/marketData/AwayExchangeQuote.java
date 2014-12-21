package com.cboe.interfaces.domain.marketData;

import com.cboe.idl.cmiMarketData.ExchangeVolumeStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.interfaces.domain.Price;

public interface AwayExchangeQuote
{
    public String getExchangeName();
    public void setExchangeName(String p_exchangeName);
    public Price getBidPrice();
    public void setBidPrice(Price p_bidPrice);
    public int getBidVolume();
    public void setBidVolume(int p_bidVolume);
    public Price getAskPrice();
    public void setAskPrice(Price p_askPrice);
    public int getAskVolume();
    public void setAskVolume(int p_askVolume);
    public int getPreferredTieExchangeNumber();
    public void setPreferredTieExchangeNumber(int p_preferredSeq);
    public void setMarketIndicator(char p_marketIndicator);
    public char getMarketIndicator(); 

    public TimeStruct getSentTime();
    public void setSentTime(TimeStruct p_sentTime);
    
    public ExchangeVolumeStruct getExchangeAskVolumeStruct();
    public ExchangeVolumeStruct getExchangeBidVolumeStruct();

}
