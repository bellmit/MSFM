package com.cboe.infrastructureServices.queue;

// java classes
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.utilities.TransactionListener;
import com.cboe.infrastructureServices.instrumentationService.InstrumentationService;
import com.cboe.infrastructureServices.instrumentationService.ServerUserDataHelper;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;
import com.cboe.instrumentationService.instrumentors.Status;



/**
 * This class manages a fifo queue.
 * Use the create method on the Home to get a unique queue object (named or unnamed).
 * A queue depth can be optionally specified. If the queue depth is not specified then a default value
 * will be used (See the queue interface for the default values).
 * The user can wait on the queue object for notification by calling the dequeue method or can poll
 * the queue.
 *
 * If the caller specifies a wait timeout on the queue then the enqueue and dequeue method will wait the
 * specified amount of time before giving up.
 *
 * If the caller doesnot specifies a 0 timeout then the enqueue and dequeue methods will not wait
 * for the operation to complete if either the queue is full (on enqueue) or if the queue is empty (on
 * dequeue).
 *
 * Currently the overall operation of the queue is managed by this class and the persistence layer
 * is provided by interface's ListDefinition and ListElement.
 *
 * NOTE: ALL THE METHODS THAT ACCESS THE UNERLYING ListDefinition HAVE TO BE SYNCHRONIZED AS THE
 *       ListDefinition itself is not synchronized (No point synchronizing the list definition because
 *		 multiple method calls on the list definition make up an atomic operation).
 *
 * @author Ravi Vazirani
 *
 * @author Henry He
 * Add instrumentation to the queue
 */
class QueueBaseImpl implements Queue, TransactionListener
{
     //The underlying list that provides the persistent layer.
    ListDefinition listDefinition;

    private boolean clearOnEnqueueFailure = false;

    // default to true so instrumentor is initialized
    private boolean isInstrumented = true;
    private String instrumentorName = null;
    private QueueInstrumentor queueInstrumentor = null;
    private static final int MAX_QUEUE_ERROR_REPORT = 20;
    private volatile int errorReportCount = 0;

    private volatile boolean queueSuspended = false;
    private volatile boolean dropEnqueueCalls = false;
    private Object suspendedNotificationObj = new Object();

    private volatile long numPacketsIgnoredWhileSuspended = 0;

    private Integer alarmThreshold;

    private long totalEnqueues = 0L;
    private long totalDequeues = 0L;

    /**
     * Optionally track the reader threads.
     */
    HashSet readerThreads = new HashSet(3); // normally 0..1 reader threads per queue

/**
 * Allows us to create a queue with the specified underlying persistence.
 *
 * @param ListDefinition the provider of the underlying persistence.
 */
public QueueBaseImpl(ListDefinition aListDefinition)
{
	listDefinition = aListDefinition;
        Log.information("QueueBaseImpl::QueueBaseImpl called to create a queue without instrumentor queueName=" + this.getQueueName());
}

public QueueBaseImpl(ListDefinition aListDefinition, String aInstrumentorName)
{
        listDefinition = aListDefinition;

        instrumentorName = aInstrumentorName;
        initQueueInstrument();
}

public void setClearOnEnqueueFailure(boolean value)
{
    clearOnEnqueueFailure = value;
}

public boolean getClearOnEnqueueFailure()
{
    return clearOnEnqueueFailure;
}

/**
 * Gets the next element from the beginning of the queue.
 * If the queue is empty and it is set in wait mode this call will
 * wait till data becomes available. <p>
 *
 * NOTE: Due to case of false notification it is possible that
 *       null value is returned.
 *
 * This method has to be synchronized because of more than one thread operating on the
 * queue, but special care has to be taken when dealing with persistent objects (as transaction
 * locks may still be held even after the method returns).
 *
 * NOTE: Transaction locks are released when the outermost transaction finally commits.
 *
 * @param int the wait mode timeout (if 0 is pecified then we are in no wait mode).
 * @return Object Queued object or null if the queue is empty.
 * @exception InterruptedException
 */
private synchronized Object _dequeue(int newWaitTime) throws QueueException
{
	Object returnObject = null;

	// Wait till there is data in the queue then dequeue.
	waitWhileEmpty(newWaitTime);

	// Get a transaction lock, when the final commit or rollback is done
	// that is when unlock will be called.
	listDefinition.lock();

	// Check for false notifications, or timeout expired.
	if (!_isEmpty())
	{
		// Start the transaction in case there is no outer transaction.
		boolean commited = false;
		listDefinition.startListTransaction();

		// Register a transaction listener. The listener is called finally when the transaction is
		// committed. (see commitEvent and rollbackEvent).
		listDefinition.notifyAfterTransaction(this);

		// Remove element from the beginning of the queue, perform basic link list operations.
		try
		{
            returnObject = getFirstListItem(true/*remove*/);
			listDefinition.commitListTransaction();
			commited = true;
            ++totalDequeues;
		}
		catch (QueueException ex)
		{
			throw ex;
		}
		catch (ListException ex)
		{
			throw new QueueException(ex + "");
		}
		finally
		{
			if (!commited)
			{
				listDefinition.rollbackListTransaction();
			}
		}
	}
	else // Since the queue is still empty, release the lock.
	{
		listDefinition.unlock();
	}

	return returnObject;
}
/**
 *  Refactored to remove dependency on linked lists
 */
protected Object getFirstListItem(boolean removeElement) throws ListException
{
    ListElement first = listDefinition.getFirstElement();
    Object returnObject = (first==null) ? null : first.getDataObject();

    if (removeElement && first != null)
    {
        listDefinition.setFirstElement(first.getNextElement());

        listDefinition.setSize(listDefinition.getSize() - 1);
        if (_isEmpty())
        {
            listDefinition.setLastElement(null);
        }

        listDefinition.destroyElement(first);
    }

    return returnObject;
}
/**
 * Queues a new object at the end of the queue. If the max queue
 * has reached then an exception is throw. <p>
 *
 * If the queue is in wait mode then this method will wait for the specfied
 * amount of time.<p>
 *
 * QueueFullException is raised if the object cannot be queued in the specified amount of time.
 *
 * This method has to be synchronized because of more than one thread operating on the
 * queue, but special care has to be taken when dealing with persistent object(as transaction
 * locks may still be held even after the method returns).
 *
 * NOTE: Transaction locks are released when the outermost transaction finally commits.
 *
 * @param Object Object to queue (cannot be null).
 * @param int timeout period to wait for enqueue to complete if queue is full
 *			  -1 means wait forever, 0 means do not wait at all, +ve value is wait timeout.
 * @return void
 * @exception QueueFullException If the queue is full.
 * @exception InterruptedException
 */
private synchronized void _enqueue(Object data, int newWaitTime) throws QueueException
{
	if (data == null)
	{
		throw new IllegalArgumentException("Data to queue cannot be null on queue: " + listDefinition.getListName());
	}

	// Wait for the specified amount of time if the queue if full.
	waitWhileFull(newWaitTime);

    // See suspendEnqueue(boolean), resumeEnqueue()
    //
    if (queueSuspended)
    {
        if (!dropEnqueueCalls)
        {
            synchronized(suspendedNotificationObj)
            {
                Log.information("QueueBaseImpl::Queue " +  getQueueName() + ": QUEUE IS SUSPENDED, BLOCKING ENQUEUE CALL UNTIL RESUMED");
                try
                {
                    suspendedNotificationObj.wait();
                }
                catch (InterruptedException ex)
                {
                }
                Log.information("QueueBaseIml::Queue " + getQueueName() + ": RESUMED ENQUEUING for thread " + Thread.currentThread().getName());
            }
        }
        else
        {
            ++this.numPacketsIgnoredWhileSuspended;
            return;
        }
    }

	// Get a transaction lock, when the final commit or rollback is done
	// that is when unlock will be called.
	listDefinition.lock();

	// Check for false notifications/race conditions.
	if (!_isFull())
	{
		// Start the transaction in case there is no outer transaction.
		listDefinition.startListTransaction();

		// Register a transaction listener. The listener is called finally when the transaction is
		// committed. (see commitEvent and rollbackEvent).
		listDefinition.notifyAfterTransaction(this);

		// Add the element to the end of the queue, basic link list operations.
		boolean commited = false;
		try
		{
            addToEndOfList(data);

			listDefinition.commitListTransaction();
			commited = true;
            ++totalEnqueues;
		}
		catch (ListException ex)
		{
			throw new QueueException(ex + "");
		}
		finally
		{
			if (!commited)
			{
				listDefinition.rollbackListTransaction();
			}
		}
	}
	else // if the queue is still full
	{
		listDefinition.unlock();
		throw new QueueFullException("Max Queue depth = " + listDefinition.getMaximumSize());
	}
}
/**
 *
 */
protected void addToEndOfList(Object data) throws ListException
{
    // Create and initialize the new node.
    ListElement newElement = listDefinition.newElement(data);

    // Add the entry to the end of the queue.
    if (_isEmpty())
    {
        listDefinition.setLastElement(newElement);
        listDefinition.setFirstElement(newElement);
    }
    else
    {
        listDefinition.getLastElement().setNextElement(newElement);
        listDefinition.setLastElement(newElement);
    }
    listDefinition.setSize(listDefinition.getSize()+1);
}
/**
 * Returns a true value if the queue is empty.
 * Internal unsynchronized method.
 *
 * @return boolean true if the queue is empty
 */
private boolean _isEmpty()
{
	return (listDefinition.getSize() == 0);
}
/**
 * Returns a true value if the queue is full.
 * Internal unsynchronized method.
 *
 * @return boolean true if the queue is full
 */
private boolean _isFull()
{
	return (listDefinition.getSize() >= listDefinition.getMaximumSize());
}
/**
 * Gets the first element from the beginning of the queue.
 * If the queue is empty this method returns null.  This call
 * does not use the defaultTimeout value: it is always NO_TIMEOUT.
 *
 * @param timeoutMillis - the timeout value to use: any positive number,
 *	or either constant NO_TIMEOUT or INFINITE_TIMEOUT.
 *
 * @return Object Queued object or null if the queue is empty.
 * @exception QueueException is case of errors.
 */
private synchronized Object _peek(int timeoutMillis) throws QueueException
{
	Object dataObject = null; // The returned object.

	// Wait till there is data in the queue.
	waitWhileEmpty(timeoutMillis);

	// Lock the list and always unlock before returning.
	// We need to acquire the lock we we donot want to access the list
	// while some else has a lock held. The other locker may end up
	// committing the transaction and data will change while we are accessing the data.
	listDefinition.lock();
	try
	{
        dataObject = getFirstListItem(false/*!remove*/);
	}
	catch (ListException ex)
	{
		throw new QueueException(ex.toString());
	}
	finally
	{
		listDefinition.unlock();
	}

 	return dataObject;
}
/**
 * Deletes all the entries in the queue.
 *
 * @exception QueueException If underlying persistence layer has errors.
 */
public synchronized void clear() throws QueueException
{

    listDefinition.startListTransaction();
    boolean committed = false;
    try
    {
        listDefinition.clear();
        listDefinition.commitListTransaction();
        committed = true;
    }
    catch (ListException ex)
    {
        throw new QueueException(ex.toString());
    }
    finally
    {
        if (!committed)
        {
            Log.alarm("QueueBaseImpl::Rolling back transaction (on queue.clear() for " + getQueueName() + ")");
            listDefinition.rollbackListTransaction();
        }
    }
}
/**
 *  Suspend the queue.
 *  On high rate queues, if there's a problem with the dequeue thread, then
 *  we may want the option of suspending the enqueue.  This will cause enqueue calls
 *  to block until resumeEnqueue() is called.
 *
 *  @param dropEnqueueCalls - if true, then enqueue() calls will be ignored, rather than
 *     blocked.
 */
public void suspendEnqueue(boolean dropEnqueueCalls)
{
    queueSuspended = true;
    this.dropEnqueueCalls = dropEnqueueCalls;
    Log.alarm("QueueBaseImpl::SUSPENDED QUEUE " + getQueueName() + ": calls to enqueue will "
        + (dropEnqueueCalls ? "drop" : "block on ") + " enqueue calls.");
    this.numPacketsIgnoredWhileSuspended = 0;
}

/**
 *  Resume the queue (clear suspended state).
 *  @see suspendEnqueue();
 */
public void resumeEnqueue()
{
    if (!queueSuspended)
    {
        Log.information("QueueBaseImpl::RESUME QUEUE " + getQueueName() + ": nothing to do, queue is not suspended.");
        return;
    }
    synchronized (suspendedNotificationObj)
    {
        queueSuspended = false;
        dropEnqueueCalls = false;
        suspendedNotificationObj.notifyAll();
        Log.alarm("QueueBaseImpl::RESUMED QUEUE " + getQueueName() + ".  " + this.numPacketsIgnoredWhileSuspended + " messages were ignored while suspended.");
    }
    this.numPacketsIgnoredWhileSuspended = 0;
}

/**
 * Processes the commit event at the end of a transaction.
 * This method is called when the outermost most transaction in a nested transaction finally
 * commits.
 *
 * NOTE: Currently this method cannot be synchronized because enqueue and dequeue
 * 		 are synchronized on the queue and the lock and unlock on the transaction may not happen in
 * 		 the same method if there are nested transactions outside of enqueue and dequeue methods.
 */
public void commitEvent()
{
	listDefinition.unlock(); // Notifies the locker to go ahead and try the lock again.

	synchronized (this)
	{
		// NOTE: We cannot optimize notifications because
		// we don't know the previous state of the queue.
		// The user migth have enqueued more than 1 element.
		notifyAll();
		// notify();
	}
}
/**
 * Dequeues and element from the queue.
 * See _dequeue for docuemtnation.
 *
 * @return Object Queued object or null if the queue is empty.
 * @exception Interrupted Exception
 */

public Object dequeue() throws QueueException
{
	return dequeue(getDefaultTimeout());
}
/**
 * Dequeues and element from the queue.
 * See _dequeue for docuemtnation.
 * @param int the wait mode timeout (if 0 is pecified then we are in no wait mode).
 * @return Object Queued object or null if the queue is empty.
 * @exception InterruptedException
 */
public Object dequeue(int newWaitTime) throws QueueException
{
	Object returnObject = null;

	// We need to wait if the caller specified wait forever till the dequeue is successfull.
	do
	{
		returnObject = _dequeue(newWaitTime);

	} while ( (returnObject == null) && (newWaitTime < 0) );

        // do instrumentation
        if (returnObject != null)
        {
            instrumentOnDequeue();
        }

	return returnObject;
}
/**
 * Dequeues and element from the queue.
 * See _dequeue for docuemtnation.
 *
 * @param int the nbr of elements to dequeue.
 * @return Object[] The 'n' data object of the topmost queue element, or <code>null</code> if
 *   			  the queue is empty.
 * @exception Interrupted Exception
 */

public Object[] dequeueMultiple(int nbrToDequeue) throws QueueException
{
    return dequeueMultiple(getDefaultTimeout(),nbrToDequeue);
}
/**
 * Dequeues and element from the queue.
 * See _dequeue for docuemtnation.
 * @param int the wait mode timeout (if 0 is pecified then we are in no wait mode).
 * @param int the nbr of elements to dequeue.
 * @return Object[] The 'n' data object of the topmost queue element, or <code>null</code> if
 *   			  the queue is empty.
 * @return Object Queued object or null if the queue is empty.
 * @exception InterruptedException
 */
public synchronized Object[] dequeueMultiple(int newWaitTime, int nbrToDequeue) throws QueueException
{
	boolean commited = false;
	Object[] objs;
	try
	{
		listDefinition.startListTransaction();
        listDefinition.notifyAfterTransaction(this);
		objs = getListItems(newWaitTime, nbrToDequeue);
		listDefinition.commitListTransaction();
		commited = true;
	}
	catch (QueueException ex)
	{
		throw ex;
	}
	catch (ListException ex)
	{
		throw new QueueException(ex + "");
	}
	finally
	{
		if (!commited)
		{
			listDefinition.rollbackListTransaction();
		}
	}

//     do instrumentation
    instrumentOnDequeueMultiple(objs.length);

	return objs;
}
/**
 * Refactored to allow for non-linked-list implementations.
 *
 * @param newWaitTime
 * @param nbrToDequeue
 * @param temp
 * @throws QueueException
 */
protected Object[] getListItems(int maxWaitTime, int nbrToDequeue) throws QueueException {
	ArrayList temp = new ArrayList(nbrToDequeue);
	Object returnObject = _dequeue(maxWaitTime);

	while (returnObject != null)
	{
		temp.add(returnObject);
		if (temp.size() >= nbrToDequeue || size() == 0) // We dequeued as much the caller has specified.
		{
			break;
		}
		returnObject = _dequeue(0);
	}

	return temp.toArray();
}

/**
 * @see Queue.dequeueFully(int, int, int)
 * @see Queue.dequeueFully(Object[], int, int, int)
 */
public synchronized Object[] dequeueFully(final int nbrToDequeue, int blockWaitMillis, final int maxWaitMillis) throws QueueException
{
    final ArrayList result = new ArrayList(nbrToDequeue);
    boolean infiniteMax = (maxWaitMillis==INFINITE_TIMEOUT);

    if (blockWaitMillis == INFINITE_TIMEOUT || (!infiniteMax && blockWaitMillis > maxWaitMillis))
    {
        blockWaitMillis = maxWaitMillis; // "blockWait must be <= maxWait"
    }

    long frTime = System.currentTimeMillis();

    // "Initially nap for blockWait if not empty, otherwise maxWait"
    //
    int napMillis = (size()==0) ? maxWaitMillis : blockWaitMillis;
    long maxTime = frTime + napMillis;

    boolean committed = false;
    listDefinition.startListTransaction();
    listDefinition.notifyAfterTransaction(this);
//Log.debug("Queue base impl: dequeueFully(" + nbrToDequeue + ", " + blockWaitMillis + ", " + maxWaitMillis + "): will initially nap for "+ napMillis + "ms.");
    try
    {
        Object obj;
        while ((infiniteMax || maxTime > frTime) && result.size() < nbrToDequeue)
        {
            if ((obj = _dequeue(napMillis)) == null)
            {
                break; // timed out: we're done.
            }
            result.add(obj);
            frTime = System.currentTimeMillis();
            if (!infiniteMax && frTime >= maxTime)
            {
                break; // timed out: we're done
            }

            if (result.size()==1 && blockWaitMillis!=INFINITE_TIMEOUT && (infiniteMax || frTime + blockWaitMillis < maxTime))
            {
                maxTime = frTime + blockWaitMillis; // "after the 1st dequeue, reduce max time from MaxWait to BlockWait if necessary."
                infiniteMax = false;
            }

            // "We're going back to sleep.  Figure out for how long."
            //
            napMillis = (size() > 0) ? NO_TIMEOUT : (int)(maxTime - frTime);
//Log.debug("Queue base impl: dequeueFully(" + nbrToDequeue + ", " + blockWaitMillis + ", " + maxWaitMillis + "): dequeued " + result.size() + " so far.  Will nap for "+ napMillis + " more ms.");
        }
        listDefinition.commitListTransaction();
        committed = true;
    }
    catch (QueueException ex)
    {
        throw ex;
    }
    catch (ListException ex)
    {
        throw new QueueException(ex + "");
    }
    finally
    {
        if (!committed)
        {
            listDefinition.rollbackListTransaction();
        }
    }
    if (!result.isEmpty()) {
        instrumentOnDequeueMultiple(result.size());
    }

    return (result.isEmpty() || !committed) ? null : result.toArray();
 }

/**
 * @see Queue.dequeueFully(Object[], int, int, int)
 */
public synchronized int dequeueFully(final Object[] result, int nbrToDequeue, int blockWaitMillis, final int maxWaitMillis) throws QueueException
{
    int resultIdx = 0;
    nbrToDequeue = Math.min(result.length, nbrToDequeue);
    boolean infiniteMax = (maxWaitMillis==INFINITE_TIMEOUT);

    if (blockWaitMillis == INFINITE_TIMEOUT || (!infiniteMax && blockWaitMillis > maxWaitMillis))
    {
        blockWaitMillis = maxWaitMillis; // "blockWait must be <= maxWait"
    }

    long frTime = System.currentTimeMillis();

    // "Initially nap for blockWait if not empty, otherwise maxWait"
    //
    int napMillis = (size()==0) ? maxWaitMillis : blockWaitMillis;
    long maxTime = frTime + napMillis;

    boolean committed = false;
    listDefinition.startListTransaction();
    listDefinition.notifyAfterTransaction(this);
//Log.debug("Queue base impl: dequeueFully(" + nbrToDequeue + ", " + blockWaitMillis + ", " + maxWaitMillis + "): will initially nap for "+ napMillis + "ms.");
    try
    {
        Object obj;
        while ((infiniteMax || maxTime > frTime) && resultIdx < nbrToDequeue)
        {
            if ((obj = _dequeue(napMillis)) == null)
            {
                break; // timed out: we're done.
            }
            result[resultIdx++] = obj;
            frTime = System.currentTimeMillis();
            if (!infiniteMax && frTime >= maxTime)
            {
                break; // timed out: we're done
            }

            if (resultIdx==1 && blockWaitMillis!=INFINITE_TIMEOUT && (infiniteMax || frTime + blockWaitMillis < maxTime))
            {
                maxTime = frTime + blockWaitMillis; // "after the 1st dequeue, reduce max time from MaxWait to BlockWait if necessary."
                infiniteMax = false;
            }

            // "We're going back to sleep.  Figure out for how long."
            //
            napMillis = (size() > 0) ? NO_TIMEOUT : (int)(maxTime - frTime);
//Log.debug("Queue base impl: dequeueFully(" + nbrToDequeue + ", " + blockWaitMillis + ", " + maxWaitMillis + "): dequeued " + result.size() + " so far.  Will nap for "+ napMillis + " more ms.");
        }
        listDefinition.commitListTransaction();
        committed = true;
    }
    catch (QueueException ex)
    {
        throw ex;
    }
    catch (ListException ex)
    {
        throw new QueueException(ex + "");
    }
    finally
    {
        if (!committed)
        {
            listDefinition.rollbackListTransaction();
        }
    }
    if (resultIdx > 0)
    {
        instrumentOnDequeueMultiple(resultIdx);
    }

    return resultIdx;
 }

/**
 * Queues a new object at the end of the queue.
 * See _enqueue for more documentation.
 *
 * @param Object Object to queue
 * @return void
 * @exception QueueFullException If the queue is full.
 * @exception QueueInterruptedException
 */
public void enqueue(Object data) throws QueueException
{
    enqueue(data, getDefaultTimeout());
}
/**
 * Queues a new object at the end of the queue.
 * See _enqueue for more documentation.
 *
 * @param Object Object to queue
 * @param int timeout period to wait for enqueue to complete if queue is full
 *			  -1 means wait forever, 0 means do not wait at all, +ve value is wait timeout.
 * @return void
 * @exception QueueFullException If the queue is full.
 * @exception InterruptedException
 */
public void enqueue(Object data, int newWaitTime) throws QueueException
{
	boolean needToWait = true;

	// We need to wait if the caller specified wait forever till the enqueue is successfull.
	while (needToWait)
	{
		try
		{
			_enqueue(data,newWaitTime);
			needToWait = false;

                        // do instrumentation if needed
                        instrumentOnEnqueue();
		}
		catch (QueueFullException e)
		{
			if (newWaitTime >= 0) // Wait time expired and the queue is still full.
			{
				throw e;
			}
		}
	}
}
/**
 * Sets the max queue depth.
 * If queue depth is zero or negative then a runtime exception is raised.
 *
 * @author Ravi Vazirani
 *
 * @param queueDepth int
 */
public synchronized int getDefaultTimeout() throws IllegalArgumentException
{
	return listDefinition.getDefaultTimeout();
}
/**
 * Sets the max queue depth.
 * If queue depth is zero or negative then a runtime exception is raised.
 *
 * @author Ravi Vazirani
 *
 * @param queueDepth int
 */
public synchronized int getMaxQueueDepth() throws IllegalArgumentException
{
	return listDefinition.getMaximumSize();
}
/**
 * Return the queue's name.
 * @return String the queue name.
 */
public synchronized String getQueueName()
{
	return listDefinition.getListName();
}
/**
 * Returns a true value if the queue is empty.
 *
 * @return boolean true if the queue is empty
 */
public synchronized boolean isEmpty()
{
	return (_isEmpty());
}
/**
 * Returns a true value if the queue is full.
 *
 * @return boolean true if the queue is full
 */
public synchronized boolean isFull()
{
	return (_isFull());
}
/**
 * Don't know how to achieve this yet.
 */
public synchronized void pack()
{
}
/**
 * Gets the first element from the beginning of the queue.
 * If the queue is empty returns null;
 *
 * @return Object Queued object or null if the queue is empty.
 * @exception QueueException In case of errors.
 */

public Object peek() throws QueueException
{
	return peek(getDefaultTimeout());
}
/**
 * Gets the first element from the beginning of the queue.
 * If the queue is empty this method returns null.  This call
 * does not use the defaultTimeout value: it is always NO_TIMEOUT.
 *
 * @param timeoutMillis - the timeout value to use: any positive number,
 *	or either constant NO_TIMEOUT or INFINITE_TIMEOUT.
 *
 * @return Object Queued object or null if the queue is empty.
 * @exception QueueException is case of errors.
 */
public Object peek(int timeoutMillis) throws QueueException
{
	Object returnObject = null; // The returned object.

	// We need to wait if the caller specified wait forever till the dequeue is successfull.
	do
	{
		returnObject = _peek(timeoutMillis);

	} while ( (returnObject == null) && (timeoutMillis< 0) );

	return returnObject;
}
/**
 * Peeks 'n' element from the queue.
 * See _dequeue for docuemtnation.
 *
 * @param int the nbr of elements to dequeue.
 * @return Object[] The 'n' data object of the topmost queue element, or <code>null</code> if
 *   			  the queue is empty.
 * @exception Interrupted Exception
 */

public Object[] peekMultiple(int nbrToPeek) throws QueueException
{
	return peekMultiple(getDefaultTimeout(),nbrToPeek);
}
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
public Object[] peekMultiple(int newWaitTime, int nbrToDequeue) throws QueueException
{
	Object[] result = null;
    Object resultObject = peek(newWaitTime);
	if (resultObject != null)
	{
		try
		{
			listDefinition.lock();
            result = peekFirstListItems(nbrToDequeue);
            if (result.length == 0)
            {
                result = new Object[] { resultObject };
            }
            if (result[0] != resultObject)
            {
                // This should probably be impossible.
                Object[] newResult = new Object[result.length+1];
                newResult[0] = resultObject;
                System.arraycopy(newResult, 1, result, 0, result.length);
                result = newResult;
            }

		}
		catch (ListException ex)
		{
			throw new QueueException(ex.toString());
		}
		finally
		{
			listDefinition.unlock();
		}
	}
  	return result;
}
/**
 *  Refactored to avoid linked lists
 */
protected Object[] peekFirstListItems(int numToPeek) throws ListException
{
    ArrayList list = new ArrayList(numToPeek);
    ListElement element = this.listDefinition.getFirstElement();
    while (element != null)
    {
        list.add(element.getDataObject());
        if (list.size() >= numToPeek) // We dequeued as much the caller has specified.
        {
            break;
        }

        element = element.getNextElement();
    }
    return list.toArray();
}
/**
 * Processes the rollback event at the end of a transaction.
 *
 * NOTE: Currently this method cannot be synchronized because enqueue and dequeue
 * 		 are synchronized on the queue and the lock and unlock on the transaction may not happen in
 * 	     the same method if there are nested transactions outside of enqueue and dequeue methods.
 */
public void rollbackEvent()
{
	listDefinition.unlock();
}
/**
 * Sets the default timeout.
 * If the timeout is 0  - NO_TIMEOUT then queue operation will not wait for completion.
 * If the timeout is -1 - INFINITE_TIMEOUT then the queue operation will wait for completion (due to case
 *						  of false notifications there is still a case where the queue operation may not
 *						  complete and will need to be tried again.
 * If the timeout is +ve  similar to INFINITE_TIMEOUT with the exception that the operation will timeout
 *						  after the specified amount of time.
 *
 * @param wait-mode-timeout.
 */
public synchronized void setDefaultTimeout(int timeout)
{
	if (timeout < 0)
	{
		timeout = INFINITE_TIMEOUT;
	}

	listDefinition.setDefaultTimeout(timeout);
}
/**
 * Sets the max queue depth.
 * If queue depth is zero or negative then a runtime exception is raised.
 *
 * @param queueDepth int
 * @exception IllegalArgumentException
 */
public synchronized void setMaxQueueDepth(int queueDepth) throws IllegalArgumentException
{
	if (queueDepth <= 0)
	{
		throw new IllegalArgumentException("Queue depth cannot be 0 or negative");
	}

	listDefinition.setMaximumSize(queueDepth);

	// Wake up the waiting threads becasue they might be able to queue
	// more now.
	this.notifyAll();
}
/**
 * Returns the number of elements queued
 *
 * @return int number of elements queued.
 */
public synchronized int size()
{
	return listDefinition.getSize();
}
/**
 * Returns the string representation of the queue.
 *
 * @return java.lang.String
 */
public synchronized String toString()
{
	StringBuffer buf = new StringBuffer("nbrQueued(");
	buf.append(size());
	buf.append("), sizeOfFreeList(");
	buf.append(listDefinition.getReserveSize() + ")");
	buf.append(", maxQueueDepth(" + listDefinition.getMaximumSize() + ")");
	buf.append(", ttlEnq(" + totalEnqueues + ")");
	buf.append(", ttlDeq(" + totalDequeues + ")");
    if (queueSuspended)
    {
        buf.append(" [QUEUE IS SUSPENDED: enqueue calls are ");
        buf.append(dropEnqueueCalls ? "IGNORED]" : "BLOCKED]");
        if (dropEnqueueCalls)
        {
            buf.append(" Ignored " + this.numPacketsIgnoredWhileSuspended + " messages.");
        }
    }
    if (clearOnEnqueueFailure)
    {
        buf.append(" [clears when full]");
    }
	return listDefinition.getListName() + "/" + buf.toString();
}
/**
 * Return queue status
 */
public synchronized String toString(String prefix)
{
    StringBuffer buf = new StringBuffer(300).append(prefix).append(toString());

    boolean oneThread = (readerThreads.size() == 1);

    for (Iterator iter = readerThreads.iterator(); iter.hasNext(); /*no incr*/)
    {
        Thread t = (Thread)iter.next();
        buf.append(oneThread ? " (" : ("\n  "+prefix));
        buf.append("Reader ").append(t.getName());
        buf.append(" isAlive=").append(t.isAlive());
        buf.append(oneThread ? ")" : "");
    }
    return buf.toString();
}

/**
 *  Register a thread as a reader of this queue.  This is expected to be used for
 *  toString(String) reader thread status reporting only.
 */
public synchronized void addReaderThread(Thread t)
{
    readerThreads.add(t);
}

/**
 *  Remove a thread as a reader of this queue.
 *  @return boolean - true IFF the thread was removed.
 *  @see addReaderThread(Thread)
 */
public synchronized boolean removeReaderThread(Thread t)
{
    return readerThreads.remove(t);
}

/**
 *  Remove a thread as a reader of this queue.
 *  @see addReaderThread(Thread)
 *  @return Thread - the thread removed.  Null if no thread named threadName is found.
 */
public synchronized Thread removeReaderThread(String threadName)
{
    for (Iterator iter = readerThreads.iterator(); iter.hasNext(); /*no incr*/)
    {
        Thread t = (Thread)iter.next();
        if (t.getName().equals(threadName))
        {
            return removeReaderThread(t) ? t : null;
        }
    }
    return null;
}

/**
 *  Remove a thread as a reader of this queue.
 *  @return the number of reader threads removed.
 *  @see addReaderThread(Thread)
 */
public synchronized int removeAllReaderThreads()
{
    int result = readerThreads.size();
    readerThreads.clear();
    return result;
}



/**
 * Waits while the queue is empty.
 * @param int the wait mode timeout or 0 if queue if no-wait-mode is specified.
 * @exception QueueException if the queue is interrupted.
 */
private void waitWhileEmpty(int waitTime) throws QueueException
{
	boolean waitMode = (waitTime != NO_TIMEOUT);

	// If the queue is empty and we are supposed to wait for more data.
	if (waitMode && _isEmpty())
	{
		try
		{
			if (waitTime < 0)
			{
				while (_isEmpty())
				{
					this.wait();
				}
			}
			else
			{
				long endTime = System.currentTimeMillis() + waitTime; // Wait is over after the time.
				long timeRemaining = waitTime;

				while ( _isEmpty() && (timeRemaining > 0) ) // Wait for timeout to expire.
				{
					this.wait(timeRemaining);
					timeRemaining = endTime - System.currentTimeMillis();
				}
			}
		}
		catch (InterruptedException ex)
		{
			throw new QueueInterruptedException("Queue wait was interrupted in dequeue(" + waitTime + ")");
		}
	}
}
/**
 * Waits while the queue is full.
 * @param int the wait mode timeout or 0 if queue if no-wait-mode is specified.
 * @exception QueueException if the queue is interrupted.
 */
private void waitWhileFull(int waitTime) throws QueueException
{
	boolean waitMode = (waitTime != NO_TIMEOUT);

        if (_isFull() && clearOnEnqueueFailure)
        {
            try
            {
                int oldSize = size();
                listDefinition.clear();
                Log.alarm("QueueBaseImpl::FORCEFULLY CLEARED QUEUE " + getQueueName() + ": size was " + oldSize + " on an enqueue call (queue was full)");
            }
            catch (ListException ex)
            {
                throw new QueueException("" + ex);
            }
        }

	// If the queue is empty and we are supposed to wait for more data.
	if (waitMode && _isFull())
	{

		try
		{
			if (waitTime < 0)
			{
				while (_isFull())
				{
					this.wait();
				}
			}
			else
			{
				long endTime = System.currentTimeMillis() + waitTime; // Wait is over after the time.
				long timeRemaining = waitTime;

				while ( _isFull() && (timeRemaining > 0) ) // Wait for timeout to expire.
				{
					this.wait(timeRemaining);
					timeRemaining = endTime - System.currentTimeMillis();
				}
			}
		}
		catch (InterruptedException ex)
		{
			throw new QueueInterruptedException("Queue wait was interrupted in enqueue(" + waitTime + ")");
		}
	}
}

    /**
     * set method for isInstrumented flag. Note that instrumentor is initialized
     * in the queue creation process. The change of the flag will enable/disable
     * the instrumentor counting activity only.
     */
    public void setInstrumented(boolean b) {
        Log.information("QueueBaseImpl::setInstrumented before change: " + getInstumentorInfo() + ". To be set to " + b);
        isInstrumented = b;
    }

    /**
     * get method for isInstrumented flag.
     */
    public boolean getInstrumented() { return isInstrumented; }

    /**
     * Do instrumentation when enqueue is called.
     */
    private void instrumentOnEnqueue()
    {
        // do not do enqueue instrumentation if isInstrumented is false.
        if (!getInstrumented())
            return;

        if (queueInstrumentor != null)
        {
            queueInstrumentor.incEnqueued(1);
            queueInstrumentor.setCurrentSize(listDefinition.getSize());
        }
        else if (errorReportCount < MAX_QUEUE_ERROR_REPORT)
        {
            // only report this error up to MAX_QUEUE_ERROR_REPORT times
            errorReportCount++;
            Log.information("QueueBaseImpl::instrumentOnEnqueue queueInstrumentor is null. " + this.getInstumentorInfo());
        }
    }

    /**
     * Do instrumentation when dequeueMultiple is called.
     */
    private void instrumentOnDequeueMultiple(int msgSize)
    {
        if (!getInstrumented())
            return;

        if (queueInstrumentor != null)
        {
            queueInstrumentor.incDequeued(msgSize);
            queueInstrumentor.setCurrentSize(listDefinition.getSize());
        }
        else if (errorReportCount < MAX_QUEUE_ERROR_REPORT)
        {
            // only report this error up to MAX_QUEUE_ERROR_REPORT times
            errorReportCount++;
            Log.information("QueueBaseImpl::instrumentOnDequeueMultiple queueInstrumentor is null. " + this.getInstumentorInfo());
        }
    }

    /**
     * Do instrumentation when dequeue is called.
     */
    private void instrumentOnDequeue()
    {
        if (!getInstrumented())
            return;

        instrumentOnDequeueMultiple(1);
    }

    /**
     * Initialize instrumentor from instrumentation service for this queue.
     */
    private void initQueueInstrument()
    {
        Log.information("QueueBaseImpl::initQueueInstrument() to init queue instrumentor. " + getInstumentorInfo());
        if ( (getInstrumentorName() == null) || (getInstrumentorName().length() == 0) )
        {
            queueInstrumentor = null;
            return;
        }
        InstrumentationService srvc = FoundationFramework.getInstance().getInstrumentationService();
        try
        {
            if (srvc.getQueueInstrumentorFactory() != null) {
                queueInstrumentor = srvc.getQueueInstrumentorFactory().create(getInstrumentorName(), null);
                if (queueInstrumentor != null) {
                    srvc.getQueueInstrumentorFactory().register(queueInstrumentor);
                    queueInstrumentor.setStatus(Status.INIT);
                }
                else {
                    Log.information("QueueBaseImpl::initQueueInstrument() get null instrumentor from InstrumentationService. " +
                                    getInstumentorInfo());
                }
            }
            else {
                Log.information("QueueBaseImpl::initQueueInstrument() get null instrumentor factory from InstrumentationService. " +
                                getInstumentorInfo());
            }
        }
        catch( InstrumentorAlreadyCreatedException e )
        {
            Log.exception(e);
            // find from the instrumentation service cache.
            queueInstrumentor = srvc.getQueueInstrumentorFactory().find(getInstrumentorName());
        }
        catch (Exception e)
        {
            // do nothing
            Log.exception(e);
        }
    }

    protected String getInstrumentorName() { return instrumentorName; }

    /**
     * Dequeue thread should not exit. If it does, report to instuemntor.
     */
    public void reportDequeueThreadExited(Exception e)
    {
        if (!getInstrumented())
        {
            Log.information("QueueBaseImpl::reportDequeueThreadExited do nothing. " + getInstumentorInfo());
            return;
        }

        if (queueInstrumentor == null)
        {
            Log.information("QueueBaseImpl::reportDequeueThreadExited instrumentor is null. " + getInstumentorInfo());
            return;
        }
        if (e == null)
        {
            e = new Exception("Queue thread has exited without specific exception.");
        }
        ServerUserDataHelper.setUserData(queueInstrumentor, e);
        queueInstrumentor.setStatus(Status.THREAD_EXITED);
        Log.information("QueueBaseImpl::reportDequeueThreadExited set instrumentor status to THREAD_EXITED. "
                        + getInstumentorInfo());
    }

    /**
     * When dequeue thread starts running, report to the instrumentor.
     */
    public void reportDequeueThreadRunning()
    {
        if (!getInstrumented())
        {
            Log.information("QueueBaseImpl::reportDequeueThreadRunning Queue instrumentor do nothing. " + getInstumentorInfo());
            return;
        }

        if (queueInstrumentor != null)
        {
            ServerUserDataHelper.setUserData(queueInstrumentor, ServerUserDataHelper.THREAD_RUNNING_TEXT);
            queueInstrumentor.setStatus(Status.THREAD_RUNNING);
            Log.information("QueueBaseImpl::reportDequeueThreadRunning set status to THREAD_RUNNING. " + getInstumentorInfo());
        }
        else
        {
            Log.information("QueueBaseImpl::reportDequeueThreadRunning queueInstrumentor is null. " +  getInstumentorInfo());
        }
    }

    public String getInstumentorInfo()
    {
        String rc = "";
        rc = "instrumentor queueName=" + listDefinition.getListName()
            + " instrumentorName=" + getInstrumentorName()
            + " isInstrumented=" + getInstrumented() + ".";
        return rc;
    }

    /**
     * Sets the alarm threshold for this queue.  Alarms will be generated when
     * the queue size is greater than alarmThreshold
     *
     * @param alarmThreshold - the min queue size for alarms.
     */
    public void setAlarmThreshold(int alarmThreshold)
    {
        this.alarmThreshold = new Integer(alarmThreshold);
    }


    /**
     * Get the min alarm threshold
     *
     * @return int - the threshold
     */
    public int getAlarmThreshold()
    {
        return (alarmThreshold==null) ? -1 : alarmThreshold.intValue();
    }

    /**
     * Returns true if <code>setThreshold(int)</code> was called.
     */
    public boolean hasAlarmThreshold()
    {
        return (alarmThreshold != null);
    }


    /**
     * DO NOT CALL This method from anywhere other than the QueueHome createPersistent
     * method. This method resets the last element in the list definition to the
     * last element in the linked list.
     * @throws QueueException
     */
    public ListDefinition resetLastElement() throws QueueException
    {
          try
           {
               ListElement firstElement = listDefinition.getFirstElement();
               ListElement lastElement = firstElement;
               if(listDefinition.getSize() == 0)
               {
                   Log.information(listDefinition.getListName() + ">> Resetting of the last element is skipped because the queue size is zero ");
                   return listDefinition;
               }

               Log.information(listDefinition.getListName() + "Starting to rebuild the queue of size:" + listDefinition.getSize());
               int counter = 0;

               //This while loops finds the last element of the queue.
               while(null != (firstElement = firstElement.getNextElement()))
               {
                      lastElement = firstElement;
                      counter++;
                      if(counter%30 == 0)
                        Log.information(listDefinition.getListName() + "Completed rebuilding " + counter + " elements of the queue");
               }

               //sets the correct last element.
               listDefinition.setLastElement(lastElement);

               Log.information(listDefinition.getListName() + "Done rebuilding the Queue");
           }
           catch (Exception ex)
           {
                   Log.exception(listDefinition.getListName() + "Error setting the Last Element of the Queue", ex);
                   throw new QueueException(listDefinition.getListName() + "Error creating new persistent queue." + ex.toString());
           }
        return listDefinition;
    }

}
