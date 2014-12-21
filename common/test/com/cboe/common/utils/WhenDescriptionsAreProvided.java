package com.cboe.common.utils;

import org.junit.Ignore;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests to make sure description related information is correctly passed along
 */
@Ignore("Executing as part of battery suite")
public class WhenDescriptionsAreProvided
{
	private final Settings settings;
	private String lastDescription = null;

	private final SettingsEventHandler handler = new SettingsEventHandler()
	{

		@Override
		public void recordUsedSettingAndValue(String settingName, Object value, String description)
		{
			lastDescription = description;

		}

		@Override
		public void alarmOnMissingSetting(String settingName)
		{
		}
	};

	/*
	 * uses injection
	 */
	public WhenDescriptionsAreProvided(Settings settings)
	{
		this.settings = settings;
		settings.add(handler);
	}

	@Test
	public void notificationShouldReceiveValueForBoolean()
	{
		String description = "notificationShouldReceiveValueForBoolean";
		settings.group("DescriptionsProvided")
				.name("notificationShouldReceiveValueForBoolean")
				.description(description)
				.defaultValue(true);
		
		assertThat(description, is(lastDescription));
	}
	
	@Test
	public void notificationShouldReceiveValueForInteger()
	{
		String description = "notificationShouldReceiveValueForInteger";
		settings.group("DescriptionsProvided")
				.name("notificationShouldReceiveValueForInteger")
				.description(description)
				.defaultValue(1);
		
		assertThat(description, is(lastDescription));
	}
	
	@Test
	public void notificationShouldReceiveValueForLong()
	{
		String description = "notificationShouldReceiveValueForLong";
		settings.group("DescriptionsProvided")
				.name("notificationShouldReceiveValueForLong")
				.description(description)
				.defaultValue(1L);
		
		assertThat(description, is(lastDescription));
	}
	
	@Test
	public void notificationShouldReceiveValueForDouble()
	{
		String description = "notificationShouldReceiveValueForDouble";
		settings.group("DescriptionsProvided")
				.name("notificationShouldReceiveValueForDouble")
				.description(description)
				.defaultValue(0.0);
		
		assertThat(description, is(lastDescription));
	}
	
	@Test
	public void notificationShouldReceiveValueForFloat()
	{
		String description = "notificationShouldReceiveValueForFloat";
		settings.group("DescriptionsProvided")
				.name("notificationShouldReceiveValueForFloat")
				.description(description)
				.defaultValue(0.0f);
		
		assertThat(description, is(lastDescription));
	}
	
	@Test
	public void notificationShouldReceiveValueForString()
	{
		String description = "notificationShouldReceiveValueForString";
		settings.group("DescriptionsProvided")
				.name("notificationShouldReceiveValueForString")
				.description(description)
				.defaultValue("");
		
		assertThat(description, is(lastDescription));
	}
	
	

}
