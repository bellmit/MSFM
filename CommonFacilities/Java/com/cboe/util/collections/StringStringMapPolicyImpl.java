package com.cboe.util.collections;

/**
 * StringStringMapPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapPolicy.template (String/String, String/String)
 *
 */

public class StringStringMapPolicyImpl implements StringStringMapPolicy
{
    public boolean canInsert(String key, String newValue)                         {return true;}
    public boolean canUpdate(String key, String newValue, String oldValue)       {return true;}
    public boolean canRemove(String key, String oldValue)                         {return true;}
    public boolean canRetrieve(String key, String oldValue)                       {return true;}
}
