package com.cboe.instrumentationService.impls;

import com.cboe.instrumentationService.instrumentors.HeapInstrumentor;
import com.cboe.instrumentationService.factories.InstrumentorFactory;

/**
 * HeapInstrumentorImpl.java
 *
 *
 * Created: Wed Sep  3 14:52:17 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class HeapInstrumentorImpl implements HeapInstrumentor {

	private String name;
	private Object userData;
	private long maxMemory = 0;
	private long totalMemory = 0;
	private long freeMemory = 0;
	private byte[] key = null;
	private boolean privateMode = false;
	private InstrumentorFactory factory = null;

	public HeapInstrumentorImpl( String name, Object userData ) {
		this.name = name;
		this.userData = userData;
	} // HeapInstrumentorImpl constructor


	public void setLockObject( Object newLockObject ) {
        // this class has no lock object
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
	 * Gets the value of maxMemory
	 *
	 * @return the value of maxMemory
	 */
	public long getMaxMemory()  {
		return this.maxMemory;
	}

	/**
	 * Sets the value of maxMemory
	 *
	 * @param argMaxMemory Value to assign to this.maxMemory
	 */
	public void setMaxMemory(long argMaxMemory) {
		this.maxMemory = argMaxMemory;
	}

	/**
	 * Gets the value of totalMemory
	 *
	 * @return the value of totalMemory
	 */
	public long getTotalMemory()  {
		return this.totalMemory;
	}

	/**
	 * Sets the value of totalMemory
	 *
	 * @param argTotalMemory Value to assign to this.totalMemory
	 */
	public void setTotalMemory(long argTotalMemory) {
		this.totalMemory = argTotalMemory;
	}

	/**
	 * Gets the value of freeMemory
	 *
	 * @return the value of freeMemory
	 */
	public long getFreeMemory()  {
		return this.freeMemory;
	}

	/**
	 * Sets the value of freeMemory
	 *
	 * @param argFreeMemory Value to assign to this.freeMemory
	 */
	public void setFreeMemory(long argFreeMemory) {
		this.freeMemory = argFreeMemory;
	}

	/**
	 * Copies this HI to the given HI.
	 *
	 * @param hi a <code>HeapInstrumentor</code> value
	 */
	public void get( HeapInstrumentor hi ) {
		if ( hi != null ) {
			hi.setMaxMemory( maxMemory );
			hi.setTotalMemory( totalMemory );
			hi.setFreeMemory( freeMemory );
		}
	}

	public String getToStringHeader( boolean showUserData, boolean showPrivateFlag ) {
		return "name," + (showPrivateFlag ? "private," : "") + "maxmem,totalmem,freemem" + (showUserData ? ",userdata" : "");
	}

    public String toString(boolean showUserData, boolean showPrivateFlag, String instNameToUse) {
        String userDataStr = "";
        if ( showUserData ) {
            userDataStr = "," + getUserData();
        }
        return instNameToUse + "," +
			(showPrivateFlag ? (isPrivate() + ",") : "") +
            getMaxMemory() + "," +
            getTotalMemory() + "," +
            getFreeMemory() + 
            userDataStr;
    }

	public String toString() {
		return toString( true, true, getName() );
	}

} // HeapInstrumentorImpl
