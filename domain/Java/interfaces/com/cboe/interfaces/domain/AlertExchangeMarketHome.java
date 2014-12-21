package com.cboe.interfaces.domain;

import com.cboe.idl.cmiIntermarketMessages.ExchangeMarketStruct;
import com.cboe.idl.cmiIntermarketMessages.ExchangeMarketStructV2;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.exceptions.SystemException;

/**
 * AlertExchangeMarketHome
 *
 * @author baranski
 * date Oct 28, 2002
 */

public interface AlertExchangeMarketHome {
    public final static String HOME_NAME = "AlertExchangeMarketHome";
    public AlertExchangeMarket create(Alert alert, ExchangeMarketStruct exchangeMkt) throws DataValidationException, TransactionFailedException, SystemException;
    public AlertExchangeMarket createV2(Alert alert, ExchangeMarketStructV2 exchangeMkt) throws DataValidationException, TransactionFailedException, SystemException;
}