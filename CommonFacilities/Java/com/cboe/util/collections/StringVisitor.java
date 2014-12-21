package com.cboe.util.collections;

/**
 * StringVisitor.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM TypedVisitor.template (String/String)
 *
 */

public interface StringVisitor extends ControllableVisitor
{
    public int visit(String key);
    public StringVisitor beforeVisit(Object arg);
    public StringVisitor afterVisit(Object arg);
}
