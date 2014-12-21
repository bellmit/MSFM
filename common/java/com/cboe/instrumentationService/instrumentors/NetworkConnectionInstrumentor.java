package com.cboe.instrumentationService.instrumentors;

/**
 * NetworkConnectionInstrumentor.java
 *
 *
 * Created: Thu Jul 24 07:23:54 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface NetworkConnectionInstrumentor extends Instrumentor {

	public static final String INSTRUMENTOR_TYPE_NAME = "NetworkConnectionInstrumentor";

	public void incBytesSent( long incAmount );
	public void setBytesSent( long newAmount );
	public long getBytesSent();

	public void incBytesReceived( long incAmount );
	public void setBytesReceived( long newAmount );
	public long getBytesReceived();

	public void incMsgsSent( long incAmount );
	public void setMsgsSent( long newAmount );
	public long getMsgsSent();

	public void incMsgsReceived( long incAmount );
	public void setMsgsReceived( long newAmount );
	public long getMsgsReceived();

	public void incPacketsSent( long incAmount );
	public void setPacketsSent( long newAmount );
	public long getPacketsSent();

	public void incPacketsReceived( long incAmount );
	public void setPacketsReceived( long newAmount );
	public long getPacketsReceived();

	public void incInvalidPacketsReceived( long incAmount );
	public void setInvalidPacketsReceived( long newAmount );
	public long getInvalidPacketsReceived();

	public void incGarbageBytesReceived( long incAmount );
	public void setGarbageBytesReceived( long newAmount );
	public long getGarbageBytesReceived();

	public void incConnects( long incAmount );
	public void setConnects( long newAmount );
	public long getConnects();

	public void incDisconnects( long incAmount );
	public void setDisconnects( long newAmount );
	public long getDisconnects();

	public void incExceptions( long incAmount );
	public void setExceptions( long newAmount );
	public long getExceptions();

	public void setLastTimeSent( long millis );
	public long getLastTimeSent();

	public void setLastTimeReceived( long millis );
	public long getLastTimeReceived();

	public void setLastConnectTime( long millis );
	public long getLastConnectTime();

	public void setLastDisconnectTime( long millis );
	public long getLastDisconnectTime();

	public void setLastExceptionTime( long millis );
	public long getLastExceptionTime();

	public void setLastException( Throwable lastException );
	public Throwable getLastException();

	public void setStatus( short newStatus );
	public short getStatus();

	public void get( NetworkConnectionInstrumentor nci );

} // NetworkConnectionInstrumentor
