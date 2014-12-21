package com.cboe.infrastructureUtility.configuration;

/**
 * A memento of connection information recorded in the {@link ConnectionPoolController}
 */
public final class ConnectionMetric
{
	public enum Side
	{
		Primary, Secondary, Undefined
	};

	private final long totalCalls;
	private final long callHWM;
	private final float callRate;

	private final int activeConnectionHWM;
	private final int availableConnections;

	private final Rating rating;
	private final Side side;

	private static final ConnectionMetric undefined = new ConnectionMetric(Side.Undefined, 0L, 0L, 0.0f, 0, 0, Rating.Unknown);

	/**
	 * returns the undefined connection metris
	 * 
	 * @return
	 */
	public static final ConnectionMetric getUndefined()
	{
		return undefined;
	}

	/**
	 * Builds a new metric
	 */
	public ConnectionMetric(Side side, long totalCalls, long callHWM, float callRate, int activeConnectionHWM, int availableConnections, Rating rating)
	{
		this.side = side;
		this.totalCalls = totalCalls;
		this.callHWM = callHWM;
		this.callRate = callRate;
		this.activeConnectionHWM = activeConnectionHWM;
		this.availableConnections = availableConnections;
		this.rating = rating;
	}

	public Side getSide()
	{
		return side;
	}

	public long getTotalCalls()
	{
		return totalCalls;
	}

	public long getCallHWM()
	{
		return callHWM;
	}

	public float getCallRate()
	{
		return callRate;
	}

	public int getActiveConnectionHWM()
	{
		return activeConnectionHWM;
	}

	public int getAvailableConnections()
	{
		return availableConnections;
	}

	public Rating getRating()
	{
		return rating;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		builder.append("[");

		builder.append(String.format("Side=%s,", getSide()));
		builder.append(String.format("Call Rate=%f,", getCallRate()));
		builder.append(String.format("TotalCalls=%d,", getTotalCalls()));
		builder.append(String.format("CallHWM=%d,", getCallHWM()));
		builder.append(String.format("AvailConn=%d,", getAvailableConnections()));
		builder.append(String.format("ActiveConnHWM=%d,", getActiveConnectionHWM()));

		builder.append("]");
		return builder.toString();
	}

}
