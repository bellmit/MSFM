package com.cboe.util.collections;

/**
 * StringLongMapPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapPolicy.template (String/String, Long/long)
 *
 */

public class StringLongMapPolicyImpl implements StringLongMapPolicy
{
    public boolean canInsert(String key, long newValue)                         {return true;}
    public boolean canUpdate(String key, long newValue, long oldValue)       {return true;}
    public boolean canRemove(String key, long oldValue)                         {return true;}
    public boolean canRetrieve(String key, long oldValue)                       {return true;}
}
