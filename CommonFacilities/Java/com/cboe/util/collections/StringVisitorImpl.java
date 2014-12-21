package com.cboe.util.collections;

/**
 * StringVisitorImpl.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM TypedVisitorImpl.template (String/String)
 *
 */

public class StringVisitorImpl implements StringVisitor
{
    public StringVisitor beforeVisit(Object arg)
    {
        return this;
    }

    public StringVisitor afterVisit(Object arg)
    {
        return this;
    }

    public int visit(String key)
    {
        return ABORT;
    }
}

