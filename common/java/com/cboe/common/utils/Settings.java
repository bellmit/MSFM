package com.cboe.common.utils;

import java.util.List;

/**
 * <p>
 * Provide access and type conversion for system settings. This API is original intended to help
 * with accessing and managing -D settings, although long term this will be mapped onto other
 * property management systems.
 * 
 * This api is a fluent-interface. Requests look usually like this:
 * </p>
 * <code>
 * int value = settings.group("foo")
 * 			.name("bar")
 * 			.defaultValue(100);
 * </code>
 * 
 * <p>
 * The 'withDefault()' method will define the return type for the access. If a boolean was provided
 * instead of 100, then the return type would be a boolean. In the above example, if the reference
 * implementation is a -D variant it would look for -Dfoo.bar=xxx
 * 
 * In some cases parameters should be provided and if they are not defined it really is an error. It
 * would be useful to halt the system in these cases, however this can be dangerous for production,
 * therefore an alarming setup was used instead. Any group with .alarmIfNotPresent() will generate
 * an alarm if the default value is chosen.
 * 
 * For example:
 * </p>
 * 
 * <code>
 * int value = settings.alarmIfNotPresent()
 * 			.group("foo")
 * 			.name("bar")
 * 			.defaultValue(100);
 * </code>
 * <p>
 * will be generated if the foo.bar settings is not defined and 100 is chosen as a default. In
 * addition to the default types you can load enums and objects
 * </p>
 * 
 */
public interface Settings
{

	/**
	 * It's not usually recommended to to expose constants in the interface, however this delimiter
	 * is important. When building and accessing groups the following are equivalent:
	 * 
	 * <code>
	 * settings.group("one").group("two") and settings.group("one" + DELIMETER + "two")
	 * </code> or more informally:<code>settings.group("one.two")</code>
	 * 
	 */
	public static final String DELIMETER = ".";

	/** Add an event handler for settings changes */
	public void add(SettingsEventHandler handler);

	/** Remove a previously added event handler for settings changes */
	public void remove(SettingsEventHandler handler);

	/** Get the subsettings of the provided name */
	public Settings group(String value);

	/** Get the setting with the provided name */
	public SettingValue name(String name);

	/** Indicates all subsettings should produce alarms if the defaults are used */
	public Settings alarmIfNotPresent();

	/** Does this settings object have alarm mode enabled */
	public boolean isAlarmIfNotPresent();

	/** Define the property return type and default value */
	public interface SettingValue
	{
		/** Doesn't actually get the value, just sees if there is a system value for it */
		public boolean isDefined();

		/** optional description that will be exported based on the implementation rules */
		public SettingValue description(String description);

		/** return type of boolean with provided defaultValue */
		public boolean defaultValue(boolean defaultValue);

		/** override the set value */
		public void set(boolean value);

		/** Ensure the value exists otherwise throw an exception */
		public boolean requireBoolean() throws SettingsException;

		/** return type of int with provided defaultValue */
		public int defaultValue(int defaultValue);

		/** override the set value */
		public void set(int value);

		/** Ensure the value exists otherwise throw an exception */
		public int requireInteger() throws SettingsException;

		/** return type of double with provided defaultValue */
		public double defaultValue(double defaultValue);

		/** override the set value */
		public void set(double value);

		/** Ensure the value exists otherwise throw an exception */
		public double requireDouble() throws SettingsException;

		/** return type of float with provided defaultValue */
		public float defaultValue(float defaultValue);

		/** override the set value */
		public void set(float value);

		/** Ensure the value exists otherwise throw an exception */
		public float requireFloat() throws SettingsException;

		/** return type of long with provided default value */
		public long defaultValue(long defaultValue);

		/** override the set value */
		public void set(long value);

		/** Ensure the value exists otherwise throw an exception */
		public long requireLong() throws SettingsException;

		/** return type of String with provided defaultValue */
		public String defaultValue(String defaultValue);

		/** override the set value */
		public void set(String value);

		/** Ensure the value exists otherwise throw an exception */
		public String requireString() throws SettingsException;

		/** return type of provided type */
		public <T> TypeSetting<T> type(Class<T> type);

		/** return the enum of the provided Type */
		public <E extends Enum<E>> EnumSetting<E> enumType(Class<E> type);

	}

	/** Define a default type for the class */
	public interface TypeSetting<T>
	{
		/** Default concrete type to build */
		public T defaultClass(Class<? extends T> type);

		/** Default instantiated object to use if none is provided */
		public T defaultValue(T object);

		/** override the set value, will use type.getName() */
		public void set(Class<? extends T> type);

		/** Ensure the value exists otherwise throw an exception */
		public T require() throws SettingsException;

		/** returns an array of items */
		public List<T> defaultList(List<T> data);

		/** require the array be returned, type T must have a constructor of type T */
		public List<T> requireList() throws SettingsException;

		/**
		 * Set the list, whatever type <T> is requires that it has a constructor that takes a string
		 * and this is transitive with toString
		 */
		public void set(List<? extends T> type);
	}

	/** Define a default enum for the provided type */
	public interface EnumSetting<E extends Enum<E>>
	{
		/** use this default if nothing has been set */
		public E defaultValue(E value);

		/** Ensure the value exists otherwise throw an exception */
		public E require() throws SettingsException;

		/** sets the value into the setting */
		public void set(Enum<E> value);
	}

}
