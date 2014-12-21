package com.cboe.util.collections;

/**
 * StringStringMapVisitor.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapVisitor.template (String/String, String/String)
 *
 */

public interface StringStringMapVisitor extends ControllableVisitor
{
    public int visit(String key, String value);
    public StringStringMapVisitor beforeVisit(Object arg);
    public StringStringMapVisitor afterVisit(Object arg);
}
