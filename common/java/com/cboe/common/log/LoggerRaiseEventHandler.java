package com.cboe.common.log;


/**
 * Temporary version that logs using our bound logger
 */
class LoggerRaiseEventHandler implements AlarmEventHandler
{

	@Override
	public void handle(Alarm alarm)
	{
		Logger.sysAlarm(alarm.toString());
	}

	@Override
	public void handle(Resolution resolution)
	{
		Logger.sysAlarm("***CLEARED*** " + resolution.toString());
	}

}
