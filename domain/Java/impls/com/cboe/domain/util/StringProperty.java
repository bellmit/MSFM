package com.cboe.domain.util;

import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Helper class to get integer properties from BOHome.
 * @author singh
 *
 */

public class StringProperty
{
	private BOHome home;

	private String property;

	private String value;

	private String defaultValue;

	private volatile boolean initialized = false;

	public StringProperty(BOHome home, String property)
	{
		this.home = home;
		this.property = property;
	}

	public StringProperty(BOHome home, String property, String defaultValue)
	{
		this.home = home;
		this.property = property;
		this.defaultValue = defaultValue;
	}

	public String get()
	{
		if (!initialized)
		{
			try
			{
				if (defaultValue == null)
				{
					value = home.getProperty(property);
				}
				else
				{
					value = home.getProperty(property, defaultValue);
				}
				
				if (value != null)
				{
					value = value.trim();
				}

				initialized = true;

				Log.information(home, "Property=" + property + ", Value=" + value);
			}
			catch (Exception e)
			{
				Log.alarm(home, "Failed to find property " + property);
				Log.exception(home, e);
				throw new IllegalArgumentException("Failed to find property " + property);
			}
		}

		return value;
	}
}