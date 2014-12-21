package com.cboe.application.groupService;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.groupElement.ElementEntryStruct;
import com.cboe.idl.groupElement.ElementErrorResultStruct;
import com.cboe.idl.groupElement.ElementStruct;
import com.cboe.idl.util.ErrorStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.interfaces.internalBusinessServices.GroupService;
import com.cboe.interfaces.internalBusinessServices.GroupServiceHome;


public class GroupServiceImpl extends BObject implements GroupService
{
    private GroupService groupService = null;

    /**
     * Constructor
     */
    public GroupServiceImpl()
    {
        super();
    }

    /**
     * Creates an valid instance of the service.
     *
     * @param name name of this object
     */
    public void create(String name)
    {
        super.create(name);
        getGroupService();
    }

    /**
     * Retrieves the real User Service
     */
    private GroupService getGroupService()
    {
        if (groupService == null)
        {
            try
            {
                GroupServiceHome
                        home = (GroupServiceHome) HomeFactory.getInstance().findHome(GroupServiceHome.ADMIN_HOME_NAME);

                groupService = (GroupService) home.find();
            }
            catch (CBOELoggableException e)
            {
                throw new NullPointerException("Could not find GroupServiceHome");
            }
        }
        return groupService;
    }


    public ElementErrorResultStruct[] createElementsForGroup(long i, ElementEntryStruct[] elementEntryStructs)
            throws SystemException, CommunicationException, TransactionFailedException, AuthorizationException, DataValidationException
    {
        return groupService.createElementsForGroup(i, elementEntryStructs);
    }

    public ErrorStruct[] addElementsToGroup(long i, long[] ints)
            throws SystemException, CommunicationException, TransactionFailedException, AuthorizationException, DataValidationException
    {
        return groupService.addElementsToGroup(i, ints);
    }

    public ElementStruct updateElement(ElementStruct elementStruct)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AlreadyExistsException,
                   AuthorizationException
    {
        return groupService.updateElement(elementStruct);
    }

    public ErrorStruct[] removeElementsFromGroup(long i, long[] ints)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException
    {
        return groupService.removeElementsFromGroup(i, ints);
    }

    public ElementStruct getRootElementForGroupType(short i)
            throws SystemException, CommunicationException, NotFoundException, AuthorizationException
    {
        return groupService.getRootElementForGroupType(i);
    }

    public ElementStruct[] getElementsForGroup(long i)
            throws SystemException, CommunicationException, AuthorizationException
    {
        return groupService.getElementsForGroup(i);
    }

    public ElementStruct getElementByKey(long i)
            throws SystemException, CommunicationException, NotFoundException, AuthorizationException
    {
        return groupService.getElementByKey(i);
    }

    public ElementStruct[] getParentGroupsForElement(long i)
            throws SystemException, CommunicationException, AuthorizationException
    {
        return groupService.getParentGroupsForElement(i);
    }

    public ElementStruct createRootGroupForType(String rootName, short groupType)
            throws DataValidationException,AlreadyExistsException, SystemException, CommunicationException, AuthorizationException
    {
        return groupService.createRootGroupForType(rootName, groupType);
    }

    public ElementStruct[] getAllLeafElementsForGroup(String groupName)
            throws SystemException, CommunicationException, AuthorizationException
    {
        return groupService.getAllLeafElementsForGroup(groupName);
    }
}
