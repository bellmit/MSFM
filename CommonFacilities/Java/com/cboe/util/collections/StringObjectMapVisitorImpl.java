package com.cboe.util.collections;

/**
 * StringObjectMapVisitorImpl.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapVisitorImpl.template (String/String, Object/Object)
 *
 */

public class StringObjectMapVisitorImpl implements StringObjectMapVisitor
{
    public StringObjectMapVisitor beforeVisit(Object arg)
    {
        return this;
    }

    public StringObjectMapVisitor afterVisit(Object arg)
    {
        return this;
    }

    public int visit(String key, Object value)
    {
        return ABORT;
    }
}

