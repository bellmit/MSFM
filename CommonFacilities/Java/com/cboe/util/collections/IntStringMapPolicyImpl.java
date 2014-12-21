package com.cboe.util.collections;

/**
 * IntStringMapPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapPolicy.template (Int/int, String/String)
 *
 */

public class IntStringMapPolicyImpl implements IntStringMapPolicy
{
    public boolean canInsert(int key, String newValue)                         {return true;}
    public boolean canUpdate(int key, String newValue, String oldValue)       {return true;}
    public boolean canRemove(int key, String oldValue)                         {return true;}
    public boolean canRetrieve(int key, String oldValue)                       {return true;}
}
