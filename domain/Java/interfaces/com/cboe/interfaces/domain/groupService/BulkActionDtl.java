//
// -----------------------------------------------------------------------------------
// Source file: BulkActionDtl.java
//
// PACKAGE: com.cboe.interfaces.domain.groupService
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.groupService;

/**
 * A bulk action history. Associates cancel requests <code>BulkActionRequest</code> with orders, and
 * to record the orders for which the requested action was not able to complete.
 *
 * @author Cherian Mathew
 */
public interface BulkActionDtl
{
    /**
     * @return Returns the unique identifier of the bulk action history
     */
    public long getUniqueDbId();

    /**
     * @return Returns the unique identifier of the bulk action request
     */
    public long getRequestDbId();

    /**
     * Sets the unique identifier of the bulk action request
     *
     * @param newRequestDbId - The unique identifier of the bulk action request
     */
    public void setRequestDbId(long newRequestDbId);

    /**
     * @return Returns the unique identifier of the object on which the action was performed
     */
    public long getTargetDbId();

    /**
     * Sets the unique identifier of the object on which the action was performed
     *
     * @param newTargetDbId - The unique identifier of the object on which the action was performed
     */
    public void setTargetDbId(long newTargetDbId);

    /**
     * @return Returns the results code (i.e., C=cancelled, R=routed to floor(TPF/PAR), X=error cancelling)
     *         of the bulk action
     */
    public String getResultCode();

    /**
     * Sets the result code (i.e., C=cancelled, R=routed to floor(TPF/PAR), X=error cancelling)
     * of the bulk action
     *
     * @param newResultCode - The bulk action result code
     */
    public void setResultCode(String newResultCode);
    
    /**
     * @return Returns tradeId which will be blank in case of order cancellation and trade Id in case of
     * Trade Bust.
     * tradeId is the new field added for Audit Logging functionality.
     */
    public long getTradeId();
    
    /**
     * Sets the value as blank in case of order cancellation and trade Id in case of Trade Bust 
     * Audit Log Functionality.
     *
     * @param newTradeId - tradeId which will be blank in case of order cancellation and trade Id in case of
     * Trade Bust.
     */
    public void setTradeId(long newTradeId);
}
