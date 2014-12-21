package com.cboe.util.collections;

/**
 * LongStringMapPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapPolicy.template (Long/long, String/String)
 *
 */

public class LongStringMapPolicyImpl implements LongStringMapPolicy
{
    public boolean canInsert(long key, String newValue)                         {return true;}
    public boolean canUpdate(long key, String newValue, String oldValue)       {return true;}
    public boolean canRemove(long key, String oldValue)                         {return true;}
    public boolean canRetrieve(long key, String oldValue)                       {return true;}
}
