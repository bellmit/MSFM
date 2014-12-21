//
// -----------------------------------------------------------------------------------
// Source file: OrderBookPriceImpl.java
//
// PACKAGE: com.cboe.internalPresentation.bookDepth;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.bookDepth;

import com.cboe.idl.marketData.OrderBookPriceDetailStruct;

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.bookDepth.OrderBookPrice;
import com.cboe.interfaces.presentation.common.formatters.VolumeFormatStrategy;

import com.cboe.presentation.common.formatters.FormatFactory;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;

/**
 * Implmements the contract that provides book prices and qty's at that price.
 */
public class OrderBookPriceImpl extends AbstractBusinessModel implements OrderBookPrice
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

    private OrderBookPriceDetailStruct struct;

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
    public OrderBookPriceImpl(OrderBookPriceDetailStruct struct)
    {
        super();
        if(struct != null)
        {
            setOrderBookPriceStruct(struct);
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
     * Sets the struct that this Object represents
     * @param struct to represent
     */
    private void setOrderBookPriceStruct(OrderBookPriceDetailStruct struct)
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

        marketMakerVolume = new Integer(struct.marketMakerVolume);
        marketMakerVolumeString = volumeFormatter.format(struct.marketMakerVolume);

        this.customerVolume = new Integer(struct.customerVolume);
        this.customerVolumeString = volumeFormatter.format(struct.customerVolume);

        this.brokerDealerVolume = new Integer(struct.brokerDealerVolume);
        this.brokerDealerVolumeString = volumeFormatter.format(struct.brokerDealerVolume);

        this.contingencyVolume = new Integer(struct.contingencyVolume);
        this.contingencyVolumeString = volumeFormatter.format(struct.contingencyVolume);

        int volTotal = struct.marketMakerVolume + struct.customerVolume + struct.brokerDealerVolume;
        this.totalVolume = new Integer(volTotal);
        this.totalVolumeString = volumeFormatter.format(volTotal);
    }


}