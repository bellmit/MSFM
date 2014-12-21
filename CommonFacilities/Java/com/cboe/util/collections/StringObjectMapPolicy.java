package com.cboe.util.collections;

/**
 * StringObjectMapPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapPolicy.template (String/String, Object/Object)
 *
 */

public interface StringObjectMapPolicy
{
    public boolean canInsert(String key, Object newValue);
    public boolean canUpdate(String key, Object newValue, Object oldValue);
    public boolean canRemove(String key, Object oldValue);
    public boolean canRetrieve(String key, Object oldValue);
}
