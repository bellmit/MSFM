package com.cboe.util.collections;

/**
 * StringLongMapPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapPolicy.template (String/String, Long/long)
 *
 */

public interface StringLongMapPolicy
{
    public boolean canInsert(String key, long newValue);
    public boolean canUpdate(String key, long newValue, long oldValue);
    public boolean canRemove(String key, long oldValue);
    public boolean canRetrieve(String key, long oldValue);
}
