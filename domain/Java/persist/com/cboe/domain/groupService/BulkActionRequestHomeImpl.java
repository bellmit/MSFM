//
// -----------------------------------------------------------------------------------
// Source file: BulkActionRequestHomeImpl.java
//
// PACKAGE: com.cboe.domain.groupService
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.groupService;

import java.util.Vector;

import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.ObjectQuery;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import com.cboe.interfaces.domain.groupService.BulkActionRequest;
import com.cboe.interfaces.domain.groupService.BulkActionRequestHome;
import com.cboe.util.ExceptionBuilder;
import com.cboe.domain.util.DateWrapper;

/**
 * An implementation of <code>BulkActionRequestHome</code> that manages persistent bulk action request
 * <code>BulkActionRequest</code> using JavaGrinder O-R mapping.
 *
 * @author Cherian Mathew
 */
public class BulkActionRequestHomeImpl extends BOHome implements BulkActionRequestHome
{
    /**
     * Creates a new persistent bulk action request
     */
    public BulkActionRequestHomeImpl()
    {
        super();
    }

    /**
     * @see BulkActionRequestHome#create(String, String, String, String, String, long)
     */
    public BulkActionRequest create(String actionRequestType, String userId, String transactionId, String serverName,
                                    String optionalText, long requestTime) throws SystemException
    {


        Log.information(this, new StringBuilder(" userId=")
                .append(userId).append(" actionRequestType=").append(actionRequestType).append(" transactionId=")
                .append(transactionId).append(" serverName=").append(serverName).append(" optionalText=")
                .append(optionalText).append(" requestTime=").append(requestTime).toString());

        BulkActionRequestImpl bulkActionRequest = new BulkActionRequestImpl();
        addToContainer(bulkActionRequest);
        bulkActionRequest.initializeObjectIdentifier();
        bulkActionRequest.setActionRequestType(actionRequestType);
        bulkActionRequest.setUserId(userId);
        bulkActionRequest.setTransactionId(transactionId);
        bulkActionRequest.setServerName(serverName);
        bulkActionRequest.setOptionalText(optionalText);
        bulkActionRequest.setRequestTime(new DateWrapper().getTimeInMillis());


        return bulkActionRequest;
    }

    public BulkActionRequest findByTransactionId(String userId, String p_transactionId) throws NotFoundException, TransactionFailedException
    {
        BulkActionRequestImpl subject = new BulkActionRequestImpl();
        addToContainer(subject);
        subject.setRequestTime(0);
        subject.setUserId(userId);
        subject.setTransactionId(p_transactionId);
        ObjectQuery query = new ObjectQuery(subject);
        Vector<BulkActionRequestImpl> resultVector = null;
        try
        {
            resultVector = query.find();
        }
        catch(PersistenceException e)
        {
            Log.exception(this, e);
            throw ExceptionBuilder.transactionFailedException(e + "", 0);
        }
        
        if (resultVector.size() == 0) {
            throw ExceptionBuilder.notFoundException("Not found BulkActionRequest.", 0);
        }
        
        BulkActionRequestImpl[] results = new BulkActionRequestImpl[resultVector.size()];
        int i = 0;
        for (BulkActionRequestImpl theActionRequest : resultVector) {
            results[i] = theActionRequest;
            addToContainer(results[i]);
            i++;
        }
        return results[0];
    }
}
