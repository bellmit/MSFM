package com.cboe.common.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Values should use the provided default if no setting exists
 */
@Ignore("Executing as part of battery suite")
public class WhenNoValueProvidedToSettings
{

	private static final String PARENT_GROUP_NAME = "Default";
	private final Settings settings;

	/*
	 * uses injection
	 */
	public WhenNoValueProvidedToSettings(Settings settings)
	{
		this.settings = settings;
	}

	@Test
	public void integerShouldUseDefault()
	{
		int defaultValue = 100;
		int result = settings.group(PARENT_GROUP_NAME)
				.name("Integer")
				.defaultValue(defaultValue);

		assertThat(result, is(defaultValue));
	}

	@Test
	public void longShouldUseDefault()
	{

		long defaultValue = 100L;
		long result = settings.group(PARENT_GROUP_NAME)
				.name("Long")
				.defaultValue(defaultValue);

		assertThat(result, is(defaultValue));
	}

	@Test
	public void floatShouldUseDefault()
	{
		float expectedValue = 1.0f;

		float result = settings.group(PARENT_GROUP_NAME)
				.name("Float")
				.defaultValue(expectedValue);

		assertThat(result, is(expectedValue));

	}

	@Test
	public void doubleShouldUseDefault()
	{
		double expectedValue = 35.0;

		double result = settings.group(PARENT_GROUP_NAME)
				.name("Double")
				.defaultValue(expectedValue);

		assertThat(result, is(expectedValue));

	}

	@Test
	public void booleanShouldUseDefault()
	{
		boolean expectedResult = true;

		boolean result = settings.group(PARENT_GROUP_NAME)
				.name("Boolean")
				.defaultValue(expectedResult);

		assertThat(result, is(expectedResult));

	}

	@Test
	public void stringShouldUseDefault()
	{
		String expectedResult = "Ryan";

		String result = settings.group(PARENT_GROUP_NAME)
				.name("String")
				.defaultValue("Ryan");

		assertThat(result, is(expectedResult));

	}

	@Test
	public void enumShouldUseDefault()
	{

		TestEnum result = settings.group(PARENT_GROUP_NAME)
				.name("Enum")
				.enumType(TestEnum.class)
				.defaultValue(TestEnum.Default);

		assertThat(result, is(TestEnum.Default));

	}

	@Test
	public void typeShouldUseDefault()
	{

		IFoo result = settings.group(PARENT_GROUP_NAME)
				.name("Type")
				.type(IFoo.class)
				.defaultClass(Foo1.class);

		Assert.assertTrue(result.getClass() == Foo1.class);
	}

	@Test
	public void objShouldUseDefault()
	{
		IFoo defaultValue = new Foo1();

		IFoo result = settings.group(PARENT_GROUP_NAME)
				.name("Obj")
				.type(IFoo.class)
				.defaultValue(defaultValue);

		assertThat(result, is(defaultValue));
	}
	
	@Test
	public void typeListShouldUseDefault()
	{
		ArrayList<Integer> expected = new ArrayList<Integer>();
		expected.add(1);
		expected.add(2);
		expected.add(3);
	
		List<Integer> result = settings.group(PARENT_GROUP_NAME)
				.name("ListType")
				.type(Integer.class)
				.defaultList(expected);
		
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
