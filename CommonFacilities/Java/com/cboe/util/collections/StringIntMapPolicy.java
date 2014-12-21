package com.cboe.util.collections;

/**
 * StringIntMapPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapPolicy.template (String/String, Int/int)
 *
 */

public interface StringIntMapPolicy
{
    public boolean canInsert(String key, int newValue);
    public boolean canUpdate(String key, int newValue, int oldValue);
    public boolean canRemove(String key, int oldValue);
    public boolean canRetrieve(String key, int oldValue);
}
