package com.cboe.util;

/**
 * This is the callback interface used by the Timer service to
 * provide the callback after the time period has expired.
 *
 * Any object that needs to use the timer service needs to implement
 * this interface.
 *
 * @Author		: Ravi Vazirani
 */
public interface Timer {
	public void dequeue(int type, Object context);
}
