package com.cboe.util.collections;

/**
 * IntLongMapPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapPolicy.template (Int/int, Long/long)
 *
 */

public class IntLongMapPolicyImpl implements IntLongMapPolicy
{
    public boolean canInsert(int key, long newValue)                         {return true;}
    public boolean canUpdate(int key, long newValue, long oldValue)       {return true;}
    public boolean canRemove(int key, long oldValue)                         {return true;}
    public boolean canRetrieve(int key, long oldValue)                       {return true;}
}
