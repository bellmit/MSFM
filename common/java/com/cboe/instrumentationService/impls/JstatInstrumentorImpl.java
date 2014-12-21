package com.cboe.instrumentationService.impls;
/**
 * 
 * JstatInstrumentorImpl
 * 
 * @author neher
 * 
 * Created: November 2, 2006
 *
 * @version 1.0
 *
 */
import com.cboe.instrumentationService.factories.InstrumentorFactory;
import com.cboe.instrumentationService.instrumentors.JstatInstrumentor;

public class JstatInstrumentorImpl implements JstatInstrumentor {


    private String name;
    private Object userData;
    private volatile double s0Capacity;
    private volatile double s0Utilization;
    private volatile double s1Capacity;
    private volatile double s1Utilization;
    private volatile double eCapacity;
    private volatile double eUtilization;
    private volatile double oCapacity;
    private volatile double oUtilization;
    private volatile double pCapacity;
    private volatile double pUtilization;
    private volatile long nbrYgGcs;
    private volatile double timeYgGcs;
    private volatile long nbrFullGcs;
    private volatile double timeFullGcs;
    private volatile double timeYgFullGcs;
    private volatile String pidName;
	private volatile long tickFreq;
	private volatile long safepointSyncTime;
	private volatile long applicationTime;
	private volatile long safepointTime;
	private volatile long safepoints;

	private byte[] key = null;
    private boolean privateMode = false;
    private InstrumentorFactory factory = null;

    public JstatInstrumentorImpl( String name, Object userData ) {
        this.name = name;
        this.userData = userData;
	} // JstatInstrumentorImpl constructor

    public void setLockObject( Object newLockObject ) {
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
        return userData;
    }

    /**
     * Sets the value of userData
     *
     * @param argUserData Value to assign to this.userData
     */
    public void setUserData(Object argUserData) {
        userData = argUserData;
    }

    public void setFactory( InstrumentorFactory factory ) {
        this.factory = factory;
    }

    public InstrumentorFactory getFactory() {
        return factory;
    }
    
    /**
     * Gets the value of s0Capacity
     *
     * @return the value of s0Capacity
     */
    public double getS0Capacity()  {
		return s0Capacity;
    }

    /**
     * Sets the value of s0Capacity
     *
     * @param argS0Capacity Value to assign to this.s0Capacity
     */
    public void setS0Capacity(double argS0Capacity) {
            s0Capacity = argS0Capacity;
    }

    /**
     * Gets the value of s0Utilization
     *
     * @return the value of s0Utilization
     */
    public double getS0Utilization()  {
            return s0Utilization;
    }

    /**
     * Sets the value of s0Utilization
     *
     * @param argS0Utilization Value to assign to this.s0Utilization
     */
    public void setS0Utilization(double argS0Utilization) {
            s0Utilization = argS0Utilization;
    }

    /**
     * Gets the value of s1Capacity
     *
     * @return the value of s1Capacity
     */
    public double getS1Capacity()  {
            return s1Capacity;
    }

    /**
     * Sets the value of s1Capacity
     *
     * @param argS1Capacity Value to assign to this.s1Capacity
     */
    public void setS1Capacity(double argS1Capacity) {
            s1Capacity = argS1Capacity;
    }

    /**
     * Gets the value of s1Utilization
     *
     * @return the value of s1Utilization
     */
    public double getS1Utilization()  {
            return s1Utilization;
    }

    /**
     * Sets the value of s1Utilization
     *
     * @param argS1Utilization Value to assign to this.s1Utilization
     */
    public void setS1Utilization(double argS1Utilization) {
		s1Utilization = argS1Utilization;
    }

    /**
     * Gets the value of eCapacity
     *
     * @return the value of eCapacity
     */
    public double getECapacity()  {
            return eCapacity;
    }

    /**
     * Sets the value of eCapacity
     *
     * @param argECapacity Value to assign to this.eCapacity
     */
    public void setECapacity(double argECapacity) {
            eCapacity = argECapacity;
    }

    /**
     * Gets the value of eUtilization
     *
     * @return the value of eUtilization
     */
    public double getEUtilization()  {
            return eUtilization;
    }

    /**
     * Sets the value of eUtilization
     *
     * @param argEUtilization Value to assign to this.eUtilization
     */
    public void setEUtilization(double argEUtilization) {
		eUtilization = argEUtilization;
    }
    
    /**
     * Gets the value of oCapacity
     *
     * @return the value of oCapacity
     */
    public double getOCapacity()  {
            return oCapacity;
    }

    /**
     * Sets the value of oCapacity
     *
     * @param argOCapacity Value to assign to this.oCapacity
     */
    public void setOCapacity(double argOCapacity) {
		oCapacity = argOCapacity;
	}

    /**
     * Gets the value of oUtilization
     *
     * @return the value of oUtilization
     */
    public double getOUtilization()  {
           return oUtilization;
	}

    /**
     * Sets the value of oUtilization
     *
     * @param argOUtilization Value to assign to this.oUtilization
     */
    public void setOUtilization(double argOUtilization) {
            oUtilization = argOUtilization;
	}

    /**
     * Gets the value of pCapacity
     *
     * @return the value of pCapacity
     */
    public double getPCapacity()  {
		return pCapacity;
    }

    /**
     * Sets the value of pCapacity
     *
     * @param argPCapacity Value to assign to this.pCapacity
     */
    public void setPCapacity(double argPCapacity) {
           pCapacity = argPCapacity;
    }

    /**
     * Gets the value of pUtilization
     *
     * @return the value of pUtilization
     */
    public double getPUtilization()  {
            return pUtilization;
    }

    /**
     * Sets the value of pUtilization
     *
     * @param argPUtilization Value to assign to this.pUtilization
     */
    public void setPUtilization(double argPUtilization) {
            pUtilization = argPUtilization;
    }
    
    /**
     * Gets the value of nbrYgGcs
     *
     * @return the value of nbrYgGcs
     */
    public long getNbrYgGcs()  {
            return nbrYgGcs;
    }

    /**
     * Sets the value of nbrYgGcs
     *
     * @param argNbrYgGcs Value to assign to this.nbrYgGcs
     */
    public void setNbrYgGcs(long argNbrYgGcs) {
            nbrYgGcs = argNbrYgGcs;
    }

    /**
     * Gets the value of timeYgGcs
     *
     * @return the value of timeYgGcs
     */
    public double getTimeYgGcs()  {
            return timeYgGcs;
    }

    /**
     * Sets the value of timeYgGcs
     *
     * @param argTimeYgGcs Value to assign to this.timeYgGcs
     */
    public void setTimeYgGcs(double argTimeYgGcs) {
		timeYgGcs = argTimeYgGcs;
	}

    /**
     * Gets the value of nbrFullGcs
     *
     * @return the value of nbrFullGcs
     */
    public long getNbrFullGcs()  {
           return nbrFullGcs;
    }

    /**
     * Sets the value of nbrFullGcs
     *
     * @param argNbrFullGcs Value to assign to this.nbrFullGcs
     */
    public void setNbrFullGcs(long argNbrFullGcs) {
            nbrFullGcs = argNbrFullGcs;
    }

    /**
     * Gets the value of timeFullGcs
     *
     * @return the value of timeFullGcs
     */
    public double getTimeFullGcs()  {
            return timeFullGcs;
    }

    /**
     * Sets the value of timeFullGcs
     *
     * @param argTimeFullGcs Value to assign to this.timeFullGcs
     */
    public void setTimeFullGcs(double argTimeFullGcs) {
            timeFullGcs = argTimeFullGcs;
    }
    
    /**
     * Gets the value of timeYgFullGcs
     *
     * @return the value of timeYgFullGcs
     */
    public double getTimeYgFullGcs()  {
            return timeYgFullGcs;
    }

    /**
     * Sets the value of timeYgFullGcs
     *
     * @param argTimeYgFullGcs Value to assign to this.timeYgFullGcs
     */
    public void setTimeYgFullGcs(double argTimeYgFullGcs) {
            timeYgFullGcs = argTimeYgFullGcs;
    }

    /**
     * Gets the value of tickFreq
     * high resolution timers below have frequency specified by this "sun.os.hrt.frequency" counter
     * @return the value of tickFreq
     */
	public long getTickFreq() {
		return tickFreq;
	}

	 /**
     * Sets the value of tickFreq
     * high resolution timers below have frequency specified by this "sun.os.hrt.frequency" counter
     * @param tickFreq Value to assign to this.tickFreq
     */
	public void setTickFreq(long tickFreq) {
		this.tickFreq = tickFreq;
	}

	/**
     * Gets the value of safepointSyncTime
     * This is wall-clock time spent waiting for a safepoint.
     * @return the value of safepointSyncTime
     */
	public long getSafepointSyncTime() {
		return safepointSyncTime;
	}

	/**
     * Sets the value of safepointSyncTime
     * This is wall-clock time spent waiting for a safepoint.
     * @param safepointSyncTime Value to assign to this.safepointSyncTime
     */
	public void setSafepointSyncTime(long safepointSyncTime) {
		this.safepointSyncTime = safepointSyncTime;
	}

	/**
     * Gets the value of applicationTime
     * This is wall-clock time the JVM spent running outside of safepoints
     * @return the value of applicationTime
     */
	public long getApplicationTime() {
		return applicationTime;
	}

	/**
     * Sets the value of applicationTime
     * This is wall-clock time the JVM spent running outside of safepoints.
     * @param applicationTime Value to assign to this.applicationTime
     */
	public void setApplicationTime(long applicationTime) {
		this.applicationTime = applicationTime;
	}

	/**
     * Gets the value of safepointTime
     * This is wall-clock time spent running safepoint code.
     * @return the value of safepointTime
     */
	public long getSafepointTime() {
		return safepointTime;
	}

	/**
     * Sets the value of safepointTime
     * This is wall-clock time spent running safepoint code.
     * @param safepointTime Value to assign to this.safepointTime
     */
	public void setSafepointTime(long safepointTime) {
		this.safepointTime = safepointTime;
	}

	/**
     * Gets the value of safepoints
     * This is number of safepoints which have occurred since the start of the JVM.
     * @return the value of safepoints
     */
	public long getSafepoints() {
		return safepoints;
	}

	/**
     * Sets the value of safepoints
     * This is number of safepoints which have occurred since the start of the JVM.
     * @param safepoints Value to assign to this.safepoints
     */
	public void setSafepoints(long safepoints) {
		this.safepoints = safepoints;
	}

    /**
     * Copies this JSTATI to the given JSTATI.
     *
     * @param jstati a <code>JstatInstrumentor</code> value
     */
    public void get( JstatInstrumentor jstati ) {
        if ( jstati != null ) {
            jstati.setS0Capacity(s0Capacity);
            jstati.setS0Utilization(s0Utilization);
            jstati.setS1Capacity(s1Capacity);
            jstati.setS1Utilization(s1Utilization);
            jstati.setECapacity(eCapacity);
            jstati.setEUtilization(eUtilization);
            jstati.setOCapacity(oCapacity);
            jstati.setOUtilization(oUtilization);
            jstati.setPCapacity(pCapacity);
            jstati.setPUtilization(pUtilization);
            jstati.setNbrYgGcs(nbrYgGcs);
            jstati.setTimeYgGcs(timeYgGcs);
            jstati.setNbrFullGcs(nbrFullGcs);
            jstati.setTimeFullGcs(timeFullGcs);
            jstati.setTimeYgFullGcs(timeYgFullGcs);
			jstati.setTickFreq(tickFreq);
			jstati.setSafepointSyncTime(safepointSyncTime);
			jstati.setApplicationTime(applicationTime);
			jstati.setSafepointTime(safepointTime);
			jstati.setSafepoints(safepoints);
            }
    }

    public String getToStringHeader( boolean showUserData, boolean showPrivateFlag ) {
        return "name," + (showPrivateFlag ? "private," : "") +
			"S0C,S1C,S0U,S1U,EC,EU,OC,OU,PC,PU,YGC,YGCT,FGC,FGCT,GCT,TICK,APPT,SPST,SPT,SP" +
			(showUserData ? ",userdata" : "");
    }

    public String toString(boolean showUserData, boolean showPrivateFlag, String instNameToUse) {
        String userDataStr = "";
        if (showUserData) {
            userDataStr = "," + getUserData();
        }

		StringBuilder tmpRetVal = new StringBuilder(256);
		tmpRetVal.append(instNameToUse);
		tmpRetVal.append(',');
		tmpRetVal.append( (showPrivateFlag ? (isPrivate() + ",") : ""));
		tmpRetVal.append( getS0Capacity() );
		tmpRetVal.append(',');
		tmpRetVal.append( getS1Capacity() );
		tmpRetVal.append(',');
		tmpRetVal.append( getS0Utilization() );
		tmpRetVal.append(',');
		tmpRetVal.append( getS1Utilization() );
		tmpRetVal.append(',');
		tmpRetVal.append( getECapacity() );
		tmpRetVal.append(',');
		tmpRetVal.append( getEUtilization() );
		tmpRetVal.append(',');
		tmpRetVal.append( getOCapacity() );
		tmpRetVal.append(',');
		tmpRetVal.append( getOUtilization() );
		tmpRetVal.append(',');
		tmpRetVal.append( getPCapacity() );
		tmpRetVal.append(',');
		tmpRetVal.append( getPUtilization() );
		tmpRetVal.append(',');
		tmpRetVal.append( getNbrYgGcs() );
		tmpRetVal.append(',');
		tmpRetVal.append( getTimeYgGcs() );
		tmpRetVal.append(',');
		tmpRetVal.append( getNbrFullGcs() );
		tmpRetVal.append(',');
		tmpRetVal.append( getTimeFullGcs() );
		tmpRetVal.append(',');
		tmpRetVal.append( getTimeYgFullGcs() );
		tmpRetVal.append(',');
		tmpRetVal.append( getTickFreq() );
		tmpRetVal.append(',');
		tmpRetVal.append( getApplicationTime() );
		tmpRetVal.append(',');
		tmpRetVal.append( getSafepointSyncTime() );
		tmpRetVal.append(',');
		tmpRetVal.append( getSafepointTime() );
		tmpRetVal.append(',');
		tmpRetVal.append( getSafepoints() );
		tmpRetVal.append( userDataStr );
		return tmpRetVal.toString();
    }

	@Override
    public String toString() {
        return toString( true, true, getName() );
    }

}
