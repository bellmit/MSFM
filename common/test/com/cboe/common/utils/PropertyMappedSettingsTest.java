package com.cboe.common.utils;

import java.util.Properties;

/**
 * Test the DSettings implementation against the settings battery test
 */
public class PropertyMappedSettingsTest extends SettingsBattery
{
	@Override
	public Settings buildTestSettings()
	{
		/*
		 * note that we are using our own "properties" here, however we could use
		 * System.getProperties() as well if we wanted to. They both fit the same api.
		 * 
		 * Also, if that's not to our liking we can load an XML or flat file.
		 */
		return new PropertyMappedSettings(new Properties());
	}

}
