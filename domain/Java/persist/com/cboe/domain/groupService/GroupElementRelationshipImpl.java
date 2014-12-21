//
// -----------------------------------------------------------------------------------
// Source file: GroupElementRelationshipImpl.java
//
// PACKAGE: com.cboe.domain.groupService
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.groupService;


import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.AttributeDefinition;
import com.cboe.infrastructureServices.persistenceService.DBAdapter;
import com.cboe.infrastructureServices.persistenceService.ObjectChangesIF;
import com.cboe.interfaces.domain.groupService.GroupElementRelationship;

import java.lang.reflect.Field;
import java.util.Vector;

/**
 * A Domain layer implementation of the GroupElementRelationship interface
 *
 * @author Antony Jesuraj
 * @author Cherian Mathew - Refactoring and documentation
 */
public class GroupElementRelationshipImpl extends PersistentBObject implements GroupElementRelationship
{
    /**
     * Table name used for object mapping.
     */
    public static final String TABLE_NAME = "group_relationship";

    /**
     * The Key of the GroupElement
     */
    private long groupElementKey;

    /**
     * The Key of the ChildElement
     */
    private long childElementKey;

    /**
     * Fields for JavaGrinder
     */
    private static Field _groupElementKey;
    private static Field _childElementKey;

    /*
	 * JavaGrinder attribute descriptions.
	 */
    private static Vector classDescriptor;

    /**
     * This static block will be regenerated if persistence is regenerated.
     */
    static
    {
        try
        {
            _groupElementKey = GroupElementRelationshipImpl.class.getDeclaredField("groupElementKey");
            _childElementKey = GroupElementRelationshipImpl.class.getDeclaredField("childElementKey");

            _groupElementKey.setAccessible(true);
            _childElementKey.setAccessible(true);
        }
        catch (NoSuchFieldException ex)
        {
            Log.exception("Unable to create field defintions for GroupElementRelationship", ex);
        }
    }

    /**
     * Create an instance of home.
     */
    public GroupElementRelationshipImpl()
    {
        super();

    }

    /**
     * @return Returns the GroupElementKey
     */
    public long getGroupElementKey()
    {
        return editor.get(_groupElementKey, groupElementKey);
    }

    /**
     * @return Returns the ChildElementKey
     */
    public long getChildElementKey()
    {
        return editor.get(_childElementKey, childElementKey);
    }

    /**
     * Describe how this class relates to the relational database.
     */
    public void initDescriptor()
    {
        if (classDescriptor != null)
        {
            return;
        }

        Vector tempDescriptor = getSuperDescriptor();

        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("groupElementKey", _groupElementKey));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("childElementKey", _childElementKey));

        classDescriptor = tempDescriptor;
    }

    /**
     * Needed to define table name and the description of this class.
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
     * Sets the GroupElementKey
     *
     * @param aValue - The key to set
     */
    public void setGroupElementKey(long aValue)
    {
        editor.set(_groupElementKey, aValue, groupElementKey);
    }

    /**
     * Sets the ChildElementKey
     *
     * @param aValue - The key to set
     */
    public void setChildElementKey(long aValue)
    {
        editor.set(_childElementKey, aValue, childElementKey);
    }

    /**
     * Utility method for logging
     *
     * @return The log
     */
    public String toString()
    {
        return new StringBuilder("groupElementKey ").append(groupElementKey).append(" groupElementKey ")
                .append(groupElementKey).toString();
    }

    /**
     * @return Returns the ID of the current thread in execution
     */
    public static String getThreadId()
    {
        return new StringBuilder("Thread ID ::: ").append(Thread.currentThread().getId()).append(" ").toString();
    }
}
