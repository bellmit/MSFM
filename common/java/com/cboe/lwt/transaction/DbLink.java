package com.cboe.lwt.transaction;

import java.sql.Connection;


/**
 * Simple Persistent Link class 
 */
public final class DbLink implements ILink
{
    ////////////////////////////////////////////////////////////////////////////
    // typesafe enum for persistent status
    
    public static final class PState
    {
        public static final PState INVALID  = new PState( -1 );
        public static final PState CLEAN    = new PState( 0 );
        public static final PState UPDATED  = new PState( 1 );
        public static final PState INSERTED = new PState( 2 );
        public static final PState DELETED  = new PState( 3 );
        
        final int val;
        
        private PState( int p_state )
        {
            val = p_state;
        }
    }
    
 
    // typesafe enum for persistent status
    ////////////////////////////////////////////////////////////////////////////

    
    private Persistable underlying;
    private PState persistentState;

    
    private DbLink( Persistable p_underlying, 
                    PState      p_state )
    {
        underlying = p_underlying;
        persistentState = p_state;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    // package-private factory methods -- only called from DbTransaction
    
    static DbLink newReference( Persistable p_underlyingObject )
    {
        return new DbLink( p_underlyingObject, PState.CLEAN );
    }
    
    static DbLink insertAsNew( Persistable p_underlyingObject )
    {
        return new DbLink( p_underlyingObject, PState.INSERTED );
    }
    
    
    // factory methods
    ////////////////////////////////////////////////////////////////////////////
    
    
    public void release()
    {
        // placeholder for releasing pooled objects
    }
    
    
    public void refreshFromDb( Persistable p_underlyingObject )
    {
        persistentState = PState.CLEAN;
        underlying = p_underlyingObject;
    }

    
    public Object get()
    {
        if ( persistentState == PState.DELETED )
        {
            return null;
        }
        
        return underlying;
    }
    
    
    public void update( Persistable p_new )
        throws TransactException
    {
        if ( persistentState == PState.DELETED )
        {
            throw new TransactException( "Attempting to update a deleted object" );
        }

        underlying = p_new;

        if ( persistentState != PState.INSERTED )
        {
            persistentState = PState.UPDATED;
        }
    }
    
    
    public void delete()
    {
        if ( persistentState == PState.INSERTED )
        {
            // then the inserted object was removed... back to start state
            underlying = null;
            persistentState = PState.CLEAN;
        }
        else
        {
            persistentState = PState.DELETED;
        }
    }

   
    public boolean isDirty()
    {
        return persistentState.val > PState.CLEAN.val;
    }
    
    
    public boolean isDeleted()
    {
        return persistentState == PState.DELETED;
    }
    

    public boolean refersToSameObject( DbLink p_equalTo )
    {
        if ( this == p_equalTo )
        {
            return true;
        }
        
        if ( p_equalTo == null )
        {
            return false;
        }
        
        return underlying == p_equalTo.underlying;        
    }
    

    ////////////////////////////////////////////////////////////////////////////
    // Package private methods
    // package-private... only called by Transaction, which has already acquired all locks

    
    void commitPhase1( Connection p_con )
        throws TransactException
    {
        // note: do nothing for  or  states )
        if (    persistentState.val == PState.INVALID.val 
             || persistentState.val == PState.CLEAN.val )
        {
            return;
        }
        
        
        if ( persistentState.val == PState.UPDATED.val )
        {
            underlying.update( p_con );
        }
        else if ( persistentState.val == PState.INSERTED.val )
        {
            underlying.insert( p_con );
        }
        else if ( persistentState.val == PState.DELETED.val )
        {
            underlying.delete( p_con );
        }
    }
    
    
    void commitPhase2()
    {
        // no-op (2nd phase of a persistent transaction is the actual DB commit)
    }        
    
    
    // package-private... only called by Transaction, which has already acquired all locks
    void abort()
    {
        persistentState = PState.INVALID;
        underlying = null;
    }
}