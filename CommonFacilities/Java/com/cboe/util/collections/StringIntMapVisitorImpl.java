package com.cboe.util.collections;

/**
 * StringIntMapVisitorImpl.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapVisitorImpl.template (String/String, Int/int)
 *
 */

public class StringIntMapVisitorImpl implements StringIntMapVisitor
{
    public StringIntMapVisitor beforeVisit(Object arg)
    {
        return this;
    }

    public StringIntMapVisitor afterVisit(Object arg)
    {
        return this;
    }

    public int visit(String key, int value)
    {
        return ABORT;
    }
}

