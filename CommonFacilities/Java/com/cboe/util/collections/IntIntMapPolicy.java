package com.cboe.util.collections;

/**
 * IntIntMapPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapPolicy.template (Int/int, Int/int)
 *
 */

public interface IntIntMapPolicy
{
    public boolean canInsert(int key, int newValue);
    public boolean canUpdate(int key, int newValue, int oldValue);
    public boolean canRemove(int key, int oldValue);
    public boolean canRetrieve(int key, int oldValue);
}
