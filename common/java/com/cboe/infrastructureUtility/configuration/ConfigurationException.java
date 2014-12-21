package com.cboe.infrastructureUtility.configuration;

/**
 * Defines a problem with configuration associated with connections.
 */
public class ConfigurationException extends Exception
{

	public ConfigurationException(String description)
	{
		super(description);
	}

	public ConfigurationException()
	{
	}

}
