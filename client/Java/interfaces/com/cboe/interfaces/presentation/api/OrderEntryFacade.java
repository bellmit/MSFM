package com.cboe.interfaces.presentation.api;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiOrder.LightOrderEntryStruct;
import com.cboe.idl.cmiOrder.LightOrderResultStruct;

/**
 * This is a facade for all methods of the Order Entry API. Any additional
 * methods in the future can be added to this interface.
 * 
 * @author morrow
 * 
 */
public interface OrderEntryFacade extends OrderEntryV7API
{
	/**
	 * Creates a new light order in CBOEDirect.
	 * 
	 * @param struct
	 *            order entry struct
	 * @return result struct
	 * @throws SystemException
	 * @throws CommunicationException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 * @throws NotAcceptedException
	 * @throws TransactionFailedException
	 * @throws AlreadyExistsException
	 */
	LightOrderResultStruct acceptLightOrder(LightOrderEntryStruct struct) throws SystemException, CommunicationException, AuthorizationException,
	        DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException;

	/**
	 * Cancels an existing light order by branch sequence id in CBOEDirect.
	 * 
	 * @param branch
	 * @param sequenceNumber
	 * @param productKey
	 * @param session
	 * @param userAssignedCancelId
	 * @return result struct
	 * @throws SystemException
	 * @throws CommunicationException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 * @throws NotAcceptedException
	 * @throws TransactionFailedException
	 */
	LightOrderResultStruct acceptLightOrderCancelRequest(String branch, int sequenceNumber, int productKey, String session,
	        String userAssignedCancelId) throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
	        NotAcceptedException, TransactionFailedException;

	/**
	 * Cancels an existing light order by high low id in CBOEDirect.
	 * 
	 * @param orderHighId
	 * @param orderLowId
	 * @param productKey
	 * @param session
	 * @param userAssignedCancelId
	 * @return result struct
	 * @throws SystemException
	 * @throws CommunicationException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 * @throws NotAcceptedException
	 * @throws TransactionFailedException
	 */
	LightOrderResultStruct acceptLightOrderCancelRequestById(int orderHighId, int orderLowId, int productKey, String session,
	        String userAssignedCancelId) throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
	        NotAcceptedException, TransactionFailedException;

}
