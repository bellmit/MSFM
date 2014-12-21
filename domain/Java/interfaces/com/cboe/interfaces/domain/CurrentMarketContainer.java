package com.cboe.interfaces.domain;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
/**
 * @author CarolVazirani
 *
 * This is a generic Wrapper interface for the CurrentMarket data that needs to be 
 * passed in through the IEC. 
 */
public interface CurrentMarketContainer 
{
    /**
     * @return
     */
    public CurrentMarketStruct[] getBestMarkets();

    /**
     * @return
     */
    public CurrentMarketStruct[] getBestPublicMarketsAtTop();


}
/**
 * @author Vaziranc
 * @Name : CurrentMarketV2Container.java
 * 
 * 
 */
