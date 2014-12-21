package com.cboe.util.property;

public class PropertyHelper
{

	public static <T extends Enum<T>> T getEnum(T defaultValue)
	{
		Class<T> clazz = defaultValue.getDeclaringClass();
		String key = clazz.getSimpleName();
		return getEnum(key, defaultValue);
	}

	public static <T extends Enum<T>> T getEnum(String key, T defaultValue)
	{
		Class<T> clazz = defaultValue.getDeclaringClass();
		T t = getEnum(key, clazz);
		if (t != null)
			return t;
		else
			return defaultValue;
	}

	public static <T extends Enum<T>> T getEnum(Class<T> clazz)
	{
		String key = clazz.getSimpleName();
		return getEnum(key, clazz);
	}

	public static <T extends Enum<T>> T getEnum(String key, Class<T> clazz)
	{
		try
		{
			return Enum.valueOf(clazz, System.getProperty(key));
		}
		catch (Exception e)
		{
			return null;
		}
	}

}
