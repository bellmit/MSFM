package com.cboe.interfaces.application;

public interface TradingSessionServiceAdapterHome
{
    public static final String HOME_NAME = "TradingSessionServiceAdapterHome";
    
    public TradingSessionServiceAdapter create();
    public TradingSessionServiceAdapter find();
}
