/**
 * 
 */
package com.cboe.application.util;

/**
 * @author Gijo Joseph
 *
 */
public abstract class OrderCallSnapshot
{
    public static void enter() { callSnapshotThreadLocal.getOrderCallSnapshot().enter(); }
    public static void done() { callSnapshotThreadLocal.getOrderCallSnapshot().done(); }

    public static void startServerCall() { callSnapshotThreadLocal.getOrderCallSnapshot().startServerCall(); }
    public static void endServerCall() { callSnapshotThreadLocal.getOrderCallSnapshot().endServerCall(); }

    public static long enterTime() { return callSnapshotThreadLocal.getOrderCallSnapshot().enterTime(); }
    public static long endTime() { return callSnapshotThreadLocal.getOrderCallSnapshot().endTime(); }
    public static long serverEnterTime() { return callSnapshotThreadLocal.getOrderCallSnapshot().serverEnterTime(); }
    public static long serverEndTime() { return callSnapshotThreadLocal.getOrderCallSnapshot().serverEndTime(); }
    
    public static long elapsedServerTime() { return callSnapshotThreadLocal.getOrderCallSnapshot().elapsedServerTime();}
    public static long elapsedTotalTime() { return callSnapshotThreadLocal.getOrderCallSnapshot().elapsedTotalTime(); }
    public static long elapsedCasTime() { return callSnapshotThreadLocal.getOrderCallSnapshot().elapsedCasTime(); }
    public static long elapsedFixTime() { return callSnapshotThreadLocal.getOrderCallSnapshot().elapsedFixTime(); }
    public static long elapsedLightOrderFixTime() { return callSnapshotThreadLocal.getOrderCallSnapshot().elapsedLightOrderFixTime(); }
    
    public static void fixEnter(long fixStartTime) { callSnapshotThreadLocal.getOrderCallSnapshot().fixEnter(fixStartTime); }

    public static void setCasStartTime(long casStartTime) { callSnapshotThreadLocal.getOrderCallSnapshot().setCasStartTime(casStartTime); }
    public static void setCasEndTime(long casEndTime) { callSnapshotThreadLocal.getOrderCallSnapshot().setCasEndTime(casEndTime); }
    
    private static OrderCallSnapshotThreadLocal callSnapshotThreadLocal = new OrderCallSnapshotThreadLocal();
    
    private static class OrderCallSnapshotThreadLocal extends ThreadLocal
    {
        protected Object initialValue() { return new OrderCallSnapshotImpl(); }
        private OrderCallSnapshotImpl getOrderCallSnapshot() { return (OrderCallSnapshotImpl) get(); }
    }

    private static class OrderCallSnapshotImpl
    {
        private long fixEnterTime = 0;
        private long casEnterTime = 0;
        private long casLeaveTime = 0;
        private long enterTime;
        private long endTime;
        private long totalTime;
        private long remoteEnterTime;
        private long remoteEndTime;
        private long remoteTime;
        
        private void enter() { enterTime = System.nanoTime();
                               endTime = 0;
                               totalTime = 0;
                               remoteEnterTime = 0;
                               remoteEndTime = 0;
                               remoteTime = 0;
                             }
        
        private void fixEnter(long fixStartTime) { fixEnterTime = fixStartTime;
        	enterTime = 0; 
	        endTime = 0;
	        totalTime = 0;
	        remoteEnterTime = 0;
	        remoteEndTime = 0;
	        remoteTime = 0;
	    }
        private void setCasStartTime(long casStartTime) { casEnterTime = casStartTime; }
        private void setCasEndTime(long casEndTime) { casLeaveTime = casEndTime; }

        private void done() { endTime = System.nanoTime();
                              totalTime = fixEnterTime > 0 ? endTime - fixEnterTime : endTime - enterTime;
                            }

        private void startServerCall() { remoteEnterTime = System.nanoTime(); }
        private void endServerCall() { remoteEndTime = System.nanoTime();
                                       remoteTime = remoteEndTime - remoteEnterTime; }

        private long enterTime() { return fixEnterTime > 0 ? fixEnterTime : enterTime; }
        private long endTime() { return endTime; }
        private long serverEnterTime() { return remoteEnterTime; }
        private long serverEndTime() { return remoteEndTime; }
        
        private long elapsedTotalTime() { return totalTime; }
        private long elapsedCasTime() { return  (casLeaveTime > 0) ? (casLeaveTime - casEnterTime) - remoteTime : totalTime - (remoteTime + elapsedFixTime()); }
        private long elapsedFixTime() { return fixEnterTime > 0 ? enterTime - fixEnterTime : 0 ; }
        private long elapsedLightOrderFixTime() { return totalTime - remoteTime; }
        private long elapsedServerTime() { return remoteTime; }        
    }
}
