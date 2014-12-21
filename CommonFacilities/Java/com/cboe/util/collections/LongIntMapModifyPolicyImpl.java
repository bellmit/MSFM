package com.cboe.util.collections;

/**
 * LongIntMapModifyPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapModifyPolicy.template (Long/long, Int/int)
 *
 */

public class LongIntMapModifyPolicyImpl extends LongIntMapPolicyImpl implements LongIntMapModifyPolicy
{
    public int valueToInsert(long key, int newValue)                     {return newValue;}
    public int valueToUpdate(long key, int oldValue, int newValue)   {return newValue;}
}

