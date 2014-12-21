// -----------------------------------------------------------------------------------
// Source file: DetailOrderBookPrice.java
//
// PACKAGE: com.cboe.interfaces.presentation.bookDepth;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.bookDepth;

/**
 * Provides a contract that provides book prices and qty's at that price.
 */
public interface DetailOrderBookPrice extends OrderBookPrice
{
    /**
     * Gets the total volume at this price formatted as a String
     */
    public OrderBookPriceView[] getPriceViews();
    /**
     * Gets the AON contigency volume at this price
     */
    public Integer getAONVolume();

    /**
     * Gets the FOK contigency volume at this price
     */
    public Integer getFOKVolume();

    /**
     * Gets the IOC contigency volume at this price
     */
    public Integer getIOCVolume();

    /**
     * Gets the Limit contigency volume at this price
     */
    public Integer getLimitVolume();

    /**
     * Gets the NO contigency volume at this price
     */
    public Integer getNoContingencyVolume();
    /**
     * Gets the AON contigency volume at this price formatted as a String
     */
    public String getAONVolumeString();

    /**
     * Gets the FOK contigency volume at this price formatted as a String
     */
    public String getFOKVolumeString();

    /**
     * Gets the IOC contigency volume at this price formatted as a String
     */
    public String getIOCVolumeString();

    /**
     * Gets the Limit contigency volume at this price formatted as a String
     */
    public String getLimitVolumeString();

    /**
     * Gets the NO contigency volume at this price formatted as a String
     */
    public String getNoContingencyVolumeString();

}