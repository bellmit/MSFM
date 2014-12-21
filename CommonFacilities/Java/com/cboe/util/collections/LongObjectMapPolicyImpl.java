package com.cboe.util.collections;

/**
 * LongObjectMapPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapPolicy.template (Long/long, Object/Object)
 *
 */

public class LongObjectMapPolicyImpl implements LongObjectMapPolicy
{
    public boolean canInsert(long key, Object newValue)                         {return true;}
    public boolean canUpdate(long key, Object newValue, Object oldValue)       {return true;}
    public boolean canRemove(long key, Object oldValue)                         {return true;}
    public boolean canRetrieve(long key, Object oldValue)                       {return true;}
}
