package com.cboe.infrastructureUtility.configuration;

/**
 * Defines a id for a managed Thread pool.
 */
public class ThreadPoolIdentity
{
	private final String name;

	/**
	 * Sub classes should define how to make these, usually just a name associated with the POA
	 * pool.
	 */
	protected ThreadPoolIdentity(String name)
	{
		if (name == null)
		{
			throw new NullPointerException("Cannot assign null for thread identity");
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
		if (obj instanceof ThreadPoolIdentity == false)
		{
			return false;
		}

		return ((ThreadPoolIdentity) obj).name.equals(name);
	}
}
