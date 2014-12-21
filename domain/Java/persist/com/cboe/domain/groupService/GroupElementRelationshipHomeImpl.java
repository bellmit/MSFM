//
// -----------------------------------------------------------------------------------
// Source file: GroupElementRelationshipHomeImpl.java
//
// PACKAGE: com.cboe.domain.groupService
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.groupService;

import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiErrorCodes.NotFoundCodes;
import com.cboe.idl.cmiErrorCodes.SystemCodes;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.NoSuchObjectException;
import com.cboe.infrastructureServices.persistenceService.ObjectQuery;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import com.cboe.interfaces.domain.groupService.GroupElementRelationship;
import com.cboe.interfaces.domain.groupService.GroupElementRelationshipHome;
import com.cboe.util.ExceptionBuilder;

import java.util.List;

/**
 * A home implementation of the GroupElementRelationshipHome interface
 *
 * @author Antony Jesuraj
 * @author Cherian Mathew - Refactoring and documentation
 */
public class GroupElementRelationshipHomeImpl extends BOHome implements GroupElementRelationshipHome
{
    /**
     * Creates a new instance
     */
    public GroupElementRelationshipHomeImpl()
    {
        super();
    }

    /**
     * @see GroupElementRelationshipHome#create
     */
    public GroupElementRelationship create(long groupElementKey, long childElementKey) throws SystemException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, new StringBuilder(getThreadId())
                    .append(" <<GroupElementRelationshipHomeImpl>> create() started").toString());
        }

        Log.information(this, new StringBuilder(" groupElementKey ").append(groupElementKey)
                .append(" childElementKey ").append(childElementKey).toString());

        GroupElementRelationshipImpl groupElementRelationshipImpl = new GroupElementRelationshipImpl();
        addToContainer(groupElementRelationshipImpl);

        groupElementRelationshipImpl.initializeObjectIdentifier();
        if (Log.isDebugOn())
        {
            Log.debug(this, new StringBuilder(" <<GroupElementRelationshipHomeImpl.create>> ")
                    .append(" OID for new entry :: ")
                    .append(groupElementRelationshipImpl.getObjectIdentifierAsLong()).toString());
        }

        groupElementRelationshipImpl.setGroupElementKey(groupElementKey);
        groupElementRelationshipImpl.setChildElementKey(childElementKey);

        if (Log.isDebugOn())
        {
            Log.debug(this, new StringBuilder(getThreadId())
                    .append(" <<GroupElementRelationshipHomeImpl>> create() completed").toString());
        }
        return groupElementRelationshipImpl;
    }

    /**
     * @see GroupElementRelationshipHome#findElementsForGroup
     */
    public List findElementsForGroup(long groupElementKey) throws SystemException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, new StringBuilder(getThreadId())
                    .append(" <<GroupElementRelationshipHomeImpl>> findElementsForGroup() started").toString());
        }

        GroupElementRelationshipImpl groupElementRelationship = new GroupElementRelationshipImpl();
        addToContainer(groupElementRelationship);

        ObjectQuery query = new ObjectQuery(groupElementRelationship);
        groupElementRelationship.setGroupElementKey(groupElementKey);

        List results = null;
        try
        {
            results = query.find();
        }
        catch (PersistenceException pe)
        {
            String message = new StringBuilder(getThreadId())
                    .append(" <<GroupElementRelationshipHomeImpl.findElementsForGroup>> ")
                    .append("Query for group relationships failed").toString();
            SystemException se = ExceptionBuilder.systemException(message, SystemCodes.PERSISTENCE_FAILURE);
            se.initCause(pe);
            throw se;
        }
        if (Log.isDebugOn())
        {
            Log.debug(this, new StringBuilder(getThreadId())
                    .append(" <<GroupElementRelationshipHomeImpl>> findElementsForGroup() completed").toString());
        }
        return results;
    }

    /**
     * @see GroupElementRelationshipHome#removeElementFromGroup
     */
    public void removeElementFromGroup(long groupElementKey, long elementKey) throws NotFoundException, SystemException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, new StringBuilder(getThreadId())
                    .append(" <<GroupElementRelationshipHomeImpl>> removeElementFromGroup() started").toString());
        }

        GroupElementRelationshipImpl groupElementRelationship = new GroupElementRelationshipImpl();
        addToContainer(groupElementRelationship);

        ObjectQuery query = new ObjectQuery(groupElementRelationship);
        groupElementRelationship.setGroupElementKey(groupElementKey);
        groupElementRelationship.setChildElementKey(elementKey);

        try
        {
            GroupElementRelationshipImpl relationship = (GroupElementRelationshipImpl) query.findUnique();
            relationship.markForDelete();
        }
        catch (NoSuchObjectException nsoe)
        {
            String message = "<<GroupElementRelationshipHomeImpl.remove>> Element relationship not found";
            NotFoundException nfe = ExceptionBuilder.notFoundException(message, NotFoundCodes.RESOURCE_DOESNT_EXIST);
            nfe.initCause(nsoe);
            throw nfe;
        }
        catch (PersistenceException pe)
        {
            String message = "<<GroupElementRelationshipHomeImpl.remove>> Remove relationship failed to execute";
            SystemException se = ExceptionBuilder.systemException(message, SystemCodes.PERSISTENCE_FAILURE);
            se.initCause(pe);
            throw se;
        }

        if (Log.isDebugOn())
        {
            Log.debug(this, new StringBuilder(getThreadId())
                    .append(" <<GroupElementRelationshipHomeImpl>> removeElementFromGroup() completed").toString());
        }
    }

    /**
     * @return Returns the ID of the current thread in execution
     */
    public static String getThreadId()
    {
        return new StringBuilder("Thread ID ::: ").append(Thread.currentThread().getId()).append(" ").toString();
    }
}
