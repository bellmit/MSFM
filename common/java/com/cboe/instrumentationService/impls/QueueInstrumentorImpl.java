package com.cboe.instrumentationService.impls;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import com.cboe.instrumentationService.factories.InstrumentorFactory;
import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;

/**
 * QueueInstrumentorImpl.java
 *
 *
 * Created: Wed Sep  3 14:22:38 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class QueueInstrumentorImpl implements QueueInstrumentor {

	private String name;
	private Object userData;
	private AtomicLong enqueued = new AtomicLong();
	private AtomicLong dequeued = new AtomicLong();
	private AtomicLong flushed = new AtomicLong();
	private AtomicLong overlaid = new AtomicLong();
	private AtomicLong flips = new AtomicLong();
	private AtomicLong flipVolume = new AtomicLong();
	private AtomicLong enqueueWaits = new AtomicLong();
	private AtomicLong dequeueWaits = new AtomicLong();
	private AtomicLong dequeueTimeouts = new AtomicLong();
	private AtomicLong highWaterMark = new AtomicLong();
	private AtomicLong overallHighWaterMark = new AtomicLong();
	private AtomicLong overallHWMTime = new AtomicLong();
	private AtomicLong currentSize = new AtomicLong();
	private AtomicInteger status = new AtomicInteger();
	private AtomicLong exceptions = new AtomicLong();
	private AtomicReference<Throwable> lastException = new AtomicReference<Throwable>();
	private AtomicLong lastExceptionTime = new AtomicLong();
	private byte[] key = null;
	private boolean privateMode = false;
	private InstrumentorFactory factory = null;
	private ConcurrentHashMap<Object,ResettableHwm> hwmKeeperValues = new ConcurrentHashMap<Object,ResettableHwm>();

	public QueueInstrumentorImpl( String name, Object userData ) {
		this.name = name;
		this.userData = userData;
	} // QueueInstrumentorImpl constructor


	public void setLockObject( Object newLockObject ) {
	}

	public void setEnqueueLockObject( Object newLockObject ) {
	}

	public void setDequeueLockObject( Object newLockObject ) {
	}

	/**
	 * Sets new value to privateMode.  This flag can control whether this
	 * instrumentor is exposed to the outside via any output
	 * mechanism.
	 *
	 * @param newValue a <code>boolean</code> value
	 */
	public void setPrivate( boolean newValue ) {
		privateMode = newValue;
	}

	/**
	 * Returns value of privateMode.  This flag can control whether this
	 * instrumentor is exposed to the outside via any output
	 * mechanism.
	 *
	 * @return a <code>boolean</code> value
	 */
	public boolean isPrivate() {
		return privateMode;
	}

	/**
	 * Gets the value of key
	 *
	 * @return the value of key
	 */
	public byte[] getKey()  {
		return this.key;
	}

	/**
	 * Sets the value of key
	 *
	 * @param argKey Value to assign to this.key
	 */
	public void setKey(byte[] argKey) {
		this.key = argKey;
	}

	/**
	 * Gets the value of name
	 *
	 * @return the value of name
	 */
	public String getName()  {
		return this.name;
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
		this.name = newName;
	}

	/**
	 * Gets the value of userData
	 *
	 * @return the value of userData
	 */
	public Object getUserData()  {
		return this.userData;
	}

	/**
	 * Sets the value of userData
	 *
	 * @param argUserObject Value to assign to this.userData
	 */
	public void setUserData(Object argUserData) {
		this.userData = argUserData;
	}

	public void setFactory( InstrumentorFactory factory ) {
		this.factory = factory;
	}

	public InstrumentorFactory getFactory() {
		return factory;
	}

	/**
	 * Gets the value of enqueued
	 *
	 * @return the value of enqueued
	 */
	public long getEnqueued()  {
		return this.enqueued.get();
	}

	/**
	 * Sets the value of enqueued
	 *
	 * @param argEnqueued Value to assign to this.enqueued
	 */
	public void setEnqueued(long argEnqueued) {
		this.enqueued.set(argEnqueued);
	}

	/**
	 * Increments the value of enqueued
	 *
	 * @param incAmount Value to increment this.enqueued
	 */
	public void incEnqueued(long incAmount) {
		this.enqueued.addAndGet(incAmount);
	}

	/**
	 * Gets the value of dequeued
	 *
	 * @return the value of dequeued
	 */
	public long getDequeued()  {
		return this.dequeued.get();
	}

	/**
	 * Sets the value of dequeued
	 *
	 * @param argDequeued Value to assign to this.dequeued
	 */
	public void setDequeued(long argDequeued) {
		this.dequeued.set(argDequeued);
	}

	/**
	 * Increments the value of dequeued
	 *
	 * @param incAmount Value to increment this.dequeued
	 */
	public void incDequeued(long incAmount) {
		this.dequeued.addAndGet(incAmount);
	}

	/**
	 * Gets the value of flushed
	 *
	 * @return the value of flushed
	 */
	public long getFlushed()  {
		return this.flushed.get();
	}

	/**
	 * Sets the value of flushed
	 *
	 * @param argFlushed Value to assign to this.flushed
	 */
	public void setFlushed(long argFlushed) {
		this.flushed.set(argFlushed);
	}

	/**
	 * Increments the value of flushed
	 *
	 * @param incAmount Value to increment this.flushed
	 */
	public void incFlushed(long incAmount) {
		this.flushed.addAndGet(incAmount);
	}

	/**
	 * Gets the value of overlaid
	 *
	 * @return the value of overlaid
	 */
	public long getOverlaid()  {
		return this.overlaid.get();
	}

	/**
	 * Sets the value of overlaid
	 *
	 * @param argOverlaid Value to assign to this.overlaid
	 */
	public void setOverlaid(long argOverlaid) {
		this.overlaid.set(argOverlaid);
	}

	/**
	 * Increments the value of overlaid
	 *
	 * @param incAmount Value to increment this.overlaid
	 */
	public void incOverlaid(long incAmount) {
		this.overlaid.addAndGet(incAmount);
	}

	/**
	 * Gets the value of flips
	 *
	 * @return the value of flips
	 */
	public long getFlips()  {
		return this.flips.get();
	}

	/**
	 * Sets the value of flips
	 *
	 * @param argFlips Value to assign to this.flips
	 */
	public void setFlips(long argFlips) {
		this.flips.set(argFlips);
	}

	/**
	 * Increments the value of flips
	 *
	 * @param incAmount Value to increment this.flips
	 */
	public void incFlips(long incAmount) {
		this.flips.addAndGet(incAmount);
	}

	/**
	 * Gets the value of flipVolume
	 *
	 * @return the value of flipVolume
	 */
	public long getFlipVolume()  {
		return this.flipVolume.get();
	}

	/**
	 * Sets the value of flipVolume
	 *
	 * @param argFlipVolume Value to assign to this.flipVolume
	 */
	public void setFlipVolume(long argFlipVolume) {
		this.flipVolume.set(argFlipVolume);
	}

	/**
	 * Increments the value of flipVolume
	 *
	 * @param incAmount Value to increment this.flipVolume
	 */
	public void incFlipVolume(long incAmount) {
		this.flipVolume.addAndGet(incAmount);
	}

	/**
	 * Gets the value of enqueueWaits
	 *
	 * @return the value of enqueueWaits
	 */
	public long getEnqueueWaits()  {
		return this.enqueueWaits.get();
	}

	/**
	 * Sets the value of enqueueWaits
	 *
	 * @param argEnqueueWaits Value to assign to this.enqueueWaits
	 */
	public void setEnqueueWaits(long argEnqueueWaits) {
		this.enqueueWaits.set(argEnqueueWaits);
	}

	/**
	 * Increments the value of enqueueWaits
	 *
	 * @param incAmount Value to increment this.enqueueWaits
	 */
	public void incEnqueueWaits(long incAmount) {
		this.enqueueWaits.addAndGet(incAmount);
	}

	/**
	 * Gets the value of dequeueWaits
	 *
	 * @return the value of dequeueWaits
	 */
	public long getDequeueWaits()  {
		return this.dequeueWaits.get();
	}

	/**
	 * Sets the value of dequeueWaits
	 *
	 * @param argDequeueWaits Value to assign to this.dequeueWaits
	 */
	public void setDequeueWaits(long argDequeueWaits) {
		this.dequeueWaits.set(argDequeueWaits);
	}

	/**
	 * Increments the value of dequeueWaits
	 *
	 * @param incAmount Value to increment this.dequeueWaits
	 */
	public void incDequeueWaits(long incAmount) {
		this.dequeueWaits.addAndGet(incAmount);
	}

	/**
	 * Gets the value of dequeueTimeouts
	 *
	 * @return the value of dequeueTimeouts
	 */
	public long getDequeueTimeouts()  {
		return this.dequeueTimeouts.get();
	}

	/**
	 * Sets the value of dequeueTimeouts
	 *
	 * @param argDequeueTimeouts Value to assign to this.dequeueTimeouts
	 */
	public void setDequeueTimeouts(long argDequeueTimeouts) {
		this.dequeueTimeouts.set(argDequeueTimeouts);
	}

	/**
	 * Increments the value of dequeueTimeouts
	 *
	 * @param incAmount Value to increment this.dequeueTimeouts
	 */
	public void incDequeueTimeouts(long incAmount) {
		this.dequeueTimeouts.addAndGet(incAmount);
	}

	/**
	 * Gets the value of highWaterMark
	 *
	 * @return the value of highWaterMark
	 */
	public long getHighWaterMark()  {
		return this.highWaterMark.get();
	}

	/**
	 * Gets the value of highWaterMark.
	 * 
	 * 5/2009 - the concept of a keeper (or "view") has been removed on the idea that it is not
	 *          needed/used and adding overhead of iteration.  Removing the iteration.
	 *
	 * @return the value of the default highWaterMark
	 */
	public long getHighWaterMarkAndReset( ) {
		long hwm = getHighWaterMark();
		setHighWaterMark( 0 );
		return hwm;
	}

	/**
	 * Sets the value of highWaterMark
	 *
	 * @param argHighWaterMark Value to assign to this.highWaterMark
	 */
	public void setHighWaterMark(long argHighWaterMark) {
		this.highWaterMark.set(argHighWaterMark);
	}

	/**
	 * Gets the value of overallHighWaterMark
	 *
	 * @return the value of overallHighWaterMark
	 */
	public long getOverallHighWaterMark()  {
		return this.overallHighWaterMark.get();
	}

	/**
	 * Sets the value of overallHighWaterMark
	 *
	 * @param argOverallHighWaterMark Value to assign to this.overallHighWaterMark
	 */
	public void setOverallHighWaterMark(long argOverallHighWaterMark) {
		this.overallHighWaterMark.set(argOverallHighWaterMark);
		this.overallHWMTime.set(System.currentTimeMillis());
	}

	/**
	 * Sets the value of overallHighWaterMarkTime
	 *
	 * @param argOverallHighWaterMarkTime Value to assign to this.overallHighWaterMarkTime
	 */
	public void setOverallHighWaterMarkTime(long argOverallHighWaterMarkTime) {
		this.overallHWMTime.set(argOverallHighWaterMarkTime);
	}

	/**
	 * Gets the value of overallHighWaterMarkTime
	 *
	 * @return the value of overallHighWaterMarkTime
	 */
	public long getOverallHighWaterMarkTime()  {
		return this.overallHWMTime.get();
	}

	/**
	 * Gets the value of currentSize
	 *
	 * @return the value of currentSize
	 */
	public long getCurrentSize()  {
		return this.currentSize.get();
	}

	/**
	 * Sets the value of currentSize
	 *
	 * @param argCurrentSize Value to assign to this.currentSize
	 */
	public void setCurrentSize(long argCurrentSize) {
		// While removing all lock usage and replacing with Atomics should improve
		// performance, it might also render this code occasionally inaccurate.  The
		// problem is that, since the whole block of code is not locked down, updates
		// to currentSize could happen throughout this method (by other threads).  So,
		// hwm setting might not be completely correct, as it may compare against some
		// newly set value smaller than the one for the thread that first came in and
		// set currentSize.

		this.currentSize.set(argCurrentSize);
		// It is possible, if instrumentor monitors are
		// operating, that the HWM will get reset on a
		// regular interval.  Doesn't effect this code,
		// but explains the difference between HWM and
		// OverallHWM (which does not get reset).
		if ( this.currentSize.get() > this.highWaterMark.get() ) {
			this.highWaterMark.set( this.currentSize.get() );
		}
		if ( this.currentSize.get() > this.overallHighWaterMark.get() ) {
			setOverallHighWaterMark( this.currentSize.get() ); // Sets time also.
		}
	}

	/**
	 * Gets the value of status
	 *
	 * @return the value of status
	 */
	public short getStatus()  {
		return (short)status.get();
	}

	/**
	 * Sets the value of status
	 *
	 * @param argStatus Value to assign to this.status
	 */
	public void setStatus(short argStatus) {
		status.set(argStatus);
	}

	/**
	 * Gets the value of exceptions
	 *
	 * @return the value of exceptions
	 */
	public long getExceptions()  {
		return this.exceptions.get();
	}

	/**
	 * Sets the value of exceptions
	 *
	 * @param argExceptions Value to assign to this.exceptions
	 */
	public void setExceptions(long argExceptions) {
		this.exceptions.set(argExceptions);
	}

	/**
	 * Increments the value of exceptions
	 *
	 * @param argExceptions Value to increment this.exceptions
	 */
	public void incExceptions(long argExceptions) {
		this.exceptions.addAndGet(argExceptions);
	}

	/**
	 * Gets the value of lastException
	 *
	 * @return the value of lastException
	 */
	public Throwable getLastException()  {
		return lastException.get();
	}

	/**
	 * Sets the value of lastException
	 *
	 * @param argLastException Value to assign to this.lastException
	 */
	public void setLastException(Throwable argLastException) {
		lastException.set(argLastException);
	}

	/**
	 * Gets the value of lastExceptionTime
	 *
	 * @return the value of lastExceptionTime
	 */
	public long getLastExceptionTime()  {
		return lastExceptionTime.get();
	}

	/**
	 * Sets the value of lastExceptionTime
	 *
	 * @param argLastExceptionTime Value to assign to this.lastExceptionTime
	 */
	public void setLastExceptionTime(long argLastExceptionTime) {
		lastExceptionTime.set(argLastExceptionTime);
	}

	/**
	 * Copies this QI into the given QI.
	 *
	 * @param qi a <code>QueueInstrumentor</code> value
	 * @return a <code>QueueInstrumentor</code> value
	 */
	public void get( QueueInstrumentor qi ) {
		if ( qi != null ) {
			qi.setEnqueued( enqueued.get() );
			qi.setDequeued( dequeued.get() );
			qi.setFlushed( flushed.get() );
			qi.setOverlaid( overlaid.get() );
			qi.setHighWaterMark( highWaterMark.get() );
			qi.setCurrentSize( currentSize.get() );
			qi.setOverallHighWaterMark( overallHighWaterMark.get() );
			qi.setUserData( userData );
			qi.setStatus( (short)status.get() );
			qi.setEnqueueWaits( enqueueWaits.get() );
			qi.setDequeueWaits( dequeueWaits.get() );
			qi.setDequeueTimeouts( dequeueTimeouts.get() );
		}
	}

	public String getToStringHeader( boolean showUserData, boolean showPrivateFlag ) {
		return "name," + (showPrivateFlag ? "private," : "") + "enqueued,dequeued,flushed,overlaid,hwm,size,status,overallhwm,overallhwmtime,enqwaits,deqwaits,deqtos" + (showUserData ? ",userdata" : "");
	}

    public String toString(boolean showUserData, boolean showPrivateFlag, String instNameToUse, Object hwmKeeper ) {
        String userDataStr = "";
        if (showUserData) {
			userDataStr = "," + getUserData();
        }

        return instNameToUse + "," +
			(showPrivateFlag ? (isPrivate() + ",") : "") +
            getEnqueued() + "," +
            getDequeued() + "," +
            getFlushed() + "," +
            getOverlaid() + "," +
            getHighWaterMarkAndReset( ) + "," +
            getCurrentSize() + "," +
			getStatus() + "," +
            getOverallHighWaterMark() + "," +
			(getOverallHighWaterMarkTime() > 0 ? InstrumentorTimeFormatter.format( getOverallHighWaterMarkTime()) : "0") + "," +
			getEnqueueWaits() + "," +
			getDequeueWaits() + "," +
			getDequeueTimeouts() +
            userDataStr;
    }
    
    public String toString(boolean showUserData, boolean showPrivateFlag, String instNameToUse) {
		return toString( showUserData, showPrivateFlag, instNameToUse, null );
	}

	public String toString() {
		return toString( true, true, getName() );
	}

} // QueueInstrumentorImpl
