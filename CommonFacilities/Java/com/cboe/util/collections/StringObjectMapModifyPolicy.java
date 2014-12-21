package com.cboe.util.collections;

/**
 * StringObjectMapModifyPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapModifyPolicy.template (String/String, Object/Object)
 *
 */

public interface StringObjectMapModifyPolicy extends StringObjectMapPolicy
{
    public Object valueToInsert(String key, Object newValue);
    public Object valueToUpdate(String key, Object oldValue, Object newValue);
}
