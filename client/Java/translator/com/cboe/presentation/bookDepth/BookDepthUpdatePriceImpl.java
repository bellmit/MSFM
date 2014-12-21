//
// -----------------------------------------------------------------------------------
// Source file: BookDepthUpdatePriceImpl.java
//
// PACKAGE: com.cboe.presentation.bookDepth;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.bookDepth;

import com.cboe.idl.cmiMarketData.BookDepthUpdatePriceStruct;

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.bookDepth.BookDepthUpdatePrice;
import com.cboe.interfaces.presentation.common.formatters.VolumeFormatStrategy;

import com.cboe.presentation.common.formatters.FormatFactory;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;

/**
 * Implmements the contract that provides book depth updates for prices and qty's
 */
public class BookDepthUpdatePriceImpl extends AbstractBusinessModel implements BookDepthUpdatePrice
{
    protected static VolumeFormatStrategy volumeFormatter;
    protected Price price;

    protected Integer marketMakerVolume;
    protected Integer customerVolume;
    protected Integer brokerDealerVolume;
    protected Integer contingencyVolume;
    protected Integer totalVolume;

    protected String marketMakerVolumeString;
    protected String customerVolumeString;
    protected String brokerDealerVolumeString;
    protected String contingencyVolumeString;
    protected String totalVolumeString;

    private BookDepthUpdatePriceStruct struct;

    /*
     * Provides static initialization of formatter which is static to this class
     */
    static
    {
        volumeFormatter = FormatFactory.getVolumeFormatStrategy();
    }

    /**
     * Default constructor
     * @param struct to represent
     */
    public BookDepthUpdatePriceImpl(BookDepthUpdatePriceStruct struct)
    {
        super();
        if(struct != null)
        {
            setStruct(struct);
        }
    }

    /**
     * Gets the price for this book entry
     */
    public Price getPrice()
    {
        return price;
    }

    /**
     * Gets the market maker volume at this price
     */
    public Integer getMarketMakerVolume()
    {
        return marketMakerVolume;
    }

    /**
     * Gets the customer volume at this price
     */
    public Integer getCustomerVolume()
    {
        return customerVolume;
    }

    /**
     * Gets the broker/dealer volume at this price
     */
    public Integer getBrokerDealerVolume()
    {
        return brokerDealerVolume;
    }

    /**
     * Gets the contigency volume at this price
     */
    public Integer getContingencyVolume()
    {
        return contingencyVolume;
    }

    /**
     * Gets the total volume at this price
     */
    public Integer getTotalVolume()
    {
        return totalVolume;
    }

    /**
     * Gets the market maker volume at this price formatted as a String
     */
    public String getMarketMakerVolumeString()
    {
        return marketMakerVolumeString;
    }

    /**
     * Gets the customer volume at this price formatted as a String
     */
    public String getCustomerVolumeString()
    {
        return customerVolumeString;
    }

    /**
     * Gets the broker/dealer volume at this price formatted as a String
     */
    public String getBrokerDealerVolumeString()
    {
        return brokerDealerVolumeString;
    }

    /**
     * Gets the contigency volume at this price formatted as a String
     */
    public String getContingencyVolumeString()
    {
        return contingencyVolumeString;
    }

    /**
     * Gets the total volume at this price formatted as a String
     */
    public String getTotalVolumeString()
    {
        return totalVolumeString;
    }

    /**
     * Gets the update type
     */
    public char getUpdateType()
    {
        return struct.updateType;
    }

    /**
     * Sets the struct that this Object represents
     * @param struct to represent
     */
    private void setStruct(BookDepthUpdatePriceStruct struct)
    {
        this.struct = struct;

        if(struct.price != null)
        {
            this.price = DisplayPriceFactory.create(struct.price);
        }
        else
        {
            this.price = DisplayPriceFactory.create(0.00);
        }

        marketMakerVolume = new Integer(0);
        marketMakerVolumeString = volumeFormatter.format(0);

        this.customerVolume = new Integer(0);
        this.customerVolumeString = volumeFormatter.format(0);

        this.brokerDealerVolume = new Integer(0);
        this.brokerDealerVolumeString = volumeFormatter.format(0);

        this.contingencyVolume = new Integer(struct.contingencyVolume);
        this.contingencyVolumeString = volumeFormatter.format(struct.contingencyVolume);

        this.totalVolume = new Integer(struct.totalVolume);
        this.totalVolumeString = volumeFormatter.format(struct.totalVolume);
    }
}
