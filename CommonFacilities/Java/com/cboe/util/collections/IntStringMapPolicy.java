package com.cboe.util.collections;

/**
 * IntStringMapPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapPolicy.template (Int/int, String/String)
 *
 */

public interface IntStringMapPolicy
{
    public boolean canInsert(int key, String newValue);
    public boolean canUpdate(int key, String newValue, String oldValue);
    public boolean canRemove(int key, String oldValue);
    public boolean canRetrieve(int key, String oldValue);
}
