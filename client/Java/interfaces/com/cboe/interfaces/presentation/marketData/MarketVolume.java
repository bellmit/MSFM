// -----------------------------------------------------------------------------------
// Source file: MarketVolume.java
//
// PACKAGE: com.cboe.interfaces.presentation.marketData;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.marketData;

import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

public interface MarketVolume extends BusinessModel
{
    public VolumeType getVolumeType();
    public Integer getQuantity();
    public Boolean isMultipleParties();

}
