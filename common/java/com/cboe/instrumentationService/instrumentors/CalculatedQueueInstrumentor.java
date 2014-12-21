package com.cboe.instrumentationService.instrumentors;

/**
 * CalculatedQueueInstrumentor.java
 *
 *
 * Created: Thu Sep 18 09:40:48 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface CalculatedQueueInstrumentor extends CalculatedInstrumentor {

	public long getPeakEnqueued();
	public long getPeakDequeued();
	public long getPeakFlushed();
	public long getPeakOverlaid();
	public long getPeakFlips();
	public long getPeakFlipVolume();
	public long getPeakEnqueueWaits();
	public long getPeakDequeueWaits();
	public long getPeakDequeueTimeouts();
	public long getIntervalEnqueued();
	public long getIntervalDequeued();
	public long getIntervalFlushed();
	public long getIntervalOverlaid();
	public long getIntervalFlips();
	public long getIntervalFlipVolume();
	public long getIntervalEnqueueWaits();
	public long getIntervalDequeueWaits();
	public long getIntervalDequeueTimeouts();

	public double getPeakEnqueuedRate();
	public double getPeakDequeuedRate();
	public double getPeakFlushedRate();
	public double getPeakOverlaidRate();
	public double getPeakFlipsRate();
	public double getPeakFlipVolumeRate();
	public double getPeakEnqueueWaitsRate();
	public double getPeakDequeueWaitsRate();
	public double getPeakDequeueTimeoutsRate();
	public double getAvgEnqueuedRate();
	public double getAvgDequeuedRate();
	public double getAvgFlushedRate();
	public double getAvgOverlaidRate();
	public double getAvgFlipsRate();
	public double getAvgFlipVolumeRate();
	public double getAvgEnqueueWaitsRate();
	public double getAvgDequeueWaitsRate();
	public double getAvgDequeueTimeoutsRate();

} // CalculatedQueueInstrumentor
