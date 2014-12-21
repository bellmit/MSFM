package com.cboe.instrumentationService.impls;

import java.util.concurrent.atomic.AtomicLong;

import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.factories.InstrumentorFactory;

/**
 * MethodInstrumentorImpl.java
 *
 *
 * Created: Tue Sep  9 14:05:59 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class MethodInstrumentorImpl implements MethodInstrumentor {

	private String name;
	private Object userData;
	private Object lock;
	private AtomicLong calls = new AtomicLong();
	private AtomicLong exceptions = new AtomicLong();
	private AtomicLong methodTime = new AtomicLong();
	private AtomicLong sumOfSquareMethodTime = new AtomicLong();
	private AtomicLong maxMethodTime = new AtomicLong();
	private byte[] key = null;
	private ThreadLocal<LongHolder> beforeMethodCallTimeTL = new ThreadLocal<LongHolder>();
	private boolean privateMode = false;
	private InstrumentorFactory factory = null;

	public MethodInstrumentorImpl( String name, Object userData ) {
		this.name = name;
		this.userData = userData;
		lock = this; // Not really used anymore.
	} // MethodInstrumentorImpl constructor


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
	 * Gets the value of calls
	 *
	 * @return the value of calls
	 */
	public long getCalls()  {
		return calls.get();
	}

	/**
	 * Sets the value of calls
	 *
	 * @param argCalls Value to assign to this.calls
	 */
	public void setCalls(long argCalls) {
		calls.set( argCalls );
	}

	/**
	 * Increments the value of calls
	 *
	 * @param argCalls Value to increment this.calls
	 */
	public void incCalls(long argCalls) {
		calls.addAndGet( argCalls );
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
	 * Marks the point in time before a method is called.
	 * Convenience method to handle the timings.
	 *
	 */
	public void beforeMethodCall() {
		LongHolder beforeTime = beforeMethodCallTimeTL.get();
		if ( beforeTime == null ) {
			beforeTime = new LongHolder();
			beforeMethodCallTimeTL.set( beforeTime );
		}
		beforeTime.value = System.nanoTime();
	}

	/**
	 * To be called after beforeMethodCall.  This calls incMethodTime
	 * with the difference between now and before time.  Convenience
	 * method to handle timings.
	 *
	 */
	public void afterMethodCall() {
		afterMethodCall( null );
	}

	/**
	 * To be called after beforeMethodCall.  This calls incMethodTime
	 * with the difference between now and before time.  Convenience
	 * method to handle timings.
	 *
	 * If Throwable is null, then update the timing - this means the
	 * call was successful.  If it is not null, then don't update the
	 * times.  But - BE CAREFUL: if this method is called that way,
	 * then #calls should not be incremented if Throwable is non-null,
	 * otherwise the response time calculations would be off.
	 */
	public void afterMethodCall( Throwable t ) {
		LongHolder beforeTime = beforeMethodCallTimeTL.get();
		if ( beforeTime != null ) {
			if ( beforeTime.value != 0 ) {
				if ( t == null ) {
					incMethodTime( System.nanoTime() - beforeTime.value );
					beforeTime.value = 0;
				}
			}
		}
	}

	/**
	 * Increments the value of methodTime.
	 *
	 * @param incAmount Value to increment this.methodTime
	 */
	public void incMethodTime( long incAmount ) {
		methodTime.addAndGet( incAmount );
		
		/* convert to millisecond precision */
		long incAmountMS = incAmount / 1000000;
		sumOfSquareMethodTime.addAndGet( incAmountMS*incAmountMS );
		
		// Could be a margin of time here where making this new setting is wrong.
		// Not worrying about it...
		if ( incAmount > maxMethodTime.get() ) {
			maxMethodTime.set( incAmount );
		}
	}

	/**
	 * Sets the value of methodTime
	 *
	 * @param newAmount Value to assign to this.methodTime
	 */
	public void setMethodTime( double newAmount ) {
		methodTime.set( (long)newAmount );
	}

	/**
	 * Gets the value of methodTime
	 *
	 * @return the value of methodTime
	 */
	public double getMethodTime() {
		return (double)methodTime.get();
	}

	/**
	 * Sets the value of sumOfSquareMethodTime, the value is in nanoseconds, but will
	 * be stored in milliseconds
	 *
	 * @param newAmount Value to assign to this.sumOfSquareMethodTime
	 */
	public void setSumOfSquareMethodTime( double newAmount ) {
		/* convert to millisecond */
		newAmount = newAmount / 1000000;
		sumOfSquareMethodTime.set( (long)newAmount );
	}

	/**
	 * Gets the value of sumOfSquareMethodTime in milliseconds
	 *
	 * @return the value of sumOfSquareMethodTime in millisecond units
	 */
	public double getSumOfSquareMethodTime() {
		return (double)sumOfSquareMethodTime.get();
	}

	/**
	 * Sets the value of maxMethodTime
	 *
	 * @param newAmount Value to assign to this.maxMethodTime
	 */
	public void setMaxMethodTime( long newAmount ) {
		maxMethodTime.set( newAmount );
	}

	/**
	 * Gets the value of maxMethodTime
	 *
	 * @return the value of maxMethodTime
	 */
	public long getMaxMethodTime() {
		return maxMethodTime.get();
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
	 * Copies this MI to the given MI.
	 *
	 * @param mi a <code>MethodInstrumentor</code> value
	 */
	public void get( MethodInstrumentor mi ) {
		if ( mi != null ) {
			mi.setCalls( calls.get() );
			mi.setExceptions( exceptions.get() );
		}
	}

	public String getToStringHeader( boolean showUserData, boolean showPrivateFlag ) {
		return "name," + (showPrivateFlag ? "private," : "") + "calls,excepts,methodtime,sumsq,maxtime" + (showUserData ? ",userdata" : "");
	}

    public String toString(boolean showUserData, boolean showPrivateFlag, String instNameToUse) {
        String userDataStr = "";
        if (showUserData) {
            userDataStr = "," + getUserData();
        }
        return instNameToUse  + "," +
			(showPrivateFlag ? (isPrivate() + ",") : "") +
            getCalls() + "," +
            getExceptions() + "," +
            getMethodTime() + "," +
            getSumOfSquareMethodTime() + "," +
            getMaxMethodTime() + 
            userDataStr;
    }

	public String toString() {
		return toString( true, true, getName() );
	}

	private class LongHolder {
		public long value;

		public LongHolder() {
		}
	}

} // MethodInstrumentorImpl
