package com.cboe.util.collections;

/**
 * LongObjectMapVisitorImpl.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapVisitorImpl.template (Long/long, Object/Object)
 *
 */

public class LongObjectMapVisitorImpl implements LongObjectMapVisitor
{
    public LongObjectMapVisitor beforeVisit(Object arg)
    {
        return this;
    }

    public LongObjectMapVisitor afterVisit(Object arg)
    {
        return this;
    }

    public int visit(long key, Object value)
    {
        return ABORT;
    }
}

