package com.cboe.util.collections;

/**
 * LongStringMapModifyPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapModifyPolicy.template (Long/long, String/String)
 *
 */

public interface LongStringMapModifyPolicy extends LongStringMapPolicy
{
    public String valueToInsert(long key, String newValue);
    public String valueToUpdate(long key, String oldValue, String newValue);
}
