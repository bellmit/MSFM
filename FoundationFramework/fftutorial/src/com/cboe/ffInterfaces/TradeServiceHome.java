package com.cboe.ffInterfaces;

public interface TradeServiceHome
{
    static final String HOME_NAME="TradeServiceHome";

    TradeService find();

    TradeService create();
}
