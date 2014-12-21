package com.cboe.instrumentationService.aggregator;
/**
 * 
 * JmxInstrumentorAggregatedImpl
 * 
 * @author neher
 * 
 * Created: Thur Oct 19 2006
 *
 * @version 1.0
 *
 */
import java.util.ArrayList;
import java.util.Iterator;

import com.cboe.instrumentationService.factories.InstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorFactoryVisitor;
import com.cboe.instrumentationService.factories.JmxInstrumentorFactory;
import com.cboe.instrumentationService.factories.JmxInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.instrumentors.AggregatedJmxInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.instrumentors.JmxInstrumentor;

public class JmxInstrumentorAggregatedImpl implements JmxInstrumentor, AggregatedJmxInstrumentor {
    private String name;
    private Object userData;
    private byte[] key = null;
    private boolean privateMode = false;
    private ArrayList instrumentors = new ArrayList();
    private InstrumentorFactory factory = null;

    public JmxInstrumentorAggregatedImpl( String name, Object userData ) {
        this.name = name;
        this.userData = userData;
    } // JmxInstrumentorAggregatedImpl constructor

    public void setLockObject( Object newLockObject ) {
    }

    public synchronized void addInstrumentor( JmxInstrumentor jmxi ) {
        if ( instrumentors.indexOf( jmxi ) < 0 ) {
            instrumentors.add( jmxi );
        }
    }

    public synchronized void removeInstrumentor( JmxInstrumentor jmxi ) {
        instrumentors.remove( jmxi );
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
            JmxInstrumentor inst = (JmxInstrumentor)iter.next();
            JmxInstrumentorFactory jmxiFactory = (JmxInstrumentorFactory)inst.getFactory();
            jmxiFactory.unregister( inst );
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
            JmxInstrumentor inst = (JmxInstrumentor)iter.next();
            if ( !((JmxInstrumentorFactoryVisitor)visitor).visit( inst ) ) {
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
     * Gets the value of peakThreadCount
     *
     * @return the value of peakThreadCount
     */
    public synchronized int getPeakThreadCount()  {
        int total = 0;
        Iterator iter = instrumentors.iterator();
        while( iter.hasNext() ) {
            total += ((JmxInstrumentor)iter.next()).getPeakThreadCount();
        }
        return total;
    }

    /**
     * Empty for this impl.
     *
     * @param argPeakThreadCount
     */
    public void setPeakThreadCount(int argPeakThreadCount) {
    }
    
    /**
     * Gets the value of currentThreadCount
     *
     * @return the value of peakThreadCount
     */
    public synchronized int getCurrentThreadCount()  {
        int total = 0;
        Iterator iter = instrumentors.iterator();
        while( iter.hasNext() ) {
            total += ((JmxInstrumentor)iter.next()).getCurrentThreadCount();
        }
        return total;
    }

    /**
     * Empty for this impl.
     *
     * @param argPeakThreadCount
     */
    public void setCurrentThreadCount(int argCurrentThreadCount) {
    }
    
    /**
     * Gets the value of totalThreadsStarted
     *
     * @return the value of totalThreadsStarted
     */
    public synchronized long getTotalThreadsStarted()  {
        long total = 0;
        Iterator iter = instrumentors.iterator();
        while( iter.hasNext() ) {
            total += ((JmxInstrumentor)iter.next()).getTotalThreadsStarted();
        }
        return total;
    }

    /**
     * Empty for this impl.
     *
     * @param argTotalThreadsStarted
     */
    public void setTotalThreadsStarted(long argTotalThreadsStarted) {
    }
    
    /**
     * Gets the value of totalCPUTime
     *
     * @return the value of totalCPUTime
     */
    public synchronized long getTotalCPUTime()  {
        int total = 0;
        Iterator iter = instrumentors.iterator();
        while( iter.hasNext() ) {
            total += ((JmxInstrumentor)iter.next()).getTotalCPUTime();
        }
        return total;
    }

    /**
     * Empty for this impl.
     *
     * @param argTotalCPUTime
     */
    public void setTotalCPUTime(long argTotalCPUTime) {
    }
    
    /**
     * Copies this jmxi to the given jmxi.
     *
     * @param tpi a <code>JmxInstrumentor</code> value
     */
    public void get( JmxInstrumentor jmxi ) {
        if ( jmxi != null ) {
        }
    }

    public String getToStringHeader( boolean showUserData, boolean showPrivateFlag ) {
        return "name," + (showPrivateFlag ? "private," : "") + "pkthr,currthr,startthr,totcputimenanosec" + (showUserData ? ",userdata" : "");
    }

    public String toString(boolean showUserData, boolean showPrivateFlag, String instNameToUse) {
        String userDataStr = "";
        if (showUserData) {
            userDataStr = "," + getUserData();
        }

        return instNameToUse + "," +
            (showPrivateFlag ? (isPrivate() + ",") : "") +
            getPeakThreadCount() + "," +
            getCurrentThreadCount() + "," +
            getTotalThreadsStarted() + "," +
            getTotalCPUTime() +
            userDataStr;
    }

    public String toString() {
        return toString( true, true, getName() );
    }

} // JmxInstrumentorAggregatedImpl
