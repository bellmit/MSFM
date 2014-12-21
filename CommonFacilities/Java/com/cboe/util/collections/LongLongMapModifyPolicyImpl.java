package com.cboe.util.collections;

/**
 * LongLongMapModifyPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapModifyPolicy.template (Long/long, Long/long)
 *
 */

public class LongLongMapModifyPolicyImpl extends LongLongMapPolicyImpl implements LongLongMapModifyPolicy
{
    public long valueToInsert(long key, long newValue)                     {return newValue;}
    public long valueToUpdate(long key, long oldValue, long newValue)   {return newValue;}
}

