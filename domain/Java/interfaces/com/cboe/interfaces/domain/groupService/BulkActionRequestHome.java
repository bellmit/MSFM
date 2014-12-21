//
// -----------------------------------------------------------------------------------
// Source file: BulkActionRequestHome.java
//
// PACKAGE: com.cboe.interfaces.domain.groupService
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.groupService;

import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

/**
 * A home interface type to create a bulk action request
 *
 * @author Cherian Mathew
 */
public interface BulkActionRequestHome
{
    /**
     * Name that will be used for the home
     */
    public static final String HOME_NAME = "BulkActionRequestHome";

    /**
     * Creates a new bulk action request
     *
     * @param actionRequestType - The type of action being requested.
     * @param userId            - The user id (login) of the help desk user invoking the request.
     * @param transactionId     - The unique identifier for the request, as defined by the SAGUI (used to identify this request).
     * @param serverName        - A name to identify the trade server executing the action (in the case of the HDE project canceling the order).
     *                            For future use (when the BC order databases are consolidated).
     * @param optionalText      - An arbitrary, human-readable string providing additional details for the request.  It should not
     *                            be more than 100 chars.
     * @param requestTime       - The timestamp "millis since 1/1/70 GMT" of the request being invoked from the SAGUI.
     * @return Returns a handle to the new bulk action request.
     * @throws SystemException  - If the create fails.
     */
    public BulkActionRequest create(String actionRequestType, String userId, String transactionId, String serverName, String optionalText, long requestTime)
            throws SystemException;
    
    public BulkActionRequest findByTransactionId(String userId, String transactionId) throws NotFoundException, TransactionFailedException;
}
