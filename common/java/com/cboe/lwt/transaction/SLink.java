package com.cboe.lwt.transaction;




/**
 * Session Link Class
 * 
 * Same as transaction link, but works with a session.
 */
public final class SLink implements ILink
{
    private CachedTransaction lastTransaction;
    private TLink             lastLink;
    private Transactable      underlying;
 

    private SLink( Transactable p_underlying )
    {
        underlying = p_underlying;
        lastTransaction = Session.getCurrent().currentTransaction;
    }
    
    
    /**
     * Creates a link to an already existing, and possibly referenced Transactable object
     * 
     * If the session is currently in a transaction, it creates and returns an
     * SLink to that transaction... otherwise, it returns an non transactional SLink 
     * which may be added to a future transaction
     * 
     * @param p_underlyingObject object to link to
     * @return Link to p_underlyingObject (if the current session is in a transaction
     * it will return an SLink, if not, it will return a Link)
     */
    public static SLink linkToExisting( Transactable p_underlying )
    {
        SLink result = new SLink( p_underlying );
        
        if ( result.lastTransaction != null )
        {
            result.lastLink = result.lastTransaction.addExisting( p_underlying );
        }
        else
        {
            result.lastLink = new NoTransactionTLink( p_underlying );
        }

        return result;
    }
    
    
    /**
     * Creates a link to a new Transactable object
     * 
     * If the session is currently in a transaction, it creates and returns an
     * SLink to that transaction... otherwise, it returns an non transactional SLink 
     * which may be added to a future transaction
     * 
     * @param p_underlyingObject object to link to
     * @return Link to p_underlyingObject (if the current session is in a transaction
     * it will return an SLink, if not, it will return a Link)
     */
    public static SLink linkToNew( Transactable p_underlying )
    {
        SLink result = new SLink( p_underlying );
        
        if ( result.lastTransaction != null )
        {
            result.lastLink = result.lastTransaction.createNew( p_underlying );
        }
        else
        {
            result.lastLink = new NoTransactionTLink( p_underlying );
        }

        return result;
    }
    
    
    public void release()
    {
        // placeholder for releasing pooled objects
    }
    
    
    public final Object get()
    {
        return getLink().get();
    }

    
    public TLink getLink()
    { 
        CachedTransaction curTransaction = Session.getCurrent().currentTransaction;
        if ( lastTransaction == curTransaction )
        {
            return lastLink;
        }
        
        // if here, then the transaction has changed since the last invocation
        if ( curTransaction == null )
        {
            lastLink = new NoTransactionTLink( underlying );
            return lastLink;
        }
        
        // if here, then the xact has changed nesting levels 
        lastTransaction = curTransaction;
        
        lastLink = curTransaction.findByUid( lastLink.getId() );
        if ( lastLink == null )
        {
            lastLink = curTransaction.addExisting( underlying );
        }
        
        return lastLink;
    }

    
    public void delete()
    {
        getLink().delete();
    }
 

    public boolean isDeleted()
    {
        return getLink().isDeleted();
    }
}