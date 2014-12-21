package com.cboe.infrastructureServices.timeService;

import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.util.Timer;
/**
 * Timer service is used by programs that want to get notified asynchronously when 
 * the timer expires.
 * Notification is done thru callbacks.<p>
 * Programs can queue timer(s) and delete queued timer(s).<p>
 *
 * Callers call "enqueue" method on the timer service passing it the following user 
 * defined parameters.<p>
 *
 * TimerType, callback object (implements the timer interface),
 * A context object and the timePeriod.<p>
 *
 * <b>NOTE:</b> "enqueue" method returns a timerId that can be used to delete
 * the timer (if needed). It is the callers responsibility to
 * keep track of timerId's.<p>
 *
 * When the time period has elapsed the "dequeue" method on the
 * callback object is invoked and is passed the contest object and timer type.<p>
 *
 * @Author		: Ravi Vazirani
 */
public class TimeServiceImpl extends TimeServiceBaseImpl implements TimeService 
{
	com.cboe.util.TimerService timeService;

	public TimeServiceImpl()
	{
		timeService = com.cboe.util.TimerService.getInstance();
	}
	/**
	 * Start a timer thread that will monitor the queued timers.
	 */
	public boolean initialize( ConfigurationService configService )
	{
/*
Thread count not supported by com.cboe.util.TimeService
		int numOfThreads = DEFAULT_THREAD_POOL_SIZE;
		try
		{
			String threadCount = configService.getProperty("TimeService.threadCount");
			numOfThreads = new Integer(threadCount).intValue();
		} 
		catch(NoSuchPropertyException ex)
		{
		}
		catch(NumberFormatException ex)
		{
		}
		initializeThreads(numOfThreads);
*/
		return true;
	}

/**
 * Searches thru the Timer Queue and deletes that timer that
 * matches the specified timer id.
 *
 * @param timerId the specific timer that needs to be deleted.
 */
public boolean delete(int timerId)
{
	return timeService.delete(timerId);
}

/**
 * Queues a timer command object in sorted(by Time) order in the Timer Queue.
 *
 * TimerCommand object is retreived from a free list.
 * If free list is empty then more TimerCommand objects are added to 
 * the free list.<p>
 * <b>Note</b> Timer command objects are notified when the time has elapsed by
 * the timer thread that is running in the background. Once the Timer
 * Command object is done with its work it puts itself in the free list.<p>
 *
 * @param type user specific timer type
 * @param timePeriod the time in milliseconds
 * @param context any user object
 * @param callbackObject the object to call on when the timer pops.
 *
 * @return timerId number associated with the timer that got queued or -1 if the timer
 *				   was not queued.
 *
 * @author Ravi Vazirani
 */
public int enqueue(int aTimerType,int aTimePeriod,Object aContext,Timer aCallbackObject) 
{
	return timeService.enqueue(aTimerType,aTimePeriod,aContext,aCallbackObject) ;
}
/**
 * Sets shutdown flag to false and notfies the timer thread.
 *
 * @author Ravi Vazirani
 */
public void shutdown() 
{
	timeService.shutdown();
}
/**
 * Returns a string value of the object.
 *
 * @return String contents of the object.
 * 
 * @author  Ravi Vazirani
 */
public String toString() 
{
	return timeService.toString();
}
}
