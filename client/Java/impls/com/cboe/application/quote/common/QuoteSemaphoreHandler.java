package com.cboe.application.quote.common;

import java.util.HashMap;
import java.util.Map;
import com.cboe.application.util.BlockableSemaphore;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.cboe.domain.util.UserSessionClassContainer;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.idl.cmiErrorCodes.NotAcceptedCodes;
import com.cboe.util.ExceptionBuilder;

/**
 *
 * @author Gijo Joseph
 */
public class QuoteSemaphoreHandler
{
    public static final String MAX_CONCURRENT_QUOTES_PER_CLASS = "maxConcurrentQuotesPerClass";
    public static final String QUOTE_TIMEOUT_PENDING_QUOTES = "quoteTimeoutPendingQuote";
    public static final String QUOTE_TIMEOUT_PENDING_CANCEL = "quoteTimeoutPendingCancel";
    private static int maxConcurrentQuotesPerClass;
    private static long quoteTimeoutPendingQuote;
    private static long quoteTimeoutPendingCancel;
    private static String cancelInProgressMsg;
    private static String concurrentQuotesExceededLimitMsg;
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock(); // this lock protects userSemaphoreLocks
    private static Map<String, UserSessionClassLocksContainer> userSemaphoreLocks = new HashMap<String, UserSessionClassLocksContainer>(11); // jeyed by userId


    static
    {
    	try
    	{
    		maxConcurrentQuotesPerClass = Integer.parseInt(System.getProperty(MAX_CONCURRENT_QUOTES_PER_CLASS, "1"));
    		quoteTimeoutPendingQuote = Long.parseLong(System.getProperty(QUOTE_TIMEOUT_PENDING_QUOTES, "0"));
    		quoteTimeoutPendingCancel = Long.parseLong(System.getProperty(QUOTE_TIMEOUT_PENDING_CANCEL, "0"));
    	}
    	catch (Exception e)
    	{
    		maxConcurrentQuotesPerClass = 1;
    		quoteTimeoutPendingQuote = 0L;
    		quoteTimeoutPendingCancel = 0L;
    	}
    	cancelInProgressMsg = "Quote Cancel by class is in progress!";
    	concurrentQuotesExceededLimitMsg = "Concurrent Quotes exceeded the limit (" + maxConcurrentQuotesPerClass + ")";
    }

    public static int acquireQuoteEntryAccess(String userId, String sessionName, int classKey) throws NotAcceptedException
    {
        UserSessionClassContainer key = new UserSessionClassContainer(userId, sessionName, classKey);
    	BlockableSemaphore sem = getUserSessionClassSemaphore(key, true);
    	if (sem.isBlocked())
    	{
    		throw ExceptionBuilder.notAcceptedException(cancelInProgressMsg, NotAcceptedCodes.QUOTE_CANCEL_IN_PROGRESS);
    	}
    	if (!(sem.acquireNoWait()))
    	{
    		throw ExceptionBuilder.notAcceptedException(concurrentQuotesExceededLimitMsg, NotAcceptedCodes.EXCEEDS_CONCURRENT_QUOTE_LIMIT);
    	}
    	return sem.getSemaphoresUsed();
    }

    public static void releaseQuoteEntryAccess(String userId, String sessionName, int classKey) throws NotAcceptedException
    {
        UserSessionClassContainer key = new UserSessionClassContainer(userId, sessionName, classKey);
    	BlockableSemaphore sem = getUserSessionClassSemaphore(key, false);
    	if (sem != null)
    	{
    		sem.release();
    	}
    }

    public static void acquireSingleQuoteCancelAccess(String userId, String sessionName, int classKey) throws NotAcceptedException
    {
        UserSessionClassContainer key = new UserSessionClassContainer(userId, sessionName, classKey);
    	BlockableSemaphore sem = getUserSessionClassSemaphore(key, true);
    	if (sem.isBlocked())
    	{
    		throw ExceptionBuilder.notAcceptedException(cancelInProgressMsg, NotAcceptedCodes.QUOTE_CANCEL_IN_PROGRESS);
    	}
    }

    public static void releaseSingleQuoteCancelAccess(String userId, String sessionName, int classKey) throws NotAcceptedException
    {
    	// NOTHING TO BE DONE
    }

    public static void acquireQuoteCancelByClassAccess(String userId, String sessionName, int classKey)
    {
        UserSessionClassContainer key = new UserSessionClassContainer(userId, sessionName, classKey);
    	BlockableSemaphore sem = getUserSessionClassSemaphore(key, true);
    	sem.setSemaphoreBlock();
    }

    public static void releaseQuoteCancelByClassAccess(String userId, String sessionName, int classKey)
    {
        UserSessionClassContainer key = new UserSessionClassContainer(userId, sessionName, classKey);
    	BlockableSemaphore sem = getUserSessionClassSemaphore(key, false);
    	if (sem != null)
    	{
    		sem.resetSemaphoreBlock(false);
    	}
    }

    public static void acquireQuoteDeleteAccess(String userId, String sessionName, int classKey)
    {
    	// used for internally generated cancels
        UserSessionClassContainer key = new UserSessionClassContainer(userId, sessionName, classKey);
    	BlockableSemaphore sem = getUserSessionClassSemaphore(key, true);
    	sem.setSemaphoreBlock();
    }

    public static void releaseQuoteDeleteAccess(String userId, String sessionName, int classKey)
    {
        UserSessionClassContainer key = new UserSessionClassContainer(userId, sessionName, classKey);
    	BlockableSemaphore sem = getUserSessionClassSemaphore(key, false);
    	if (sem != null)
    	{
    		sem.resetSemaphoreBlock(false);
    	}
    }

    public static void acquireQuoteCancelAllAccess(String userId) throws NotAcceptedException
    {
    	// Nothing needed here
    }

    public static void releaseQuoteCancelAllAccess(String userId)
    {
    	// Nothing needed here
    }

    //////////////////////////////////////////////////////////////////////////////

    private static BlockableSemaphore getUserSessionClassSemaphore(UserSessionClassContainer key, boolean create)
    {
	    UserSessionClassLocksContainer userSemLock = getUserSessionClassLocksContainer(key.getUserId(), create);
	    if (userSemLock == null)
	    {
	    	return null;
	    }
    	ReadWriteLock rwLock = userSemLock.getReadWriteLock();
        boolean rLockReleased = false;
        rwLock.readLock().lock();
        try
        {
        	BlockableSemaphore sem = userSemLock.getBlockableSemaphoreMap().get(key);
	        if (sem == null)
	        {
	        	rwLock.readLock().unlock();
	            rwLock.writeLock().lock();
	            rLockReleased = true;
	            try
	            {
	            	sem = userSemLock.getBlockableSemaphoreMap().get(key);
			        if (sem == null)
			        {
			            sem = new BlockableSemaphore(maxConcurrentQuotesPerClass, true, quoteTimeoutPendingQuote, quoteTimeoutPendingCancel);
			            userSemLock.getBlockableSemaphoreMap().put(key, sem);
			        }
	            }
	            finally
	            {
	            	rwLock.writeLock().unlock();
	            }
	        }
	        return sem;
        }
        finally
        {
            if (!rLockReleased)
            {
            	rwLock.readLock().unlock();
            }
        }

    }


    //////////////////////////////////////////////////////////////////////////////


    private static UserSessionClassLocksContainer getUserSessionClassLocksContainer(String userId, boolean create)
    {
        rwLock.readLock().lock();
        boolean rLockReleased = false;
        try
        {
            UserSessionClassLocksContainer userSemLock = userSemaphoreLocks.get(userId);
            if (userSemLock == null && create)
            {
                rwLock.readLock().unlock();
                rwLock.writeLock().lock();
                rLockReleased = true;
                try
                {
                    userSemLock = userSemaphoreLocks.get(userId);
                    if (userSemLock == null)
                    {
                        userSemLock = new UserSessionClassLocksContainer(new ReentrantReadWriteLock(), new HashMap<UserSessionClassContainer, BlockableSemaphore>(89));
                        userSemaphoreLocks.put(userId, userSemLock);
                    }
                }
                finally
                {
                    rwLock.writeLock().unlock();
                }
            }
            return userSemLock;
        }
        finally
        {
            if (!rLockReleased)
            {
                rwLock.readLock().unlock();
            }
        }
    }

    // cleanup actions when a user logs out.
    public static void cleanupSemaphores(String userId)
    {
        rwLock.writeLock().lock();
        try
        {
            UserSessionClassLocksContainer userSemLock = userSemaphoreLocks.get(userId);
            if (userSemLock != null)
            {
                userSemLock.getBlockableSemaphoreMap().clear();
            }
            userSemaphoreLocks.remove(userId);
        }
        finally
        {
            rwLock.writeLock().unlock();
        }

    }

    // Wrapper class that includes a User specific ReadWriteLock and a Map that contains class level semaphores.
    static class UserSessionClassLocksContainer
    {
    	private final ReadWriteLock rwLock;
    	private final Map<UserSessionClassContainer, BlockableSemaphore> blockableSemaphoreMap;

    	public UserSessionClassLocksContainer(ReadWriteLock rwLock, Map<UserSessionClassContainer, BlockableSemaphore> blockableSemaphoreMap)
    	{
    		this.rwLock = rwLock;
    		this.blockableSemaphoreMap = blockableSemaphoreMap;
    	}

    	public ReadWriteLock getReadWriteLock()
    	{
    		return rwLock;
    	}

    	public Map<UserSessionClassContainer, BlockableSemaphore> getBlockableSemaphoreMap()
    	{
    		return blockableSemaphoreMap;
    	}
    }
}
