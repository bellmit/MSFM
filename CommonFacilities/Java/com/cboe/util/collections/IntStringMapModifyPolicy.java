package com.cboe.util.collections;

/**
 * IntStringMapModifyPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapModifyPolicy.template (Int/int, String/String)
 *
 */

public interface IntStringMapModifyPolicy extends IntStringMapPolicy
{
    public String valueToInsert(int key, String newValue);
    public String valueToUpdate(int key, String oldValue, String newValue);
}
