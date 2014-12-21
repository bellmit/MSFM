package com.cboe.domain.optionsLinkage;

import java.util.List;

import com.cboe.idl.order.MarketDetailStruct;
import com.cboe.interfaces.domain.optionsLinkage.AllExchangesBBO;
import com.cboe.interfaces.domain.optionsLinkage.SweepElement;

/**
 * Domain object impl for away exchange quote services.
 * 
 * @author Byron Xiao
 *
 */

public class AllExchangesBBOImpl implements AllExchangesBBO
{
    private List <SweepElement> disqualifiedExchangesBBO;
    private List <SweepElement> qualifiedExchangesBBO;
    private MarketDetailStruct[] marketStructs;

    public List <SweepElement> getDisqualifiedExchangesBBO()
    {
        return disqualifiedExchangesBBO;
    }

    public List <SweepElement> getQualifiedExchangesBBO()
    {
        return qualifiedExchangesBBO;
    }

    public void setDisqualifiedExchangesBBO(List <SweepElement> p_se)
    {
        disqualifiedExchangesBBO = p_se;
    }

    public void setQualifiedExchangesBBO(List <SweepElement> p_se)
    {
        qualifiedExchangesBBO = p_se;
    }
    
    public MarketDetailStruct[] getMarketDetailStructs()
    {
        return marketStructs;
    }
    
    public void setMarketDetailStructs(MarketDetailStruct[] detailStructs)
    {
        marketStructs = detailStructs;
    }

}
