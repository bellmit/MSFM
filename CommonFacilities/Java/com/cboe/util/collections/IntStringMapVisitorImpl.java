package com.cboe.util.collections;

/**
 * IntStringMapVisitorImpl.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapVisitorImpl.template (Int/int, String/String)
 *
 */

public class IntStringMapVisitorImpl implements IntStringMapVisitor
{
    public IntStringMapVisitor beforeVisit(Object arg)
    {
        return this;
    }

    public IntStringMapVisitor afterVisit(Object arg)
    {
        return this;
    }

    public int visit(int key, String value)
    {
        return ABORT;
    }
}

