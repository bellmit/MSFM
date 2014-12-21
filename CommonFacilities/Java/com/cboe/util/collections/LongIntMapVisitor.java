package com.cboe.util.collections;

/**
 * LongIntMapVisitor.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapVisitor.template (Long/long, Int/int)
 *
 */

public interface LongIntMapVisitor extends ControllableVisitor
{
    public int visit(long key, int value);
    public LongIntMapVisitor beforeVisit(Object arg);
    public LongIntMapVisitor afterVisit(Object arg);
}
