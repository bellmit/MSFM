package com.cboe.client.util.queue;

import com.cboe.instrumentationService.factories.InstrumentorFactory;
import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertNull;
import org.junit.Test;      // annotation

/** Class used by DoublePriorityEventChannelTest. This is not intended as a
 * JUnit test class, but our build system insists on submitting it to JUnit.
 * Therefore, this class contains just enough JUnit content to run as a test
 * and not report failure. 
 */
public class QueueInstrumentorForTest implements QueueInstrumentor
{
    // Allow this JUnit 4 test to run under JUnit3
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(QueueInstrumentorForTest.class);
    }
    @Test public void aTestSoJUnitDoesNotComplain() { assertNull(key); }
    public QueueInstrumentorForTest() { userData = null; }

    Object userData;
    String name;
    byte[] key;
    boolean isPrivate;
    Object lockObject;
    InstrumentorFactory factory;

    public void setUserData(Object o) { userData = o; }
    public Object getUserData() { return userData; }
    public String getName() { return name; }
    public void setKey( byte[] key ) { this.key = key; }
    public byte[] getKey() { return this.key; }
    public void setPrivate( boolean newValue ) { isPrivate = newValue; }
    public boolean isPrivate() { return isPrivate; }
    public void setLockObject( Object lock ) { lockObject = lock; }
    public void setFactory( InstrumentorFactory factory )
        { this.factory = factory; }
    public InstrumentorFactory getFactory() { return factory; }
    public void rename( String newName ) { name = newName; }
    public String getToStringHeader(
            boolean showUserData, boolean showPrivateFlag )
        { return ""; }
    public String toString(
            boolean showUserData, boolean showPrivateFlag, String instrName)
        { return ""; }

    Object enqueueLockObject;
    Object dequeueLockObject;
    AtomicLong nEnqueued = new AtomicLong();
    AtomicLong nDequeued = new AtomicLong();
    AtomicLong nFlushed = new AtomicLong();
    AtomicLong nOverlaid = new AtomicLong();
    AtomicLong nFlips = new AtomicLong();
    AtomicLong flipVolume = new AtomicLong();
    AtomicLong nEnqueueWaits = new AtomicLong();
    AtomicLong nDequeueWaits = new AtomicLong();
    AtomicLong nDequeueTimeouts = new AtomicLong();
    AtomicLong highWaterMark = new AtomicLong();
    AtomicLong overallHwm = new AtomicLong();
    AtomicLong overallHwmTime = new AtomicLong();
    AtomicLong currentSize = new AtomicLong();
    AtomicInteger status = new AtomicInteger();

    public void setEnqueueLockObject( Object newLock )
        { enqueueLockObject = newLock; }
    public void setDequeueLockObject( Object newLock )
        { dequeueLockObject = newLock; }

    public void incEnqueued( long incAmount )
        { nEnqueued.addAndGet(incAmount); }
    public void setEnqueued( long newAmount )
        { nEnqueued.set(newAmount); }
    public long getEnqueued() { return nEnqueued.get(); }

    public void incDequeued( long incAmount )
        { nDequeued.addAndGet(incAmount); }
    public void setDequeued( long newAmount )
        { nDequeued.set(newAmount); }
    public long getDequeued() { return nDequeued.get(); }

    public void incFlushed( long incAmount )
        { nFlushed.addAndGet(incAmount); }
    public void setFlushed( long newAmount )
        { nFlushed.set(newAmount); }
    public long getFlushed() { return nFlushed.get(); }

    public void incOverlaid( long incAmount )
        { nOverlaid.addAndGet(incAmount); }
    public void setOverlaid( long newAmount )
        { nOverlaid.set(newAmount); }
    public long getOverlaid() { return nOverlaid.get(); }

    public void incFlips( long incAmount )
        { nFlips.addAndGet(incAmount); }
    public void setFlips( long newAmount )
        { nFlips.set(newAmount); }
    public long getFlips() { return nFlips.get(); }

    public void incFlipVolume( long incAmount )
        { flipVolume.addAndGet(incAmount); }
    public void setFlipVolume( long newAmount )
        { flipVolume.set(newAmount); }
    public long getFlipVolume() { return flipVolume.get(); }

    public void incEnqueueWaits( long incAmount )
        { nEnqueueWaits.addAndGet(incAmount); }
    public void setEnqueueWaits( long newAmount )
        { nEnqueueWaits.set(newAmount); }
    public long getEnqueueWaits() { return nEnqueueWaits.get(); }

    public void incDequeueWaits( long incAmount )
        { nDequeueWaits.addAndGet(incAmount); }
    public void setDequeueWaits( long newAmount )
        { nDequeueWaits.set(newAmount); }
    public long getDequeueWaits() { return nDequeueWaits.get(); }

    public void incDequeueTimeouts( long incAmount )
        { nDequeueTimeouts.addAndGet(incAmount); }
    public void setDequeueTimeouts( long newAmount )
        { nDequeueTimeouts.set(newAmount); }
    public long getDequeueTimeouts() { return nDequeueTimeouts.get(); }

    public void setHighWaterMark( long newSize )
        { highWaterMark.set(newSize); }
    public long getHighWaterMark() { return highWaterMark.get(); }

    public void setOverallHighWaterMark( long newSize )
        { overallHwm.set(newSize); }
    public void setOverallHighWaterMarkTime( long newTime )
        { overallHwmTime.set(newTime); }
    public long getOverallHighWaterMark() { return overallHwm.get(); }
    public long getOverallHighWaterMarkTime()
        { return overallHwmTime.get(); }

    public long getHighWaterMarkAndReset()
        { return highWaterMark.getAndSet(0L); }

    public void setCurrentSize( long newSize )
        { currentSize.set(newSize); }
    public long getCurrentSize() { return currentSize.get(); }

    public void setStatus( short newStatus ) { status.set(newStatus); }
    public short getStatus() { return (short) status.get(); }

    public void get( QueueInstrumentor qi )
    {
        if (qi == null) return;

        qi.setEnqueued( nEnqueued.get() );
        qi.setDequeued( nDequeued.get() );
        qi.setFlushed( nFlushed.get() );
        qi.setOverlaid( nOverlaid.get() );
        qi.setHighWaterMark( highWaterMark.get() );
        qi.setCurrentSize( currentSize.get() );
        qi.setOverallHighWaterMark( overallHwm.get() );
        qi.setUserData( userData );
        qi.setStatus( (short)status.get() );
        qi.setEnqueueWaits( nEnqueueWaits.get() );
        qi.setDequeueWaits( nDequeueWaits.get() );
        qi.setDequeueTimeouts( nDequeueTimeouts.get() );
    }

    public String toString( boolean showUserData, boolean showPrivateFlag,
                            String instrName, Object hwmKeeper )
    { return ""; }
}
