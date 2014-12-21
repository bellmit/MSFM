package com.cboe.interfaces.domain;

/**
 * An interface to be used with the DependencyWatcher in impls...
 * 
 * @author Eric Fredericks
 */
public interface DependencyCondition
{
    boolean conditionMet();
}
