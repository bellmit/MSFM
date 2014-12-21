package com.cboe.instrumentationService.calculator;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.cboe.instrumentationService.factories.InstrumentorFactory;
import com.cboe.instrumentationService.instrumentors.CalculatedNetworkConnectionInstrumentor;
import com.cboe.instrumentationService.instrumentors.NetworkConnectionInstrumentor;

/**
 * NetworkConnectionInstrumentorCalculatedImpl.java
 *
 *
 * Created: Thu Sep 18 14:37:55 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class NetworkConnectionInstrumentorCalculatedImpl implements NetworkConnectionInstrumentor, CalculatedNetworkConnectionInstrumentor {

	private NetworkConnectionInstrumentor raw;
	private Object lock;
	private long numSamples = 0;
	private long lastSampleTime;
	// These are set before every time new values are given to raw via interface set methods,
	// if this is the best sample so far.
	private long curPeakBytesSent = 0;
	private long curPeakBytesReceived = 0;
	private long curPeakMsgsSent = 0;
	private long curPeakMsgsReceived = 0;
	private long curPeakPacketsSent = 0;
	private long curPeakPacketsReceived = 0;
	private long curPeakInvalidPacketsReceived = 0;
	private long curPeakGarbageBytesReceived = 0;
	private long curPeakConnects = 0;
	private long curPeakDisconnects = 0;
	private long curPeakExceptions = 0;

	// These are set at the beginning of a stats interval.
	private long curIntervalBytesSent = 0;
	private long curIntervalBytesReceived = 0;
	private long curIntervalMsgsSent = 0;
	private long curIntervalMsgsReceived = 0;
	private long curIntervalPacketsSent = 0;
	private long curIntervalPacketsReceived = 0;
	private long curIntervalInvalidPacketsReceived = 0;
	private long curIntervalGarbageBytesReceived = 0;
	private long curIntervalConnects = 0;
	private long curIntervalDisconnects = 0;
	private long curIntervalExceptions = 0;
	private long intervalTimeMillis = 0;

	// Calculated values.
	private long peakBytesSent = 0;
	private long peakBytesReceived = 0;
	private long peakMsgsSent = 0;
	private long peakMsgsReceived = 0;
	private long peakPacketsSent = 0;
	private long peakPacketsReceived = 0;
	private long peakInvalidPacketsReceived = 0;
	private long peakGarbageBytesReceived = 0;
	private long peakConnects = 0;
	private long peakDisconnects = 0;
	private long peakExceptions = 0;
	private long intervalBytesSent = 0;
	private long intervalBytesReceived = 0;
	private long intervalMsgsSent = 0;
	private long intervalMsgsReceived = 0;
	private long intervalPacketsSent = 0;
	private long intervalPacketsReceived = 0;
	private long intervalInvalidPacketsReceived = 0;
	private long intervalGarbageBytesReceived = 0;
	private long intervalConnects = 0;
	private long intervalDisconnects = 0;
	private long intervalExceptions = 0;
	private double peakBytesSentRate = 0.0;
	private double peakBytesReceivedRate = 0.0;
	private double peakMsgsSentRate = 0.0;
	private double peakMsgsReceivedRate = 0.0;
	private double peakPacketsSentRate = 0.0;
	private double peakPacketsReceivedRate = 0.0;
	private double peakInvalidPacketsReceivedRate = 0.0;
	private double peakGarbageBytesReceivedRate = 0.0;
	private double peakConnectsRate = 0.0;
	private double peakDisconnectsRate = 0.0;
	private double peakExceptionsRate = 0.0;
	private double avgBytesSentRate = 0.0;
	private double avgBytesReceivedRate = 0.0;
	private double avgMsgsSentRate = 0.0;
	private double avgMsgsReceivedRate = 0.0;
	private double avgPacketsSentRate = 0.0;
	private double avgPacketsReceivedRate = 0.0;
	private double avgInvalidPacketsReceivedRate = 0.0;
	private double avgGarbageBytesReceivedRate = 0.0;
	private double avgConnectsRate = 0.0;
	private double avgDisconnectsRate = 0.0;
	private double avgExceptionsRate = 0.0;
	private double bytesSentRateHWM = 0.0;
	private long bytesSentRateHWMTime = 0;
	private double bytesReceivedRateHWM = 0.0;
	private long bytesReceivedRateHWMTime = 0;
	private double msgsSentRateHWM = 0.0;
	private long msgsSentRateHWMTime = 0;
	private double msgsReceivedRateHWM = 0.0;
	private long msgsReceivedRateHWMTime = 0;
	private double packetsSentRateHWM = 0.0;
	private long packetsSentRateHWMTime = 0;
	private double packetsReceivedRateHWM = 0.0;
	private long packetsReceivedRateHWMTime = 0;

	private SimpleDateFormat dateFormatter = new SimpleDateFormat( "HH:mm:ss" );


	private InstrumentorFactory factory = null;

	public NetworkConnectionInstrumentorCalculatedImpl( NetworkConnectionInstrumentor rawInst ) {
		raw = rawInst;
		lock = this;
		lastSampleTime = System.currentTimeMillis();
	} // NetworkConnectionInstrumentorCalculatedImpl constructor

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
			peakBytesSent = curPeakBytesSent;
			peakBytesReceived = curPeakBytesReceived;
			peakMsgsSent = curPeakMsgsSent;
			peakMsgsReceived = curPeakMsgsReceived;
			peakPacketsSent = curPeakPacketsSent;
			peakPacketsReceived = curPeakPacketsReceived;
			peakInvalidPacketsReceived = curPeakInvalidPacketsReceived;
			peakGarbageBytesReceived = curPeakGarbageBytesReceived;
			peakConnects = curPeakConnects;
			peakDisconnects = curPeakDisconnects;
			peakExceptions = curPeakExceptions;

			intervalBytesSent = raw.getBytesSent() - curIntervalBytesSent;
			if ( intervalBytesSent < 0 ) {
				intervalBytesSent = 0;
			}
			intervalBytesReceived = raw.getBytesReceived() - curIntervalBytesReceived;
			if ( intervalBytesReceived < 0 ) {
				intervalBytesReceived = 0;
			}
			intervalMsgsSent = raw.getMsgsSent() - curIntervalMsgsSent;
			if ( intervalMsgsSent < 0 ) {
				intervalMsgsSent = 0;
			}
			intervalMsgsReceived = raw.getMsgsReceived() - curIntervalMsgsReceived;
			if ( intervalMsgsReceived < 0 ) {
				intervalMsgsReceived = 0;
			}
			intervalPacketsSent = raw.getPacketsSent() - curIntervalPacketsSent;
			if ( intervalPacketsSent < 0 ) {
				intervalPacketsSent = 0;
			}
			intervalPacketsReceived = raw.getPacketsReceived() - curIntervalPacketsReceived;
			if ( intervalPacketsReceived < 0 ) {
				intervalPacketsReceived = 0;
			}
			intervalInvalidPacketsReceived = raw.getInvalidPacketsReceived() - curIntervalInvalidPacketsReceived;
			if ( intervalInvalidPacketsReceived < 0 ) {
				intervalInvalidPacketsReceived = 0;
			}
			intervalGarbageBytesReceived = raw.getGarbageBytesReceived() - curIntervalGarbageBytesReceived;
			if ( intervalGarbageBytesReceived < 0 ) {
				intervalGarbageBytesReceived = 0;
			}
			intervalConnects = raw.getConnects() - curIntervalConnects;
			if ( intervalConnects < 0 ) {
				intervalConnects = 0;
			}
			intervalDisconnects = raw.getDisconnects() - curIntervalDisconnects;
			if ( intervalDisconnects < 0 ) {
				intervalDisconnects = 0;
			}
			intervalExceptions = raw.getExceptions() - curIntervalExceptions;
			if ( intervalExceptions < 0 ) {
				intervalExceptions = 0;
			}

			long sampleTimeMillis = intervalTimeMillis / calcToSampleFactor;
			peakBytesSentRate = peakBytesSent / (sampleTimeMillis / 1000.0);
			peakBytesReceivedRate = peakBytesReceived / (sampleTimeMillis / 1000.0);
			peakMsgsSentRate = peakMsgsSent / (sampleTimeMillis / 1000.0);
			peakMsgsReceivedRate = peakMsgsReceived / (sampleTimeMillis / 1000.0);
			peakPacketsSentRate = peakPacketsSent / (sampleTimeMillis / 1000.0);
			peakPacketsReceivedRate = peakPacketsReceived / (sampleTimeMillis / 1000.0);
			peakInvalidPacketsReceivedRate = peakInvalidPacketsReceived / (sampleTimeMillis / 1000.0);
			peakGarbageBytesReceivedRate = peakGarbageBytesReceived / (sampleTimeMillis / 1000.0);
			peakConnectsRate = peakConnects / (sampleTimeMillis / 1000.0);
			peakDisconnectsRate = peakDisconnects / (sampleTimeMillis / 1000.0);
			peakExceptionsRate = peakExceptions / (sampleTimeMillis / 1000.0);

			avgBytesSentRate = intervalBytesSent / (intervalTimeMillis / 1000.0);
			avgBytesReceivedRate = intervalBytesReceived / (intervalTimeMillis / 1000.0);
			avgMsgsSentRate = intervalMsgsSent / (intervalTimeMillis / 1000.0);
			avgMsgsReceivedRate = intervalMsgsReceived / (intervalTimeMillis / 1000.0);
			avgPacketsSentRate = intervalPacketsSent / (intervalTimeMillis / 1000.0);
			avgPacketsReceivedRate = intervalPacketsReceived / (intervalTimeMillis / 1000.0);
			avgInvalidPacketsReceivedRate = intervalInvalidPacketsReceived / (intervalTimeMillis / 1000.0);
			avgGarbageBytesReceivedRate = intervalGarbageBytesReceived / (intervalTimeMillis / 1000.0);
			avgConnectsRate = intervalConnects / (intervalTimeMillis / 1000.0);
			avgDisconnectsRate = intervalDisconnects / (intervalTimeMillis / 1000.0);
			avgExceptionsRate = intervalExceptions / (intervalTimeMillis / 1000.0);

			// Set HWM values based upon avg rates.
			long hwmTime = System.currentTimeMillis();
			if ( avgBytesSentRate > bytesSentRateHWM ) {
				bytesSentRateHWM = avgBytesSentRate;
				bytesSentRateHWMTime = hwmTime;
			}
			if ( avgBytesReceivedRate > bytesReceivedRateHWM ) {
				bytesReceivedRateHWM = avgBytesReceivedRate;
				bytesReceivedRateHWMTime = hwmTime;
			}
			if ( avgMsgsSentRate > msgsSentRateHWM ) {
				msgsSentRateHWM = avgMsgsSentRate;
				msgsSentRateHWMTime = hwmTime;
			}
			if ( avgMsgsReceivedRate > msgsReceivedRateHWM ) {
				msgsReceivedRateHWM = avgMsgsReceivedRate;
				msgsReceivedRateHWMTime = hwmTime;
			}
			if ( avgPacketsSentRate > packetsSentRateHWM ) {
				packetsSentRateHWM = avgPacketsSentRate;
				packetsSentRateHWMTime = hwmTime;
			}
			if ( avgPacketsReceivedRate > packetsReceivedRateHWM ) {
				packetsReceivedRateHWM = avgPacketsReceivedRate;
				packetsReceivedRateHWMTime = hwmTime;
			}


			// Clear out counters.
			curPeakBytesSent = 0;
			curPeakBytesReceived = 0;
			curPeakMsgsSent = 0;
			curPeakMsgsReceived = 0;
			curPeakPacketsSent = 0;
			curPeakPacketsReceived = 0;
			curPeakInvalidPacketsReceived = 0;
			curPeakGarbageBytesReceived = 0;
			curPeakConnects = 0;
			curPeakDisconnects = 0;
			curPeakExceptions = 0;

			curIntervalBytesSent = 0;
			curIntervalBytesReceived = 0;
			curIntervalMsgsSent = 0;
			curIntervalMsgsReceived = 0;
			curIntervalPacketsSent = 0;
			curIntervalPacketsReceived = 0;
			curIntervalInvalidPacketsReceived = 0;
			curIntervalGarbageBytesReceived = 0;
			curIntervalConnects = 0;
			curIntervalDisconnects = 0;
			curIntervalExceptions = 0;

			intervalTimeMillis = 0;
		}
	}

	public long incSamples() {
		numSamples++;
		return numSamples;
	}

	/**
	 * Gets the value of peakBytesSent
	 *
	 * @return the value of peakBytesSent
	 */
	public long getPeakBytesSent()  {
		synchronized( lock ) {
			return this.peakBytesSent;
		}
	}

	/**
	 * Gets the value of peakBytesReceived
	 *
	 * @return the value of peakBytesReceived
	 */
	public long getPeakBytesReceived()  {
		synchronized( lock ) {
			return this.peakBytesReceived;
		}
	}

	/**
	 * Gets the value of peakMsgsSent
	 *
	 * @return the value of peakMsgsSent
	 */
	public long getPeakMsgsSent()  {
		synchronized( lock ) {
			return this.peakMsgsSent;
		}
	}

	/**
	 * Gets the value of peakMsgsReceived
	 *
	 * @return the value of peakMsgsReceived
	 */
	public long getPeakMsgsReceived()  {
		synchronized( lock ) {
			return this.peakMsgsReceived;
		}
	}

	/**
	 * Gets the value of peakPacketsSent
	 *
	 * @return the value of peakPacketsSent
	 */
	public long getPeakPacketsSent()  {
		synchronized( lock ) {
			return this.peakPacketsSent;
		}
	}

	/**
	 * Gets the value of peakPacketsReceived
	 *
	 * @return the value of peakPacketsReceived
	 */
	public long getPeakPacketsReceived()  {
		synchronized( lock ) {
			return this.peakPacketsReceived;
		}
	}

	/**
	 * Gets the value of peakInvalidPacketsReceived
	 *
	 * @return the value of peakInvalidPacketsReceived
	 */
	public long getPeakInvalidPacketsReceived()  {
		synchronized( lock ) {
			return this.peakInvalidPacketsReceived;
		}
	}

	/**
	 * Gets the value of peakGarbageBytesReceived
	 *
	 * @return the value of peakGarbageBytesReceived
	 */
	public long getPeakGarbageBytesReceived()  {
		synchronized( lock ) {
			return this.peakGarbageBytesReceived;
		}
	}

	/**
	 * Gets the value of peakConnects
	 *
	 * @return the value of peakConnects
	 */
	public long getPeakConnects()  {
		synchronized( lock ) {
			return this.peakConnects;
		}
	}

	/**
	 * Gets the value of peakDisconnects
	 *
	 * @return the value of peakDisconnects
	 */
	public long getPeakDisconnects()  {
		synchronized( lock ) {
			return this.peakDisconnects;
		}
	}

	/**
	 * Gets the value of peakExceptions
	 *
	 * @return the value of peakExceptions
	 */
	public long getPeakExceptions()  {
		synchronized( lock ) {
			return this.peakExceptions;
		}
	}

	/**
	 * Gets the value of intervalBytesSent
	 *
	 * @return the value of intervalBytesSent
	 */
	public long getIntervalBytesSent()  {
		synchronized( lock ) {
			return this.intervalBytesSent;
		}
	}

	/**
	 * Gets the value of intervalBytesReceived
	 *
	 * @return the value of intervalBytesReceived
	 */
	public long getIntervalBytesReceived()  {
		synchronized( lock ) {
			return this.intervalBytesReceived;
		}
	}

	/**
	 * Gets the value of intervalMsgsSent
	 *
	 * @return the value of intervalMsgsSent
	 */
	public long getIntervalMsgsSent()  {
		synchronized( lock ) {
			return this.intervalMsgsSent;
		}
	}

	/**
	 * Gets the value of intervalMsgsReceived
	 *
	 * @return the value of intervalMsgsReceived
	 */
	public long getIntervalMsgsReceived()  {
		synchronized( lock ) {
			return this.intervalMsgsReceived;
		}
	}

	/**
	 * Gets the value of intervalPacketsSent
	 *
	 * @return the value of intervalPacketsSent
	 */
	public long getIntervalPacketsSent()  {
		synchronized( lock ) {
			return this.intervalPacketsSent;
		}
	}

	/**
	 * Gets the value of intervalPacketsReceived
	 *
	 * @return the value of intervalPacketsReceived
	 */
	public long getIntervalPacketsReceived()  {
		synchronized( lock ) {
			return this.intervalPacketsReceived;
		}
	}

	/**
	 * Gets the value of intervalInvalidPacketsReceived
	 *
	 * @return the value of intervalInvalidPacketsReceived
	 */
	public long getIntervalInvalidPacketsReceived()  {
		synchronized( lock ) {
			return this.intervalInvalidPacketsReceived;
		}
	}

	/**
	 * Gets the value of intervalGarbageBytesReceived
	 *
	 * @return the value of intervalGarbageBytesReceived
	 */
	public long getIntervalGarbageBytesReceived()  {
		synchronized( lock ) {
			return this.intervalGarbageBytesReceived;
		}
	}

	/**
	 * Gets the value of intervalConnects
	 *
	 * @return the value of intervalConnects
	 */
	public long getIntervalConnects()  {
		synchronized( lock ) {
			return this.intervalConnects;
		}
	}

	/**
	 * Gets the value of intervalDisconnects
	 *
	 * @return the value of intervalDisconnects
	 */
	public long getIntervalDisconnects()  {
		synchronized( lock ) {
			return this.intervalDisconnects;
		}
	}

	/**
	 * Gets the value of intervalExceptions
	 *
	 * @return the value of intervalExceptions
	 */
	public long getIntervalExceptions()  {
		synchronized( lock ) {
			return this.intervalExceptions;
		}
	}

	/**
	 * Gets the value of peakBytesSentRate
	 *
	 * @return the value of peakBytesSentRate
	 */
	public double getPeakBytesSentRate()  {
		synchronized( lock ) {
			return this.peakBytesSentRate;
		}
	}

	/**
	 * Gets the value of peakBytesReceivedRate
	 *
	 * @return the value of peakBytesReceivedRate
	 */
	public double getPeakBytesReceivedRate()  {
		synchronized( lock ) {
			return this.peakBytesReceivedRate;
		}
	}

	/**
	 * Gets the value of peakMsgsSentRate
	 *
	 * @return the value of peakMsgsSentRate
	 */
	public double getPeakMsgsSentRate()  {
		synchronized( lock ) {
			return this.peakMsgsSentRate;
		}
	}

	/**
	 * Gets the value of peakMsgsReceivedRate
	 *
	 * @return the value of peakMsgsReceivedRate
	 */
	public double getPeakMsgsReceivedRate()  {
		synchronized( lock ) {
			return this.peakMsgsReceivedRate;
		}
	}

	/**
	 * Gets the value of peakPacketsSentRate
	 *
	 * @return the value of peakPacketsSentRate
	 */
	public double getPeakPacketsSentRate()  {
		synchronized( lock ) {
			return this.peakPacketsSentRate;
		}
	}

	/**
	 * Gets the value of peakPacketsReceivedRate
	 *
	 * @return the value of peakPacketsReceivedRate
	 */
	public double getPeakPacketsReceivedRate()  {
		synchronized( lock ) {
			return this.peakPacketsReceivedRate;
		}
	}

	/**
	 * Gets the value of peakInvalidPacketsReceivedRate
	 *
	 * @return the value of peakInvalidPacketsReceivedRate
	 */
	public double getPeakInvalidPacketsReceivedRate()  {
		synchronized( lock ) {
			return this.peakInvalidPacketsReceivedRate;
		}
	}

	/**
	 * Gets the value of peakGarbageBytesReceivedRate
	 *
	 * @return the value of peakGarbageBytesReceivedRate
	 */
	public double getPeakGarbageBytesReceivedRate()  {
		synchronized( lock ) {
			return this.peakGarbageBytesReceivedRate;
		}
	}

	/**
	 * Gets the value of peakConnectsRate
	 *
	 * @return the value of peakConnectsRate
	 */
	public double getPeakConnectsRate()  {
		synchronized( lock ) {
			return this.peakConnectsRate;
		}
	}

	/**
	 * Gets the value of peakDisconnectsRate
	 *
	 * @return the value of peakDisconnectsRate
	 */
	public double getPeakDisconnectsRate()  {
		synchronized( lock ) {
			return this.peakDisconnectsRate;
		}
	}

	/**
	 * Gets the value of peakExceptionsRate
	 *
	 * @return the value of peakExceptionsRate
	 */
	public double getPeakExceptionsRate()  {
		synchronized( lock ) {
			return this.peakExceptionsRate;
		}
	}

	/**
	 * Gets the value of avgBytesSentRate
	 *
	 * @return the value of avgBytesSentRate
	 */
	public double getAvgBytesSentRate()  {
		synchronized( lock ) {
			return this.avgBytesSentRate;
		}
	}

	public double getBytesSentRateHWM() {
		synchronized( lock ) {
			return this.bytesSentRateHWM;
		}
	}

	public long getBytesSentRateHWMTime() {
		synchronized( lock ) {
			return this.bytesSentRateHWMTime;
		}
	}

	/**
	 * Gets the value of avgBytesReceivedRate
	 *
	 * @return the value of avgBytesReceivedRate
	 */
	public double getAvgBytesReceivedRate()  {
		synchronized( lock ) {
			return this.avgBytesReceivedRate;
		}
	}

	public double getBytesReceivedRateHWM() {
		synchronized( lock ) {
			return this.bytesReceivedRateHWM;
		}
	}

	public long getBytesReceivedRateHWMTime() {
		synchronized( lock ) {
			return this.bytesReceivedRateHWMTime;
		}
	}

	/**
	 * Gets the value of avgMsgsSentRate
	 *
	 * @return the value of avgMsgsSentRate
	 */
	public double getAvgMsgsSentRate()  {
		synchronized( lock ) {
			return this.avgMsgsSentRate;
		}
	}

	public double getMsgsSentRateHWM() {
		synchronized( lock ) {
			return this.msgsSentRateHWM;
		}
	}

	public long getMsgsSentRateHWMTime() {
		synchronized( lock ) {
			return this.msgsSentRateHWMTime;
		}
	}

	/**
	 * Gets the value of avgMsgsReceivedRate
	 *
	 * @return the value of avgMsgsReceivedRate
	 */
	public double getAvgMsgsReceivedRate()  {
		synchronized( lock ) {
			return this.avgMsgsReceivedRate;
		}
	}

	public double getMsgsReceivedRateHWM() {
		synchronized( lock ) {
			return this.msgsReceivedRateHWM;
		}
	}

	public long getMsgsReceivedRateHWMTime() {
		synchronized( lock ) {
			return this.msgsReceivedRateHWMTime;
		}
	}

	/**
	 * Gets the value of avgPacketsSentRate
	 *
	 * @return the value of avgPacketsSentRate
	 */
	public double getAvgPacketsSentRate()  {
		synchronized( lock ) {
			return this.avgPacketsSentRate;
		}
	}

	public double getPacketsSentRateHWM() {
		synchronized( lock ) {
			return this.packetsSentRateHWM;
		}
	}

	public long getPacketsSentRateHWMTime() {
		synchronized( lock ) {
			return this.packetsSentRateHWMTime;
		}
	}

	/**
	 * Gets the value of avgPacketsReceivedRate
	 *
	 * @return the value of avgPacketsReceivedRate
	 */
	public double getAvgPacketsReceivedRate()  {
		synchronized( lock ) {
			return this.avgPacketsReceivedRate;
		}
	}

	public double getPacketsReceivedRateHWM() {
		synchronized( lock ) {
			return this.packetsReceivedRateHWM;
		}
	}

	public long getPacketsReceivedRateHWMTime() {
		synchronized( lock ) {
			return this.packetsReceivedRateHWMTime;
		}
	}

	/**
	 * Gets the value of avgInvalidPacketsReceivedRate
	 *
	 * @return the value of avgInvalidPacketsReceivedRate
	 */
	public double getAvgInvalidPacketsReceivedRate()  {
		synchronized( lock ) {
			return this.avgInvalidPacketsReceivedRate;
		}
	}

	/**
	 * Gets the value of avgGarbageBytesReceivedRate
	 *
	 * @return the value of avgGarbageBytesReceivedRate
	 */
	public double getAvgGarbageBytesReceivedRate()  {
		synchronized( lock ) {
			return this.avgGarbageBytesReceivedRate;
		}
	}

	/**
	 * Gets the value of avgConnectsRate
	 *
	 * @return the value of avgConnectsRate
	 */
	public double getAvgConnectsRate()  {
		synchronized( lock ) {
			return this.avgConnectsRate;
		}
	}

	/**
	 * Gets the value of avgDisconnectsRate
	 *
	 * @return the value of avgDisconnectsRate
	 */
	public double getAvgDisconnectsRate()  {
		synchronized( lock ) {
			return this.avgDisconnectsRate;
		}
	}

	/**
	 * Gets the value of avgExceptionsRate
	 *
	 * @return the value of avgExceptionsRate
	 */
	public double getAvgExceptionsRate()  {
		synchronized( lock ) {
			return this.avgExceptionsRate;
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
	 * Gets the value of bytesSent
	 *
	 * @return the value of bytesSent
	 */
	public long getBytesSent()  {
		return raw.getBytesSent();
	}

	/**
	 * Sets the value of bytesSent
	 *
	 * @param argBytesSent Value to assign to this.bytesSent
	 */
	public void setBytesSent(long argBytesSent) {
		synchronized( lock ) {
			long deltaBytesSent = argBytesSent - raw.getBytesSent();
			if ( deltaBytesSent > curPeakBytesSent ) {
				curPeakBytesSent = deltaBytesSent;
			}

			if ( curIntervalBytesSent == 0 ) {
				curIntervalBytesSent = raw.getBytesSent();
			}
		}

		raw.setBytesSent( argBytesSent );
	}

	/**
	 * Increments the value of bytesSent
	 *
	 * @param incAmount Value to increment this.bytesSent
	 */
	public void incBytesSent(long incAmount) {
		raw.incBytesSent( incAmount );
	}

	/**
	 * Gets the value of bytesReceived
	 *
	 * @return the value of bytesReceived
	 */
	public long getBytesReceived()  {
		return raw.getBytesReceived();
	}

	/**
	 * Sets the value of bytesReceived
	 *
	 * @param argBytesReceived Value to assign to this.bytesReceived
	 */
	public void setBytesReceived(long argBytesReceived) {
		synchronized( lock ) {
			long deltaBytesReceived = argBytesReceived - raw.getBytesReceived();
			if ( deltaBytesReceived > curPeakBytesReceived ) {
				curPeakBytesReceived = deltaBytesReceived;
			}

			if ( curIntervalBytesReceived == 0 ) {
				curIntervalBytesReceived = raw.getBytesReceived();
			}
		}

		raw.setBytesReceived( argBytesReceived );
	}

	/**
	 * Increments the value of bytesReceived
	 *
	 * @param incAmount Value to increment this.bytesReceived
	 */
	public void incBytesReceived(long incAmount) {
		raw.incBytesReceived( incAmount );
	}

	/**
	 * Gets the value of msgsSent
	 *
	 * @return the value of msgsSent
	 */
	public long getMsgsSent()  {
		return raw.getMsgsSent();
	}

	/**
	 * Sets the value of msgsSent
	 *
	 * @param argMsgsSent Value to assign to this.msgsSent
	 */
	public void setMsgsSent(long argMsgsSent) {
		synchronized( lock ) {
			long deltaMsgsSent = argMsgsSent - raw.getMsgsSent();
			if ( deltaMsgsSent > curPeakMsgsSent ) {
				curPeakMsgsSent = deltaMsgsSent;
			}

			if ( curIntervalMsgsSent == 0 ) {
				curIntervalMsgsSent = raw.getMsgsSent();
			}
		}

		raw.setMsgsSent( argMsgsSent );
	}

	/**
	 * Increments the value of msgsSent
	 *
	 * @param incAmount Value to increment this.msgsSent
	 */
	public void incMsgsSent(long incAmount) {
		raw.incMsgsSent( incAmount );
	}

	/**
	 * Gets the value of msgsReceived
	 *
	 * @return the value of msgsReceived
	 */
	public long getMsgsReceived()  {
		return raw.getMsgsReceived();
	}

	/**
	 * Sets the value of msgsReceived
	 *
	 * @param argMsgsReceived Value to assign to this.msgsReceived
	 */
	public void setMsgsReceived(long argMsgsReceived) {
		synchronized( lock ) {
			long deltaMsgsReceived = argMsgsReceived - raw.getMsgsReceived();
			if ( deltaMsgsReceived > curPeakMsgsReceived ) {
				curPeakMsgsReceived = deltaMsgsReceived;
			}

			if ( curIntervalMsgsReceived == 0 ) {
				curIntervalMsgsReceived = raw.getMsgsReceived();
			}
		}

		raw.setMsgsReceived( argMsgsReceived );
	}

	/**
	 * Increments the value of msgsReceived
	 *
	 * @param incAmount Value to increment this.msgsReceived
	 */
	public void incMsgsReceived(long incAmount) {
		raw.incMsgsReceived( incAmount );
	}

	/**
	 * Gets the value of packetsSent
	 *
	 * @return the value of packetsSent
	 */
	public long getPacketsSent()  {
		return raw.getPacketsSent();
	}

	/**
	 * Sets the value of packetsSent
	 *
	 * @param argPacketsSent Value to assign to this.packetsSent
	 */
	public void setPacketsSent(long argPacketsSent) {
		synchronized( lock ) {
			long deltaPacketsSent = argPacketsSent - raw.getPacketsSent();
			if ( deltaPacketsSent > curPeakPacketsSent ) {
				curPeakPacketsSent = deltaPacketsSent;
			}

			if ( curIntervalPacketsSent == 0 ) {
				curIntervalPacketsSent = raw.getPacketsSent();
			}
		}

		raw.setPacketsSent( argPacketsSent );
	}

	/**
	 * Increments the value of packetsSent
	 *
	 * @param incAmount Value to increment this.packetsSent
	 */
	public void incPacketsSent(long incAmount) {
		raw.incPacketsSent( incAmount );
	}

	/**
	 * Gets the value of packetsReceived
	 *
	 * @return the value of packetsReceived
	 */
	public long getPacketsReceived()  {
		return raw.getPacketsReceived();
	}

	/**
	 * Sets the value of packetsReceived
	 *
	 * @param argPacketsReceived Value to assign to this.packetsReceived
	 */
	public void setPacketsReceived(long argPacketsReceived) {
		synchronized( lock ) {
			long deltaPacketsReceived = argPacketsReceived - raw.getPacketsReceived();
			if ( deltaPacketsReceived > curPeakPacketsReceived ) {
				curPeakPacketsReceived = deltaPacketsReceived;
			}

			if ( curIntervalPacketsReceived == 0 ) {
				curIntervalPacketsReceived = raw.getPacketsReceived();
			}
		}

		raw.setPacketsReceived( argPacketsReceived );
	}

	/**
	 * Increments the value of packetsReceived
	 *
	 * @param incAmount Value to increment this.packetsReceived
	 */
	public void incPacketsReceived(long incAmount) {
		raw.incPacketsReceived( incAmount );
	}

	/**
	 * Gets the value of invalidPacketsReceived
	 *
	 * @return the value of invalidPacketsReceived
	 */
	public long getInvalidPacketsReceived()  {
		return raw.getInvalidPacketsReceived();
	}

	/**
	 * Sets the value of invalidPacketsReceived
	 *
	 * @param argInvalidPacketsReceived Value to assign to this.invalidPacketsReceived
	 */
	public void setInvalidPacketsReceived(long argInvalidPacketsReceived) {
		synchronized( lock ) {
			long deltaInvalidPacketsReceived = argInvalidPacketsReceived - raw.getInvalidPacketsReceived();
			if ( deltaInvalidPacketsReceived > curPeakInvalidPacketsReceived ) {
				curPeakInvalidPacketsReceived = deltaInvalidPacketsReceived;
			}

			if ( curIntervalInvalidPacketsReceived == 0 ) {
				curIntervalInvalidPacketsReceived = raw.getInvalidPacketsReceived();
			}
		}

		raw.setInvalidPacketsReceived( argInvalidPacketsReceived );
	}

	/**
	 * Increments the value of invalidPacketsReceived
	 *
	 * @param incAmount Value to assign to this.invalidPacketsReceived
	 */
	public void incInvalidPacketsReceived(long incAmount) {
		raw.incInvalidPacketsReceived( incAmount );
	}

	/**
	 * Gets the value of garbageBytesReceived
	 *
	 * @return the value of garbageBytesReceived
	 */
	public long getGarbageBytesReceived()  {
		return raw.getGarbageBytesReceived();
	}

	/**
	 * Sets the value of garbageBytesReceived
	 *
	 * @param argGarbageBytesReceived Value to assign to this.garbageBytesReceived
	 */
	public void setGarbageBytesReceived(long argGarbageBytesReceived) {
		synchronized( lock ) {
			long deltaGarbageBytesReceived = argGarbageBytesReceived - raw.getGarbageBytesReceived();
			if ( deltaGarbageBytesReceived > curPeakGarbageBytesReceived ) {
				curPeakGarbageBytesReceived = deltaGarbageBytesReceived;
			}

			if ( curIntervalGarbageBytesReceived == 0 ) {
				curIntervalGarbageBytesReceived = raw.getGarbageBytesReceived();
			}
		}

		raw.setGarbageBytesReceived( argGarbageBytesReceived );
	}

	/**
	 * Increments the value of garbageBytesReceived
	 *
	 * @param incAmount Value to assign to this.garbageBytesReceived
	 */
	public void incGarbageBytesReceived(long incAmount) {
		raw.incGarbageBytesReceived( incAmount );
	}

	/**
	 * Gets the value of lastTimeSent
	 *
	 * @return the value of lastTimeSent
	 */
	public long getLastTimeSent()  {
		return raw.getLastTimeSent();
	}

	/**
	 * Sets the value of lastTimeSent
	 *
	 * @param argLastTimeSent Value to assign to this.lastTimeSent
	 */
	public void setLastTimeSent(long argLastTimeSent) {
		raw.setLastTimeSent( argLastTimeSent );
	}

	/**
	 * Gets the value of lastTimeReceived
	 *
	 * @return the value of lastTimeReceived
	 */
	public long getLastTimeReceived()  {
		return raw.getLastTimeReceived();
	}

	/**
	 * Sets the value of lastTimeReceived
	 *
	 * @param argLastTimeReceived Value to assign to this.lastTimeReceived
	 */
	public void setLastTimeReceived(long argLastTimeReceived) {
		raw.setLastTimeReceived( argLastTimeReceived );
	}

	/**
	 * Gets the value of status
	 *
	 * @return the value of status
	 */
	public short getStatus()  {
		return raw.getStatus();
	}

	/**
	 * Sets the value of status
	 *
	 * @param argStatus Value to assign to this.status
	 */
	public void setStatus(short argStatus) {
		raw.setStatus( argStatus );
	}


	/**
	 * Gets the value of connects
	 *
	 * @return the value of connects
	 */
	public long getConnects()  {
		return raw.getConnects();
	}

	/**
	 * Sets the value of connects
	 *
	 * @param argConnects Value to assign to this.connects
	 */
	public void setConnects(long argConnects) {
		synchronized( lock ) {
			long deltaConnects = argConnects - raw.getConnects();
			if ( deltaConnects > curPeakConnects ) {
				curPeakConnects = deltaConnects;
			}

			if ( curIntervalConnects == 0 ) {
				curIntervalConnects = raw.getConnects();
			}
		}

		raw.setConnects( argConnects );
	}

	/**
	 * Increments the value of connects
	 *
	 * @param argConnects Value to increment this.connects
	 */
	public void incConnects(long incAmount) {
		raw.incConnects( incAmount );
	}

	/**
	 * Gets the value of disconnects
	 *
	 * @return the value of disconnects
	 */
	public long getDisconnects()  {
		return raw.getDisconnects();
	}

	/**
	 * Sets the value of disconnects
	 *
	 * @param argDisconnects Value to assign to this.disconnects
	 */
	public void setDisconnects(long argDisconnects) {
		synchronized( lock ) {
			long deltaDisconnects = argDisconnects - raw.getDisconnects();
			if ( deltaDisconnects > curPeakDisconnects ) {
				curPeakDisconnects = deltaDisconnects;
			}

			if ( curIntervalDisconnects == 0 ) {
				curIntervalDisconnects = raw.getDisconnects();
			}
		}

		raw.setDisconnects( argDisconnects );
	}

	/**
	 * Increments the value of disconnects
	 *
	 * @param argDisconnects Value to increment this.disconnects
	 */
	public void incDisconnects(long incAmount) {
		raw.incDisconnects( incAmount );
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
	public void incExceptions(long incAmount) {
		raw.incExceptions( incAmount );
	}

	/**
	 * Gets the value of lastConnectTime
	 *
	 * @return the value of lastConnectTime
	 */
	public long getLastConnectTime()  {
		return raw.getLastConnectTime();
	}

	/**
	 * Sets the value of lastConnectTime
	 *
	 * @param argLastConnectTime Value to assign to this.lastConnectTime
	 */
	public void setLastConnectTime(long argLastConnectTime) {
		raw.setLastConnectTime( argLastConnectTime );
	}

	/**
	 * Gets the value of lastDisconnectTime
	 *
	 * @return the value of lastDisconnectTime
	 */
	public long getLastDisconnectTime()  {
		return raw.getLastDisconnectTime();
	}

	/**
	 * Sets the value of lastDisconnectTime
	 *
	 * @param argLastDisconnectTime Value to assign to this.lastDisconnectTime
	 */
	public void setLastDisconnectTime(long argLastDisconnectTime) {
		raw.setLastDisconnectTime( argLastDisconnectTime );
	}

	/**
	 * Gets the value of lastExceptionTime
	 *
	 * @return the value of lastExceptionTime
	 */
	public long getLastExceptionTime()  {
		return raw.getLastExceptionTime();
	}

	/**
	 * Sets the value of lastExceptionTime
	 *
	 * @param argLastExceptionTime Value to assign to this.lastExceptionTime
	 */
	public void setLastExceptionTime(long argLastExceptionTime) {
		raw.setLastExceptionTime( argLastExceptionTime );
	}

	/**
	 * Gets the value of lastException
	 *
	 * @return the value of lastException
	 */
	public Throwable getLastException()  {
		return raw.getLastException();
	}

	/**
	 * Sets the value of lastException
	 *
	 * @param argLastException Value to assign to this.lastException
	 */
	public void setLastException(Throwable argLastException) {
		raw.setLastException( argLastException );
	}
	/**
	 * Copies this NCI to the given NCI.
	 *
	 * @param nci a <code>NetworkConnectionInstrumentor</code> value
	 */
	public void get( NetworkConnectionInstrumentor nci ) {
		raw.get( nci );
	}

	public String getToStringHeader( boolean showUserData, boolean showPrivate ) {
		return "THIS getToStringHeader NOT IMPLEMENTED";
	}

	public String getToStringHeader() {
		return "Name,PeakBytesS,PeakBytesR,PeakMsgsS,PeakMsgsR,PeakPktsS,PeakPktsR,PeakGbgR,PeakConns,PeakDisc,PeakExcepts,IntBytesS,IntBytesR,IntMsgsS,IntMsgsR,IntPktsS,IntPktsR,IntGbgR,IntConns,IntDisc,IntExcepts,PeakBytesSR,PeakBytesRR,PeakMsgsSR,PeakMsgsRR,PeakPktsSR,PeakPktsRR,PeakGbgRR,PeakConnsR,PeakDiscR,PeakExceptR,AvgBytesSR,AvgBytesRR,AvgMsgsSR,AvgMsgsRR,AvgPktsSR,AvgPktsRR,AvgGbgRR,AvgConnsR,AvgDiscR,AvgExceptsR,BytesSRHWM,BytesSRHWMTime,BytesRRHWM,BytesRRHWMTime,MsgsSRHWM,MsgsSRHWMTime,MsgsRRHWM,MsgsRRHWMTime,PacketsSRHWM,PacketsSRHWMTime,PacketsRRHWM,PacketsRRHWMTime";
	}

	public String toString( boolean showUserData, boolean showPrivate, String instNameToUse ) {
		return raw.toString( showUserData, showPrivate, instNameToUse );
	}

	public String toString( String instNameToUse ) {
		return instNameToUse + "," +
			getPeakBytesSent() + "," +
			getPeakBytesReceived() + "," +
			getPeakMsgsSent() + "," +
			getPeakMsgsReceived() + "," +
			getPeakPacketsSent() + "," +
			getPeakPacketsReceived() + "," +
			getPeakGarbageBytesReceived() + "," +
			getPeakConnects() + "," +
			getPeakDisconnects() + "," +
			getPeakExceptions() + "," +
			getIntervalBytesSent() + "," +
			getIntervalBytesReceived() + "," +
			getIntervalMsgsSent() + "," +
			getIntervalMsgsReceived() + "," +
			getIntervalPacketsSent() + "," +
			getIntervalPacketsReceived() + "," +
			getIntervalGarbageBytesReceived() + "," +
			getIntervalConnects() + "," +
			getIntervalDisconnects() + "," +
			getIntervalExceptions() + "," +
			getPeakBytesSentRate() + "," +
			getPeakBytesReceivedRate() + "," +
			getPeakMsgsSentRate() + "," +
			getPeakMsgsReceivedRate() + "," +
			getPeakPacketsSentRate() + "," +
			getPeakPacketsReceivedRate() + "," +
			getPeakGarbageBytesReceivedRate() + "," +
			getPeakConnectsRate() + "," +
			getPeakDisconnectsRate() + "," +
			getPeakExceptionsRate() + "," +
			getAvgBytesSentRate() + "," +
			getAvgBytesReceivedRate() + "," +
			getAvgMsgsSentRate() + "," +
			getAvgMsgsReceivedRate() + "," +
			getAvgPacketsSentRate() + "," +
			getAvgPacketsReceivedRate() + "," +
			getAvgGarbageBytesReceivedRate() + "," +
			getAvgConnectsRate() + "," +
			getAvgDisconnectsRate() + "," +
			getAvgExceptionsRate() + "," +
			getBytesSentRateHWM() + "," +
			dateFormatter.format( new Date( getBytesSentRateHWMTime() ) )+ "," +
			getBytesReceivedRateHWM() + "," +
			dateFormatter.format( new Date( getBytesReceivedRateHWMTime() ) ) + "," +
			getMsgsSentRateHWM() + "," +
			dateFormatter.format( new Date( getMsgsSentRateHWMTime() ) ) + "," +
			getMsgsReceivedRateHWM() + "," +
			dateFormatter.format( new Date( getMsgsReceivedRateHWMTime() ) ) + "," +
			getPacketsSentRateHWM() + "," +
			dateFormatter.format( new Date( getPacketsSentRateHWMTime() ) ) + "," +
			getPacketsReceivedRateHWM() + "," +
			dateFormatter.format( new Date( getPacketsReceivedRateHWMTime() ) );
	}

	public String toString() {
		return toString( getName() );
	}

} // NetworkConnectionInstrumentorCalculatedImpl
