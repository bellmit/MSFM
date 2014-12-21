package com.cboe.util.collections;

/**
 * LongIntMapVisitorImpl.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapVisitorImpl.template (Long/long, Int/int)
 *
 */

public class LongIntMapVisitorImpl implements LongIntMapVisitor
{
    public LongIntMapVisitor beforeVisit(Object arg)
    {
        return this;
    }

    public LongIntMapVisitor afterVisit(Object arg)
    {
        return this;
    }

    public int visit(long key, int value)
    {
        return ABORT;
    }
}

