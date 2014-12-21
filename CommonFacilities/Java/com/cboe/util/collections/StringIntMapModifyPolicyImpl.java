package com.cboe.util.collections;

/**
 * StringIntMapModifyPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapModifyPolicy.template (String/String, Int/int)
 *
 */

public class StringIntMapModifyPolicyImpl extends StringIntMapPolicyImpl implements StringIntMapModifyPolicy
{
    public int valueToInsert(String key, int newValue)                     {return newValue;}
    public int valueToUpdate(String key, int oldValue, int newValue)   {return newValue;}
}

