/*
 *  Copyright 2000
 *
 *  CBOE
 *  All rights reserved
 */
package com.cboe.presentation.marketData;
import com.cboe.idl.cmiConstants.VolumeTypes;
import com.cboe.idl.cmiConstants.MultiplePartiesIndicators;
import com.cboe.idl.cmiConstants.ProductTypes;

import com.cboe.idl.cmiMarketData.MarketVolumeStruct;
import com.cboe.idl.cmiMarketData.MarketVolumeStructV4;

/**
 *  Description of the Class
 *
 *@author     Nick DePasquale
 *@created    December 6, 2000
 */
public class MarketVolumeStructHelper
{
    public static final short UNSPECIFIED_PRODUCT_TYPE = -1;

    /**
     *  Constructor for the MarketVolumeStructHelper object
     */
    public MarketVolumeStructHelper()
    {
    }

    /**
     * Converts array of MarketVolumeStructV4 to array of MarketVolumeStructV3.
     * @param v4Structs
     * @return MarketVolumeStruct[]
     */
    public static MarketVolumeStruct[] convertV4MarketVolumeStructsToV3(MarketVolumeStructV4[] v4Structs)
    {
        MarketVolumeStruct[] v3Structs = new MarketVolumeStruct[v4Structs.length];
        for(int i = 0; i < v4Structs.length; i++)
        {
            v3Structs[i] = convertV4MarketVolumeStructToV3(v4Structs[i]);
        }
        return v3Structs;
    }

    /**
     * Converts a MarketVolumeStructV4 to a MarketVolumeStructV3.
     *
     * In the new V3 struct, the multipleParties field will be true if the V4 struct's
     * multipleParties field was MultiplePartiesIndicators.YES; it will be false in
     * the V3 struct if the V4 was either MultiplePartiesIndicators.NO or
     * MultiplePartiesIndicators.UNKNOWN.
     *
     * @param v4Struct
     * @return MarketVolumeStruct
     */
    public static MarketVolumeStruct convertV4MarketVolumeStructToV3(MarketVolumeStructV4 v4Struct)
    {
        // multiParty = false if MultiplePartiesIndicators.NO or MultiplePartiesIndicators.UNKNOWN
        boolean multiParty = (v4Struct.multipleParties == MultiplePartiesIndicators.YES);
        return new MarketVolumeStruct(v4Struct.volumeType, v4Struct.quantity, multiParty);
    }

    /**
     *  Finds the Limit and IOC category in the MarketVolumeStruct array and
     *  returns the value
     *
     *@param  volume  Description of Parameter
     *@return         int
     */
    public static int getQuantityValue(MarketVolumeStruct[] volume)
    {
        return getQuantityValue(false, volume);
    }

    /**
     *  Finds the Limit, IOC categories in the MarketVolumeStruct array and
     *  returns the value.  If includeQuotes is true, it will also include VolumeTypes.QUOTES.
     */
    public static int getQuantityValue(boolean includeQuotes, MarketVolumeStruct[] volume)
    {
        int qty = 0;
        int x;

        for (x = 0; x < volume.length; x++)
        {
            if (volume[x] == null)
            {
                continue;
            }

            if(volume[x].volumeType == VolumeTypes.LIMIT || volume[x].volumeType == VolumeTypes.IOC ||
               (includeQuotes && volume[x].volumeType == VolumeTypes.QUOTES))
            {
                qty += volume[x].quantity;
            }

        }

        return qty;
    }

    /**
     * Finds the Limit, IOC, and Quotes categories in the MarketVolumeStructV4 array and returns the value
     * @param volume Description of Parameter
     * @return int
     */
    public static int getQuantityValue(MarketVolumeStructV4[] volume)
    {
        MarketVolumeStruct[] v3Structs = convertV4MarketVolumeStructsToV3(volume);
        return getQuantityValue(true, v3Structs);
    }

    /**
     *  Finds the Limit and IOC category in the MarketVolumeStruct array and
     *  returns as a string this value
     *
     *@param  volume  Description of Parameter
     *@return         java.lang.String
     */
    public static String getQuantity(MarketVolumeStruct[] volume)
    {
        return getQuantity(volume, UNSPECIFIED_PRODUCT_TYPE);
    }

    /**
     * If productType is ProductTypes.EQUITY, then VolumeTypes.QUOTES will be included in the returned quantity.
     * @param volume
     * @param productType
     * @return
     */
    public static String getQuantity(MarketVolumeStruct[] volume, short productType)
    {
        boolean includeQuotes = false;
        if(productType == ProductTypes.EQUITY)
        {
            includeQuotes = true;
        }
        return Integer.toString(getQuantityValue(includeQuotes, volume));
    }

    /**
     * Finds the Limit and IOC category in the MarketVolumeStructV4 array and returns as a string this value
     * @param volume Description of Parameter
     * @return java.lang.String
     */
    public static String getQuantity(MarketVolumeStructV4[] volume)
    {
        return Integer.toString(getQuantityValue(volume));
    }

    /**
     *  Finds the FOK and AON categories in the MarketVolumeStruct array and
     *  returns as a string this value
     *
     *@param  volume  Description of Parameter
     *@return         java.lang.String
     */
    public static int getVolumeContingencyQuantityValue(MarketVolumeStruct[] volume)
    {

        int qty = 0;
        int x;

        for (x = 0; x < volume.length; x++)
        {
            if (volume[x] == null)
            {
                continue;
            }
            if (volume[x].volumeType == VolumeTypes.AON || volume[x].volumeType == VolumeTypes.FOK)
            {
                qty += volume[x].quantity;
            }

        }

        return qty;
    }

    /**
     *  Finds the FOK and AON categories in the MarketVolumeStruct array and
     *  returns as a string this value
     *
     *@param  volume  Description of Parameter
     *@return         java.lang.String
     */
    public static String getVolumeContingencyQuantity(MarketVolumeStruct[] volume)
    {
        return Integer.toString(getVolumeContingencyQuantityValue(volume));
    }

    public static int getVolumeContingencyQuantityValue(MarketVolumeStructV4[] volume)
    {
        MarketVolumeStruct[] v3Structs = convertV4MarketVolumeStructsToV3(volume);
        return getVolumeContingencyQuantityValue(v3Structs);
    }

    /**
     *  Finds the FOK and AON categories in the MarketVolumeStruct array and
     *  returns "*" if there is volume else returns ""
     *
     *@param  volume  Description of Parameter
     *@return         java.lang.String
     */
    public static String getVolumeContingencyIndicator(MarketVolumeStruct[] volume)
    {
        String str;

        if (containsVolumeContingency(volume))
        {
            str = "*";
        }
        else
        {
            str = "";
        }

        return str;
    }

    /**
     *  Returns true if - any volume contingency has multilpe parties or - there
     *  is more than 1 type of volume contingency Else returns false
     *
     *@param  volume  Description of Parameter
     *@return         boolean
     */
    public static boolean isVolumeContingencyMultipleParty(MarketVolumeStruct[] volume)
    {
        boolean ret = false;
        int qty = 0;
        int x;

        for (x = 0; x < volume.length; x++)
        {
            if (volume[x] == null)
            {
                continue;
            }

            if (volume[x].volumeType == VolumeTypes.AON || volume[x].volumeType == VolumeTypes.FOK)
            {
                qty++;
                if (volume[x].multipleParties == true)
                {
                    qty++;
                }
            }
        }

        if (qty > 1)
        {
            ret = true;
        }

        return ret;
    }

    public static boolean isVolumeContingencyMultipleParty(MarketVolumeStructV4[] volume)
    {
        MarketVolumeStruct[] v3Structs = convertV4MarketVolumeStructsToV3(volume);
        return isVolumeContingencyMultipleParty(v3Structs);
    }

    /**
     *  Gets the FOKorIOCBestOnly attribute of the MarketVolumeStructHelper
     *  class
     *
     *@param  volume  Description of Parameter
     *@return         if FOK or IOC is at the Best price and no other type orders
     */
    public static boolean isFOKorIOCBestOnly(MarketVolumeStruct[] volume)
    {
        boolean ret = false;
        int qtyOther = 0;
        int qtyIOCFOK = 0;
        int x;

        for (x = 0; x < volume.length; x++)
        {
            if (volume[x] == null)
            {
                continue;
            }

            if (volume[x].volumeType == VolumeTypes.IOC || volume[x].volumeType == VolumeTypes.FOK)
            {
                qtyIOCFOK += volume[x].quantity;
                break;
            }
            else
            {
                qtyOther++;
            }

        }

        if (qtyIOCFOK > 0 && qtyOther == 0)
        {
            ret = true;
        }

        return ret;
    }

    /**
     *  Finds the FOK and AON categories in the MarketVolumeStruct array and
     *  returns true if volume else returns false
     *
     *@param  volume  Description of Parameter
     *@return         java.lang.String
     */
    public static boolean containsVolumeContingency(MarketVolumeStruct[] volume)
    {
        boolean ret = false;
        int qty = 0;
        int x;

        for (x = 0; x < volume.length; x++)
        {
            if (volume[x] == null)
            {
                continue;
            }

            if (volume[x].volumeType == VolumeTypes.AON || volume[x].volumeType == VolumeTypes.FOK)
            {
                qty += volume[x].quantity;
                break;
            }

        }

        if (qty > 0)
        {
            ret = true;
        }

        return ret;
    }

    public static boolean containsVolumeContingency(MarketVolumeStructV4[] volume)
    {
        MarketVolumeStruct[] v3Structs = convertV4MarketVolumeStructsToV3(volume);
        return containsVolumeContingency(v3Structs);
    }
}
