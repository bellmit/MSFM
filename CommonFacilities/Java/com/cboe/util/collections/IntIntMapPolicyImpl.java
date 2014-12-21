package com.cboe.util.collections;

/**
 * IntIntMapPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapPolicy.template (Int/int, Int/int)
 *
 */

public class IntIntMapPolicyImpl implements IntIntMapPolicy
{
    public boolean canInsert(int key, int newValue)                         {return true;}
    public boolean canUpdate(int key, int newValue, int oldValue)       {return true;}
    public boolean canRemove(int key, int oldValue)                         {return true;}
    public boolean canRetrieve(int key, int oldValue)                       {return true;}
}
