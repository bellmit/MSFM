package com.cboe.common.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Make sure exceptions are NOT thrown when the values are provided and we are using the required
 * interface 
 */
@Ignore("Executing as part of battery suite")
public class WhenValueExistsAndExceptionModeSettings
{
	private final Settings settings;
	
	private static final String PARENT_GROUP_NAME = "NoException";
	
	public WhenValueExistsAndExceptionModeSettings(Settings settings)
	{
		this.settings = settings;
	}

	@Test
	public void integerShouldUseProvided() throws SettingsException
	{
		int expectedResult = 1;
		settings.group(PARENT_GROUP_NAME)
			.name("Integer")
			.set(expectedResult);
		
		int result = settings.group(PARENT_GROUP_NAME)
							.name("Integer")
							.requireInteger();

		assertThat(result, is(expectedResult));
	}


	@Test
	public void longShouldUseProvided() throws SettingsException
	{
		long expectedResult = 1;
		settings.group(PARENT_GROUP_NAME)
				.name("Long")
				.set(expectedResult);
		
		long result = settings.group(PARENT_GROUP_NAME)
							.name("Integer")
							.requireLong();

		assertThat(result, is(expectedResult));
	}

	@Test
	public void floatShouldUseProvided() throws SettingsException
	{
		float expectedValue = 1000.0f;
		settings.group(PARENT_GROUP_NAME)
				.name("Float")
				.set(expectedValue);
		
		float result = settings.group(PARENT_GROUP_NAME)
						.name("Float")
						.requireFloat();
		assertThat(result, is(expectedValue));

	}
	

	@Test
	public void doubleShouldUseProvided() throws SettingsException
	{
		double expectedValue = 35.0;
		settings.group(PARENT_GROUP_NAME)
				.name("Double")
				.set(expectedValue);
		double result = settings.group(PARENT_GROUP_NAME)
							.name("Double")
							.requireDouble();

		assertThat(result, is(expectedValue));

	}
	

	@Test
	public void booleanShouldUseProvided() throws SettingsException
	{
		boolean expectedResult = true;
		settings.group(PARENT_GROUP_NAME)
				.name("Boolean")
				.set(expectedResult);

		boolean result = settings.group(PARENT_GROUP_NAME)
							.name("Boolean")
							.requireBoolean();

		assertThat(result, is(expectedResult));

	}


	@Test
	public void stringShouldUseProvided() throws SettingsException
	{
		String expectedResult = "Kevin";
		settings.group(PARENT_GROUP_NAME)
				.name("String")
				.set(expectedResult);
		
		String result = settings.group(PARENT_GROUP_NAME)
							.name("String")
							.requireString();

		assertThat(result, is(expectedResult));
	}
	

	@Test
	public void enumShouldUseProvided() throws SettingsException
	{
		TestEnum expectedValue = TestEnum.System;
		settings.group(PARENT_GROUP_NAME)
				.name("Enum")
				.enumType(TestEnum.class)
				.set(expectedValue);

				
		TestEnum result = settings.group(PARENT_GROUP_NAME)
							.name("Enum")
							.enumType(TestEnum.class)
							.require();

		assertThat(result, is(TestEnum.System));
	}
	
	@Test
	public void typeShouldUseProvided() throws SettingsException
	{
		settings.group(PARENT_GROUP_NAME)
				.name("Type")
				.type(IFoo.class)
				.set(Foo2.class);
		
		IFoo result = settings.group(PARENT_GROUP_NAME)
							.name("Type")
							.type(IFoo.class)
							.require();
		
		Assert.assertTrue(result.getClass() == Foo2.class);
	}
	
	@Test
	public void typeListShouldUseProvided() throws SettingsException
	{
		ArrayList<Integer> expected = new ArrayList<Integer>();
		expected.add(1);
		expected.add(2);
		expected.add(3);
		
		settings.group(PARENT_GROUP_NAME)
				.name("ListType")
				.type(Integer.class)
				.set(expected);
		
		List<Integer> result = settings.group(PARENT_GROUP_NAME)
				.name("ListType")
				.type(Integer.class)
				.requireList();
		
		Assert.assertArrayEquals(expected.toArray(),result.toArray());
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
