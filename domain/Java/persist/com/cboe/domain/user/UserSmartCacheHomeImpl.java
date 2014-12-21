package com.cboe.domain.user;

import java.sql.DatabaseMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import com.cboe.domain.util.intMaps.ConcurrentIntHashMap;
import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiErrorCodes.NotFoundCodes;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.utilities.Transaction;
import com.cboe.infrastructureServices.persistenceService.ObjectQuery;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import com.cboe.infrastructureServices.systemsManagementService.ApplicationPropertyHelper;
import com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException;
import com.cboe.interfaces.domain.QuoteHome;
import com.cboe.interfaces.domain.TradingClass;
import com.cboe.interfaces.domain.TradingClassHome;
import com.cboe.interfaces.domain.user.UserSmartCache;
import com.cboe.interfaces.domain.user.UserSmartCacheHome;
import com.cboe.server.dependencyFramework.DIFHome;
import com.cboe.server.dependencyFramework.Homes;
import com.cboe.server.util.FFStatusQuery;
import com.cboe.server.util.TradeServerIdPropertyHelper;
import com.cboe.util.ExceptionBuilder;
import com.objectwave.persist.BrokerFactory;

/**
 * 
 */
public class UserSmartCacheHomeImpl extends DIFHome implements UserSmartCacheHome{
    
    private long midnightMillis;
    private Map<String, UserSmartCache> userIdMap;  // Map<UserId><UserSmartCacheImpl>
    private ConcurrentIntHashMap<UserSmartCache> userKeyMap;
    private static transient byte serverInstanceNumber;
    private static final String SpreadTS_ID = "SpreadTradeServer_ID";
    private static transient byte STSID;
    private Map<String, String> correspondentMap;
    private static char[] correspondentChars = { 'X', 'A', 'A', 'A'};
    private volatile boolean querySmartCache = false;
    
    public UserSmartCacheHomeImpl()
    {
        setMidnight();
        userIdMap = new ConcurrentHashMap<String, UserSmartCache>(1007);
        userKeyMap = new ConcurrentIntHashMap<UserSmartCache>(1007);
        correspondentMap = new ConcurrentHashMap<String, String> (1007);
        initTradeServerId();
    }
    
    protected void setMidnight()
    {
        Calendar cal = Calendar.getInstance();
        final int yr = cal.get(Calendar.YEAR);
        final int mo = cal.get(Calendar.MONTH);
        final int dy = cal.get(Calendar.DAY_OF_MONTH);
        cal.clear();
        cal.set(yr, mo, dy);
        this.midnightMillis = cal.getTimeInMillis();
    }

    static void initTradeServerId()
    {
        try
        {
            String serverInstanceNumberStr = ApplicationPropertyHelper.getProperty("serverInstanceNumber");
            String sessionList = ApplicationPropertyHelper.getProperty("sessionNames");
            String tradeServerIdStr = TradeServerIdPropertyHelper.getTradeServerId(serverInstanceNumberStr, sessionList);
            serverInstanceNumber = (byte) Integer.parseInt(tradeServerIdStr);
            
            // Use 5 for the SpreadTradeServer ID
            try {
                STSID = (byte) Integer.parseInt(System.getProperty(SpreadTS_ID));
                serverInstanceNumber = STSID;
            }
            catch (Exception e) {
            }
			correspondentChars[0] = (char) ('A' + serverInstanceNumber-1);
            Log.information("UserSmartCacheHomeImpl: trade server id set to " + serverInstanceNumber);
        }
        catch (NoSuchPropertyException e)
        {
            Log.information("UserSmartCacheHomeImpl:  Unable to determine trade server id value from defined properties. Using 1 as default value");
            serverInstanceNumber = 1;
        }
        catch (NumberFormatException nfe)
        {
            Log.alarm("UserSmartCacheHomeImpl: Unable to determine trade server id value. Invalid value defined for serverInstanceNumber. Using 1 as default value");
            serverInstanceNumber = 1;
        }
    }

    @Override protected void onInitialize() {}
    @Override protected void onStart() {}
    
    @Override 
    protected void onGoSlave() {
        UserSmartCache[] allUsers = retrieveAllUsersFromPersistence();
        for (UserSmartCache aUser : allUsers)
        {
            if (userIdMap.get(aUser.getUserId()) == null) {
                userIdMap.put(aUser.getUserId(), aUser);
            }
            if (userKeyMap.get(aUser.getUserKey()) == null)  {
                userKeyMap.put(aUser.getUserKey(), aUser);
            }
        }  
        
        initQuoteUserClassKeyMap();
        initCorrespondentHashMap(allUsers);
    }
    
    @Override 
    protected void onGoMaster(boolean pIsFailover) 
    {
    	if (pIsFailover)
    	{
    		querySmartCache = true;
    	}
    }
    
    @Override
    public void onPostGoMaster(boolean failover)
    {
    	
    }
    public void initQuoteUserClassKeyMap()
    {
        TradingClassHome tcHome = Homes.getInstance().get(TradingClassHome.class);
        QuoteHome quoteHome = Homes.getInstance().get(QuoteHome.class);
        TradingClass[] tClasses = tcHome.findAllConfigured();
        
        
        int maxAmtToNotExceed=quoteHome.getMaxBootStrapSize();
        int nbrCreated=0;
        int usersCreated=0;
        SimpleDateFormat dateFormatter = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS" );
        
        try
        {
            ArrayList<UserSmartCache> userList = new ArrayList<UserSmartCache>(userIdMap.values());
            
            Collections.sort(userList, new Comparator() {
                public int compare(Object p_o1, Object p_o2)
                {
                    return ( ((UserSmartCacheImpl) p_o1).getLastActionTime() > ((UserSmartCacheImpl)p_o2).getLastActionTime() ? -1 : ( ((UserSmartCacheImpl)p_o1).getLastActionTime() == ((UserSmartCacheImpl)p_o2).getLastActionTime() ? 0 : 1) );
                }                
            });
            
            Outer:
            for(int index=0; index < userIdMap.size(); index++) {
                System.out.println(userList.get(index));
                UserSmartCacheImpl cache = (UserSmartCacheImpl)userList.get(index);
                if (nbrCreated <= maxAmtToNotExceed) {
                    usersCreated++;
                }
                
                // log the user and last updated time
                Log.information(this, "Bootstrapped User:" + cache.getUserId() + 
                        " LastActionTime:" + dateFormatter.format(new Date(cache.getLastActionTime() )));
                
                for (int j = 0; j < tClasses.length; j++) 
                {
                    if (nbrCreated >= maxAmtToNotExceed) {
                        break Outer;
                    }
                    nbrCreated++;
                    if (!tClasses[j].isStrategy())
                        quoteHome.createMapForUser(cache.getUserId(), tClasses[j].getProductClassKey());
                }
                
            }
            Log.information(this, 
                    "\n    Bootstrapped number of non-strategy classes: " + tClasses.length +                     
                    "\n    For: " + usersCreated + " users created." +                     
                    "\n    Total number of users: " +  userIdMap.size() +
                    "\n    Number of ConcurrentHashMaps created: " + nbrCreated +
                    "\n    Max boot strap size set to: " +  maxAmtToNotExceed );
            
        }
        catch (Exception ex)
        {
            Log.exception(this, "failed initial query for user smart cache", ex);
        }
    }

    public UserSmartCache create(String userId, int userKey) throws AlreadyExistsException, DataValidationException{
    	if (querySmartCache)
    	{    		
    		querySmartCache = false;
    		UserSmartCache ucache = checkDatabaseForUpdates(userId);
    		if (ucache != null)
    		{
        		return ucache;
    		}
    	}
        UserSmartCacheImpl aUser = new UserSmartCacheImpl();
        aUser.setAsTransient(true);
        addToContainer(aUser);
        aUser.setUserId(userId);
        aUser.setUserKey(userKey);
        aUser.setLastActionTime(0);
        aUser.setTradeServerId(serverInstanceNumber);
      	// the database update and the correspondentMap update 
        // are done only if it is master. 
        if (FFStatusQuery.getInstance().isCurrentFFStatusMaster())
        {
        	makePersistent(aUser);
            correspondentMap.put(aUser.getUserId(), getNextCorrespondent());
        }
        userIdMap.put(userId, aUser);
        userKeyMap.put(userKey, aUser);
        return aUser;        
    }
    
    private synchronized UserSmartCache checkDatabaseForUpdates(String userId)
    {
    	UserSmartCache ucache = null;
    	UserSmartCache[] results = retrieveAllUsersFromPersistence();
    	for (UserSmartCache aUser: results)
    	{
    		if (!(correspondentMap.containsKey(aUser.getUserId())))
    		{
    			// add this to the the correspondentMap
    			correspondentMap.put(aUser.getUserId(), getNextCorrespondent());
    			// add this to the other maps just in case they don't have it
    			userIdMap.put(aUser.getUserId(), aUser);
	            userKeyMap.put(aUser.getUserKey(), aUser);
    			if (ucache == null && userId.equals(aUser.getUserId()))
    			{
    				ucache = aUser;
    			}
    		}
    	}
		StringBuilder msg = new StringBuilder(60);
		msg.append("Reloaded users from smart cache. ");
		if (ucache != null)
		{
			msg.append(userId).append(" was found in DB");
		}
		else
		{
			msg.append(userId).append(" was not found in DB");
		}
		Log.information(this, msg.toString());

    	return ucache;
    }
    
    /**
     * Makes the order persistent.
     */
    private void makePersistent(UserSmartCacheImpl userCache) {
        userCache.setAsTransient(false);
        try {
            userCache.insert();
        }
        catch (PersistenceException e) {
            Log.exception("UserSmartCacheHomeImpl >> Unable to make UserSmartCache persistent", e);
        }
    }    
    
    private UserSmartCache[] retrieveAllUsersFromPersistence()
    {
        UserSmartCache[] result;
        UserSmartCacheImpl example = new UserSmartCacheImpl();
        addToContainer(example);
        example.setBrokerName(BrokerFactory.getDefaultBroker().getName());
        ObjectQuery query = new ObjectQuery(example);
        example.setTradeServerId(serverInstanceNumber);
        query.addOrderByField("objectIdentifier", false);
        result = doQuery(query);  
        return result;
    }
    
    public long getUserLocalCacheCount()
    {
        return userKeyMap != null ? userKeyMap.size() : 0;
    }
    
    
	public UserSmartCache[] findAll()
	{	   
	    UserSmartCache[] result;
	    if (userKeyMap.size() > 0)  {
	        Collection<UserSmartCache> allUsers = userKeyMap.values();
	        result = allUsers.toArray(new UserSmartCache[userKeyMap.size()]);	        
	    }
	    else {
	        result = retrieveAllUsersFromPersistence();  
            userIdMap.clear();
            userKeyMap.clear();
	        for (UserSmartCache aUser : result) {
	            userIdMap.put(aUser.getUserId(), aUser);
	            userKeyMap.put(aUser.getUserKey(), aUser);
	        }
	        initCorrespondentHashMap(result);
	    }
	    return result;
    }
    
	public UserSmartCache findByUserKey(int userKey) throws NotFoundException
	{
        UserSmartCache result = userKeyMap.get(userKey);
        if (result != null)
        {
            return result;
        }

		UserSmartCacheImpl example = new UserSmartCacheImpl();    	
        addToContainer(example);
        example.setBrokerName(BrokerFactory.getDefaultBroker().getName());
        ObjectQuery query = new ObjectQuery(example);
        example.setUserKey(userKey); 
        example.setTradeServerId(serverInstanceNumber);
		result = doUniqueQuery(query);
		userKeyMap.put(userKey, result);
		userIdMap.put(result.getUserId(), result);
		return result;
    }
    
    public UserSmartCache findByUserId(String userId) throws NotFoundException
    {
        UserSmartCache result = (UserSmartCache)userIdMap.get(userId);
        if (result != null)
        {
            return result;
        }
        
        throw ExceptionBuilder.notFoundException("unable to findByUserId: " + userId, NotFoundCodes.RESOURCE_DOESNT_EXIST);
        
    }
    
    private UserSmartCacheImpl[] doQuery(ObjectQuery query) {
        UserSmartCacheImpl[] result;
        try
        {
            Vector<?> queryResult = query.find();
            result = new UserSmartCacheImpl[queryResult.size()];
            queryResult.copyInto(result);
            addObjectsToContainer( queryResult );
        }
        catch (PersistenceException e)
        {
            Log.exception(this, "Query for users failed", e);
            result = new UserSmartCacheImpl[0];
        }
        return result;
    }   
    
    private void addObjectsToContainer(Vector<?> vector) {

        Enumeration<?> usersEnum = vector.elements();
        while( usersEnum.hasMoreElements() ){
            addToContainer( (UserSmartCacheImpl)usersEnum.nextElement() );
        }
    }    

    private UserSmartCache doUniqueQuery(ObjectQuery query) throws NotFoundException {
        UserSmartCacheImpl theUser;
        try
        {
            theUser = (UserSmartCacheImpl) query.findUnique();
            addToContainer( theUser );
        }
        catch (PersistenceException e)
        {
            throw ExceptionBuilder.notFoundException("Query failed for user: " + e, 0);
        }
        return theUser;
    }

    public final boolean shouldUpdateActivityTime(final String userId) throws NotFoundException
    {
        final UserSmartCacheImpl userCache = (UserSmartCacheImpl)findByUserId(userId);
        return (userCache.getLastActionTime()<= midnightMillis);
    }
    
    /**
     * @see com.cboe.interfaces.domain.user.UserSmartCacheHome#updateActivityTime(java.lang.String)
     */
    public final void updateActivityTime(final String userId) throws NotFoundException
    {
        try
        {
              final UserSmartCacheImpl userCache = (UserSmartCacheImpl)findByUserId(userId);
              if (userCache.getLastActionTime()<= midnightMillis)
              {
                // this should only be once per day:
                // per user
                    synchronized(userCache)
                    {
                        Transaction.startTransaction();
                        boolean committed = false;
                        try
                        {
                            userCache.setLastActionTime(System.currentTimeMillis());
                            committed = Transaction.commit();
                        }
                        finally
                        {
                            if (!committed)
                            {
                                Transaction.rollback();
                            }
                        }
                    }
              }
        }
        catch (NotFoundException ex)
        {
                Log.information(this, "UserSmartCacheHome unable to update last activity time for user:"+ userId + " Exception: " +  ex);
            // If we can't find the cache, then there's nothing to update.  This should never happen.
        }
    }

    /**
     * @see com.cboe.interfaces.domain.user.UserSmartCacheHome#delete(java.lang.String)
     */
    public void delete(String userId) throws SystemException
    {
        try
        {
            final UserSmartCacheImpl userCache = (UserSmartCacheImpl)findByUserId(userId);
            userCache.markForDelete();
        }
        catch (PersistenceException ex)
        {
            throw ExceptionBuilder.systemException("Failed to delete user smart cache for '" + userId + "'", 0);
        }
        catch (NotFoundException ex)
        {
            // ok
        }
    }

    public long getMidnightTime()
    {
        return midnightMillis;
    }
       
    public String getCorrespondent(String userId)
    {
    	String correspondent = correspondentMap.get(userId);
    	// if the correspondent is not found in the map
    	// and if there was a failover (querySmartCache is set),
    	// read the users from database and add the missing entries to map.
    	// this is a one-time deal; any other null should be an 
    	// error condition the caller should handle.
    	if (correspondent == null && querySmartCache)
    	{    		
    		querySmartCache = false;
    		UserSmartCache ucache = checkDatabaseForUpdates(userId);
    		if (ucache == null)
    		{
    			Log.alarm(this, "User could not be found in the user smart cache! userId:" + userId);
    		}
    		correspondent = correspondentMap.get(userId);
    	}
    	return correspondent;    	
    }
    
    private synchronized void initCorrespondentHashMap(UserSmartCache[] allUsers)
    {
    	resetCorrespondent();
    	for (UserSmartCache uCache: allUsers)
    	{
    		correspondentMap.put(uCache.getUserId(), getNextCorrespondent());
    	}
    }
    
    private static synchronized String getNextCorrespondent()
    {
    	boolean done = false;
    	for (int i=3; i > 0 && !done; i--)
    	{
    		if (correspondentChars[i] < 'Z')
    		{
    			correspondentChars[i] = (char) (correspondentChars[i] + 1);
    			done = true;
    		}
    		else
    		{
    			correspondentChars[i] = 'A';
    		}
    	}
    	
    	if (!done)
    	{
    		Log.alarm("UserSmartCacheHomeImpl: Correspondent IDs exhausted!!! Resetting...");
    		resetCorrespondent();
    	}
    	return String.copyValueOf(correspondentChars);
    }
    
    private static synchronized void resetCorrespondent()
    {
    	correspondentChars[1] = 'A';
    	correspondentChars[2] = 'A';
    	correspondentChars[3] = 'A';
    }
}

