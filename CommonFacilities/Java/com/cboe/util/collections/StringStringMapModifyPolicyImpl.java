package com.cboe.util.collections;

/**
 * StringStringMapModifyPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapModifyPolicy.template (String/String, String/String)
 *
 */

public class StringStringMapModifyPolicyImpl extends StringStringMapPolicyImpl implements StringStringMapModifyPolicy
{
    public String valueToInsert(String key, String newValue)                     {return newValue;}
    public String valueToUpdate(String key, String oldValue, String newValue)   {return newValue;}
}

