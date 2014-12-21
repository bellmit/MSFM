package com.cboe.domain.util;

import com.cboe.interfaces.domain.*;

import java.util.ArrayList;

/*
 *  UD 11/09/04 ...
 *  Container class to hold trade information for a strategy leg
 *  in a quote trigger condition.
 *
 */

public class StrategyLegQuoteTriggerContainer
{
    private TradingProduct tradingProduct;
	private ParticipantList participantList;
    private Price legTradePrice;
	private Side legSide;
    private int matchQuantity;
    private ArrayList atomicTrades;
    long tradeId;
    
    public StrategyLegQuoteTriggerContainer()
    {
        tradingProduct = null;
        participantList = null;   
        legTradePrice = null;
        legSide = null;
        matchQuantity = 0;
        atomicTrades = null;
    }
    
    public StrategyLegQuoteTriggerContainer(TradingProduct tradingProduct, ParticipantList participantList, Price legTradePrice, Side legSide, int matchQuantity, ArrayList atomicTrades)
    {
        this.participantList = participantList;   
        this.legTradePrice = legTradePrice;
        this.legSide = legSide;
        this.tradingProduct = tradingProduct;
        this.matchQuantity = matchQuantity;
        this.atomicTrades = atomicTrades;
    }
   
    public TradingProduct getTradingProduct()
    {
        return tradingProduct;
    }
    
    public ParticipantList getParticipantList()
    {
        return participantList;
    }
    
    public Price getLegTradePrice()
    {
        return legTradePrice;
    }
    
    public Side getLegSide()
    {
        return legSide;
    }

    public int getMatchQuantity()
    {
        return matchQuantity;
    } 
    
    public ArrayList getAtomicTrades()
    {
        return atomicTrades;    
    }
    
    public long getTradeId()
    {
        return tradeId;
    } 
    
    public void setTradingProduct(TradingProduct tradingProduct)
    {
        this.tradingProduct = tradingProduct;
    }
    
    public void setParticipantList(ParticipantList participantList)
    {
        this.participantList = participantList;
    }
    
    public void setLegTradePrice(Price legTradePrice)
    {
        this.legTradePrice = legTradePrice;
    }
    
    public void setLegSide(Side legSide)
    {
        this.legSide = legSide;
    }
    
    public void setMatchQuantity(int matchQuantity)
    {
        this.matchQuantity = matchQuantity;
    }
    
    public void setAtomicTrades(ArrayList atomicTrades)
    {
        this.atomicTrades = atomicTrades;    
    }
    
    public void setTradeId(long tradeId)
    {
        this.tradeId = tradeId;
    }
}
