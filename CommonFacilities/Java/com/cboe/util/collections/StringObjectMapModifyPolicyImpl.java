package com.cboe.util.collections;

/**
 * StringObjectMapModifyPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapModifyPolicy.template (String/String, Object/Object)
 *
 */

public class StringObjectMapModifyPolicyImpl extends StringObjectMapPolicyImpl implements StringObjectMapModifyPolicy
{
    public Object valueToInsert(String key, Object newValue)                     {return newValue;}
    public Object valueToUpdate(String key, Object oldValue, Object newValue)   {return newValue;}
}

