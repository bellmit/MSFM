//
// -----------------------------------------------------------------------------------
// Source file: MarketVolumeFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.idl.cmiMarketData.MarketVolumeStruct;
import com.cboe.idl.cmiMarketData.MarketVolumeStructV4;
import com.cboe.idl.cmiConstants.VolumeTypes;

import com.cboe.presentation.marketData.MarketVolumeStructHelper;

import com.cboe.interfaces.presentation.common.formatters.MarketVolumeFormatStrategy;

/**
 * Responsible for formatting MarketVolume Array Structs
 * @author Nick DePasquale
 */
class MarketVolumeFormatter extends Formatter implements MarketVolumeFormatStrategy
{
/**
 */
    public MarketVolumeFormatter()
    {
        super();

        addStyle(REAL_VOLUME_NAME , REAL_VOLUME_DESCRIPTION);
        addStyle(REAL_VOLUME_PLUS_NAME , REAL_VOLUME_PLUS_DESCRIPTION);
        addStyle(REAL_CONTINGENT_VOLUME_NAME , REAL_CONTINGENT_VOLUME_DESCRIPTION);
        addStyle(PUBLIC_VOLUME_NAME, PUBLIC_VOLUME_DESCRIPTION);

        setDefaultStyle(REAL_VOLUME_NAME);
    }
    /**
     * Implements format definition from BustFormatStrategy
     */
    public String format(MarketVolumeStruct[] volume)
    {
        return format(volume, getDefaultStyle());
    }

    /**
     * Implements format definition from BustFormatStrategy
     */
    public String format(MarketVolumeStruct[] volume, String styleName)
    {
        return format(volume, styleName, MarketVolumeStructHelper.UNSPECIFIED_PRODUCT_TYPE);
    }

    /**
     * If productType is ProductTypes.EQUITY, then VolumeTypes.QUOTES will be included in the returned quantity.
     */
    public String format(MarketVolumeStruct[] volume, String styleName, short productType)
    {
        validateStyle(styleName);
        StringBuffer volumeText = new StringBuffer(22);

        if(styleName.equals(REAL_VOLUME_NAME))
        {
            volumeText.append( getQuantity(volume, productType) );
        }
        else if(styleName.equals(REAL_VOLUME_PLUS_NAME))
        {
            getQuantityPlus(volumeText, volume, productType);
        }
        else if(styleName.equals(REAL_CONTINGENT_VOLUME_NAME))
        {
            getQuantityPlusVolumeContingency(volumeText, volume, productType);
        }
        else if(styleName.equals(PUBLIC_VOLUME_NAME))
        {
            getPublicVolumes(volumeText, volume);
        }

        return volumeText.toString();
    }

    public String format(MarketVolumeStructV4[] volume, String styleName)
    {
        validateStyle(styleName);
        StringBuffer volumeText = new StringBuffer(22);

        if(styleName.equals(REAL_VOLUME_NAME))
        {
            volumeText.append(getQuantity(volume));
        }
        else if(styleName.equals(REAL_VOLUME_PLUS_NAME))
        {
            getQuantityPlus(volumeText, volume);
        }
        else if(styleName.equals(REAL_CONTINGENT_VOLUME_NAME))
        {
            getQuantityPlusVolumeContingency(volumeText, volume);
        }
        else if(styleName.equals(PUBLIC_VOLUME_NAME))
        {
            getPublicVolumes(volumeText, volume);
        }

        return volumeText.toString();
    }

    private String getQuantity(MarketVolumeStruct[] volume, short productType)
    {
        return MarketVolumeStructHelper.getQuantity(volume, productType);
    }

    private String getQuantity(MarketVolumeStructV4[] volume)
    {
        return MarketVolumeStructHelper.getQuantity(volume);
    }

    private boolean getQuantityPlus(StringBuffer buffer, MarketVolumeStruct[] volume, short productType)
    {
        boolean containsVolumeContingency = false;
        buffer.append(getQuantity(volume, productType));
        if (MarketVolumeStructHelper.containsVolumeContingency(volume))
        {
            buffer.append('+');
            containsVolumeContingency = true;
        }
        return containsVolumeContingency;
    }

    private boolean getQuantityPlus(StringBuffer buffer, MarketVolumeStructV4[] volume)
    {
        boolean containsVolumeContingency = false;
        buffer.append(getQuantity(volume));
        if(MarketVolumeStructHelper.containsVolumeContingency(volume))
        {
            buffer.append('+');
            containsVolumeContingency = true;
        }
        return containsVolumeContingency;
    }

    private boolean getQuantityPlusVolumeContingency(StringBuffer buffer, MarketVolumeStruct[] volume, short productType)
    {
        boolean contingencyMultipleParty = false;
        boolean quantityPlus = getQuantityPlus(buffer, volume, productType);
        if(quantityPlus)
        {
            buffer.append(MarketVolumeStructHelper.getVolumeContingencyQuantity(volume));
            if (MarketVolumeStructHelper.isVolumeContingencyMultipleParty(volume) == true)
            {
                buffer.append("*");
                contingencyMultipleParty = true;
            }
        }
        return contingencyMultipleParty;
    }

    private boolean getQuantityPlusVolumeContingency(StringBuffer buffer, MarketVolumeStructV4[] volume)
    {
        boolean contingencyMultipleParty = false;
        boolean quantityPlus = getQuantityPlus(buffer, volume);
        if(quantityPlus)
        {
            buffer.append(MarketVolumeStructHelper.getVolumeContingencyQuantityValue(volume));
            if(MarketVolumeStructHelper.isVolumeContingencyMultipleParty(volume) == true)
            {
                buffer.append("*");
                contingencyMultipleParty = true;
            }
        }
        return contingencyMultipleParty;
    }

    private void getPublicVolumes(StringBuffer buffer, MarketVolumeStruct[] volume)
    {
        int customerTotal = 0;
        int professionalTotal = 0;
        if (volume != null)
        {
            for (int i = 0; i < volume.length; ++i)
            {
                if (volume[i].volumeType == VolumeTypes.PROFESSIONAL_ORDER)
                {
                    professionalTotal += volume[i].quantity;
                }
                else if (volume[i].volumeType == VolumeTypes.CUSTOMER_ORDER)
                {
                    customerTotal += volume[i].quantity;
                }
            }
        }
        buffer.append("Prof: ");
        buffer.append(professionalTotal);
        buffer.append("  Cust: ");
        buffer.append(customerTotal);
    }

    private void getPublicVolumes(StringBuffer buffer, MarketVolumeStructV4[] volume)
    {
        MarketVolumeStruct[] v3Structs = MarketVolumeStructHelper.convertV4MarketVolumeStructsToV3(volume);
        getPublicVolumes(buffer, v3Structs);
    }
}
