/*
 * Created on Jun 2, 2004
 *
 */
package com.cboe.lwt.transaction;

/**
 * @author dotyl
 *
 */
public abstract class DefaultTransactable
    implements Transactable
{
    private boolean dirty;
    
    protected DefaultTransactable()
    {
        dirty = false;
    }
    

    public final void dirty()
    {
        dirty = true;
    }
    
    
    public final void clean()
    {
        dirty = false;
    }
    
    
    public final boolean isDirty()
    {
        return dirty;
    }
}
