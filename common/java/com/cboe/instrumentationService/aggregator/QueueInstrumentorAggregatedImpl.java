package com.cboe.instrumentationService.aggregator;

import java.util.*;
import java.text.SimpleDateFormat;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;
import com.cboe.instrumentationService.instrumentors.AggregatedQueueInstrumentor;
import com.cboe.instrumentationService.factories.InstrumentorFactory;
import com.cboe.instrumentationService.factories.QueueInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorFactoryVisitor;
import com.cboe.instrumentationService.factories.QueueInstrumentorFactoryVisitor;

/**
 * QueueInstrumentorAggregatedImpl.java
 *
 *
 * Created: Wed Sep  3 14:22:38 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class QueueInstrumentorAggregatedImpl implements QueueInstrumentor, AggregatedQueueInstrumentor {

	private String name;
	private Object userData;
	private byte[] key = null;
	private boolean privateMode = false;
	private ArrayList instrumentors = new ArrayList();
	private InstrumentorFactory factory = null;

	public QueueInstrumentorAggregatedImpl( String name, Object userData ) {
		this.name = name;
		this.userData = userData;
	} // QueueInstrumentorAggregatedImpl constructor

	public void setLockObject( Object newLockObject ) {
	}
	public void setEnqueueLockObject( Object newLockObject ) {
	}
	public void setDequeueLockObject( Object newLockObject ) {
	}


	public synchronized void addInstrumentor( QueueInstrumentor qi ) {
		if ( instrumentors.indexOf( qi ) < 0 ) {
			instrumentors.add( qi );
		}
	}

	public synchronized void removeInstrumentor( QueueInstrumentor qi ) {
		instrumentors.remove( qi );
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
	 * Iterates through the member instrumentors for this Aggregator and
	 * sets the private flag on each to the new value.
	 *
	 * @param newPrivateValue a <code>boolean</code> value
	 */
	public synchronized void setPrivateOnMembers( boolean newPrivateValue ) {
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			Instrumentor inst = (Instrumentor)iter.next();
			inst.setPrivate( newPrivateValue );
		}
	}

	/**
	 * This method can be called to unregister / remove all the
	 * member instrumentors from their respective factories.
	 *
	 */
	public synchronized void removeMembersFromFactories() {
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			QueueInstrumentor inst = (QueueInstrumentor)iter.next();
			QueueInstrumentorFactory ciFactory = (QueueInstrumentorFactory)inst.getFactory();
			ciFactory.unregister( inst );
		}
	}

	/**
	 * Visit all members of this aggregator.  Uses the existing
	 * InstrumentorFactoryVisitor interfaces.
	 *
	 * @param visitor an <code>InstrumentorFactoryVisitor</code> value
	 */
	public synchronized void visitMembers( InstrumentorFactoryVisitor visitor ) {
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			QueueInstrumentor inst = (QueueInstrumentor)iter.next();
			if ( !((QueueInstrumentorFactoryVisitor)visitor).visit( inst ) ) {
				return;
			}
		}
	}

	public void setFactory( InstrumentorFactory factory ) {
		this.factory = factory;
	}

	public InstrumentorFactory getFactory() {
		return factory;
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

	/**
	 * Gets the value of enqueued
	 *
	 * @return the value of enqueued
	 */
	public synchronized long getEnqueued()  {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((QueueInstrumentor)iter.next()).getEnqueued();
		}
		return total;
	}

	/**
	 * Empty for this impl.
	 *
	 * @param argEnqueued
	 */
	public void setEnqueued(long argEnqueued) {
	}

	/**
	 * Empty for this impl.
	 *
	 * @param incAmount
	 */
	public void incEnqueued(long incAmount) {
	}

	/**
	 * Gets the value of dequeued
	 *
	 * @return the value of dequeued
	 */
	public synchronized long getDequeued()  {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((QueueInstrumentor)iter.next()).getDequeued();
		}
		return total;
	}

	/**
	 * Empty for this impl.
	 *
	 * @param argDequeued
	 */
	public void setDequeued(long argDequeued) {
	}

	/**
	 * Empty for this impl.
	 *
	 * @param incAmount
	 */
	public void incDequeued(long incAmount) {
	}

	/**
	 * Gets the value of flushed
	 *
	 * @return the value of flushed
	 */
	public synchronized long getFlushed()  {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((QueueInstrumentor)iter.next()).getFlushed();
		}
		return total;
	}

	/**
	 * Empty for this impl.
	 *
	 * @param argFlushed
	 */
	public void setFlushed(long argFlushed) {
	}

	/**
	 * Empty for this impl.
	 *
	 * @param incAmount
	 */
	public void incFlushed(long incAmount) {
	}

	/**
	 * Gets the value of overlaid
	 *
	 * @return the value of overlaid
	 */
	public synchronized long getOverlaid()  {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((QueueInstrumentor)iter.next()).getOverlaid();
		}
		return total;
	}

	/**
	 * Empty for this impl.
	 *
	 * @param argOverlaid
	 */
	public void setOverlaid(long argOverlaid) {
	}

	/**
	 * Empty for this impl.
	 *
	 * @param incAmount
	 */
	public void incOverlaid(long incAmount) {
	}

	/**
	 * Gets the value of flips
	 *
	 * @return the value of flips
	 */
	public synchronized long getFlips()  {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((QueueInstrumentor)iter.next()).getFlips();
		}
		return total;
	}

	/**
	 * Empty for this impl.
	 *
	 * @param argFlips
	 */
	public void setFlips(long argFlips) {
	}

	/**
	 * Empty for this impl.
	 *
	 * @param incAmount
	 */
	public void incFlips(long incAmount) {
	}

	/**
	 * Gets the value of flipVolume
	 *
	 * @return the value of flipVolume
	 */
	public synchronized long getFlipVolume()  {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((QueueInstrumentor)iter.next()).getFlipVolume();
		}
		return total;
	}

	/**
	 * Empty for this impl.
	 *
	 * @param argFlipVolume
	 */
	public void setFlipVolume(long argFlipVolume) {
	}

	/**
	 * Empty for this impl.
	 *
	 * @param incAmount
	 */
	public void incFlipVolume(long incAmount) {
	}

	/**
	 * Gets the value of enqueueWaits
	 *
	 * @return the value of enqueueWaits
	 */
	public synchronized long getEnqueueWaits() {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((QueueInstrumentor)iter.next()).getEnqueueWaits();
		}
		return total;
	}

	/**
	 * Empty for this impl.
	 *
	 * @param argEnqueueWaits
	 */
	public void setEnqueueWaits(long argEnqueueWaits) {
	}

	/**
	 * Empty for this impl.
	 *
	 * @param incAmount
	 */
	public void incEnqueueWaits(long incAmount) {
	}

	/**
	 * Gets the value of dequeueWaits
	 *
	 * @return the value of dequeueWaits
	 */
	public synchronized long getDequeueWaits() {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((QueueInstrumentor)iter.next()).getDequeueWaits();
		}
		return total;
	}

	/**
	 * Empty for this impl.
	 *
	 * @param argDequeueWaits
	 */
	public void setDequeueWaits(long argDequeueWaits) {
	}

	/**
	 * Empty for this impl.
	 *
	 * @param incAmount
	 */
	public void incDequeueWaits(long incAmount) {
	}

	/**
	 * Gets the value of dequeueTimeouts
	 *
	 * @return the value of dequeueTimeouts
	 */
	public synchronized long getDequeueTimeouts() {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((QueueInstrumentor)iter.next()).getDequeueTimeouts();
		}
		return total;
	}

	/**
	 * Empty for this impl.
	 *
	 * @param argDequeueTimeouts
	 */
	public void setDequeueTimeouts(long argDequeueTimeouts) {
	}

	/**
	 * Empty for this impl.
	 *
	 * @param incAmount
	 */
	public void incDequeueTimeouts(long incAmount) {
	}

	/**
	 * Gets the value of highWaterMark
	 *
	 * @return the value of highWaterMark
	 */
	public synchronized long getHighWaterMark()  {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((QueueInstrumentor)iter.next()).getHighWaterMark();
		}
		return total;
	}

	/**
	 * Gets the value of highWaterMark
	 *
	 * @return the value of highWaterMark
	 */
	public synchronized long getHighWaterMarkAndReset( )  {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((QueueInstrumentor)iter.next()).getHighWaterMarkAndReset( );
		}
		return total;
	}

	/**
	 * Empty for this impl.
	 *
	 * @param argHighWaterMark
	 */
	public void setHighWaterMark(long argHighWaterMark) {
	}

	/**
	 * Gets the value of overallHighWaterMark
	 *
	 * @return the value of overallHighWaterMark
	 */
	public synchronized long getOverallHighWaterMark()  {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((QueueInstrumentor)iter.next()).getOverallHighWaterMark();
		}
		return total;
	}

	/**
	 * Empty for this impl.
	 *
	 * @param argHighWaterMark
	 */
	public void setOverallHighWaterMark(long argHighWaterMark) {
	}

	/**
	 * Empty for this impl.
	 *
	 * @param argHighWaterMarkTime
	 */
	public void setOverallHighWaterMarkTime(long argHighWaterMarkTime) {
	}

	public long getOverallHighWaterMarkTime() {
		return 0; // Doesn't really make sense for an aggregate.
	}

	/**
	 * Gets the value of currentSize
	 *
	 * @return the value of currentSize
	 */
	public synchronized long getCurrentSize()  {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((QueueInstrumentor)iter.next()).getCurrentSize();
		}
		return total;
	}

	/**
	 * Empty for this impl.
	 *
	 * @param argCurrentSize
	 */
	public void setCurrentSize(long argCurrentSize) {
	}

	/**
	 * Gets the value of status
	 *
	 * @return the value of status
	 */
	public short getStatus()  {
		return 0; // Not applicable for aggregate.
	}

	/**
	 * Empty for this impl.
	 *
	 * @param argStatus
	 */
	public void setStatus(short argStatus) {
	}

	/**
	 * Sets the value of lastExceptionTime
	 *
	 * @param argLastExceptionTime Value to assign to this.lastExceptionTime
	 */
	public void setLastExceptionTime(long argLastExceptionTime) {
	}

	/**
	 * Copies this QI into the given QI.
	 *
	 * @param qi a <code>QueueInstrumentor</code> value
	 * @return a <code>QueueInstrumentor</code> value
	 */
	public void get( QueueInstrumentor qi ) {
		if ( qi != null ) {
		}
	}

	public String getToStringHeader( boolean showUserData, boolean showPrivateFlag ) {
		return "name," + (showPrivateFlag ? "private," : "") + "enqueued,dequeued,flushed,overlaid,hwm,size,status,overallhwm,overallhwmtime,enqwaits,deqwaits,deqtos" + (showUserData ? ",userdata" : "");
	}

    public String toString(boolean showUserData, boolean showPrivateFlag, String instNameToUse, Object hwmKeeper ) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
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
			dateFormatter.format( new Date( getOverallHighWaterMarkTime() ) ) + "," +
			getEnqueueWaits() + "," +
			getDequeueWaits() + "," +
			getDequeueTimeouts() + "," +
            userDataStr;
    }
    
    public String toString(boolean showUserData, boolean showPrivateFlag, String instNameToUse) {
		return toString( showUserData, showPrivateFlag, instNameToUse, null );
	}

	public String toString() {
		return toString( true, true, getName() );
	}

} // QueueInstrumentorAggregatedImpl
