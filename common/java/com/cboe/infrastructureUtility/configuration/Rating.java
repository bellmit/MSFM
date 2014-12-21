package com.cboe.infrastructureUtility.configuration;

/**
 * Defines a universal rating that can be applied to dynamic resources
 */
public enum Rating
{
	/** a status cannot be computed */
	Unknown(0),

	/** the pool has made no requests */
	Silent(1),

	/** the pool has a low rate of requests */
	Idle(2),

	/** the current rate is within tolerable limits */
	Acceptable(3),

	/** The current rate is elevated but we still have some threads available */
	Busy(4),

	/** The pool is not large enough to supply the request rate */
	Starved(5)

	;

	/* manually setting rank because java spec says to not depend on order */
	private final int rank;

	private Rating(int rank)
	{
		this.rank = rank;
	}

	/**
	 * Return the worst rating of either this or <code>rating</code>
	 */
	public static Rating pickWorst(Rating a, Rating b)
	{
		return (a.rank > b.rank) ? a : b;
	}

	public boolean isWorst()
	{
		return this == Rating.Starved;
	}

	public static Rating getValueForUtilization(float utilization)
	{
		if (utilization == 1.0f)
		{
			return Rating.Starved;
		}
		else if (utilization > 0.75f)
		{
			return Rating.Busy;
		}
		else if (utilization > 0.25f)
		{
			return Rating.Acceptable;
		}
		else
		{
			return Rating.Idle;
		}
	}
}