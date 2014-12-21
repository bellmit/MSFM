package com.cboe.util.collections;

/**
 * IntObjectMapModifyPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapModifyPolicy.template (Int/int, Object/Object)
 *
 */

public class IntObjectMapModifyPolicyImpl extends IntObjectMapPolicyImpl implements IntObjectMapModifyPolicy
{
    public Object valueToInsert(int key, Object newValue)                     {return newValue;}
    public Object valueToUpdate(int key, Object oldValue, Object newValue)   {return newValue;}
}

