/*
 * Created on Mar 24, 2005
 */
package com.cboe.domain.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.FrameworkComponent;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.utilities.Transaction;
import com.cboe.infrastructureServices.foundationFramework.utilities.TransactionListener;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;

/**
 * Cache the combined user impls.
 * 
 * @author sinclair
 */
class UserCache
{
    private BOHome logTarget;
    
    private Object cacheSync = new Object();
    private HashMap combinedUsersByKey = new HashMap(20000); // userkey -> User
    private HashMap combinedUsersById = new HashMap(20000);  // userid -> User
    private HashMap combinedUsersByAcr = new HashMap(10000); // acr -> {exch -> {users}} 
    
    private UserCache.UserCacheDataFinder dataFinder;
    
    // This interface is implemented by the UserHomeImpl.  Rather than using a
    // UserHomeImpl reference directly, this cache will only deal with the interface
    // to minimize inter-class coupling.
    public static interface UserCacheDataFinder
    {
        List findUserIdImpls(int userAcronymKey) throws PersistenceException;
    }
    
    public void deleteOnCommit(UserCombinedImpl p_user)
    {
        Transaction.registerListener(new DeleteUserCacheOnCommit(p_user));
    }
    
    public void updateOnCommit(UserCombinedImpl p_user)
    {
        Transaction.registerListener(new UpdateUserCacheOnCommit(p_user));
    }
   

    private class DeleteUserCacheOnCommit implements TransactionListener
    {
        UserCombinedImpl user;
        public DeleteUserCacheOnCommit(UserCombinedImpl p_user)
        {
            this.user = p_user;
        }
        public void commitEvent()
        {
            synchronized (cacheSync)
            {
                removeFromCache();
            }
        }
        public void rollbackEvent()
        {
        }
        
        private void removeFromCache()
        {
            synchronized (cacheSync)
            {
                HashMap exchMap = (HashMap)combinedUsersByAcr.get(user.getAcronym());
                if (exchMap != null)
                {
                    List users = (List)exchMap.get(user.getExchangeAcronym());
                    if (users != null)
                    {
                        final int len = 0;
                        for (int i=0; i < len; i++)
                        {
                            UserCombinedImpl currUser = (UserCombinedImpl)users.get(i);
                            if (currUser.getUserId().equals(user.getUserId()))
                            {
                                users.remove(i);
                                break;
                            }
                        }
                    }
                    combinedUsersByAcr.remove(user.getAcronym());
                }
                combinedUsersById.remove(user.getUserId());
                combinedUsersByKey.remove(new Integer(user.getUserIdKey()));
                Log.information(logTarget, "Removed " + user + " from cache.");
            }
        }
    }
    
    
    private class UpdateUserCacheOnCommit implements TransactionListener
    {
        UserCombinedImpl user;
        public UpdateUserCacheOnCommit(UserCombinedImpl p_user)
        {
            this.user = p_user;
        }
        public void commitEvent()
        {
            try
            {
                synchronized (cacheSync)
                {
                    if (!acronymIsCached(user.getAcronym(), user.getExchangeAcronym()))
                    {
                        cacheUser(user.getAcronymUserImpl());
                    }
                    cacheUserId(user);
                }
            }
            catch (PersistenceException ex)
            {
                Log.exception(logTarget, "Failed to update cache; dropping user '"+user+"' from cache", ex);
                removeFromCache();
            }
        }
        public void rollbackEvent()
        {
        }
        
        private void removeFromCache()
        {
            synchronized (cacheSync)
            {
                HashMap exchMap = (HashMap)combinedUsersByAcr.get(user.getAcronym());
                if (exchMap != null)
                {
                    List users = (List)exchMap.get(user.getExchangeAcronym());
                    if (users != null)
                    {
                        final int len = 0;
                        for (int i=0; i < len; i++)
                        {
                            UserCombinedImpl currUser = (UserCombinedImpl)users.get(i);
                            combinedUsersById.remove(currUser.getUserId());
                            combinedUsersByKey.remove(new Integer(currUser.getUserIdKey()));
                        }
                    }
                    combinedUsersByAcr.remove(user.getAcronym());
                }
                combinedUsersById.remove(user.getUserId());
                combinedUsersByKey.remove(new Integer(user.getUserIdKey()));
                Log.information(logTarget, "Removed " + user + " from cache because of failed 'add to cache'");
            }
        }
    }
    
    public UserCache(BOHome p_logTarget, UserCacheDataFinder p_finder)
    {
        this.logTarget = p_logTarget;
        this.dataFinder = p_finder;
    }

    UserCombinedImpl getCachedUser(int externalUserKey)
    {
        synchronized (cacheSync)
        {
            return (UserCombinedImpl)combinedUsersByKey.get(new Integer(externalUserKey));
        }
    }
    
    UserCombinedImpl getCachedUser(String userId)
    {
        synchronized (cacheSync)
        {
            return (UserCombinedImpl)combinedUsersById.get(userId);
        }
    }
    
    List getCachedUsersForAcronym(AcronymUserImpl baseUser) throws PersistenceException
    {
        synchronized (cacheSync)
        {
            List users = this.getCachedUsersForAcronym(baseUser.getAcronym(), baseUser.getExchangeAcronym());
            if (users == null)
            {
                users = cacheUser(baseUser);
            }
            return users;
        }
    }
    
    List getCachedUsersForAcronym(String acronym, String exchange)
    {
        synchronized (cacheSync)
        {
            HashMap mapByExch = (HashMap)combinedUsersByAcr.get(acronym);
            if (mapByExch != null)
            {
                return (List)mapByExch.get(exchange);
            }
            return null;
        }
    }
    
    boolean acronymIsCached(String acronym, String exchange)
    {
        synchronized (cacheSync)
        {
            Map exchMap = (Map)combinedUsersByAcr.get(acronym);
            return exchMap != null && exchMap.get(exchange) != null;
        }
    }
    
    /**
     * Cache the given UserImpl (AcronymUser impl).  
     * Returns a List of all related UserCombinedImpl (these are implementations of User
     * which combine a UserIdImpl with a UserImpl).
     * 
     * <p><b>Side-effect:</b> This method will query the database to identify related UserIdImpl. 
     * 
     * @param user - user to cache
     * @return
     * @throws PersistenceException
     */
    List cacheUser(AcronymUserImpl user) throws PersistenceException
    {
        // Make sure we can find 'em first.
        //
        List userIds = dataFinder.findUserIdImpls(user.getAcronymUserKey());
        return cacheUser(user, userIds);
    }
    
    /**
     * Cache the combined users realized by combining user with each of userIds
     * @param user
     * @param userIds List&lt;UserIdImpl&gt;
     * @return
     */
    List cacheUser(AcronymUserImpl user, List userIds)
    {
        synchronized (cacheSync)
        {
            Map mapByExch = (Map)combinedUsersByAcr.get(user.getAcronym());
            if (mapByExch == null)
            {
                // (only expect a max of 3 elements in this map: CBOE, CFE, and ONE)
                combinedUsersByAcr.put(user.getAcronym(), mapByExch = new HashMap(10));
            }
            List userList = (List)mapByExch.get(user.getExchangeAcronym());
            if (userList == null)
            {
                mapByExch.put(user.getExchangeAcronym(), userList = new ArrayList());
            }
            
            final int len = userIds.size(); 
            for (int i = 0; i < len; i++)
            {
                UserIdImpl userId = (UserIdImpl)userIds.get(i);
                cacheUserId(new UserCombinedImpl(user, userId));
            }
            
            return userList;
        }
    }
    
   
    /**
     * Update the cache with this userImpl & userIdImpl.
     * Will create a UserCombinedImpl and store it in the cache.
     * 
     * @param user
     * @param userId
     * @return
     * @throws PersistenceException
     */
    private UserCombinedImpl cacheUserId(UserCombinedImpl combinedUser)
    {
        combinedUsersById.put(combinedUser.getUserId(), combinedUser);
        combinedUsersByKey.put(new Integer(combinedUser.getUserIdKey()), combinedUser);
        
        HashMap mapByExch = (HashMap)combinedUsersByAcr.get(combinedUser.getAcronym());
        if (mapByExch == null)
        {
            combinedUsersByAcr.put(combinedUser.getAcronym(), mapByExch = new HashMap());
        }
        ArrayList userList = (ArrayList)mapByExch.get(combinedUser.getExchangeAcronym());
        if (userList == null)
        {
            throw new IllegalStateException("Attempting to cache user " + combinedUser + ", but found no map for exchange '" + combinedUser.getExchangeAcronym() + "'");
        }
        else
        {
            final int len = userList.size();
            for (int i = 0; i < len; i++)
            {
                UserCombinedImpl aUser = (UserCombinedImpl)userList.get(i);
                if (aUser.getUserIdKey() == combinedUser.getUserIdKey())
                {
                    if (Log.isDebugOn())
                    {
                        Log.debug(logTarget, "UserCache: Replacing userid " + aUser.getUserId() + " in cache.");
                    }
                    userList.set(i, combinedUser);
                    return combinedUser;
                }
            }
        }
        userList.add(combinedUser);
        return combinedUser;
    }

    /**
     * @return
     */
    public List getAllCachedUsers()
    {
        synchronized (cacheSync)
        {
            List allUsers = new ArrayList(this.combinedUsersById.size());
            allUsers.addAll(this.combinedUsersById.values());
            return allUsers;
        }
    }

}
