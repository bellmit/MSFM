/**
 * 
 */
package com.cboe.presentation.omt;

import com.cboe.interfaces.presentation.omt.MarketabilityCheckedListener;
import com.cboe.interfaces.presentation.omt.MessageCollection;
import com.cboe.interfaces.presentation.omt.MessageElement;

/**
 * Adapt an orderMessageCollection to the OMTMarketabilityWorker result.
 * Once the OMTMarketabilityWorker is done with the marketability of a MessageElement 
 * it will fire an event and this class will udpate the collection OrderMessageCollection.
 * 
 * @author Eric Maheo
 * 
 */
public class OrderMessageCollectionAdapter implements MarketabilityCheckedListener
{
	
	private final OrderMessageCollection msgCollection;

	/**
	 * Create an object OrderMessageCollectionAdapter.
	 * @param collection adaptee.
	 */
	public OrderMessageCollectionAdapter(MessageCollection collection){
		if ((collection instanceof OrderMessageCollection) == false){
			throw new ClassCastException("MessageCollection is expected to be OrderMessageCollection.");
		}
		msgCollection = (OrderMessageCollection) collection;
	}
	
	
	/**
	 * On event updates the collection with the MessageElement.
	 */
	@Override
	public void messageElementMarketabilityUpdated(MessageElement element)
	{
		msgCollection.updateElement(element);
	}

}
