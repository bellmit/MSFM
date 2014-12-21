package com.cboe.instrumentationService.impls;
/**
 * 
 * JmxInstrumentorImpl
 * 
 * @author neher
 * 
 * Created: Thur Oct 19 2006
 *
 * @version 1.0
 *
 */
import com.cboe.instrumentationService.factories.InstrumentorFactory;
import com.cboe.instrumentationService.instrumentors.JmxInstrumentor;

public class JmxInstrumentorImpl implements JmxInstrumentor {

    private String name;
    private Object userData;
    private Object lock;
    private int peakThreadCount = 0;
    private int currentThreadCount = 0;
    private long totalThreadsStarted = 0;
    private long totalCPUTime = 0;
    private byte[] key = null;
    private boolean privateMode = false;
    private InstrumentorFactory factory = null;

    public JmxInstrumentorImpl( String name, Object userData ) {
        this.name = name;
        this.userData = userData;
        lock = this;
    } // JmxInstrumentorImpl constructor

    public void setLockObject( Object newLockObject ) {
        lock = newLockObject;
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
     * Gets the value of peakThreadCount
     *
     * @return the value of peakThreadCount
     */
    public int getPeakThreadCount()  {
        synchronized( lock ) {
            return this.peakThreadCount;
        }
    }

    /**
     * Sets the value of peakThreadCount
     *
     * @param argPeakThreadCount Value to assign to this.peakThreadCount
     */
    public void setPeakThreadCount(int argPeakThreadCount) {
        synchronized( lock ) {
            this.peakThreadCount = argPeakThreadCount;
        }
    }

    /**
     * Gets the value of currentThreadCount
     *
     * @return the value of currentThreadCount
     */
    public int getCurrentThreadCount()  {
        synchronized( lock ) {
            return this.currentThreadCount;
        }
    }

    /**
     * Sets the value of currentThreadCount
     *
     * @param argCurrentThreadCount Value to assign to this.currentThreadCount
     */
    public void setCurrentThreadCount(int argCurrentThreadCount) {
        synchronized( lock ) {
            this.currentThreadCount = argCurrentThreadCount;
        }
    }

    /**
     * Gets the value of totalThreadsStarted
     *
     * @return the value of totalThreadsStarted
     */
    public long getTotalThreadsStarted()  {
        synchronized( lock ) {
            return this.totalThreadsStarted;
        }
    }

    /**
     * Sets the value of totalThreadsStarted
     *
     * @param argTotalThreadsStarted Value to assign to this.totalThreadsStarted
     */
    public void setTotalThreadsStarted(long argTotalThreadsStarted) {
        synchronized( lock ) {
            this.totalThreadsStarted = argTotalThreadsStarted;
        }
    }

    /**
     * Gets the value of totalCPUTime
     *
     * @return the value of totalCPUTime
     */
    public long getTotalCPUTime()  {
        synchronized( lock ) {
            return this.totalCPUTime;
        }
    }

    /**
     * Sets the value of totalCPUTime
     *
     * @param argTotalCPUTime Value to assign to this.totalCPUTime
     */
    public void setTotalCPUTime(long argTotalCPUTime) {
        synchronized( lock ) {
            this.totalCPUTime = argTotalCPUTime;
        }
    }
    
    /**
     * Copies this JMXI to the given JMXI.
     *
     * @param jmxi a <code>JmxInstrumentor</code> value
     */
    public void get( JmxInstrumentor jmxi ) {
        if ( jmxi != null ) {
            jmxi.setPeakThreadCount(peakThreadCount);
            jmxi.setCurrentThreadCount(currentThreadCount);
            jmxi.setTotalThreadsStarted(totalThreadsStarted);
            jmxi.setTotalCPUTime(totalCPUTime);
        }
    }

    public String getToStringHeader( boolean showUserData, boolean showPrivateFlag ) {
        return "name," + (showPrivateFlag ? "private," : "") + "pkthr,currthr,startthr,totcpunanosec" + (showUserData ? ",userdata" : "");
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
}
