package com.cboe.util.collections;

/**
 * IntObjectMapPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapPolicy.template (Int/int, Object/Object)
 *
 */

public class IntObjectMapPolicyImpl implements IntObjectMapPolicy
{
    public boolean canInsert(int key, Object newValue)                         {return true;}
    public boolean canUpdate(int key, Object newValue, Object oldValue)       {return true;}
    public boolean canRemove(int key, Object oldValue)                         {return true;}
    public boolean canRetrieve(int key, Object oldValue)                       {return true;}
}
