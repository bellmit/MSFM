package com.cboe.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Dash D implementation of the settings API.
 */
public class PropertyMappedSettings extends AbstractSettings
{
	private final String prefix;
	private final boolean isAlarmIfNotPresent;
	private final PropertyMappedSettings parent;
	private final Properties properties;

	/**
	 * Create a new settings object
	 */
	public PropertyMappedSettings(Properties properties)
	{
		this(null, properties, null, false);
	}

	/**
	 * Used for internal chaining of sub groups and alarm settings, not intended for
	 * public/protected
	 */
	private PropertyMappedSettings(PropertyMappedSettings parent, Properties properties, String prefix, boolean isAlarmIfNotPresent)
	{
		this.parent = parent;
		this.prefix = prefix;
		this.isAlarmIfNotPresent = isAlarmIfNotPresent;
		this.properties = properties;
	}

	/**
	 * Expose the properties that contain our settings
	 */
	public Properties getProperties()
	{
		return properties;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof PropertyMappedSettings == false)
		{
			return false;
		}
		PropertyMappedSettings other = (PropertyMappedSettings) obj;
		if (other.isAlarmIfNotPresent != isAlarmIfNotPresent)
		{
			return false;
		}

		if (prefix == null)
		{
			return (prefix == other.prefix);
		}
		else
		{
			return prefix.equals(other.prefix);
		}
	}

	@Override
	public int hashCode()
	{
		int id = (isAlarmIfNotPresent()) ? 1 : 0;
		if (prefix == null)
		{
			return id;
		}
		return id + prefix.hashCode();
	}

	@Override
	protected void alarmOnMissingSetting(String settingName)
	{
		if (parent != null)
			parent.alarmOnMissingSetting(settingName);
		super.alarmOnMissingSetting(settingName);

	}

	@Override
	protected void recordUsedSettingAndValue(String settingName, Object value, String description)
	{
		if (parent != null)
			parent.recordUsedSettingAndValue(settingName, value, description);
		super.recordUsedSettingAndValue(settingName, value, description);
	}

	@Override
	public boolean isAlarmIfNotPresent()
	{
		return isAlarmIfNotPresent;
	}

	/**
	 * Take the name list and convert it to a delimited single string
	 */
	public static final String buildName(String... names)
	{
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < names.length; i++)
		{
			if (names[i] == null)
				continue;
			builder.append(names[i]);
			if (i < names.length - 1)
				builder.append(DELIMETER);
		}
		return builder.toString();
	}

	@Override
	public SettingValue name(String name)
	{
		return new SettingDefaultImpl(name);
	}

	@Override
	public Settings alarmIfNotPresent()
	{
		return new PropertyMappedSettings(this, properties, prefix, true);
	}

	@Override
	public Settings group(String value)
	{
		return new PropertyMappedSettings(this, properties, buildName(prefix, value), isAlarmIfNotPresent);
	}

	/**
	 * This is the core of the settings API, this fetches the values based on the "withDefault"
	 * type.
	 */
	private class SettingDefaultImpl implements Settings.SettingValue
	{

		private final String name;
		private String description = null;

		public SettingDefaultImpl(String name)
		{
			this.name = buildName(prefix, name);
		}

		@Override
		public boolean defaultValue(boolean defaultValue)
		{
			String value = properties.getProperty(name);
			if (value == null)
			{
				if (isAlarmIfNotPresent())
				{
					alarmOnMissingSetting(name);
				}
				recordUsedSettingAndValue(name, defaultValue, description);
				return defaultValue;
			}
			Boolean result = Boolean.parseBoolean(value);
			recordUsedSettingAndValue(name, result, description);
			return result.booleanValue();
		}

		@Override
		public boolean requireBoolean() throws SettingsException
		{
			String value = properties.getProperty(name);
			if (value == null)
			{
				throw new SettingsException("No value provided for required value: " + name);
			}
			Boolean result = Boolean.parseBoolean(value);
			recordUsedSettingAndValue(name, result, description);
			return result.booleanValue();
		}

		@Override
		public void set(boolean value)
		{
			properties.setProperty(name, "" + value);
		}

		@Override
		public int defaultValue(int defaultValue)
		{
			String property = properties.getProperty(name);
			int result = defaultValue;

			if (property != null)
			{
				try
				{
					result = Integer.parseInt(property);
				}
				catch (Exception e)
				{
					/* pass through, we'll keep the default */
				}
			}

			if (isAlarmIfNotPresent() && isDefined() == false)
			{
				alarmOnMissingSetting(name);
			}

			recordUsedSettingAndValue(name, result, description);
			return result;
		}

		@Override
		public int requireInteger() throws SettingsException
		{
			String property = properties.getProperty(name);
			int result;

			if (property == null)
			{
				throw new SettingsException("No value provided for required value: " + name);
			}
			else
			{
				try
				{
					result = Integer.parseInt(property);
				}
				catch (Exception e)
				{
					throw new SettingsException("No value provided for required value: " + name);
				}
			}

			recordUsedSettingAndValue(name, result, description);
			return result;
		}

		@Override
		public void set(int value)
		{
			properties.setProperty(name, "" + value);
		}

		@Override
		public long defaultValue(long defaultValue)
		{
			String property = properties.getProperty(name);
			long result = defaultValue;

			if (property != null)
			{
				try
				{
					result = Long.parseLong(property);
				}
				catch (Exception e)
				{
					/* pass through, we'll keep the default */
				}
			}
			if (isAlarmIfNotPresent() && isDefined() == false)
			{
				alarmOnMissingSetting(name);
			}

			recordUsedSettingAndValue(name, result, description);
			return result;
		}

		@Override
		public long requireLong() throws SettingsException
		{
			String property = properties.getProperty(name);
			long result;

			if (property == null)
			{
				throw new SettingsException("No value provided for required value: " + name);
			}
			else
			{
				try
				{
					result = Long.parseLong(property);
				}
				catch (Exception e)
				{
					throw new SettingsException("No value provided for required value: " + name);
				}
			}

			recordUsedSettingAndValue(name, result, description);
			return result;
		}

		@Override
		public void set(long value)
		{
			properties.setProperty(name, "" + value);
		}

		@Override
		public double defaultValue(double defaultValue)
		{
			String sysString = properties.getProperty(name);
			double result;
			if (sysString == null)
			{
				result = defaultValue;
				if (isAlarmIfNotPresent())
				{
					alarmOnMissingSetting(name);
				}
			}
			else
			{
				try
				{
					result = Double.parseDouble(sysString);
				}
				catch (Throwable t)
				{
					result = defaultValue;
				}
			}

			recordUsedSettingAndValue(name, result, description);
			return result;
		}

		@Override
		public double requireDouble() throws SettingsException
		{
			String sysString = properties.getProperty(name);
			double result;
			if (sysString == null)
			{
				throw new SettingsException("No value provided for required value: " + name);

			}
			try
			{
				result = Double.parseDouble(sysString);
			}
			catch (Throwable t)
			{
				throw new SettingsException("No value provided for required value: " + name);
			}

			recordUsedSettingAndValue(name, result, description);
			return result;
		}

		@Override
		public void set(double value)
		{
			properties.setProperty(name, "" + value);
		}

		@Override
		public float defaultValue(float defaultValue)
		{
			String sysString = properties.getProperty(name);
			float result;
			if (sysString == null)
			{
				result = defaultValue;
				if (isAlarmIfNotPresent())
				{
					alarmOnMissingSetting(name);
				}
			}
			else
			{
				try
				{
					result = Float.parseFloat(sysString);
				}
				catch (Throwable t)
				{
					result = defaultValue;
				}
			}

			recordUsedSettingAndValue(name, result, description);
			return result;
		}

		@Override
		public float requireFloat() throws SettingsException
		{
			String sysString = properties.getProperty(name);
			float result;
			if (sysString == null)
			{
				throw new SettingsException("No value provided for required value: " + name);
			}
			else
			{
				try
				{
					result = Float.parseFloat(sysString);
				}
				catch (Throwable t)
				{
					throw new SettingsException("No value provided for required value: " + name);
				}
			}

			recordUsedSettingAndValue(name, result, description);
			return result;
		}

		@Override
		public void set(float value)
		{
			properties.setProperty(name, "" + value);

		}

		@Override
		public String defaultValue(String defaultValue)
		{
			String result = properties.getProperty(name);

			if (result == null)
			{
				result = defaultValue;
				if (isAlarmIfNotPresent())
				{
					alarmOnMissingSetting(name);
				}
			}

			recordUsedSettingAndValue(name, result, description);
			return result;
		}

		@Override
		public String requireString() throws SettingsException
		{
			String result = properties.getProperty(name);

			if (result == null)
			{
				throw new SettingsException("No value provided for required value: " + name);
			}

			recordUsedSettingAndValue(name, result, description);
			return result;
		}

		@Override
		public void set(String value)
		{
			properties.setProperty(name, value);
		}

		@Override
		public <E extends Enum<E>> EnumSetting<E> enumType(Class<E> type)
		{
			return new EnumSettingImpl(type, name, description);
		}

		@Override
		public <T> TypeSetting<T> type(Class<T> type)
		{
			return new TypeSettingImpl(name, type, description);
		}

		@Override
		public boolean isDefined()
		{
			return properties.getProperty(name) != null;
		}

		@Override
		public SettingValue description(String description)
		{
			this.description = description;
			return this;
		}
	}

	/**
	 * Converts -D settings for class names into real objects based on -D settings
	 */
	public class TypeSettingImpl<T> implements Settings.TypeSetting<T>
	{
		private static final String ARRAY_DELIMITER = ",";
		private final String name;
		private Class<T> expectedType;
		private final String description;

		public TypeSettingImpl(String name, Class<T> expectedType, String description)
		{
			this.expectedType = expectedType;
			this.name = name;
			this.description = description;
		}

		@Override
		public T defaultClass(Class<? extends T> defaultType)
		{
			String clazzName = properties.getProperty(name);
			T result = null;
			Class<? extends T> clazzType = null;

			/*
			 * try loading the system version first
			 */
			if (clazzName != null)
			{
				try
				{
					clazzType = (Class<T>) Class.forName(clazzName);
				}
				catch (Exception e)
				{
				}
			}

			if (clazzType == null)
			{
				clazzType = defaultType;
				if (isAlarmIfNotPresent())
				{
					alarmOnMissingSetting(name);
				}
			}

			try
			{
				result = clazzType.newInstance();

				if (expectedType.isAssignableFrom(result.getClass()) == false)
				{
					result = defaultType.newInstance();
				}
			}
			catch (InstantiationException e)
			{
				result = null;
			}
			catch (IllegalAccessException e)
			{
				result = null;
			}

			// a little more work, we are not recording the result, but rather
			// the type
			if (result == null)
			{
				if (isAlarmIfNotPresent())
				{
					alarmOnMissingSetting(name);
				}
				recordUsedSettingAndValue(name, "Could not instantiate " + clazzType, description);
			}
			else
			{

				recordUsedSettingAndValue(name, clazzName, description);
			}

			return result;
		}

		@Override
		public T defaultValue(T defaultValue)
		{
			String clazzName = properties.getProperty(name);
			T result = null;
			Class<? extends T> clazzType = null;

			if (clazzName != null)
			{
				try
				{
					clazzType = (Class<T>) Class.forName(clazzName);
					result = clazzType.newInstance();
				}
				catch (Exception e)
				{
					// nothing we'll use the default
				}
			}
			if (result == null)
			{
				result = defaultValue;
				if (isAlarmIfNotPresent())
				{
					alarmOnMissingSetting(name);
				}
			}

			/*
			 * possible here we actually have a type that is not of type T
			 */
			if (expectedType.isAssignableFrom(result.getClass()) == false)
			{
				result = defaultValue;
			}

			recordUsedSettingAndValue(name, result.getClass(), description);

			return result;
		}

		@Override
		public T require() throws SettingsException
		{
			String clazzName = properties.getProperty(name);
			T result = null;
			Class<? extends T> clazzType;

			if (clazzName == null)
			{
				throw new SettingsException("No value provided for required value: " + name);
			}

			try
			{
				clazzType = (Class<T>) Class.forName(clazzName);
				result = clazzType.newInstance();

				/*
				 * its possible this isn't our type
				 */
				if (expectedType.isAssignableFrom(result.getClass()) == false)
				{
					throw new SettingsException("Incorrect type provided for required value: " + name);
				}

			}
			catch (Exception e)
			{
				throw new SettingsException("No value provided for required value: " + name);
			}
			recordUsedSettingAndValue(name, result.getClass(), description);

			return result;
		}

		@Override
		public void set(Class<? extends T> type)
		{
			properties.setProperty(name, type.getName());
		}

		@Override
		public List<T> defaultList(List<T> data)
		{
			String propertyList = properties.getProperty(name);

			try
			{
				List<T> result = new ArrayList<T>();
				String[] split = propertyList.split(ARRAY_DELIMITER);
				for(String itemStr : split) {
					T item = expectedType.getConstructor(String.class).newInstance(itemStr);
					result.add(item);
				}
				recordUsedSettingAndValue(name, propertyList, description);
				return result;
				
			}
			catch (Exception e)
			{
				// noop, just means use default
			}

			recordUsedSettingAndValue(name, toString(data), description);
			if (isAlarmIfNotPresent())
			{
				alarmOnMissingSetting(name);
			}
			
			return data;
		}

		@Override
		public List<T> requireList() throws SettingsException
		{
			String propertyList = properties.getProperty(name);

			try
			{
				List<T> result = new ArrayList<T>();
				String[] split = propertyList.split(ARRAY_DELIMITER);
				for(String itemStr : split) {
					T item = expectedType.getConstructor(String.class).newInstance(itemStr);
					result.add(item);
				}
				recordUsedSettingAndValue(name, propertyList, description);
				return result;
				
			}
			catch (Exception e)
			{
				// noop, just means use default
			}
			throw new SettingsException("No value provided for required value: " + name);
		}

		@Override
		public void set(List<? extends T> types)
		{
		
			properties.setProperty(name, toString(types));
		}
		
		/**
		 * Builds a list of strings from the list of types
		 */
		private String toString(List<? extends T> types) {
			StringBuilder builder = new StringBuilder();

			final int length = types.size();
			for (int i = 0; i < length; i++)
			{
				builder.append(types.get(i).toString());
				if (i < length - 1)
				{
					builder.append(ARRAY_DELIMITER);
				}
			}
			return builder.toString();
		}
		

	}

	/**
	 * Converts -D settins for enums into real values.
	 */
	public class EnumSettingImpl<E extends Enum<E>> implements Settings.EnumSetting<E>
	{
		private final Class clazz;
		private final String name;
		private final String description;

		public EnumSettingImpl(Class clazz, String name, String description)
		{
			this.name = name;
			this.clazz = clazz;
			this.description = description;
		}

		@Override
		public E defaultValue(E defaultValue)
		{
			E result;
			try
			{
				String value = properties.getProperty(name);

				if (value == null)
				{
					result = defaultValue;
					if (isAlarmIfNotPresent())
					{
						alarmOnMissingSetting(name);
					}
				}
				else
				{
					result = (E) Enum.valueOf(clazz, value);
				}
			}
			catch (Exception e)
			{
				result = defaultValue;
				if (isAlarmIfNotPresent())
				{
					alarmOnMissingSetting(name);
				}

			}

			recordUsedSettingAndValue(name, result, description);

			return result;
		}

		@Override
		public E require() throws SettingsException
		{
			E result;
			try
			{
				String value = properties.getProperty(name);

				if (value == null)
				{
					throw new SettingsException("No value provided for required value: " + name);
				}
				result = (E) Enum.valueOf(clazz, value);
			}
			catch (Exception e)
			{
				throw new SettingsException("No value provided for required value: " + name);
			}

			recordUsedSettingAndValue(name, result, description);

			return result;
		}

		@Override
		public void set(Enum<E> value)
		{
			properties.setProperty(name, value.name());
		}

	}

}
