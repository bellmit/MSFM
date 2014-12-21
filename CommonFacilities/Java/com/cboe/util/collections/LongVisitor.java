package com.cboe.util.collections;

/**
 * LongVisitor.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM TypedVisitor.template (Long/long)
 *
 */

public interface LongVisitor extends ControllableVisitor
{
    public int visit(long key);
    public LongVisitor beforeVisit(Object arg);
    public LongVisitor afterVisit(Object arg);
}
