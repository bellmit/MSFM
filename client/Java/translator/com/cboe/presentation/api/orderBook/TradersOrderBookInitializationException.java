package com.cboe.presentation.api.orderBook;

/**
 * This exception is thrown by the TradersOrderBook constructor
 * when it fails to complete its initialization
 */
public class TradersOrderBookInitializationException extends Exception
{
    public TradersOrderBookInitializationException(String message)
    {
        super(message);
    }
}
