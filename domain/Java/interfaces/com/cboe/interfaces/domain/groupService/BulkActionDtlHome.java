//
// -----------------------------------------------------------------------------------
// Source file: BulkActionDtlHome.java
//
// PACKAGE: com.cboe.interfaces.domain.groupService
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.groupService;

import com.cboe.exceptions.SystemException;

/**
 * A home interface type to create the bulk action history of a bulk action
 * request <code>BulkActionRequest</code>
 *
 * @author Cherian Mathew
 */
public interface BulkActionDtlHome
{
    /**
     * Name that will be used for the home
     */
    public static final String HOME_NAME = "BulkActionDtlHome";

    /**
     * Creates a bulk action history for a bulk action request
     *
     * @param requestDbId - The unique identifier of the bulk action request
     * @param targetDbId  - The unique identifier of the order
     * @param resultCode  - An indicator of the result of the action (in the case of the group cancel action it would
     *                    be cancel: C=cancelled, R=routed to floor (TPF/PAR),  X=error)
     * @return Returns a handle to the new bulk action history
     * @throws SystemException - If the create fails
     */
    public BulkActionDtl create(long requestDbId, long targetDbId, String resultCode) throws SystemException;
    
    /**
     * Creates a bulk action history for Audit Logging Functionality
     *
     * @param requestDbId - The unique identifier of the bulk action request
     * @param tradeId     - tradeId passed by Audit Logging functionality which will be inserted to new field TRADEID in bulk_action_dtl Table. 
     * @param resultCode  - An indicator of the result of the action (in the case of the group cancel action it would
     *                    be cancel: C=Canceled, R=routed to floor (TPF/PAR),  X=error)
     * @param operationType - The operation type (ORDER_CANCEL | TRADEBUST) as defined in FederatedOperationType  
     * @return Returns a handle to the new bulk action history
     * @throws SystemException - If the create fails
     */
    public BulkActionDtl create(long requestDbId, long tradeId, String resultCode, short operationType) throws SystemException;
}
