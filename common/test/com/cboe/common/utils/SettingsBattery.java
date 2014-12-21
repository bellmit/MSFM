package com.cboe.common.utils;

import org.junit.runner.RunWith;

import com.cboe.test.util.Battery;

/**
 * This battery test will ensure the settings object conforms to specification. To test an
 * implementation of {@link Settings} just subclass this test battery and return your
 * implementation.
 */
@RunWith(Battery.class)
@Battery.Tests({
	WhenValueExistsForSettings.class,
	WhenValueExistsAndExceptionModeSettings.class,
	WhenNoValueProvidedToSettings.class, 
	WhenNoValueAndAlarmModeSettings.class,
	WhenNoValueAndExceptionModeSettings.class,
	WhenLoadingClassesFromSettings.class,
	WhenAccessingGroups.class,
	WhenDescriptionsAreProvided.class})
public abstract class SettingsBattery
{
	@Battery.Factory
	public abstract Settings buildTestSettings();

}
