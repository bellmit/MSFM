package com.cboe.client.util.collections;

/**
 * ObjectInspectionPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 */

public class ObjectInspectionPolicy implements ObjectInspectionPolicyIF
{
    public boolean inspect(Object value)
    {
        return true;
    }
}

