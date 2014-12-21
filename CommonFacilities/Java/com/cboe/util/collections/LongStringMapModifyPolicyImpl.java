package com.cboe.util.collections;

/**
 * LongStringMapModifyPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapModifyPolicy.template (Long/long, String/String)
 *
 */

public class LongStringMapModifyPolicyImpl extends LongStringMapPolicyImpl implements LongStringMapModifyPolicy
{
    public String valueToInsert(long key, String newValue)                     {return newValue;}
    public String valueToUpdate(long key, String oldValue, String newValue)   {return newValue;}
}

