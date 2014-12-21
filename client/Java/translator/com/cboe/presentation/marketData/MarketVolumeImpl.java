// -----------------------------------------------------------------------------------
// Source file: MarketVolumeImpl.java
//
// PACKAGE: com.cboe.presentation.marketData;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.marketData;

import com.cboe.interfaces.presentation.marketData.MarketVolume;
import com.cboe.interfaces.presentation.marketData.VolumeType;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;
import com.cboe.idl.cmiMarketData.MarketVolumeStruct;
import com.cboe.domain.util.MarketDataStructBuilder;

public class MarketVolumeImpl extends AbstractBusinessModel implements MarketVolume
{
    private MarketVolumeStruct struct;
    private Integer quantity;
    private VolumeType volumeType;
    private Boolean isMultipleParties;

    private MarketVolumeImpl()
    {
        super();
    }
    MarketVolumeImpl(MarketVolumeStruct struct)
    {
        this();
        if (struct == null)
        {
            throw new IllegalArgumentException("Market Volume struct can not be null");
        }
        this.struct = struct;
    }

    public Integer getQuantity()
    {
        if (quantity == null)
        {
            quantity = new Integer(struct.quantity);
        }
        return quantity;
    }

    public VolumeType getVolumeType()
    {
        if (volumeType == null)
        {
            volumeType = VolumeTypeImpl.getByKey(struct.volumeType);
        }
        return volumeType;
    }

    public Boolean isMultipleParties()
    {
        if (isMultipleParties == null)
        {
            isMultipleParties = new Boolean(struct.multipleParties);
        }
        return isMultipleParties;
    }

    public Object clone() throws CloneNotSupportedException
    {
        MarketVolumeStruct clonedStruct = MarketDataStructBuilder.cloneMarketVolumeStruct(this.struct);
        return MarketVolumeFactory.create(clonedStruct);
    }
}
