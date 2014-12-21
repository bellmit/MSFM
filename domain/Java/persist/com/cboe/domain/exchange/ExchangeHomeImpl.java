package com.cboe.domain.exchange;

import com.cboe.exceptions.*;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.exchange.ExchangeStruct;
import com.cboe.util.ExceptionBuilder;
import com.cboe.interfaces.domain.exchange.*;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import com.cboe.infrastructureServices.persistenceService.ObjectQuery;
import java.util.*;

import junit.framework.*;

/**
 *  A persistent implementation of the ExchangeHome interface
 *
 *  @author Steven Sinclair
 */
public class ExchangeHomeImpl extends BOHome implements ExchangeHome
{
	/**
	 *  Return an array of all known exchanges
	 *
	 *  @throws SystemException - thrown if there's a low-level persistence problem.
	 *  @return Exchange[] - all exchanges
	 */
	public Exchange[] findAllExchanges()
		throws SystemException
	{
		Vector exchanges = findByExample(newExchangeImpl());
		Exchange[] resultArray = new Exchange[exchanges.size()];
		exchanges.copyInto(resultArray);
		return resultArray;
	}

	protected ExchangeImpl newExchangeImpl()
	{
		ExchangeImpl ex = new ExchangeImpl();
		addToContainer(ex);
		return ex;
	}

	/**
	 *  Return the exchange whose key is <code>exchangeKey</code>.
	 *
	 *  @param exchangeKey - the key for an exchange.
	 *  @throws DataValidationException - thrown if the exchange cannot be found.
	 *  @throws SystemException - thrown if there's a low-level persistence problem.
	 * 	@return Exchange - the exchange found.
	 */
	public Exchange findExchangeForKey(int exchangeKey)
		throws NotFoundException, SystemException
	{
		ExchangeImpl queryExample = newExchangeImpl();
		queryExample.setObjectIdentifierFromInt(exchangeKey);
		return findUnique(queryExample);
	}

	/**
	 *  @throws SystemException - thrown if there's a low-level persistence problem.
	 *  @throws DataValidationException - thrown if the exchange cannot be found.
	 * 	@return Exchange - the exchange found.
	 */
	public Exchange findExchangeForAcronym(String acronym)
		throws NotFoundException, DataValidationException, SystemException
	{
		ExchangeImpl queryExample = newExchangeImpl();
		queryExample.setAcronym(acronym);
		return findUnique(queryExample);
	}

	/**
	 *  Create a new persistent instance of the Exchange interface.
	 *
	 *  @param name - the name of the new exchange
	 *  @param acronym - the acronym for the new exchange
	 *
	 *  @throws SystemException - thrown if there's a low-level persistence problem.
	 *  @throws AlreadyExistsException - thrown if the acronym already exists.
	 * 	@return Exchange - the exchange found.
	 */
	public Exchange create(ExchangeStruct exchangeStruct)
		throws AlreadyExistsException, DataValidationException, TransactionFailedException, SystemException
	{
		try
		{
			findExchangeForAcronym(exchangeStruct.acronym);
			throw ExceptionBuilder.alreadyExistsException("Cannot create: exchange already exists for " + exchangeStruct.acronym, 0);
		}
		catch (NotFoundException ex)
		{
			// this is expected
		}

		ExchangeImpl newExchange;
		boolean committed = false;
		try
		{
			Transaction.startTransaction();
			newExchange = newExchangeImpl();
			newExchange.insert();
			newExchange.setName(exchangeStruct.name);
			newExchange.setAcronym(exchangeStruct.acronym);
                        newExchange.setMembershipKey(exchangeStruct.membershipKey);
			committed = Transaction.commit();
		}
		catch (PersistenceException ex)
		{
			throw ExceptionBuilder.transactionFailedException("Error creating/initializing exchange: " + ex, 0);
		}
		finally
		{
			if (!committed)
			{
				Transaction.rollback();
			}
		}
		return newExchange;
	}

	/**
	 *  Remove the given exchange from persistent storage.
	 *
	 *  @param exchange - the exchange object to remove.
	 *  @throws SystemException - thrown if there's a low-level persistence problem.
	 */
	public void remove(Exchange exchange)
		throws SystemException, TransactionFailedException
	{
		if (exchange instanceof ExchangeImpl){
                        boolean success = false;
			try{
				Transaction.startTransaction();
				((ExchangeImpl)exchange).markForDelete();
				success = Transaction.commit();
			}
			catch (PersistenceException ex){
				throw ExceptionBuilder.transactionFailedException("Failed to remove exchange: " + ex, 0);
			}
                        finally{
                                if(!success){
                                        Transaction.rollback();
                                }
                        }
		}
	}

	/**
	 *
	 *  @param exchangeKey - the key value of the exchange to remove
	 *  @throws SystemException - thrown if there's a low-level persistence problem.
	 *  @throws DataValidationException - thrown if the exchange cannot be found.
	 */
	public void removeByKey(int exchangeKey)
		throws NotFoundException, SystemException, TransactionFailedException
	{
		ExchangeImpl exchange = (ExchangeImpl)findExchangeForKey(exchangeKey);
		remove(exchange);
	}

	/**
	 *  Find expecting a unique result (exactly 1).
	 *
	 *  @param example - the example object to search for.
	 *  @throws SystemException - thrown if there's a low-level persistence problem.
	 *  @throws DataValidationException - thrown if the exchange cannot be found.
	 *  @return Exchange - the exchange found
	 */
	protected Exchange findUnique(ExchangeImpl example)
		throws SystemException, NotFoundException
	{
		Vector exchanges = findByExample(example);
		if (exchanges.isEmpty())
		{
			throw ExceptionBuilder.notFoundException("Could not find exchange!", 0);
		}
		if (exchanges.size() > 1)
		{
			Log.alarm(this, "Query for unique exchange returned more than one result! (using the first result value)");
		}
		return (Exchange)exchanges.elementAt(0);
	}

	/**
	 *  Find matches to the given example in the persistent storage.
	 *
	 *  @throws SystemException = thrown if there's a low-level persistence problem.
	 *  @return Vector - a vector of ExchangeImpl objects, possibly of length 0.
	 */
	protected Vector findByExample(ExchangeImpl example)
		throws SystemException
	{
		ObjectQuery objectQuery = example.newObjectQuery(this);
		try
		{
			return objectQuery.find();
		}
		catch (PersistenceException ex)
		{
			Log.exception(this, "Persistence error in Exchange query: ", ex.getOriginalException());
			throw ExceptionBuilder.systemException("Error finding exchange(s): " + ex, 0);
		}
	}
}

