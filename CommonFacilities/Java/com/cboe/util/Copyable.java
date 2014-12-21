package com.cboe.util;

/**
 * This interface allows objects to be copied
 * arbitrarily deep.
 *
 * @version 0.31
 * @author David Wegener
 */
public interface Copyable {

/**
 * This method must be implemented to perform the deep
 * copy of an object
 * @return java.lang.Object
 * @author David Wegener
 */
public Object copy();

public long getAcquiringThreadId();

public void setAcquiringThreadId(long acquiringThreadId);

public void clear();

}
