package com.cboe.util.collections;

/**
 * IntStringMapVisitor.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapVisitor.template (Int/int, String/String)
 *
 */

public interface IntStringMapVisitor extends ControllableVisitor
{
    public int visit(int key, String value);
    public IntStringMapVisitor beforeVisit(Object arg);
    public IntStringMapVisitor afterVisit(Object arg);
}
