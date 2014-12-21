package com.cboe.instrumentationService.aggregator;

import java.util.ArrayList;
import java.util.Iterator;

import com.cboe.instrumentationService.factories.CountInstrumentorFactory;
import com.cboe.instrumentationService.factories.CountInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.factories.InstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorFactoryVisitor;
import com.cboe.instrumentationService.instrumentors.AggregatedCountInstrumentor;
import com.cboe.instrumentationService.instrumentors.CountInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;

/**
 * CountInstrumentorAggregatedImpl.java
 *
 *
 * Created: Mon Oct  6 11:35:26 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class CountInstrumentorAggregatedImpl
implements CountInstrumentor, AggregatedCountInstrumentor {

	private String name;
	private Object userData;
	private byte[] key = null;
	private boolean privateMode = false;
	private ArrayList instrumentors = new ArrayList();
	private InstrumentorFactory factory = null;

	public CountInstrumentorAggregatedImpl( String name, Object userData ) {
		this.name = name;
		this.userData = userData;
	} // CountInstrumentorAggregatedImpl constructor

	public void setLockObject( Object newLockObject ) {
		// Not used in this impl.
	}

	public synchronized void addInstrumentor( CountInstrumentor ci ) {
		if ( instrumentors.indexOf( ci ) < 0 ) {
			instrumentors.add( ci );
		}
	}

	public synchronized void removeInstrumentor( CountInstrumentor ci ) {
		instrumentors.remove( ci );
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
			CountInstrumentor inst = (CountInstrumentor)iter.next();
			CountInstrumentorFactory ciFactory = (CountInstrumentorFactory)inst.getFactory();
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
			CountInstrumentor inst = (CountInstrumentor)iter.next();
			if ( !((CountInstrumentorFactoryVisitor)visitor).visit( inst ) ) {
				return;
			}
		}
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

	public void setFactory( InstrumentorFactory factory ) {
		this.factory = factory;
	}

	public InstrumentorFactory getFactory() {
		return factory;
	}

	/**
	 * Gets the value of count
	 *
	 * @return the total count
	 */
	public synchronized long getCount()  {
		long total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((CountInstrumentor)iter.next()).getCount();
		}
		return total;
	}

	/**
	 * Empty for this impl
	 *
	 * @param argCount
	 */
	public void setCount(long argCount) {
	}

	/**
	 * Empty for this impl.
	 *
	 * @param argCount
	 */
	public void incCount(long argCount) {
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

} // CountInstrumentorAggregatedImpl
