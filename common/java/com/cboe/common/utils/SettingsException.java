package com.cboe.common.utils;

/**
 * There are issues with a {@link Settings} call
 */
public class SettingsException extends Exception
{
	public SettingsException(String description)
	{
		super(description);
	}
}
