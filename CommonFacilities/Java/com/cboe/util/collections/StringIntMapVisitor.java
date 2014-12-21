package com.cboe.util.collections;

/**
 * StringIntMapVisitor.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapVisitor.template (String/String, Int/int)
 *
 */

public interface StringIntMapVisitor extends ControllableVisitor
{
    public int visit(String key, int value);
    public StringIntMapVisitor beforeVisit(Object arg);
    public StringIntMapVisitor afterVisit(Object arg);
}
