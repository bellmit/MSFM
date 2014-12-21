package com.cboe.util.collections;

/**
 * IntStringMapModifyPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapModifyPolicy.template (Int/int, String/String)
 *
 */

public class IntStringMapModifyPolicyImpl extends IntStringMapPolicyImpl implements IntStringMapModifyPolicy
{
    public String valueToInsert(int key, String newValue)                     {return newValue;}
    public String valueToUpdate(int key, String oldValue, String newValue)   {return newValue;}
}

