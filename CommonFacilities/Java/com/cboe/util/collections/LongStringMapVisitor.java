package com.cboe.util.collections;

/**
 * LongStringMapVisitor.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapVisitor.template (Long/long, String/String)
 *
 */

public interface LongStringMapVisitor extends ControllableVisitor
{
    public int visit(long key, String value);
    public LongStringMapVisitor beforeVisit(Object arg);
    public LongStringMapVisitor afterVisit(Object arg);
}
