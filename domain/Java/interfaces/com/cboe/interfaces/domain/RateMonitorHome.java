
package com.cboe.interfaces.domain;

/**
 * RateMonitorHome interface
 * All rate monitors are found using this interface.
 *
 * NOTE : Rate monitors should not be cached - this method MUST
 * be called every time to find the correct rate monitor.
 * Author: Connie Feng
 */

public interface RateMonitorHome {
	public final static String HOME_NAME = "RateMonitorHome";
    /**
     *  Find a rate monitor given a generic key
     *
     *  @param key Object
     *  @return com.cboe.domain.RateMonitor
     */
    public RateMonitor find(Object key, int windowSize, long windowMilliSecondPeriod);
}
