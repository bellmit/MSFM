//
// -----------------------------------------------------------------------------------
// Source file: OrderBookPrice.java
//
// PACKAGE: com.cboe.interfaces.presentation.bookDepth;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.bookDepth;

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

/**
 * Provides a contract that provides book prices and qty's at that price.
 */
public interface OrderBookPrice extends BusinessModel
{
    /**
     * Gets the price for this book entry
     */
    public Price getPrice();

    /**
     * Gets the market maker volume at this price
     */
    public Integer getMarketMakerVolume();

    /**
     * Gets the customer volume at this price
     */
    public Integer getCustomerVolume();

    /**
     * Gets the broker/dealer volume at this price
     */
    public Integer getBrokerDealerVolume();

    /**
     * Gets the contigency volume at this price
     */
    public Integer getContingencyVolume();

    /**
     * Gets the total volume at this price
     */
    public Integer getTotalVolume();

    /**
     * Gets the market maker volume at this price formatted as a String
     */
    public String getMarketMakerVolumeString();

    /**
     * Gets the customer volume at this price formatted as a String
     */
    public String getCustomerVolumeString();

    /**
     * Gets the broker/dealer volume at this price formatted as a String
     */
    public String getBrokerDealerVolumeString();

    /**
     * Gets the contigency volume at this price formatted as a String
     */
    public String getContingencyVolumeString();


    /**
     * Gets the total volume at this price formatted as a String
     */
    public String getTotalVolumeString();
}