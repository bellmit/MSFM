package com.cboe.util.collections;

/**
 * IntIntMapModifyPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapModifyPolicy.template (Int/int, Int/int)
 *
 */

public class IntIntMapModifyPolicyImpl extends IntIntMapPolicyImpl implements IntIntMapModifyPolicy
{
    public int valueToInsert(int key, int newValue)                     {return newValue;}
    public int valueToUpdate(int key, int oldValue, int newValue)   {return newValue;}
}

