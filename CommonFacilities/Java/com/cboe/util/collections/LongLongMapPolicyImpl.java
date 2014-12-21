package com.cboe.util.collections;

/**
 * LongLongMapPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapPolicy.template (Long/long, Long/long)
 *
 */

public class LongLongMapPolicyImpl implements LongLongMapPolicy
{
    public boolean canInsert(long key, long newValue)                         {return true;}
    public boolean canUpdate(long key, long newValue, long oldValue)       {return true;}
    public boolean canRemove(long key, long oldValue)                         {return true;}
    public boolean canRetrieve(long key, long oldValue)                       {return true;}
}
