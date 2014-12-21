package com.cboe.util.collections;

/**
 * StringLongMapModifyPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapModifyPolicy.template (String/String, Long/long)
 *
 */

public interface StringLongMapModifyPolicy extends StringLongMapPolicy
{
    public long valueToInsert(String key, long newValue);
    public long valueToUpdate(String key, long oldValue, long newValue);
}
