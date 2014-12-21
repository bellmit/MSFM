package com.cboe.common.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * A set of more elaborate tests to handle edge cases such as incorrect interfaces or missing class
 * names. This is run as part of a battery suite.
 */
@Ignore("Executing as part of battery suite")
public class WhenLoadingClassesFromSettings
{
	private final Settings settings;

	private static final String PARENT_GROUP_NAME = "ClassLoading";

	public WhenLoadingClassesFromSettings(Settings settings)
	{
		this.settings = settings;
	}

	@Test
	public void shouldUseDefaultOnMissing()
	{
		/* a bit of trickery, we are setting the string because
		 * the class name will always be stored as a string regardless
		 * of impl.
		 */
		settings.group(PARENT_GROUP_NAME)
			.name("shouldUseDefaultOnMissing")
			.set("com.cboe.FakeryClass");
		

		/*
		 * should use the default because we failed to load
		 */
		IFoo defaultValue = new Foo1();
		IFoo result = settings.group(PARENT_GROUP_NAME)
				.name("shouldUseDefaultOnMissing")
				.type(IFoo.class)
				.defaultValue(defaultValue);
		

		assertThat(result, is(defaultValue));
	}
	
	
	
	
	@Test(expected=SettingsException.class)
	public void shouldThrowExceptionOnRequiredAndMissing() throws SettingsException
	{
		/* a bit of trickery, we are setting the string because
		 * the class name will always be stored as a string regardless
		 * of impl.
		 */
		settings.group(PARENT_GROUP_NAME)
			.name("shouldThrowExceptionOnRequiredAndMissing")
			.set("com.cboe.FakeryClass");
		

		/*
		 * should use the default because we failed to load
		 */
		settings.group(PARENT_GROUP_NAME)
				.name("shouldThrowExceptionOnRequiredAndMissing")
				.type(IFoo.class)
				.require();

	}

	@Test
	public void shouldUseDefaultValueOnNonInherited()
	{
		/* a bit of trickery, we are setting the string because
		 * the class name will always be stored as a string regardless
		 * of impl.
		 */
		settings.group(PARENT_GROUP_NAME)
			.name("shouldUseDefaultValueOnNonInherited")
			.set(NonFoo.class.getName());
		

		/*
		 * should use the default because we failed to load
		 */
		IFoo defaultValue = new Foo1();
		IFoo result = settings.group(PARENT_GROUP_NAME)
				.name("shouldUseDefaultValueOnNonInherited")
				.type(IFoo.class)
				.defaultValue(defaultValue);

		assertThat(result, is(defaultValue));
	}
	@Test
	public void shouldUseDefaultClassOnNonInherited()
	{
		/* a bit of trickery, we are setting the string because
		 * the class name will always be stored as a string regardless
		 * of impl.
		 */
		settings.group(PARENT_GROUP_NAME)
			.name("shouldUseDefaultClassOnNonInherited")
			.set(NonFoo.class.getName());
		

		/*
		 * should use the default because we failed to load
		 */
		IFoo result = settings.group(PARENT_GROUP_NAME)
				.name("shouldUseDefaultClassOnNonInherited")
				.type(IFoo.class)
				.defaultClass(Foo2.class);

		Assert.assertTrue(result.getClass() == Foo2.class);
	}
	
	
	
	@Test(expected=SettingsException.class)
	public void shouldThrowExceptionOnRequiredValueAndOnNonInherited() throws SettingsException
	{
		/* a bit of trickery, we are setting the string because
		 * the class name will always be stored as a string regardless
		 * of impl.
		 */
		settings.group(PARENT_GROUP_NAME)
			.name("shouldThrowExceptionOnRequiredValueAndOnNonInherited")
			.set(NonFoo.class.getName());
			

		/*
		 * should use the default because we failed to load
		 */
		settings.group(PARENT_GROUP_NAME)
				.name("shouldThrowExceptionOnRequiredValueAndOnNonInherited")
				.type(IFoo.class)
				.require();

	}

	@Test(expected=SettingsException.class)
	public void shouldThrowExceptionOnMissingDefaultConstructor() throws SettingsException
	{
		settings.group(PARENT_GROUP_NAME)
			.name("shouldThrowExceptionOnMissingDefaultConstructor")
			.type(IFoo.class)
			.defaultValue(new FooWithNonDefaultConstructor(""));
		
		settings.group(PARENT_GROUP_NAME)
				.name("shouldThrowExceptionOnMissingDefaultConstructor")
				.type(IFoo.class)
				.require();
	}
	
	@Test
	public void shouldReturnDefaultOnMissingDefaultConstructor()
	{
		IFoo defaultValue = new Foo2();
		settings.group(PARENT_GROUP_NAME)
			.name("shouldReturnDefaultOnMissingDefaultConstructor")
			.type(IFoo.class)
			.defaultValue(new FooWithNonDefaultConstructor(""));
	
		IFoo result = settings.group(PARENT_GROUP_NAME)
			.name("shouldReturnDefaultOnMissingDefaultConstructor")
			.type(IFoo.class)
			.defaultValue(defaultValue);
		
		assertThat(result, is(defaultValue));
	}
	
	
	@Test
	public void shouldUseDefaultEnumOnMissing()
	{
		/* a bit of trickery, we are setting the string because
		 * the class name will always be stored as a string regardless
		 * of impl.
		 */
		settings.group(PARENT_GROUP_NAME)
			.name("shouldUseDefaultEnumOnMissing")
			.set("com.cboe.FakeryClass");
		

		/*
		 * should use the default because we failed to load
		 */
		TestEnum result = settings.group(PARENT_GROUP_NAME)
				.name("shouldUseDefaultEnumOnMissing")
				.enumType(TestEnum.class)
				.defaultValue(TestEnum.Default);
		

		assertThat(result, is(TestEnum.Default));
	}
	
	
	
	
	@Test(expected=SettingsException.class)
	public void shouldThrowExceptionOnEnumRequiredAndMissing() throws SettingsException
	{
		/* a bit of trickery, we are setting the string because
		 * the class name will always be stored as a string regardless
		 * of impl.
		 */
		settings.group(PARENT_GROUP_NAME)
			.name("shouldThrowExceptionOnEnumRequiredAndMissing")
			.set(Foo1.class.getName());
		

		/*
		 * should use the default because we failed to load
		 */
		settings.group(PARENT_GROUP_NAME)
				.name("shouldThrowExceptionOnEnumRequiredAndMissing")
				.enumType(TestEnum.class)
				.require();

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

	public static class NonFoo
	{

	}

	public static class FooWithNonDefaultConstructor implements IFoo
	{
		public FooWithNonDefaultConstructor(String hi) { }
	}

	private enum TestEnum
	{
		System, Default
	};
}
