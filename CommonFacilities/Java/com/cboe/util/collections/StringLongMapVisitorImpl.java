package com.cboe.util.collections;

/**
 * StringLongMapVisitorImpl.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapVisitorImpl.template (String/String, Long/long)
 *
 */

public class StringLongMapVisitorImpl implements StringLongMapVisitor
{
    public StringLongMapVisitor beforeVisit(Object arg)
    {
        return this;
    }

    public StringLongMapVisitor afterVisit(Object arg)
    {
        return this;
    }

    public int visit(String key, long value)
    {
        return ABORT;
    }
}

