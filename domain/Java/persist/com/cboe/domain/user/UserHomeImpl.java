package com.cboe.domain.user;

//----------------------------------------------------------------------
// Source file: Java/com/cboe/domain/user/UserHomeImpl.java
//
// PACKAGE: com.cboe.domain.user
//----------------------------------------------------------------------
// Copyright (c) 1999 The Chicago Board Options Exchange. All Rights Reserved.
//----------------------------------------------------------------------

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.cboe.domain.product.ProductClassImpl;
import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.SessionProfileUserStruct;
import com.cboe.idl.constants.UserTypes;
import com.cboe.idl.user.SessionProfileUserDefinitionStruct;
import com.cboe.idl.user.UserSummaryStruct;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.utilities.Transaction;
import com.cboe.infrastructureServices.persistenceService.ObjectQuery;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import com.cboe.interfaces.domain.FirmHome;
import com.cboe.interfaces.domain.user.AcronymUser;
import com.cboe.interfaces.domain.user.User;
import com.cboe.interfaces.domain.user.UserHome;
import com.cboe.util.ExceptionBuilder;

/**
 * A implementation of UserHome that uses JavaGrinder for
 * persistence.
 *
 * @author John Wickberg
 * @author Brad Samuels
 */
public class UserHomeImpl extends BOHome implements UserHome, UserCache.UserCacheDataFinder 
{
    UserCache cache = new UserCache(this, this);
    
    boolean foundAll = false;
    
	/**
     * @param userIdKey
     * @return
     */
    public List findUserIdImpls(int userIdKey) throws PersistenceException
    {
        UserIdImpl example = new UserIdImpl();
        this.addToContainer(example);
        ObjectQuery query = new ObjectQuery(example);
        example.setAcronymUserKey(userIdKey);
        return query.find();
    }

    /**
	 * Creates a home instance.
	 *
	 */
	public UserHomeImpl() {
		super();
	}

	/**
	 * Adds each object in the vector the container
	 *
	 * @param objects - collection of objects
	 */
	private void addObjectsToContainer(Collection objects) {

		Iterator iter = objects.iterator();
		while( iter.hasNext() ){
			addToContainer( (BObject)iter.next() );
		}
	}
	/**
	 * Adds each object in the vector the container
	 *
	 * @param objects - array of objects
	 */
	private void addObjectsToContainer(Object[] objects) 
    {
	    for (int i = 0; i < objects.length; i++)
        {
            addToContainer( (BObject)objects[i] );
		}
	}

	/**
	 * @see UserHome#create
	 *
	 */
    public User create(SessionProfileUserDefinitionStruct newUser, boolean membershipDefined) throws DataValidationException, SystemException
    {
        if (!Transaction.inTransaction())
        {
            throw new RuntimeException("Application exception: UserHomeImpl.create() should only be called from within a transaction!");
        }

        List usersForAcr = cache.getCachedUsersForAcronym(newUser.userAcronym.acronym, newUser.userAcronym.exchange);
		AcronymUserImpl theUser;
        
        // To support possible fallback, we will set the first id for a user acronym
        // to have the same idKey as the acrKey.  This is so that the FK relations
        // in the user_pref will be compatible for both sbt_user and login_userid
        // during rollout of the SingleAcronym project.
        //
        boolean useAcrKeyForIdKey = false;
        
        if (usersForAcr==null || usersForAcr.isEmpty())
        {
            Log.information(this, "Adding new user acronym (mem.def'd="+ membershipDefined+"): " + toString(newUser.userAcronym));
    		theUser = new AcronymUserImpl();
    		addToContainer(theUser);
    		theUser.fromStruct(newUser, membershipDefined);
            theUser.initializeObjectIdentifier();
            
            // This is the first userid for the acronym: use the AcronymUser dbid.
            //
            useAcrKeyForIdKey = true;
        }
        else
        {
            Log.information(this, "Adding user '" + newUser.userId + "' (mem.def'd="+ membershipDefined+") to existing acronym: " + toString(newUser.userAcronym));
            Log.information(this, "The acronym currently has " + usersForAcr.size() + " users.");
            theUser = ((UserCombinedImpl)usersForAcr.get(0)).getAcronymUserImpl();
            
            // update base values:
            theUser.updateBase(newUser, membershipDefined);
        }

        UserCombinedImpl newUserImpl = create(theUser, newUser.userId, newUser.isActive);
        if (useAcrKeyForIdKey)
        {
            newUserImpl.getUserIdImpl().setPrimaryKeyField(new Integer(theUser.getAcronymUserKey()));
        }
        else
        {
            newUserImpl.getUserIdImpl().initializeObjectIdentifier();
        }
        return newUserImpl;
    }
    
    /**
     * Create the UserIdImpl and CombinedUserImpl, tie them together with the acrUser.
     * @param acrUser
     * @param userId
     * @param isActive
     * @return UserCombinedImpl
     */
    private UserCombinedImpl create(AcronymUserImpl acrUser, String userId, boolean isActive)
    {
        UserIdImpl theUserId= new UserIdImpl();
        addToContainer(theUserId);
        theUserId.setAcronymUserKey(acrUser.getAcronymUserKey());
        theUserId.setUserId(userId);
        theUserId.setIsActive(isActive);
		UserCombinedImpl userImpl = new UserCombinedImpl(acrUser, theUserId);
        cache.updateOnCommit(userImpl);
        return userImpl;
	}
    
	/**
	 * Performs queries that return multiple users.
	 *
	 * @param query query for a set of users
	 * @return users found by query
	 */
	private List doAcronymUserQuery(ObjectQuery queryByBaseExample) {
		try
		{
			Vector queryResult = queryByBaseExample.find();
			addObjectsToContainer( queryResult );
            return queryResult;
		}
		catch (PersistenceException e)
		{
			Log.exception(this, "Query for users failed", e);
			return new ArrayList(0);
		}
	}
	/**
	 * Performs unique queries for a user.
	 *
	 * @param query query that should retrieve a unique user
	 * @return user found by query
	 * @exception NotFoundException if the query fails
	 */
	private AcronymUserImpl doAcronymUserUniqueQuery(ObjectQuery queryByBaseExample) throws NotFoundException 
    {
		try
		{
            AcronymUserImpl theUser = (AcronymUserImpl)queryByBaseExample.findUnique();
			if (theUser == null)
			{
				throw ExceptionBuilder.notFoundException("Could not find user with name = " + name, 0);
			}
			addToContainer( theUser );
            return theUser;
		}
		catch (PersistenceException e)
		{
			throw ExceptionBuilder.notFoundException("Query failed for user: " + e, 0);
		}
	}
	/**
	 * Performs queries for UserImpl that return multiple UserCombinedImpl.
	 *
	 * @param query query for a set of users
	 * @return users found by query
	 */
	private List doUserQuery(ObjectQuery queryByBaseExample) {
		try
		{
			Vector queryResult = queryByBaseExample.find();
            addObjectsToContainer( queryResult );
            ArrayList result = new ArrayList(queryResult.size()*10);
            final int len = queryResult.size(); 
            for (int i=0; i < len; i++)
            {
                AcronymUserImpl aUser = (AcronymUserImpl)queryResult.get(i);
                List combinedUsers = cache.getCachedUsersForAcronym(aUser);
                if (combinedUsers == null)
                {
                    combinedUsers = cache.cacheUser(aUser);
                    addObjectsToContainer(combinedUsers);
                }
                result.addAll(combinedUsers);
            }
            return result;
		}
		catch (PersistenceException e)
		{
			Log.exception(this, "Query for users failed", e);
			return new ArrayList(0);
		}
	}
    
    /**
     * Return a match to the query.  Returns an active user if possible.
     * 
     * @param queryByBaseExample
     * @return
     * @throws NotFoundException
     */
	private User doFindFirstQuery(ObjectQuery queryByBaseExample) throws NotFoundException
	{
	    AcronymUserImpl userImpl = doAcronymUserUniqueQuery(queryByBaseExample);
        
        try
        {
            // the userImpl should have populated the cache.
    	    List users = cache.getCachedUsersForAcronym(userImpl);
    	    final int len = users==null ? 0 : users.size();
    	    if (len == 0)
    	    {
    	        throw ExceptionBuilder.notFoundException("No user found for query!", 0);
    	    }
            for (int i = 0; i < len; i++)
            {
                User user = (User)users.get(i);
                if (user.isActive())
                {
                    return user;
                }
            }
    	    return (User)users.get(0);
        }
        catch (PersistenceException ex)
        {
            Log.exception(this, "Failed query for users of acronym", ex);
            throw ExceptionBuilder.notFoundException("No user found for query!", 0);
        }
	}
    
	/**
	 * Performs unique queries for a user.  Returns a collection of the users
     * for the unique acr+exchange.
	 *
	 * @param query query that should retrieve a unique user
     * @param subject subject of the query
	 * @return user found by query
	 * @exception NotFoundException if the query fails
	 */
	private User doUserUniqueQuery(UserIdImpl subject, String errMsg) throws NotFoundException 
    {
        addToContainer(subject);
        ObjectQuery query = new ObjectQuery(subject);
		try
		{
    		UserIdImpl theUserId = (UserIdImpl) query.findUnique();
			if (theUserId == null)
			{
				throw ExceptionBuilder.notFoundException(errMsg, 0);
			}
			addToContainer( theUserId );
            User result = cache.getCachedUser(theUserId.getUserIdKey());
            if (result == null)
            {
                AcronymUserImpl exampleUser = new AcronymUserImpl();
                addToContainer(exampleUser);
                ObjectQuery query2 = new ObjectQuery(exampleUser);
                exampleUser.setObjectIdentifierFromInt(theUserId.getAcronymUserKey());
                AcronymUserImpl userImpl = doAcronymUserUniqueQuery(query2);
                result = new UserCombinedImpl(userImpl, theUserId);
            }
            return result;
		}
		catch (PersistenceException e)
		{
			throw ExceptionBuilder.notFoundException("Query failed for user: " + e, 0);
		}
	}
    
	/**
	 * Finds all users.
	 *
	 * @see UserHome#findAll
	 */
	public List findAll() 
    {
        Log.information(this, "UserHomeImpl.findAll: called by " + new Exception().getStackTrace()[1]);
        if (foundAll)
        {
            Log.information(this, "findAll: already cached, just return the list.");
            return cache.getAllCachedUsers();
        }
        
// TODO - verify this alternate implementation.  Is it faster?  Is it right?
        HashMap idMapping; // acrKey : List<UserIdImpl>
        List result;
        try
        {
            Log.information(this, "Finding all userids...");
            UserIdImpl exId = new UserIdImpl();
            addToContainer(exId);
            ObjectQuery idQuery = new ObjectQuery(exId);
            List allIds = idQuery.find();
            final int len = allIds.size();
            idMapping = new HashMap(len*2); // acrKey : List<UserIdImpl>
            for (int i = 0; i < len; i++)
            {
                UserIdImpl id = (UserIdImpl)allIds.get(i);
                final Integer acrKey = new Integer(id.getAcronymUserKey());
                List idList = (List)idMapping.get(acrKey);
                if (idList == null)
                {
                    idMapping.put(acrKey, idList = new ArrayList(20));
                }
                idList.add(id);
            }
            result = new ArrayList(allIds.size());
            Log.information(this, "Found " + allIds.size() + " userIds");
        }
        catch (PersistenceException ex)
        {
            Log.exception(this, "Failed to query for all userIds", ex);
            return new ArrayList(0);
        }
        
        try
        {
            Log.information(this, "Finding all acronym user...");
            AcronymUserImpl exAcr = new AcronymUserImpl();
            addToContainer(exAcr);
            ObjectQuery idQuery = new ObjectQuery(exAcr);
            List allUsers = idQuery.find();
            Log.information(this, "Found " + allUsers.size() + " acronym users.");
            Log.information(this, "Merging userids and acronym users to create cache (of UserCombinedImpl).");
            final int len = allUsers.size();
            for (int i = 0; i < len; i++)
            {
                AcronymUserImpl user = (AcronymUserImpl)allUsers.get(i);
                final Integer acrKey = new Integer(user.getAcronymUserKey());
                List userIds = (List)idMapping.get(acrKey);
                if (userIds == null)
                {
                    Log.information(this, "User acronym " + user + " does not have any userids.  Maybe bad data?");
                }
                else
                {
                    result.addAll(cache.cacheUser(user, userIds));
                }
            }
            Log.information(this, "Done finding all users.");
        }
        catch (PersistenceException ex)
        {
            Log.exception(this, "Failed to query for all userIds", ex);
            return new ArrayList(0);
        }
        foundAll = true;
        return result;
        
// TODO - DELETE if the above code is significantly faster.
//
//		AcronymUserImpl example = new AcronymUserImpl();
//		addToContainer(example);
//		ObjectQuery query = new ObjectQuery(example);
//		return doUserQuery(query);
	}
	/**
	 * Searches for users who have the given account userId listed in their accounts vector.
	 *
	 * @param account request account id
	 * @exception DataValidationException - thrown if the account is not found
	 * @exception SystemException - thrown if there was a persistence problem.
	 * @return User[] - the users who have the given account in the accounts vector.
	 */
	public List findByAccount(String accountId) throws DataValidationException, SystemException
	{
		User accountUser;
		try
		{
			accountUser = findByUserId(accountId);
		}
		catch (NotFoundException ex)
		{
			throw ExceptionBuilder.dataValidationException("User for account id \"" + accountId + "\" not found!", 0);
		}

		UserAccountRelation example = new UserAccountRelation();
		addToContainer(example);
		ObjectQuery query = new ObjectQuery(example);
		example.setAccount(((UserCombinedImpl)accountUser).getUserImpl());

		Vector queryResult;
		try
		{
			queryResult = query.find();
		}
		catch (PersistenceException ex)
		{
			throw ExceptionBuilder.systemException("Persistence error finding users: " + ex, 0);
		}

		List usersForAcct = new ArrayList(queryResult.size()*10);
		for (int i=0; i < queryResult.size(); i++)
		{
			UserAccountRelation userAccountRelation = (UserAccountRelation)queryResult.elementAt(i);
            AcronymUserImpl theUser = userAccountRelation.getUser();
            try
            {
                List cachedUsers = cache.getCachedUsersForAcronym(theUser);
                if (cachedUsers != null)
                {
                    usersForAcct.addAll(cachedUsers);
                }
            }
            catch (PersistenceException ex)
            {
                Log.exception(this, "Failed query for users of acronym " + theUser.getAcronym() + ":" + theUser.getExchangeAcronym(), ex);
            }
		}
		return usersForAcct;
	}
	/**
	 * Finds by DPM assigned class: returns users who have a DPM account which is assigned the given class key.
	 *
	 * @see UserHome#findByAssignedClass
	 */
	public List findByDpmAssignedClass(int classKey, char role)
	{
		AssignedClass example = new AssignedClass();
		addToContainer(example);
		ObjectQuery query = new ObjectQuery(example);
		example.setAssignedClass(classKey);

		AcronymUserImpl userExample = new AcronymUserImpl();
		addToContainer(userExample);
		userExample.setUserType(UserTypes.DPM_ACCOUNT);
		example.setUser(userExample);

		// can't use doQuery(...), since result set contains assigned classes
		//
		// A hashtable is used here since there's no programatic guarantee that
		// there will not be any overlap (the HashMap will prevent duplicates)
		//
		HashMap result;
		try {
			result = new HashMap();
			Vector queryResult = query.find(); // find UserImpl's
			// For each DPM we retrieved, find all users who have the DPM account assigned to him.
			//
			for (int i=0; i < queryResult.size(); i++)
			{
				AssignedClass aResult = (AssignedClass)queryResult.elementAt(i);
				AcronymUserImpl dpm = aResult.getUser();

				// Subquery: find all UserAccountRelations having dpm as the "account".
				//
				UserAccountRelation uarExample = new UserAccountRelation();
				addToContainer(uarExample);
				ObjectQuery uarQuery = new ObjectQuery(uarExample);
				uarExample.setAccount(dpm);
				if (role != '\0')
				{
					AcronymUserImpl uarUserExample = new AcronymUserImpl();
					addToContainer(uarUserExample);
					uarUserExample.setRole(role);
					uarExample.setUser(uarUserExample);
				}
				Vector dpmParticipants = uarQuery.find();
				for (int j=0; j < dpmParticipants.size(); j++)
				{
					UserAccountRelation uar = (UserAccountRelation)dpmParticipants.elementAt(j);
					result.put(uar.getUser().getObjectIdentifier(), uar.getUser());
				}
			}
		}
		catch (PersistenceException e) {
			Log.exception(this, "Unable to query for assigned classes", e);
			return new ArrayList(0);
		}

		// Assemble & return the results.
		//
        final Iterator valuesIter = result.values().iterator();
        final ArrayList resultList = new ArrayList(result.size()*10);
        while (valuesIter.hasNext())
        {
            final AcronymUserImpl theUser = (AcronymUserImpl)valuesIter.next();
            try
            {
                final List someCombinedUsers = cache.getCachedUsersForAcronym(theUser);
                if (someCombinedUsers != null)
                {
                    resultList.addAll(someCombinedUsers);
                }
            }
            catch (PersistenceException ex)
            {
                Log.exception(this, "Failed query for users of acronym " + theUser.getAcronym() + ":" + theUser.getExchangeAcronym(), ex);
            }
        }
        return resultList;
	}
	/**
	 * Finds by assigned class.
	 *
	 * @see UserHome#findByAssignedClass
	 */
	public List findByAssignedClass(int classKey) {
		AssignedClass example = new AssignedClass();
		addToContainer(example);
		ObjectQuery query = new ObjectQuery(example);
		example.setAssignedClass(classKey);
		// can't use doQuery(...), since result set contains assigned classes
		List result;
		try {
			final List queryResult = query.find();
            result = new ArrayList(queryResult.size()*10);
			final Iterator iter = queryResult.iterator();
			for (int i = 0; iter.hasNext(); i++) {
				final AssignedClass assignment = (AssignedClass)iter.next();
				final AcronymUserImpl theUser = (AcronymUserImpl)assignment.getUser();
                final List users = cache.getCachedUsersForAcronym(theUser); 
                if (users != null)
                {
                    result.addAll(users);
                }
			}
		}
		catch (PersistenceException e) {
			Log.exception(this, "Unable to query for assigned classes", e);
			result = new ArrayList(0);
		}
		return result;
	}
	/**
	 * Finds user by key.
	 *
	 * @see UserHome#findByKey
	 */
	public User findByUserIdKey(int userIdKey) throws NotFoundException 
    {
        User result = cache.getCachedUser(userIdKey);
        if (result != null)
        {
            return result;
        }
        
		UserIdImpl exampleId = new UserIdImpl();
		addToContainer(exampleId);
		ObjectQuery queryById = new ObjectQuery(exampleId);
		exampleId.setObjectIdentifierFromInt(userIdKey);
        try
        {
            UserIdImpl foundId = (UserIdImpl)queryById.findUnique();
            AcronymUserImpl userExample = new AcronymUserImpl();
            addToContainer(userExample);
            ObjectQuery baseQuery = new ObjectQuery(userExample);
            userExample.setObjectIdentifierFromInt(foundId.getAcronymUserKey());
            AcronymUserImpl user = doAcronymUserUniqueQuery(baseQuery);
            return new UserCombinedImpl(user, foundId);
        }
        catch (PersistenceException e)
        {
            throw ExceptionBuilder.notFoundException("Query failed for user id key '"+userIdKey+"': " + e, 0);
        }
	}
	/**
	 * Finds user by user acronym and exchange.
	 *
	 * @see UserHome#findByExchangeAcronym
	 */    
	public List findByExchangeAcronym(ExchangeAcronymStruct exchangeAcronymStruct) 
    {
        List result = cache.getCachedUsersForAcronym(exchangeAcronymStruct.acronym, exchangeAcronymStruct.exchange);
        if (result == null)
        {
    		AcronymUserImpl example = new AcronymUserImpl();
    		addToContainer(example);
    		ObjectQuery query = new ObjectQuery(example);
    		example.setAcronym(exchangeAcronymStruct.acronym);
    		example.setExchangeAcronym(exchangeAcronymStruct.exchange);
    		result = doUserQuery(query);
        }
        return result;
	}

	/**
	 * Finds user by user acronym and exchange.
	 *
	 * @see UserHome#findByFirstExchangeAcronym
	 */    
	public User findByFirstExchangeAcronym(ExchangeAcronymStruct exchangeAcronymStruct) throws NotFoundException 
    {
        List result = cache.getCachedUsersForAcronym(exchangeAcronymStruct.acronym, exchangeAcronymStruct.exchange);
        if (result == null)
        {
    		AcronymUserImpl example = new AcronymUserImpl();
    		addToContainer(example);
    		ObjectQuery query = new ObjectQuery(example);
    		example.setAcronym(exchangeAcronymStruct.acronym);
    		example.setExchangeAcronym(exchangeAcronymStruct.exchange);
    		return doFindFirstQuery(query);
        }
        else
        {
            if (result.isEmpty())
            {
                throw ExceptionBuilder.notFoundException("User not found: exch:acr = " + toString(exchangeAcronymStruct), 0);
            }
            final int len = result.size();
            for (int i = 0; i < len; i++)
            {
                User user = (User)result.get(i);
                if (user.isActive())
                {
                    return user;
                }
            }
            return (User)result.get(0);
        }
	}

	/**
	 * Finds user by user id (acronym).
	 *
	 * @see UserHome#findByName
	 */
	public User findByUserId(String userId) throws NotFoundException
	{
        if (userId.equals("")) {
            throw ExceptionBuilder.notFoundException("The userId passed in, is an empty string " , 0);
        }
        User result = cache.getCachedUser(userId);
        if (result == null)
        {
    		UserIdImpl example = new UserIdImpl();
    		example.setUserId(userId);
    		result = doUserUniqueQuery(example, "No user found for userid '" + userId + "'");
        }
        return result;
	}
	/**
	 * Finds users by role.
	 *
	 * @see UserHome#findByRole
	 */
	public List findByRole(char role) {
		AcronymUserImpl example = new AcronymUserImpl();
		addToContainer(example);
		ObjectQuery query = new ObjectQuery(example);
		example.setRole(role);
		return doUserQuery(query);
	}
	/**
	 * Finds users by type.
	 *
	 * @see UserHome#findByType
	 */
	public List findByType(short type) {
		AcronymUserImpl example = new AcronymUserImpl();
		addToContainer(example);
		ObjectQuery query = new ObjectQuery(example);
		example.setUserType(type);
		return doUserQuery(query);
	}
 	/**
	 * Finds users for a firm.
	 *
	 * @see UserHome#findByFirm
	 */
	public List findByFirm(int firmKey) {
		AcronymUserImpl example = new AcronymUserImpl();
		addToContainer(example);
		ObjectQuery query = new ObjectQuery(example);
		example.setFirmKey(firmKey);
		return doUserQuery(query);
	}

    /**
     * Finds the users which have given assignment type for the class and session.
     * @param classKey
     * @param assignmentType
     * @param sessionName
     * @return
     */ 
    public List findByClassAssignmentType(int classKey, short assignmentType, String sessionName)
    {
        AssignedClass example = new AssignedClass();
		addToContainer(example);
		ObjectQuery query = new ObjectQuery(example);
        
        example.setAssignedClass(classKey);
        example.setAssignmentType(assignmentType);
        example.setSessionName(sessionName);
        
        List result = new ArrayList(0);
        try
        {
            final Vector queryResult = query.find();
            result = new ArrayList(queryResult.size()*10);
            for(int i=0; i< queryResult.size(); i++)
            {
                final AssignedClass asgn = (AssignedClass) queryResult.elementAt(i);
                final AcronymUserImpl userImpl = (AcronymUserImpl)asgn.getUser();
                final List users = cache.getCachedUsersForAcronym(userImpl);
                if (users != null)
                {
                    result.addAll(users);
                }
            }
        }
        catch (PersistenceException e)
        {
            Log.exception(this,"call to findByClassAssignmentType failed.", e);
        }
        
        if(Log.isDebugOn())
        {
            String msg= "Found following users for class/assignment type/session " +classKey + "/" + assignmentType + "/" + sessionName + " >>>";
            final int len = result.size();
            for(int i=0; i<len; i++)
            {
                msg += ":" + ((User)result.get(i)).getUserId();
            }
            Log.debug(this, msg);
        }
        
        return result;
    }

	/**
	 * Queries users in database to prime object pool, if it is being
	 * used.
	 *
	 */
	public void goMaster(boolean failover)
	{
		Log.information(this, "Start of Initialization....");
		AcronymUserImpl example = new AcronymUserImpl();
		addToContainer(example);
		ObjectQuery query = new ObjectQuery(example);
		try
		{
			Vector theUsers = query.find();
			Log.information(this, "Number of users in database = " + theUsers.size());
            
            
		}
		catch (PersistenceException e)
		{
			Log.exception(this, "Initial query for users failed", e);
		}
        new BasicQueriesThread().start();
		Log.information(this, "Initialized!");
	}
    
	
	private class BasicQueriesThread extends Thread
	{
	    BasicQueriesThread() { super("UserMaintenanceServiceHome.BasicQueriesThread"); }
	    UserHomeImpl home = UserHomeImpl.this;
	    public void run()
	    {
	        try
	        {
	            execute();
	        }
	        catch (Exception ex)
	        {
	            Log.exception(home, getName() + ": failed basic queries (non-fatal)", ex);
	        }
	    }
	    private void execute() throws Exception
	    {
	        Log.information(home, getName() + ": do basic queries");
	        
            {
                Log.information(home, getName() + ":  - find user account relations");
                UserAccountRelation example = new UserAccountRelation();
                addToContainer(example);
                ObjectQuery query = new ObjectQuery(example);
                query.find();
            }

            {
                Log.information(home, getName() + ":  - find profiles");
                Profile example = new Profile();
                addToContainer(example);
                ObjectQuery query = new ObjectQuery(example);
                query.find();
            }
            
            {
                Log.information(home, getName() + ":  - find firms");
                FirmHome firmHome = (FirmHome)HomeFactory.getInstance().findHome(FirmHome.HOME_NAME);
                firmHome.findAllFirms();
            }
            
            {
                Log.information(home, getName() + ":  - find user-firm relations");
                UserFirmRelation example = new UserFirmRelation();
                addToContainer(example);
                ObjectQuery query = new ObjectQuery(example);
                query.find();
            }
            
            {
                Log.information(home, getName() + ":  - find assigned classes");
                AssignedClass example = new AssignedClass();
                addToContainer(example);
                ObjectQuery query = new ObjectQuery(example);
                query.find();
            }
            
	        Log.information(home, getName() + ": done basic queries");
	    }
	}

    
	/**
	 * @see BOHome#shutdown
	 */
	public void shutdown()
	{
		Log.information(this, "Shutdown!");
	}
	/**
	 * @see BOHome#start
	 */
	public void start()
	{
		Log.information(this, "Started!");
	}

	/**
	 * Updates user.
	 *
	 * @see UserHome#updateUser
	 */
	public void updateUser(User updatedUser, SessionProfileUserDefinitionStruct updatedValues, boolean membershipDefined)
	    throws DataValidationException
	{
	    UserCombinedImpl userImpl = (UserCombinedImpl)updatedUser;
	    userImpl.fromStruct(updatedValues, membershipDefined);
	    cache.updateOnCommit(userImpl);
	}

	/**
	 * Updates user.
	 *
	 * @see UserHome#updateUser
	 */
    public void updateUserBase(User updatedUser, SessionProfileUserDefinitionStruct updatedValues, boolean membershipDefined)
            throws DataValidationException
    {
	    UserCombinedImpl userImpl = (UserCombinedImpl)updatedUser;
		userImpl.updateBase(updatedValues, membershipDefined);
        cache.updateOnCommit(userImpl);
	}

	public void setTestClassesOnly(String userId, boolean testClassesOnlyFlag)
		throws DataValidationException
	{
		try
		{
    	    UserCombinedImpl userImpl = (UserCombinedImpl)findByUserId(userId);
			userImpl.setTestClassesOnly(testClassesOnlyFlag);
		}
		catch (NotFoundException ex)
		{
			throw ExceptionBuilder.dataValidationException("Cannot set testClassesOnly flag, userid '" + userId + "' is not known.", 0);
		}
	}

	public boolean getTestClassesOnly(String userId)
		throws NotFoundException
	{
	    UserCombinedImpl userImpl = (UserCombinedImpl)findByUserId(userId);
		return userImpl.getTestClassesOnly();
	}

    /**
     * Converts user to a definition struct.
     *
     * @see UserHome# toDefinitionStruct
     */
    public SessionProfileUserDefinitionStruct toDefinitionStruct(User user)
    {
        UserCombinedImpl userImpl = (UserCombinedImpl)user;
        return userImpl.toDefinitionStruct();
    }
    
    public UserSummaryStruct toSummaryStruct(User user)
    {
        UserCombinedImpl userImpl = (UserCombinedImpl)user;
        return userImpl.toSummaryStruct();
    }

    /**
     * Converts user to user information struct.
     *
     * @see UserHome#toSessionProfileUserStruct
     */
    public SessionProfileUserStruct toUserStruct(User user)
    {
        UserCombinedImpl userImpl = (UserCombinedImpl)user;
        return userImpl.toUserStruct();
    }

    /**
     * @see com.cboe.interfaces.domain.user.UserHome#updateAcronymUserData(java.util.List, com.cboe.idl.user.SessionProfileUserDefinitionStruct)
     */
    public void updateAcronymUserData(List usersToUpdate, SessionProfileUserDefinitionStruct updatedValues, boolean membershipDefined) throws DataValidationException
    {
        if (usersToUpdate != null)
        {
            final int len = usersToUpdate.size();
            AcronymUserImpl acrImpl = null;
            for (int i=0; i < len; i++)
            {
                UserCombinedImpl user = (UserCombinedImpl)usersToUpdate.get(i);
                if (acrImpl != null && user.getAcronymUserImpl() == acrImpl)
                {
                    // We expect that all users in the list wil have the same acr impl.
                    continue; // (already updated this impl)
                }
                acrImpl = user.getAcronymUserImpl(); 
                acrImpl.updateBase(updatedValues, membershipDefined);
            }
        }
    }

    /**
     * 
     * Create a new user, having all of the attribributes of any existing user for the given exchAcr.
     * 
     * @param userId - the new userId
     * @param exchAcr - the acronym to use to locate most of the new user's information
     * @param createAsActive - should the new user be activE?
     * @return User - the new user
     * 
     * @throws AlreadyExistsException
     * @throws NotFoundException - thrown if no user having exchAcr is found.
     * @throws DataValidationException
     * @throws SystemException
     * 
     * @see com.cboe.interfaces.domain.user.UserHome#createUsingAcronym(java.lang.String, com.cboe.idl.cmiUser.ExchangeAcronymStruct, boolean)
     */
    public User createUsingAcronym(String userId, ExchangeAcronymStruct exchAcr, boolean createAsActive) throws AlreadyExistsException, NotFoundException, DataValidationException, SystemException
    {
        try
        {
            findByUserId(userId);
            throw ExceptionBuilder.alreadyExistsException("Cannot create userId '"+ userId + "': already exists.", 0);
        }
        catch (NotFoundException ex)
        {
            // (expected)
        }
        UserCombinedImpl theUser = (UserCombinedImpl)findByFirstExchangeAcronym(exchAcr);
        return create(theUser.getAcronymUserImpl(), userId, createAsActive);
    }

    private String toString(ExchangeAcronymStruct exchAcr)
    {
        return exchAcr.exchange + ":" + exchAcr.acronym;
    }

    /**
     * @see com.cboe.interfaces.domain.user.UserHome#deleteUserid(java.lang.String)
     */
    public void deleteUserid(String userid) throws NotFoundException, SystemException
    {
        if (!Transaction.inTransaction())
        {
            throw new RuntimeException("deleteUserid('" + userid + "') must be called from within a transaction!");
        }
        try
        {
            UserCombinedImpl userComboImpl= (UserCombinedImpl)findByUserId(userid);
            userComboImpl.getUserIdImpl().markForDelete();
            cache.deleteOnCommit(userComboImpl);
        }
        catch (PersistenceException ex)
        {
            Log.exception(this, "failed to delete userid '" + userid + "'", ex);
            throw ExceptionBuilder.systemException("failed to delete userid '" + userid + "'", 0);
        }
    }

    /**
     * get dpms who has the class in its assigned class list
     */
    public SessionProfileUserDefinitionStruct[] getDpmsForClass(String userid, int classKey) throws NotFoundException
    {
        UserCombinedImpl user = (UserCombinedImpl)this.findByUserId(userid);
        AcronymUserImpl acrImpl = user.getAcronymUserImpl();
        UserAccountRelation[] dpmAccounts = acrImpl.getDpmAccounts();
        ArrayList resultDpms = new ArrayList();
        for (int i = 0; i < dpmAccounts.length; i++)
        {
            AcronymUserImpl dpmUser = dpmAccounts[i].getAccount();
            int[] dpmClasses = dpmUser.getAssignedClasses();
            for (int j = 0; j < dpmClasses.length; j++)
            {
                if (dpmClasses[j] == classKey)
                {
                    ExchangeAcronymStruct exchAcr = new ExchangeAcronymStruct();
                    exchAcr.exchange = dpmUser.getExchangeAcronym();
                    exchAcr.acronym = dpmUser.getAcronym();
                    User account = this.findByFirstExchangeAcronym(exchAcr);
                    resultDpms.add(toDefinitionStruct(account));
                    break;
                }
            }
        }
        SessionProfileUserDefinitionStruct[] structs = new SessionProfileUserDefinitionStruct[resultDpms.size()];
        resultDpms.toArray(structs);
        return structs;
    }

    /**
     * Find and return the user definition of the DPM joint account for the user
     * who has class <code>classKey</code> listed in the DPM's assigned class
     * list.
     */
    public SessionProfileUserDefinitionStruct getDpmJointAccountForClass(String userid, int classKey) throws DataValidationException, NotFoundException
    {
        UserCombinedImpl user = null;
        try{
            user = (UserCombinedImpl)this.findByUserId(userid);
        }
        catch(NotFoundException nfe){
           Log.exception(this, "User id \"" + userid + "\" not found.", nfe );
            throw ExceptionBuilder.dataValidationException("User id \"" + userid + "\" not found.", DataValidationCodes.INVALID_USER);
        }
        AcronymUserImpl acrImpl = user.getAcronymUserImpl();
        UserAccountRelation[] dpms = acrImpl.getDpmAccounts();
        for (int i = 0; i < dpms.length; i++)
        {
            AcronymUserImpl dpm = dpms[i].getAccount();
            int[] assignedClasses = dpm.getAssignedClasses();
            for (int j = 0; j < assignedClasses.length; j++)
            {
                if (assignedClasses[j] == classKey)
                {
                    Vector dpmAccounts = dpm.getAccountsVector(); // get the JA
                                                                  // for the DPM
                    if (dpmAccounts.size() == 0)
                    {
                        throw ExceptionBuilder.dataValidationException("No accounts assigned to DPM " + dpm.loggableName(), 0);
                    }
                    try
                    {
                        UserAccountRelation ja = (UserAccountRelation) dpmAccounts.firstElement();
                        ExchangeAcronymStruct exchAcr = new ExchangeAcronymStruct();
                        exchAcr.exchange = ja.getAccount().getExchangeAcronym();
                        exchAcr.acronym = ja.getAccount().getAcronym();
                        User account = this.findByFirstExchangeAcronym(exchAcr);
                        return toDefinitionStruct(account);
                    }
                    catch (Exception ex)
                    {
                        throw ExceptionBuilder.dataValidationException("Could not find joint account for dpm" + dpm.loggableName(), 0);
                    }
                }
            }
        }
        if (Log.isDebugOn())
        {
            Log.debug(this, "XXX - No DPM for class " + classKey + " assigned to user " + user);
            Log.debug(this, "XXX - User has " + dpms.length + " UserAccountRelation elements.");
            for (int i = 0; i < dpms.length; i++)
            {
                AcronymUserImpl acct = dpms[i].getAccount();
                StringBuffer buf = new StringBuffer("DPM " + acct.loggableName() + ":");
                for (int j = 0; j < acct.getAssignedClasses().length; j++)
                {
                    buf.append(" " + acct.getAssignedClasses()[j]);
                }
                Log.debug(this, "XXX -     " + buf.toString());
            }
        }
        throw ExceptionBuilder.notFoundException("No DPM for class " + classKey + " assigned to user " + user, 0);
    }

    public AcronymUser findAcronymUserForUserId(String userId) throws NotFoundException
    {
        UserCombinedImpl user = (UserCombinedImpl)findByUserId(userId);
        return user.getAcronymUserImpl();
    }
}
