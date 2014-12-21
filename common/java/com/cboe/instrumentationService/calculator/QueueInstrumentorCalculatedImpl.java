package com.cboe.instrumentationService.calculator;

import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedQueueInstrumentor;
import com.cboe.instrumentationService.factories.InstrumentorFactory;

/**
 * QueueInstrumentorCalculatedImpl.java
 *
 *
 * Created: Thu Sep 18 08:31:25 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class QueueInstrumentorCalculatedImpl implements QueueInstrumentor, CalculatedQueueInstrumentor {

	private QueueInstrumentor raw;
	private Object lock;
	private long numSamples = 0;
	private long lastSampleTime;
	// These are set before every time new values are given to raw via interface set methods,
	// if this is the best sample so far.
	private long curPeakEnqueued = 0;
	private long curPeakDequeued = 0;
	private long curPeakFlushed = 0;
	private long curPeakOverlaid = 0;
	private long curPeakFlips = 0;
	private long curPeakFlipVolume = 0;
	private long curPeakEnqueueWaits = 0;
	private long curPeakDequeueWaits = 0;
	private long curPeakDequeueTimeouts = 0;
	// These are set at the beginning of a stats interval.
	private long curIntervalEnqueued = 0;
	private long curIntervalDequeued = 0;
	private long curIntervalFlushed = 0;
	private long curIntervalOverlaid = 0;
	private long curIntervalFlips = 0;
	private long curIntervalFlipVolume = 0;
	private long curIntervalEnqueueWaits = 0;
	private long curIntervalDequeueWaits = 0;
	private long curIntervalDequeueTimeouts = 0;
	private long intervalTimeMillis = 0;

	// Calculated values.
	private long peakEnqueued = 0;
	private long peakDequeued = 0;
	private long peakFlushed = 0;
	private long peakOverlaid = 0;
	private long peakFlips = 0;
	private long peakFlipVolume = 0;
	private long peakEnqueueWaits = 0;
	private long peakDequeueWaits = 0;
	private long peakDequeueTimeouts = 0;
	private long intervalEnqueued = 0;
	private long intervalDequeued = 0;
	private long intervalFlushed = 0;
	private long intervalOverlaid = 0;
	private long intervalFlips = 0;
	private long intervalFlipVolume = 0;
	private long intervalEnqueueWaits = 0;
	private long intervalDequeueWaits = 0;
	private long intervalDequeueTimeouts = 0;
	private double peakEnqueuedRate = 0.0;
	private double peakDequeuedRate = 0.0;
	private double peakFlushedRate = 0.0;
	private double peakOverlaidRate = 0.0;
	private double peakFlipsRate = 0.0;
	private double peakFlipVolumeRate = 0.0;
	private double peakEnqueueWaitsRate = 0.0;
	private double peakDequeueWaitsRate = 0.0;
	private double peakDequeueTimeoutsRate = 0.0;
	private double avgEnqueuedRate = 0.0;
	private double avgDequeuedRate = 0.0;
	private double avgFlushedRate = 0.0;
	private double avgOverlaidRate = 0.0;
	private double avgFlipsRate = 0.0;
	private double avgFlipVolumeRate = 0.0;
	private double avgEnqueueWaitsRate = 0.0;
	private double avgDequeueWaitsRate = 0.0;
	private double avgDequeueTimeoutsRate = 0.0;
	private InstrumentorFactory factory = null;

	public QueueInstrumentorCalculatedImpl( QueueInstrumentor rawInst ) {
		raw = rawInst;
		lock = this;
		lastSampleTime = System.currentTimeMillis();
	} // QueueInstrumentorCalculatedImpl constructor

	public void setLockObject( Object newLockObject ) {
		lock = newLockObject;
		raw.setLockObject( lock );
	}

	public void setEnqueueLockObject( Object newLockObject ) {
		// Don't really need enqLock here, as this is on calc side.
		raw.setEnqueueLockObject( lock );
	}

	public void setDequeueLockObject( Object newLockObject ) {
		// Don't really need deqLock here, as this is on calc side.
		raw.setDequeueLockObject( lock );
	}

	public void setFactory( InstrumentorFactory factory ) {
		this.factory = factory;
	}

	public InstrumentorFactory getFactory() {
		return factory;
	}

	public void sumIntervalTime( long timestamp ) {
		intervalTimeMillis += (timestamp - lastSampleTime);
		lastSampleTime = timestamp;
	}

	public void calculate( short calcToSampleFactor ) {
		synchronized( lock ) {
			peakEnqueued = curPeakEnqueued;
			peakDequeued = curPeakDequeued;
			peakFlushed = curPeakFlushed;
			peakOverlaid = curPeakOverlaid;
			peakFlips = curPeakFlips;
			peakFlipVolume = curPeakFlipVolume;
			peakEnqueueWaits = curPeakEnqueueWaits;
			peakDequeueWaits = curPeakDequeueWaits;
			peakDequeueTimeouts = curPeakDequeueTimeouts;
			intervalEnqueued = raw.getEnqueued() - curIntervalEnqueued;
			if ( intervalEnqueued < 0 ) {
				intervalEnqueued = 0;
			}
			intervalDequeued = raw.getDequeued() - curIntervalDequeued;
			if ( intervalDequeued < 0 ) {
				intervalDequeued = 0;
			}
			intervalFlushed = raw.getFlushed() - curIntervalFlushed;
			if ( intervalFlushed < 0 ) {
				intervalFlushed = 0;
			}
			intervalOverlaid = raw.getOverlaid() - curIntervalOverlaid;
			if ( intervalOverlaid < 0 ) {
				intervalOverlaid = 0;
			}
			intervalFlips = raw.getFlips() - curIntervalFlips;
			if ( intervalFlips < 0 ) {
				intervalFlips = 0;
			}
			intervalFlipVolume = raw.getFlipVolume() - curIntervalFlipVolume;
			if ( intervalFlipVolume < 0 ) {
				intervalFlipVolume = 0;
			}
			intervalEnqueueWaits = raw.getEnqueueWaits() - curIntervalEnqueueWaits;
			if ( intervalEnqueueWaits < 0 ) {
				intervalEnqueueWaits = 0;
			}
			intervalDequeueWaits = raw.getDequeueWaits() - curIntervalDequeueWaits;
			if ( intervalDequeueWaits < 0 ) {
				intervalDequeueWaits = 0;
			}
			intervalDequeueTimeouts = raw.getDequeueTimeouts() - curIntervalDequeueTimeouts;
			if ( intervalDequeueTimeouts < 0 ) {
				intervalDequeueTimeouts = 0;
			}

			long sampleTimeMillis = intervalTimeMillis / calcToSampleFactor;
			peakEnqueuedRate = peakEnqueued / (sampleTimeMillis / 1000.0);
			peakDequeuedRate = peakDequeued / (sampleTimeMillis / 1000.0);
			peakFlushedRate = peakFlushed / (sampleTimeMillis / 1000.0);
			peakOverlaidRate = peakOverlaid / (sampleTimeMillis / 1000.0);
			peakFlipsRate = peakFlips / (sampleTimeMillis / 1000.0);
			peakFlipVolumeRate = peakFlipVolume / (sampleTimeMillis / 1000.0);
			peakEnqueueWaitsRate = peakEnqueueWaits / (sampleTimeMillis / 1000.0);
			peakDequeueWaitsRate = peakDequeueWaits / (sampleTimeMillis / 1000.0);
			peakDequeueTimeoutsRate = peakDequeueTimeouts / (sampleTimeMillis / 1000.0);

			avgEnqueuedRate = intervalEnqueued / (intervalTimeMillis / 1000.0);
			avgDequeuedRate = intervalDequeued / (intervalTimeMillis / 1000.0);
			avgFlushedRate = intervalFlushed / (intervalTimeMillis / 1000.0);
			avgOverlaidRate = intervalOverlaid / (intervalTimeMillis / 1000.0);
			avgFlipsRate = intervalFlips / (intervalTimeMillis / 1000.0);
			avgFlipVolumeRate = intervalFlipVolume / (intervalTimeMillis / 1000.0);
			avgEnqueueWaitsRate = intervalEnqueueWaits / (intervalTimeMillis / 1000.0);
			avgDequeueWaitsRate = intervalDequeueWaits / (intervalTimeMillis / 1000.0);
			avgDequeueTimeoutsRate = intervalDequeueTimeouts / (intervalTimeMillis / 1000.0);

			// Zero-out cur values.
			curPeakEnqueued = 0;
			curPeakDequeued = 0;
			curPeakFlushed = 0;
			curPeakOverlaid = 0;
			curPeakFlips = 0;
			curPeakFlipVolume = 0;
			curPeakEnqueueWaits = 0;
			curPeakDequeueWaits = 0;
			curPeakDequeueTimeouts = 0;
			curIntervalEnqueued = 0;
			curIntervalDequeued = 0;
			curIntervalFlushed = 0;
			curIntervalOverlaid = 0;
			curIntervalFlips = 0;
			curIntervalFlipVolume = 0;
			curIntervalEnqueueWaits = 0;
			curIntervalDequeueWaits = 0;
			curIntervalDequeueTimeouts = 0;

			intervalTimeMillis = 0;
		}
	}

	public long incSamples() {
		numSamples++;
		return numSamples;
	}

	/**
	 * Gets the value of peakEnqueued
	 *
	 * @return the value of peakEnqueued
	 */
	public long getPeakEnqueued()  {
		synchronized( lock ) {
			return this.peakEnqueued;
		}
	}

	/**
	 * Gets the value of peakDequeued
	 *
	 * @return the value of peakDequeued
	 */
	public long getPeakDequeued()  {
		synchronized( lock ) {
			return this.peakDequeued;
		}
	}

	/**
	 * Gets the value of peakFlushed
	 *
	 * @return the value of peakFlushed
	 */
	public long getPeakFlushed()  {
		synchronized( lock ) {
			return this.peakFlushed;
		}
	}

	/**
	 * Gets the value of peakOverlaid
	 *
	 * @return the value of peakOverlaid
	 */
	public long getPeakOverlaid()  {
		synchronized( lock ) {
			return this.peakOverlaid;
		}
	}

	/**
	 * Gets the value of peakFlips
	 *
	 * @return the value of peakFlips
	 */
	public long getPeakFlips()  {
		synchronized( lock ) {
			return this.peakFlips;
		}
	}

	/**
	 * Gets the value of peakFlipVolume
	 *
	 * @return the value of peakFlipVolume
	 */
	public long getPeakFlipVolume()  {
		synchronized( lock ) {
			return this.peakFlipVolume;
		}
	}

	/**
	 * Gets the value of peakEnqueueWaits
	 *
	 * @return the value of peakEnqueueWaits
	 */
	public long getPeakEnqueueWaits()  {
		synchronized( lock ) {
			return this.peakEnqueueWaits;
		}
	}

	/**
	 * Gets the value of peakDequeueWaits
	 *
	 * @return the value of peakDequeueWaits
	 */
	public long getPeakDequeueWaits()  {
		synchronized( lock ) {
			return this.peakDequeueWaits;
		}
	}

	/**
	 * Gets the value of peakDequeueTimeouts
	 *
	 * @return the value of peakDequeueTimeouts
	 */
	public long getPeakDequeueTimeouts()  {
		synchronized( lock ) {
			return this.peakDequeueTimeouts;
		}
	}

	/**
	 * Gets the value of intervalEnqueued
	 *
	 * @return the value of intervalEnqueued
	 */
	public long getIntervalEnqueued()  {
		synchronized( lock ) {
			return this.intervalEnqueued;
		}
	}

	/**
	 * Gets the value of intervalDequeued
	 *
	 * @return the value of intervalDequeued
	 */
	public long getIntervalDequeued()  {
		synchronized( lock ) {
			return this.intervalDequeued;
		}
	}

	/**
	 * Gets the value of intervalFlushed
	 *
	 * @return the value of intervalFlushed
	 */
	public long getIntervalFlushed()  {
		synchronized( lock ) {
			return this.intervalFlushed;
		}
	}

	/**
	 * Gets the value of intervalOverlaid
	 *
	 * @return the value of intervalOverlaid
	 */
	public long getIntervalOverlaid()  {
		synchronized( lock ) {
			return this.intervalOverlaid;
		}
	}

	/**
	 * Gets the value of intervalFlips
	 *
	 * @return the value of intervalFlips
	 */
	public long getIntervalFlips()  {
		synchronized( lock ) {
			return this.intervalFlips;
		}
	}

	/**
	 * Gets the value of intervalFlipVolume
	 *
	 * @return the value of intervalFlipVolume
	 */
	public long getIntervalFlipVolume()  {
		synchronized( lock ) {
			return this.intervalFlipVolume;
		}
	}

	/**
	 * Gets the value of intervalEnqueueWaits
	 *
	 * @return the value of intervalEnqueueWaits
	 */
	public long getIntervalEnqueueWaits()  {
		synchronized( lock ) {
			return this.intervalEnqueueWaits;
		}
	}

	/**
	 * Gets the value of intervalDequeueWaits
	 *
	 * @return the value of intervalDequeueWaits
	 */
	public long getIntervalDequeueWaits()  {
		synchronized( lock ) {
			return this.intervalDequeueWaits;
		}
	}

	/**
	 * Gets the value of intervalDequeueTimeouts
	 *
	 * @return the value of intervalDequeueTimeouts
	 */
	public long getIntervalDequeueTimeouts()  {
		synchronized( lock ) {
			return this.intervalDequeueTimeouts;
		}
	}

	/**
	 * Gets the value of peakEnqueuedRate
	 *
	 * @return the value of peakEnqueuedRate
	 */
	public double getPeakEnqueuedRate()  {
		synchronized( lock ) {
			return this.peakEnqueuedRate;
		}
	}

	/**
	 * Gets the value of peakDequeuedRate
	 *
	 * @return the value of peakDequeuedRate
	 */
	public double getPeakDequeuedRate()  {
		synchronized( lock ) {
			return this.peakDequeuedRate;
		}
	}

	/**
	 * Gets the value of peakFlushedRate
	 *
	 * @return the value of peakFlushedRate
	 */
	public double getPeakFlushedRate()  {
		synchronized( lock ) {
			return this.peakFlushedRate;
		}
	}

	/**
	 * Gets the value of peakOverlaidRate
	 *
	 * @return the value of peakOverlaidRate
	 */
	public double getPeakOverlaidRate()  {
		synchronized( lock ) {
			return this.peakOverlaidRate;
		}
	}

	/**
	 * Gets the value of peakFlipsRate
	 *
	 * @return the value of peakFlipsRate
	 */
	public double getPeakFlipsRate()  {
		synchronized( lock ) {
			return this.peakFlipsRate;
		}
	}

	/**
	 * Gets the value of peakFlipVolumeRate
	 *
	 * @return the value of peakFlipVolumeRate
	 */
	public double getPeakFlipVolumeRate()  {
		synchronized( lock ) {
			return this.peakFlipVolumeRate;
		}
	}

	/**
	 * Gets the value of peakEnqueueWaitsRate
	 *
	 * @return the value of peakEnqueueWaitsRate
	 */
	public double getPeakEnqueueWaitsRate()  {
		synchronized( lock ) {
			return this.peakEnqueueWaitsRate;
		}
	}

	/**
	 * Gets the value of peakDequeueWaitsRate
	 *
	 * @return the value of peakDequeueWaitsRate
	 */
	public double getPeakDequeueWaitsRate()  {
		synchronized( lock ) {
			return this.peakDequeueWaitsRate;
		}
	}

	/**
	 * Gets the value of peakDequeueTimeoutsRate
	 *
	 * @return the value of peakDequeueTimeoutsRate
	 */
	public double getPeakDequeueTimeoutsRate()  {
		synchronized( lock ) {
			return this.peakDequeueTimeoutsRate;
		}
	}

	/**
	 * Gets the value of avgEnqueuedRate
	 *
	 * @return the value of avgEnqueuedRate
	 */
	public double getAvgEnqueuedRate()  {
		synchronized( lock ) {
			return this.avgEnqueuedRate;
		}
	}

	/**
	 * Gets the value of avgDequeuedRate
	 *
	 * @return the value of avgDequeuedRate
	 */
	public double getAvgDequeuedRate()  {
		synchronized( lock ) {
			return this.avgDequeuedRate;
		}
	}

	/**
	 * Gets the value of avgFlushedRate
	 *
	 * @return the value of avgFlushedRate
	 */
	public double getAvgFlushedRate()  {
		synchronized( lock ) {
			return this.avgFlushedRate;
		}
	}

	/**
	 * Gets the value of avgOverlaidRate
	 *
	 * @return the value of avgOverlaidRate
	 */
	public double getAvgOverlaidRate()  {
		synchronized( lock ) {
			return this.avgOverlaidRate;
		}
	}

	/**
	 * Gets the value of avgFlipsRate
	 *
	 * @return the value of avgFlipsRate
	 */
	public double getAvgFlipsRate()  {
		synchronized( lock ) {
			return this.avgFlipsRate;
		}
	}

	/**
	 * Gets the value of avgFlipVolumeRate
	 *
	 * @return the value of avgFlipVolumeRate
	 */
	public double getAvgFlipVolumeRate()  {
		synchronized( lock ) {
			return this.avgFlipVolumeRate;
		}
	}

	/**
	 * Gets the value of avgEnqueueWaitsRate
	 *
	 * @return the value of avgEnqueueWaitsRate
	 */
	public double getAvgEnqueueWaitsRate()  {
		synchronized( lock ) {
			return this.avgEnqueueWaitsRate;
		}
	}

	/**
	 * Gets the value of avgDequeueWaitsRate
	 *
	 * @return the value of avgDequeueWaitsRate
	 */
	public double getAvgDequeueWaitsRate()  {
		synchronized( lock ) {
			return this.avgDequeueWaitsRate;
		}
	}

	/**
	 * Gets the value of avgDequeueTimeoutsRate
	 *
	 * @return the value of avgDequeueTimeoutsRate
	 */
	public double getAvgDequeueTimeoutsRate()  {
		synchronized( lock ) {
			return this.avgDequeueTimeoutsRate;
		}
	}


	// Provide impls for interface, delegate everything to raw.

	/**
	 * Sets new value to privateMode.  This flag can control whether this
	 * instrumentor is exposed to the outside via any output
	 * mechanism.
	 *
	 * @param newValue a <code>boolean</code> value
	 */
	public void setPrivate( boolean newValue ) {
		raw.setPrivate( newValue );
	}

	/**
	 * Returns value of privateMode.  This flag can control whether this
	 * instrumentor is exposed to the outside via any output
	 * mechanism.
	 *
	 * @return a <code>boolean</code> value
	 */
	public boolean isPrivate() {
		return raw.isPrivate();
	}

	/**
	 * Gets the value of key
	 *
	 * @return the value of key
	 */
	public byte[] getKey()  {
		return raw.getKey();
	}

	/**
	 * Sets the value of key
	 *
	 * @param argKey Value to assign to this.key
	 */
	public void setKey(byte[] argKey) {
		raw.setKey( argKey );
	}

	/**
	 * Gets the value of name
	 *
	 * @return the value of name
	 */
	public String getName()  {
		return raw.getName();
	}

	/**
	 * Renames the instrumentor.  Do not call this method
	 * if the instrumentor is currently registered with
	 * its factory.  A rename without first unregistering
	 * the instrumentor will make a subsequent unregister
	 * call fail (it won't find the instrumentor, so the
	 * instrumentor won't be unregistered).
	 *
	 * So, before calling this method, unregister this
	 * instrumentor.  After the rename, the instrumentor
	 * can be reregistered with the factory under the new
	 * name.
	 *
	 * @param newName a <code>String</code> value
	 */
	public void rename( String newName ) {
		raw.rename( newName );
	}

	/**
	 * Gets the value of userData
	 *
	 * @return the value of userData
	 */
	public Object getUserData()  {
		return raw.getUserData();
	}

	/**
	 * Sets the value of userData
	 *
	 * @param argUserObject Value to assign to this.userData
	 */
	public void setUserData(Object argUserData) {
		raw.setUserData( argUserData );
	}

	/**
	 * Gets the value of enqueued
	 *
	 * @return the value of enqueued
	 */
	public long getEnqueued()  {
		return raw.getEnqueued();
	}

	/**
	 * Sets the value of enqueued
	 *
	 * @param argEnqueued Value to assign to this.enqueued
	 */
	public void setEnqueued(long argEnqueued) {
		synchronized( lock ) {
			long deltaEnq = argEnqueued - raw.getEnqueued();
			if ( deltaEnq > curPeakEnqueued ) {
				curPeakEnqueued = deltaEnq;
			}

			if ( curIntervalEnqueued == 0 ) {
				curIntervalEnqueued = raw.getEnqueued();
			}
		}

		raw.setEnqueued( argEnqueued );
	}

	/**
	 * Increments the value of enqueued
	 *
	 * @param incAmount Value to increment this.enqueued
	 */
	public void incEnqueued(long incAmount) {
		raw.incEnqueued( incAmount );
	}

	/**
	 * Gets the value of dequeued
	 *
	 * @return the value of dequeued
	 */
	public long getDequeued()  {
		return raw.getDequeued();
	}

	/**
	 * Sets the value of dequeued
	 *
	 * @param argDequeued Value to assign to this.dequeued
	 */
	public void setDequeued(long argDequeued) {
		synchronized( lock ) {
			long deltaDeq = argDequeued - raw.getDequeued();
			if ( deltaDeq > curPeakDequeued ) {
				curPeakDequeued = deltaDeq;
			}

			if ( curIntervalDequeued == 0 ) {
				curIntervalDequeued = raw.getDequeued();
			}
		}

		raw.setDequeued( argDequeued );
	}

	/**
	 * Increments the value of dequeued
	 *
	 * @param incAmount Value to increment this.dequeued
	 */
	public void incDequeued(long incAmount) {
		raw.incDequeued( incAmount );
	}

	/**
	 * Gets the value of flushed
	 *
	 * @return the value of flushed
	 */
	public long getFlushed()  {
		return raw.getFlushed();
	}

	/**
	 * Sets the value of flushed
	 *
	 * @param argFlushed Value to assign to this.flushed
	 */
	public void setFlushed(long argFlushed) {
		synchronized( lock ) {
			long deltaFlush = argFlushed - raw.getFlushed();
			if ( deltaFlush > curPeakFlushed ) {
				curPeakFlushed = deltaFlush;
			}

			if ( curIntervalFlushed == 0 ) {
				curIntervalFlushed = raw.getFlushed();
			}
		}

		raw.setFlushed( argFlushed );
	}

	/**
	 * Increments the value of flushed
	 *
	 * @param incAmount Value to increment this.flushed
	 */
	public void incFlushed(long incAmount) {
		raw.incFlushed( incAmount );
	}

	/**
	 * Gets the value of overlaid
	 *
	 * @return the value of overlaid
	 */
	public long getOverlaid()  {
		return raw.getOverlaid();
	}

	/**
	 * Sets the value of overlaid
	 *
	 * @param argOverlaid Value to assign to this.overlaid
	 */
	public void setOverlaid(long argOverlaid) {
		synchronized( lock ) {
			long deltaOverlaid = argOverlaid - raw.getOverlaid();
			if ( deltaOverlaid > curPeakOverlaid ) {
				curPeakOverlaid = deltaOverlaid;
			}

			if ( curIntervalOverlaid == 0 ) {
				curIntervalOverlaid = raw.getOverlaid();
			}
		}

		raw.setOverlaid( argOverlaid );
	}

	/**
	 * Increments the value of overlaid
	 *
	 * @param incAmount Value to increment this.overlaid
	 */
	public void incOverlaid(long incAmount) {
		raw.incOverlaid( incAmount );
	}

	/**
	 * Gets the value of flips
	 *
	 * @return the value of flips
	 */
	public synchronized long getFlips()  {
		return raw.getFlips();
	}

	/**
	 * Empty for this impl.
	 *
	 * @param argFlips
	 */
	public void setFlips(long argFlips) {
		synchronized( lock ) {
			long deltaFlips = argFlips - raw.getFlips();
			if ( deltaFlips > curPeakFlips ) {
				curPeakFlips = deltaFlips;
			}

			if ( curIntervalFlips == 0 ) {
				curIntervalFlips = raw.getFlips();
			}
		}

		raw.setFlips( argFlips );
	}

	/**
	 * Increments the value of flips
	 *
	 * @param incAmount
	 */
	public void incFlips(long incAmount) {
		raw.incFlips( incAmount );
	}

	/**
	 * Gets the value of flipVolume
	 *
	 * @return the value of flipVolume
	 */
	public synchronized long getFlipVolume()  {
		return getFlipVolume();
	}

	/**
	 * Empty for this impl.
	 *
	 * @param argFlipVolume
	 */
	public void setFlipVolume(long argFlipVolume) {
		synchronized( lock ) {
			long deltaFlipVolume = argFlipVolume - raw.getFlipVolume();
			if ( deltaFlipVolume > curPeakFlipVolume ) {
				curPeakFlipVolume = deltaFlipVolume;
			}

			if ( curIntervalFlipVolume == 0 ) {
				curIntervalFlipVolume = raw.getFlipVolume();
			}
		}

		raw.setFlipVolume( argFlipVolume );
	}

	/**
	 * Increments the value of flipVolume
	 *
	 * @param incAmount
	 */
	public void incFlipVolume(long incAmount) {
		raw.incFlipVolume( incAmount );
	}

	/**
	 * Gets the value of enqueueWaits
	 *
	 * @return the value of enqueueWaits
	 */
	public synchronized long getEnqueueWaits() {
		return raw.getEnqueueWaits();
	}

	/**
	 * Empty for this impl.
	 *
	 * @param argEnqueueWaits
	 */
	public void setEnqueueWaits(long argEnqueueWaits) {
		synchronized( lock ) {
			long deltaEnqueueWaits = argEnqueueWaits - raw.getEnqueueWaits();
			if ( deltaEnqueueWaits > curPeakEnqueueWaits ) {
				curPeakEnqueueWaits = deltaEnqueueWaits;
			}

			if ( curIntervalEnqueueWaits == 0 ) {
				curIntervalEnqueueWaits = raw.getEnqueueWaits();
			}
		}

		raw.setEnqueueWaits( argEnqueueWaits );
	}

	/**
	 * Increments the value of enqueueWaits
	 *
	 * @param incAmount
	 */
	public void incEnqueueWaits(long incAmount) {
		raw.incEnqueueWaits( incAmount );
	}

	/**
	 * Gets the value of dequeueWaits
	 *
	 * @return the value of dequeueWaits
	 */
	public synchronized long getDequeueWaits() {
		return getDequeueWaits();
	}

	/**
	 * Empty for this impl.
	 *
	 * @param argDequeueWaits
	 */
	public void setDequeueWaits(long argDequeueWaits) {
		synchronized( lock ) {
			long deltaDequeueWaits = argDequeueWaits - raw.getDequeueWaits();
			if ( deltaDequeueWaits > curPeakDequeueWaits ) {
				curPeakDequeueWaits = deltaDequeueWaits;
			}

			if ( curIntervalDequeueWaits == 0 ) {
				curIntervalDequeueWaits = raw.getDequeueWaits();
			}
		}

		raw.setDequeueWaits( argDequeueWaits );
	}

	/**
	 * Increments the value of dequeueWaits
	 *
	 * @param incAmount
	 */
	public void incDequeueWaits(long incAmount) {
		raw.incDequeueWaits( incAmount );
	}

	/**
	 * Gets the value of dequeueTimeouts
	 *
	 * @return the value of dequeueTimeouts
	 */
	public synchronized long getDequeueTimeouts() {
		return getDequeueTimeouts();
	}

	/**
	 * Empty for this impl.
	 *
	 * @param argDequeueTimeouts
	 */
	public void setDequeueTimeouts(long argDequeueTimeouts) {
		synchronized( lock ) {
			long deltaDequeueTimeouts = argDequeueTimeouts - raw.getDequeueTimeouts();
			if ( deltaDequeueTimeouts > curPeakDequeueTimeouts ) {
				curPeakDequeueTimeouts = deltaDequeueTimeouts;
			}

			if ( curIntervalDequeueTimeouts == 0 ) {
				curIntervalDequeueTimeouts = raw.getDequeueTimeouts();
			}
		}

		raw.setDequeueTimeouts( argDequeueTimeouts );
	}

	/**
	 * Increments the value of dequeueTimeouts
	 *
	 * @param incAmount
	 */
	public void incDequeueTimeouts(long incAmount) {
		raw.incDequeueTimeouts( incAmount );
	}

	/**
	 * Gets the value of highWaterMark
	 *
	 * @return the value of highWaterMark
	 */
	public long getHighWaterMark()  {
		return raw.getHighWaterMark();
	}

	/**
	 * Gets the value of highWaterMark
	 *
	 * @return the value of highWaterMark
	 */
	public long getHighWaterMarkAndReset( )  {
		return raw.getHighWaterMarkAndReset( );
	}

	/**
	 * Sets the value of highWaterMark
	 *
	 * @param argHighWaterMark Value to assign to this.highWaterMark
	 */
	public void setHighWaterMark(long argHighWaterMark) {
		raw.setHighWaterMark( argHighWaterMark );
	}

	/**
	 * Gets the value of overallHighWaterMark
	 *
	 * @return the value of overallHighWaterMark
	 */
	public long getOverallHighWaterMark()  {
		return raw.getOverallHighWaterMark();
	}

	/**
	 * Sets the value of overallHighWaterMark
	 *
	 * @param argHighWaterMark Value to assign to this.overallHighWaterMark
	 */
	public void setOverallHighWaterMark(long argHighWaterMark) {
		raw.setOverallHighWaterMark( argHighWaterMark );
	}

	/**
	 * Sets the value of overallHighWaterMarkTime
	 *
	 * @param argHighWaterMarkTime Value to assign to this.overallHighWaterMarkTime
	 */
	public void setOverallHighWaterMarkTime(long argHighWaterMarkTime) {
		raw.setOverallHighWaterMarkTime( argHighWaterMarkTime );
	}

	/**
	 * Gets the value of overallHighWaterMarkTime
	 *
	 * @return the value of overallHighWaterMarkTime
	 */
	public long getOverallHighWaterMarkTime()  {
		return raw.getOverallHighWaterMarkTime();
	}

	/**
	 * Gets the value of currentSize
	 *
	 * @return the value of currentSize
	 */
	public long getCurrentSize()  {
		return raw.getCurrentSize();
	}

	/**
	 * Sets the value of currentSize
	 *
	 * @param argCurrentSize Value to assign to this.currentSize
	 */
	public void setCurrentSize(long argCurrentSize) {
		raw.setCurrentSize( argCurrentSize );
	}

	/**
	 * Gets the value of status
	 *
	 * @return the value of status
	 */
	public short getStatus()  {
		return raw.getStatus();
	}

	/**
	 * Sets the value of status
	 *
	 * @param argStatus Value to assign to this.status
	 */
	public void setStatus(short argStatus) {
		raw.setStatus( argStatus );
	}

	/**
	 * Copies this QI into the given QI.
	 *
	 * @param qi a <code>QueueInstrumentor</code> value
	 * @return a <code>QueueInstrumentor</code> value
	 */
	public void get( QueueInstrumentor qi ) {
		raw.get( qi );
	}

	public String getToStringHeader( boolean showUserData, boolean showPrivate ) {
		return "THIS getToStringHeader NOT IMPLEMENTED";
	}

	public String getToStringHeader() {
		return "Name,PeakEnqueued,PeakDequeued,PeakFlushed,PeakOverlaid,IntEnqueued,IntDequeued,IntFlushed,IntOverlaid,PeakEnqRate,PeakDeqRate,PeakFlushRate,PeakOverRate,AvgEnqRate,AvgDeqRate,AvgFlushRate,AvgOverRate";
	}

	public String toString( boolean showUserData, boolean showPrivate, String instNameToUse ) {
		return raw.toString( showUserData, showPrivate, instNameToUse );
	}

	public String toString( boolean showUserData, boolean showPrivate, String instNameToUse, Object hwmKeeper ) {
		return raw.toString( showUserData, showPrivate, instNameToUse, hwmKeeper );
	}

	public String toString( String instNameToUse ) {
		return instNameToUse + "," +
			getPeakEnqueued() + "," +
			getPeakDequeued() + "," +
			getPeakFlushed() + "," +
			getPeakOverlaid() + "," +
			getIntervalEnqueued() + "," +
			getIntervalDequeued() + "," +
			getIntervalFlushed() + "," +
			getIntervalOverlaid() + "," +
			getPeakEnqueuedRate() + "," +
			getPeakDequeuedRate() + "," +
			getPeakFlushedRate() + "," +
			getPeakOverlaidRate() + "," +
			getAvgEnqueuedRate() + "," +
			getAvgDequeuedRate() + "," +
			getAvgFlushedRate() + "," +
			getAvgOverlaidRate();
	}

	public String toString() {
		return toString( getName() );
	}

} // QueueInstrumentorCalculatedImpl
