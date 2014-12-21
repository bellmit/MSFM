package com.cboe.directoryService;


/**
 * Utility class for building consistent log messages for the trader.
 * 
 * This class is to make grepping a little more standardized for the TraderService logs.
 * 
 * @author eccles
 * 
 */
public final class TraderLogBuilder
{
	/**
	 * Define the beginning of a method. This is useful for tracing.
	 * 
	 * @param groupId
	 *            a string to identify a cluster(usuall class name).
	 * @param methodName
	 *            The name of the method just entered
	 * @param pattern
	 *            a string or pattern, based off of {@link String#format(String, Object...)}
	 * @param values
	 *            values to be consumed by the pattern
	 * @return A formatted string
	 */
	public static final String formatEnter(String groupId, String methodName, String pattern, Object... values)
	{
		return format(groupId, methodName + "<enter>", pattern, values);
	}

	/**
	 * Define the beginning of a method for logging. This is useful for tracing.
	 * 
	 * @param groupId
	 *            a string to identify a cluster(usuall class name).
	 * @param methodName
	 *            The name of the method just entered
	 * @return A formatted string
	 */
	public static final String formatEnter(String groupId, String methodName)
	{
		return format(groupId, methodName + "<enter>");
	}

	/**
	 * Define the end of a method for logging.This is useful for tracing.
	 * 
	 * @param groupId
	 *            A string to identify a cluster (usual class name)
	 * @param methodName
	 *            The executing method
	 * @return A formatted string
	 */
	public static final String formatExit(String groupId, String methodName)
	{
		return format(groupId, methodName + "<exit>");
	}

	/**
	 * Define the end of a method for logging.This is useful for tracing.
	 * 
	 * @param groupId
	 *            A string to identify a cluster (usual class name)
	 * @param methodName
	 *            The executing method
	 * @param pattern
	 *            a string or pattern, based off of {@link String#format(String, Object...)}
	 * @param values
	 *            values to be consumed by the pattern
	 * @return A formatted string
	 */
	public static final String formatExit(String groupId, String methodName, String pattern, Object... values)
	{
		return format(groupId, methodName + "<exit>", pattern, values);
	}

	/**
	 * Format a string for logging.
	 * 
	 * @param groupId
	 *            A string to identify a cluster (usual class name)
	 * @param methodName
	 *            The executing method
	 * @param pattern
	 *            a string or pattern, based off of {@link String#format(String, Object...)}. It is
	 *            recommended the only pattern you use is %s as it gracefully handles all types.
	 * @param values
	 *            values to be consumed by the pattern
	 * @return A formatted string
	 */
	public static final String format(String groupId, String methodName, String pattern, Object... values)
	{
		String params;
		if (values == null || values.length == 0)
		{
			params = pattern;
		}
		else
		{
			try
			{
				params = String.format(pattern, values);
			}
			catch (Throwable t)
			{
				// Don't let a logging formatting error destroy the stack
				params = pattern;
			}
		}

		return format(groupId, methodName) + "[[" + params + "]]";
	}

	/**
	 * Format a string for logging
	 * 
	 * @param groupId
	 *            A string to identify a cluster (usual class name)
	 * @param methodName
	 *            The executing method
	 * 
	 * @return A formatted string
	 */
	public static final String format(String groupId, String methodName)
	{
		return groupId + "." + methodName;
	}

	
}
