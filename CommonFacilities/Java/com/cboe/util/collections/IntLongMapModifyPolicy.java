package com.cboe.util.collections;

/**
 * IntLongMapModifyPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapModifyPolicy.template (Int/int, Long/long)
 *
 */

public interface IntLongMapModifyPolicy extends IntLongMapPolicy
{
    public long valueToInsert(int key, long newValue);
    public long valueToUpdate(int key, long oldValue, long newValue);
}
