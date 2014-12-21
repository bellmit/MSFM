package com.cboe.util.collections;

/**
 * IntIntMapModifyPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapModifyPolicy.template (Int/int, Int/int)
 *
 */

public interface IntIntMapModifyPolicy extends IntIntMapPolicy
{
    public int valueToInsert(int key, int newValue);
    public int valueToUpdate(int key, int oldValue, int newValue);
}
