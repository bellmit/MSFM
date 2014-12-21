package com.cboe.domain.util;

import com.cboe.idl.cmiConstants.AuctionTypesOperations;

/**
 * This class is designed to provide the facility to define internal auction types
 */

public class InternalAuctionTypes implements AuctionTypesOperations
{
    public static final short AUCTION_HALO = (short) -4;
    public static final short AUCTION_NEW_HAL = (short) -8;
}
