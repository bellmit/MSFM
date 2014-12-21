package com.cboe.domain.util;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.interfaces.domain.CurrentMarketContainer;
/**
 * @author Carol Vazirani
 * @Name : CurrentMarketContainerImpl.java
 *
 *
 */

public class CurrentMarketContainerImpl implements CurrentMarketContainer
{

    private  CurrentMarketStruct[] bestMarkets;
    private  CurrentMarketStruct[] bestPublicMarketsAtTop;

    public CurrentMarketContainerImpl(CurrentMarketStruct[] bestMarkets,
                                    CurrentMarketStruct[] bestPublicMarketsAtTop)
    {
        this.bestMarkets = bestMarkets;
        this.bestPublicMarketsAtTop = bestPublicMarketsAtTop;
    }

    public CurrentMarketContainerImpl(CurrentMarketStruct[] bestMarkets)
    {
        this.bestMarkets = bestMarkets;
        this.bestPublicMarketsAtTop = new CurrentMarketStruct[0];
    }

    /**
     * @return
     */
    public CurrentMarketStruct[] getBestMarkets() {
        return bestMarkets;
    }

    /**
     * @return
     */
    public CurrentMarketStruct[] getBestPublicMarketsAtTop() {
        return bestPublicMarketsAtTop;
    }

    /**
     * @param structs
     */
    public void setBestMarkets(CurrentMarketStruct[] bestMarkets) {
        this.bestMarkets = bestMarkets;
    }

    /**
     * @param structs
     */
    public void setBestPublicMarketsAtTop(CurrentMarketStruct[] bestPublicMarketsAtTop) {
        this.bestPublicMarketsAtTop = bestPublicMarketsAtTop;
    }

}
