package com.cboe.domain.util;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.interfaces.domain.CurrentMarketProductContainer;
/**
 * @author Vaziranc
 * @Name : CurrentMarketV2ProductContainer.java
 * 
 * 
 */
public class CurrentMarketProductContainerImpl implements CurrentMarketProductContainer{

    private  CurrentMarketStruct bestMarket;
    private  CurrentMarketStruct bestPublicMarketAtTop;
    
    public CurrentMarketProductContainerImpl()
    {
        super();
    }


    public CurrentMarketProductContainerImpl(CurrentMarketStruct productBestMarket,
                                    CurrentMarketStruct productBestPublicMarketAtTop)
    {
        this.bestMarket = productBestMarket;
        this.bestPublicMarketAtTop = productBestPublicMarketAtTop;
    }
    /**
     * @return
     */
    public CurrentMarketStruct getBestMarket() {
        return bestMarket;
    }

    /**
     * @return
     */
    public CurrentMarketStruct getBestPublicMarketAtTop() {
        return bestPublicMarketAtTop;
    }

    /**
     * @param struct
     */
    public void setBestMarket(CurrentMarketStruct productBestMarket) {
        bestMarket = productBestMarket;
    }

    /**
     * @param struct
     */
    public void setBestPublicMarketAtTop(CurrentMarketStruct bestPublicMarket) {
        bestPublicMarketAtTop = bestPublicMarket;
    }

}
