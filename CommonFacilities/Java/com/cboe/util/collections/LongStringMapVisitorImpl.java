package com.cboe.util.collections;

/**
 * LongStringMapVisitorImpl.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapVisitorImpl.template (Long/long, String/String)
 *
 */

public class LongStringMapVisitorImpl implements LongStringMapVisitor
{
    public LongStringMapVisitor beforeVisit(Object arg)
    {
        return this;
    }

    public LongStringMapVisitor afterVisit(Object arg)
    {
        return this;
    }

    public int visit(long key, String value)
    {
        return ABORT;
    }
}

