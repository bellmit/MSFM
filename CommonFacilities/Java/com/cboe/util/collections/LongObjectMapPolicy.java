package com.cboe.util.collections;

/**
 * LongObjectMapPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapPolicy.template (Long/long, Object/Object)
 *
 */

public interface LongObjectMapPolicy
{
    public boolean canInsert(long key, Object newValue);
    public boolean canUpdate(long key, Object newValue, Object oldValue);
    public boolean canRemove(long key, Object oldValue);
    public boolean canRetrieve(long key, Object oldValue);
}
