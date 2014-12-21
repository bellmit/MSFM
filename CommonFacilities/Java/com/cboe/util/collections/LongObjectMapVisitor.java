package com.cboe.util.collections;

/**
 * LongObjectMapVisitor.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapVisitor.template (Long/long, Object/Object)
 *
 */

public interface LongObjectMapVisitor extends ControllableVisitor
{
    public int visit(long key, Object value);
    public LongObjectMapVisitor beforeVisit(Object arg);
    public LongObjectMapVisitor afterVisit(Object arg);
}
