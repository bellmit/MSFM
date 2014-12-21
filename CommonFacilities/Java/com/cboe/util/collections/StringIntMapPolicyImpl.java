package com.cboe.util.collections;

/**
 * StringIntMapPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapPolicy.template (String/String, Int/int)
 *
 */

public class StringIntMapPolicyImpl implements StringIntMapPolicy
{
    public boolean canInsert(String key, int newValue)                         {return true;}
    public boolean canUpdate(String key, int newValue, int oldValue)       {return true;}
    public boolean canRemove(String key, int oldValue)                         {return true;}
    public boolean canRetrieve(String key, int oldValue)                       {return true;}
}
