package com.cboe.util.collections;

/**
 * LongLongMapModifyPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapModifyPolicy.template (Long/long, Long/long)
 *
 */

public interface LongLongMapModifyPolicy extends LongLongMapPolicy
{
    public long valueToInsert(long key, long newValue);
    public long valueToUpdate(long key, long oldValue, long newValue);
}
