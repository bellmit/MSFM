package com.cboe.util.collections;

/**
 * LongIntMapModifyPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapModifyPolicy.template (Long/long, Int/int)
 *
 */

public interface LongIntMapModifyPolicy extends LongIntMapPolicy
{
    public int valueToInsert(long key, int newValue);
    public int valueToUpdate(long key, int oldValue, int newValue);
}
