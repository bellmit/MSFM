package com.cboe.util.collections;

/**
 * LongIntMapPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapPolicy.template (Long/long, Int/int)
 *
 */

public class LongIntMapPolicyImpl implements LongIntMapPolicy
{
    public boolean canInsert(long key, int newValue)                         {return true;}
    public boolean canUpdate(long key, int newValue, int oldValue)       {return true;}
    public boolean canRemove(long key, int oldValue)                         {return true;}
    public boolean canRetrieve(long key, int oldValue)                       {return true;}
}
