package com.cboe.infrastructureServices.timeService;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.util.Timer;
/**
 * Esentially just repackage Ravi's initial timer service implementation.
 */
public interface TimeService
{
/**
 * Searches thru the Timer Queue and deletes that timer that
 * matches the specified timer id.
 *
 * @param timerId the specific timer that needs to be deleted.
 */
public boolean delete(int timerId);
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
public int enqueue(int aTimerType,int aTimePeriod,Object aContext,Timer aCallbackObject);
/**
 * Sets shutdown flag to false and notfies the timer thread.
 *
 * @author Ravi Vazirani
 */
public void shutdown();
/**
 */
public long getCurrentDateTime();
/** 
 * Initialize the number of threads in a thead pool with the
 * TimerService.threadCount property.
 */
public boolean initialize(ConfigurationService configService) ;

}
