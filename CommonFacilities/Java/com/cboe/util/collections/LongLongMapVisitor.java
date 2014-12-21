package com.cboe.util.collections;

/**
 * LongLongMapVisitor.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapVisitor.template (Long/long, Long/long)
 *
 */

public interface LongLongMapVisitor extends ControllableVisitor
{
    public int visit(long key, long value);
    public LongLongMapVisitor beforeVisit(Object arg);
    public LongLongMapVisitor afterVisit(Object arg);
}
