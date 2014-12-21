package com.cboe.domain.user;

import com.cboe.exceptions.*;
import com.cboe.idl.user.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.persistenceService.*;
import com.cboe.interfaces.domain.user.*;
import com.cboe.util.ExceptionBuilder;
import java.util.*;

/**
 * Class for converting UserEnablementElementImpl's to/from the corresponding struct.
 *
 *  @author Steven Sinclair
 */
public class UserEnablementHomeImpl extends BOHome implements UserEnablementHome
{
	protected HashMap userEnablementCache = new HashMap();
	protected boolean userEnablementCacheIsAllUsers = false;
	protected UserEnablementStruct[] allUsersCache = null;

	protected synchronized UserEnablementStruct getCachedStruct(String userId)
	{
		return (UserEnablementStruct)userEnablementCache.get(userId);
	}

	protected synchronized void setCachedStruct(UserEnablementStruct struct)
	{
		userEnablementCache.put(struct.userId, struct);
		allUsersCache = null;
	}

	public synchronized UserEnablementStruct[] getAllUserEnablements() throws SystemException
	{
		if (allUsersCache != null)
		{
			Log.debug(this, "Return the all user enablement cached object");
			return allUsersCache;
		}
		if (userEnablementCacheIsAllUsers)
		{
			Log.debug(this, "Building all user enablement cached object from user enablement cache map");
			allUsersCache = new UserEnablementStruct[ userEnablementCache.size() ];
			userEnablementCache.values().toArray(allUsersCache);
			return allUsersCache;
		}

		try
		{
			UserEnablementElementImpl[] allElements = findAllElements();
			ArrayList structList = new ArrayList();
			int elemIdx=0;
			while (elemIdx < allElements.length)
			{
				UserEnablementStruct aStruct = new UserEnablementStruct();
				elemIdx += fillInStruct(allElements, aStruct, elemIdx);
				structList.add(aStruct);
			}
			
			allUsersCache = new UserEnablementStruct[structList.size()];
			structList.toArray(allUsersCache);
			userEnablementCacheIsAllUsers = true;
			return allUsersCache;
		}
		catch (PersistenceException ex)
		{
			Log.exception(this, "Error finding all user enablement elements.", ex);
			throw ExceptionBuilder.systemException("Error finding all user enablement elements: " + ex, 0);
		}
	}

	public UserEnablementStruct getUserEnablement(String userId) throws SystemException
	{
		UserEnablementStruct struct = getCachedStruct(userId);
		if (struct != null)
		{
			return struct;
		}

		try
		{
            // NOTE: Now that all enablements are provided via the PropertyService, there is no need
            //       for this query.
            //UserEnablementElementImpl[] elements = findElementsForUser(userId);
            UserEnablementElementImpl[] elements = new UserEnablementElementImpl[0];

			struct = new UserEnablementStruct();
			struct.userId = userId;
			fillInStruct(elements, struct, 0);
			return struct;
		}
		catch (PersistenceException ex)
		{
			throw ExceptionBuilder.systemException("Failed to find enablement elements for user " + userId, 0);
		}
	}

	/**
	 *  Fill in the struct based on the elements array, starting at startIdx.
	 *  @return int - the number of elements used to build the struct.
	 */
	protected int fillInStruct(UserEnablementElementImpl[] elements, UserEnablementStruct struct, int startIdx)
		throws PersistenceException
	{
		if (startIdx >= elements.length)
		{
			struct.sessionEnablements = new UserSessionEnablementStruct[0];
			return 0;
		}

		struct.userId = elements[startIdx].getUserId();

		struct.sessionEnablements = new UserSessionEnablementStruct[countSessions(elements, startIdx)];

		int elemIdx=startIdx;
		for (int i=0; i < struct.sessionEnablements.length; i++)
		{
			struct.sessionEnablements[i] = new UserSessionEnablementStruct();
			elemIdx += buildSessionEnablement(struct.sessionEnablements[i], elements, elemIdx);
		}

		setCachedStruct(struct);
		return elemIdx - startIdx;
	}

	/**
	 * Build the product type array for session enablement.
	 *
	 * @return int - the number of elements used to describe the session enablement
	 */
	protected int buildSessionEnablement(UserSessionEnablementStruct struct, UserEnablementElementImpl[] sortedElements, int idx)
	{
		struct.sessionName = sortedElements[idx].getSessionName();

		int typeCount = countProductTypes(sortedElements, idx);
		struct.productTypeEnablements = new short[typeCount];

		for (int i=0; i < typeCount; i++)
		{
			struct.productTypeEnablements[i] = sortedElements[idx+i].getProductType();
		}

		return typeCount;
	}

	/**
	 *  Count the number of sessions for the given user at the given index.
	 */
	protected int countSessions(UserEnablementElementImpl[] sortedElements, int idx)
	{
		int count=0;
		String lastName = null;
		String lastUserId = sortedElements[idx].getUserId();
		for (int i=idx; i < sortedElements.length; i++)
		{
			String name = sortedElements[i].getSessionName();
			String userId = sortedElements[i].getUserId();
			if (!userId.equals(lastUserId))
			{
				break;
			}
			if (lastName==null || !name.equals(lastName))
			{
				lastName = name;
				++count;
			}
		}
		return count;
	}

	/**
	 *  Count product types, starting at idx, until the session name or user id changes.
	 */
	protected int countProductTypes(UserEnablementElementImpl[] sortedElements, int idx)
	{
		int count=0;
		String sessionName = sortedElements[idx].getSessionName();
		String userId = sortedElements[idx].getUserId();
		int lastType = Integer.MIN_VALUE; // productType is short, so can never be this value
		for (int i=idx; i < sortedElements.length; i++)
		{
			if (!sortedElements[i].getSessionName().equals(sessionName) ||
			    !sortedElements[i].getUserId().equals(userId))
			{
				break;
			}
			int type = sortedElements[i].getProductType();
			if (type != lastType)
			{
				lastType = type;
				++count;
			}
		}
		return count;
	}

	public void setUserEnablement(UserEnablementStruct enablementStruct) throws TransactionFailedException
	{
		Transaction.startTransaction();
		boolean committed = false;
		try
		{
			setUserEnablementInternal(enablementStruct);
			committed = Transaction.commit();
		}
		catch (PersistenceException ex)
		{
			String msg = "Failed to set user enablement for user " + enablementStruct.userId;
			Log.exception(this, msg, ex);
			throw ExceptionBuilder.transactionFailedException(msg + ": " + ex, 0);
		}
		finally
		{
			if (!committed)
			{
				Transaction.rollback();
			}
			else
			{
				setCachedStruct(enablementStruct);
			}
		}
	}

	/**
	 *  Refactored to deal with the transactional stuff in another method (setUserEnablement)
	 */
	protected void setUserEnablementInternal(UserEnablementStruct enablementStruct) throws PersistenceException
	{
		UserEnablementElementImpl[] elements = findElementsForUser(enablementStruct.userId);
		boolean[] reuseElements = new boolean[elements.length];

		for (int i=0; i < enablementStruct.sessionEnablements.length; i++)
		{
			UserSessionEnablementStruct sessionEnablement = enablementStruct.sessionEnablements[i];
			String session = sessionEnablement.sessionName;
			for (int j=0; j < sessionEnablement.productTypeEnablements.length; j++)
			{
				short productType = sessionEnablement.productTypeEnablements[j];
				int elemIdx = findElement(elements, session, productType);
				if (elemIdx < 0)
				{
					createElement(enablementStruct.userId, session, productType);
				}
				else
				{
					reuseElements[elemIdx] = true;
				}
			}
		}

		for (int i=0; i < elements.length; i++)
		{
			if (!reuseElements[i])
			{
				elements[i].markForDelete();
			}
		}
	}

	/**
	 *  Find the first element matching session and prodType.  Userid is not considered.
	 *
	 *	@return element index, or -1 if not found.
	 */
	protected int findElement(UserEnablementElementImpl[] elements, String session, short prodType)
	{
		for (int i=0; i < elements.length; i++)
		{
			if (elements[i].getProductType() == prodType && elements[i].getSessionName().equals(session))
			{
				return i;
			}
		}
		return -1;
	}

	protected UserEnablementElementImpl createElement(String userId, String sessionName, short productType)
	{
		UserEnablementElementImpl newElement = new UserEnablementElementImpl();
		addToContainer(newElement);

		newElement.setUserId(userId);
		newElement.setSessionName(sessionName);
		newElement.setProductType(productType);

		return newElement;
	}

	/**
	 *   Query for all enablement elements for a given user id.
	 *
	 *   @return <b>sorted</b> array of enablement elements.
	 *   @see UserEnablementElementImplComparator
	 */
	protected UserEnablementElementImpl[] findElementsForUser(String userId)	throws PersistenceException
	{
		UserEnablementElementImpl queryExample = new UserEnablementElementImpl();
		addToContainer(queryExample);
		ObjectQuery query = new ObjectQuery(queryExample);
		queryExample.setUserId(userId);
		Vector result = query.find();

		UserEnablementElementImpl[] resultArray = new UserEnablementElementImpl[result.size()];
		result.toArray(resultArray);
		sortEnablements(resultArray);
		return resultArray;
	}

	protected UserEnablementElementImpl[] findAllElements() throws PersistenceException
	{
		return findElementsForUser(null);
	}

	protected void sortEnablements(UserEnablementElementImpl[] elements)
	{
		Arrays.sort(elements, userEnablementElementImplComparator);
	}

	protected static final Comparator userEnablementElementImplComparator = new UserEnablementElementImplComparator();

	/**
	 * Compare elements, sorting by user, then session, then prodtype
	 */
	protected static class UserEnablementElementImplComparator implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			UserEnablementElementImpl e1 = (UserEnablementElementImpl)o1;
			UserEnablementElementImpl e2 = (UserEnablementElementImpl)o2;
			int comp = e1.getUserId().compareTo(e2.getUserId());
			if (comp == 0)
			{
				comp = e1.getSessionName().compareTo(e2.getSessionName());
				if (comp == 0)
				{
					comp = e1.getProductType() - e2.getProductType();
				}
			}
			return comp;
		}
	}
}
