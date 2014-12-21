package com.cboe.instrumentationService.calculator;

/**
 * 
 * JstatInstrumentorCalculatedImpl
 * 
 * @author neher
 * 
 * Created: November 2, 2006
 *
 * @version 1.0
 *
 */

import com.cboe.instrumentationService.factories.InstrumentorFactory;
import com.cboe.instrumentationService.instrumentors.CalculatedJstatInstrumentor;
import com.cboe.instrumentationService.instrumentors.JstatInstrumentor;

public class JstatInstrumentorCalculatedImpl implements CalculatedJstatInstrumentor, JstatInstrumentor {

	private JstatInstrumentor raw;
	private Object lock;
	private long numSamples = 0;
	// These are set at the beginning of a stats interval.
	private volatile double curIntervalS0Capacity = 0;
	private volatile double curIntervalS0Utilization = 0;
	private volatile double curIntervalS1Capacity = 0;
	private volatile double curIntervalS1Utilization = 0;
	private volatile double curIntervalECapacity = 0;
	private volatile double curIntervalEUtilization = 0;
	private volatile double curIntervalOCapacity = 0;
	private volatile double curIntervalOUtilization = 0;
	private volatile double curIntervalPCapacity = 0;
	private volatile double curIntervalPUtilization = 0;
	private volatile long curIntervalNbrYgGcs = 0;
	private volatile double curIntervalTimeYgGcs = 0;
	private volatile long curIntervalNbrFullGcs = 0;
	private volatile double curIntervalTimeFullGcs = 0;
	private volatile double curIntervalTimeYgFullGcs = 0;
	private volatile long curIntervalTickFreq = 0;
	private volatile long curIntervalSafepointSyncTime = 0;
	private volatile long curIntervalApplicationTime = 0;
	private volatile long curIntervalSafepointTime = 0;
	private volatile long curIntervalSafepoints = 0;
	// Calculated values.
	private volatile double intervalS0Capacity = 0;
	private volatile double intervalS0Utilization = 0;
	private volatile double intervalS1Capacity = 0;
	private volatile double intervalS1Utilization = 0;
	private volatile double intervalECapacity = 0;
	private volatile double intervalEUtilization = 0;
	private volatile double intervalOCapacity = 0;
	private volatile double intervalOUtilization = 0;
	private volatile double intervalPCapacity = 0;
	private volatile double intervalPUtilization = 0;
	private volatile long intervalNbrYgGcs = 0;
	private volatile double intervalTimeYgGcs = 0;
	private volatile long intervalNbrFullGcs = 0;
	private volatile double intervalTimeFullGcs = 0;
	private volatile double intervalTimeYgFullGcs = 0;
	private volatile long intervalTickFreq = 0;
	private volatile long intervalSafepointSyncTime = 0;
	private volatile long intervalApplicationTime = 0;
	private volatile long intervalSafepointTime = 0;
	private volatile long intervalSafepoints = 0;
	private InstrumentorFactory factory = null;

	public JstatInstrumentorCalculatedImpl(JstatInstrumentor rawInst) {
		raw = rawInst;
		lock = this;
	} // JstatInstrumentorCalculatedImpl constructor

	public void setLockObject(Object newLockObject) {
		lock = newLockObject;
		raw.setLockObject(lock);
	}

	public void setFactory(InstrumentorFactory factory) {
		this.factory = factory;
	}

	public InstrumentorFactory getFactory() {
		return factory;
	}

	public void sumIntervalTime(long timestamp) {
		// Not used here.
	}

	/**
	 * Was considering removing the calculations for "capacity" values, but
	 * if Adaptive Sizing is enabled, it may be valuable to see the changes
	 *
	 * @param calcToSampleFactor
	 */
	public void calculate(short calcToSampleFactor) {
		synchronized (lock) {
			intervalS0Capacity = raw.getS0Capacity() - curIntervalS0Capacity;
			if (intervalS0Capacity < 0) {
				intervalS0Capacity = 0;
			}

			intervalS0Utilization = raw.getS0Utilization() - curIntervalS0Utilization;
			if (intervalS0Utilization < 0) {
				intervalS0Utilization = 0;
			}

			intervalS1Capacity = raw.getS1Capacity() - curIntervalS1Capacity;
			if (intervalS1Capacity < 0) {
				intervalS1Capacity = 0;
			}

			intervalS1Utilization = raw.getS1Utilization() - curIntervalS1Utilization;
			if (intervalS1Utilization < 0) {
				intervalS1Utilization = 0;
			}

			intervalECapacity = raw.getECapacity() - curIntervalECapacity;
			if (intervalECapacity < 0) {
				intervalECapacity = 0;
			}

			intervalEUtilization = raw.getEUtilization() - curIntervalEUtilization;
			if (intervalEUtilization < 0) {
				intervalEUtilization = 0;
			}

			intervalOCapacity = raw.getOCapacity() - curIntervalOCapacity;
			if (intervalOCapacity < 0) {
				intervalOCapacity = 0;
			}

			intervalOUtilization = raw.getOUtilization() - curIntervalOUtilization;
			if (intervalOUtilization < 0) {
				intervalOUtilization = 0;
			}

			intervalPCapacity = raw.getPCapacity() - curIntervalPCapacity;
			if (intervalPCapacity < 0) {
				intervalPCapacity = 0;
			}

			intervalPUtilization = raw.getPUtilization() - curIntervalPUtilization;
			if (intervalPUtilization < 0) {
				intervalPUtilization = 0;
			}

			intervalNbrYgGcs = raw.getNbrYgGcs() - curIntervalNbrYgGcs;
			if (intervalNbrYgGcs < 0) {
				intervalNbrYgGcs = 0;
			}

			intervalTimeYgGcs = raw.getTimeYgGcs() - curIntervalTimeYgGcs;
			if (intervalTimeYgGcs < 0) {
				intervalTimeYgGcs = 0;
			}

			intervalNbrFullGcs = raw.getNbrFullGcs() - curIntervalNbrFullGcs;
			if (intervalNbrFullGcs < 0) {
				intervalNbrFullGcs = 0;
			}

			intervalTimeFullGcs = raw.getTimeFullGcs() - curIntervalTimeFullGcs;
			if (intervalTimeFullGcs < 0) {
				intervalTimeFullGcs = 0;
			}

			intervalTimeYgFullGcs = raw.getTimeYgFullGcs() - curIntervalTimeYgFullGcs;
			if (intervalTimeYgFullGcs < 0) {
				intervalTimeYgFullGcs = 0;
			}

			// this value should never change but do this anyway.
			intervalTickFreq = raw.getTickFreq() - curIntervalTickFreq;
			if (intervalTickFreq < 0) {
				intervalTickFreq = 0;
			}

			intervalSafepointSyncTime = raw.getSafepointSyncTime() - curIntervalSafepointSyncTime;
			if (intervalSafepointSyncTime < 0) {
				intervalSafepointSyncTime = 0;
			}

			intervalApplicationTime = raw.getApplicationTime() - curIntervalApplicationTime;
			if (intervalApplicationTime < 0) {
				intervalApplicationTime = 0;
			}

			intervalSafepointTime = raw.getSafepointTime() - curIntervalSafepointTime;
			if (intervalSafepointTime < 0) {
				intervalSafepointTime = 0;
			}

			intervalSafepoints = raw.getSafepoints() - curIntervalSafepoints;
			if (intervalSafepoints < 0) {
				intervalSafepoints = 0;
			}

			curIntervalS0Capacity = 0;
			curIntervalS0Utilization = 0;
			curIntervalS1Capacity = 0;
			curIntervalS1Utilization = 0;
			curIntervalECapacity = 0;
			curIntervalEUtilization = 0;
			curIntervalOCapacity = 0;
			curIntervalOUtilization = 0;
			curIntervalPCapacity = 0;
			curIntervalPUtilization = 0;
			curIntervalNbrYgGcs = 0;
			curIntervalTimeYgGcs = 0;
			curIntervalNbrFullGcs = 0;
			curIntervalTimeFullGcs = 0;
			curIntervalTimeYgFullGcs = 0;
			curIntervalTickFreq = 0;
			curIntervalSafepointSyncTime = 0;
			curIntervalApplicationTime = 0;
			curIntervalSafepointTime = 0;
			curIntervalSafepoints = 0;
		}
	}

	public long incSamples() {
		numSamples++;
		return numSamples;
	}

	public double getIntervalS0Capacity() {
		return intervalS0Capacity;
	}

	public double getIntervalS0Utilization() {
		return intervalS0Utilization;
	}

	public double getIntervalS1Capacity() {
		return intervalS1Capacity;
	}

	public double getIntervalS1Utilization() {
		return intervalS1Utilization;
	}

	public double getIntervalECapacity() {
		return intervalECapacity;
	}

	public double getIntervalEUtilization() {
		return intervalEUtilization;
	}

	public double getIntervalOCapacity() {
		return intervalOCapacity;
	}

	public double getIntervalOUtilization() {
		return intervalOUtilization;
	}

	public double getIntervalPCapacity() {
		return intervalPCapacity;
	}

	public double getIntervalPUtilization() {
		return intervalPUtilization;
	}

	public long getIntervalNbrYgGcs() {
		return intervalNbrYgGcs;
	}

	public double getIntervalTimeYgGcs() {
		return intervalTimeYgGcs;
	}

	public long getIntervalNbrFullGcs() {
		return intervalNbrFullGcs;
	}

	public double getIntervalTimeFullGcs() {
		return intervalTimeFullGcs;
	}

	public double getIntervalTimeYgFullGcs() {
		return intervalTimeYgFullGcs;
	}

	public long getIntervalTickFreq() {
		return intervalTickFreq;
	}

	public long getIntervalSafepointSyncTime() {
		return intervalSafepointSyncTime;
	}

	public long getIntervalApplicationTime() {
		return intervalApplicationTime;
	}

	public long getIntervalSafepointTime() {
		return intervalSafepointTime;
	}

	public long getIntervalSafepoints() {
		return intervalSafepoints;
	}

	// Provide impls for interface, delegate everything to raw.
	/**
	 * Sets new value to privateMode.  This flag can control whether this
	 * instrumentor is exposed to the outside via any output
	 * mechanism.
	 *
	 * @param newValue a <code>boolean</code> value
	 */
	public void setPrivate(boolean newValue) {
		raw.setPrivate(newValue);
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
	public byte[] getKey() {
		return raw.getKey();
	}

	/**
	 * Sets the value of key
	 *
	 * @param argKey Value to assign to this.key
	 */
	public void setKey(byte[] argKey) {
		raw.setKey(argKey);
	}

	/**
	 * Gets the value of name
	 *
	 * @return the value of name
	 */
	public String getName() {
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
	public void rename(String newName) {
		raw.rename(newName);
	}

	/**
	 * Gets the value of userData
	 *
	 * @return the value of userData
	 */
	public Object getUserData() {
		return raw.getUserData();
	}

	/**
	 * Sets the value of userData
	 *
	 * @param argUserData Value to assign to this.userData
	 */
	public void setUserData(Object argUserData) {
		raw.setUserData(argUserData);
	}

	/**
	 * Gets the value of s0Capacity
	 *
	 * @return the value of s0Capacity
	 */
	public double getS0Capacity() {
		return raw.getS0Capacity();
	}

	/**
	 * Sets the value of s0Capacity
	 *
	 * @param argS0Capacity Value to assign to this.s0Capacity
	 */
	public void setS0Capacity(double argS0Capacity) {
		synchronized (lock) {
			if (curIntervalS0Capacity == 0) {
				curIntervalS0Capacity = raw.getS0Capacity();
			}
		}

		raw.setS0Capacity(argS0Capacity);
	}

	/**
	 * Gets the value of s0Utilization
	 *
	 * @return the value of s0Utilization
	 */
	public double getS0Utilization() {
		return raw.getS0Utilization();
	}

	/**
	 * Sets the value of s0Utilization
	 *
	 * @param argS0Utilization Value to assign to this.s0Utilization
	 */
	public void setS0Utilization(double argS0Utilization) {
		synchronized (lock) {
			if (curIntervalS0Utilization == 0) {
				curIntervalS0Utilization = raw.getS0Utilization();
			}
		}

		raw.setS0Utilization(argS0Utilization);
	}

	/**
	 * Gets the value of s1Capacity
	 *
	 * @return the value of s1Capacity
	 */
	public double getS1Capacity() {
		return raw.getS1Capacity();
	}

	/**
	 * Sets the value of s1Capacity
	 *
	 * @param argS1Capacity Value to assign to this.s1Capacity
	 */
	public void setS1Capacity(double argS1Capacity) {
		synchronized (lock) {
			if (curIntervalS1Capacity == 0) {
				curIntervalS1Capacity = raw.getS1Capacity();
			}
		}

		raw.setS1Capacity(argS1Capacity);
	}

	/**
	 * Gets the value of s1Utilization
	 *
	 * @return the value of s1Utilization
	 */
	public double getS1Utilization() {
		return raw.getS1Utilization();
	}

	/**
	 * Sets the value of s1Utilization
	 *
	 * @param argS1Utilization Value to assign to this.s1Utilization
	 */
	public void setS1Utilization(double argS1Utilization) {
		synchronized (lock) {
			if (curIntervalS1Utilization == 0) {
				curIntervalS1Utilization = raw.getS1Utilization();
			}
		}

		raw.setS1Utilization(argS1Utilization);
	}

	/**
	 * Gets the value of eCapacity
	 *
	 * @return the value of eCapacity
	 */
	public double getECapacity() {
		return raw.getECapacity();
	}

	/**
	 * Sets the value of eCapacity
	 *
	 * @param argECapacity Value to assign to this.eCapacity
	 */
	public void setECapacity(double argECapacity) {
		synchronized (lock) {
			if (curIntervalECapacity == 0) {
				curIntervalECapacity = raw.getECapacity();
			}
		}

		raw.setECapacity(argECapacity);
	}

	/**
	 * Gets the value of eUtilization
	 *
	 * @return the value of eUtilization
	 */
	public double getEUtilization() {
		return raw.getEUtilization();
	}

	/**
	 * Sets the value of eUtilization
	 *
	 * @param argEUtilization Value to assign to this.eUtilization
	 */
	public void setEUtilization(double argEUtilization) {
		synchronized (lock) {
			if (curIntervalEUtilization == 0) {
				curIntervalEUtilization = raw.getEUtilization();
			}
		}

		raw.setEUtilization(argEUtilization);
	}

	/**
	 * Gets the value of oCapacity
	 *
	 * @return the value of oCapacity
	 */
	public double getOCapacity() {
		return raw.getOCapacity();
	}

	/**
	 * Sets the value of oCapacity
	 *
	 * @param argOCapacity Value to assign to this.oCapacity
	 */
	public void setOCapacity(double argOCapacity) {
		synchronized (lock) {
			if (curIntervalOCapacity == 0) {
				curIntervalOCapacity = raw.getOCapacity();
			}
		}

		raw.setOCapacity(argOCapacity);
	}

	/**
	 * Gets the value of oUtilization
	 *
	 * @return the value of oUtilization
	 */
	public double getOUtilization() {
		return raw.getOUtilization();
	}

	/**
	 * Sets the value of oUtilization
	 *
	 * @param argOUtilization Value to assign to this.oUtilization
	 */
	public void setOUtilization(double argOUtilization) {
		synchronized (lock) {
			if (curIntervalOUtilization == 0) {
				curIntervalOUtilization = raw.getOUtilization();
			}
		}

		raw.setOUtilization(argOUtilization);
	}

	/**
	 * Gets the value of pCapacity
	 *
	 * @return the value of pCapacity
	 */
	public double getPCapacity() {
		return raw.getPCapacity();
	}

	/**
	 * Sets the value of pCapacity
	 *
	 * @param argPCapacity Value to assign to this.pCapacity
	 */
	public void setPCapacity(double argPCapacity) {
		synchronized (lock) {
			if (curIntervalPCapacity == 0) {
				curIntervalPCapacity = raw.getPCapacity();
			}
		}

		raw.setPCapacity(argPCapacity);
	}

	/**
	 * Gets the value of pUtilization
	 *
	 * @return the value of pUtilization
	 */
	public double getPUtilization() {
		return raw.getPUtilization();
	}

	/**
	 * Sets the value of pUtilization
	 *
	 * @param argPUtilization Value to assign to this.pUtilization
	 */
	public void setPUtilization(double argPUtilization) {
		synchronized (lock) {
			if (curIntervalPUtilization == 0) {
				curIntervalPUtilization = raw.getPUtilization();
			}
		}

		raw.setPUtilization(argPUtilization);
	}

	/**
	 * Gets the value of nbrYgGcs
	 *
	 * @return the value of nbrYgGcs
	 */
	public long getNbrYgGcs() {
		return raw.getNbrYgGcs();
	}

	/**
	 * Sets the value of nbrYgGcs
	 *
	 * @param argNbrYgGcs Value to assign to this.nbrYgGcs
	 */
	public void setNbrYgGcs(long argNbrYgGcs) {
		synchronized (lock) {
			if (curIntervalNbrYgGcs == 0) {
				curIntervalNbrYgGcs = raw.getNbrYgGcs();
			}
		}

		raw.setNbrYgGcs(argNbrYgGcs);
	}

	/**
	 * Gets the value of timeYgGcs
	 *
	 * @return the value of timeYgGcs
	 */
	public double getTimeYgGcs() {
		return raw.getTimeYgGcs();
	}

	/**
	 * Sets the value of timeYgGcs
	 *
	 * @param argTimeYgGcs Value to assign to this.timeYgGcs
	 */
	public void setTimeYgGcs(double argTimeYgGcs) {
		synchronized (lock) {
			if (curIntervalTimeYgGcs == 0) {
				curIntervalTimeYgGcs = raw.getTimeYgGcs();
			}
		}

		raw.setTimeYgGcs(argTimeYgGcs);
	}

	/**
	 * Gets the value of nbrFullGcs
	 *
	 * @return the value of nbrFullGcs
	 */
	public long getNbrFullGcs() {
		return raw.getNbrFullGcs();
	}

	/**
	 * Sets the value of nbrFullGcs
	 *
	 * @param argNbrFullGcs Value to assign to this.nbrFullGcs
	 */
	public void setNbrFullGcs(long argNbrFullGcs) {
		synchronized (lock) {
			if (curIntervalNbrFullGcs == 0) {
				curIntervalNbrFullGcs = raw.getNbrFullGcs();
			}
		}

		raw.setNbrFullGcs(argNbrFullGcs);
	}

	/**
	 * Gets the value of timeFullGcs
	 *
	 * @return the value of timeFullGcs
	 */
	public double getTimeFullGcs() {
		return raw.getTimeFullGcs();
	}

	/**
	 * Sets the value of timeFullGcs
	 *
	 * @param argTimeFullGcs Value to assign to this.timeFullGcs
	 */
	public void setTimeFullGcs(double argTimeFullGcs) {
		synchronized (lock) {
			if (curIntervalTimeFullGcs == 0) {
				curIntervalTimeFullGcs = raw.getTimeFullGcs();
			}
		}

		raw.setTimeFullGcs(argTimeFullGcs);
	}

	/**
	 * Gets the value of timeYgFullGcs
	 *
	 * @return the value of timeYgFullGcs
	 */
	public double getTimeYgFullGcs() {
		return raw.getTimeYgFullGcs();
	}

	/**
	 * Sets the value of timeYgFullGcs
	 *
	 * @param argTimeYgFullGcs Value to assign to this.timeYgFullGcs
	 */
	public void setTimeYgFullGcs(double argTimeYgFullGcs) {
		synchronized (lock) {
			if (curIntervalTimeYgFullGcs == 0) {
				curIntervalTimeYgFullGcs = raw.getTimeYgFullGcs();
			}
		}

		raw.setTimeYgFullGcs(argTimeYgFullGcs);
	}

	/**
	 * Gets the value of tickFreq
	 * @return the value of tickFreq from raw
	 */
	public long getTickFreq() {
		return raw.getTickFreq();
	}

	/**
	 * Sets the value of tickFreq
	 * @param tickFreq Value to assign to raw tickFreq
	 */
	public void setTickFreq(long tickFreq) {
		synchronized (lock) {
			if (curIntervalTickFreq == 0) {
				curIntervalTickFreq = raw.getTickFreq();
			}
		}

		raw.setTickFreq(tickFreq);
	}

	/**
	 * Gets the value of SafepointSyncTime
	 * @return the value of SafepointSyncTime from raw
	 */
	public long getSafepointSyncTime() {
		return raw.getSafepointSyncTime();
	}

	/**
	 * Sets the value of SafepointSyncTime
	 * @param safepointSyncTime Value to assign to raw SafepointSyncTime
	 */
	public void setSafepointSyncTime(long safepointSyncTime) {
		synchronized (lock) {
			if (curIntervalSafepointSyncTime == 0) {
				curIntervalSafepointSyncTime = raw.getSafepointSyncTime();
			}
		}

		raw.setSafepointSyncTime(safepointSyncTime);
	}

	/**
	 * Gets the value of ApplicationTime
	 * @return the value of ApplicationTime from raw
	 */
	public long getApplicationTime() {
		return raw.getApplicationTime();
	}

	/**
	 * Sets the value of applicationTime
	 * @param applicationTime Value to assign to raw applicationTime
	 */
	public void setApplicationTime(long applicationTime) {
		synchronized (lock) {
			if (curIntervalApplicationTime == 0) {
				curIntervalApplicationTime = raw.getApplicationTime();
			}
		}

		raw.setApplicationTime(applicationTime);
	}

	/**
	 * Gets the value of SafepointTime
	 * @return the value of SafepointTime from raw
	 */
	public long getSafepointTime() {
		return raw.getSafepointTime();
	}

	/**
	 * Sets the value of SafepointTime
	 * @param safepointTime Value to assign to raw SafepointTime
	 */
	public void setSafepointTime(long safepointTime) {
		synchronized (lock) {
			if (curIntervalSafepointTime == 0) {
				curIntervalSafepointTime = raw.getSafepointTime();
			}
		}

		raw.setSafepointTime(safepointTime);
	}

	/**
	 * Gets the value of safepoints
	 * @return the value of safepoints from raw
	 */
	public long getSafepoints() {
		return raw.getSafepoints();
	}

	/**
	 * Sets the value of SafepointTime
	 * @param safepoints Value to assign to raw Safepoints
	 */
	public void setSafepoints(long safepoints) {
		synchronized (lock) {
			if (curIntervalSafepoints == 0) {
				curIntervalSafepoints = raw.getSafepoints();
			}
		}

		raw.setSafepoints(safepoints);
	}

	/**
	 * Copies this jstati to the given jstati.
	 *
	 * @param jstati a <code>JstatInstrumentor</code> value
	 */
	public void get(JstatInstrumentor jstati) {
		raw.get(jstati);
	}

	public String getToStringHeader(boolean showUserData, boolean showPrivate) {
		return "THIS getToStringHeader NOT IMPLEMENTED";
	}

	public String getToStringHeader() {
		return "Name,S0C,S1C,S0U,S1U,EC,EU,OC,OU,PC,PU,YGC,TGCT,FGC,FGCT,GCT,TICK,APPT,SPST,SPT,SP";
	}

	public String toString(boolean showUserData, boolean showPrivate, String instNameToUse) {
		return raw.toString(showUserData, showPrivate, instNameToUse);
	}

	public String toString(String instNameToUse) {
		StringBuilder tmpRetVal = new StringBuilder(256);
		tmpRetVal.append(instNameToUse);
		tmpRetVal.append(getIntervalS0Capacity());
		tmpRetVal.append(',');
		tmpRetVal.append(getIntervalS1Capacity());
		tmpRetVal.append(',');
		tmpRetVal.append(getIntervalS0Utilization());
		tmpRetVal.append(',');
		tmpRetVal.append(getIntervalS1Utilization());
		tmpRetVal.append(',');
		tmpRetVal.append(getIntervalECapacity());
		tmpRetVal.append(',');
		tmpRetVal.append(getIntervalEUtilization());
		tmpRetVal.append(',');
		tmpRetVal.append(getIntervalEUtilization());
		tmpRetVal.append(',');
		tmpRetVal.append(getIntervalOCapacity());
		tmpRetVal.append(',');
		tmpRetVal.append(getIntervalOUtilization());
		tmpRetVal.append(',');
		tmpRetVal.append(getIntervalPCapacity());
		tmpRetVal.append(',');
		tmpRetVal.append(getIntervalPUtilization());
		tmpRetVal.append(',');
		tmpRetVal.append(getIntervalNbrYgGcs());
		tmpRetVal.append(',');
		tmpRetVal.append(getIntervalTimeYgGcs());
		tmpRetVal.append(',');
		tmpRetVal.append(getIntervalNbrFullGcs());
		tmpRetVal.append(',');
		tmpRetVal.append(getIntervalTimeFullGcs());
		tmpRetVal.append(',');
		tmpRetVal.append(getIntervalTimeYgFullGcs());
		tmpRetVal.append(',');
		tmpRetVal.append(getIntervalTickFreq());
		tmpRetVal.append(',');
		tmpRetVal.append(getIntervalApplicationTime());
		tmpRetVal.append(',');
		tmpRetVal.append(getIntervalSafepointSyncTime());
		tmpRetVal.append(',');
		tmpRetVal.append(getIntervalSafepointTime());
		tmpRetVal.append(',');
		tmpRetVal.append(getIntervalSafepoints());
		return tmpRetVal.toString();
	}

	@Override
	public String toString() {
		return toString(getName());
	}
}
