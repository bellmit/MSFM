//
// -----------------------------------------------------------------------------------
// Source file: MarketQueryV5API.java
//
// PACKAGE: com.cboe.interfaces.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.api;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.util.event.EventChannelListener;


/**
 * MarketQueryV5API allows to subscribe a client at the product level.<br>
 * All events occuring at the product itself will send a notification 
 * to the clients.
 * 
 * @author Eric Maheo
 * 
 */
public interface MarketQueryV5API
{
	/**
	 * Subscribe a client to the current market for a product.
	 * The product can be either an Option Series or a Strategy Series.
	 * 
	 * @param aClassKey			class id.
	 * @param aProductKey		product id.
	 * @param clientListener	client listener.
	 * @throws SystemException			
	 * @throws CommunicationException	
	 * @throws AuthorizationException	
	 * @throws DataValidationException	
	 */
	public void subscribeCurrentMarketByProductV5(int aClassKey, int aProductKey,
			EventChannelListener clientListener)
		throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
	
	/**
	 * Unsubscribe a client to the current market for a product.
	 *  
	 * @param aClassKey
	 * @param aProductKey
	 * @param clientListener
	 * @throws SystemException
	 * @throws CommunicationException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 */
	public void unsubscribeCurrentMarketByProductV5(int aClassKey, int aProductKey,
			EventChannelListener clientListener)
			throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
	
	/**
	 * Subscribe a client for a recap of a product.
	 * 
	 * @param aClassKey
	 * @param aProductKey
	 * @param clientListener
	 * @throws SystemException
	 * @throws CommunicationException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 */
	public void subscribeRecapLastSaleByProductV5(int aClassKey, int aProductKey,
			EventChannelListener clientListener)
		throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
	
	/**
	 * Unsubscribe a client for a recap of a product.
	 * 
	 * @param aClassKey
	 * @param aProductKey
	 * @param clientListener
	 * @throws SystemException
	 * @throws CommunicationException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 */
	public void unsubscribeRecapLastSaleByProductV5(int aClassKey, int aProductKey,
			EventChannelListener clientListener)
		throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
	
	/**
	 * Subscribe a client for NBBO for a product.
	 * 
	 * @param aClassKey
	 * @param aProductKey
	 * @param clientListener
	 * @throws SystemException
	 * @throws CommunicationException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 */
	public void subscribeNBBOByProductV5(String session, int aClassKey, int aProductKey,
			EventChannelListener clientListener)
		throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

	/**
	 * Unsubscribe a client for a NBBO for a product.
	 * 
	 * @param aClassKey
	 * @param aProductKey
	 * @param clientListener
	 * @throws SystemException
	 * @throws CommunicationException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 */
	public void unsubscribeNBBOByProductV5(String session, int aClassKey, int aProductKey,
			EventChannelListener clientListener)
		throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
	
	/**
     * Subcribe a client to the Ticket by product.
     * 
     * @param classKey
     * @param productKey
     * @param clientListener
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     */
    public void subscribeTickerByProductV5(int classKey, int productKey, EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    
    /**
     * Unsubcribe a client to the Ticket by product.
     * 
     * @param classKey
     * @param productKey
     * @param clientListener
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     */
    public void unsubscribeTickerByProductV5(int classKey, int productKey, EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    	
}
