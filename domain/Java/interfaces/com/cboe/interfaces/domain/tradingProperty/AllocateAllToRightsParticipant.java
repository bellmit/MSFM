package com.cboe.interfaces.domain.tradingProperty;

public interface AllocateAllToRightsParticipant extends TradingProperty
{
    public static final short FOLLOW_REGULAR_STRATEGY = 0;
    public static final short ALLOCATE_ALL_TO_PMM = 1;
    public static final short ALLOCATE_RIGHTS_TO_PMM = 2;
}
