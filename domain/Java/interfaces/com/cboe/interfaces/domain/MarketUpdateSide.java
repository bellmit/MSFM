package com.cboe.interfaces.domain;


public interface MarketUpdateSide
{

    public Price getBestPrice();

    public Price getBestLimitPrice();

    public Price getBestPublicPrice();

    public boolean hasPublicPrice();
    
    public boolean hasLimitPrice();

    public boolean isMultiParty(MarketUpdateVolType p_volType);

    public void setIsMultiParty(MarketUpdateVolType p_volType);

    public int getTotalVolume(short p_volType);

    public int getVolume(MarketUpdateVolType p_volType);

    public void setBestPrice(Price p_price);

    public void setBestLimitPrice(Price p_price);

    public void setBestPublicPrice(Price p_price);

    public void setVolume(MarketUpdateVolType p_volType, int p_volume, boolean p_isMultiParty);

    public void clear();

    public boolean isPublicPriceBest();
    
    public boolean isLimitPriceBest();

    public boolean isBid();

    public  boolean hasLimitAndReserveVolume();

    public boolean hasRoundLotLimitMarket();
    public void setRoundLotLimitMarket(boolean p_roundLotLimit);

    public Price getBestLimitRoundLotPrice();
    public void setBestLimitRoundLotPrice(Price p_bestLimitRoundLotPrice);

}
