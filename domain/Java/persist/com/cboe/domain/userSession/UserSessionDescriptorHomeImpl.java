package com.cboe.domain.userSession;

import com.cboe.exceptions.*;
import com.cboe.interfaces.domain.userSession.*;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import com.cboe.infrastructureServices.persistenceService.ObjectQuery;
import com.cboe.util.ExceptionBuilder;
import java.util.*;

/**
 *  A persistent implementation of the UserSessionDescriptorHome interface
 *
 *  @author Steven Sinclair
 */
public class UserSessionDescriptorHomeImpl extends BOHome implements UserSessionDescriptorHome
{
	/** 
	 */
	public Collection findAllDescriptors()
		throws SystemException
	{
		return findByExample(newImpl());
	}

	protected UserSessionDescriptorImpl newImpl()
	{
		UserSessionDescriptorImpl ex = new UserSessionDescriptorImpl();
		addToContainer(ex);
		return ex;
	}

	/**
	 *  @param userSessionKey - the key for an userSession.
	 *  @throws DataValidationException - thrown if the userSession cannot be found.
	 *  @throws SystemException - thrown if there's a low-level persistence problem.
	 * 	@return UserSessionDescriptor - the userSession found.
	 */
	public UserSessionDescriptor findUserSessionDescriptor(String userId)
		throws NotFoundException, SystemException
	{
		UserSessionDescriptorImpl queryExample = newImpl();
		queryExample.setUserId(userId);
		return findUnique(queryExample);
	}

	/**
	 *  Return the userSession whose key is <code>userSessionKey</code>.
	 *
	 *  @param userSessionKey - the key for an userSession.
	 *  @throws DataValidationException - thrown if the userSession cannot be found.
	 *  @throws SystemException - thrown if there's a low-level persistence problem.
	 * 	@return UserSessionDescriptor - the userSession found.
	 */
	public UserSessionDescriptor findUserSessionDescriptorForKey(int userSessionKey)
		throws NotFoundException, SystemException
	{
		UserSessionDescriptorImpl queryExample = newImpl();
		queryExample.setObjectIdentifierFromInt(userSessionKey);
		return findUnique(queryExample);
	}
	

	/**
	 *  Create a new persistent instance of the UserSessionDescriptor interface.
	 *
	 *  @throws SystemException - thrown if there's a low-level persistence problem.
	 *  @throws AlreadyExistsException - thrown if the acronym already exists.
	 * 	@return UserSessionDescriptor - the userSession found.
	 */
	public UserSessionDescriptor create(String userId)
		throws AlreadyExistsException, TransactionFailedException, SystemException
	{
		try
		{
			findUserSessionDescriptor(userId);
			throw ExceptionBuilder.alreadyExistsException("Cannot create: userSession already exists for userId " + userId, 0);
		}
		catch (NotFoundException ex)
		{
			// this is expected
		}

		UserSessionDescriptorImpl newUserSessionDescriptor;
		boolean committed = false;
		try
		{
			Transaction.startTransaction();
			newUserSessionDescriptor = newImpl();
			newUserSessionDescriptor.insert();
			newUserSessionDescriptor.setUserId(userId);
			newUserSessionDescriptor.setSessionOpen(true);
			committed = Transaction.commit();
		}
		catch (PersistenceException ex)
		{
			throw ExceptionBuilder.transactionFailedException("Error creating/initializing userSession: " + ex, 0);
		}
		finally
		{
			if (!committed)
			{
				Transaction.rollback();
			}
		}
		return newUserSessionDescriptor;
	}

	/**
	 *  Remove the given userSession from persistent storage.
	 *
	 *  @param userSession - the userSession object to remove.
	 *  @throws SystemException - thrown if there's a low-level persistence problem.
	 */
	public void remove(UserSessionDescriptor userSession)
		throws SystemException, TransactionFailedException
	{
		if (userSession instanceof UserSessionDescriptorImpl)
		{
			UserLoginDescriptorHome loginHome;
			try
			{
				loginHome = (UserLoginDescriptorHome)HomeFactory.getInstance().findHome(UserLoginDescriptorHome.HOME_NAME);
			}
			catch (CBOELoggableException ex)
			{
				Log.exception(this, "Failed to finduser login home", ex);
				throw ExceptionBuilder.systemException("Failed to find user login home"+ ex, 0);
			}
   
            boolean success = false;
			try
			{
				Transaction.startTransaction();
				Iterator logins = userSession.getUserLogins().iterator();
				while (logins.hasNext())
				{
					loginHome.remove((UserLoginDescriptor)logins.next());
				}
				((UserSessionDescriptorImpl)userSession).markForDelete();
				success = Transaction.commit();
			}
			catch (PersistenceException ex)
			{
				throw ExceptionBuilder.transactionFailedException("Failed to remove userSession: " + ex, 0);
			}
            finally
            {
                if(!success)
                {
                    Transaction.rollback();
                }
            }
		}
	}

	/**
	 *  Find expecting a unique result (exactly 1).
	 *
	 *  @param example - the example object to search for.
	 *  @throws SystemException - thrown if there's a low-level persistence problem.
	 *  @throws DataValidationException - thrown if the userSession cannot be found.
	 *  @return UserSessionDescriptor - the userSession found
	 */
	protected UserSessionDescriptor findUnique(UserSessionDescriptorImpl example)
		throws SystemException, NotFoundException
	{
		Collection userSessions = findByExample(example);
		if (userSessions.isEmpty())
		{
			throw ExceptionBuilder.notFoundException("Could not find userSession!", 0);
		}
		if (userSessions.size() > 1)
		{
			Log.alarm(this, "Query for unique userSession returned more than one result! (using the first result value)");
		}
		return (UserSessionDescriptor)userSessions.iterator().next();
	}

	/**
	 *  Find matches to the given example in the persistent storage.
	 *
	 *  @throws SystemException = thrown if there's a low-level persistence problem.
	 *  @return Vector - a vector of UserSessionDescriptorImpl objects, possibly of length 0.
	 */
	protected Collection findByExample(UserSessionDescriptorImpl example)
		throws SystemException
	{
		ObjectQuery objectQuery = example.newObjectQuery(this);
		try
		{
			return objectQuery.find();
		}
		catch (PersistenceException ex)
		{
			Log.exception(this, "Persistence error in UserSessionDescriptor query: ", ex.getOriginalException());
			throw ExceptionBuilder.systemException("Error finding userSession(s): " + ex, 0);
		}
	}
}

