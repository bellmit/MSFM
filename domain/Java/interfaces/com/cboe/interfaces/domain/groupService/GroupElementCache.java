//
// -----------------------------------------------------------------------------------
// Source file: GroupElementCache.java
//
// PACKAGE: com.cboe.interfaces.domain.groupService
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.groupService;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.CacheInitializationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.idl.groupElement.ElementEntryStruct;
import com.cboe.idl.groupElement.ElementStruct;

import java.util.Set;

/**
 * Group Service common cache interface. This interface used by GUI as well as Server cache implementation.
 * Provides method for common functionality.
 */
public interface GroupElementCache<T extends GroupElementWrapper>
{
    /**
     * Method to load all the data loaded in cache.
     */
    public void initializeCache() throws CacheInitializationException;

    /**
     * Method to clear all the data loaded in cache.
     */
    public void cleanUpCache();

    /**
     * Finds the element using the 'uniqueSearchHashCode' implementation by the E model objects.
     * This method helps in checking if 'object already exist' while creating new object based on object unique attributes.
     * Returns NULL if elment doesnot exist.
     *
     * @param groupElement object that needs to be searched.
     * @return if exist E object else null.
     */
    public T findElementKeyByUniqueSearchHashCode(T groupElement);

    /**
     * Overloadded method to find the object using unique key combination on element entry struct.
     *
     * @param groupElement object that needs to be searched.
     * @return if exist E object else null.
     */
    public T findElementKeyByUniqueSearchHashCode(ElementEntryStruct groupElement);

    /**
     * Finds the element in the cache using the 'elementKey' and returns the element object.
     * Returns NULL if elment doesnot exist.
     *
     * @param elementKey element to be searched by.
     * @return if exist E object else null.
     */
    public T getGroupElementByElementKey(long elementKey);

    /**
     * Returns the root element in the cloud for the requested group type.
     *
     * @param groupType type of the cloud represented by the constants starting with GROUP_TYPE_*.
     * @return the root element in the cloud, used to start travelling in the cloud.
     */
    public T getRootGroupForGroupType(short groupType);

    /**
     * Returns all the group elements in the cloud without hierarchy or tree structure. Get all groups that belong to
     * the requested group (cloud) type.
     *
     * @param groupType type of the cloud represented by the constants starting with GROUP_TYPE_*.
     * @return all the group elements in the cloud without hierarchy or tree structure.
     */
    public Set<T> getAllGroupsForGroupType(short groupType);

    /**
     * Returns all the direct child elements in the group.
     *
     * @param groupKey element key of the group.
     * @return all the direct child elements in the group.
     */
    public Set<T> getElementsForGroup(long groupKey);

    /**
     * Returns all the direct child group (non-leaf) elements only for the group. Group elements means non-leaf elements.
     *
     * @param groupKey element key of the group.
     * @return all the direct child group elements in the group.
     */
    public Set<T> getSubGroupsForGroup(long groupKey);

    /**
     * Returns all the child group elements (non-leaf) traveling deep level in hierarchy for the group.
     *
     * @param groupKey element key of the group.
     * @return all the child group elements ('n' level deep in the group.)
     */
    public Set<T> getAllSubGroupsForGroup(long groupKey);

    /**
     * Returns the direct leaf elements only for the group.
     *
     * @param groupKey element key of the group.
     * @return all the direct leaf child elements for the group.
     */
    public Set<T> getLeafElementsForGroup(long groupKey);

    /**
     * Returns all the child leaf elements traveling deep level in hierarchy for the group.
     *
     * @param groupKey element key of the group.
     * @return element key of the group.
     */
    public Set<T> getAllLeafElementsForGroup(long groupKey);

    /**
     * Returns all the direct parent group elements for the group.
     *
     * @param groupKey element key of the group.
     * @return all the direct parent groups only.
     */
    public Set<T> getParentGroupsForGroupElement(long groupKey);

    /**
     * Returns all the parent group elements ('n' level up in the hierarchy) for the group.
     *
     * @param groupKey element key of the group.
     * @return all the parent groups 'n' level up in the hierarchy.
     */
    public Set<T> getAllParentGroupsForGroupElement(long groupKey);

    /**
     * Adds the element to group. If element doesn't exist first it cretes the element in cache.
     * Also updates all the relationship maps. This method is used during normal operations by user.
     * Method obtains the row level lock while adding the element.
     *
     * @param groupKey     parent groupKey for which the element to be added.
     * @param groupElement group to be added in the specified group.
     */
    public void addElementToGroup(long groupKey, T groupElement);

    /**
     * Adds a new root element in the cache. New root element creates a new cloud type.
     * If root element already exist for the same cloud/group type it replaces with the new root.
     * @param rootElement root element to be added in the cache.
     */
    public void addRootElement(T rootElement);

    /**
     * Removes the element from the cache. This method is used during normal operations by user.
     * Method obtains the row level lock while removing the element.
     *
     * @param groupKey           group key from which the element to be removed.
     * @param removeGroupElement element that needs to be removed from the group.
     */
    public void removeElementFromGroup(long groupKey, T removeGroupElement);

    /**
     * Update the existing element with the changes in the cache.
     * @param oldElement old element that needs to be updated.
     * @param updatedElement new updated element.
     */
    public void updateElement(ElementStruct oldElement, T updatedElement);
}
