package com.cboe.lwt.transaction;



/**
 * A session is a logical grouping of threads.
 * You will have only one transaction per session.
 */
public class Session
{
     private static ThreadLocal threadSession  = new ThreadLocal();
     private static LockMgr     defaultLockMgr = new LockMgr();

     String            name;
     CachedTransaction currentTransaction;
     DbTransaction     dbTransaction;
    
    
    private Session( String p_name )
    {
        name               = p_name;
        currentTransaction = null;
        dbTransaction      = null;
    }
    
    
    /**
     * Thread local static factory method
     * @return Session the session associated with the current thread.
     */
    public static Session getCurrent()
    {
        Session curThreadSession = (Session)threadSession.get();
        
        if ( curThreadSession == null )
        {
            curThreadSession = new Session( "Default" );
            threadSession.set( curThreadSession );
        }
	    return curThreadSession;
    }
    
        
    public CachedTransaction getTopLevelTransaction()
    {
        CachedTransaction top = currentTransaction;
        
        if ( top != null )
        {
            top = top.getTopLevelTransaction();
        }
        
        return top;
    }
    
    
//    public DbTransaction getDbTransaction( String p_name )
//        throws PersistException
//    {
//        if ( dbTransaction != null )
//        {
//            throw new PersistException( "Attempting to start nested Db Transaction" );
//        }
//
//        CachedTransaction topXact = getTopLevelTransaction();
//        
//        if ( topXact == null )
//        {
//            throw new PersistException( "Can't start a db Transaction before session.beginTransaction() called" );
//        }
//
//        try
//        {
//            dbTransaction = DbTransaction.getInstance( p_name, 
//                                                       ThreadBoundDbConnection.getThreadConnection() );
//            if ( dbTransaction == null )
//            {
//                throw new PersistException( "Couldn't begin DbTransaction" );
//            }
//            
//            topXact.addObserver( dbTransaction );
//        }
//        catch( SQLException ex )
//        {
//            throw new PersistException( "Couldn't start DbTransaction due to underlying error", ex );
//        }
//        
//        
//        return dbTransaction;
//    }
    
    
    /**
    * Commit any changes made during this transaction.
    */
    public void commit() 
        throws TransactException,
               InterruptedException
    {
        if ( currentTransaction == null ) 
        { 
            throw new TransactException( "Session " + name + ", Committed while not in transaction" );
        }
        
        try
        {
            currentTransaction.commit();
        }
        finally
        {
            currentTransaction = currentTransaction.getParentTransaction();
        }
    }
    
    
    public void setName( String p_name )
    {
	    name = p_name;
    }
    
    
    /**
    *This method is used to check if  transaction exists for the session 
    *@return true if transaction exists else false
    */
    public boolean isInsideTransaction()
    {
	    return currentTransaction != null;
    }

    
    /**
	 * May be useful for debugging purposes.
	 * 
	 * @return String The arbitrary name of this session.
	 */
	public String getName()
	{
	    return name;
    }
    
    
    /**
    * Have the current thread join this session. If the current thread was already in a session, it will leave that session.
    */
    public synchronized void join()
    {
	    threadSession.set( this );
    }
    
    
    /**
    * Have the current thread leave a session.
    * This is not required, however, it is nice for garbage collection.
    */
    public synchronized void leave()
    {
	    threadSession.set( null );
    }
    
    
    /**
     * Undo any changes that this transaction has made to the TransactionalObjects.
     */
    public synchronized void rollback()
        throws TransactException
    {
        if ( currentTransaction == null ) 
        { 
            throw new TransactException( "Session " + name + ", Aborted while not in transaction" );
        }
        
        try
        {
            currentTransaction.abort();
        }
        finally
        {
            currentTransaction = currentTransaction.getParentTransaction();
        }
    }
    
    
    /**
     * Start a root or subtransaction. This is not the same as a database begin.
     * A transaction log will be created to capture changes made during this transaction.
     * If a transaction is already in progress, the transaction started via this command
     * will be a subtransaction.
     * @param p_name A name or transaction type.
     */
    public void startTransaction( String p_name )
    {
        if ( currentTransaction == null )
        {
            // start a top-level transaciton
            currentTransaction = CachedTransaction.getInstance( p_name, defaultLockMgr );
        }
        else
        {
            currentTransaction = currentTransaction.startNestedTransaction("unknown");
        }
    }
    
    
    /**
     * Attempt to append some relevant information to the string.
     * @return String of information about this session.
     */
    public String toString()
    {
	    String result = name;
        
        if ( currentTransaction != null )
        {
            result += " : " + currentTransaction.toString();
        }
        
        return result;
    }

}
