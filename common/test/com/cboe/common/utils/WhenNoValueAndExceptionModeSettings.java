package com.cboe.common.utils;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Make sure exceptions are thrown when no values are provided. 
 */
@Ignore("Executing as part of battery suite")
public class WhenNoValueAndExceptionModeSettings
{
	private final Settings settings;
	
	private static final String PARENT_GROUP_NAME = "Exception";
	
	public WhenNoValueAndExceptionModeSettings(Settings settings)
	{
		this.settings = settings;
	}

	@Test(expected=SettingsException.class)
	public void integerShouldExcept() throws SettingsException
	{
		settings.group(PARENT_GROUP_NAME)
				.name("Integer")
				.requireInteger();
	}

	@Test(expected=SettingsException.class)
	public void longShouldExcept() throws SettingsException
	{
		
		settings.group(PARENT_GROUP_NAME)
				.name("Long")
				.requireLong();
	}


	
	@Test(expected=SettingsException.class)
	public void floatShouldExcept() throws SettingsException
	{
		
		settings.group(PARENT_GROUP_NAME)
			.name("Float")
			.requireFloat();
	}
	
	
	@Test(expected=SettingsException.class)
	public void doubleShouldExcept() throws SettingsException
	{
		settings.group(PARENT_GROUP_NAME)
				.name("Double")
				.requireDouble();
	}


	@Test(expected=SettingsException.class)
	public void booleanShouldExcept() throws SettingsException
	{
		settings.group(PARENT_GROUP_NAME)
				.name("Boolean")
				.requireBoolean();
	}

	
	
	@Test(expected=SettingsException.class)
	public void stringShouldExcept() throws SettingsException
	{
		settings.group(PARENT_GROUP_NAME)
				.name("String")
				.requireString();
	}

	
	@Test(expected=SettingsException.class)
	public void enumShouldExcept() throws SettingsException
	{
		settings.group(PARENT_GROUP_NAME)
				.name("Enum")
				.enumType(TestEnum.class)
				.require();
	}
	

	@Test(expected=SettingsException.class)
	public void typeShouldExcept() throws SettingsException
	{
		settings.group(PARENT_GROUP_NAME)
				.name("Type")
				.type(IFoo.class)
				.require();
	}
	
	@Test(expected=SettingsException.class)
	public void typeListShouldExcept() throws SettingsException
	{
		settings.group(PARENT_GROUP_NAME)
				.name("ListType")
				.type(Integer.class)
				.requireList();
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
