package com.cboe.util.collections;

/**
 * IntObjectMapModifyPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapModifyPolicy.template (Int/int, Object/Object)
 *
 */

public interface IntObjectMapModifyPolicy extends IntObjectMapPolicy
{
    public Object valueToInsert(int key, Object newValue);
    public Object valueToUpdate(int key, Object oldValue, Object newValue);
}
