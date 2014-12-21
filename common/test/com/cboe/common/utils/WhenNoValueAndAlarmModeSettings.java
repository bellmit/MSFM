package com.cboe.common.utils;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * tests to make sure the settings object will signal alarms if trying access a value that has not
 * been set yet.
 */
@Ignore("Executing as part of battery suite")
public class WhenNoValueAndAlarmModeSettings
{
	private static final String PARENT_GROUP_NAME = "Alarm";
	private final Settings settings;
	private boolean hasAlarmed = false;
	private SettingsEventHandler alarmChecker = new SettingsEventHandler()
	{
		@Override
		public void recordUsedSettingAndValue(String settingName, Object value, String description)
		{
			
		}
		
		@Override
		public void alarmOnMissingSetting(String settingName)
		{
			hasAlarmed = true;
		}
	};
	
	public WhenNoValueAndAlarmModeSettings(Settings settings)
	{
		this.settings = settings;
		settings.add(alarmChecker  );
	}

	/**
	 * Checks to see if the alarm has fired, and resets it's status
	 * @return
	 */
	private boolean checkAndResetAlarmStatus()
	{
		boolean result =  hasAlarmed;
		hasAlarmed = false;
		return result;
	}
	

	@Test
	public void integerShouldAlarm()
	{
		settings.alarmIfNotPresent()
				.group(PARENT_GROUP_NAME)
				.name("Integer")
				.defaultValue(1000000000);
		
		Assert.assertTrue(checkAndResetAlarmStatus());
	}

	@Test
	public void longShouldAlarm()
	{
		
		settings.alarmIfNotPresent()
				.group(PARENT_GROUP_NAME)
				.name("Long")
				.defaultValue(1000000000L);
		
		Assert.assertTrue(checkAndResetAlarmStatus());
	}


	
	@Test
	public void floatShouldAlarm()
	{
		
		settings.alarmIfNotPresent()
			.group(PARENT_GROUP_NAME)
			.name("Float")
			.defaultValue(0.0f);
		
		Assert.assertTrue(checkAndResetAlarmStatus());
	}
	
	
	@Test
	public void doubleShouldAlarm()
	{
		settings.alarmIfNotPresent()
				.group(PARENT_GROUP_NAME)
				.name("Double")
				.defaultValue(3.0);
		
		Assert.assertTrue(checkAndResetAlarmStatus());
	}


	@Test
	public void booleanShouldAlarm()
	{
		settings.alarmIfNotPresent()
				.group(PARENT_GROUP_NAME)
				.name("Boolean")
				.defaultValue(true);

		Assert.assertTrue(checkAndResetAlarmStatus());
	}

	
	
	@Test
	public void stringShouldAlarm()
	{
		settings.alarmIfNotPresent()
				.group(PARENT_GROUP_NAME)
				.name("String")
				.defaultValue("Ryan");
		Assert.assertTrue(checkAndResetAlarmStatus());
	}

	
	@Test
	public void enumShouldAlarm()
	{
		settings.alarmIfNotPresent()
				.group(PARENT_GROUP_NAME)
				.name("Enum")
				.enumType(TestEnum.class)
				.defaultValue(TestEnum.Default);

		Assert.assertTrue(checkAndResetAlarmStatus());
	}
	

	@Test
	public void typeShouldAlarm()
	{
		settings.alarmIfNotPresent()
				.group(PARENT_GROUP_NAME)
				.name("Type")
				.type(IFoo.class)
				.defaultClass(Foo1.class);
		
		Assert.assertTrue(checkAndResetAlarmStatus());
	}
	
	
	
	@Test
	public void objShouldAlarm()
	{
		settings.alarmIfNotPresent()
				.group(PARENT_GROUP_NAME)
				.name("Obj")
				.type(IFoo.class)
				.defaultValue(new Foo2());
		Assert.assertTrue(checkAndResetAlarmStatus());
	}
		
	@Test
	public void typeListShouldAlarm()
	{
		settings.alarmIfNotPresent()
				.group(PARENT_GROUP_NAME)
				.name("ListType")
				.type(Integer.class)
				.defaultList(new ArrayList<Integer>());
		Assert.assertTrue(checkAndResetAlarmStatus());
	}

	public static interface IFoo
	{

	}

	public static class Foo1 implements IFoo
	{

	}

	public static class Foo2 implements IFoo
	{

	}

	private enum TestEnum
	{
		System, Default
	};
}
