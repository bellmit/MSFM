package com.cboe.util.collections;

/**
 * LongObjectMapModifyPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapModifyPolicy.template (Long/long, Object/Object)
 *
 */

public class LongObjectMapModifyPolicyImpl extends LongObjectMapPolicyImpl implements LongObjectMapModifyPolicy
{
    public Object valueToInsert(long key, Object newValue)                     {return newValue;}
    public Object valueToUpdate(long key, Object oldValue, Object newValue)   {return newValue;}
}

