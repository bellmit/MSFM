package com.cboe.domain.util;

import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Helper class to get boolean from BOHome.
 *
 */

public class BooleanProperty
{
	private BOHome home;

	private String property;

	private boolean value;

	private String defaultValue;

	private volatile boolean initialized = false;

	public BooleanProperty(BOHome home, String property)
	{
		this.home = home;
		this.property = property;
	}

	public BooleanProperty(BOHome home, String property, String defaultValue)
	{
		this.home = home;
		this.property = property;
		this.defaultValue = defaultValue;
	}

	public boolean get()
	{
		if (!initialized)
		{
			try
			{
				if (defaultValue == null)
				{
					value = Boolean.parseBoolean(home.getProperty(property));
				}
				else
				{
					value = Boolean.parseBoolean(home.getProperty(property, defaultValue));
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