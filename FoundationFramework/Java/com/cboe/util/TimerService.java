package com.cboe.util;

/**
 * 
 * @author Neher
 *
 * 8-4-2004 It looks like the thread pool idea is not used with Timer Service so I am 
 * commiting this out for now.
 */

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
public class TimerService implements Runnable {

   /**
	* Default timer service
	*/
 	private static TimerService defaultInstance = null;
 	
   /**
	* Maximum number of timers that can queued in the queue
	* Timer id rolls over to a value of 0 after this limit is
	* reached.
	*/
	private static final int MAX_TIMER_ID 		= Integer.MAX_VALUE;
	
   /**
	* Timer ids start at this number.
	*/
	private static final int FIRST_TIMER_ID 	= 0;
	
   /**
	* Allocation capacity increment of the timer elements
	* in the timer queue. Used when the queue gets full and more
	* timer elements are needed.
	*/
	private static final int CAPACITY_INCREMENT = 10;

	// Instance variables. 
   /**
	* Number of threads used by the timer service for callbacks.
	*/
	private volatile int	 nbrCallbackThreads = 1;
	
   /**
	* True if the timer queue is being shutdown.
	*/
	private volatile boolean shutdown			= false;
	
   /**
	* Timer id that will be returned when the next timer is
	* queued.
	*/
	private volatile int nextTimerId	  	= FIRST_TIMER_ID;
	
   /**
   	* Total number of timer elements that have been allocated.
	*/
	private volatile int nbrTotalTimers   	= 0;
	
   /**
   	* Number of active timers.
	*/
	private volatile int nbrQueuedTimers  	= 0;

   /**
   	* Lock object associated with the free list of timer elements.
	*/
	private Object 	 	 freeQueueLock    	= new Object();
	
   /**
   	* Lock object associated with the timer queue.
	*/
	private Object 	 	 timerQueueLock   	= new Object();

	private TimerCommand freeQueueHead    	= null;
	private TimerCommand timerQueueHead   	= null;

	private Thread 		t  = null; // Holds on to the timer thread.
//	Thead pool stuff	
//	private PooledExecutor	tp = null;

	
	// Member Classes
   /**
 	* This is the callback object that is used by the Thread pool utility.
 	* Every time we queue a timer, we get a new TimerCommand from a pool of
 	* TimerCommands. In this we store all the information that is specified
 	* in the "enqueue" method of the TimerService.
 	*/
	private class TimerCommand /*extends ThreadCommand */{
		
		// Instance variables
		private int 			timerType;
		private int				timerId;
		private long 			notifyTime;
		private Object			context;
		private Timer			callbackObject;
		private TimerCommand	next;

		// Constructor
		TimerCommand() {};

	   /**
 		* This method is called by the Thread pool stuff once it is done 
 		* with our thread command. We then use this to put this object into our free list.
 		*/
		public void complete() {
  			// Dequeue the timer and insert it into the free list.
  			// Since no-one is pointing to this object (at this point in time) 
  			// it can free itself.
			dequeue(this);
		}

	   /**
 		* This method is either called from thread pool of the TimerService thread or from
 		* the timer service directly if there is not thread pool.
 		* This is called only when a timer has elapsed.
 		*/
		public void execute() {

			// Notify the object that the timer has elapsed.
			callbackObject.dequeue(timerType,context);
		}

		private void setValues(int aTimerType,int aTimerId, long aNotifyTime,Object aContext,Timer aCallbackObject) {
			
			timerType 		= aTimerType;
			timerId			= aTimerId;
			notifyTime 		= aNotifyTime;
			context 		= aContext;
			callbackObject 	= aCallbackObject;
		}
		
	} // End Class TimerCommand
	
/**
 * Start a timer thread that will monitor the queued timers.
 *
 * @author Ravi Vazirani
 * @param nbrThreads int
 */
public TimerService(int nbrThreads) {
	super();

	// Figure out if we need to use the Thread pool (ie. if more than 1
	// Timer callback threads are needed).
	nbrCallbackThreads = (nbrThreads > 1) ? nbrThreads:1;

	// Start the timer thread
	t = new Thread(this);
	t.start();
	return;
}
/**
 * Adds the element to the beginning of the free list.
 * The caller of this method needs to synchronize on freeQueueLock
 *
 * @author Ravi Vazirani
 * @param tc com.cboe.util.TimerCommand
 */
private void addToFreeList(TimerCommand tc) {

	// Add command to the beginning of the free list.
	tc.next = freeQueueHead;
	freeQueueHead = tc;

	// Null out user object references (so the VM does not hold them).
	tc.context = null;
	tc.callbackObject = null;
}
/**
 * Searches thru the Timer Queue and deletes that timer that
 * matches the specified timer id.
 *
 * @param timerId the specific timer that needs to be deleted.
 */
public boolean delete(int timerId) {

	// Invalid timer id, don't bother.
	if (timerId <= 0) 
		return true;
	
	TimerCommand prev,curr;

	synchronized(timerQueueLock) {
		
		prev = null;
		curr = timerQueueHead;
		
		while (curr != null) {
			if (curr.timerId == timerId)
				break;

			prev = curr;
			curr = curr.next;
		}
		
  	 	// Notify the timer thread only if the timer has been deleted from the
	 	// beginning of the list.
	 	// Note: Order of notify method is important. Notify is done only after we
	 	//       remove the Timer command.
		if (curr != null) {
		 	if (prev == null) {
				timerQueueHead = curr.next;
				timerQueueLock.notify();
		 	}
		 	else
				prev.next = curr.next;
		}
	}

	// Add this command to the free list.
	if (curr != null)
		dequeue(curr);

	return (curr != null);
}
/**
 * Searches thru the Timer Queue and deletes any timer that
 * matches the specified timer context object (using .equals() 
 * comparisons)).
 *
 * @param timerId the specific timer that needs to be deleted.
 * @return the number of timer commands deleted.
 */
public int delete(Object context) 
{
	TimerCommand prev,curr;

	java.util.Vector dequeueList = new java.util.Vector();
	
	synchronized(timerQueueLock) 
	{
		
		prev = null;
		curr = timerQueueHead;

		boolean deletedQueueHead = false;
		
		while (curr != null) 
		{
			if (curr.context != null && curr.context.equals(context))
			{
				dequeueList.addElement(curr);
				if (prev == null)
				{
					deletedQueueHead = true;
					timerQueueHead = curr.next;
				}
				else
				{
					prev.next = curr.next;
				}
			}
			else
			{
				prev = curr;
			}
			curr = curr.next;
		}
		
  	 	// Notify the timer thread only if the timer has been deleted from the
	 	// beginning of the list.
	 	// Note: Order of notify method is important. Notify is done only after we
	 	//       remove the Timer command.
	 	if (deletedQueueHead)
		{
			timerQueueLock.notify();
		}
	}

	// Add the deleted commands to the free list.
	//
	java.util.Enumeration anEnum = dequeueList.elements();
	while (anEnum.hasMoreElements())
	{		
		dequeue((TimerCommand)anEnum.nextElement());
	}

	return dequeueList.size();
} 	// Class constants
/**
 * Puts the Timer command object in the free list.
 * This method is called from the TimerCommand to free itself.
 *
 * @param tc com.cboe.util.TimerCommand
 */
public void dequeue(TimerCommand tc) {

	synchronized(freeQueueLock) {
		addToFreeList(tc);
		nbrQueuedTimers--;
	}
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
public int enqueue(int aTimerType,int aTimePeriod,Object aContext,Timer aCallbackObject) {

	int			 queuedTimerId;
	long 		 notifyTime;
	TimerCommand tc, prev, curr;

	// Calculate the time when the caller wants to get notified.
	notifyTime = aTimePeriod + System.currentTimeMillis();
	
	// Perform validations on parameters.
	if ((aCallbackObject == null) || (aTimePeriod < 0))
		return -1;

	// Get a TimerCommand from the free list.
	tc = getFromFreeList();

	synchronized(timerQueueLock) {
		
		// Re-cycle TimerId if needed.
		if (++nextTimerId >= MAX_TIMER_ID)
			nextTimerId = FIRST_TIMER_ID + 1;

		// Fill in the timer information in the timer command.
		tc.setValues(aTimerType,nextTimerId,notifyTime,aContext,aCallbackObject);
		nbrQueuedTimers++;

		// Insert the TimerCommand in the queue (between prev and curr).
		prev = null;
		curr = timerQueueHead;
		
		while (curr != null) {
			if (notifyTime < curr.notifyTime)
				break;

			prev = curr;
			curr = curr.next;
		}
		
  	 	// Notify the timer thread only if a new Timer has been added to the
	 	// beginning of the list.
		tc.next = curr;
	 	if (prev == null) {
			timerQueueHead = tc;
			timerQueueLock.notify();
	 	}
	 	else
			prev.next = tc;

		queuedTimerId = nextTimerId;
	}

	return queuedTimerId;
}
/**
 * Get a timer command object from the beginning of the free list.
 * If the free list is empty more elements are added to the list.
 *
 * @author Ravi Vazirani
 * @return com.cboe.util.TimerCommand
 */
private TimerCommand getFromFreeList() {
	
	TimerCommand tc;
	
	synchronized(freeQueueLock) {
		
		if (freeQueueHead == null) {
			for (int i = 0; i < CAPACITY_INCREMENT; i++) {
				addToFreeList(new TimerCommand());
				nbrTotalTimers++;
			}
		}

		tc = freeQueueHead;
		freeQueueHead = freeQueueHead.next;
	}

	return tc;
}
/**
 * Returns a timer service with only one callback thread.
 *
 * @author Ravi Vazirani
 * 
 * @return com.cboe.util.TimerService
 */
public static synchronized TimerService getInstance() {

	if (defaultInstance == null)
		defaultInstance = new TimerService(1);
		
	return defaultInstance;
}
/**
 * Infinite loop processing the timer queue.
 *
 * Calculates the sleep time using the element at the
 * beginning of the list and then sleeps for that amount of time.
 * Once the time has elapsed it calls the thread pool to
 * execute the "execute" method in the TimerCommand.
 *
 * @author Ravi Vazirani
 */
public void run() {
	
	long waitTime = 0;
	TimerCommand tc = null;
	
	// If the Timer Service has been specified to start more than one thread
	// then use the ThreadPool utility Class to manage the threads.
	// Thead pool stuff	
	//if (nbrCallbackThreads > 1)
		//tp = new ThreadPool(nbrCallbackThreads,"TimerService");
		//tp = new PooledExecutor(nbrCallbackThreads);

	// Be in a never ending while loop waiting for timeouts to occur.
	// If a timer has elapsed, then remove that TimerCommand form the timer queue
	// If we are using thread pool then schedule it to the ThreadPool for callbacks
	// else invoke the TimerCommands "execute" method directly.
	// Note: It is possible that while we are waiting Timer may get added and
	// and deleted from the beginning of the timer list. In this case the Timer
	// service will notify the timerQueueLock object and then we will recalculate
	// the new wait time.
		
	while (!shutdown) {

		tc = waitForTimerEvent();	
		if (tc == null) // This will happen only if shutdown is true.
			continue;
//		Thead pool stuff	
/*		if (tp != null) { // If there is a thread pool.
			try {
				//tp.schedule(tc);
				tp.execute(this);
			}
			catch (Exception e) {
				System.out.println("Exception in ThreadPool.schedule: " + e.getMessage());
			}
		}
		else {
*/
			tc.execute();
			tc.complete();
//		}
			
		Thread.yield(); // Give other threads a chance to work.
				
	} // End of never ending loop
}
/**
 * Sets shutdown flag to false and notfies the timer thread.
 *
 * @author Ravi Vazirani
 */
public void shutdown() {
	
	shutdown = true;

	// Notifies the timer thread that it must shut down.
	// Note: Service home will not reference this object any more.
	synchronized(timerQueueLock) {
		timerQueueLock.notify();
	}

	// Wait for 10 seconds to see if the timer thread has shutdown
	// If not force shutdown the thread.
	for (int i = 0; (i < 10) && t.isAlive(); i++) {
		try {Thread.sleep(1024);} catch(InterruptedException e){}
	}
	
	if (t.isAlive())
		t.destroy();

	// Shutdown the Threadpool
	//	Thead pool stuff	

	//	if (tp != null)
	//		tp.shutdownAfterProcessingCurrentlyQueuedTasks();

	return;
}
/**
 * Returns a string value of the object.
 *
 * @return String contents of the object.
 * 
 * @author  Ravi Vazirani
 */
public String toString() {

	String returnValue = "";
	
	returnValue += "\nMax Timer Id			  : " + MAX_TIMER_ID;
	returnValue += "\nNbr Callback Threads	  : " + nbrCallbackThreads;
	returnValue += "\nNext Timer Id			  : " + (nextTimerId + 1);
	returnValue += "\nNumber Total Timer Elems: " + nbrTotalTimers;
	returnValue += "\nNumber Queued Timers	  : " + nbrQueuedTimers;

	return returnValue + "\n";
}
/**
 * Waits till an timer has elapsed or till it receives a shutdown.
 *
 * @return TimerCommand for an elapsed timer or null if need to shutdown.
 * @author Ravi Vazirani
 */
private TimerCommand waitForTimerEvent() {

	long		 waitTime;
	TimerCommand tc = null;
	
	synchronized(timerQueueLock) {

		while (!shutdown) {

			if (timerQueueHead != null) {
				if ((waitTime = timerQueueHead.notifyTime - System.currentTimeMillis()) <= 0) {
					tc = timerQueueHead; 
					timerQueueHead = timerQueueHead.next; // Remove TimerCommand from queue.
					break;
				}
			}
			else
				waitTime = -1;
			
			try {
				if (waitTime > 0) 
					timerQueueLock.wait(waitTime);
				else 
					timerQueueLock.wait();
			}
			catch (InterruptedException e){}
			
		} // End while loop

	} // End Synchronized
	
	return (tc) ;
}
}

