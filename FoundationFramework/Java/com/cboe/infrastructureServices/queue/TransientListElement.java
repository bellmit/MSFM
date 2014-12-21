package com.cboe.infrastructureServices.queue;

/**
 * A simple implementation of the ListElement interface.  Simply maps the accessor/mutator methods
 * to local private variables.
 *
 * @author Steven Sinclair
 * @author Ravi Vazirani - added documentation.
 */
class TransientListElement implements ListElement
{
   /**
	* Next element to this element.
	*/
	private ListElement nextElement;
	
   /**
	* Data contained in this element.
	*/
	private Object dataObject;
/**
 * TransientQueueElement constructor comment.
 */
public TransientListElement()
{
}
/**
 * getDataObject method comment.
 */
public Object getDataObject()
{
	return dataObject;
}
/**
 * getNextElement method comment.
 */
public ListElement getNextElement()
{
	return nextElement;
}
/**
 * setDataObject method comment.
 */
public void setDataObject(Object dataObject)
{
	this.dataObject = dataObject;
}
/**
 * setNextElement method comment.
 */
public void setNextElement(ListElement nextElement)
{
	this.nextElement = nextElement;
}
}
