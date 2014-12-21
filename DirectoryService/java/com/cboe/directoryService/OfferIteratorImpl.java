//-----------------------------------------------------------------------
// FILE: OfferIteratorImpl.java
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

import java.util.Properties;

import org.omg.CosTrading.Offer;
import org.omg.CosTrading.OfferIteratorOperations;
import org.omg.CosTrading.OfferIteratorPOATie;
import org.omg.CosTrading.OfferSeqHolder;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import com.cboe.common.log.Logger;


/**
*  This class holds on to the results found when a list of offers
*  was found from the query method in the LookupImpl class.
*  It provides the max_left method to get the number of offers left
*  and the next_n method to get a specified number of offers.
*  The client should use the destroy method to remove the instance
*  of this class when finished with it.
*
* @author             Judd Herman
*/
public class OfferIteratorImpl
implements OfferIteratorOperations
{
	/** for logging, common name to give all log messages*/
	private static final String CLASS_ID = OfferIteratorOperations.class.getSimpleName();
		
	/** Offer 'already returned' count */
	private int returnedCount;

	/** a reference to myself */  
	private OfferIteratorPOATie connectedObject;
	
	/** Offer sequence to be returned */
	private Offer[] offerSeq;
        
	/**
	* Constructor
	*/
	public OfferIteratorImpl()
	{ }

	/**
	* Constructor
	*/
	public OfferIteratorImpl(Offer[] offerSeq, Properties traderProperties)
	{
		this.offerSeq = new Offer[offerSeq.length];
		System.arraycopy(offerSeq, 0, this.offerSeq, 0, offerSeq.length);
		returnedCount = 0;
	}

	/**   
	* Gets the number of offers left in the iterator
	* @return amount left in the offers array
	*/
	public int max_left()
	{
		return (offerSeq.length - returnedCount);
	}

	/**
	* Get the next 'n' offers. If 'n' is greater than
	* the available number, just return the rest that exist.
	* @param n number of offers to return 
	* @param offers the offer sequence holder
	* @return true if more offers exist, false otherwise.
	*/
	public boolean next_n(int n, OfferSeqHolder offers)
	{
		int len = Math.min(max_left(), n);
		Offer[] retVal = new Offer[len];
		offers.value = retVal;
		for (int i=0; i<len; i++) {
			retVal[i] = offerSeq[returnedCount];
			returnedCount++;
		}

		return (max_left() > 0);
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
			Logger.sysWarn(format(CLASS_ID, METHOD_ID), wp);
		}
		catch(ObjectNotActive ona) {
			Logger.sysWarn(format(CLASS_ID, METHOD_ID), ona);
		}
	}

	/**
	* set the CORBA object for disconnecting. 
	* @param obj the iterator object
	*/
	public void setConnectedObject(OfferIteratorPOATie obj)
	{
		connectedObject = obj;
	}

}
