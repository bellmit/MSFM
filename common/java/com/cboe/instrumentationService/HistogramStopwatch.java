package com.cboe.instrumentationService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Stores the records as a histogram. Lost of this class are pulled from the
 * JFreeChart Implementation
 * 
 * This class is not thread safe.
 */
public class HistogramStopwatch implements Stopwatch
{
	private static final DateFormat format = new SimpleDateFormat(
			"EEE, d MMM yyyy HH:mm:ss.S");
	/* used when printing our data */
	private String currentMarkName = "Test";
	private double binWidth;

	/* our actual histo gram */
	private List<HistogramBin> listOfBins;

	private final double maximumNS;
	private final double minimumNS;
	private final int bins;

	private final TimeUnit preferredUnits;
	private double maxValue = Double.MIN_VALUE;

	/**
	 * Build a histogram between the minimum and maximum values with the
	 * specified number of buckets between these numbers.
	 */
	public HistogramStopwatch(final int minimum, final int maximum,
			final int bins, TimeUnit units)
	{
		if (minimum < 0)
			throw new IllegalArgumentException("Can't have negitive minimum");
		if (maximum < 0)
			throw new IllegalArgumentException("Can't have negative maximum");
		if (maximum < minimum)
			throw new IllegalArgumentException(
					"Can't have maximum less than minimum");
		if (bins < 1)
			throw new IllegalArgumentException(
					"Can't have less than one bucket");

		this.minimumNS = units.toNanos(minimum);
		this.maximumNS = units.toNanos(maximum);
		this.bins = bins;
		this.preferredUnits = units;

		binWidth = (maximumNS - minimumNS) / (double) bins;
		listOfBins = new ArrayList<HistogramBin>(bins);

		double lower = minimumNS;
		double upper;

		/*
		 * Build our buckets based on our range
		 */
		for (int i = 0; i < bins; i++)
		{
			HistogramBin bin;
			// make sure bins[bins.length]'s upper boundary ends at maximum
			// to avoid the rounding issue. the bins[0] lower boundary is
			// guaranteed start from min
			if (i == bins - 1)
			{
				bin = new HistogramBin(lower, maximumNS, preferredUnits);
			} else
			{
				upper = minimumNS + (i + 1) * binWidth;
				bin = new HistogramBin(lower, upper, preferredUnits);
				lower = upper;
			}
			listOfBins.add(bin);
		}

	}

	public TimeUnit getPreferredUnits()
	{
		return preferredUnits;
	}

	public String getCurrentMarkName()
	{
		return currentMarkName;
	}

	/**
	 * The implementation version
	 * 
	 * @return
	 */
	private static String getVersion()
	{
		return "1.0";
	}

	/**
	 * Will output the histogram whatever current state it is in.
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Type\t" + getClass().getSimpleName() + "\n");
		builder.append("Version\t" + getVersion() + "\n");
		builder.append("Mark Name\t" + currentMarkName + "\n");
		builder.append("Time\t" + getTime() + "\n");
		builder.append("Units\t" + preferredUnits.toString() + "\n");
		builder.append("Min\t"
				+ (preferredUnits.convert((long) minimumNS * 100,
						TimeUnit.NANOSECONDS) / 100.0) + "\n");
		builder.append("Max\t"
				+ (preferredUnits.convert((long) maximumNS * 100,
						TimeUnit.NANOSECONDS) / 100.0) + "\n");
		builder.append("Buckets\t" + bins + "\n");
		for (HistogramBin bin : listOfBins)
		{
			builder.append(bin + "\n");
		}
		builder.append(maxValue + "\n");
		maxValue = Double.MIN_VALUE;

		return builder.toString();
	}

	public List<HistogramBin> getListOfBins()
	{
		return listOfBins;
	}

	/**
	 * Capture a string representation of the current time
	 * 
	 * @return
	 */
	private String getTime()
	{

		return format.format(new Date(System.currentTimeMillis()));
	}

	@Override
	public final void mark()
	{
		mark("Test");
	}

	@Override
	public void mark(String name)
	{
		// replace whatever we have
		currentMarkName = name;
		for (HistogramBin hb : listOfBins)
		{
			hb.resetCount();
		}
	}

	/**
	 * Update our buckets with the new times.
	 */
	@Override
	public void record(long before, long after, TimeUnit units)
	{
		long durationInMicros = units.toNanos(after - before);

		maxValue = Math.max(maxValue, preferredUnits.convert(durationInMicros, TimeUnit.NANOSECONDS));

		int binIndex = bins - 1;
		if (durationInMicros < maximumNS)
		{
			double fraction = (durationInMicros - minimumNS)
					/ (maximumNS - minimumNS);
			if (fraction < 0.0)
			{
				fraction = 0.0;
			}
			binIndex = (int) (fraction * bins);
			// rounding could result in binIndex being equal to bins
			// which will cause an IndexOutOfBoundsException - see bug
			// report 1553088
			if (binIndex >= bins)
			{
				binIndex = bins - 1;
			}
		}
		HistogramBin bin = (HistogramBin) listOfBins.get(binIndex);
		bin.incrementCount();

	}
}
