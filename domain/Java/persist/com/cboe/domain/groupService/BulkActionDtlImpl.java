//
// -----------------------------------------------------------------------------------
// Source file: BulkActionDtlImpl.java
//
// PACKAGE: com.cboe.domain.groupService
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.groupService;

import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.AttributeDefinition;
import com.cboe.infrastructureServices.persistenceService.DBAdapter;
import com.cboe.infrastructureServices.persistenceService.ObjectChangesIF;
import com.cboe.interfaces.domain.groupService.BulkActionDtl;

import java.lang.reflect.Field;
import java.util.Vector;

/**
 * A persistent implementation of <code>BulkActionDtl</code>.
 *
 * @author Cherian Mathew
 */
public class BulkActionDtlImpl extends PersistentBObject implements BulkActionDtl
{
    /**
     * Table name used for object mapping.
     */
    public static final String TABLE_NAME = "bulk_action_dtl";

    /**
     * The unique identifier of the order cancel request
     */
    private long requestDbId;

    /**
     * The unique identifier of the order that was cancelled
     */
    private long targetDbId;
    
    /**
     * For order cancellation it is blank and for Trade Bust functionality it is passed trade Id
     */
    private long tradeId;

    /**
     * The cancel results code (i.e., C=cancelled, R=routed to floor(TPF/PAR), X=error cancelling)
     */
    private String resultCode;

    /**
     * Fields for JavaGrinder
     */
    private static Field _requestDbId;
    private static Field _targetDbId;
    private static Field _resultCode;
    private static Field _tradeId;

    /*
	 * JavaGrinder descriptor attribute.
	 */
    private static Vector classDescriptor;

    /**
     * Creates a new <code>BulkActionDtl</code> instance
     */
    public BulkActionDtlImpl()
    {
        super();
    }

    /**
     * This static block will be regenerated if persistence is regenerated.
     */
    static
    {
        try
        {
            _requestDbId = BulkActionDtlImpl.class.getDeclaredField("requestDbId");
            _targetDbId = BulkActionDtlImpl.class.getDeclaredField("targetDbId");
            _tradeId = BulkActionDtlImpl.class.getDeclaredField("tradeId");
            _resultCode = BulkActionDtlImpl.class.getDeclaredField("resultCode");

            _requestDbId.setAccessible(true);
            _targetDbId.setAccessible(true);
            _tradeId.setAccessible(true);
            _resultCode.setAccessible(true);
        }
        catch (NoSuchFieldException nsfe)
        {
            Log.exception("Unable to create field defintions for bulk action dtl", nsfe);
        }
    }

    /**
     * Creates JavaGrinder descriptor for the database record of this object.
     */
    public void initDescriptor()
    {
        if (classDescriptor != null)
        {
            return;
        }

        Vector tempDescriptor = getSuperDescriptor();

        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("requestDbId", _requestDbId));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("targetDbId", _targetDbId));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("tradeId", _tradeId));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("resultCode", _resultCode));

        classDescriptor = tempDescriptor;
    }

    /**
     * Creates JavaGrinder editor for this object.
     */
    public ObjectChangesIF initializeObjectEditor()
    {
        final DBAdapter result = (DBAdapter) super.initializeObjectEditor();
        if (classDescriptor == null)
        {
            initDescriptor();
        }
        result.setTableName(TABLE_NAME);
        result.setClassDescription(classDescriptor);
        return result;
    }

    /**
     * @see BulkActionDtl#getUniqueDbId()
     */
    public long getUniqueDbId()
    {
        return getObjectIdentifierAsLong();
    }

    /**
     * @see BulkActionDtl#getRequestDbId()
     */
    public long getRequestDbId()
    {
        return editor.get(_requestDbId, requestDbId);
    }

    /**
     * @see BulkActionDtl#setRequestDbId(long)
     */
    public void setRequestDbId(long newRequestDbId)
    {
        editor.set(_requestDbId, newRequestDbId, requestDbId);
    }

    /**
     * @see BulkActionDtl#getTargetDbId()
     */
    public long getTargetDbId()
    {
        return editor.get(_targetDbId, targetDbId);
    }

    /**
     * @see BulkActionDtl#setTargetDbId(long)
     */
    public void setTargetDbId(long newTargetDbId)
    {
        editor.set(_targetDbId, newTargetDbId, targetDbId);
    }

    /**
     * @see BulkActionDtl#getResultCode()
     */
    public String getResultCode()
    {
        return editor.get(_resultCode, resultCode);
    }

    /**
     * @see BulkActionDtl#setResultCode(String)
     */
    public void setResultCode(String newResultCode)
    {
        editor.set(_resultCode, newResultCode, resultCode);
    }

    /**
     * @see BulkActionDtl#getTradeId()
     */
    public long getTradeId()
    {
        return editor.get(_tradeId, tradeId);
    }

    /**
     * @see BulkActionDtl#setTradeId(long)
     */
    public void setTradeId(long newTradeId)
    {
        editor.set(_tradeId, newTradeId, tradeId);
    }
    /**
     * @return Returns a <code>java.lang.String</code> of all the attribute name and values
     */
    public String toString()
    {
        return new StringBuilder("requestDbId =").append(requestDbId).append(" targetDbId =").append(targetDbId)
                .append(" tradeId =").append(tradeId).append(" resultCode =").append(resultCode).toString();
    }
}
