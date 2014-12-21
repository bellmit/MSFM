package com.cboe.infrastructureServices.queue;

import com.cboe.infrastructureServices.foundationFramework.utilities.TransactionListener;

/**
 * A simple implemenation of the ListDefinition interface.  The <code>destroyElement(ListElement)</code> and
 * <code>newElement(Object)</code> methods manage an unbounded "free list".  All accessor/mutator methods simply
 * refer to local private variables.
 *
 * This is bascially an in memory link list.
 *
 * NOTE: ALL ACCESS MUST BE SYNCHRONIZED EXTERNALLY.
 *
 * @author Steven Sinclair
 * @author Ravi Vazirani - added documentation, made code changes to limit the size of the free pool.
 */
class TransientListDefinition implements ListDefinition
{
  // CLASS VARIABLES
   /**
	* Maximum size of the free pool.
	*/
	private static final int MAX_FREE_POOL_SIZE = 200;

  // INSTANCE VARIABLES
   /**
	* Name of the queue.
	*/
	private String listName;

   /**
	* Reference to the listener, needed to keep the flow of the ListDefinition protocol where
	* we need to notiofy the listener when the transaction is committed, even though there
	* really is no real Transaction going on.
	*/
	private TransactionListener listener;

   /**
	* Refrence to first element in the list.
	*/
	private ListElement firstElement;
	
   /**
	* Refrence to last element in the list.
	*/
	private ListElement lastElement;
	
   /**
	* Current size of the queue. (i.e. number of unread elements).
	*/
	private int size;
	
   /**
	* Maximum allowed queue depth.
	*/
	private int maximumSize = Integer.MAX_VALUE;
	
   /**
	* Default wait-mode timeout.
	* If 0 then the queue is no-wait mode.
	* If -ve then queue operations will wait for ever.
	*/
	private int defaultTimeout = Queue.NO_TIMEOUT;
	
   /**
	* Reference to first element in free pool.
	*/
	private ListElement freeElementPool;

   /**
	* Current free pool size.
	*/
	private int freePoolSize = 0;
/**
 * Allows instantiation.
 */
public TransientListDefinition()
{
	super();
}

/**
 * Clear the list.
 */
public void clear()
{
    firstElement = null;
    lastElement = null;
    size = 0;
}

/**
 *  Commit a fictitious transaction by  calling the registered listener.
 */
public void commitListTransaction()
{
    if (listener != null)
    {
	    listener.commitEvent();
    }
}
/**
 * Queues the element in the free list.
 * If the size of the free pool is exceeded then we just loose the reference.
 */
public void destroyElement(ListElement elementToDestroy)
{
	if (freePoolSize < MAX_FREE_POOL_SIZE)
	{
		elementToDestroy.setNextElement(freeElementPool);
		elementToDestroy.setDataObject(null);
		freeElementPool = elementToDestroy;
		freePoolSize++;
	}
}
/**
 * Returns the default wait mode timeout associated with the queue.
 * @return int the timeout value.
 */
public int getDefaultTimeout()
{
	return defaultTimeout;
}
/**
 * Returns the first element in the queue.
 * @return ListElement the first element.
 */
public ListElement getFirstElement()
{
	return firstElement;
}
/**
 * Returns the last element in the queue.
 * @return ListElement the last element.
 */
public ListElement getLastElement()
{
	return lastElement;
}
/**
 * Returns the name of the queue.
 * @return String the queue name.
 */
public String getListName()
{
	if (listName == null)
	{
		return "Un-named queue";
	}
	else
	{
		return listName;
	}
}
/**
 * Returns the maximum queue depth allowed on the queue.
 * @return int the maximum queue depth.
 */
public int getMaximumSize()
{
	return maximumSize;
}
/**
 * Return the number of elements in the free list. 
 *
 * @return int the size of the free list.
 */
public int getReserveSize()
{
	return freePoolSize;
}
/**
 * Returns the current size of the queue.
 */
public int getSize()
{
	return size;
}
/**
 * Locks this list.  Required for interface - no implmentation.
 */
public void lock() {
}
/**
 * Create a new a list element if none exists in the free pool.
 * @return ListElement the new element.
 */
public ListElement newElement(Object dataObject)
{
	ListElement listElement;
	if (freeElementPool == null)
	{
		listElement = new TransientListElement();
	}
	else // get from the free pool, order is important.
	{
		listElement = freeElementPool;
		freeElementPool = freeElementPool.getNextElement();
		listElement.setNextElement(null);
		freePoolSize--;
	}
	listElement.setDataObject(dataObject);
	return listElement;
}
/**
 * Registers listener with the transaction. No-transaction for transient list.
 * But need to save of the listener, for when commitListTransaction is called we will
 * call the commitEvent method on the listener.
 */
public void notifyAfterTransaction(TransactionListener aListener)
{
	listener = aListener;
}
/**
 *  Rollback a transaction: does nothign since there are no transient transactions.
 */
public void rollbackListTransaction()
{
}
/**
 * Sets the default wait mode timeout on the queue.
 * @param defaultTimeout the timeout value.
 */
public void setDefaultTimeout(int defaultTimeout)
{
	this.defaultTimeout = defaultTimeout;
}
/**
 * Sets the first element in the queue.
 * @param firstElement the first element.
 */
public void setFirstElement(ListElement firstElement)
{
	this.firstElement = firstElement;
}
/**
 * Sets the last element in the queue.
 * @param lastElement the last element.
 */
public void setLastElement(ListElement lastElement)
{
	this.lastElement = lastElement;
}
/**
 * Sets the name of the queue.
 * @param listName the name of the queue.
 */
public void setListName(String listName)
{
	this.listName = listName;
}
/**
 * Sets the maximum queue depth allowed on the queue.
 * @param newMaxSize the maximum allowed qeueue depth.
 */
public void setMaximumSize(int newMaxSize)
{
	this.maximumSize = newMaxSize;
}
/**
 * Sets the current size of the queue.
 * @param newSize the size of the queue.
 */
public void setSize(int newSize)
{
	this.size = newSize;
}
/**
 * Begin a transaction: does nothing since there are no transient transactions.
 */
public void startListTransaction()
{
}
/**
 * Unlocks this object.  Required for interface - no implementation.
 */
public void unlock() {
}
}
