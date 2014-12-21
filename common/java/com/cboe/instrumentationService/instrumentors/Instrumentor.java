package com.cboe.instrumentationService.instrumentors;

import com.cboe.instrumentationService.factories.InstrumentorFactory;

/**
 * Instrumentor.java
 *
 *
 * Created: Thu Jul 24 07:36:22 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface Instrumentor {

	// Common delimiter used when constructing
	// an instrumentor name.
	public static final String NAME_DELIMITER = "/";
	// Common delimiter which can be used for
	// user data contents.
	public static final String USER_DATA_DELIMITER = "\u0001";

	public void setUserData( Object newUserData );
	public Object getUserData();
	public String getName();
	public void setKey( byte[] key );
	public byte[] getKey();
	public void setPrivate( boolean newValue );
	public boolean isPrivate();
	public void setLockObject( Object lock );
	public void setFactory( InstrumentorFactory factory );
	public InstrumentorFactory getFactory();

	// This method should be used with care.  Renaming an
	// instrumentor might mean it will have a different name
	// than it did when it was registered with a factory.  If
	// the instrumentor is not unregistered first before renaming,
	// then it can not be unregistered.
	public void rename( String newName );

	public String getToStringHeader( boolean showUserData, boolean showPrivateFlag );

	public String toString( boolean showUserData, boolean showPrivateFlag, String instrName );

} // Instrumentor
