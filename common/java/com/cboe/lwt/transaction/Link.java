package com.cboe.lwt.transaction;



/**
 * In order to be a transactional object it is necessary for this interface to
 * be implemented.
 * 
 * @version 1.1
 */
public final class Link
    implements ILink
{
    private Identifiable underlying;
    private final Identifiable originalReference;


    ////////////////////////////////////////////////////////////////////////////
    // private constructor
    
    
    private Link( Identifiable p_underlying )
    {
        underlying = p_underlying;
        originalReference = p_underlying;
    }
    
    
    // private constructor
    ////////////////////////////////////////////////////////////////////////////
    // factory methods
    // package-private... only called by Transaction
    // users can only get XactLinks from Transactions
    
    static Link getInstance( Identifiable p_underlyingObject )
    {
        return new Link( p_underlyingObject );
    }

    
    // factory methods
    ////////////////////////////////////////////////////////////////////////////
    
    
    public void release()
    {
        // placeholder for releasing pooled objects
    }

    
    public final Object get()
    {
        return underlying;
    }
    
    
    public void delete()
    {
        underlying = null;
    }

   
    public boolean isDeleted()
    {
        return underlying == null;
    }
    

    /**
     * @return true if the reference (not the referred object) has changed
     */
    public boolean isDirty()
    {
        return originalReference != underlying;
    }
}
