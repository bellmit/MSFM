package com.cboe.util.collections;

/**
 * StringObjectMapPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapPolicy.template (String/String, Object/Object)
 *
 */

public class StringObjectMapPolicyImpl implements StringObjectMapPolicy
{
    public boolean canInsert(String key, Object newValue)                         {return true;}
    public boolean canUpdate(String key, Object newValue, Object oldValue)       {return true;}
    public boolean canRemove(String key, Object oldValue)                         {return true;}
    public boolean canRetrieve(String key, Object oldValue)                       {return true;}
}
