package com.cboe.common.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests to make sure the values set are used when the exist
 */
@Ignore("Executing as part of battery suite")
public class WhenValueExistsForSettings
{
	private static final String PARENT_GROUP_NAME = "System";
	private final Settings settings;
	
	public WhenValueExistsForSettings(Settings settings)
    {
		this.settings = settings;
    }
	

	@Test
	public void integerShouldUseProvided()
	{
		int expectedResult = 1;
		settings.group(PARENT_GROUP_NAME)
			.name("Integer")
			.set(expectedResult);
		
		int result = settings.group(PARENT_GROUP_NAME)
							.name("Integer")
							.defaultValue(1000000000);

		assertThat(result, is(expectedResult));
	}


	@Test
	public void longShouldUseProvided()
	{
		long expectedResult = 1;
		settings.group(PARENT_GROUP_NAME)
				.name("Long")
				.set(expectedResult);
		
		long result = settings.group(PARENT_GROUP_NAME)
							.name("Integer")
							.defaultValue(1000000000);

		assertThat(result, is(expectedResult));
	}

	@Test
	public void floatShouldUseProvided()
	{
		float expectedValue = 1000.0f;
		settings.group(PARENT_GROUP_NAME)
				.name("Float")
				.set(expectedValue);
		
		float result = settings.group(PARENT_GROUP_NAME)
						.name("Float")
						.defaultValue(0.0f);

		assertThat(result, is(expectedValue));

	}
	

	@Test
	public void doubleShouldUseProvided()
	{
		double expectedValue = 35.0;
		settings.group(PARENT_GROUP_NAME)
				.name("Double")
				.set(expectedValue);
		double result = settings.group(PARENT_GROUP_NAME)
							.name("Double")
							.defaultValue(3.0);

		assertThat(result, is(expectedValue));

	}
	

	@Test
	public void booleanShouldUseProvided()
	{
		boolean expectedResult = false;
		settings.group(PARENT_GROUP_NAME)
				.name("Boolean")
				.set(expectedResult);

		boolean result = settings.group(PARENT_GROUP_NAME)
							.name("Boolean")
							.defaultValue(true);

		assertThat(result, is(expectedResult));

	}


	@Test
	public void stringShouldUseProvided()
	{
		String expectedResult = "Kevin";
		settings.group(PARENT_GROUP_NAME)
				.name("String")
				.set(expectedResult);
		
		String result = settings.group(PARENT_GROUP_NAME)
							.name("String")
							.defaultValue("Ryan");

		assertThat(result, is(expectedResult));
	}
	

	@Test
	public void enumShouldUseProvided()
	{
		TestEnum expectedValue = TestEnum.System;
		settings.group(PARENT_GROUP_NAME)
				.name("Enum")
				.enumType(TestEnum.class)
				.set(expectedValue);

				
		TestEnum result = settings.group(PARENT_GROUP_NAME)
							.name("Enum")
							.enumType(TestEnum.class)
							.defaultValue(TestEnum.Default);

		assertThat(result, is(TestEnum.System));
	}
	
	@Test
	public void typeShouldUseProvided()
	{
		settings.group(PARENT_GROUP_NAME)
				.name("Type")
				.type(IFoo.class)
				.set(Foo2.class);
		
		IFoo result = settings.group(PARENT_GROUP_NAME)
							.name("Type")
							.type(IFoo.class)
							.defaultClass(Foo1.class);
		
		Assert.assertTrue(result.getClass() == Foo2.class);
	}
	
	@Test
	public void typeListShouldUseProvided()
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
				.defaultList(new ArrayList<Integer>());
		
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
