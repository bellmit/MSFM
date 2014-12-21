package com.cboe.interfaces.domain;

import com.cboe.idl.cmiIntermarketMessages.ExchangeMarketStruct;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.NotFoundException;

/**
 * OrderExchangeMarketHome
 *
 * @author Mei Wu
 * date Dec 5, 2002
 */

public interface OrderExchangeMarketHome
{
    public final static String HOME_NAME = "OrderExchangeMarketHome";
    public long create(ExchangeMarketStruct exchangeMkt) throws DataValidationException, TransactionFailedException, SystemException;
    public OrderExchangeMarket find(long echangeMktId)throws NotFoundException, TransactionFailedException, SystemException;
    public void deleteOrderExchangeMarketFromCache(long exchangeMarketId);
}