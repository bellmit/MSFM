package com.cboe.interfaces.domain.exchange;

import com.cboe.exceptions.*;
import com.cboe.idl.exchange.*;

/**
 *  The interface definition for an Exchange object
 *
 *  @author Steven Sinclair
 */
public interface Exchange
{
	/**
	 *  @see com.cboe.domain.exchange.Exchange.getName()
	 */
	String getName();
	/**
	 *  @see com.cboe.domain.exchange.Exchange.getAcronym()
	 */
	String getAcronym();
	/**
	 *  @see com.cboe.domain.exchange.Exchange.getExchangeKey()
	 */
	int getExchangeKey();
	/**
	 *  @see com.cboe.domain.exchange.Exchange.setName(String)
	 */
	void setName(String name);
	/**
	 *  @see com.cboe.domain.exchange.Exchange.setAcronym(String)
	 */
	void setAcronym(String acronym);
	/**
	 *  @see com.cboe.domain.exchange.Exchange.fromStruct(ExchangeStruct)
	 */
	void fromStruct(ExchangeStruct exchangeStruct);
	/**
	 *  @see com.cboe.domain.exchange.Exchange.toStruct()
	 */
	ExchangeStruct toStruct();
}
