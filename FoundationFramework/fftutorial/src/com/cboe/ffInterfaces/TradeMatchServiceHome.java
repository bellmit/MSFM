package com.cboe.ffInterfaces;

public interface TradeMatchServiceHome
{
    static final String HOME_NAME="TradeMatchServiceHome";

    TradeMatchService find();

    TradeMatchService create();
}
