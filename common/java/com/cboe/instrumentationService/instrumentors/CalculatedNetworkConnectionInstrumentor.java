package com.cboe.instrumentationService.instrumentors;

/**
 * CalculatedNetworkConnectionInstrumentor.java
 *
 *
 * Created: Thu Sep 18 09:48:53 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface CalculatedNetworkConnectionInstrumentor extends CalculatedInstrumentor {

	public long getPeakBytesSent();
	public long getPeakBytesReceived();
	public long getPeakMsgsSent();
	public long getPeakMsgsReceived();
	public long getPeakPacketsSent();
	public long getPeakPacketsReceived();
	public long getPeakInvalidPacketsReceived();
	public long getPeakGarbageBytesReceived();
	public long getPeakConnects();
	public long getPeakDisconnects();
	public long getPeakExceptions();
	public long getIntervalBytesSent();
	public long getIntervalBytesReceived();
	public long getIntervalMsgsSent();
	public long getIntervalMsgsReceived();
	public long getIntervalPacketsSent();
	public long getIntervalPacketsReceived();
	public long getIntervalInvalidPacketsReceived();
	public long getIntervalGarbageBytesReceived();
	public long getIntervalConnects();
	public long getIntervalDisconnects();
	public long getIntervalExceptions();

	public double getPeakBytesSentRate();
	public double getPeakBytesReceivedRate();
	public double getPeakMsgsSentRate();
	public double getPeakMsgsReceivedRate();
	public double getPeakPacketsSentRate();
	public double getPeakPacketsReceivedRate();
	public double getPeakInvalidPacketsReceivedRate();
	public double getPeakGarbageBytesReceivedRate();
	public double getPeakConnectsRate();
	public double getPeakDisconnectsRate();
	public double getPeakExceptionsRate();
	public double getAvgBytesSentRate();
	public double getAvgBytesReceivedRate();
	public double getAvgMsgsSentRate();
	public double getAvgMsgsReceivedRate();
	public double getAvgPacketsSentRate();
	public double getAvgPacketsReceivedRate();
	public double getAvgInvalidPacketsReceivedRate();
	public double getAvgGarbageBytesReceivedRate();
	public double getAvgConnectsRate();
	public double getAvgDisconnectsRate();
	public double getAvgExceptionsRate();

	public double getBytesSentRateHWM();
	public long getBytesSentRateHWMTime();
	public double getBytesReceivedRateHWM();
	public long getBytesReceivedRateHWMTime();
	public double getMsgsSentRateHWM();
	public long getMsgsSentRateHWMTime();
	public double getMsgsReceivedRateHWM();
	public long getMsgsReceivedRateHWMTime();
	public double getPacketsSentRateHWM();
	public long getPacketsSentRateHWMTime();
	public double getPacketsReceivedRateHWM();
	public long getPacketsReceivedRateHWMTime();

} // CalculatedNetworkConnectionInstrumentor
