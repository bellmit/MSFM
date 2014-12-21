package com.cboe.infrastructureServices.queue;

import com.cboe.infrastructureServices.foundationFramework.utilities.TransactionListener;

/**
 *  This is used to allow the QueueBaseImpl to "plug-in" different storage strategies for the queue elements.
 *  This interface specifies how to access and modify queue state information, as well as how to manage the
 *  creation and deletion of queue elements.
 *
 * @author Steven Sinclair
 */
interface ListDefinition
{
/**
 *  Called when a list modification is complete: this is to allow transaction list definitions.
 */
void commitListTransaction() throws ListException;
/**
 * Implement a method for disposing of old elements.  This could be simply ignoring the reference
 * or managing a pool of free elements.
 *
 * @param element - the ListElement to "free".
 */
void destroyElement(ListElement element) throws ListException;
/**
 *  Return the default timeout in milliseconds.  May be one of the constants defined
 *  in <code>Queue</code>, NO_TIMEOUT or INFINITE_TIMEOUT.
 */
int getDefaultTimeout();
/**
 *  Return the first element in the list.
 */
ListElement getFirstElement();
/**
 *  Return the last element in the list.
 */
ListElement getLastElement();
/**
 *  Return the name of the list, or null if the list is anonymous.
 */
String getListName();
/**
 *  Return the maximum size of the list.
 */
int getMaximumSize();
/**
 *  Get the number of available free elements.
 */
int getReserveSize();
/**
 *  Return the size of the list.
 */
int getSize();
/**
 * Locks this list until unlock is called.
 */
void lock();
/**
 *   Return a new element.  This could be a simple <code>new XxxxXxxx()</code> call, or part
 *   of the managment of a pool of free elements.
 */
ListElement newElement(Object dataObject) throws ListException;
/**
 * Notifies listener at the end of the current transaction or immediately if no transaction is in progress.
 */
void notifyAfterTransaction(TransactionListener listener);
/**
 *  Cancel a failed list modification sequence, just in case the list definition is transactional.
 */
void rollbackListTransaction();
/**
 *  Set the default timeout used for enqueue and dequeue operations.
 */
void setDefaultTimeout(int timeoutValue);
/**
 *   Set the reference to the first element of the list.
 */
void setFirstElement(ListElement firstElement);
/**
 *  Set the reference to the last element in the list.
 */
void setLastElement(ListElement lastElement);
/**
 *  Set the list's name.  null is legal for anonymous lists.
 */
void setListName(String listName);
/**
 *  Set the maximum size of the list.
 */
void setMaximumSize(int newMaxSize);
/**
 *  Set the size of the list.  It is quite important that this be synchronized properly with the true list
 *  size, since list manipulation algorithms depend on these values matching.
 */
void setSize(int newSize);
/**
 *  Called when a list modification sequence begins:  this is to allow transactional list definitions.
 */
void startListTransaction();
/**
 * Unlocks this list.
 */
void unlock();
/**
 * Clears this list.
 */
void clear() throws ListException;
}
