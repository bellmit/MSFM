package com.cboe.instrumentationService.impls;

import java.util.concurrent.atomic.AtomicLong;

import com.cboe.instrumentationService.instrumentors.CountInstrumentor;
import com.cboe.instrumentationService.factories.InstrumentorFactory;

/**
 * CountInstrumentorImpl.java
 *
 *
 * Created: Mon Oct  6 14:27:22 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class CountInstrumentorImpl implements CountInstrumentor {

	private String name;
	private Object userData;
	private Object lock;
	private AtomicLong count = new AtomicLong();
	private byte[] key = null;
	private boolean privateMode = false;
	private InstrumentorFactory factory = null;

	public CountInstrumentorImpl( String name, Object userData ) {
		this.name = name;
		this.userData = userData;
		lock = this; // Not really used anymore.
	} // CountInstrumentorImpl constructor

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
	 * @param argUserData Value to assign to this.userData
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
	 * Gets the value of count
	 *
	 * @return the value of count
	 */
	public long getCount()  {
		return count.get();
	}

	/**
	 * Sets the value of count
	 *
	 * @param argCount Value to assign to this.count
	 */
	public void setCount(long argCount) {
		count.set( argCount );
	}

	/**
	 * Increments the value of count
	 *
	 * @param argCount Value to increment this.count
	 */
	public void incCount(long argCount) {
		count.addAndGet( argCount );
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

	public String getToStringHeader( boolean showUserData, boolean showPrivateFlag ) {
		return "name," + (showPrivateFlag ? "private," : "") + "count" + (showUserData ? ",userdata" : "");
	}

    public String toString(boolean showUserData, boolean showPrivateFlag, String instNameToUse) {
        String userDataStr = "";
        if (showUserData) {
            userDataStr = "," + getUserData();
        }
        return instNameToUse + "," +
			(showPrivateFlag ? (isPrivate() + ",") : "") +
            getCount() + 
            userDataStr;
    }

    public String toString() {
		return toString( true, true, getName() );
    }
} // CountInstrumentorImpl
