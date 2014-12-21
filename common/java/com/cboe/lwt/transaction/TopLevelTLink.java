package com.cboe.lwt.transaction;


/**
 * In order to be a transactional object it is necessary for this interface to
 * be implemented.
 * 
 * @version 1.1
 */
public final class TopLevelTLink extends TLink
{
    private Transactable current;
    
    
    ////////////////////////////////////////////////////////////////////////////
    // construction
    
    
    private TopLevelTLink( Transactable p_underlying )
    {
        super( p_underlying );
        underlying.clean();
        current = null;
    }
    
    
    static TopLevelTLink createVersion( Transactable p_underlyingObject )
    {
        return new TopLevelTLink( p_underlyingObject );
    }

    
    // construction
    ////////////////////////////////////////////////////////////////////////////
    
    
    public final void release()
    {
        if ( current != null )
        {
            current.release();
            current = null;
        }
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
        
        if ( current == null )
        {
            current = underlying.newCopy();
        }
        return current;
    }
     
    ////////////////////////////////////////////////////////////////////////////
    // Package private methods
    // package-private... only called by Transaction, which has already acquired all locks
    

    final void commit()
    {
        if ( current == null )
        {
            return;  // it's not been read
        }
  
        if ( ! current.isDirty() )
        {
            return;
        }
        
        // then still in synch
        
        underlying.updateFrom( current );
        release(); 
    }        
    
    
    final void abort()
    {
        deleted = false;
        
        release(); 
    }

}