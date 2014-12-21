package com.cboe.util.collections;

/**
 * StringLongMapVisitor.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMapVisitor.template (String/String, Long/long)
 *
 */

public interface StringLongMapVisitor extends ControllableVisitor
{
    public int visit(String key, long value);
    public StringLongMapVisitor beforeVisit(Object arg);
    public StringLongMapVisitor afterVisit(Object arg);
}
