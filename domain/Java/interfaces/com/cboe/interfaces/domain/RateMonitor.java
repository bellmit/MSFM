package com.cboe.interfaces.domain;

/**
 * Mechanism for limiting the number of times an operation is invoked in a
 * given time window. The canAccept() methods modify the RateMonitor object upon
 * a successful return.
 *
 * If an operation must check multiple RateMonitor objects, you should use the
 * previewCanAccept() methods to check RateMonitor objects without modifying
 * them; if all those checks succeed then call the canAccept() methods to
 * modify the RateMonitor objects.
 *
 * @author Connie Feng
 */

public interface RateMonitor {
    public boolean previewCanAccept(long currentTime);
    public boolean previewCanAccept(long currentTime, int blockSize);
    public boolean canAccept(long currentTime);
    public boolean canAccept(long currentTime, int blockSize);
    public int getWindowSize();
    public long getWindowMilliSecondPeriod();
    public boolean canAcceptBlock(int blockSize);
}
