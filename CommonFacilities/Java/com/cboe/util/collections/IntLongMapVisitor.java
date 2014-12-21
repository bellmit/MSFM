package com.cboe.util.collections;

/**
 * IntLongMapVisitor.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapVisitor.template (Int/int, Long/long)
 *
 */

public interface IntLongMapVisitor extends ControllableVisitor
{
    public int visit(int key, long value);
    public IntLongMapVisitor beforeVisit(Object arg);
    public IntLongMapVisitor afterVisit(Object arg);
}
