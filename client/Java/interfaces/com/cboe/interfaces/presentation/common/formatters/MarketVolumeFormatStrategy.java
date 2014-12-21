//
// -----------------------------------------------------------------------------------
// Source file: MarketVolumeFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.idl.cmiMarketData.MarketVolumeStruct;
import com.cboe.idl.cmiMarketData.MarketVolumeStructV4;

/**
 * Defines a contract for a class that formats MarketVolumeStrategy structs.
 * @author Nick DePasquale
 */
public interface MarketVolumeFormatStrategy extends FormatStrategy
{
/**
 * Defines a method for formatting order MarketVolumeStruct's.
 * @param MarketVolumeStruct
 * @return String
 */
 public String format(MarketVolumeStruct [] volume);
/**
 * Defines a method for formatting order MarketVolumeStruct's.
 * @param MarketVolumeStruct
 * @param styleName to use for formatting
 * @return String
 */
public String format(MarketVolumeStruct [] volume, String styleName);

/**
 * If productType is ProductTypes.EQUITY, then VolumeTypes.QUOTES will be included in the returned quantity.
 */
public String format(MarketVolumeStruct [] volume, String styleName, short productType);

public String format(MarketVolumeStructV4[] volume, String styleName);

public static final String REAL_VOLUME_NAME = "Real Volume";
public static final String REAL_VOLUME_PLUS_NAME =  "Real Volume+";
public static final String REAL_CONTINGENT_VOLUME_NAME = "Complete Volume";
public static final String PUBLIC_VOLUME_NAME = "Cust & Prof Volume";

public static final String REAL_VOLUME_DESCRIPTION = "Volume consisting of IOC and Limit orders";
public static final String REAL_VOLUME_PLUS_DESCRIPTION =  "Volume of IOC and Limit orders and an indicator (+) to indicate there is some volume contingency.";
public static final String REAL_CONTINGENT_VOLUME_DESCRIPTION = "Displays 2 Volumes - Volume consisting of IOC and Limit orders and volume of AON and FOK orders";
public static final String PUBLIC_VOLUME_DESCRIPTION = "Customer and Professional Quantity At Top";

}
