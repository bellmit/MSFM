package com.cboe.domain.util;

import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.marketData.MarketDataStructsHolder;
import com.cboe.interfaces.domain.Price;

/**
 * This is designed as convenient class which will holds several CurrentMarketStructs 
 */
public class MarketDataStructsHolderImpl implements MarketDataStructsHolder {
    
    private int productKey;
    private CurrentMarketStruct bestMarket;
    private CurrentMarketStruct bestLimitMarket;
    private CurrentMarketStruct bestPublicMarket;
    private CurrentMarketStruct bestPublicMarketAtTop;

    public MarketDataStructsHolderImpl(){
    }
    
    public MarketDataStructsHolderImpl(int aProductKey,
                                   CurrentMarketStruct aBestMarket,
                                   CurrentMarketStruct aBestLimitMarket,
                                   CurrentMarketStruct aBestPublicMarket,
                                   CurrentMarketStruct aBestPublicMarketAtTop)
    {
        productKey = aProductKey;
        bestMarket = aBestMarket;
        bestLimitMarket = aBestLimitMarket;
        bestPublicMarket = aBestPublicMarket;
        bestPublicMarketAtTop = aBestPublicMarketAtTop;
    }

    public int getProductKey(){
        return productKey;
    }
    
    public CurrentMarketStruct getBestMarket(){
        return bestMarket;
    }
    
    public CurrentMarketStruct getBestLimitMarket(){
        return bestLimitMarket;
    }

    public CurrentMarketStruct getBestPublicMarket(){
        return bestPublicMarket;
    }
    
    public CurrentMarketStruct getBestPublicMarketAtTop(){
        return bestPublicMarketAtTop;
    }  
    
    public void setProductKey(int aProductKey){
        productKey = aProductKey;    
    }
    
    public void setBestMarket(CurrentMarketStruct aBestMarket){
        bestMarket = aBestMarket;
    }
    
    public void setBestLimitMarket(CurrentMarketStruct aBestLimitMarket){
        bestLimitMarket = aBestLimitMarket;
    }

    public void setBestPublicMarket(CurrentMarketStruct aBestPublicMarket){
        bestPublicMarket = aBestPublicMarket;
    }
    
    public void setBestPublicMarketAtTop(CurrentMarketStruct aBestPublicMarketAtTop){
        bestPublicMarketAtTop = aBestPublicMarketAtTop;
    }    
    
    public String toString()
    {
        String methodName = "MarketDataStructsHolderImpl.toString";

        String str = "productKey: " + productKey + "/n" +
        "bestMarket: " + "/n" +
        CurrentMarketStructHelper.currentMarketStructToString(bestMarket) +
        "bestLimitMarket: " + "/n" +
        CurrentMarketStructHelper.currentMarketStructToString(bestLimitMarket) +
        "bestPublicMarket: " + "/n" +
        CurrentMarketStructHelper.currentMarketStructToString(bestPublicMarket) +
        "bestPublicMarketAtTop: " + "/n" +
        CurrentMarketStructHelper.currentMarketStructToString(bestPublicMarketAtTop);
        
        return str;
    }       
    
//    /**
//     * change the inNBBO indicator for current market. 
//     * 
//     * Note: 
//     * 0. bestLimitBidInNBBO &  bestLimitAskInNBBO have been relocated to MarketDataHelper
//     * 1. The inNBBO indicator of current market is calculated based on 
//     *    BestLimitMarket which is the data we publish to the outside world. 
//     * 2. The inNBBO indicator of bestPublicMarket will be ignored, because 
//     *    a calculation is required,  but it is not used anywhere.
//     */ 
//    public void bestLimitBidInNBBO(boolean isInNBBO)
//    {
//        updateBidInNBBOIndicator(bestMarket, isInNBBO);
//        updateBidInNBBOIndicator(bestLimitMarket, isInNBBO);
//        updateBidInNBBOIndicator(bestPublicMarketAtTop, isInNBBO);
//    }
//    public void bestLimitAskInNBBO(boolean isInNBBO){
//        updateAskInNBBOIndicator(bestMarket, isInNBBO);
//        updateAskInNBBOIndicator(bestLimitMarket, isInNBBO);
//        updateAskInNBBOIndicator(bestPublicMarketAtTop, isInNBBO);        
//    }
//    
//    private void updateBidInNBBOIndicator(CurrentMarketStruct aMarketView, boolean isInNBBO){
//        if ( aMarketView == null ||
//             aMarketView.bidPrice == null ||       
//             aMarketView.bidPrice.type == PriceTypes.NO_PRICE)
//        {
//            return;
//        }
//        aMarketView.bidIsMarketBest = isInNBBO;
//    }
//    
//    private void updateAskInNBBOIndicator(CurrentMarketStruct aMarketView, boolean isInNBBO){
//        if ( aMarketView == null ||
//             aMarketView.askPrice == null ||       
//             aMarketView.askPrice.type == PriceTypes.NO_PRICE)
//        {
//            return;
//        }
//        aMarketView.askIsMarketBest = isInNBBO;
//    }    
}
