package com.cboe.util;

/**
 *
 * @version 1.1
 * @author David Wegener
 */
public class TransactionalThreadPool extends ThreadPool {
/**
 * TransactionalThreadPool constructor comment.
 * @param threadCount int
 * @param name java.lang.String
 */
public TransactionalThreadPool(int threadCount, String name) {
	super(threadCount, name);
}
}
