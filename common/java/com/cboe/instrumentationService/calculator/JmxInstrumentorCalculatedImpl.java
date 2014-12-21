package com.cboe.instrumentationService.calculator;
/**
 * 
 * KeyValueInstrumentorCalulatedImpl
 * 
 * @author neher
 * 
 * Created: Fri Oct 20 2006
 *
 * @version 1.0
 *
 */
import com.cboe.instrumentationService.factories.InstrumentorFactory;
import com.cboe.instrumentationService.instrumentors.CalculatedJmxInstrumentor;
import com.cboe.instrumentationService.instrumentors.JmxInstrumentor;

public class JmxInstrumentorCalculatedImpl implements JmxInstrumentor, CalculatedJmxInstrumentor {


    private JmxInstrumentor raw;
    private Object lock;
    private long numSamples = 0;
    // These are set at the beginning of a stats interval.
    private long curIntervalPeakThreadCount = 0;
    private long curIntervalCurrentThreadCount = 0;
    private long curIntervalTotalThreadsStarted = 0;
    private long curIntervalTotalCPUTime = 0;

    // Calculated values.
    private long intervalPeakThreadCount = 0;
    private long intervalCurrentThreadCount = 0;
    private long intervalTotalThreadsStarted = 0;
    private long intervalTotalCPUTime = 0;
    private InstrumentorFactory factory = null;

    public JmxInstrumentorCalculatedImpl( JmxInstrumentor rawInst ) {
        raw = rawInst;
        lock = this;
    } // ThreadPoolInstrumentorCalculatedImpl constructor

    public void setLockObject( Object newLockObject ) {
        lock = newLockObject;
        raw.setLockObject( lock );
    }

    public void setFactory( InstrumentorFactory factory ) {
        this.factory = factory;
    }

    public InstrumentorFactory getFactory() {
        return factory;
    }

    public void sumIntervalTime( long timestamp ) {
        // Not used here.
    }

    public void calculate( short calcToSampleFactor ) {
        synchronized( lock ) {
            intervalPeakThreadCount = raw.getPeakThreadCount() - curIntervalPeakThreadCount;
            if ( intervalPeakThreadCount < 0 ) {
                intervalPeakThreadCount = 0;
            }
            intervalCurrentThreadCount = raw.getCurrentThreadCount() - curIntervalCurrentThreadCount;
            if ( intervalCurrentThreadCount < 0 ) {
                intervalCurrentThreadCount = 0;
            }
            intervalTotalThreadsStarted = raw.getTotalThreadsStarted() - curIntervalTotalThreadsStarted;
            if ( intervalTotalThreadsStarted < 0 ) {
                intervalTotalThreadsStarted = 0;
            }
            intervalTotalCPUTime = raw.getTotalCPUTime() - curIntervalTotalCPUTime;
            if ( intervalTotalCPUTime < 0 ) {
                intervalTotalCPUTime = 0;
            }

            curIntervalPeakThreadCount = 0;
            curIntervalCurrentThreadCount = 0;
            curIntervalTotalThreadsStarted = 0;
            curIntervalTotalCPUTime = 0;
        }
    }

    public long incSamples() {
        numSamples++;
        return numSamples;
    }

    public long getIntervalPeakThreadCount() {
        synchronized( lock ) {
            return intervalPeakThreadCount;
        }
    }

    public long getIntervalCurrentThreadCount() {
        synchronized( lock ) {
            return intervalCurrentThreadCount;
        }
    }

    public long getIntervalTotalThreadsStarted() {
        synchronized( lock ) {
            return intervalTotalThreadsStarted;
        }
    }

    public long getIntervalTotalCPUTime() {
        synchronized( lock ) {
            return intervalTotalCPUTime;
        }
    }

    // Provide impls for interface, delegate everything to raw.

    /**
     * Sets new value to privateMode.  This flag can control whether this
     * instrumentor is exposed to the outside via any output
     * mechanism.
     *
     * @param newValue a <code>boolean</code> value
     */
    public void setPrivate( boolean newValue ) {
        raw.setPrivate( newValue );
    }

    /**
     * Returns value of privateMode.  This flag can control whether this
     * instrumentor is exposed to the outside via any output
     * mechanism.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isPrivate() {
        return raw.isPrivate();
    }

    /**
     * Gets the value of key
     *
     * @return the value of key
     */
    public byte[] getKey()  {
        return raw.getKey();
    }

    /**
     * Sets the value of key
     *
     * @param argKey Value to assign to this.key
     */
    public void setKey(byte[] argKey) {
        raw.setKey( argKey );
    }

    /**
     * Gets the value of name
     *
     * @return the value of name
     */
    public String getName()  {
        return raw.getName();
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
        raw.rename( newName );
    }

    /**
     * Gets the value of userData
     *
     * @return the value of userData
     */
    public Object getUserData()  {
        return raw.getUserData();
    }

    /**
     * Sets the value of userData
     *
     * @param argUserObject Value to assign to this.userData
     */
    public void setUserData(Object argUserData) {
        raw.setUserData( argUserData );
    }
    
    /**
     * Gets the value of peakThreadCount
     *
     * @return the value of peakThreadCount
     */
    public int getPeakThreadCount()  {
        return raw.getPeakThreadCount();
    }

    /**
     * Sets the value of peakThreadCount
     *
     * @param argPeakThreadCount Value to assign to this.peakThreadCount
     */
    public void setPeakThreadCount(int argPeakThreadCount) {
        synchronized( lock ) {
            if ( curIntervalPeakThreadCount == 0 ) {
                curIntervalPeakThreadCount = raw.getPeakThreadCount();
            }
        }

        raw.setPeakThreadCount( argPeakThreadCount );
    }

    /**
     * Gets the value of currentThreadCount
     *
     * @return the value of currentThreadCount
     */
    public int getCurrentThreadCount()  {
        return raw.getCurrentThreadCount();
    }

    /**
     * Sets the value of currentThreadCount
     *
     * @param argCurrentThreadCount Value to assign to this.currentThreadCount
     */
    public void setCurrentThreadCount(int argCurrentThreadCount) {
        synchronized( lock ) {
            if ( curIntervalCurrentThreadCount == 0 ) {
                curIntervalCurrentThreadCount = raw.getCurrentThreadCount();
            }
        }

        raw.setCurrentThreadCount( argCurrentThreadCount );
    }

    /**
     * Gets the value of totalThreadsStarted
     *
     * @return the value of totalThreadsStarted
     */
    public long getTotalThreadsStarted()  {
        return raw.getTotalThreadsStarted();
    }

    /**
     * Sets the value of totalThreadsStarted
     *
     * @param argPeakThreadCount Value to assign to this.totalThreadsStarted
     */
    public void setTotalThreadsStarted(long argTotalThreadsStarted) {
        synchronized( lock ) {
            if ( curIntervalTotalThreadsStarted == 0 ) {
                curIntervalTotalThreadsStarted = raw.getTotalThreadsStarted();
            }
        }

        raw.setTotalThreadsStarted( argTotalThreadsStarted );
    }

    /**
     * Gets the value of totalCPUTime
     *
     * @return the value of totalCPUTime
     */
    public long getTotalCPUTime()  {
        return raw.getTotalCPUTime();
    }

    /**
     * Sets the value of totalCPUTime
     *
     * @param argCPUTime Value to assign to this.totalCPUTime
     */
    public void setTotalCPUTime(long argTotalCPUTime) {
        synchronized( lock ) {
            if ( curIntervalTotalCPUTime == 0 ) {
                curIntervalTotalCPUTime = raw.getTotalCPUTime();
            }
        }

        raw.setTotalCPUTime( argTotalCPUTime );
    }
    
    /**
     * Copies this jmxi to the given jmxi.
     *
     * @param jmxi a <code>JmxInstrumentor</code> value
     */
    public void get( JmxInstrumentor jmxi ) {
        raw.get( jmxi );
    }

    public String getToStringHeader( boolean showUserData, boolean showPrivate ) {
        return "THIS getToStringHeader NOT IMPLEMENTED";
    }

    public String getToStringHeader() {
        return "Name,PeakThreadCount,CurrentThreadCount,TotalThreadsStarted,TotalCPUTimeNanoSec";
    }

    public String toString( boolean showUserData, boolean showPrivate, String instNameToUse ) {
        return raw.toString( showUserData, showPrivate, instNameToUse );
    }

    public String toString( String instNameToUse ) {
        return instNameToUse +
            getIntervalPeakThreadCount() + "," +
            getIntervalCurrentThreadCount() + "," +
            getIntervalTotalThreadsStarted() + "," +
            getIntervalTotalCPUTime();
    }
    
    public String toString() {
        return toString( getName() );
    }
}
