package com.cboe.util.collections;

/**
 * StringObjectMapVisitor.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapVisitor.template (String/String, Object/Object)
 *
 */

public interface StringObjectMapVisitor extends ControllableVisitor
{
    public int visit(String key, Object value);
    public StringObjectMapVisitor beforeVisit(Object arg);
    public StringObjectMapVisitor afterVisit(Object arg);
}
