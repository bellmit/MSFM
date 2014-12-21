package com.cboe.infrastructureUtility.configuration;

/**
 * Uniquely identify a connection end-point. This object is intended to track and store connections.
 */
public class ConnectionIdentity
{
	private final String name;

	/**
	 * Sub classes should define how to make these
	 */
	protected ConnectionIdentity(String name)
	{
		if (name == null)
		{
			throw new NullPointerException("Cannot assign null for connection identity");
		}

		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj.hashCode() != hashCode())
		{
			return false;
		}
		if (obj instanceof ConnectionIdentity == false)
		{
			return false;
		}

		return ((ConnectionIdentity) obj).name.equals(name);
	}

}
