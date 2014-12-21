package com.cboe.domain.groupService;

import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiErrorCodes.NotFoundCodes;
import com.cboe.idl.cmiErrorCodes.SystemCodes;
import com.cboe.idl.groupElement.ElementEntryStruct;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.NoSuchObjectException;
import com.cboe.infrastructureServices.persistenceService.ObjectQuery;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import com.cboe.interfaces.domain.groupService.GroupElement;
import com.cboe.interfaces.domain.groupService.GroupElementHome;
import com.cboe.util.ExceptionBuilder;

import java.util.Collection;
import java.util.List;


/**
 * A Home implementation of the GroupElementHome interface
 *
 * @author Antony Jesuraj
 * @author Cherian Mathew - Refactoring and documentation
 */
public class GroupElementHomeImpl extends BOHome implements GroupElementHome
{
    /**
     * Creates a new instance
     */
    public GroupElementHomeImpl()
    {
        super();
    }

    /**
     * @see GroupElementHome#create
     */
    public GroupElement create(ElementEntryStruct struct) throws SystemException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this,
                      new StringBuilder(getThreadId()).append(" <<GroupElementHomeImpl>> create() started").toString());
        }

        Log.information(this, new StringBuilder(" elementname ")
                .append(struct.elementName).append(" elementDataType ").append(struct.elementDataType)
                .append(" elementDataKey ").append(struct.elementDataKey).append(" nodeType ")
                .append(struct.nodeType).append(" extensions ").append(struct.extensions).toString());

        GroupElementImpl newInstance = new GroupElementImpl();
        addToContainer(newInstance);

        newInstance.initializeObjectIdentifier();
        if (Log.isDebugOn())
        {
            Log.debug(this,
                      new StringBuilder(getThreadId()).append(" <<GroupElementHomeImpl.create>> OID for new entry :: ")
                              .append(newInstance.getObjectIdentifierAsLong()).toString());
        }

        newInstance.setElementName(struct.elementName);
        newInstance.setElementGroupType(struct.elementGroupType);
        newInstance.setElementDataType(struct.elementDataType);
        newInstance.setElementDataKey(struct.elementDataKey);
        newInstance.setNodeType(struct.nodeType);
        newInstance.setExtensions(struct.extensions);
        newInstance.setVersionId(1);

        // get the current time
        long currentTime = FoundationFramework.getInstance().getTimeService().getCurrentDateTime();

        newInstance.setCreatedTime(currentTime);
        newInstance.setLastModifiedTime(currentTime);

        if (Log.isDebugOn())
        {
            Log.debug(this, new StringBuilder(getThreadId())
                    .append(" <<GroupElementHomeImpl>> create() completed").toString());
        }
        return newInstance;
    }

    /**
     * @see GroupElementHome#findElementByKey
     */
    public GroupElement findElementByKey(long elementKey) throws NotFoundException, SystemException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, new StringBuilder(getThreadId())
                    .append(" <<GroupElementHomeImpl>> findElementByKey() started").toString());
        }

        GroupElementImpl queryElement = new GroupElementImpl();
        addToContainer(queryElement);

        ObjectQuery query = new ObjectQuery(queryElement);
        queryElement.setObjectIdentifierFromLong(elementKey);

        try
        {
            queryElement = (GroupElementImpl) query.findUnique();
        }
        catch (NoSuchObjectException nsoe)
        {
            String message = new StringBuilder("<<GroupElementHomeImpl.findElementByKey>> Element with key ")
                    .append(elementKey).append(" couldn't be found").toString();
            NotFoundException nfe = ExceptionBuilder.notFoundException(message, NotFoundCodes.RESOURCE_DOESNT_EXIST);
            nfe.initCause(nsoe);
            throw nfe;
        }
        catch (PersistenceException pe)
        {
            String message = new StringBuilder("<<GroupElementHomeImpl.findElementByKey>> Query for element with key ")
                    .append(elementKey).append(" failed to execute").toString();
            SystemException se = ExceptionBuilder.systemException(message, SystemCodes.PERSISTENCE_FAILURE);
            se.initCause(pe);
            throw se;
        }
        if (Log.isDebugOn())
        {
            Log.debug(this, new StringBuilder(getThreadId())
                    .append(" <<GroupElementHomeImpl>> findElementByKey() completed").toString());
        }
        return queryElement;
    }

    /**
     * @see GroupElementHome#findAllByDataType
     */
    public Collection findAllByDataType(short elementDataType) throws SystemException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, new StringBuilder(getThreadId())
                    .append(" <<GroupElementHomeImpl>> findAllByDataType() started").toString());
        }

        GroupElementImpl queryElement = new GroupElementImpl();
        addToContainer(queryElement);

        ObjectQuery query = new ObjectQuery(queryElement);
        queryElement.setElementDataType(elementDataType);

        List results = null;
        try
        {
            results = query.find();
        }
        catch (PersistenceException pe)
        {
            String message = new StringBuilder(
                    "<<GroupElementHomeImpl.findAllByDataType>> Query for element with data type ")
                    .append(elementDataType).append(" failed to execute").toString();
            SystemException se = ExceptionBuilder.systemException(message, SystemCodes.PERSISTENCE_FAILURE);
            se.initCause(pe);
            throw se;
        }
        if (Log.isDebugOn())
        {
            Log.debug(this, new StringBuilder(getThreadId())
                    .append(" <<GroupElementHomeImpl>> findAllByDataType() completed").toString());
        }
        return results;
    }

    /**
     * @see GroupElementHome#findAll
     */
    public Collection findAll() throws SystemException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, new StringBuilder(getThreadId())
                    .append(" <<GroupElementHomeImpl>> findAll() started").toString());
        }

        GroupElementImpl queryElement = new GroupElementImpl();
        addToContainer(queryElement);

        ObjectQuery query = new ObjectQuery(queryElement);
        List results = null;
        try
        {
            results = query.find();
        }
        catch (PersistenceException pe)
        {
            String message = "<<GroupElementHomeImpl.findAll>> Query for all elements failed to execute";
            SystemException se = ExceptionBuilder.systemException(message, SystemCodes.PERSISTENCE_FAILURE);
            se.initCause(pe);
            throw se;
        }

        if (Log.isDebugOn())
        {
            Log.debug(this, new StringBuilder(getThreadId())
                    .append(" <<GroupElementHomeImpl>> findAll() completed").toString());
        }
        return results;
    }

    /**
     * @see GroupElementHome#remove
     */
    public void remove(long elementKey) throws NotFoundException, SystemException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this,
                      new StringBuilder(getThreadId()).append(" <<GroupElementHomeImpl>> remove() started").toString());
        }

        GroupElementImpl groupElement = new GroupElementImpl();
        addToContainer(groupElement);

        groupElement.setObjectIdentifierFromLong(elementKey);
        ObjectQuery query = new ObjectQuery(groupElement);

        try
        {
            GroupElementImpl element = (GroupElementImpl) query.findUnique();
            element.markForDelete();
        }
        catch (NoSuchObjectException nsoe)
        {
            String message = new StringBuilder("<<GroupElementHomeImpl.remove>> Element with key ")
                    .append(elementKey).append(" couldn't be found").toString();
            NotFoundException nfe = ExceptionBuilder.notFoundException(message, NotFoundCodes.RESOURCE_DOESNT_EXIST);
            nfe.initCause(nsoe);
            throw nfe;
        }
        catch (PersistenceException pe)
        {
            String message = new StringBuilder("<<GroupElementHomeImpl.remove>> Remove for element with key ")
                    .append(elementKey).append(" failed to execute").toString();
            SystemException se = ExceptionBuilder.systemException(message, SystemCodes.PERSISTENCE_FAILURE);
            se.initCause(pe);
            throw se;
        }
        if (Log.isDebugOn())
        {
            Log.debug(this, new StringBuilder(getThreadId())
                    .append(" <<GroupElementHomeImpl>> remove() completed").toString());
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
