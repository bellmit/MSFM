package com.cboe.infrastructureServices.queue;

/**
 * Defines the accessor/mutator methods required for whatever implementation of the queue's underlying list elements
 * is to be used.
 *
 * @author Steven Sinclair
 */
interface ListElement
{
/**
 *  Return the data object of this list element.
 *
 * @return Object - the element's data
 */
Object getDataObject();
/**
 *  return a reference to the next element in the list.
 *
 *  @return ListElement - the next element in the list, or null if this is the last element in the list.
 */
ListElement getNextElement();
/**
 *  Set the data for this list element.
 *
 *  @param data - any Object.  It may need to be Serializable/Externalizable, depending on the queue implementation
 *   	(ie, transient queues can use any object, but persistent ones must be supplied with streamable objects).
 */
void setDataObject(Object data);
/**
 *  Set the reference to the next element in the list.
 *
 *  @param nextElement - the next element in the list.  The "null" value is legal.
 */
void setNextElement(ListElement nextElement);
}
