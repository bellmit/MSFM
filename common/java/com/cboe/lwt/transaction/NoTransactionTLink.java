package com.cboe.lwt.transaction;


/**
 * In order to be a transactional object it is necessary for this interface to
 * be implemented.
 * 
 * @version 1.1
 */
public final class NoTransactionTLink extends TLink
{
    ////////////////////////////////////////////////////////////////////////////
    // construction
    
    
    NoTransactionTLink( Transactable p_underlying )
    {
        super( p_underlying );
    }

    
    // construction
    ////////////////////////////////////////////////////////////////////////////
    
    
    public final void release()
    {
    }
    
    
    public final Object get()
    {
        return tGet();
    }
    
    
    public final Transactable tGet()// and people say generics are a bad thing... look at the alternative  ugh!
    {
        if ( deleted )
        {
            return null;
        }
        
        return underlying;
    }
     
    ////////////////////////////////////////////////////////////////////////////
    // Package private methods
    // package-private... only called by Transaction, which has already acquired all locks
    //
    // NOTE: These following 2 methods SHOULD NEVER BE CALLED

    final void commit()
    {
        throw new RuntimeException( "Commit called on NoTransactionTLink" );
    }        
    
    
    final void abort()
    {
        throw new RuntimeException( "Abort called on NoTransactionTLink" );
    }

}