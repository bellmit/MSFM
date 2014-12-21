package com.cboe.instrumentationService.instrumentors;

/**
 * QueueInstrumentor.java
 *
 *
 * Created: Wed Jul 23 15:55:10 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface QueueInstrumentor extends Instrumentor {
	
	public static final String INSTRUMENTOR_TYPE_NAME = "QueueInstrumentor";

	public void setEnqueueLockObject( Object newLock );
	public void setDequeueLockObject( Object newLock );

	public void incEnqueued( long incAmount );
	public void setEnqueued( long newAmount );
	public long getEnqueued();

	public void incDequeued( long incAmount );
	public void setDequeued( long newAmount );
	public long getDequeued();

	public void incFlushed( long incAmount );
	public void setFlushed( long newAmount );
	public long getFlushed();

	public void incOverlaid( long incAmount );
	public void setOverlaid( long newAmount );
	public long getOverlaid();

	public void incFlips( long incAmount );
	public void setFlips( long newAmount );
	public long getFlips();

	public void incFlipVolume( long incAmount );
	public void setFlipVolume( long newAmount );
	public long getFlipVolume();

	public void incEnqueueWaits( long incAmount );
	public void setEnqueueWaits( long newAmount );
	public long getEnqueueWaits();

	public void incDequeueWaits( long incAmount );
	public void setDequeueWaits( long newAmount );
	public long getDequeueWaits();

	public void incDequeueTimeouts( long incAmount );
	public void setDequeueTimeouts( long newAmount );
	public long getDequeueTimeouts();

	public void setHighWaterMark( long newSize );
	public long getHighWaterMark();

	public void setOverallHighWaterMark( long newSize );
	public void setOverallHighWaterMarkTime( long newTime );
	public long getOverallHighWaterMark();
	public long getOverallHighWaterMarkTime();

	public long getHighWaterMarkAndReset( );

	public void setCurrentSize( long newSize );
	public long getCurrentSize();

	public void setStatus( short newStatus );
	public short getStatus();

	public void get( QueueInstrumentor qi );

	public String toString( boolean showUserData, boolean showPrivateFlag, String instrName, Object hwmKeeper );

} // QueueInstrumentor
