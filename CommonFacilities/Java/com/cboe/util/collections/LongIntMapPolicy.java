package com.cboe.util.collections;

/**
 * LongIntMapPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapPolicy.template (Long/long, Int/int)
 *
 */

public interface LongIntMapPolicy
{
    public boolean canInsert(long key, int newValue);
    public boolean canUpdate(long key, int newValue, int oldValue);
    public boolean canRemove(long key, int oldValue);
    public boolean canRetrieve(long key, int oldValue);
}
