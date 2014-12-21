package com.cboe.common.log;

/**
 * Handle raise events from {@link Resolution} and {@link Alarm}, typically this means forwarding
 * the alarm somehow off to System Health Monitor or some equivalent.
 */
public interface AlarmEventHandler
{
	/**
	 * handle the raising of the provided alarm
	 */
	public void handle(Alarm alarm);

	/**
	 * Handle a resolution for a particular alarm
	 */
	public void handle(Resolution resolution);

}
