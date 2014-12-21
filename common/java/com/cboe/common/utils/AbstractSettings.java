package com.cboe.common.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * A bridge to a handler for common events in a {@link Settings} implementation
 */
abstract class AbstractSettings implements Settings
{

	protected final Set<SettingsEventHandler> handlers = new HashSet<SettingsEventHandler>();

	/** proxy call to our handler if provided */
	protected void alarmOnMissingSetting(String settingName)
	{
		for (SettingsEventHandler handler : handlers)
		{
			handler.alarmOnMissingSetting(settingName);
		}
	}

	/** proxy call to our handler if provide */
	protected void recordUsedSettingAndValue(String settingName, Object value, String description)
	{
		for (SettingsEventHandler handler : handlers)
		{
			handler.recordUsedSettingAndValue(settingName, value, description);
		}

	}

	@Override
	public void add(SettingsEventHandler handler)
	{
		if (handler == null)
			return;
		handlers.add(handler);
	}

	@Override
	public void remove(SettingsEventHandler handler)
	{
		handlers.remove(handler);
	}

}
