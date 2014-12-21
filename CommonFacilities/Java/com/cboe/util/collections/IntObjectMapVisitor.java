package com.cboe.util.collections;

/**
 * IntObjectMapVisitor.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapVisitor.template (Int/int, Object/Object)
 *
 */

public interface IntObjectMapVisitor extends ControllableVisitor
{
    public int visit(int key, Object value);
    public IntObjectMapVisitor beforeVisit(Object arg);
    public IntObjectMapVisitor afterVisit(Object arg);
}
