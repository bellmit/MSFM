package com.cboe.util.collections;

/**
 * IntLongMapPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapPolicy.template (Int/int, Long/long)
 *
 */

public interface IntLongMapPolicy
{
    public boolean canInsert(int key, long newValue);
    public boolean canUpdate(int key, long newValue, long oldValue);
    public boolean canRemove(int key, long oldValue);
    public boolean canRetrieve(int key, long oldValue);
}
