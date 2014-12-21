package com.cboe.util.collections;

/**
 * StringLongMapModifyPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapModifyPolicy.template (String/String, Long/long)
 *
 */

public class StringLongMapModifyPolicyImpl extends StringLongMapPolicyImpl implements StringLongMapModifyPolicy
{
    public long valueToInsert(String key, long newValue)                     {return newValue;}
    public long valueToUpdate(String key, long oldValue, long newValue)   {return newValue;}
}

