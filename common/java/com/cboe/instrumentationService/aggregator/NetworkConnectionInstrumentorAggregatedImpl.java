package com.cboe.instrumentationService.aggregator;

import java.util.*;
import java.text.SimpleDateFormat;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.instrumentors.NetworkConnectionInstrumentor;
import com.cboe.instrumentationService.instrumentors.AggregatedNetworkConnectionInstrumentor;
import com.cboe.instrumentationService.factories.InstrumentorFactory;
import com.cboe.instrumentationService.factories.NetworkConnectionInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorFactoryVisitor;
import com.cboe.instrumentationService.factories.NetworkConnectionInstrumentorFactoryVisitor;

/**
 * NetworkConnectionInstrumentorAggregatedImpl.java
 *
 *
 * Created: Wed Sep  3 15:12:02 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class NetworkConnectionInstrumentorAggregatedImpl implements NetworkConnectionInstrumentor, AggregatedNetworkConnectionInstrumentor {

	private String name;
	private Object userData;
	private byte[] key = null;
	private boolean privateMode = false;
	private ArrayList instrumentors = new ArrayList();
	private InstrumentorFactory factory = null;

	public NetworkConnectionInstrumentorAggregatedImpl( String name, Object userData ) {
		this.name = name;
		this.userData = userData;
	} // NetworkConnectionInstrumentorAggregatedImpl constructor

	public void setLockObject( Object newLockObject ) {
	}

	public synchronized void addInstrumentor( NetworkConnectionInstrumentor nci ) {
		if ( instrumentors.indexOf( nci ) < 0 ) {
			instrumentors.add( nci );
		}
	}

	public synchronized void removeInstrumentor( NetworkConnectionInstrumentor nci ) {
		instrumentors.remove( nci );
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
			NetworkConnectionInstrumentor inst = (NetworkConnectionInstrumentor)iter.next();
			NetworkConnectionInstrumentorFactory ciFactory = (NetworkConnectionInstrumentorFactory)inst.getFactory();
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
			NetworkConnectionInstrumentor inst = (NetworkConnectionInstrumentor)iter.next();
			if ( !((NetworkConnectionInstrumentorFactoryVisitor)visitor).visit( inst ) ) {
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
	 * Gets the value of bytesSent
	 *
	 * @return the value of bytesSent
	 */
	public synchronized long getBytesSent()  {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((NetworkConnectionInstrumentor)iter.next()).getBytesSent();
		}
		return total;
	}

	/**
	 * Sets the value of bytesSent
	 *
	 * @param argBytesSent Value to assign to this.bytesSent
	 */
	public void setBytesSent(long argBytesSent) {
	}

	/**
	 * Increments the value of bytesSent
	 *
	 * @param incAmount Value to increment this.bytesSent
	 */
	public void incBytesSent(long incAmount) {
	}

	/**
	 * Gets the value of bytesReceived
	 *
	 * @return the value of bytesReceived
	 */
	public synchronized long getBytesReceived()  {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((NetworkConnectionInstrumentor)iter.next()).getBytesReceived();
		}
		return total;
	}

	/**
	 * Sets the value of bytesReceived
	 *
	 * @param argBytesReceived Value to assign to this.bytesReceived
	 */
	public void setBytesReceived(long argBytesReceived) {
	}

	/**
	 * Increments the value of bytesReceived
	 *
	 * @param incAmount Value to increment this.bytesReceived
	 */
	public void incBytesReceived(long incAmount) {
	}

	/**
	 * Gets the value of msgsSent
	 *
	 * @return the value of msgsSent
	 */
	public synchronized long getMsgsSent()  {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((NetworkConnectionInstrumentor)iter.next()).getMsgsSent();
		}
		return total;
	}

	/**
	 * Sets the value of msgsSent
	 *
	 * @param argMsgsSent Value to assign to this.msgsSent
	 */
	public void setMsgsSent(long argMsgsSent) {
	}

	/**
	 * Increments the value of msgsSent
	 *
	 * @param incAmount Value to increment this.msgsSent
	 */
	public void incMsgsSent(long incAmount) {
	}

	/**
	 * Gets the value of msgsReceived
	 *
	 * @return the value of msgsReceived
	 */
	public synchronized long getMsgsReceived()  {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((NetworkConnectionInstrumentor)iter.next()).getMsgsReceived();
		}
		return total;
	}

	/**
	 * Sets the value of msgsReceived
	 *
	 * @param argMsgsReceived Value to assign to this.msgsReceived
	 */
	public void setMsgsReceived(long argMsgsReceived) {
	}

	/**
	 * Increments the value of msgsReceived
	 *
	 * @param incAmount Value to increment this.msgsReceived
	 */
	public void incMsgsReceived(long incAmount) {
	}

	/**
	 * Gets the value of packetsSent
	 *
	 * @return the value of packetsSent
	 */
	public synchronized long getPacketsSent()  {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((NetworkConnectionInstrumentor)iter.next()).getPacketsSent();
		}
		return total;
	}

	/**
	 * Sets the value of packetsSent
	 *
	 * @param argPacketsSent Value to assign to this.packetsSent
	 */
	public void setPacketsSent(long argPacketsSent) {
	}

	/**
	 * Increments the value of packetsSent
	 *
	 * @param incAmount Value to increment this.packetsSent
	 */
	public void incPacketsSent(long incAmount) {
	}

	/**
	 * Gets the value of packetsReceived
	 *
	 * @return the value of packetsReceived
	 */
	public synchronized long getPacketsReceived()  {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((NetworkConnectionInstrumentor)iter.next()).getPacketsReceived();
		}
		return total;
	}

	/**
	 * Sets the value of packetsReceived
	 *
	 * @param argPacketsReceived Value to assign to this.packetsReceived
	 */
	public void setPacketsReceived(long argPacketsReceived) {
	}

	/**
	 * Increments the value of packetsReceived
	 *
	 * @param incAmount Value to increment this.packetsReceived
	 */
	public void incPacketsReceived(long incAmount) {
	}

	/**
	 * Gets the value of invalidPacketsReceived
	 *
	 * @return the value of invalidPacketsReceived
	 */
	public synchronized long getInvalidPacketsReceived()  {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((NetworkConnectionInstrumentor)iter.next()).getInvalidPacketsReceived();
		}
		return total;
	}

	/**
	 * Sets the value of invalidPacketsReceived
	 *
	 * @param argInvalidPacketsReceived Value to assign to this.invalidPacketsReceived
	 */
	public void setInvalidPacketsReceived(long argInvalidPacketsReceived) {
	}

	/**
	 * Increments the value of invalidPacketsReceived
	 *
	 * @param incAmount Value to assign to this.invalidPacketsReceived
	 */
	public void incInvalidPacketsReceived(long incAmount) {
	}

	/**
	 * Gets the value of garbageBytesReceived
	 *
	 * @return the value of garbageBytesReceived
	 */
	public synchronized long getGarbageBytesReceived()  {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((NetworkConnectionInstrumentor)iter.next()).getGarbageBytesReceived();
		}
		return total;
	}

	/**
	 * Sets the value of garbageBytesReceived
	 *
	 * @param argGarbageBytesReceived Value to assign to this.garbageBytesReceived
	 */
	public void setGarbageBytesReceived(long argGarbageBytesReceived) {
	}

	/**
	 * Increments the value of garbageBytesReceived
	 *
	 * @param incAmount Value to assign to this.garbageBytesReceived
	 */
	public void incGarbageBytesReceived(long incAmount) {
	}

	/**
	 * Gets the value of lastTimeSent
	 *
	 * @return the value of lastTimeSent
	 */
	public synchronized long getLastTimeSent()  {
		long lastTime = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			long thisTime = ((NetworkConnectionInstrumentor)iter.next()).getLastTimeSent();
			if ( thisTime > lastTime ) {
				lastTime = thisTime;
			}
		}
		return lastTime;
	}

	/**
	 * Sets the value of lastTimeSent
	 *
	 * @param argLastTimeSent Value to assign to this.lastTimeSent
	 */
	public void setLastTimeSent(long argLastTimeSent) {
	}

	/**
	 * Gets the value of lastTimeReceived
	 *
	 * @return the value of lastTimeReceived
	 */
	public synchronized long getLastTimeReceived()  {
		long lastTime = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			long thisTime = ((NetworkConnectionInstrumentor)iter.next()).getLastTimeReceived();
			if ( thisTime > lastTime ) {
				lastTime = thisTime;
			}
		}
		return lastTime;
	}

	/**
	 * Sets the value of lastTimeReceived
	 *
	 * @param argLastTimeReceived Value to assign to this.lastTimeReceived
	 */
	public void setLastTimeReceived(long argLastTimeReceived) {
	}

	/**
	 * Gets the value of status
	 *
	 * @return the value of status
	 */
	public short getStatus()  {
		return 0; // Doesn't make sense for aggregate.
	}

	/**
	 * Sets the value of status
	 *
	 * @param argStatus Value to assign to this.status
	 */
	public void setStatus(short argStatus) {
	}


	/**
	 * Gets the value of connects
	 *
	 * @return the value of connects
	 */
	public synchronized long getConnects()  {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((NetworkConnectionInstrumentor)iter.next()).getConnects();
		}
		return total;
	}

	/**
	 * Sets the value of connects
	 *
	 * @param argConnects Value to assign to this.connects
	 */
	public void setConnects(long argConnects) {
	}

	/**
	 * Increments the value of connects
	 *
	 * @param argConnects Value to increment this.connects
	 */
	public void incConnects(long argConnects) {
	}

	/**
	 * Gets the value of disconnects
	 *
	 * @return the value of disconnects
	 */
	public synchronized long getDisconnects()  {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((NetworkConnectionInstrumentor)iter.next()).getDisconnects();
		}
		return total;
	}

	/**
	 * Sets the value of disconnects
	 *
	 * @param argDisconnects Value to assign to this.disconnects
	 */
	public void setDisconnects(long argDisconnects) {
	}

	/**
	 * Increments the value of disconnects
	 *
	 * @param argDisconnects Value to increment this.disconnects
	 */
	public void incDisconnects(long argDisconnects) {
	}

	/**
	 * Gets the value of exceptions
	 *
	 * @return the value of exceptions
	 */
	public synchronized long getExceptions()  {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((NetworkConnectionInstrumentor)iter.next()).getExceptions();
		}
		return total;
	}

	/**
	 * Sets the value of exceptions
	 *
	 * @param argExceptions Value to assign to this.exceptions
	 */
	public void setExceptions(long argExceptions) {
	}

	/**
	 * Increments the value of exceptions
	 *
	 * @param argExceptions Value to increment this.exceptions
	 */
	public void incExceptions(long argExceptions) {
	}

	/**
	 * Gets the value of lastConnectTime
	 *
	 * @return the value of lastConnectTime
	 */
	public synchronized long getLastConnectTime()  {
		long lastTime = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			long thisTime = ((NetworkConnectionInstrumentor)iter.next()).getLastConnectTime();
			if ( thisTime > lastTime ) {
				lastTime = thisTime;
			}
		}
		return lastTime;
	}

	/**
	 * Sets the value of lastConnectTime
	 *
	 * @param argLastConnectTime Value to assign to this.lastConnectTime
	 */
	public void setLastConnectTime(long argLastConnectTime) {
	}

	/**
	 * Gets the value of lastDisconnectTime
	 *
	 * @return the value of lastDisconnectTime
	 */
	public synchronized long getLastDisconnectTime()  {
		long lastTime = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			long thisTime = ((NetworkConnectionInstrumentor)iter.next()).getLastDisconnectTime();
			if ( thisTime > lastTime ) {
				lastTime = thisTime;
			}
		}
		return lastTime;
	}

	/**
	 * Sets the value of lastDisconnectTime
	 *
	 * @param argLastDisconnectTime Value to assign to this.lastDisconnectTime
	 */
	public void setLastDisconnectTime(long argLastDisconnectTime) {
	}

	/**
	 * Gets the value of lastExceptionTime
	 *
	 * @return the value of lastExceptionTime
	 */
	public synchronized long getLastExceptionTime()  {
		long lastTime = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			long thisTime = ((NetworkConnectionInstrumentor)iter.next()).getLastExceptionTime();
			if ( thisTime > lastTime ) {
				lastTime = thisTime;
			}
		}
		return lastTime;
	}

	/**
	 * Sets the value of lastExceptionTime
	 *
	 * @param argLastExceptionTime Value to assign to this.lastExceptionTime
	 */
	public void setLastExceptionTime(long argLastExceptionTime) {
	}

	/**
	 * Gets the value of lastException
	 *
	 * @return the value of lastException
	 */
	public Throwable getLastException()  {
		return null; // Doesn't make sense for aggregate.
	}

	/**
	 * Sets the value of lastException
	 *
	 * @param argLastException Value to assign to this.lastException
	 */
	public void setLastException(Throwable argLastException) {
	}
	/**
	 * Copies this NCI to the given NCI.
	 *
	 * @param nci a <code>NetworkConnectionInstrumentor</code> value
	 */
	public void get( NetworkConnectionInstrumentor nci ) {
		if ( nci != null ) {
		}
	}

	public String getToStringHeader( boolean showUserData, boolean showPrivateFlag ) {
		return "name," + (showPrivateFlag ? "private," : "") + "bsent,brecv,msent,mrecv,psent,precv,invprecv,gbrecv,conns,disconns,excepts,lastsent,lastrecv,lastconn,lastdisconn,lastexcept,lastexceptstr,status" + (showUserData ? ",userdata" : "");
	}

    public String toString(boolean showUserData, boolean showPrivateFlag, String instNameToUse) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
        String userDataStr = "";
        if ( showUserData ) {
            userDataStr = "," + getUserData();
        }
        String lastExceptionStr = "";
        if ( getLastException() != null ) {
            lastExceptionStr = getLastException().toString();
        }
        return instNameToUse + "," +
			(showPrivateFlag ? (isPrivate() + ",") : "") +
            getBytesSent() + "," +
            getBytesReceived() + "," +
            getMsgsSent() + "," +
            getMsgsReceived() + "," +
            getPacketsSent() + "," +
            getPacketsReceived() + "," +
            getInvalidPacketsReceived() + "," +
            getGarbageBytesReceived() + "," +
            getConnects() + "," +
            getDisconnects() + "," +
            getExceptions() + "," +
			dateFormatter.format( new Date( getLastTimeSent() ) ) + "," +
			dateFormatter.format( new Date( getLastTimeReceived() ) ) + "," +
			dateFormatter.format( new Date( getLastConnectTime() ) ) + "," +
			dateFormatter.format( new Date( getLastDisconnectTime() ) ) + "," +
			dateFormatter.format( new Date( getLastExceptionTime() ) ) + "," +
            lastExceptionStr + "," +
            getStatus() + 
            userDataStr;

    }
    
	public String toString() {
		return toString( true, true, getName() );
	}

} // NetworkConnectionInstrumentorAggregatedImpl
