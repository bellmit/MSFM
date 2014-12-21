package com.cboe.infrastructureServices.queue;

/**
 * The generic interface to all queues returned by the <code>QueueHome</code>.
 * This interface specifies strict queue behaviour (no peeking beyond the top element).
 *
 * The queue can put in wait-mode or no-wait mode on enqueue and dequeue methods.
 * If the queue is in wait mode then the enqueue,dequeue and peek operations will
 * wait for the specified amount of time for the operation to complete, else the operations
 * will return immediately (if the queue is in no-wait-mode).
 *
 * @author Steven Sinclair
 * @author Ravi Vazirani - added comments.
 */
public interface Queue
{
	// CONSTANTS
   /**
	* The dequeue and enqueue methods on the queue will not wait if
	* the queue if empty or full respectively. Exceptions will be thrown
	* if this value is provided and the queue is full on enqueue. On dequeue
	* if the queue is empty a null is returned.
	*/
	public static final int NO_TIMEOUT = 0;

   /**
	* The dequeue and enqueue methods will wait forever if the queue is
	* empty or full respectively. Any negative value is considered an INFINITE_TIMEOUT.
	*/
	public static final int INFINITE_TIMEOUT = -1;
/**
 *  Suspend the queue.
 *  On high rate queues, if there's a problem with the dequeue thread, then
 *  we may want the option of suspending the enqueue.  This will cause enqueue calls
 *  to block until resumeEnqueue() is called.
 *
 *  @param dropEnqueueCalls - if true, then enqueue() calls will be ignored, rather than
 *     blocked.
 */
public void suspendEnqueue(boolean dropEnqueueCalls);
/**
 *  Resume the queue (clear suspended state).
 *  @see suspendEnqueue();
 */
public void resumeEnqueue();

/**
 *  Clear all queue contents.
 */
void clear() throws QueueException;
/**
 * Remove the front element from the queue and return it's data object to the caller.
 * If the queue is in wait mode and the operation cannot complete in the specified (DEFAULT VALAUE)
 * amount of time a NULL value will be returned to the caller.
 *
 * NOTE: The dequeue operation may not wait for the complete specified amount of time IF
 * 		 there are multiple readers and the queue is in wait mode with a finite wait-time.
 *
 * @return Object The data object of the topmost queue element, or <code>null</code> if
 *   			  the queue is empty.
 *
 * @exception QueueInterruptedException Queue operation interrupted.
 * @exception QueueException serious errors.
 */
Object dequeue() throws QueueException;
/**
 * Dequeue the front element of the queue and return it's data object to the caller.
 * If the queue is empty, then wait for <code>waitMillis</code> milliseconds or for
 * an item to be enqueued.
 *
 * If the queue is in wait mode and the operation cannot complete in the specified
 * amount of time a NULL value will be returned to the caller.
 *
 * NOTE: The dequeue operation may not wait for the complete specified amount of time IF
 * 		 there are multiple readers and the queue is in wait mode with a finite wait-time.
 *
 * @param waitMillis int specifying the maximum time to wait for the queue to become non-empty.
 * @return Object the data object of the front element in the queue, or null if the queue was empty
 *				  after the given wait period.
 * @exception QueueInterruptedException Queue operation interrupted.
 * @exception QueueException serious errors.
 */
Object dequeue(int waitMillis) throws QueueException;
/**
 * Remove the front element from the queue and return it's data object to the caller.
 * If the queue is in wait mode and the operation cannot complete in the specified (DEFAULT VALAUE)
 * amount of time a NULL value will be returned to the caller.
 *
 * NOTE: The dequeue operation may not wait for the complete specified amount of time IF
 * 		 there are multiple readers and the queue is in wait mode with a finite wait-time.
 *
 * @param int the nbr of elements to dequeue.
 * @return Object[] The 'n' data object of the topmost queue element, or <code>null</code> if
 *   			  the queue is empty.
 *
 * @exception QueueInterruptedException Queue operation interrupted.
 * @exception QueueException serious errors.
 */
Object[] dequeueMultiple(int nbrToDequeue) throws QueueException;

/**
 * Wait until either the queue depth is equals to the requested number of elements or the
 * maxWaitMillis timeout occurs or blockWaitMillis expires after the first item is in the queue.
 *
 * <p><b>NOTE:</b> The implementation may wait for <i>at least</i> maxWaitMillis.  Due to OS timeslice scheduling, etc,
 *  the actual duration of a call to dequeueFully(int,int) may take slightly longer.
 *
 * @param nbrToDequeue - the max # of objects to dequeue.
 * @param blockWaitMillis - how long to wait for others after at least one item is enqueued.
 * @param maxWaitMillis - the max # of milliseconds to wait, no matter how many items are in the queue.
 * @return Object[] - an object array having length in the range 1..nbrToDequeue (inclusive), or null if nothing was dequeued.
 *
 * <b>NOTE:</b> It is more efficient to use dequeueFully(Object[] dequeuedObjs, int nbrToDequeue, int blockWaitMillis, int maxWaitMillis).
 * @see Queue.dequeueFully(Object[],int,int,int).
 */
Object[] dequeueFully(int nbrToDequeue, int blockWaitMillis, int maxWaitMillis) throws QueueException;


/**
 * Wait until either the queue depth is equals to the requested number of elements or the
 * maxWaitMillis timeout occurs or blockWaitMillis expires after the first item is in the queue.
 *
 * <p><b>NOTE:</b> The implementation may wait for <i>at least</i> maxWaitMillis.  Due to OS timeslice scheduling, etc,
 *  the actual duration of a call to dequeueFully(int,int) may take slightly longer.
 *
 * @param dequeuedObjs - the array to hold the dequeue results
 * @param nbrToDequeue - the max # of objects to dequeue.  Must be &lt;= dequeuedObjs.length.
 * @param blockWaitMillis - how long to wait for others after at least one item is enqueued.
 * @param maxWaitMillis - the max # of milliseconds to wait, no matter how many items are in the queue.
 * @return int - the number of items dequeued.  Always between 0 and nbrToDequeue, inclusive.
 */
int dequeueFully(Object[] dequeuedObjs, int nbrToDequeue, int blockWaitMillis, int maxWaitMillis) throws QueueException;

/**
 * Remove the front element from the queue and return it's data object to the caller.
 * If the queue is in wait mode and the operation cannot complete in the specified (DEFAULT VALAUE)
 * amount of time a NULL value will be returned to the caller.
 *
 * NOTE: The dequeue operation may not wait for the complete specified amount of time IF
 * 		 there are multiple readers and the queue is in wait mode with a finite wait-time.
 *
 * @param waitMillis int specifying the maximum time to wait for the queue to become non-empty.
 * @param int the nbr of elements to dequeue.
 * @return Object The 'n' data object of the topmost queue element, or <code>null</code> if
 *   			  the queue is empty.
 *
 * @exception QueueInterruptedException Queue operation interrupted.
 * @exception QueueException serious errors.
 */
Object[] dequeueMultiple(int waitMillis,int nbrToDequeue) throws QueueException;
/**
 * Add the given <code>dataObject</code> to the rear of the queue.
 *
 * If the queue is in wait mode and the operation cannot complete in the specified (DEFAULT VALAUE)
 * amount of time an exception is thrown.
 *
 * @param dataObject - the object to enqueue.
 * @exception QueueFullException if the queue is already full and the timeout expires.
 * @exception QueueInterruptedException Queue operation interrupted.
 * @exception QueueException serious errors.
 */
void enqueue(Object dataObject) throws QueueException;
/**
 * Add the given <code>dataObject</code> to the rear of the queue.  If the queue is
 * full, then wait for up to <code>waitMillis</code> milliseconds for the queue to
 * become non-full.
 *
 * If the queue is in wait mode and the operation cannot complete in the specified (DEFAULT VALAUE)
 * amount of time an exception is thrown.
 *
 * @param dataObject - the object to enqueue.
 * @param waitMillis - maximum wait time to use, if the queue is currently full.
 *
 * @exception QueueFullException if the queue is already full and the timeout expires.
 * @exception QueueInterruptedException Queue operation interrupted.
 * @exception QueueException serious errors.
 */
void enqueue(Object dataObject, int waitMillis) throws QueueException;
/**
 *  Get the timeout value used when <code>enqueue(Object)</code> or
 *  <code>dequeue()</code> are called.  Not that the local constants
 *   NO_TIMEOUT and INFINITE_TIMEOUT may be returned, indicating "never wait"
 *   and "infinite wait" policies, respectively.
 */
int getDefaultTimeout();
/**
 *  Get the maximum allowable depth of the queue.
 *
 *  @return int - the maximum depth of the queue
 */
int getMaxQueueDepth();
/**
 *  Return the queue's name, or null if it is a transient queue.
 */
String getQueueName();
/**
 *  Check to see whether the queue is empty or not.
 *
 *  @return boolean - true if and only if the queue is currently empty.
 */
boolean isEmpty() throws QueueException;
/**
 *  Check to see whether the queue if full or not.
 *
 *  @return boolean - true if and only if the queue is currently full.
 */
boolean isFull() throws QueueException;
/**
 *  In some queue implementations, the queue may require periodic packing.  If, for
 *  instance, the original front of the queue is being maintained for historic purposes,
 *  the user may want to "forget" that reference.
 */
void pack() throws QueueException;
/**
 *  Return the data object of the front of the queue without affecting the queue's state at all.  This peek is always executed with a NO_TIMEOUT.
 *
 *  @param timeoutMillis - timeout value to wait: this can be any positive
 *		number, or either constant NO_TIMEOUT or INFINITE_TIMEOUT.
 * @return Object - the data object of the front of the queue, or null if the queue is empty.
 */
Object peek() throws QueueException;
/**
 *  Return the data object of the front of the queue without affecting the queue's state at all.  This peek will wait for up to timeoutMillis milliseconds for
 *  the queue to become non-empty.
 *
 *  @param timeoutMillis - timeout value to wait: this can be any positive
 *		number, or either constant NO_TIMEOUT or INFINITE_TIMEOUT.
 *
 * @return Object - the data object of the front of the queue, or null if the queue is empty.
 */
Object peek(int timeoutMillis) throws QueueException;
/**
 *  Return the data object of the front of the queue without affecting the queue's state at all.  This peek is always executed with a NO_TIMEOUT.
 *
 * @return Object[] - the 'n' data object of the front of the queue, or null if the queue is empty.
 */
Object[] peekMultiple(int nbrToPeek) throws QueueException;
/**
 * Dequeues and element from the queue.
 * See _peek for docuemtnation.
 * @param int the wait mode timeout (if 0 is pecified then we are in no wait mode).
 * @param int the nbr of elements to dequeue.
 * @return Object[] The 'n' data object of the topmost queue element, or <code>null</code> if
 *   			  the queue is empty.
 * @return Object Queued object or null if the queue is empty.
 * @exception InterruptedException
 */
public Object[] peekMultiple(int newWaitTime, int nbrToDequeue) throws QueueException;
/**
 *  Set the timeout value to use when <code>enqueue(Object)</code> or
 *  <code>dequeue()</code> are called.
 *
 *  @param timeoutMillis - the timeout value in milliseconds.  The local
 *		constants NO_TIMEOUT and INFINITE_TIMEOUT can be specified to
 *		"never wait" or to "wait indefinitely", respectively.
 */
void setDefaultTimeout(int timeoutMillis);
/**
 *  Set the maximum allowable depth of the queue.
 *
 *  @param maxQueueDepth - the maximum depth of the queue
 */
void setMaxQueueDepth(int maxQueueDepth) throws QueueException;
/**
 *  Get the number of elements currently in the queue.
 *
 *  @return int - the number of elements currently in the queue.
 */
int size();

/**
 *  If true, then rather than throw a QueueFullException on enqueue, the queue will be cleared.
 *  This is a sensible option on queues where the data is highly transient, and we'd rather lose
 *  the "old" data in the queue than block or discard the new inbound data.
 *  If the queue is cleared, then an Alarm is logged.
 */
void setClearOnEnqueueFailure(boolean value);

boolean getClearOnEnqueueFailure();

/**
 *  Return human-readable queue status
 *
 *  @param prefix - each line of the resulting string begins with this prefix
 *  @return a multiline string describing the current state of the queue.
 */
String toString(String prefix);

/**
 *  Register a thread as a reader of this queue.  This is expected to be used for
 *  toString(String) reader thread status reporting only.
 */
void addReaderThread(Thread t);
/**
 *  Remove a thread as a reader of this queue.
 *  @return boolean - true IFF the thread was removed.
 *  @see addReaderThread(Thread)
 */
boolean removeReaderThread(Thread t);
/**
 *  Remove a thread as a reader of this queue.
 *  @see addReaderThread(Thread)
 *  @return Thread - the thread removed.  Null if no thread named threadName is found.
 */
Thread removeReaderThread(String threadName);
/**
 *  Remove a thread as a reader of this queue.
 *  @return the number of reader threads removed.
 *  @see addReaderThread(Thread)
 */
int removeAllReaderThreads();

public void setInstrumented(boolean b);
public boolean getInstrumented();

/**
 * When dequeue thread starts running, report to the instrumentor.
 */
public void reportDequeueThreadRunning();

/**
 * Dequeue thread should not exit. If it does, report to instuemntor.
 */
public void reportDequeueThreadExited(Exception e);

/**
 * Sets the alarm threshold for this queue.  Alarms will be generated when
 * the queue size is greater than alarmThreshold
 *
 * @param alarmThreshold - the min queue size for alarms.
 */
public void setAlarmThreshold(int alarmThreshold);

/**
 * Get the min alarm threshold
 *
 * @return int -
 */
public int getAlarmThreshold();

/**
 * Returns true if <code>setThreshold(int)</code> was called.
 */
public boolean hasAlarmThreshold();

public ListDefinition resetLastElement() throws QueueException;
}
