package com.cboe.interfaces.domain;

import com.cboe.idl.cmiIntermarketMessages.ExchangeMarketStruct;
import com.cboe.idl.cmiIntermarketMessages.ExchangeMarketStructV2;
import com.cboe.exceptions.DataValidationException;


/**
 * AlertExchangeMarket
 *
 * @author baranski
 * date Oct 28, 2002
 */

public interface AlertExchangeMarket {
    public void create(Alert alert, ExchangeMarketStruct exchangeMarket) throws DataValidationException;
    public void createV2(Alert alert, ExchangeMarketStructV2 exchangeMarket) throws DataValidationException;
    public ExchangeMarketStruct toStruct();
}
