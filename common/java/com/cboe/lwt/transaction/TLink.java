/*
 * Created on May 17, 2004
 *
 */

package com.cboe.lwt.transaction;

/**
 * @author dotyl
 *
 */
public abstract class TLink implements ILink
{
    private   Uid          id;
    protected Transactable underlying;
    protected boolean      deleted;
    
    protected TLink( Transactable p_underlying )
    {
        underlying = p_underlying;
        id         = underlying.getUid();
        deleted    = false;
    }
    
    public final Uid getId()
    {
        return id;
    }
    
    
    public final void delete()
    {
        deleted = true;
        tGet().dirty();
    }

   
    public final boolean isDeleted()
    {
        return deleted;
    }
    
    public abstract Transactable tGet(); // and people say generics are a bad thing... look at the alternative  ugh!

    abstract void commit();
    abstract void abort();
}