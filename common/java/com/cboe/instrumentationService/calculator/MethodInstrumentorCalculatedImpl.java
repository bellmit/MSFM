package com.cboe.instrumentationService.calculator;

import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedMethodInstrumentor;
import com.cboe.instrumentationService.factories.InstrumentorFactory;

/**
 * MethodInstrumentorCalculatedImpl.java
 *
 *
 * Created: Thu Sep 18 12:30:34 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class MethodInstrumentorCalculatedImpl implements MethodInstrumentor, CalculatedMethodInstrumentor {

	private MethodInstrumentor raw;
	private Object lock;
	private long numSamples = 0;
	private long lastSampleTime;
	// These are set before every time new values are given to raw via interface set methods,
	// if this is the best sample so far.
	private long curPeakCalls = 0;
	private long curPeakExceptions = 0;
	private double curPeakMethodTime = 0;
	// These are set at the beginning of a stats interval.
	private long curIntervalCalls = 0;
	private long curIntervalExceptions = 0;
	private double curIntervalMethodTime = 0;
	private long intervalTimeMillis = 0;

	// Calculated values.
	private long peakCalls = 0;
	private long peakExceptions = 0;
	private double peakMethodTime = 0;
	private long intervalCalls = 0;
	private long intervalExceptions = 0;
	private double intervalMethodTime = 0;
	private double peakCallsRate = 0.0;
	private double peakExceptionsRate = 0.0;
	private double peakResponseTime = 0.0;
	private double avgCallsRate = 0.0;
	private double avgExceptionsRate = 0.0;
	private double avgResponseTime = 0.0;
	private InstrumentorFactory factory = null;

	public MethodInstrumentorCalculatedImpl( MethodInstrumentor rawInst ) {
		raw = rawInst;
		lock = this;
		lastSampleTime = System.currentTimeMillis();
	} // MethodInstrumentorCalculatedImpl constructor

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
		intervalTimeMillis += (timestamp - lastSampleTime);
		lastSampleTime = timestamp;
	}

	public void calculate( short calcToSampleFactor ) {
		synchronized( lock ) {
			peakCalls = curPeakCalls;
			peakExceptions = curPeakExceptions;
			peakMethodTime = curPeakMethodTime;
			intervalCalls = raw.getCalls() - curIntervalCalls;
			if ( intervalCalls < 0 ) {
				intervalCalls = 0;
			}
			intervalExceptions = raw.getExceptions() - curIntervalExceptions;
			if ( intervalExceptions < 0 ) {
				intervalExceptions = 0;
			}
			intervalMethodTime = raw.getMethodTime() - curIntervalMethodTime;
			if ( intervalMethodTime < 0.0 ) {
				intervalMethodTime = 0.0;
			}

			long sampleTimeMillis = intervalTimeMillis / calcToSampleFactor;
			peakCallsRate = peakCalls / (sampleTimeMillis / 1000.0);
			peakExceptionsRate = peakExceptions / (sampleTimeMillis / 1000.0);
			if ( peakCalls > 0 ) {
				peakResponseTime = peakMethodTime / peakCalls;
			} else {
				peakResponseTime = 0.0;
			}

			avgCallsRate = intervalCalls / (intervalTimeMillis / 1000.0);
			avgExceptionsRate = intervalExceptions / (intervalTimeMillis / 1000.0);
			if ( intervalCalls > 0 ) {
				avgResponseTime = intervalMethodTime / intervalCalls;
			} else {
				avgResponseTime = 0.0;
			}

			// Zero-out cur values.
			curPeakCalls = 0;
			curPeakExceptions = 0;
			curPeakMethodTime = 0.0;
			curIntervalCalls = 0;
			curIntervalExceptions = 0;
			curIntervalMethodTime = 0.0;
			intervalTimeMillis = 0;
		}
	}

	public long incSamples() {
		numSamples++;
		return numSamples;
	}

	public long getPeakCalls() {
		synchronized( lock ) {
			return peakCalls;
		}
	}

	public long getPeakExceptions() {
		synchronized( lock ) {
			return peakExceptions;
		}
	}

	public double getPeakMethodTime() {
		synchronized( lock ) {
			return peakMethodTime;
		}
	}

	public long getIntervalCalls() {
		synchronized( lock ) {
			return intervalCalls;
		}
	}

	public long getIntervalExceptions() {
		synchronized( lock ) {
			return intervalExceptions;
		}
	}

	public double getIntervalMethodTime() {
		synchronized( lock ) {
			return intervalMethodTime;
		}
	}

	public double getPeakCallsRate() {
		synchronized( lock ) {
			return peakCallsRate;
		}
	}

	public double getPeakExceptionsRate() {
		synchronized( lock ) {
			return peakExceptionsRate;
		}
	}

	public double getPeakResponseTime() {
		synchronized( lock ) {
			return peakResponseTime;
		}
	}

	public double getAvgCallsRate() {
		synchronized( lock ) {
			return avgCallsRate;
		}
	}

	public double getAvgExceptionsRate() {
		synchronized( lock ) {
			return avgExceptionsRate;
		}
	}

	public double getAvgResponseTime() {
		synchronized( lock ) {
			return avgResponseTime;
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
	 * @param argUserData Value to assign to this.userData
	 */
	public void setUserData(Object argUserData) {
		raw.setUserData( argUserData );
	}

	/**
	 * Gets the value of calls
	 *
	 * @return the value of calls
	 */
	public long getCalls()  {
		return raw.getCalls();
	}

	/**
	 * Sets the value of calls
	 *
	 * @param argCalls Value to assign to this.calls
	 */
	public void setCalls(long argCalls) {
		synchronized( lock ) {
			long deltaCalls = argCalls - raw.getCalls();
			if ( deltaCalls > curPeakCalls ) {
				curPeakCalls = deltaCalls;
			}

			if ( curIntervalCalls == 0 ) {
				curIntervalCalls = raw.getCalls();
			}
		}

		raw.setCalls( argCalls );
	}

	/**
	 * Increments the value of calls
	 *
	 * @param argCalls Value to increment this.calls
	 */
	public void incCalls(long argCalls) {
		raw.incCalls( argCalls );
	}

	/**
	 * Gets the value of exceptions
	 *
	 * @return the value of exceptions
	 */
	public long getExceptions()  {
		return raw.getExceptions();
	}

	/**
	 * Sets the value of exceptions
	 *
	 * @param argExceptions Value to assign to this.exceptions
	 */
	public void setExceptions(long argExceptions) {
		synchronized( lock ) {
			long deltaExceptions = argExceptions - raw.getExceptions();
			if ( deltaExceptions > curPeakExceptions ) {
				curPeakExceptions = deltaExceptions;
			}

			if ( curIntervalExceptions == 0 ) {
				curIntervalExceptions = raw.getExceptions();
			}
		}

		raw.setExceptions( argExceptions );
	}

	/**
	 * Increments the value of exceptions
	 *
	 * @param argExceptions Value to increment this.exceptions
	 */
	public void incExceptions(long argExceptions) {
		raw.incExceptions( argExceptions );
	}

	public void beforeMethodCall() {
		raw.beforeMethodCall();
	}

	public void afterMethodCall() {
		raw.afterMethodCall();
	}

	public void afterMethodCall( Throwable t ) {
		raw.afterMethodCall( t );
	}

	/**
	 * Increments the value of methodTime.
	 *
	 * @param incAmount Value to increment this.methodTime
	 */
	public void incMethodTime( long incAmount ) {
		raw.incMethodTime( incAmount );
	}

	/**
	 * Sets the value of methodTime
	 *
	 * @param newAmount Value to assign to this.methodTime
	 */
	public void setMethodTime( double newAmount ) {
		synchronized( lock ) {
			double deltaMethodTime = newAmount - raw.getMethodTime();
			if ( deltaMethodTime > curPeakMethodTime ) {
				curPeakMethodTime = deltaMethodTime;
			}

			if ( curIntervalMethodTime == 0.0 ) {
				curIntervalMethodTime = raw.getMethodTime();
			}
		}

		raw.setMethodTime( newAmount );
	}

	/**
	 * Gets the value of methodTime
	 *
	 * @return the value of methodTime
	 */
	public double getMethodTime() {
		return raw.getMethodTime();
	}

	/**
	 * Sets the value of sumOfSquareMethodTime
	 *
	 * @param newAmount Value to assign to this.sumOfSquareMethodTime
	 */
	public void setSumOfSquareMethodTime( double newAmount ) {
		raw.setSumOfSquareMethodTime( newAmount );
	}

	/**
	 * Gets the value of sumOfSquareMethodTime
	 *
	 * @return the value of sumOfSquareMethodTime
	 */
	public double getSumOfSquareMethodTime() {
		return raw.getSumOfSquareMethodTime();
	}

	/**
	 * Sets the value of maxMethodTime
	 *
	 * @param newAmount Value to assign to this.maxMethodTime
	 */
	public void setMaxMethodTime( long newAmount ) {
		raw.setMaxMethodTime( newAmount );
	}

	/**
	 * Gets the value of maxMethodTime
	 *
	 * @return the value of maxMethodTime
	 */
	public long getMaxMethodTime() {
		return raw.getMaxMethodTime();
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
	 * Copies this MI to the given MI.
	 *
	 * @param mi a <code>MethodInstrumentor</code> value
	 */
	public void get( MethodInstrumentor mi ) {
		raw.get( mi );
	}

	public String getToStringHeader( boolean showUserData, boolean showPrivate ) {
		return "THIS getToStringHeader NOT IMPLEMENTED";
	}

	public String getToStringHeader() {
		return "Name,PeakCalls,PeakExcepts,PeakMethodTime,IntCalls,IntExcepts,IntMethodTime,PeakCallsRate,PeakExceptsRate,PeakResponseTime,AvgCallsRate,AvgExceptsRate,AvgResponseTime";
	}

	public String toString( boolean showUserData, boolean showPrivate, String instNameToUse ) {
		return raw.toString( showUserData, showPrivate, instNameToUse );
	}

	public String toString( String instNameToUse ) {
		return instNameToUse + "," +
			getPeakCalls() + "," +
			getPeakExceptions() + "," +
			getPeakMethodTime() + "," +
			getIntervalCalls() + "," +
			getIntervalExceptions() + "," +
			getIntervalMethodTime() + "," +
			getPeakCallsRate() + "," +
			getPeakExceptionsRate() + "," +
			getPeakResponseTime() + "," +
			getAvgCallsRate() + "," +
			getAvgExceptionsRate() + "," +
			getAvgResponseTime();
	}

	public String toString() {
		return toString( getName() );
	}

} // MethodInstrumentorCalculatedImpl
