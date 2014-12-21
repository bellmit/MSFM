package com.cboe.instrumentationService.aggregator;

import java.util.ArrayList;
import java.util.Iterator;

import com.cboe.instrumentationService.factories.InstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorFactoryVisitor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.instrumentors.AggregatedMethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;

/**
 * MethodInstrumentorAggregatedImpl.java
 *
 *
 * Created: Tue Sep  9 14:05:59 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class MethodInstrumentorAggregatedImpl
implements MethodInstrumentor, AggregatedMethodInstrumentor {

	private String name;
	private Object userData;
	private byte[] key = null;
	private boolean privateMode = false;
	private ArrayList instrumentors = new ArrayList();
	private InstrumentorFactory factory = null;

	public MethodInstrumentorAggregatedImpl( String name, Object userData ) {
		this.name = name;
		this.userData = userData;
	} // MethodInstrumentorAggregatedImpl constructor

	public void setLockObject( Object newLockObject ) {
	}

	public synchronized void addInstrumentor( MethodInstrumentor mi ) {
		if ( instrumentors.indexOf( mi ) < 0 ) {
			instrumentors.add( mi );
		}
	}

	public synchronized void removeInstrumentor( MethodInstrumentor mi ) {
		instrumentors.remove( mi );
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
			MethodInstrumentor inst = (MethodInstrumentor)iter.next();
			MethodInstrumentorFactory ciFactory = (MethodInstrumentorFactory)inst.getFactory();
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
			MethodInstrumentor inst = (MethodInstrumentor)iter.next();
			if ( !((MethodInstrumentorFactoryVisitor)visitor).visit( inst ) ) {
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

	/**
	 * Gets the value of calls
	 *
	 * @return the value of calls
	 */
	public synchronized long getCalls()  {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((MethodInstrumentor)iter.next()).getCalls();
		}
		return total;
	}

	/**
	 * Sets the value of calls
	 *
	 * @param argCalls Value to assign to this.calls
	 */
	public void setCalls(long argCalls) {
	}

	/**
	 * Increments the value of calls
	 *
	 * @param argCalls Value to increment this.calls
	 */
	public void incCalls(long argCalls) {
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
			total += ((MethodInstrumentor)iter.next()).getExceptions();
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
	 * Marks the point in time before a method is called.
	 * Convenience method to handle the timings.
	 *
	 */
	public void beforeMethodCall() {
	}

	/**
	 * To be called after beforeMethodCall.  This calls incMethodTime
	 * with the difference between now and before time.  Convenience
	 * method to handle timings.
	 *
	 */
	public void afterMethodCall() {
	}

	/**
	 * To be called after beforeMethodCall.  This calls incMethodTime
	 * with the difference between now and before time.  Convenience
	 * method to handle timings.
	 *
	 */
	public void afterMethodCall( Throwable t ) {
	}

	/**
	 * Increments the value of methodTime.
	 *
	 * @param incAmount Value to increment this.methodTime
	 */
	public void incMethodTime( long incAmount ) {
	}

	/**
	 * Sets the value of methodTime
	 *
	 * @param newAmount Value to assign to this.methodTime
	 */
	public void setMethodTime( double newAmount ) {
	}

	/**
	 * Gets the value of methodTime
	 *
	 * @return the value of methodTime
	 */
	public synchronized double getMethodTime() {
		double total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((MethodInstrumentor)iter.next()).getMethodTime();
		}
		return total;
	}

	/**
	 * Sets the value of sumOfSquareMethodTime
	 *
	 * @param newAmount Value to assign to this.sumOfSquareMethodTime
	 */
	public void setSumOfSquareMethodTime( double newAmount ) {
	}

	/**
	 * Gets the value of sumOfSquareMethodTime
	 *
	 * @return the value of sumOfSquareMethodTime
	 */
	public synchronized double getSumOfSquareMethodTime() {
		double total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((MethodInstrumentor)iter.next()).getSumOfSquareMethodTime();
		}
		return total;
	}

	/**
	 * Sets the value of maxMethodTime
	 *
	 * @param newAmount Value to assign to this.maxMethodTime
	 */
	public void setMaxMethodTime( long newAmount ) {
	}

	/**
	 * Gets the value of maxMethodTime
	 *
	 * @return the value of maxMethodTime
	 */
	public long getMaxMethodTime() {
		return 0; // Doesn't make sense for aggregate.
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

} // MethodInstrumentorAggregatedImpl
