package com.cboe.application.util;

public abstract class QuoteCallSnapshot
{
    public static void enter() { callSnapshotThreadLocal.getQuoteCallSnapshot().enter(); }
    public static void fixEnter(long fixStartTime) { callSnapshotThreadLocal.getQuoteCallSnapshot().fixEnter(fixStartTime); }
    public static void done() { callSnapshotThreadLocal.getQuoteCallSnapshot().done(); }
    public static void setSizeParams(int size, int acceptedCount) { callSnapshotThreadLocal.getQuoteCallSnapshot().setSizeParams(size, acceptedCount); }

    public static void startServerCall() { callSnapshotThreadLocal.getQuoteCallSnapshot().startServerCall(); }
    public static void endServerCall() { callSnapshotThreadLocal.getQuoteCallSnapshot().endServerCall(); }

    public static long enterTime() { return callSnapshotThreadLocal.getQuoteCallSnapshot().enterTime(); }
    public static long endTime() { return callSnapshotThreadLocal.getQuoteCallSnapshot().endTime(); }
    public static long serverEnterTime() { return callSnapshotThreadLocal.getQuoteCallSnapshot().serverEnterTime(); }
    public static long serverEndTime() { return callSnapshotThreadLocal.getQuoteCallSnapshot().serverEndTime(); }
    
    public static long elapsedTotalTime() { return callSnapshotThreadLocal.getQuoteCallSnapshot().elapsedTotalTime(); }
    public static long elapsedServerTime() { return callSnapshotThreadLocal.getQuoteCallSnapshot().elapsedServerTime(); }
    public static long elapsedCasTime() { return callSnapshotThreadLocal.getQuoteCallSnapshot().elapsedCasTime(); }
    public static long elapsedFixTime() { return callSnapshotThreadLocal.getQuoteCallSnapshot().elapsedFixTime(); }
    
    public static int elementSequenceSize() { return callSnapshotThreadLocal.getQuoteCallSnapshot().elementSequenceSize(); }
    public static int acceptedCount() { return callSnapshotThreadLocal.getQuoteCallSnapshot().acceptedCount(); }

    public static void classKeyLockWaitStart() { callSnapshotThreadLocal.getQuoteCallSnapshot().classKeyLockWaitStart(); }
    public static void classKeyLockWaitEnd() { callSnapshotThreadLocal.getQuoteCallSnapshot().classKeyLockWaitEnd(); }        
    public static void classKeyLockHoldEnd() { callSnapshotThreadLocal.getQuoteCallSnapshot().classKeyLockHoldEnd(); }        
    public static void quoteCacheLockWaitStart() { callSnapshotThreadLocal.getQuoteCallSnapshot().quoteCacheLockWaitStart(); }
    public static void quoteCacheLockWaitEnd() { callSnapshotThreadLocal.getQuoteCallSnapshot().quoteCacheLockWaitEnd(); }    
    public static void quoteCacheLockHoldEnd() { callSnapshotThreadLocal.getQuoteCallSnapshot().quoteCacheLockHoldEnd(); }    

    public static long classKeyLockWaitStartTime() { return callSnapshotThreadLocal.getQuoteCallSnapshot().classKeyLockWaitStartTime(); }
    public static long classKeyLockWaitEndTime() { return callSnapshotThreadLocal.getQuoteCallSnapshot().classKeyLockWaitEndTime(); }
    public static long classKeyLockHoldEndTime() { return callSnapshotThreadLocal.getQuoteCallSnapshot().classKeyLockHoldEndTime(); }
    public static long quoteCacheLockWaitStartTime() { return callSnapshotThreadLocal.getQuoteCallSnapshot().quoteCacheLockWaitStartTime(); }
    public static long quoteCacheLockWaitEndTime() { return callSnapshotThreadLocal.getQuoteCallSnapshot().quoteCacheLockWaitEndTime(); }
    public static long quoteCacheLockHoldEndTime() { return callSnapshotThreadLocal.getQuoteCallSnapshot().quoteCacheLockHoldEndTime(); }
    public static long elapsedClassKeyLockWaitTime() { return callSnapshotThreadLocal.getQuoteCallSnapshot().elapsedClassKeyLockWaitTime(); }
    public static long elapsedClassKeyLockHoldTime() { return callSnapshotThreadLocal.getQuoteCallSnapshot().elapsedClassKeyLockHoldTime(); }
    public static long elapsedQuoteCacheLockWaitTime() { return callSnapshotThreadLocal.getQuoteCallSnapshot().elapsedQuoteCacheLockWaitTime(); }
    public static long elapsedQuoteCacheLockHoldTime() { return callSnapshotThreadLocal.getQuoteCallSnapshot().elapsedQuoteCacheLockHoldTime(); }
    public static int getSemaphoresUsed() { return callSnapshotThreadLocal.getQuoteCallSnapshot().getSemaphoresUsed(); }
    public static void setSemaphoresUsed(int semUsed) { callSnapshotThreadLocal.getQuoteCallSnapshot().setSemaphoresUsed(semUsed); }
    
    private static QuoteCallSnapshotThreadLocal callSnapshotThreadLocal = new QuoteCallSnapshotThreadLocal();
    
    private static class QuoteCallSnapshotThreadLocal extends ThreadLocal
    {
        protected Object initialValue() { return new QuoteCallSnapshotImpl(); }
        private QuoteCallSnapshotImpl getQuoteCallSnapshot() { return (QuoteCallSnapshotImpl) get(); }
    }

    private static class QuoteCallSnapshotImpl
    {
        private long enterTime;
        private long endTime;
        private long totalTime;
        private long remoteEnterTime;
        private long remoteEndTime;
        private long remoteTime;
        private int sequenceSize;
        private int acceptedSequenceElementCount;
        private long classKeyLockWaitStartTime;
        private long classKeyLockWaitEndTime;
        private long classKeyLockHoldEndTime;
        private long quoteCacheLockWaitStartTime;
        private long quoteCacheLockWaitEndTime;
        private long quoteCacheLockHoldEndTime;
        private long elapsedClassKeyLockWaitTime;
        private long elapsedQuoteCacheLockWaitTime;
        private long elapsedClassKeyLockHoldTime;
        private long elapsedQuoteCacheLockHoldTime;
        private int semaphoreUsed;
        private long fixEnterTime = 0;
        
        private void initialize() {
			        endTime = 0;
			        totalTime = 0;
			        remoteEnterTime = 0;
			        remoteEndTime = 0;
			        remoteTime = 0;
			        sequenceSize = 0;
			        acceptedSequenceElementCount = 0;
			        classKeyLockWaitStartTime = 0;
			        classKeyLockWaitEndTime = 0;
			        classKeyLockHoldEndTime = 0;
			        quoteCacheLockWaitStartTime = 0;
			        quoteCacheLockWaitEndTime = 0;
			        quoteCacheLockHoldEndTime = 0;
			        elapsedClassKeyLockWaitTime = 0;
			        elapsedQuoteCacheLockWaitTime = 0;
			        elapsedClassKeyLockHoldTime = 0;
			        elapsedQuoteCacheLockHoldTime = 0;
			        semaphoreUsed=0;
			      }
        private void enter() { enterTime = System.nanoTime();
                               initialize();
                             }
        private void fixEnter(long fixStartTime) { fixEnterTime = fixStartTime;
        					enterTime = 0;
        					initialize();
					      }
        private void setSizeParams(int sequenceSize, int acceptedCount) {
                                                                 this.acceptedSequenceElementCount = acceptedCount;
                                                                 this.sequenceSize = sequenceSize;
                                                               }
        private void done() { endTime = System.nanoTime();
                              totalTime = fixEnterTime > 0 ? endTime - fixEnterTime : endTime - enterTime;
                              elapsedClassKeyLockWaitTime = classKeyLockWaitEndTime - classKeyLockWaitStartTime;
                              elapsedQuoteCacheLockWaitTime = quoteCacheLockWaitEndTime - quoteCacheLockWaitStartTime;
                              elapsedClassKeyLockHoldTime = classKeyLockHoldEndTime - classKeyLockWaitEndTime;
                              elapsedQuoteCacheLockHoldTime = quoteCacheLockHoldEndTime - quoteCacheLockWaitEndTime;
                            }

        private void startServerCall() { remoteEnterTime = System.nanoTime(); }
        private void endServerCall() { remoteEndTime = System.nanoTime();
                                       remoteTime = remoteEndTime - remoteEnterTime; }

        private long enterTime() { return enterTime; }
        private long endTime() { return endTime; }
        private long serverEnterTime() { return remoteEnterTime; }
        private long serverEndTime() { return remoteEndTime; }
        
        private int elementSequenceSize() { return sequenceSize; }
        private int acceptedCount() { return acceptedSequenceElementCount; }

        private long elapsedTotalTime() { return totalTime; }
        private long elapsedServerTime() { return remoteTime; }
        private long elapsedCasTime() { return totalTime - (remoteTime + elapsedFixTime()); }
        private long elapsedFixTime() { return fixEnterTime > 0 ? enterTime - fixEnterTime : 0 ;  }
        
        private long classKeyLockWaitStartTime() { return classKeyLockWaitStartTime; }
        private long classKeyLockWaitEndTime() { return classKeyLockWaitEndTime; }
        private long classKeyLockHoldEndTime() { return classKeyLockHoldEndTime; }
        private long quoteCacheLockWaitStartTime() { return quoteCacheLockWaitStartTime; }
        private long quoteCacheLockWaitEndTime() { return quoteCacheLockWaitEndTime; }
        private long quoteCacheLockHoldEndTime() { return quoteCacheLockHoldEndTime; }

        private long elapsedClassKeyLockWaitTime() { return elapsedClassKeyLockWaitTime; }
        private long elapsedQuoteCacheLockWaitTime() { return elapsedQuoteCacheLockWaitTime; }
        private long elapsedClassKeyLockHoldTime() { return elapsedClassKeyLockHoldTime; }
        private long elapsedQuoteCacheLockHoldTime() { return elapsedQuoteCacheLockHoldTime; }

        private void classKeyLockWaitStart() { classKeyLockWaitStartTime = System.nanoTime(); }
        private void classKeyLockWaitEnd() { classKeyLockWaitEndTime = System.nanoTime(); }
        private void classKeyLockHoldEnd() { classKeyLockHoldEndTime = System.nanoTime(); }
        private void quoteCacheLockWaitStart() { quoteCacheLockWaitStartTime = System.nanoTime(); }
        private void quoteCacheLockWaitEnd() { quoteCacheLockWaitEndTime = System.nanoTime(); }
        private void quoteCacheLockHoldEnd() { quoteCacheLockHoldEndTime = System.nanoTime(); }   
        
        private int getSemaphoresUsed() { return semaphoreUsed; }
        private void setSemaphoresUsed(int semUsed) { semaphoreUsed = semUsed; }
    }
}
