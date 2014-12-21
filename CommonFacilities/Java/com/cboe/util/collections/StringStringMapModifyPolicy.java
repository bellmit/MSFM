package com.cboe.util.collections;

/**
 * StringStringMapModifyPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapModifyPolicy.template (String/String, String/String)
 *
 */

public interface StringStringMapModifyPolicy extends StringStringMapPolicy
{
    public String valueToInsert(String key, String newValue);
    public String valueToUpdate(String key, String oldValue, String newValue);
}
