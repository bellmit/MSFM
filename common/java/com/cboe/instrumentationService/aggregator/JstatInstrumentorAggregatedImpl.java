package com.cboe.instrumentationService.aggregator;
/**
 * 
 * JstatInstrumentorAggregatedImpl
 * 
 * @author neher
 * 
 * Created: November 2, 2006
 *
 * @version 1.0
 *
 */
import java.util.ArrayList;
import java.util.Iterator;

import com.cboe.instrumentationService.factories.InstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorFactoryVisitor;
import com.cboe.instrumentationService.factories.JstatInstrumentorFactory;
import com.cboe.instrumentationService.factories.JstatInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.instrumentors.AggregatedJstatInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.instrumentors.JstatInstrumentor;

public class JstatInstrumentorAggregatedImpl implements JstatInstrumentor, AggregatedJstatInstrumentor {
    private String name;
    private Object userData;
    private byte[] key = null;
    private boolean privateMode = false;
    private ArrayList instrumentors = new ArrayList();
    private InstrumentorFactory factory = null;

    public JstatInstrumentorAggregatedImpl( String name, Object userData ) {
        this.name = name;
        this.userData = userData;
    } // JstatInstrumentorAggregatedImpl constructor

    public void setLockObject( Object newLockObject ) {
    }

    public synchronized void addInstrumentor( JstatInstrumentor jstati ) {
        if ( instrumentors.indexOf( jstati ) < 0 ) {
            instrumentors.add( jstati );
        }
    }

    public synchronized void removeInstrumentor( JstatInstrumentor jstati ) {
        instrumentors.remove( jstati );
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
            JstatInstrumentor inst = (JstatInstrumentor)iter.next();
            JstatInstrumentorFactory jstatiFactory = (JstatInstrumentorFactory)inst.getFactory();
            jstatiFactory.unregister( inst );
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
            JstatInstrumentor inst = (JstatInstrumentor)iter.next();
            if ( !((JstatInstrumentorFactoryVisitor)visitor).visit( inst ) ) {
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
     * @param argUserData Value to assign to this.userData
     */
    public void setUserData(Object argUserData) {
        this.userData = argUserData;
    }
    
    /**
     * Gets the value of s0Capacity
     *
     * @return the value of s0Capacity
     */
    public synchronized double getS0Capacity()  {
        double total = 0;
        Iterator iter = instrumentors.iterator();
        while( iter.hasNext() ) {
            total += ((JstatInstrumentor)iter.next()).getS0Capacity();
        }
        return total;
    }

    /**
     * Empty for this impl.
     *
     * @param argS0Capacity
     */
    public void setS0Capacity(double argS0Capacity) {
    }
    
    /**
     * Gets the value of s0Utilization
     *
     * @return the value of s0Utilization
     */
    public synchronized double getS0Utilization()  {
        double total = 0;
        Iterator iter = instrumentors.iterator();
        while( iter.hasNext() ) {
            total += ((JstatInstrumentor)iter.next()).getS0Utilization();
        }
        return total;
    }

    /**
     * Empty for this impl.
     *
     * @param argS0Utilization
     */
    public void setS0Utilization(double argS0Utilization) {
    }
    
    /**
     * Gets the value of s1Capacity
     *
     * @return the value of s1Capacity
     */
    public synchronized double getS1Capacity()  {
        double total = 0;
        Iterator iter = instrumentors.iterator();
        while( iter.hasNext() ) {
            total += ((JstatInstrumentor)iter.next()).getS1Capacity();
        }
        return total;
    }

    /**
     * Empty for this impl.
     *
     * @param argS1Capacity
     */
    public void setS1Capacity(double argS1Capacity) {
    }
    
    /**
     * Gets the value of s1Utilization
     *
     * @return the value of s1Utilization
     */
    public synchronized double getS1Utilization()  {
        double total = 0;
        Iterator iter = instrumentors.iterator();
        while( iter.hasNext() ) {
            total += ((JstatInstrumentor)iter.next()).getS1Utilization();
        }
        return total;
    }

    /**
     * Empty for this impl.
     *
     * @param argS1Utilization
     */
    public void setS1Utilization(double argS1Utilization) {
    }
 
    /**
     * Gets the value of eCapacity
     *
     * @return the value of eCapacity
     */
    public synchronized double getECapacity()  {
        double total = 0;
        Iterator iter = instrumentors.iterator();
        while( iter.hasNext() ) {
            total += ((JstatInstrumentor)iter.next()).getECapacity();
        }
        return total;
    }

    /**
     * Empty for this impl.
     *
     * @param argECapacity
     */
    public void setECapacity(double argECapacity) {
    }
    
    /**
     * Gets the value of eUtilization
     *
     * @return the value of eUtilization
     */
    public synchronized double getEUtilization()  {
        double total = 0;
        Iterator iter = instrumentors.iterator();
        while( iter.hasNext() ) {
            total += ((JstatInstrumentor)iter.next()).getEUtilization();
        }
        return total;
    }

    /**
     * Empty for this impl.
     *
     * @param argEUtilization
     */
    public void setEUtilization(double argEUtilization) {
    }
    
    /**
     * Gets the value of oCapacity
     *
     * @return the value of oCapacity
     */
    public synchronized double getOCapacity()  {
        double total = 0;
        Iterator iter = instrumentors.iterator();
        while( iter.hasNext() ) {
            total += ((JstatInstrumentor)iter.next()).getOCapacity();
        }
        return total;
    }

    /**
     * Empty for this impl.
     *
     * @param argOCapacity
     */
    public void setOCapacity(double argOCapacity) {
    }
    
    /**
     * Gets the value of oUtilization
     *
     * @return the value of oUtilization
     */
    public synchronized double getOUtilization()  {
        double total = 0;
        Iterator iter = instrumentors.iterator();
        while( iter.hasNext() ) {
            total += ((JstatInstrumentor)iter.next()).getOUtilization();
        }
        return total;
    }

    /**
     * Empty for this impl.
     *
     * @param argOUtilization
     */
    public void setOUtilization(double argOUtilization) {
    }

    /**
     * Gets the value of pCapacity
     *
     * @return the value of pCapacity
     */
    public synchronized double getPCapacity()  {
        double total = 0;
        Iterator iter = instrumentors.iterator();
        while( iter.hasNext() ) {
            total += ((JstatInstrumentor)iter.next()).getPCapacity();
        }
        return total;
    }

    /**
     * Empty for this impl.
     *
     * @param argPCapacity
     */
    public void setPCapacity(double argPCapacity) {
    }
    
    /**
     * Gets the value of pUtilization
     *
     * @return the value of pUtilization
     */
    public synchronized double getPUtilization()  {
        double total = 0;
        Iterator iter = instrumentors.iterator();
        while( iter.hasNext() ) {
            total += ((JstatInstrumentor)iter.next()).getPUtilization();
        }
        return total;
    }

    /**
     * Empty for this impl.
     *
     * @param argPUtilization
     */
    public void setPUtilization(double argPUtilization) {
    }


    /**
     * Gets the value of nbrYgGcs
     *
     * @return the value of nbrYgGcs
     */
    public synchronized long getNbrYgGcs()  {
        long total = 0;
        Iterator iter = instrumentors.iterator();
        while( iter.hasNext() ) {
            total += ((JstatInstrumentor)iter.next()).getNbrYgGcs();
        }
        return total;
    }

    /**
     * Empty for this impl.
     *
     * @param argNbrYgGcs
     */
    public void setNbrYgGcs(long argNbrYgGcs) {
    }
    
    /**
     * Gets the value of timeYgGcs
     *
     * @return the value of timeYgGcs
     */
    public synchronized double getTimeYgGcs()  {
        double total = 0;
        Iterator iter = instrumentors.iterator();
        while( iter.hasNext() ) {
            total += ((JstatInstrumentor)iter.next()).getTimeYgGcs();
        }
        return total;
    }

    /**
     * Empty for this impl.
     *
     * @param argTimeYgGcs
     */
    public void setTimeYgGcs(double argTimeYgGcs) {
    }

    /**
     * Gets the value of nbrFullGcs
     *
     * @return the value of nbrFullGcs
     */
    public synchronized long getNbrFullGcs()  {
        long total = 0;
        Iterator iter = instrumentors.iterator();
        while( iter.hasNext() ) {
            total += ((JstatInstrumentor)iter.next()).getNbrFullGcs();
        }
        return total;
    }

    /**
     * Empty for this impl.
     *
     * @param argNbrFullGcs
     */
    public void setNbrFullGcs(long argNbrFullGcs) {
    }
    
    /**
     * Gets the value of timeFullGcs
     *
     * @return the value of timeFullGcs
     */
    public synchronized double getTimeFullGcs()  {
        double total = 0;
        Iterator iter = instrumentors.iterator();
        while( iter.hasNext() ) {
            total += ((JstatInstrumentor)iter.next()).getTimeFullGcs();
        }
        return total;
    }

    /**
     * Empty for this impl.
     *
     * @param argTimeFullGcs
     */
    public void setTimeFullGcs(double argTimeFullGcs) {
    }

    /**
     * Gets the value of timeYgFullGcs
     *
     * @return the value of timeYgFullGcs
     */
    public synchronized double getTimeYgFullGcs()  {
        double total = 0;
        Iterator iter = instrumentors.iterator();
        while( iter.hasNext() ) {
            total += ((JstatInstrumentor)iter.next()).getTimeYgFullGcs();
        }
        return total;
    }

    /**
     * Empty for this impl.
     *
     * @param argTimeYgFullGcs
     */
    public void setTimeYgFullGcs(double argTimeYgFullGcs) {
    }
    
	/**
     * Gets the value of Tick Freq
	 * It doesn't make sense to aggregate this value as it is just the "units"
	 * for the SafePoint values (e.g. nanoseconds). So just grab the first one
     *
     * @return the first TickFreq value
     */
    public synchronized long  getTickFreq()  {
        Iterator iter = instrumentors.iterator();
        long total = ((JstatInstrumentor)iter.next()).getTickFreq();
        return total;
    }
	
	/**
     * Empty for this impl.
     *
     * @param tickFreq
     */
    public void setTickFreq(long tickFreq) {
    }

	/**
     * Gets the value of Safepoint SyncTIme
	 *
     * @return the Safepoint SyncTIme value
     */
    public synchronized long getSafepointSyncTime()  {
		long total = 0;
        Iterator iter = instrumentors.iterator();
        while( iter.hasNext() ) {
            total += ((JstatInstrumentor)iter.next()).getSafepointSyncTime();
        }
        return total;
    }
	
	/**
     * Empty for this impl.
     *
     * @param safepointSyncTime
     */
    public void setSafepointSyncTime(long safepointSyncTime) {
    }

	/**
     * Gets the value of Application Time
	 *
     * @return the Application tIme value
     */
    public synchronized long getApplicationTime()  {
		long total = 0;
        Iterator iter = instrumentors.iterator();
        while( iter.hasNext() ) {
            total += ((JstatInstrumentor)iter.next()).getApplicationTime();
        }
        return total;
    }

	/**
     * Empty for this impl.
     *
     * @param applicationTime
     */
    public void setApplicationTime(long applicationTime) {
    }

	/**
     * Gets the value of Safepoint SyncTIme
	 *
     * @return the Safepoint tIme value
     */
    public synchronized long getSafepointTime()  {
		long total = 0;
        Iterator iter = instrumentors.iterator();
        while( iter.hasNext() ) {
            total += ((JstatInstrumentor)iter.next()).getSafepointTime();
        }
        return total;
    }

	/**
     * Empty for this impl.
     *
     * @param safepointTime
     */
    public void setSafepointTime(long safepointTime) {
    }

	/**
     * Gets the value of Safepoints
	 *
     * @return the Safepoint count
     */
    public synchronized long getSafepoints()  {
		long total = 0;
        Iterator iter = instrumentors.iterator();
        while( iter.hasNext() ) {
            total += ((JstatInstrumentor)iter.next()).getSafepoints();
        }
        return total;
    }

	/**
     * Empty for this impl.
     *
     * @param safepoints
     */
    public void setSafepoints(long safepoints) {
    }

    /**
     * Copies this jstati to the given jstati.
     *
     * @param tpi a <code>JstatInstrumentor</code> value
     */
    public void get( JstatInstrumentor jstati ) {
        if ( jstati != null ) {
        }
    }

    public String getToStringHeader( boolean showUserData, boolean showPrivateFlag ) {
        return "name," + (showPrivateFlag ? "private," : "") +
			"SOC,S1C,SOU,SIU,EC,EU,OC,OU,PC,PU,YGC,YGCT,FGC,FGCT,GCT,TICK,APPT,SPST,SPT,SP" +
			"" + (showUserData ? ",userdata" : "");
    }

    public String toString(boolean showUserData, boolean showPrivateFlag, String instNameToUse) {
        String userDataStr = "";
        if (showUserData) {
            userDataStr = "," + getUserData();
        }

		StringBuilder tmpRetVal = new StringBuilder(256);
		tmpRetVal.append(instNameToUse);
		tmpRetVal.append(',');
        tmpRetVal.append( (showPrivateFlag ? (isPrivate() + ",") : "") );
		tmpRetVal.append(getS0Capacity());
        tmpRetVal.append(',');
		tmpRetVal.append(getS1Capacity());
        tmpRetVal.append(',');
		tmpRetVal.append(getS0Utilization());
        tmpRetVal.append(',');
		tmpRetVal.append(getS1Utilization());
        tmpRetVal.append(',');
		tmpRetVal.append(getECapacity());
        tmpRetVal.append(',');
		tmpRetVal.append(getEUtilization());
        tmpRetVal.append(',');
		tmpRetVal.append(getOCapacity());
        tmpRetVal.append(',');
		tmpRetVal.append(getOUtilization());
        tmpRetVal.append(',');
		tmpRetVal.append(getPCapacity());
        tmpRetVal.append(',');
		tmpRetVal.append(getPUtilization());
        tmpRetVal.append(',');
		tmpRetVal.append(getNbrYgGcs());
        tmpRetVal.append(',');
		tmpRetVal.append(getTimeYgGcs());
        tmpRetVal.append(',');
		tmpRetVal.append(getNbrFullGcs());
        tmpRetVal.append(',');
		tmpRetVal.append(getTimeFullGcs());
        tmpRetVal.append(',');
		tmpRetVal.append(getTimeYgFullGcs());
		tmpRetVal.append(',');
		tmpRetVal.append(getTickFreq());
		tmpRetVal.append(',');
		tmpRetVal.append(getApplicationTime());
		tmpRetVal.append(',');
		tmpRetVal.append(getSafepointSyncTime());
		tmpRetVal.append(',');
		tmpRetVal.append(getSafepointTime());
		tmpRetVal.append(',');
		tmpRetVal.append(getSafepoints());
		tmpRetVal.append(userDataStr);
		return tmpRetVal.toString();
    }

	@Override
    public String toString() {
        return toString( true, true, getName() );
    }
}
