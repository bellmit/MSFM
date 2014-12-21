package com.cboe.util.collections;

/**
 * IntIntMapVisitorImpl.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapVisitorImpl.template (Int/int, Int/int)
 *
 */

public class IntIntMapVisitorImpl implements IntIntMapVisitor
{
    public IntIntMapVisitor beforeVisit(Object arg)
    {
        return this;
    }

    public IntIntMapVisitor afterVisit(Object arg)
    {
        return this;
    }

    public int visit(int key, int value)
    {
        return ABORT;
    }
}

