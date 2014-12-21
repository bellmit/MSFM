//-----------------------------------------------------------------------
// FILE: OfferIdIteratorImpl.java
//
// PACKAGE: com.cboe.directoryService.CosTrading
//
//-----------------------------------------------------------------------
//
// Copyright (c) 1998 The Chicago Board Options Exchange. All Rights Reserved.
//
//
//------------------------------------------------------------------------
package com.cboe.directoryService;

import static com.cboe.directoryService.TraderLogBuilder.format;

import org.omg.CosTrading.OfferIdIteratorPOA;
import org.omg.CosTrading.OfferIdIteratorPOATie;
import org.omg.CosTrading.OfferIdSeqHolder;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import com.cboe.common.log.Logger;

/**
*  This class holds on to the results found when a list of offer ids
*  was found from the list_offers method in the AdminImpl class.
*  It provides the max_left method to get the number of offer ids left
*  and the next_n method to get a specified number of offers ids.
*  The client should use the destroy method to remove the instance
*  of this class when finished with it.
*
* @author             Judd Herman
*/
public class OfferIdIteratorImpl
extends OfferIdIteratorPOA
{
	/** for logging, common name to give all log messages*/
	private static final String CLASS_ID = OfferIdIteratorImpl.class.getSimpleName();
	
	/** Offer ID Array */
	private String[] offerIDs;

	/** Offer ID 'already returned' count */
	private int returnedCount;

	/** Holds a reference to itself */  
	private OfferIdIteratorPOATie connectedObject;

	public OfferIdIteratorImpl()
	{ }

	public OfferIdIteratorImpl(String[] ids, int count)
	{
		offerIDs = new String[ids.length];
		System.arraycopy(ids, 0, offerIDs, 0, ids.length);
		returnedCount = count;
	}

	/**   
	* Gets the number of offers left in the iterator
	* @return amount left in the naming enumeration
	*/
	public int max_left()
	{
		return (offerIDs.length - returnedCount);
	}

	/**
	* Get the next 'n' offers. If 'n' is greater than
	* the available number, just return the rest that exist.
	* @param n number of offer IDs to return 
	* @param ids the sequence holder of offer IDs
	* @return true if more offer IDs exist, false otherwise.
	*/
	public boolean next_n(int n, OfferIdSeqHolder ids)
	{
		int len = Math.min(max_left(), n);
		String[] returnStrings = new String[len];
		ids.value = returnStrings;
		for (int i=0; i<len; i++) {
			returnStrings[i] = offerIDs[returnedCount];
			returnedCount++;
		}

		return (max_left() > 0);
	}

	/**
	* set the CORBA object for disconnecting.
	* @param org.omg.CosTrading.OfferIdIterator object
	*/
	public void setConnectedObject(OfferIdIteratorPOATie obj)
	{
		connectedObject = obj;
	}

	/**
	* Disconnect the object from the orb 
	*/
	public void destroy()
	{
		final String METHOD_ID = "destroy";
		try {
			if (!connectedObject._non_existent()) {
				connectedObject._poa().deactivate_object(connectedObject._object_id());
			}
		}
		catch(WrongPolicy wp) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID), wp);
		}
		catch(ObjectNotActive ona) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID),ona);
		}
	}
}
