//
// -----------------------------------------------------------------------------------
// Source file: BulkActionRequestImpl.java
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
import com.cboe.interfaces.domain.groupService.BulkActionRequest;

import java.lang.reflect.Field;
import java.util.Vector;

/**
 * A persistent implementation of <code>BulkActionRequest</code>.
 *
 * @author Cherian Mathew
 */
public class BulkActionRequestImpl extends PersistentBObject implements BulkActionRequest
{
    /**
     * Table name used for object mapping.
     */
    public static final String TABLE_NAME = "bulk_action_request";

    /**
     * The user id (login) of the help desk user who invoked the request
     */
    private String userId = null;

    /**
     * The type of action that was requested
     */
    private String actionRequestType = null;

    /**
     * The unique identifier for the request, as defined by the SAGUI (used to identify this request)
     */
    private String transactionId = null;

    /**
     * The name to identify the trade server in which the action was executed (in the case of
     * the HDE project canceling the order).
     */
    private String serverName = null;

    /**
     * The additional details of the request
     */
    private String optionalText = null;

    /**
     * The timestamp of the request being invoked from the SAGUI
     */
    private long requestTime = -1;

    /**
     * Fields for JavaGrinder
     */
    private static Field _userId;
    private static Field _actionRequestType;
    private static Field _transactionId;
    private static Field _serverName;
    private static Field _optionalText;
    private static Field _requestTime;

    /*
	 * JavaGrinder descriptor attribute.
	 */
    private static Vector classDescriptor;

    /**
     * Creates a new <code>BulkActionRequest</code> instance
     */
    public BulkActionRequestImpl()
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
            _userId = BulkActionRequestImpl.class.getDeclaredField("userId");
            _actionRequestType = BulkActionRequestImpl.class.getDeclaredField("actionRequestType");
            _transactionId = BulkActionRequestImpl.class.getDeclaredField("transactionId");
            _serverName = BulkActionRequestImpl.class.getDeclaredField("serverName");
            _optionalText = BulkActionRequestImpl.class.getDeclaredField("optionalText");
            _requestTime = BulkActionRequestImpl.class.getDeclaredField("requestTime");

            _userId.setAccessible(true);
            _actionRequestType.setAccessible(true);
            _transactionId.setAccessible(true);
            _serverName.setAccessible(true);
            _optionalText.setAccessible(true);
            _requestTime.setAccessible(true);
        }
        catch (NoSuchFieldException nsfe)
        {
            Log.exception("Unable to create field defintions for bulk action request", nsfe);
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

        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("userId", _userId));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("actionRequestType", _actionRequestType));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("transactionId", _transactionId));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("serverName", _serverName));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("optionalText", _optionalText));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("requestTime", _requestTime));

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
     * @see BulkActionRequest#getRequestDbId()
     */
    public long getRequestDbId()
    {
        return getObjectIdentifierAsLong();
    }

    /**
     * @see BulkActionRequest#getUserId()
     */
    public String getUserId()
    {
        return editor.get(_userId, userId);
    }

    /**
     * @see BulkActionRequest#setUserId(String)
     */
    public void setUserId(String newUserId)
    {
        editor.set(_userId, newUserId, userId);
    }

    /**
     * @see BulkActionRequest#getActionRequestType()
     */
    public String getActionRequestType()
    {
        return editor.get(_actionRequestType, actionRequestType);
    }

    /**
     * @see BulkActionRequest#setActionRequestType(String)
     */
    public void setActionRequestType(String newActionRequestType)
    {
        editor.set(_actionRequestType, newActionRequestType, actionRequestType);
    }

    /**
     * @see BulkActionRequest#getTransactionId()
     */
    public String getTransactionId()
    {
        return editor.get(_transactionId, transactionId);
    }

    /**
     * @see BulkActionRequest#setTransactionId(String)
     */
    public void setTransactionId(String newTransactionId)
    {
        editor.set(_transactionId, newTransactionId, transactionId);
    }

    /**
     * @see BulkActionRequest#getServerName()
     */
    public String getServerName()
    {
        return editor.get(_serverName, serverName);
    }

    /**
     * @see BulkActionRequest#setServerName(String)
     */
    public void setServerName(String newServerName)
    {
        editor.set(_serverName, newServerName, serverName);
    }

    /**
     * @see BulkActionRequest#getOptionalText()
     */
    public String getOptionalText()
    {
        return editor.get(_optionalText, optionalText);
    }

    /**
     * @see BulkActionRequest#setOptionalText(String)
     */
    public void setOptionalText(String newOptionalText)
    {
        editor.set(_optionalText, newOptionalText, optionalText);
    }

    /**
     * @see BulkActionRequest#getRequestTime()
     */
    public long getRequestTime()
    {
        return editor.get(_requestTime, requestTime);
    }

    /**
     * @see BulkActionRequest#setRequestTime(long)
     */
    public void setRequestTime(long newRequestTime)
    {
        editor.set(_requestTime, newRequestTime, requestTime);
    }

    /**
     * @return Returns a <code>java.lang.String</code> of all the attribute name and values
     */
    public String toString()
    {
        return new StringBuilder("userId=").append(userId).append(" transactionId=").append(transactionId)
                .append(" actionRequestType=").append(actionRequestType).append(" serverName=").append(serverName)
                .append(" optionalText=").append(optionalText)
                .append(" requestTime=").append(requestTime).toString();
    }
}
