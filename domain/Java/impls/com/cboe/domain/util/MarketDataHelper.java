package com.cboe.domain.util;

import com.cboe.idl.cmiConstants.VolumeTypes;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.ExchangeVolumeStruct;
import com.cboe.idl.cmiMarketData.MarketVolumeStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.marketData.MarketDataStructsHolder;

/**
 * This class is designed as convenient class to perform variety of calculation related to MarketData
 */
public final class MarketDataHelper {
    
    private MarketDataHelper(){}


    public static MarketDataStructsHolder[] organizeByProducts(CurrentMarketStruct[] bestMarkets,
            CurrentMarketStruct[] bestLimitMarkets,
            CurrentMarketStruct[] bestPublicMarkets,
            CurrentMarketStruct[] bestPublicMarketsAtTop)
    {
        if(bestMarkets.length == bestLimitMarkets.length)
        {
            return organizeByProductsV1(bestMarkets, bestLimitMarkets, bestPublicMarkets, bestPublicMarketsAtTop);        
        }
        else
        {
            return organizeByProductsV2(bestMarkets, bestLimitMarkets, bestPublicMarkets, bestPublicMarketsAtTop);
        }
    }  
    
    
    /**
     * This method combines different views of market for a product together. Assumes
     * 1. bestMarkets and bestLimitMarkets have the same sizes and each element corresponding to 
     *    the same product
     * 2. bestPublicMarkets may have a size < =the size of bestMarkets
     * 3. bestPublicMarketsAtTop may have a size <= the size of bestMarkets
     */ 
    public static MarketDataStructsHolder[] organizeByProductsV1(CurrentMarketStruct[] bestMarkets,
            CurrentMarketStruct[] bestLimitMarkets,
            CurrentMarketStruct[] bestPublicMarkets,
            CurrentMarketStruct[] bestPublicMarketsAtTop)
    {
        MarketDataStructsHolder[] holders = new MarketDataStructsHolder[bestMarkets.length];
        int key;
        for (int i = 0; i < bestMarkets.length; i++){
            key = bestMarkets[i].productKeys.productKey;
            holders[i] = new MarketDataStructsHolderImpl(key, 
                    bestMarkets[i], 
                    bestLimitMarkets[i], 
                    findStructForProduct(bestPublicMarkets, key), 
                    findStructForProduct(bestPublicMarketsAtTop, key));
        } 
        if (Log.isDebugOn()){
            printOut(holders);
        }
        return holders;
    }
    
    public static MarketDataStructsHolder[] organizeByProductsV2(CurrentMarketStruct[] bestMarkets,
            CurrentMarketStruct[] bestLimitMarkets,
            CurrentMarketStruct[] bestPublicMarkets,
            CurrentMarketStruct[] bestPublicMarketsAtTop)
    {
        MarketDataStructsHolder[] holders = new MarketDataStructsHolder[bestMarkets.length];
        int key;
        for (int i = 0; i < bestMarkets.length; i++){
            key = bestMarkets[i].productKeys.productKey;
            holders[i] = new MarketDataStructsHolderImpl(key, 
                    bestMarkets[i], 
                    findStructForProduct(bestLimitMarkets, key), 
                    findStructForProduct(bestPublicMarkets, key), 
                    findStructForProduct(bestPublicMarketsAtTop, key));
        } 
        if (Log.isDebugOn()){
            printOut(holders);
        }
        return holders;
    }    
    
    
    public static CurrentMarketStruct findStructForProduct(CurrentMarketStruct[] aMarkets, int prodKey){
        CurrentMarketStruct result = null;
        if (aMarkets == null)
            return result;
        for (int i = 0; i < aMarkets.length; i++) {
            if (aMarkets[i].productKeys.productKey == prodKey){
                result = aMarkets[i];
                break;
            }
        } 
        return result;
    }
    
    private static void printOut(MarketDataStructsHolder[] holders){
        for ( int i = 0; i < holders.length; i++) {
            ReflectiveStructBuilder.printStruct(holders[i].getBestMarket(), "BestMarket");
            ReflectiveStructBuilder.printStruct(holders[i].getBestLimitMarket(), "BestLimitMarket");
            ReflectiveStructBuilder.printStruct(holders[i].getBestPublicMarket(), "BestPublicMarket");
            ReflectiveStructBuilder.printStruct(holders[i].getBestPublicMarketAtTop(), "BestPublicMarketAtTop");
        }
    }
    
//    public static MarketDataStructsHolder organize(CurrentMarketStruct bestMarket, 
//            CurrentMarketStruct bestLimitMarket,
//            CurrentMarketStruct bestPublicMarket,
//            CurrentMarketStruct bestPublicAtTop)
//    {
//        return new MarketDataStructsHolderImpl(bestMarket.productKeys.productKey,
//                    bestMarket,bestLimitMarket,bestPublicMarket,bestPublicAtTop);        
//    }
//    

    /**
     * change the inNBBO indicator for current market. 
     * 
     * Note: 
     * 
     * 0. The following 4 static methods were refactored from 
     *    MarketDataStrutsHolder
     * 1. The inNBBO indicator of current market is calculated based on 
     *    BestLimitMarket which is the data we publish to the outside world. 
     * 2. The inNBBO indicator of bestPublicMarket will be ignored, because 
     *    a calculation is required,  but it is not used anywhere.
     *    
     */ 
//    public static void bestLimitBidInNBBO(
//            CurrentMarketStruct bestMarketHeld,
//            CurrentMarketStruct bestLimitMarketHeld,
//            CurrentMarketStruct bestPublicMarketHeld,
//            CurrentMarketStruct bestPublicMarketAtTopHeld,
//            boolean isInNBBO)
//    {
//        updateBidInNBBOIndicator(bestMarketHeld, isInNBBO);
//        updateBidInNBBOIndicator(bestLimitMarketHeld, isInNBBO);
//        updateBidInNBBOIndicator(bestPublicMarketAtTopHeld, isInNBBO);
//    }
//    public static void bestLimitAskInNBBO(
//            CurrentMarketStruct bestMarketHeld,
//            CurrentMarketStruct bestLimitMarketHeld,
//            CurrentMarketStruct bestPublicMarketHeld,
//            CurrentMarketStruct bestPublicMarketAtTopHeld,
//            boolean isInNBBO){
//        updateAskInNBBOIndicator(bestMarketHeld, isInNBBO);
//        updateAskInNBBOIndicator(bestLimitMarketHeld, isInNBBO);
//        updateAskInNBBOIndicator(bestPublicMarketAtTopHeld, isInNBBO);        
//    }
//    
//    public static void updateBidInNBBOIndicator(CurrentMarketStruct aMarketView, boolean isInNBBO){
//        if ( aMarketView == null ||
//             aMarketView.bidPrice == null ||       
//             aMarketView.bidPrice.type == PriceTypes.NO_PRICE)
//        {
//            return;
//        }
//        aMarketView.bidIsMarketBest = isInNBBO;
//    }
//    
//    public static void updateAskInNBBOIndicator(CurrentMarketStruct aMarketView, boolean isInNBBO){
//        if ( aMarketView == null ||
//             aMarketView.askPrice == null ||       
//             aMarketView.askPrice.type == PriceTypes.NO_PRICE)
//        {
//            return;
//        }
//        aMarketView.askIsMarketBest = isInNBBO;
//    }      
//    
//   
    
    public static final int calcCustomerSize(MarketVolumeStruct[] volume)
    {
        int total = 0;

        if (volume != null)
        {
            for (int i = 0; i < volume.length; i++)
            {
                if (volume[i].volumeType == VolumeTypes.CUSTOMER_ORDER)
                {
                    total += volume[i].quantity;
                }
            }
        }
        return total;
    }
    
    public static final int combineNBBOVolumeFromExchanges(ExchangeVolumeStruct[] exchangeVols, int localNBBOSize)
    {
        int vol = localNBBOSize;
        if (exchangeVols == null) return vol;
        for (ExchangeVolumeStruct exVol : exchangeVols)
        {
            vol += exVol.volume;
        }
        return vol;
    }
    
    //The funny thing is only volume contingency is considered to be included in max/min calc
    //    but the non-contingent qty does not include ANY contignency qty
    //    so, there is a gap between non-contingent qty and contingent max qty
    //        in other words, we may see
    //           non-continget (limit) market $1.00 @ 100
    //           max-contingent market $1.20 @ 0, indicating some non-volume contingent orders are at $1.20
    // Adding nonQtyContingentQty to cover the gap, but not sure it is indeed doing any good
    //    say, IOC contingency, the qty may be cancelled on its own timer before a trade attempt.
    //       then the trade, especially involving remote market, can fail unexpectedly
    // On the other hand, it seems FOK or IOC shall never be booked, then the gap shall never exist.
    public static final int getMaxContingentQuantity(MarketVolumeStruct[] categorizedVolume)
    {
        if (categorizedVolume == null) return 0;
        //TODO: The way cross product leg trading is implemented makes this MIN/MAX not necessary
        //      but need total review of the DQ implementation. Leave it as 0 for now
        //TODO: leave it out for now, because the following code exists in BestBook.getContingent***QuantityMax
        //      and we do not want to have the logic in 2 places
        //Besides, we do not have MIN quantity here anyway, just mock it up to the same 0 as MIN quantities
        int maxQty = 0;
        for (int i=0; i<categorizedVolume.length; i++)
        {
            if (categorizedVolume[i].volumeType == VolumeTypes.AON)
            {
                maxQty +=categorizedVolume[i].quantity;
            }
            else if (categorizedVolume[i].volumeType == VolumeTypes.FOK)
            {
                maxQty +=categorizedVolume[i].quantity;
            }
        }
        return maxQty;
    }
    /**
     * this method is totally coupled with the above method
     *    but to avoid one more loop, keep it as is
     * @param categorizedVolume
     * @return
     */
    public static final int getTotalNonVcSize(MarketVolumeStruct[] categorizedVolume)
    {
        if (categorizedVolume == null) return 0;
        int totalQty = 0;
        for (int i=0; i<categorizedVolume.length; i++)
        {
            if (categorizedVolume[i].volumeType == VolumeTypes.AON)
            {
            }
            else if (categorizedVolume[i].volumeType == VolumeTypes.FOK)
            {
            }
            else if (categorizedVolume[i].volumeType == VolumeTypes.ODD_LOT)
            {
            }
            else
            {
                totalQty +=categorizedVolume[i].quantity;
            }
        }
        return totalQty;
    }
    public static final int getTotalSize(MarketVolumeStruct[] categorizedVolume)
    {
        if (categorizedVolume == null) return 0;
        int totalQty = 0;
        for (int i=0; i<categorizedVolume.length; i++)
        {
            if (categorizedVolume[i].volumeType == VolumeTypes.ODD_LOT)
            {
                continue;
            }
            totalQty +=categorizedVolume[i].quantity;
        }
        return totalQty;
    }
}
