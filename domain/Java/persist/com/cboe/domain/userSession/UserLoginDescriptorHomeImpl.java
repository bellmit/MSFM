package com.cboe.domain.userSession;

import com.cboe.exceptions.*;
import com.cboe.util.ExceptionBuilder;
import com.cboe.interfaces.domain.userSession.*;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import com.cboe.infrastructureServices.persistenceService.ObjectQuery;
import java.util.*;

import junit.framework.*;

/**
 *  A persistent implementation of the UserLoginDescriptorHome interface
 *
 *  @author Steven Sinclair
 */
public class UserLoginDescriptorHomeImpl extends BOHome implements UserLoginDescriptorHome
{
	/** 
	 */
	public Collection findAllDescriptors()
		throws SystemException
	{
		return findByExample(newImpl());
	}

	protected UserLoginDescriptorImpl newImpl()
	{
		UserLoginDescriptorImpl ex = new UserLoginDescriptorImpl();
		addToContainer(ex);
		return ex;
	}

	/**
	 *  Return the userLogin whose key is <code>userLoginKey</code>.
	 *
	 *  @param userLoginKey - the key for an userLogin.
	 *  @throws DataValidationException - thrown if the userLogin cannot be found.
	 *  @throws SystemException - thrown if there's a low-level persistence problem.
	 * 	@return UserLoginDescriptor - the userLogin found.
	 */
	public UserLoginDescriptor findUserLoginDescriptor(UserSessionDescriptor userSession, String sourceComponent)
		throws NotFoundException, SystemException
	{
		UserLoginDescriptorImpl queryExample = newImpl();
		queryExample.setUserSession(userSession);
		queryExample.setSourceComponent(sourceComponent);
		return findUnique(queryExample);
	}

	/**
	 *  Return the userLogin whose key is <code>userLoginKey</code>.
	 *
	 *  @param userLoginKey - the key for an userLogin.
	 *  @throws DataValidationException - thrown if the userLogin cannot be found.
	 *  @throws SystemException - thrown if there's a low-level persistence problem.
	 * 	@return UserLoginDescriptor - the userLogin found.
	 */
	public UserLoginDescriptor findUserLoginDescriptorForKey(int userLoginKey)
		throws NotFoundException, SystemException
	{
		UserLoginDescriptorImpl queryExample = newImpl();
		queryExample.setObjectIdentifierFromInt(userLoginKey);
		return findUnique(queryExample);
	}
	

	/**
	 *  Create a new persistent instance of the UserLoginDescriptor interface.
	 *  Reference count is set to one since there is automatically one login if the
	 *  descriptor gets created.
	 *
	 *  @throws SystemException - thrown if there's a low-level persistence problem.
	 *  @throws AlreadyExistsException - thrown if the acronym already exists.
	 * 	@return UserLoginDescriptor - the userLogin found.
	 */
	public UserLoginDescriptor create(UserSessionDescriptor userSession, String sourceComponent)
		throws AlreadyExistsException, TransactionFailedException, SystemException
	{
		try
		{
			findUserLoginDescriptor(userSession, sourceComponent);
			throw ExceptionBuilder.alreadyExistsException("Cannot create: userLogin already exists for (session/source) (" + userSession.getSessionKey() + "/" + sourceComponent, 0);
		}
		catch (NotFoundException ex)
		{
			// this is expected
		}

		UserLoginDescriptorImpl newUserLoginDescriptor;
		boolean committed = false;
		try
		{
			Transaction.startTransaction();
			newUserLoginDescriptor = newImpl();
			newUserLoginDescriptor.insert();
			newUserLoginDescriptor.setUserSession(userSession);
			newUserLoginDescriptor.setSourceComponent(sourceComponent);
			newUserLoginDescriptor.setReferenceCount(1);
			committed = Transaction.commit();
		}
		catch (PersistenceException ex)
		{
			throw ExceptionBuilder.transactionFailedException("Error creating/initializing userLogin: " + ex, 0);
		}
		finally
		{
			if (!committed)
			{
				Transaction.rollback();
			}
		}
		return newUserLoginDescriptor;
	}

	/**
	 *  Remove the given userLogin from persistent storage.
	 *
	 *  @param userLogin - the userLogin object to remove.
	 *  @throws SystemException - thrown if there's a low-level persistence problem.
	 */
	public void remove(UserLoginDescriptor userLogin)
		throws SystemException, TransactionFailedException
	{
		if (userLogin instanceof UserLoginDescriptorImpl)
		{
            boolean success = false;
			try
			{
				Transaction.startTransaction();
				((UserLoginDescriptorImpl)userLogin).markForDelete();
				success = Transaction.commit();
			}
			catch (PersistenceException ex)
			{
				throw ExceptionBuilder.transactionFailedException("Failed to remove userLogin: " + ex, 0);
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
	 *  @throws DataValidationException - thrown if the userLogin cannot be found.
	 *  @return UserLoginDescriptor - the userLogin found
	 */
	protected UserLoginDescriptor findUnique(UserLoginDescriptorImpl example)
		throws SystemException, NotFoundException
	{
		Collection userLogins = findByExample(example);
		if (userLogins.isEmpty())
		{
			throw ExceptionBuilder.notFoundException("Could not find userLogin!", 0);
		}
		if (userLogins.size() > 1)
		{
			Log.alarm(this, "Query for unique userLogin returned more than one result! (using the first result value)");
		}
		return (UserLoginDescriptor)userLogins.iterator().next();
	}

	/**
	 *  Find matches to the given example in the persistent storage.
	 *
	 *  @throws SystemException = thrown if there's a low-level persistence problem.
	 *  @return Vector - a vector of UserLoginDescriptorImpl objects, possibly of length 0.
	 */
	protected Collection findByExample(UserLoginDescriptorImpl example)
		throws SystemException
	{
		ObjectQuery objectQuery = example.newObjectQuery(this);
		try
		{
			return objectQuery.find();
		}
		catch (PersistenceException ex)
		{
			Log.exception(this, "Persistence error in UserLoginDescriptor query: ", ex.getOriginalException());
			throw ExceptionBuilder.systemException("Error finding userLogin(s): " + ex, 0);
		}
	}
}

