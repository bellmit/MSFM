package com.cboe.util.collections;

/**
 * IntVisitorImpl.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM TypedVisitorImpl.template (Int/int)
 *
 */

public class IntVisitorImpl implements IntVisitor
{
    public IntVisitor beforeVisit(Object arg)
    {
        return this;
    }

    public IntVisitor afterVisit(Object arg)
    {
        return this;
    }

    public int visit(int key)
    {
        return ABORT;
    }
}

