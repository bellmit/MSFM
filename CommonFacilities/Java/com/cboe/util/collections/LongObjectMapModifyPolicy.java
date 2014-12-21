package com.cboe.util.collections;

/**
 * LongObjectMapModifyPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapModifyPolicy.template (Long/long, Object/Object)
 *
 */

public interface LongObjectMapModifyPolicy extends LongObjectMapPolicy
{
    public Object valueToInsert(long key, Object newValue);
    public Object valueToUpdate(long key, Object oldValue, Object newValue);
}
