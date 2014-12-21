package com.cboe.domain.util;

import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Helper class to get integer properties from BOHome.
 * @author singh
 *
 */

public class IntProperty
{
	private BOHome home;

	private String property;

	private int value;

	private String defaultValue;

	private volatile boolean initialized = false;

	public IntProperty(BOHome home, String property)
	{
		this.home = home;
		this.property = property;
	}

	public IntProperty(BOHome home, String property, String defaultValue)
	{
		this.home = home;
		this.property = property;
		this.defaultValue = defaultValue;
	}

	public int get()
	{
		if (!initialized)
		{
			try
			{
				if (defaultValue == null)
				{
					value = Integer.parseInt(home.getProperty(property));
				}
				else
				{
					value = Integer.parseInt(home.getProperty(property, defaultValue));
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