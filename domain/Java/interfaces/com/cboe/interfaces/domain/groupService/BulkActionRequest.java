//
// -----------------------------------------------------------------------------------
// Source file: BulkActionRequest.java
//
// PACKAGE: com.cboe.interfaces.domain.groupService
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.groupService;

/**
 * Tracks a cancel request. In addition, tracks requests that impact multiple orders beyond
 * just supporting group cancels.
 *
 * @author Cherian Mathew
 */
public interface BulkActionRequest
{
    /**
     * @return Returns the unique identifier of the bulk action request
     */
    public long getRequestDbId();

    /**
     * @return Returns the type of action that was  requested
     */
    public String getActionRequestType();

    /**
     * Sets the type of action being requested
     *
     * @param newActionRequestType - The type of action being requested
     */
    public void setActionRequestType(String newActionRequestType);

    /**
     * @return Returns the user id (login) of the help desk user who invoked the request
     */
    public String getUserId();

    /**
     * Sets the user id (login) of the help desk user invoking the request
     *
     * @param newUserId - The user id (login) of the help desk user invoking the request
     */
    public void setUserId(String newUserId);

    /**
     * @return Returns the unique identifier for the request, as defined by the
     *         SAGUI (used to identify this request)
     */
    public String getTransactionId();

    /**
     * Sets the unique identifier for the request, as defined by the SAGUI (used to identify this request)
     *
     * @param newTransactionId - The unique identifier for the request, as defined by the
     *                           SAGUI (used to identify this request)
     */
    public void setTransactionId(String newTransactionId);

    /**
     * @return Returns the name to identify the trade server in which the action was
     *        executed (in the case of the HDE project canceling the order).
     */
    public String getServerName();

    /**
     * Sets the name to identify the trade server in which the action was executed (in the case of the
     * HDE project canceling the order).
     *
     * @param newServerName - The name to identify the trade server in which the action was
     *                        executed (in the case of the HDE project canceling the order).
     */
    public void setServerName(String newServerName);

    /**
     * @return Returns the additional details of the request
     */
    public String getOptionalText();

    /**
     * Sets the additional details of the request
     *
     * @param newOptionalText - The additional details of the request
     */
    public void setOptionalText(String newOptionalText);

    /**
     * @return Returns the timestamp of the request being invoked from the SAGUI
     */
    public long getRequestTime();

    /**
     * Sets the timestamp of the request being invoked from the SAGUI
     *
     * @param newRequestTime - The timestamp of the request being invoked from the SAGUI
     */
    public void setRequestTime(long newRequestTime);
}
