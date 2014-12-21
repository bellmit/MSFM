package com.cboe.util.collections;

/**
 * IntVisitor.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM TypedVisitor.template (Int/int)
 *
 */

public interface IntVisitor extends ControllableVisitor
{
    public int visit(int key);
    public IntVisitor beforeVisit(Object arg);
    public IntVisitor afterVisit(Object arg);
}
