package com.cboe.instrumentationService.impls;

import java.util.concurrent.atomic.AtomicLong;

import com.cboe.instrumentationService.factories.InstrumentorFactory;
import com.cboe.instrumentationService.instrumentors.NetworkConnectionInstrumentor;

/**
 * NetworkConnectionInstrumentorImpl.java
 *
 *
 * Created: Wed Sep  3 15:12:02 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class NetworkConnectionInstrumentorImpl implements NetworkConnectionInstrumentor {

	private String name;
	private Object userData;
	private Object lock;
	private AtomicLong bytesSent = new AtomicLong();
	private AtomicLong bytesReceived = new AtomicLong();
	private AtomicLong msgsSent = new AtomicLong();
	private AtomicLong msgsReceived = new AtomicLong();
	private AtomicLong packetsSent = new AtomicLong();
	private AtomicLong packetsReceived = new AtomicLong();
	private AtomicLong invalidPacketsReceived = new AtomicLong();
	private AtomicLong garbageBytesReceived = new AtomicLong();
	private AtomicLong connects = new AtomicLong();
	private AtomicLong disconnects = new AtomicLong();
	private AtomicLong exceptions = new AtomicLong();
	private AtomicLong lastTimeSent = new AtomicLong();
	private AtomicLong lastTimeReceived = new AtomicLong();
	private AtomicLong lastConnectTime = new AtomicLong();
	private AtomicLong lastDisconnectTime = new AtomicLong();
	private AtomicLong lastExceptionTime = new AtomicLong();
	private Throwable lastException = null;
	private AtomicLong status = new AtomicLong();
	private byte[] key = null;
	private boolean privateMode = false;
	private InstrumentorFactory factory = null;

	public NetworkConnectionInstrumentorImpl( String name, Object userData ) {
		this.name = name;
		this.userData = userData;
		lock = this; // Not really used anymore.
	} // NetworkConnectionInstrumentorImpl constructor

	public void setLockObject( Object newLockObject ) {
		lock = newLockObject; // Not really used anymore.
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
	 * Gets the value of bytesSent
	 *
	 * @return the value of bytesSent
	 */
	public long getBytesSent()  {
		return bytesSent.get();
	}

	/**
	 * Sets the value of bytesSent
	 *
	 * @param argBytesSent Value to assign to this.bytesSent
	 */
	public void setBytesSent(long argBytesSent) {
		bytesSent.set( argBytesSent );
	}

	/**
	 * Increments the value of bytesSent
	 *
	 * @param incAmount Value to increment this.bytesSent
	 */
	public void incBytesSent(long incAmount) {
		bytesSent.addAndGet( incAmount );
	}

	/**
	 * Gets the value of bytesReceived
	 *
	 * @return the value of bytesReceived
	 */
	public long getBytesReceived()  {
		return bytesReceived.get();
	}

	/**
	 * Sets the value of bytesReceived
	 *
	 * @param argBytesReceived Value to assign to this.bytesReceived
	 */
	public void setBytesReceived(long argBytesReceived) {
		bytesReceived.set( argBytesReceived );
	}

	/**
	 * Increments the value of bytesReceived
	 *
	 * @param incAmount Value to increment this.bytesReceived
	 */
	public void incBytesReceived(long incAmount) {
		bytesReceived.addAndGet( incAmount );
	}

	/**
	 * Gets the value of msgsSent
	 *
	 * @return the value of msgsSent
	 */
	public long getMsgsSent()  {
		return msgsSent.get();
	}

	/**
	 * Sets the value of msgsSent
	 *
	 * @param argMsgsSent Value to assign to this.msgsSent
	 */
	public void setMsgsSent(long argMsgsSent) {
		msgsSent.set( argMsgsSent );
	}

	/**
	 * Increments the value of msgsSent
	 *
	 * @param incAmount Value to increment this.msgsSent
	 */
	public void incMsgsSent(long incAmount) {
		msgsSent.addAndGet( incAmount );
	}

	/**
	 * Gets the value of msgsReceived
	 *
	 * @return the value of msgsReceived
	 */
	public long getMsgsReceived()  {
		return msgsReceived.get();
	}

	/**
	 * Sets the value of msgsReceived
	 *
	 * @param argMsgsReceived Value to assign to this.msgsReceived
	 */
	public void setMsgsReceived(long argMsgsReceived) {
		msgsReceived.set( argMsgsReceived );
	}

	/**
	 * Increments the value of msgsReceived
	 *
	 * @param incAmount Value to increment this.msgsReceived
	 */
	public void incMsgsReceived(long incAmount) {
		msgsReceived.addAndGet( incAmount );
	}

	/**
	 * Gets the value of packetsSent
	 *
	 * @return the value of packetsSent
	 */
	public long getPacketsSent()  {
		return packetsSent.get();
	}

	/**
	 * Sets the value of packetsSent
	 *
	 * @param argPacketsSent Value to assign to this.packetsSent
	 */
	public void setPacketsSent(long argPacketsSent) {
		packetsSent.set( argPacketsSent );
	}

	/**
	 * Increments the value of packetsSent
	 *
	 * @param incAmount Value to increment this.packetsSent
	 */
	public void incPacketsSent(long incAmount) {
		packetsSent.addAndGet( incAmount );
	}

	/**
	 * Gets the value of packetsReceived
	 *
	 * @return the value of packetsReceived
	 */
	public long getPacketsReceived()  {
		return packetsReceived.get();
	}

	/**
	 * Sets the value of packetsReceived
	 *
	 * @param argPacketsReceived Value to assign to this.packetsReceived
	 */
	public void setPacketsReceived(long argPacketsReceived) {
		packetsReceived.set( argPacketsReceived );
	}

	/**
	 * Increments the value of packetsReceived
	 *
	 * @param incAmount Value to increment this.packetsReceived
	 */
	public void incPacketsReceived(long incAmount) {
		packetsReceived.addAndGet( incAmount );
	}

	/**
	 * Gets the value of invalidPacketsReceived
	 *
	 * @return the value of invalidPacketsReceived
	 */
	public long getInvalidPacketsReceived()  {
		return invalidPacketsReceived.get();
	}

	/**
	 * Sets the value of invalidPacketsReceived
	 *
	 * @param argInvalidPacketsReceived Value to assign to this.invalidPacketsReceived
	 */
	public void setInvalidPacketsReceived(long argInvalidPacketsReceived) {
		invalidPacketsReceived.set( argInvalidPacketsReceived );
	}

	/**
	 * Increments the value of invalidPacketsReceived
	 *
	 * @param incAmount Value to assign to this.invalidPacketsReceived
	 */
	public void incInvalidPacketsReceived(long incAmount) {
		invalidPacketsReceived.addAndGet( incAmount );
	}

	/**
	 * Gets the value of garbageBytesReceived
	 *
	 * @return the value of garbageBytesReceived
	 */
	public long getGarbageBytesReceived()  {
		return garbageBytesReceived.get();
	}

	/**
	 * Sets the value of garbageBytesReceived
	 *
	 * @param argGarbageBytesReceived Value to assign to this.garbageBytesReceived
	 */
	public void setGarbageBytesReceived(long argGarbageBytesReceived) {
		garbageBytesReceived.set( argGarbageBytesReceived );
	}

	/**
	 * Increments the value of garbageBytesReceived
	 *
	 * @param incAmount Value to assign to this.garbageBytesReceived
	 */
	public void incGarbageBytesReceived(long incAmount) {
		garbageBytesReceived.addAndGet( incAmount );
	}

	/**
	 * Gets the value of lastTimeSent
	 *
	 * @return the value of lastTimeSent
	 */
	public long getLastTimeSent()  {
		return lastTimeSent.get();
	}

	/**
	 * Sets the value of lastTimeSent
	 *
	 * @param argLastTimeSent Value to assign to this.lastTimeSent
	 */
	public void setLastTimeSent(long argLastTimeSent) {
		lastTimeSent.set( argLastTimeSent );
	}

	/**
	 * Gets the value of lastTimeReceived
	 *
	 * @return the value of lastTimeReceived
	 */
	public long getLastTimeReceived()  {
		return lastTimeReceived.get();
	}

	/**
	 * Sets the value of lastTimeReceived
	 *
	 * @param argLastTimeReceived Value to assign to this.lastTimeReceived
	 */
	public void setLastTimeReceived(long argLastTimeReceived) {
		lastTimeReceived.set( argLastTimeReceived );
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
		status.set( (long)argStatus );
	}


	/**
	 * Gets the value of connects
	 *
	 * @return the value of connects
	 */
	public long getConnects()  {
		return connects.get();
	}

	/**
	 * Sets the value of connects
	 *
	 * @param argConnects Value to assign to this.connects
	 */
	public void setConnects(long argConnects) {
		connects.set( argConnects );
	}

	/**
	 * Increments the value of connects
	 *
	 * @param argConnects Value to increment this.connects
	 */
	public void incConnects(long argConnects) {
		connects.addAndGet( argConnects );
	}

	/**
	 * Gets the value of disconnects
	 *
	 * @return the value of disconnects
	 */
	public long getDisconnects()  {
		return disconnects.get();
	}

	/**
	 * Sets the value of disconnects
	 *
	 * @param argDisconnects Value to assign to this.disconnects
	 */
	public void setDisconnects(long argDisconnects) {
		disconnects.set( argDisconnects );
	}

	/**
	 * Increments the value of disconnects
	 *
	 * @param argDisconnects Value to increment this.disconnects
	 */
	public void incDisconnects(long argDisconnects) {
		disconnects.addAndGet( argDisconnects );
	}

	/**
	 * Gets the value of exceptions
	 *
	 * @return the value of exceptions
	 */
	public long getExceptions()  {
		return exceptions.get();
	}

	/**
	 * Sets the value of exceptions
	 *
	 * @param argExceptions Value to assign to this.exceptions
	 */
	public void setExceptions(long argExceptions) {
		exceptions.set( argExceptions );
	}

	/**
	 * Increments the value of exceptions
	 *
	 * @param argExceptions Value to increment this.exceptions
	 */
	public void incExceptions(long argExceptions) {
		exceptions.addAndGet( argExceptions );
	}

	/**
	 * Gets the value of lastConnectTime
	 *
	 * @return the value of lastConnectTime
	 */
	public long getLastConnectTime()  {
		return lastConnectTime.get();
	}

	/**
	 * Sets the value of lastConnectTime
	 *
	 * @param argLastConnectTime Value to assign to this.lastConnectTime
	 */
	public void setLastConnectTime(long argLastConnectTime) {
		lastConnectTime.set( argLastConnectTime );
	}

	/**
	 * Gets the value of lastDisconnectTime
	 *
	 * @return the value of lastDisconnectTime
	 */
	public long getLastDisconnectTime()  {
		return lastDisconnectTime.get();
	}

	/**
	 * Sets the value of lastDisconnectTime
	 *
	 * @param argLastDisconnectTime Value to assign to this.lastDisconnectTime
	 */
	public void setLastDisconnectTime(long argLastDisconnectTime) {
		lastDisconnectTime.set( argLastDisconnectTime );
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
		lastExceptionTime.set( argLastExceptionTime );
	}

	/**
	 * Gets the value of lastException
	 *
	 * @return the value of lastException
	 */
	public Throwable getLastException()  {
		synchronized( lock ) {
			return this.lastException;
		}
	}

	/**
	 * Sets the value of lastException
	 *
	 * @param argLastException Value to assign to this.lastException
	 */
	public void setLastException(Throwable argLastException) {
		synchronized( lock ) {
			this.lastException = argLastException;
		}
	}
	/**
	 * Copies this NCI to the given NCI.
	 *
	 * @param nci a <code>NetworkConnectionInstrumentor</code> value
	 */
	public void get( NetworkConnectionInstrumentor nci ) {
		if ( nci != null ) {
			nci.setBytesSent( bytesSent.get() );
			nci.setBytesReceived( bytesReceived.get() );
			nci.setMsgsSent( msgsSent.get() );
			nci.setMsgsReceived( msgsReceived.get() );
			nci.setPacketsSent( packetsSent.get() );
			nci.setPacketsReceived( packetsReceived.get() );
			nci.setInvalidPacketsReceived( invalidPacketsReceived.get() );
			nci.setGarbageBytesReceived( garbageBytesReceived.get() );
			nci.setConnects( connects.get() );
			nci.setDisconnects( disconnects.get() );
			nci.setExceptions( exceptions.get() );
			nci.setLastTimeSent( lastTimeSent.get() );
			nci.setLastTimeReceived( lastTimeReceived.get() );
			nci.setLastConnectTime( lastConnectTime.get() );
			nci.setLastDisconnectTime( lastDisconnectTime.get() );
			nci.setLastExceptionTime( lastExceptionTime.get() );
			nci.setLastException( lastException );
			nci.setStatus( (short)status.get() );
		}
	}

	public String getToStringHeader( boolean showUserData, boolean showPrivateFlag ) {
		return "name," + (showPrivateFlag ? "private," : "") + "bsent,brecv,msent,mrecv,psent,precv,invprecv,gbrecv,conns,disconns,excepts,lastsent,lastrecv,lastconn,lastdisconn,lastexcept,lastexceptstr,status" + (showUserData ? ",userdata" : "");
	}

    public String toString(boolean showUserData, boolean showPrivateFlag, String instNameToUse) {
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
			(getLastTimeSent() > 0 ? InstrumentorTimeFormatter.format( getLastTimeSent()  ): "0") + "," +
			(getLastTimeReceived() > 0 ? InstrumentorTimeFormatter.format( getLastTimeReceived() )  : "0") + "," +
			(getLastConnectTime() > 0 ? InstrumentorTimeFormatter.format( getLastConnectTime() )  : "0") + "," +
			(getLastDisconnectTime() > 0 ? InstrumentorTimeFormatter.format( getLastDisconnectTime()  ) : "0") + "," +
			(getLastExceptionTime() > 0 ? InstrumentorTimeFormatter.format( getLastExceptionTime()  ) : "0") + "," +
            lastExceptionStr + "," +
            getStatus() + 
            userDataStr;

    }
    
	public String toString() {
		return toString( true, true, getName() );
	}

} // NetworkConnectionInstrumentorImpl
