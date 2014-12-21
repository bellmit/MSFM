package com.cboe.interfaces.domain;

import com.cboe.idl.cmiIntermarketMessages.ExchangeMarketStruct;
import com.cboe.exceptions.DataValidationException;

/**
 * OrderExchangeMarket
 *
 * @author Mei Wu
 * date Dec 5, 2002
 */
public interface OrderExchangeMarket
{
    public void create( ExchangeMarketStruct exchangeMarket) throws DataValidationException;
    public ExchangeMarketStruct toStruct();
}
