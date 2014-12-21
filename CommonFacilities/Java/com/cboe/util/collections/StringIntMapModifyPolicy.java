package com.cboe.util.collections;

/**
 * StringIntMapModifyPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapModifyPolicy.template (String/String, Int/int)
 *
 */

public interface StringIntMapModifyPolicy extends StringIntMapPolicy
{
    public int valueToInsert(String key, int newValue);
    public int valueToUpdate(String key, int oldValue, int newValue);
}
