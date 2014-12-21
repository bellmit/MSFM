package com.cboe.interfaces.domain.exchange;

import com.cboe.exceptions.*;
import com.cboe.idl.exchange.ExchangeStruct;

/**
 *  A home for finding/creating/deleting Exchange objects.
 *
 *  @author Steven Sinclair
 */
public interface ExchangeHome
{
	public static final String HOME_NAME = "ExchangeHome";

	/**
	 *  @see com.cboe.domain.exchange.ExchangeHomeImpl.findAllExchanges()
	 */
	Exchange[] findAllExchanges()
		throws SystemException;

	/**
	 *  @see com.cboe.domain.exchange.ExchangeHomeImpl.findAllExchanges(int)
	 */
	Exchange findExchangeForKey(int exchangeKey)
		throws NotFoundException, SystemException;

	/**
	 *  @see com.cboe.domain.exchange.ExchangeHomeImpl.findExchangeForAcronym(String,int)
	 */
	Exchange findExchangeForAcronym(String acronym)
		throws NotFoundException, DataValidationException, SystemException;

	/**
	 *  @see com.cboe.domain.exchange.ExchangeHomeImpl.create(String,String,int)
	 */
	Exchange create(ExchangeStruct exchange)
		throws AlreadyExistsException, DataValidationException, SystemException, TransactionFailedException;

	/**
	 *  @see com.cboe.domain.exchange.ExchangeHomeImpl.remove(com.cboe.interfaces.domain.exchange.Exchange)
	 */
	void remove(Exchange exchange)
		throws SystemException, TransactionFailedException;

	/**
	 *  @see com.cboe.domain.exchange.ExchangeHomeImpl.remove(int)
	 */
	void removeByKey(int exchangeKey)
		throws NotFoundException, SystemException, TransactionFailedException;
}

