package com.cboe.domain.util;
import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiConstants.CurrentMarketViewTypes;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.util.ExceptionBuilder;

/**
 * @author Carol Vazirani
 *
 * This class has methods to process the currentMarketStructs coming
 * in through the event channel. It will support the processing for
 * multiple versions.
 * For Version 1, the contingentMarket struct is passed along, so
 * no processing required
 * For Version 2, this class will receive the best in contingentMarkets
 * and the Prof/Customer best in marketV2Struct. This class will be
 * responsible for consolidating the information into 1 marketV2Struct
 * This supports the case where the server sends the info in both structs.
 * For the third Version, the server sends all the info in MarketV2Struct.
 * This class will extract the current market into currentMarket struct. This supports
 * both old and new.
 * In both cases, both contingentMarkets and allMarkets will be populated
 * with the right information needed so both structs can be passed to the 
 * on two unique channel keys.
 * 
 */

public class CurrentMarketStructBuilder 
{
    private CurrentMarketStructBuilder()
    {
    }

    /**
     * Given a CurrentMarketV2 struct sequence, create a CurrentMarketStruct[] sequence
     * @return
     * @throws DataValidationException
     */
    public static CurrentMarketStruct[] buildCurrentMarketStruct(CurrentMarketStructV2[] currentMarketsV2)
            throws DataValidationException
    {
        //when all in one flag is true, means we have to generate a good contingentMarketArray
        if ( currentMarketsV2 == null || currentMarketsV2.length == 0)
        {
            if (Log.isDebugOn())
            {
                Log.debug("CurrentMarketStructBuilder Alert, 0 length structs received, no processing will be done");
            }
            throw ExceptionBuilder.dataValidationException("currentMarketsV2 struct is null or 0 length",0);
        }

        CurrentMarketStruct[] ecContingentMarkets = new CurrentMarketStruct[currentMarketsV2.length];
        int bestMarketIndex = 0;
        for (int index = 0; index < currentMarketsV2.length; index ++)
        {
            CurrentMarketStruct bestMarket = MarketDataStructBuilder.getContingencyBBO(currentMarketsV2[index]);
            if (bestMarket != null)
            {
                ecContingentMarkets[bestMarketIndex] = bestMarket;
            }
            
        }
        return ecContingentMarkets;
             
    }

    /****
     * buildCurrentMarketStructV2 - Creates a CurrentMarketStructV2 sequence containing only the Public
     * Offering at the Best Price.
     * @return
     * @throws DataValidationException
     */
    public static CurrentMarketStructV2[] buildCurrentMarketStructV2(CurrentMarketStructV2[] allMarkets)
            throws DataValidationException
    {
        //when all in one flag is true, means we have to generate a good contingentMarketArray
        if ( allMarkets == null )
        {
            if (Log.isDebugOn())
            {
                Log.debug("CurrentMarketStructBuilder Alert, 0 length structs received, no processing will be done");
            }
            throw ExceptionBuilder.dataValidationException("currentMarketsV2 struct is null or 0 length",0);
        }
        
        CurrentMarketStructV2[] custProfBestMarkets = new CurrentMarketStructV2[allMarkets.length]; 
        int bestMarketIndex = 0;
        for (int index = 0; index < allMarkets.length; index ++)
        {
            CurrentMarketStructV2 currentMarket = allMarkets[index];
            CurrentMarketStructV2 bestMarket = MarketDataStructBuilder.buildBestCustomerProfV2(currentMarket);
            if (bestMarket != null)
            {
                custProfBestMarkets[bestMarketIndex] = bestMarket;
            }
            
        }
        return custProfBestMarkets;
             
    }

    public static CurrentMarketStructV4 cloneCurrentMarketStructV4(CurrentMarketStructV4 struct)
    {
        CurrentMarketStructV4 newStruct = new CurrentMarketStructV4();

        newStruct.classKey = struct.classKey;
        newStruct.productKey = struct.productKey;
        newStruct.productType = struct.productType;
        newStruct.exchange = struct.exchange;
        newStruct.currentMarketType = struct.currentMarketType;
        newStruct.bidPrice = struct.bidPrice;
        newStruct.bidSizeSequence = cloneMarketVolumeStructsV4(struct.bidSizeSequence);
        newStruct.askPrice = struct.askPrice;
        newStruct.askSizeSequence = cloneMarketVolumeStructsV4(struct.askSizeSequence);
        newStruct.sentTime = struct.sentTime;
        newStruct.productState = struct.productState;
        newStruct.priceScale = struct.priceScale;

        return newStruct;
    }

    public static MarketVolumeStruct[] cloneMarketVolumeStructs(MarketVolumeStruct[] structs)
    {
        MarketVolumeStruct[] newStructs = new MarketVolumeStruct[structs.length];
        for(int i = 0; i < newStructs.length; i++)
        {
            newStructs[i] = cloneMarketVolumeStruct(structs[i]);
        }
        return newStructs;
    }

    public static MarketVolumeStruct cloneMarketVolumeStruct(MarketVolumeStruct struct)
    {
        MarketVolumeStruct newStruct = MarketDataStructBuilder.getMarketVolumeStruct(struct.volumeType, struct.quantity, struct.multipleParties);
        return newStruct;
    }

    public static MarketVolumeStructV4[] cloneMarketVolumeStructsV4(MarketVolumeStructV4[] structs)
    {
        MarketVolumeStructV4[] newStructs = new MarketVolumeStructV4[structs.length];
        for(int i = 0; i < newStructs.length; i++)
        {
            newStructs[i] = cloneMarketVolumeStructV4(structs[i]);
        }
        return newStructs;
    }

    public static MarketVolumeStructV4 cloneMarketVolumeStructV4(MarketVolumeStructV4 struct)
    {
        MarketVolumeStructV4 newStruct = new MarketVolumeStructV4();
        newStruct.multipleParties = struct.multipleParties;
        newStruct.quantity = struct.quantity;
        newStruct.volumeType = struct.volumeType;
        return newStruct;
    }
}
