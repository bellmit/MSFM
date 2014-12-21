package com.cboe.util.collections;

/**
 * IntObjectMapPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapPolicy.template (Int/int, Object/Object)
 *
 */

public interface IntObjectMapPolicy
{
    public boolean canInsert(int key, Object newValue);
    public boolean canUpdate(int key, Object newValue, Object oldValue);
    public boolean canRemove(int key, Object oldValue);
    public boolean canRetrieve(int key, Object oldValue);
}
