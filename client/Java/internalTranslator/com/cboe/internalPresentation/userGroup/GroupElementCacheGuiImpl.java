//
// -----------------------------------------------------------------------------------
// Source file: GroupElementCacheGuiImpl.java
//
// PACKAGE: com.cboe.internalPresentation.userGroup
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

package com.cboe.internalPresentation.userGroup;

import com.cboe.domain.util.AbstractGroupElementCacheImpl;
import com.cboe.domain.util.GroupElementEventStructContainer;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CacheInitializationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.constants.ElementGroupTypes;
import com.cboe.idl.groupElement.ElementStruct;
import com.cboe.idl.internalBusinessServices.GroupService;
import com.cboe.interfaces.internalPresentation.GroupServiceAPI;
import com.cboe.interfaces.internalPresentation.userGroup.GroupElementModel;
import com.cboe.internalPresentation.common.logging.GUILoggerSABusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelListener;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * GUI layer implementation of the Group Service cache extending. Initialization is impleted in this class, all other
 * base functionality is provided by the Abstarct class.
 */
public class GroupElementCacheGuiImpl extends AbstractGroupElementCacheImpl<GroupElementModel> implements EventChannelListener
{

    protected GroupServiceAPI subscriptionService;
    protected GroupService groupService;
    protected EventChannelAdapter eventChannel;

    // add list of all the clouds to be fetched and stored in the cache
    protected short[] groupTypes = {ElementGroupTypes.GROUP_TYPE_USER};

    // initial capacity of the collections
    protected static final int INITIAL_CAPACITY_NO_OF_ROOTS_VALUE = 1;
    protected static final int INITIAL_CAPACITY_NO_OF_GROUPS_VALUE = 100;
    protected static final int INITIAL_CAPACITY_NO_OF_ELEMENTS_VALUE = 1000;
    protected static final int INITIAL_CAPACITY_OF_LEAF_ELEMENTS_SET_VALUE = 1000;
    protected static final int INITIAL_CAPACITY_OF_GROUP_RELATIONSHIP_SET_VALUE = 8;
    protected static final int INITIAL_CAPACITY_OF_EMPTY_SET_VALUE = 0;
    protected static final int INITIAL_CAPACITY_OF_HASHCODE_COLLISION_SET_VALUE = 4;
    protected static final int INITIAL_CAPACITY_OF_DEFAULT_SET_VALUE = 16;

    /**
     * Added for Unit Testing.
     */
    public GroupElementCacheGuiImpl()
    {
        super();
    }

    /**
     * Constructor.
     */
    public GroupElementCacheGuiImpl(GroupService apiService, GroupServiceAPI subscriptionService, EventChannelAdapter eventChannel)
    {
        super();
        if (subscriptionService == null)
        {
            throw new IllegalArgumentException("GroupServiceAPI may not be null.");
        }
        this.subscriptionService = subscriptionService;

        if (apiService == null)
        {
            throw new IllegalArgumentException("GroupService may not be null.");
        }
        this.groupService = apiService;

        if (eventChannel == null)
        {
            throw new IllegalArgumentException("EventChannelAdapter may not be null.");
        }
        this.eventChannel = eventChannel;
    }

    /**
     * Method to return initial capacity.
     *
     * @param initialCapacityFor find initial capacity for.
     * @return initial capacity for the map.
     */
    protected int getInitialCapacity(String initialCapacityFor)
    {
        if (initialCapacityFor.equals(INITIAL_CAPACITY_NO_OF_ROOTS))
        {
            return INITIAL_CAPACITY_NO_OF_ROOTS_VALUE;
        }
        else if (initialCapacityFor.equals(INITIAL_CAPACITY_NO_OF_GROUPS))
        {
            return INITIAL_CAPACITY_NO_OF_GROUPS_VALUE;
        }
        else if (initialCapacityFor.equals(INITIAL_CAPACITY_NO_OF_ELEMENTS))
        {
            return INITIAL_CAPACITY_NO_OF_ELEMENTS_VALUE;
        }
        else if (initialCapacityFor.equals(INITIAL_CAPACITY_OF_LEAF_ELEMENTS_SET))
        {
            return INITIAL_CAPACITY_OF_LEAF_ELEMENTS_SET_VALUE;
        }
        else if (initialCapacityFor.equals(INITIAL_CAPACITY_OF_GROUP_RELATIONSHIP_SET))
        {
            return INITIAL_CAPACITY_OF_GROUP_RELATIONSHIP_SET_VALUE;
        }
        else if (initialCapacityFor.equals(INITIAL_CAPACITY_OF_EMPTY_SET))
        {
            return INITIAL_CAPACITY_OF_EMPTY_SET_VALUE;
        }
        else if (initialCapacityFor.equals(INITIAL_CAPACITY_OF_HASHCODE_COLLISION_SET))
        {
            return INITIAL_CAPACITY_OF_HASHCODE_COLLISION_SET_VALUE;
        }
        else // also handles scenario INITIAL_CAPACITY_OF_DEFAULT_SET
        {
            return INITIAL_CAPACITY_OF_DEFAULT_SET_VALUE;//This is the default.
        }
    }

    /**
     * Implemented to log message in GUI.
     *
     * @param message to be logged.
     */
    protected void logMessage(String message)
    {
        GUILoggerHome.find().information(getClass().getName(), GUILoggerSABusinessProperty.USER_MANAGEMENT, message);
    }


    /**
     * Initialize cahce from the database using group service. Method is synchronized to avoid simultaneous calls.
     * Also the flag cacheInitialized is used to avoid multiple time initialization of the cache.
     *
     * @throws CacheInitializationException exception in case of any error during cache initialization.
     */
    public void initializeCache() throws CacheInitializationException
    {

        synchronized (elementLocks)
        {
            if (!cacheInitialized)
            {
                try
                {
                    loadDataFromGroupService();
                    subscriptionService.subscribeGroupElementEvent(this);

                    //Validating whether there is atleast root node present
                    if (rootGroupsMap.isEmpty())
                    {
                        GUILoggerHome.find().information(getClass().getName() + ".initializeCache", GUILoggerSABusinessProperty.USER_MANAGEMENT,
                                                         "Error Loading Group Service Cache: No root node found.");
                        throw new CacheInitializationException("Error Loading Group Service Cache: No root node found.");
                    }

                    if (GUILoggerHome.find().isDebugOn() &&
                            GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
                    {
                        GUILoggerHome.find().debug(getClass().getName() + ".initializeCache",
                                                   GUILoggerSABusinessProperty.USER_MANAGEMENT, " Total number of Root Elements " + rootGroupsMap.size() +
                                " Total number of Elements " + elementsMap.size() + "\n subGroupsMap.size() " + subGroupsMap.size() +
                                " \n parentGroupsMap.size() " + parentGroupsMap.size() + "\n groupElementsMap.size() " + groupElementsMap.size() +
                                " \n groupLeafElementsMap.size() " + groupLeafElementsMap.size());
                    }
                    cacheInitialized = true;
                }
                catch (SystemException e)
                {
                    GUILoggerHome.find().exception("Error loading GroupElement cache:" + e.getMessage(), e);
                    throw new CacheInitializationException("Error Loading Group Service Cache:" + e.details.message);
                }
                catch (NotFoundException e)
                {
                    GUILoggerHome.find().exception("Error loading GroupElement cache:" + e.getMessage(), e);
                    throw new CacheInitializationException("Error Loading Group Service Cache:" + e.details.message);
                }
                catch (CommunicationException e)
                {
                    GUILoggerHome.find().exception("Error loading GroupElement cache:" + e.getMessage(), e);
                    throw new CacheInitializationException("Error Loading Group Service Cache:" + e.details.message);
                }
                catch (AuthorizationException e)
                {
                    GUILoggerHome.find().exception("Error loading GroupElement cache:" + e.getMessage(), e);
                    throw new CacheInitializationException("Error Loading Group Service Cache:" + e.details.message);
                }
                catch (DataValidationException e)
                {
                    GUILoggerHome.find().exception("Error loading GroupElement cache:" + e.getMessage(), e);
                    throw new CacheInitializationException("Error Loading Group Service Cache:" + e.details.message);
                }
                finally
                {
                    if (!cacheInitialized)
                    {
                        // cleaning up all the data from cache maps in case of any failure
                        cleanUpCache();
                    }
                }

            }

        }
    }

    /**
     * Update the existing element with the changes in the cache.
     * @param oldElement old element that needs to be updated.
     * @param updatedElement new updated element.
     */
    public void updateElement(ElementStruct oldElement, GroupElementModel updatedElement)
    {
        synchronized (getElementLockObject(updatedElement.getElementKey()))
        {
            // the same reference of the element is stored in all the collection, hence
            // updating element and uniqueElementByHashCodeMap
            
            // find the original element using elementKey
            GroupElementModel element = getGroupElementByElementKey(oldElement.elementKey);

            // removing old elemnt from the uniqueElementByHashCodeMap
            Set<GroupElementModel> elements = uniqueElementByHashCodeMap.get(uniqueSearchHashCode(oldElement.entryStruct));
            if (elements != null)
            {
                elements.remove(element);
            }
            
            // update the element with new values
            element.copyValues(updatedElement);

            // adding update group element to uniqueElementByHashCodeMap
            long uniqueSearchHashCode = uniqueSearchHashCode(element);
            elements = uniqueElementByHashCodeMap.get(uniqueSearchHashCode);
            if (elements == null)
            {
                elements = createSetCollection(INITIAL_CAPACITY_OF_HASHCODE_COLLISION_SET);
                uniqueElementByHashCodeMap.put(uniqueSearchHashCode, elements);
            }
            elements.add(element);
            
        }
    }

    /**
     * Overriding the method to create sorted relationship collection objects ot type TreeSet.
     *
     * @param initialCapacityFor
     * @return Sortable tree collection object.
     */
    protected Set<GroupElementModel> createSetCollection(String initialCapacityFor)
    {
        // added synchronized set to take care of simultenous add, get from different threads
        return Collections.synchronizedSortedSet(new TreeSet<GroupElementModel>());
    }

    /**
     * Method creates the new copy of the collection setas shallow copy.
     *
     * @param collectionToClone collection to be cloned.
     * @return shallow cloned collection object.
     */
    protected Set<GroupElementModel> shallowCloneSetCollection(Set<GroupElementModel> collectionToClone)
    {
        return new TreeSet<GroupElementModel>(collectionToClone);
    }

    /**
     * Internal method to initilaize the cache using the groupService API.
     */
    protected void loadDataFromGroupService() throws SystemException, NotFoundException, CommunicationException, AuthorizationException
    {
        for (short groupType : groupTypes)
        {
            ElementStruct rootGroupStruct = groupService.getRootElementForGroupType(groupType);
            GroupElementModel rootGroup = new GroupElementModelImpl(rootGroupStruct);
            // creating root element in cache
            createElementWithoutLock(rootGroup);
            // adding relationship of th root element in cache
            fetchAndUpdateRelationships(rootGroup);
            // calling recursive method to update all the elements and their relationship in the cache.
            findSubgroupsForGroupAndPopulateRelationships(rootGroup);
        }
    }

    /**
     * Run recursively and populates all the data from database.
     *
     * @param element element for which the subgroups to be loaded and populated.
     */
    protected void findSubgroupsForGroupAndPopulateRelationships(GroupElementModel element)
            throws SystemException, CommunicationException, AuthorizationException
    {
        Set<GroupElementModel> subGroups = subGroupsMap.get(element.getElementKey());
        if (subGroups != null)
        {
            for (GroupElementModel subGroup : subGroups)
            {
                fetchAndUpdateRelationships(subGroup);
                findSubgroupsForGroupAndPopulateRelationships(subGroup);
            }
        }
    }

    /**
     * Method that actually fetches the relationship from database and updates relationships for the given
     * group element.
     *
     * @param element for which relationship to be fetched from database.
     */
    protected void fetchAndUpdateRelationships(GroupElementModel element)
            throws SystemException, CommunicationException, AuthorizationException
    {
        ElementStruct[] childElements = groupService.getElementsForGroup(element.getElementKey());
        for (ElementStruct child : childElements)
        {
            GroupElementModel childModel = getGroupElementByElementKey(child.elementKey);
            if (childModel == null)
            {
                childModel = new GroupElementModelImpl(child);
            }
            addElementToGroupWithoutLock(element.getElementKey(), childModel);
        }
    }


    /**
     * Implementation of EventChannelListner interface.
     *
     * @param channelEvent received event.
     */
    public void channelUpdate(ChannelEvent channelEvent)
    {
        int channelType = ((ChannelKey) channelEvent.getChannel()).channelType;
        Object eventData = channelEvent.getEventData();
        if (GUILoggerHome.find().isDebugOn() &&
                            GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
        {
            GUILoggerHome.find().debug(getClass().getName(), GUILoggerSABusinessProperty.USER_MANAGEMENT,
                                       "Received Group Element Event of Channel Type: " + channelType);
        }
        if (channelType == ChannelType.GROUP_ADD_ELEMENT)
        {
            handleEventForGroupElementAddition(eventData);
        }
        else if (channelType == ChannelType.GROUP_REMOVE_ELEMENT)
        {
            handleEventForGroupElementRemoval(eventData);
        }
        else if (channelType == ChannelType.GROUP_UPDATE_ELEMENT)
        {
            handleEventForGroupElementUpdation(eventData);
        }
    }

    /**
     * Method to handle the ChannelType.CB_GROUP_UPDATE_ELEMENT event.
     *
     * @param eventData data received in the event.
     */
    private void handleEventForGroupElementUpdation(Object eventData)
    {
        //Element struct will have the updated data.
        ElementStruct elementStruct = (ElementStruct) eventData;
        //Element model will have the old data.
        GroupElementModel updatedModel = new GroupElementModelImpl(elementStruct);
        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
        {
            GUILoggerHome.find().debug(getClass().getName(), GUILoggerSABusinessProperty.USER_MANAGEMENT,
                                       "Calling updateElement()");
        }
        // updating the cache with updated model
        // find the original element using elementKey
        GroupElementModel oldModel = getGroupElementByElementKey(updatedModel.getElementKey());
        updateElement(oldModel.toElementStruct(),updatedModel);
        acceptUpdateElement(updatedModel);
    }

    /**
     * Method to handle the ChannelType.CB_GROUP_REMOVE_ELEMENT event.
     *
     * @param eventData data received in the event.
     */
    private void handleEventForGroupElementRemoval(Object eventData)
    {
        GroupElementEventStructContainer structContainer = (GroupElementEventStructContainer) eventData;
        long groupKey = structContainer.getParentGroupElementKey();
        ElementStruct elementStruct = structContainer.getElementStruct();
        GroupElementModel elementModel = getGroupElementByElementKey(elementStruct.elementKey);
        if (elementModel != null)
        {
            if (GUILoggerHome.find().isDebugOn() &&
                            GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
            {
                GUILoggerHome.find().debug(getClass().getName(), GUILoggerSABusinessProperty.USER_MANAGEMENT,
                                           "Calling removeElementFromGroup()");
            }
            removeElementFromGroup(groupKey, elementModel);

            GroupElementCacheEventContainer groupElementCacheEventContainer =
                    new GroupElementCacheEventContainer(getGroupElementByElementKey(groupKey), elementModel);
            acceptRemoveElement(groupElementCacheEventContainer);
        }
    }

    /**
     * Method to handle the ChannelType.CB_GROUP_ADD_ELEMENT event.
     *
     * @param eventData data received in the event.
     */
    private void handleEventForGroupElementAddition(Object eventData)
    {
        GroupElementEventStructContainer structContainer = (GroupElementEventStructContainer) eventData;
        long parentGroupKey = structContainer.getParentGroupElementKey();
        ElementStruct elementStruct = structContainer.getElementStruct();
        if (GUILoggerHome.find().isDebugOn() &&
                            GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
        {
            GUILoggerHome.find().debug(getClass().getName(), GUILoggerSABusinessProperty.USER_MANAGEMENT,
                                       "Calling addElementToGroup()");
        }
        GroupElementModel groupElement = getGroupElementByElementKey(elementStruct.elementKey);
        if (groupElement == null)
        {
            groupElement = new GroupElementModelImpl(elementStruct);
        }
        addElementToGroup(parentGroupKey, groupElement);

        GroupElementCacheEventContainer groupElementCacheEventCointainer =
                new GroupElementCacheEventContainer(getGroupElementByElementKey(parentGroupKey), groupElement);
        acceptAddElement(groupElementCacheEventCointainer);
    }

    /**
     * Method to dispatch event on IEC. GUI components are subscribed to receive this events.
     * @param groupElementCacheEventCointainer to be data sent in event.
     */
    private void acceptAddElement(GroupElementCacheEventContainer groupElementCacheEventCointainer)
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_GROUP_ADD_ELEMENT, 0);
        ChannelEvent event = eventChannel.getChannelEvent(this, key, groupElementCacheEventCointainer);
        eventChannel.dispatch(event);
    }

    /**
     * Method to dispatch event on IEC. GUI components are subscribed to receive this events.
     * @param groupElementCacheEventCointainer to be data sent in event.
     */
    private void acceptRemoveElement(GroupElementCacheEventContainer groupElementCacheEventCointainer)
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_GROUP_REMOVE_ELEMENT, 0);
        ChannelEvent event = eventChannel.getChannelEvent(this, key, groupElementCacheEventCointainer);
        eventChannel.dispatch(event);
    }

    /**
     * Method to dispatch event on IEC. GUI components are subscribed to receive this events.
     * @param elementModel to be data sent in event.
     */
    private void acceptUpdateElement(GroupElementModel elementModel)
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_GROUP_UPDATE_ELEMENT, 0);
        ChannelEvent event = eventChannel.getChannelEvent(this, key, elementModel);
        eventChannel.dispatch(event);
    }
}
