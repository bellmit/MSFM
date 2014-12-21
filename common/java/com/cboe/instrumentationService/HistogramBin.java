package com.cboe.instrumentationService;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Just holds our data we have bucketed
 */
final public class HistogramBin
{

	/** The number of items in the bin. */
	private AtomicInteger count;

	/** The start boundary. */
	private double startBoundary;

	/** The end boundary. */
	private double endBoundary;

	private TimeUnit units;

	/**
	 * Creates a new bin.
	 * 
	 * @param startBoundary
	 *            the start boundary.
	 * @param endBoundary
	 *            the end boundary.
	 */
	public HistogramBin(double startBoundary, double endBoundary, TimeUnit units)
	{
		if (startBoundary > endBoundary)
		{
			throw new IllegalArgumentException(
					"HistogramBin():  startBoundary > endBoundary.");
		}
		this.units = units;
		this.count = new AtomicInteger();
		this.startBoundary = startBoundary;
		this.endBoundary = endBoundary;
	}

	@Override
	public String toString()
	{
		// time with .00
		double time = getStartBoundary();
		return String.format("%f,%d", time, getCount());
	}

	/**
	 * Resets our counter
	 */
	public void resetCount()
	{
		this.count.set(0);
	}

	/**
	 * Returns the number of items in the bin.
	 * 
	 * @return The item count.
	 */
	public int getCount()
	{
		return this.count.get();
	}

	/**
	 * Increments the item count.
	 */
	public void incrementCount()
	{
		this.count.incrementAndGet();
	}

	/**
	 * Returns the start boundary.
	 * 
	 * @return The start boundary.
	 */
	public double getStartBoundary()
	{
		return units.convert((long)(this.startBoundary*1000.0), TimeUnit.NANOSECONDS) / 1000.0;
	}

	/**
	 * Returns the end boundary.
	 * 
	 * @return The end boundary.
	 */
	public double getEndBoundary()
	{
		return this.endBoundary;
	}

	/**
	 * Returns the bin width.
	 * 
	 * @return The bin width.
	 */
	public double getBinWidth()
	{
		return this.endBoundary - this.startBoundary;
	}

	/**
	 * Tests this object for equality with an arbitrary object.
	 * 
	 * @param obj
	 *            the object to test against.
	 * 
	 * @return A boolean.
	 */
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (obj == this)
		{
			return true;
		}
		if (obj instanceof HistogramBin)
		{
			HistogramBin bin = (HistogramBin) obj;
			boolean b0 = bin.startBoundary == this.startBoundary;
			boolean b1 = bin.endBoundary == this.endBoundary;
			boolean b2 = bin.count == this.count;
			return b0 && b1 && b2;
		}
		return false;
	}

}