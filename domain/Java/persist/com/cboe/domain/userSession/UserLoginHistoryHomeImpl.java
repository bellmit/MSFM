package com.cboe.domain.userSession;

import com.cboe.exceptions.*;
import com.cboe.interfaces.domain.userSession.*;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import com.cboe.infrastructureServices.persistenceService.ObjectQuery;
import com.objectwave.persist.constraints.*;
import com.cboe.util.ExceptionBuilder;
import java.util.*;

/**
 *  @author Steven Sinclair
 */
public class UserLoginHistoryHomeImpl extends BOHome implements UserLoginHistoryHome
{
	public Collection findAll()
		throws SystemException
	{
		UserLoginHistoryImpl example = new UserLoginHistoryImpl();
		addToContainer(example);
		try
		{
			ObjectQuery query = new ObjectQuery(example);
			return query.find();
		}
		catch (PersistenceException ex)
		{
			Log.exception(this, "Error finding history items.", ex);
			throw ExceptionBuilder.systemException("Error finding history items: " + ex, 0);
		}
	}

	public UserLoginHistory[] findUserLoginHistory(Date fromTime, Date toTime)
		throws SystemException
	{
		return find(new UserLoginHistoryImpl(), fromTime, toTime);
	}

	public UserLoginHistory[] findUserLoginHistory(int sessionKey, Date fromTime, Date toTime)
		throws SystemException
	{
		UserLoginHistoryImpl example = new UserLoginHistoryImpl();
		example.setSessionKey(sessionKey);
		return find(example, fromTime, toTime);
	}

	public UserLoginHistory[] findUserLoginHistory(String userId, Date fromTime, Date toTime)
		throws SystemException
	{
		UserLoginHistoryImpl example = new UserLoginHistoryImpl();
		example.setUserId(userId);
		return find(example, fromTime, toTime);
	}

	protected UserLoginHistory[] find(UserLoginHistoryImpl example, Date fromTime, Date toTime)
		throws SystemException
	{
		addToContainer(example);
		try
		{
			ObjectQuery query = new ObjectQuery(example);

			if (fromTime != null && toTime != null)
			{
				ConstraintBetween c = new ConstraintBetween();
				c.setPersistence(example);
				c.setField("time");
				c.setNot(false);
				c.setBetweenMin(Long.toString(fromTime.getTime()));
				c.setBetweenMax(Long.toString(toTime.getTime()));
				query.addConstraint(c);
			}

			Vector result = query.find();
			UserLoginHistory[] resultArray = new UserLoginHistory[result.size()];
			result.toArray(resultArray);
			return resultArray;
		}
		catch (PersistenceException ex)
		{
			Log.exception(this, "Error finding history items.", ex);
			throw ExceptionBuilder.systemException("Error finding history items: " + ex, 0);
		}
	}

	public UserLoginHistory create(int sessionKey, String userId, String sourceComponent, int action, Date time, String description)
		throws TransactionFailedException, SystemException
	{
		UserLoginHistoryImpl history = new UserLoginHistoryImpl();
		addToContainer(history);
		history.setSessionKey(sessionKey);
		history.setUserId(userId);
		history.setSourceComponent(sourceComponent);
		history.setDescription(description);
		history.setAction(action);
		history.setTime(time);
		return history;
	}
}

