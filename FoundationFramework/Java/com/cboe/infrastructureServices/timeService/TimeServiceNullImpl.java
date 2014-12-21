package com.cboe.infrastructureServices.timeService;
import com.cboe.util.Timer;
/**
 */
public class TimeServiceNullImpl extends TimeServiceBaseImpl
{
	public boolean delete(int i)
	{
		return true;
	}
	public void shutdown()
	{
	}
	public int enqueue(int aTimerType,int aTimePeriod,Object aContext,Timer aCallbackObject)
	{
		return 0;
	}
}
