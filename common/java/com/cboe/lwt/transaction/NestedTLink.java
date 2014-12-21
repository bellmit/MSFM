package com.cboe.lwt.transaction;


/**
 * In order to be a transactional object it is necessary for this interface to
 * be implemented.
 * 
 * @version 1.1
 */
public final class NestedTLink extends TLink
{
    private Transactable rollbackTo;

    
    ////////////////////////////////////////////////////////////////////////////
    // construction

    private NestedTLink( Transactable p_toModify )
    {
        super( p_toModify );
        
        rollbackTo = p_toModify.newCopy();
    }

    
    static NestedTLink createVersion( TLink p_parentLink )
    {
        assert ( p_parentLink.tGet() != null );
        
        return new NestedTLink( p_parentLink.tGet() );
    }

    
    // construction
    ////////////////////////////////////////////////////////////////////////////
    
    
    public final void release()
    {
        rollbackTo.release();
        rollbackTo = null;
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
        release(); 
    }        
    
    
    final void abort()
    {
        underlying.updateFrom( rollbackTo );
        release(); 
    }
    

    // package-private... only called by Transaction, which has already acquired all locks
    ////////////////////////////////////////////////////////////////////////////

}