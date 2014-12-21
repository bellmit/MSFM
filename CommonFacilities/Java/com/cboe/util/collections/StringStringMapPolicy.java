package com.cboe.util.collections;

/**
 * StringStringMapPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapPolicy.template (String/String, String/String)
 *
 */

public interface StringStringMapPolicy
{
    public boolean canInsert(String key, String newValue);
    public boolean canUpdate(String key, String newValue, String oldValue);
    public boolean canRemove(String key, String oldValue);
    public boolean canRetrieve(String key, String oldValue);
}
