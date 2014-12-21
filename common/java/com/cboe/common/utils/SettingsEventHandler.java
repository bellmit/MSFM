package com.cboe.common.utils;

/**
 * Respond to events generated from the handler
 */
public interface SettingsEventHandler
{
	/** record that a setting name was not provided when it was required */
	public void alarmOnMissingSetting(String settingName);

	/** record the use of the provided setting */
	public void recordUsedSettingAndValue(String settingName, Object value, String description);
}
