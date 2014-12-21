package com.cboe.util.collections;

/**
 * StringStringMapVisitorImpl.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapVisitorImpl.template (String/String, String/String)
 *
 */

public class StringStringMapVisitorImpl implements StringStringMapVisitor
{
    public StringStringMapVisitor beforeVisit(Object arg)
    {
        return this;
    }

    public StringStringMapVisitor afterVisit(Object arg)
    {
        return this;
    }

    public int visit(String key, String value)
    {
        return ABORT;
    }
}

