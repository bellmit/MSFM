package com.cboe.domain.util;

import com.cboe.interfaces.domain.*;

/*
 *  UD 11/09/04 ...
 *  Container class to hold trade information for a strategy 
 *	order that can trade with the legs...
 */
public class StrategyLegTradeContainer
{
    private final static Price NO_PRICE = PriceFactory.create(Price.NO_PRICE_STRING);
    
	private Tradable tradable;
	private int quantity;
	private boolean useContingentBest;
	private StrategyLegTradePrice[] legPrices;
    private boolean canTradeWithLegs;
    ParticipantList spreadList;

	public StrategyLegTradeContainer()
    {
		tradable = null;
		quantity = 0;
		useContingentBest = false;
		legPrices = null;
        canTradeWithLegs = false;
        spreadList = null;
    }
	
    // setters
	public void setTradable(Tradable derivedTradable)
	{
        this.tradable = derivedTradable;
	}

	public void setQuantity(int derivedQuantity)
	{
        this.quantity = derivedQuantity;
	}

	public void setUseContingentBest(boolean useContingentBest)
	{
        this.useContingentBest = useContingentBest;
	}
    
    public void setLegPrices(StrategyLegTradePrice[] legPrices)
    {
        this.legPrices = legPrices;
    }
    
    public void setCanTradeWithLegs(boolean canTradeWithLegs)
    {
        this.canTradeWithLegs = canTradeWithLegs;
    }
    
    public void setParticipantList(ParticipantList spreadList)
	{
        this.spreadList = spreadList;
	}
    
    // Getters
    public Tradable getTradable()
	{
        return tradable;
	}

	public int getQuantity()
	{
        return quantity;
	}

	public boolean getUseContingentBest()
	{
        return useContingentBest;
	}
    
    public StrategyLegTradePrice[] getLegPrices()
    {
       return legPrices;
    }
    
    public boolean getCanTradeWithLegs()
    {
        return canTradeWithLegs;
    }
    
    public Price getTradablePrice()
	{
        if (tradable != null) {
            return tradable.getPrice();
        }
        return NO_PRICE;
	}
    
    public ParticipantList getParticipantList(ParticipantList spreadList)
	{
        return this.spreadList;
	}
}
