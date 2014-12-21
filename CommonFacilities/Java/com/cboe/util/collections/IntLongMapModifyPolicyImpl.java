package com.cboe.util.collections;

/**
 * IntLongMapModifyPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapModifyPolicy.template (Int/int, Long/long)
 *
 */

public class IntLongMapModifyPolicyImpl extends IntLongMapPolicyImpl implements IntLongMapModifyPolicy
{
    public long valueToInsert(int key, long newValue)                     {return newValue;}
    public long valueToUpdate(int key, long oldValue, long newValue)   {return newValue;}
}

