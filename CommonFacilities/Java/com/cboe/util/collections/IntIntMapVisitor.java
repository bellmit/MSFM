package com.cboe.util.collections;

/**
 * IntIntMapVisitor.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapVisitor.template (Int/int, Int/int)
 *
 */

public interface IntIntMapVisitor extends ControllableVisitor
{
    public int visit(int key, int value);
    public IntIntMapVisitor beforeVisit(Object arg);
    public IntIntMapVisitor afterVisit(Object arg);
}
