//
// -----------------------------------------------------------------------------------
// Source file: BulkActionDtlHomeImpl.java
//
// PACKAGE: com.cboe.domain.groupService
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.groupService;

import com.cboe.exceptions.SystemException;
import com.cboe.idl.constants.FederatedOperationType;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.groupService.BulkActionDtl;
import com.cboe.interfaces.domain.groupService.BulkActionDtlHome;

/**
 * An implementation of <code>BulkActionDtlHome</code> that manages persistent bulk action
 * history <code>BulkActionDtl</code> for a bulk action request <code>BulkActionRequest</code>
 * using JavaGrinder O-R mapping.
 *
 * @author Cherian Mathew
 */
public class BulkActionDtlHomeImpl extends BOHome implements BulkActionDtlHome
{
    /**
     * Creates a new persistent bulk action history
     */
    public BulkActionDtlHomeImpl()
    {
        super();
    }

    /**
     *
     * @see BulkActionDtlHome#create(long, long, String)
     */
    public BulkActionDtl create(long requestDbId, long targetDbId, String resultCode) throws SystemException
    {
        BulkActionDtlImpl bulkActionDtl = new BulkActionDtlImpl();
        addToContainer(bulkActionDtl);
        bulkActionDtl.initializeObjectIdentifier();
        bulkActionDtl.setRequestDbId(requestDbId);
        bulkActionDtl.setTargetDbId(targetDbId);
        bulkActionDtl.setResultCode(resultCode);
        /*
        Log.information(this, new StringBuilder("Bulk Action Detail created with info requestDbId=")
                .append(requestDbId).append(" targetDbId=").append(targetDbId)
                .append(" resultCode=").append(resultCode).toString());
                */

        return bulkActionDtl;
    }

    /**
    *
    * @see BulkActionDtlHome#createTradeBust(long, long, String)
    */
    public BulkActionDtl create(long requestDbId, long tradeId, String resultCode, short operationType)
            throws SystemException {
        BulkActionDtlImpl bulkActionDtl = new BulkActionDtlImpl();
        addToContainer(bulkActionDtl);
        bulkActionDtl.initializeObjectIdentifier();
        bulkActionDtl.setRequestDbId(requestDbId);
        
        switch (operationType) {
        	case FederatedOperationType.ALL_ORDERS:
        	case FederatedOperationType.ORDERS:
        	case FederatedOperationType.IORDERS:
        					bulkActionDtl.setTargetDbId(tradeId);
        					break;
        	case FederatedOperationType.TRADEBUST:
	        				bulkActionDtl.setTradeId(tradeId);
	        				break;
	        default:
	        		Log.alarm("The operationType specified for persisting BulkAction_Dtl is incorrect");
        }
        
        bulkActionDtl.setResultCode(resultCode);
        /*
        Log.information(this, new StringBuilder("Bulk Action Detail created with info requestDbId=")
                .append(requestDbId).append(" referenceDbId=").append(tradeId)
                .append(" resultCode=").append(resultCode).toString());
                */

        return bulkActionDtl;
    }
}
