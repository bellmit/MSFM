package com.cboe.interfaces.domain;

import com.cboe.exceptions.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.interfaces.domain.Order;

/**
 *  Define what the external interface to an AgentAssignedOrderHome is, and also implements
 *  the singleton pattern to access the configured type of AgentAssignedOrderHome.
 */
public interface AgentAssignedOrderHome
{
	public final static String HOME_NAME = "AgentAssignedOrderHome";
    
    /**
     * Finds all held orders for a class.  Processed orders will not be returned
     * @param classKey the class key 
     * @return all assigned orders for a class.  It could be an empty array if none found.
     */    
    public AgentAssignedOrder[] findHeldOrdersForClass(int classKey) throws TransactionFailedException;
    /**
     * Finds all held orders cancel requests and cancel replace requests for a class.  
     * Processed requests will not be returned
     * @param classKey the class key 
     * @return all assigned orders for a class. It could be an empty array if none found.
     */    
    public AgentAssignedOrder[] findHeldCancelRequestsForClass(int classKey) throws TransactionFailedException;

    /**
     * Finds the assigned order based on the order key.  Only HELD one will be returned.  NotFoundException
     * will be thrown if has been PROCESSED
     * @param orderKey the assigned order key.
     * @return the assigned held order for the key.  Null will be returned if not found.
     */    
    public AgentAssignedOrder findAssignedHeldOrder(long orderKey) 
        throws TransactionFailedException, NotFoundException, DataValidationException;

    /**
     * Finds the assigned order based on the assigned order database key.  
     * All type regardless if processed or not will
     * be returned.
     * @param assignedOrderKey the assigned order database key.
     * @return the assigned order for the key.  Null will be returned if not found.
     */        
    public AgentAssignedOrder findAgentAssignedOrder(long assignedOrderKey) 
    throws TransactionFailedException, NotFoundException, DataValidationException;
    
    /**
     * Finds the assigned order based on the CboeId.  All status will be returned
     * regardless if has been processed.
     * @param cboeIdStruct the assigned order cboe id key.
     * @return the assigned order for the key.  Null will be returned if not found.
     */         
    public AgentAssignedOrder findCancelRequest(CboeIdStruct cboeIdStruct)    
    throws TransactionFailedException, NotFoundException, DataValidationException;
    
    /**
     * Finds the assigned order cancel or cancel replace requests based on the order key if has not processed
     * @param orderKey the held order key.
     * @return list of cancel or cancel requests. It could be an empty array if none found.
     */    
    public AgentAssignedOrder[] findCancelRequests(long orderKey) throws TransactionFailedException;
     
    /**
     * Creates the assigned order based on the order.
     * @param anOrder the order.
     * @return the assigned order just created.  
     */     
    public AgentAssignedOrder createAssignedHeldOrder(Order anOrder) 
        throws TransactionFailedException, SystemException, AlreadyExistsException, DataValidationException;

    /**
     * Creates the assigned order based on the order's cancel request.
     * @param anOrder the order.
     * @param cancelQuantity the quantity
     * @parma cancelType the cancel type
     * @param userAssignedCancelId the user assigned cancel request id.
     * @return the assigned order just created
     */     
    public AgentAssignedOrder createAssignedCancelRequest(Order anOrder,int cancelQuantity, short cancelType, String userAssignedCancelId)
        throws TransactionFailedException, SystemException, DataValidationException;

    /**
     * Creates the assigned order based on the order's cancel replace request.
     * @param anOrder the order.
     * @param cancelQuantity the quantity
     * @parma cancelType the cancel type
     * @param userAssignedCancelId the user assigned cancel request id.
     * @param replacementOrder the replacement order.
     * @return the assigned order just created
     */     
    public AgentAssignedOrder createAssignedCancelReplace(Order anOrder, 
                                                         int cancelQuantity, 
                                                         short cancelType, 
                                                         String userAssignedCancelId, 
                                                         Order replacementOrder)
                throws TransactionFailedException, SystemException, DataValidationException;

    /**
     * Updates the assigned order on status.
     * @param assignedOrder the assigned order.
     * @param status the status to be updated to.
     * @return none
     */     
    public AgentAssignedOrder updateStatus(AgentAssignedOrder assignedOrder, char status)
        throws TransactionFailedException, DataValidationException, NotFoundException;
    
    /**
     * Get the last actitvity time for the order.  
     */
    public long getLastActivityTimeForOrder(long orderKey) throws TransactionFailedException, NotFoundException;
 
    /**
     * Gets all the agent assigned held order entries(not include the cancels)
     * Processed will not be included  
     */
    public AgentAssignedOrder[] findAllAgentAssignedHeldOrders() throws TransactionFailedException;
    
    /**
     * Gets all the agent assigned cancel order entries, processed will not be included  
     */
    public AgentAssignedOrder[] findAllAgentAssignedCancelRequests() throws TransactionFailedException;
    
     /**
     * Delete all assigned orders that are in processed state and exceed the retention period.
     *
      * @exception com.cboe.exceptions.UpdateFailedException
     */
    public abstract void deleteAllProcessed() throws UpdateFailedException;
    /**
     * Delete an assigned order from Home.
     * @exception com.cboe.exceptions.UpdateFailedException
     */
    public abstract void delete(AgentAssignedOrder anOrder) throws UpdateFailedException;
}
