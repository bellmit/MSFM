package com.cboe.common.log;


/**
 * Communicate standard startup messages to the log system.
 * 
 * This class was created in order to standardize common lifecyle messages during startup. This
 * class avoids "magic log messages", that can change from application to application.
 * 
 */
public final class StartupLogger
{
	/* made final so we don't get strange variants */

	/** small string to help with less navigation */
	private static final String DELIMITER_BEGIN = "=== ";
	private static final String DELIMITER_END = " ===";

	/**
	 * Signals that the process is done, scripts that were waiting for it to complete may continue.
	 */
	public static void signalSystemReady()
	{
		signalSystemReady(null);
	}

	/**
	 * Signals that the process is done, scripts that were waiting for it to complete may continue,
	 * and the optional message is attached to the startup message
	 * 
	 * @param message
	 *            An optional message to attach.
	 */
	public static void signalSystemReady(String message)
	{
		String result = "System Ready";
		if (message != null)
		{
			result += ":" + message;
		}

		Logger.sysNotify(DELIMITER_BEGIN + result + DELIMITER_END);
	}

	/**
	 * Standard message indicating the progress of startup
	 * 
	 * @param message
	 *            A message indicating progress, null will be silently ignored.
	 */
	public static void progress(String message)
	{
		if (message == null)
		{
			return;
		}
		Logger.sysNotify(DELIMITER_BEGIN + message + DELIMITER_END);
	}
}
