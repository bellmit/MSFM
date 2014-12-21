package com.cboe.lwt.transaction;


/**
 * In order to be a transactional object it is necessary for this interface to
 * be implemented.
 * 
 * @version 1.1
 */
public final class CreatedTLink extends TLink
{
    ////////////////////////////////////////////////////////////////////////////
    // construction
    
    
    CreatedTLink( Transactable p_underlying )
    {
        super( p_underlying );
        p_underlying.dirty();
    }
    
    
    static CreatedTLink createVersion( Transactable p_underlyingObject )
    {
        return new CreatedTLink( p_underlyingObject );
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
    

    final void commit()
    {
    }        
    
    
    final void abort()
    {
        underlying = null;
    }

}