package com.cboe.util.collections;

/**
 * LongStringMapPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapPolicy.template (Long/long, String/String)
 *
 */

public interface LongStringMapPolicy
{
    public boolean canInsert(long key, String newValue);
    public boolean canUpdate(long key, String newValue, String oldValue);
    public boolean canRemove(long key, String oldValue);
    public boolean canRetrieve(long key, String oldValue);
}
