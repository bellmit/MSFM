package com.cboe.instrumentationService.impls;
/**
 * 
 * OutlierInstrumentorImpl
 * 
 * @author neher
 * 
 * Created: Thur Oct 19 2006
 *
 * @version 1.0
 *
 */
import com.cboe.instrumentationService.factories.InstrumentorFactory;
import com.cboe.instrumentationService.instrumentors.OutlierInstrumentor;

public class OutlierInstrumentorImpl implements OutlierInstrumentor {

    private String name;
    private Object userData;
    private Object lock;
    private String methodName;
    private String machine;
    private String process;
    private String timeOutValue;
    private long actualDuration;
    private long timeStamp;
    private long classKey;
    private int blockSize;
    private byte sessionNumber;
    private byte[] key = null;
    private boolean privateMode = false;
    private InstrumentorFactory factory = null;

    public OutlierInstrumentorImpl( String name, Object userData ) {
        this.name = name;
        this.userData = userData;
        lock = this;
    } // OutlierInstrumentorImpl constructor

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
    
    public void setMethodName( String methodName ) {
        this.methodName = methodName;
    }
    public String getMethodName() {
        return methodName;
    }
    
    public void setMachine( String machine ) {
        this.machine = machine;
    }
    public String getMachine() {
        return machine;
    }
    
    public void setProcess( String processStr ) {
        this.process = processStr;
    }
    public String getProcess() {
        return process;
    }
    
    public void setTimeOutValue( String timeOutValue ) {
        this.timeOutValue = timeOutValue;
    }
    public String getTimeOutValue() {
        return timeOutValue;
    }
    
    public void setActualDuration( long actualDuration ) {
        this.actualDuration = actualDuration;
    }
    public long getActualDuration() {
        return this.actualDuration;
    }
    
    public void setTimeStamp( long timeStamp ) {
        this.timeStamp = timeStamp;
    }
    public long getTimeStamp() {
        return timeStamp;
    }
    
    public void setClassKey( long  classKey ) {
        this.classKey = classKey;
    }
    public long getClassKey() {
        return this.classKey;
    }
    public void setBlockSize( int blockSize ) {
        this.blockSize = blockSize;
    }
    public int getBlockSize() {
        return this.blockSize;
    }
    public void setSessionNumber( byte sessionNumber ) {
        this.sessionNumber = sessionNumber;
    }
    public byte getSessionNumber() {
        return this.sessionNumber;
    }
    
    
    public void get( OutlierInstrumentor outlieri ) {
        if (outlieri != null) {
            outlieri.setMethodName(methodName);
            outlieri.setMachine(machine);
            outlieri.setProcess(process);
            outlieri.setTimeOutValue(timeOutValue);
            outlieri.setActualDuration(actualDuration);
            outlieri.setTimeStamp(timeStamp);
            outlieri.setClassKey(classKey);
            outlieri.setBlockSize(blockSize);
            outlieri.setSessionNumber(sessionNumber);
        }
    }

    
    public String getToStringHeader( boolean showUserData, boolean showPrivateFlag ) {
        return "name," + (showPrivateFlag ? "private," : "") + "methodname,machine,process,timeoutvalue,actualduration,timestamp,classKey,blockSize,session" + (showUserData ? ",userdata" : "");
    }

    public String toString(boolean showUserData, boolean showPrivateFlag, String instNameToUse) {
        String userDataStr = "";
        if (showUserData) {
            userDataStr = "," + getUserData();
        }

        return instNameToUse + "," +
            (showPrivateFlag ? (isPrivate() + ",") : "") +
            getMethodName() + "," +
            getMachine() + "," +
            getProcess() + "," +
            getTimeOutValue() + "," +
            getActualDuration() + "," +
            getTimeStamp() + "," +
            getClassKey() + "," +
            getBlockSize() + "," +
            getSessionNumber() +
            userDataStr;
    }

    public String toString() {
        return toString( true, true, getName() );
    }
}
