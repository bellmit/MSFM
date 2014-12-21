package com.cboe.util.collections;

/**
 * LongLongMapPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapPolicy.template (Long/long, Long/long)
 *
 */

public interface LongLongMapPolicy
{
    public boolean canInsert(long key, long newValue);
    public boolean canUpdate(long key, long newValue, long oldValue);
    public boolean canRemove(long key, long oldValue);
    public boolean canRetrieve(long key, long oldValue);
}
