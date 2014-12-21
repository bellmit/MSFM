//
// -----------------------------------------------------------------------------------
// Source file: AbstractGroupElementCacheImpl.java
//
// PACKAGE: com.cboe.domain.util
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.util;

import com.cboe.idl.groupElement.ElementEntryStruct;
import com.cboe.interfaces.domain.groupService.GroupElementCache;
import com.cboe.interfaces.domain.groupService.GroupElementWrapper;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AbstractGroupElementCacheImpl provides common caching functionality for GroupElementWrapper object.
 * This cache is used by GroupService on SAGUI and Global Server.
 */
public abstract class AbstractGroupElementCacheImpl<E extends GroupElementWrapper> implements GroupElementCache<E>
{
    public static final String INITIAL_CAPACITY_NO_OF_ELEMENTS = "initialCapacityNoOfElements";
    public static final String INITIAL_CAPACITY_NO_OF_GROUPS = "initialCapacityNoOfGroups";
    public static final String INITIAL_CAPACITY_NO_OF_ROOTS = "initialCapacityNoOfRoots";

    public static final String INITIAL_CAPACITY_OF_LEAF_ELEMENTS_SET = "initialCapacityOfLeafElementsSet";
    public static final String INITIAL_CAPACITY_OF_GROUP_RELATIONSHIP_SET = "initialCapacityOfGroupRelationshipSet";
    public static final String INITIAL_CAPACITY_OF_EMPTY_SET = "initialCapacityOfEmptySet";
    public static final String INITIAL_CAPACITY_OF_DEFAULT_SET = "initialCapacityOfDefaultSet";
    public static final String INITIAL_CAPACITY_OF_HASHCODE_COLLISION_SET = "initialCapacityOfHashcodeCollisionSet";

    // elements map
    /**
     * Map created with elementKey as the key and element object as value in the map.
     */
    //protected final Map<Long, E> elementsMap;
    public final Map<Long, E> elementsMap;


    /**
     * Map created with unique attributes composite hashcode as the key and element object as value in the map.
     * Helps in finding the element exist or not based on unique attributes. And returns the object if exist.
     */
    protected final Map<Long, Set<E>> uniqueElementByHashCodeMap;

    //relationship maps
    /**
     * Holds all the direct child elements of the group. Child element can be of type subgroup or leaf element.
     * Group elementKey is stored as key and Set of all child elements as value.
     */
    protected final Map<Long, Set<E>> groupElementsMap;

    /**
     * Holds all the direct child <b>leaf elements only</b> of the group.
     * Group elementKey is stored as key and Set of group child elements as value.
     */
    protected final Map<Long, Set<E>> groupLeafElementsMap;

    /**
     * Holds all the direct child <b>group elements only</b> of the group.
     * Group elementKey is stored as key and Set of leaf child elements as value.
     */
    protected final Map<Long, Set<E>> subGroupsMap;

    /**
     * Holds all the direct parent group elements <b>only</b> for the element.
     * Element can be either group or leaf element.
     * Any kind of element's elementKey is stored as key and Set of parent group elements as value.
     */
    protected final Map<Long, Set<E>> parentGroupsMap;

    /**
     * Holds the root group of the cloud to begin with. This helps in building the cloud and
     * allows using different clouds for different needs.
     */
    protected final Map<Short, E> rootGroupsMap;

    /**
     * Map to hold the element/row level locking.
     */
    protected final Map<Long, Long> elementLocks;

    /**
     * Flag to maintain state of the cache initialization.
     */
    protected boolean cacheInitialized = false;

    /**
     * Used as lock when no row level lock is available
     */
    protected final Object genericLockObject;

    /**
     * creating map objects for cache.
     */
    public AbstractGroupElementCacheImpl()
    {
        int noOfRoots = getInitialCapacity(INITIAL_CAPACITY_NO_OF_ROOTS);
        int noOfGroups = getInitialCapacity(INITIAL_CAPACITY_NO_OF_GROUPS);
        int noOfElements = getInitialCapacity(INITIAL_CAPACITY_NO_OF_ELEMENTS);
        elementsMap = new ConcurrentHashMap<Long, E>(noOfElements);
        uniqueElementByHashCodeMap = new ConcurrentHashMap<Long, Set<E>>(noOfElements);
        groupElementsMap = new ConcurrentHashMap<Long, Set<E>>(noOfGroups);
        groupLeafElementsMap = new ConcurrentHashMap<Long, Set<E>>(noOfGroups);
        subGroupsMap = new ConcurrentHashMap<Long, Set<E>>(noOfGroups);
        parentGroupsMap = new ConcurrentHashMap<Long, Set<E>>(noOfElements);
        rootGroupsMap = new ConcurrentHashMap<Short, E>(noOfRoots);
        elementLocks = new ConcurrentHashMap<Long, Long>(noOfElements);
        genericLockObject = new Object();
    }

    /**
     * Method to clear all the data loaded in cache.
     */
    public void cleanUpCache()
    {
        elementsMap.clear();
        uniqueElementByHashCodeMap.clear();
        groupElementsMap.clear();
        groupLeafElementsMap.clear();
        subGroupsMap.clear();
        parentGroupsMap.clear();
        rootGroupsMap.clear();
        elementLocks.clear();
    }
    /**
     * Method to return initial capacity from property file.
     *
     * @param initialCapacityFor find initial capacity for.
     * @return initial capacity for the map.
     */
    protected abstract int getInitialCapacity(String initialCapacityFor);

    /**
     * Logger abstract method. Added to log messages appropriatly by different implementation.
     */
    protected abstract void logMessage(String message);


    /**
     * Returns the cache initialization status.
     *
     * @return true if cache is initialized successfully else false.
     */
    public boolean isCacheInitialized()
    {
        return cacheInitialized;
    }

    /**
     * Finds the element in the cache using the 'elementKey' and returns the element object.
     * Returns NULL if elment doesnot exist.
     *
     * @param elementKey element to be searched by.
     * @return if exist E object else null.
     */
    public E getGroupElementByElementKey(long elementKey)
    {
        return elementsMap.get(elementKey);
    }

    /**
     * Finds the element using the 'uniqueSearchHashCode' implementation by the E model objects.
     * This method helps in checking if 'object already exist' while creating new object based on object unique attributes.
     * Returns NULL if elment doesnot exist.
     *
     * @param element object that needs to be searched.
     * @return if exist E object else null.
     */
    public E findElementKeyByUniqueSearchHashCode(E element)
    {
        // using ConcurrentHashMap hence no lock is required
        Set<E> elements = uniqueElementByHashCodeMap.get(uniqueSearchHashCode(element));
        // doing linear search inside the set with 4 parameter equals
        if (elements != null)
        {
            for (E cacheElement : elements)
            {
                if (isElementsEqual(cacheElement, element))
                {
                    return cacheElement;
                }
            }
        }
        // returning null if element not found
        return null;
    }

    /**
     * The hascode is build using the combination of unique attributes of the object. The implementation evaluates the
     * unique hash code using attributes <b>elementName</b>, <b>elementDataKey</b>, <b>elementDataType</b> and <b>nodeType</b>.
     *
     * @param element for which the hashCode to be calculated.
     * @return hashcode used to search object uniquely.
     */
    protected long uniqueSearchHashCode(E element)
    {
        return
                ((element.getElementName() != null ? element.getElementName().hashCode() : 0) * 13) +
                        element.getElementGroupType() * 17 +
                        element.getElementDataType() * 47 +
                        element.getNodeType() * 3;
    }

    /**
     * The hascode is build using the combination of unique attributes of the object. The implementation evaluates the
     * unique hash code using attributes <b>elementName</b>, <b>elementDataKey</b>, <b>elementDataType</b> and <b>nodeType</b>.
     *
     * @param groupElement for which the hashCode to be calculated.
     * @return hashcode used to search object uniquely.
     */
    protected long uniqueSearchHashCode(ElementEntryStruct groupElement)
    {
        return
                ((groupElement.elementName != null ? groupElement.elementName.hashCode() : 0) * 13) +
                        groupElement.elementGroupType * 17 +
                        groupElement.elementDataType * 47 +
                        groupElement.nodeType * 3;
    }

    /**
     * Method to perform equals on 4 unique attributes.
     * 1) Element Name, 2) Group Type, 3) Data Type and 4) Node Type.
     *
     * @param cacheElement element to compare.
     * @param newElement   element to compare.
     * @return true if both elements are equal.
     */
    protected boolean isElementsEqual(E cacheElement, E newElement)
    {
        // no null check required for elementName because elementName cannot be empty or null
        return (cacheElement.getElementName().equals(newElement.getElementName()))
                && (cacheElement.getElementGroupType() == newElement.getElementGroupType())
                && (cacheElement.getElementDataType() == newElement.getElementDataType())
                && (cacheElement.getNodeType() == newElement.getNodeType());

    }

    /**
     * Method to perform equals on 4 unique attributes.
     * 1) Element Name, 2) Group Type, 3) Data Type and 4) Node Type.
     *
     * @param cacheElement element to compare.
     * @param newElement   element to compare.
     * @return true if both elements are equal.
     */
    protected boolean isElementsEqual(E cacheElement, ElementEntryStruct newElement)
    {
        // no null check required for elementName because elementName cannot be empty or null
        return (cacheElement.getElementName().equals(newElement.elementName))
                && (cacheElement.getElementGroupType() == newElement.elementGroupType)
                && (cacheElement.getElementDataType() == newElement.elementDataType)
                && (cacheElement.getNodeType() == newElement.nodeType);

    }

    /**
     * Overloadded method to find the object using unique key combination on element entry struct.
     *
     * @param groupElement object that needs to be searched.
     * @return if exist E object else null.
     */
    public E findElementKeyByUniqueSearchHashCode(ElementEntryStruct groupElement)
    {
        Set<E> elements = uniqueElementByHashCodeMap.get(uniqueSearchHashCode(groupElement));
        // doing linear search inside the set
        if (elements != null)
        {
            for (E cacheElement : elements)
            {
                if (isElementsEqual(cacheElement, groupElement))
                {
                    return cacheElement;
                }
            }
        }
        // returning null if element not found
        return null;
    }

    /**
     * Returns the root element in the cloud for the requested group type.
     *
     * @param groupType type of the cloud represented by the constants starting with GROUP_TYPE_*.
     * @return the root element in the cloud, used to start travelling in the cloud.
     */
    public E getRootGroupForGroupType(short groupType)
    {
        // no lock required since this map doesn't change or changes in controlled env.
        return rootGroupsMap.get(groupType);
    }


    /**
     * Method allows to create different types of collection for the cache.
     *
     * @param initialCapacityFor
     * @return the new Set collection object.
     */
    protected abstract Set<E> createSetCollection(String initialCapacityFor);

    /**
     * Method creates the new copy of the collection setas shallow copy.
     *
     * @param collectionToClone collection to be cloned.
     * @return shallow cloned collection object.
     */
    protected abstract Set<E> shallowCloneSetCollection(Set<E> collectionToClone);

    /**
     * Method to acquire the element level (row level) locking.
     *
     * @param elementKey the element key for which the lock needs to be acquired.
     * @return the object that can be used infor Synchronize block.
     */
    protected Object getElementLockObject(long elementKey)
    {
        synchronized (elementLocks)
        {
            Long lockObject = elementLocks.get(elementKey);
            if (lockObject == null)
            {
                logMessage("Error getting lock for the elementKey: " + elementKey);
                return genericLockObject;
            }
            return lockObject;
        }
    }

    /**
     * Method to acquire the element level (row level) locking,
     * if lock doesn't exist method takes care of creating lock object.
     *
     * @param elementKey the element key for which the lock needs to be acquired.
     * @return the object that can be used infor Synchronize block.
     */
    protected Object getOrCreateElementLockObject(long elementKey)
    {
        Long elementLock;
        synchronized (elementLocks)
        {
            elementLock = elementLocks.get(elementKey);
            if (elementLock == null)
            {
                // creating the new element lock object in cas if it doesn't exist.
                elementLocks.put(elementKey, elementKey);
                elementLock = elementLocks.get(elementKey);
            }
        }
        return elementLock;
    }

    /**
     * Returns all the group elements in the cloud without hierarchy or tree structure. Get all groups that belong to
     * the requested group (cloud) type.
     *
     * @param groupType type of the cloud represented by the constants starting with GROUP_TYPE_*.
     * @return all the group elements in the cloud without hierarchy or tree structure.
     */
    public Set<E> getAllGroupsForGroupType(short groupType)
    {
        E rootGroup = getRootGroupForGroupType(groupType);
        Set<E> allGroups;
        if (rootGroup != null)
        {
            // putting the above element to start the recursive algorithm of looping
            Set<E> rootGroups = createSetCollection(INITIAL_CAPACITY_OF_DEFAULT_SET);
            rootGroups.add(rootGroup);

            // creating set to populate the group element objects
            allGroups = createSetCollection(INITIAL_CAPACITY_OF_GROUP_RELATIONSHIP_SET);

            // calling method to run recursively to find all the groups in tree cloud
            populateSubGroups(rootGroups, allGroups);
        }
        else
        {
            allGroups = createSetCollection(INITIAL_CAPACITY_OF_EMPTY_SET);
        }

        return allGroups;
    }

    /**
     * Internal method to navigate the cloud recursively. Similar to the tree navigation.
     *
     * @param newGroups groups that needs to be travelled.
     * @param allGroups list of all groups.
     */
    protected void populateSubGroups(Set<E> newGroups, Set<E> allGroups)
    {
        Set<E> subgroups;
        // add all the Groups
        for (E group : newGroups)
        {
            allGroups.add(group);
            subgroups = getSubGroupsForGroup(group.getElementKey());
            if (subgroups != null && subgroups.size() > 0)
            {
                populateSubGroups(subgroups, allGroups);
            }
        }

    }

    /**
     * Returns all the direct child elements in the group.
     *
     * @param groupKey element key of the group.
     * @return all the direct child elements in the group.
     */
    public Set<E> getElementsForGroup(long groupKey)
    {
        Set<E> tempChildGroups;

        // locking the group from updates while accessing
        synchronized (getElementLockObject(groupKey))
        {
            Set<E> childElements = groupElementsMap.get(groupKey);

            if (childElements != null)
            {
                tempChildGroups = shallowCloneSetCollection(childElements);
            }
            else
            {
                tempChildGroups = createSetCollection(INITIAL_CAPACITY_OF_EMPTY_SET);
            }
        }
        return tempChildGroups;
    }

    /**
     * Returns all the direct child group (non-leaf) elements only for the group. Group elements means non-leaf elements.
     *
     * @param groupKey element key of the group.
     * @return all the direct child group elements in the group.
     */
    public Set<E> getSubGroupsForGroup(long groupKey)
    {
        Set<E> tempSubGroups;

        // locking the group from updates while accessing
        synchronized (getElementLockObject(groupKey))
        {
            Set<E> subgroups = subGroupsMap.get(groupKey);
            if (subgroups != null)
            {
                tempSubGroups = shallowCloneSetCollection(subgroups);
            }
            else
            {
                tempSubGroups = createSetCollection(INITIAL_CAPACITY_OF_EMPTY_SET);
            }
        }
        return tempSubGroups;
    }

    /**
     * Returns all the child group elements (non-leaf) traveling deep level in hierarchy for the group.
     *
     * @param groupKey element key of the group.
     * @return all the child group elements ('n' level deep in the group.)
     */
    public Set<E> getAllSubGroupsForGroup(long groupKey)
    {
        Set<E> groups;
        if (elementLocks.containsKey(groupKey))
        {
            Set<E> subgroups = getSubGroupsForGroup(groupKey);
            groups = createSetCollection(INITIAL_CAPACITY_OF_GROUP_RELATIONSHIP_SET);
            populateSubGroups(subgroups, groups);
        }
        else
        {
            groups = createSetCollection(INITIAL_CAPACITY_OF_EMPTY_SET);
        }
        return groups;
    }

    /**
     * Returns the direct leaf elements only for the group.
     *
     * @param groupKey element key of the group.
     * @return all the direct leaf child elements for the group.
     */
    public Set<E> getLeafElementsForGroup(long groupKey)
    {
        Set<E> tempLeafElements;

        // locking the group from updates while accessing
        synchronized (getElementLockObject(groupKey))
        {
            Set<E> leafElements = groupLeafElementsMap.get(groupKey);
            if (leafElements != null)
            {
                tempLeafElements = shallowCloneSetCollection(leafElements);
            }
            else
            {
                tempLeafElements = createSetCollection(INITIAL_CAPACITY_OF_EMPTY_SET);
            }
        }

        return tempLeafElements;
    }

    /**
     * Returns all the child leaf elements traveling deep level in hierarchy for the group.
     *
     * @param groupKey element key of the group.
     * @return element key of the group.
     */
    public Set<E> getAllLeafElementsForGroup(long groupKey)
    {
        Set<E> allLeafElements;
        if (elementLocks.containsKey(groupKey))
        {
            allLeafElements = createSetCollection(INITIAL_CAPACITY_OF_LEAF_ELEMENTS_SET);
            allLeafElements.addAll(getLeafElementsForGroup(groupKey));

            Set<E> allSubgroups = getAllSubGroupsForGroup(groupKey);

            for (E groupElement : allSubgroups)
            {
                allLeafElements.addAll(getLeafElementsForGroup(groupElement.getElementKey()));
            }
        }
        else
        {
            allLeafElements = createSetCollection(INITIAL_CAPACITY_OF_EMPTY_SET);
        }
        return allLeafElements;
    }

    /**
     * Returns all the direct parent group elements for the group.
     *
     * @param groupElementKey element key of the group.
     * @return all the direct parent groups only.
     */
    public Set<E> getParentGroupsForGroupElement(long groupElementKey)
    {
        Set<E> tempParentGroups;

        // locking the element from updates while accessing
        synchronized (getElementLockObject(groupElementKey))
        {
            Set<E> parentGroups = parentGroupsMap.get(groupElementKey);
            if (parentGroups != null)
            {
                tempParentGroups = shallowCloneSetCollection(parentGroups);
            }
            else
            {
                tempParentGroups = createSetCollection(INITIAL_CAPACITY_OF_EMPTY_SET);
            }
        }

        return tempParentGroups;
    }

    /**
     * Returns all the parent group elements ('n' level up in the hierarchy) for the group.
     *
     * @param groupElementKey element key of the group.
     * @return all the parent groups 'n' level up in the hierarchy.
     */
    public Set<E> getAllParentGroupsForGroupElement(long groupElementKey)
    {
        Set<E> allParentGroups;
        if (elementLocks.containsKey(groupElementKey))
        {
            Set<E> parentGroups = getParentGroupsForGroupElement(groupElementKey);

            // creating set to populate the group element objects
            allParentGroups = createSetCollection(INITIAL_CAPACITY_OF_GROUP_RELATIONSHIP_SET);

            // calling method to run recursively to find all the groups in tree cloud
            populateParentGroups(parentGroups, allParentGroups);
        }
        else
        {
            allParentGroups = createSetCollection(INITIAL_CAPACITY_OF_EMPTY_SET);
        }

        return allParentGroups;
    }

    /**
     * Method to recuresively find all the parent groupss including grand parents for the given
     * group recursively in the hierarcy.
     *
     * @param parentGroups    set of parent groups to be navigated.
     * @param allParentGroups list of all the parent groups.
     */
    protected void populateParentGroups(Set<E> parentGroups, Set<E> allParentGroups)
    {
        Set<E> nextParentGroups;
        // add all the Groups
        for (E group : parentGroups)
        {
            allParentGroups.add(group);
            nextParentGroups = getParentGroupsForGroupElement(group.getElementKey());
            if (!nextParentGroups.isEmpty())
            {
                populateParentGroups(nextParentGroups, allParentGroups);
            }
        }
    }


    /**
     * Update master cache maps for creating new element.
     *
     * @param newGroupElement new element to be created.
     */
    protected void createElement(E newGroupElement)
    {
        // get will create the lock object if it doesn't exist
        synchronized (getOrCreateElementLockObject(newGroupElement.getElementKey()))
        {
            createGroupElement(newGroupElement);
        }
    }

    /**
     * private helper method for creating a new element.
     * @param newGroupElement new element to be created.
     */
    private void createGroupElement(E newGroupElement)
    {
        // adding new group element to elementsGroup
        elementsMap.put(newGroupElement.getElementKey(), newGroupElement);

        // adding new group element to uniqueElementByHashCodeMap
        long uniqueSearchHashCode = uniqueSearchHashCode(newGroupElement);
        Set<E> elements = uniqueElementByHashCodeMap.get(uniqueSearchHashCode);
        if (elements == null)
        {
            elements = createSetCollection(INITIAL_CAPACITY_OF_HASHCODE_COLLISION_SET);
            uniqueElementByHashCodeMap.put(uniqueSearchHashCode, elements);
        }
        elements.add(newGroupElement);

        // if new group element is root add into root map
        if (newGroupElement.isRoot())
        {
            rootGroupsMap.put(newGroupElement.getElementGroupType(), newGroupElement);
        }
    }

    /**
     * Update master cache maps for creating new element.
     *
     * @param newGroupElement new element to be created.
     */
    protected void createElementWithoutLock(E newGroupElement)
    {
        // get will create the lock object if it doesn't exist
        getOrCreateElementLockObject(newGroupElement.getElementKey());
        createGroupElement(newGroupElement);
    }

    /**
     * Adds the element to group. If element doesn't exist first it cretes the element in cache.
     * Also updates all the relationship maps. This method is used during normal operations by user.
     * Method obtains the row level lock while adding the element.
     *
     * @param groupKey     parent groupKey for which the element to be added.
     * @param groupElement group to be added in the specified group.
     */
    public void addElementToGroup(long groupKey, E groupElement)
    {

        synchronized (getElementLockObject(groupKey))
        {
            // validate if the object already exist
            if (! elementsMap.containsKey(groupElement.getElementKey()))
            {
                createElement(groupElement);
            }
            // adding new group element in the parentGroupsMap, also holds the parents for the leaf elements
            // obtaining lock of the element to update parents
            synchronized (getElementLockObject(groupElement.getElementKey()))
            {
                addParentRelationship(groupKey, groupElement);
            }

            addRelationship(groupKey, groupElement);
        }
    }

    /**
     * Adds the element to group. Update all the relationship maps. This method is used during cache initialization.
     * Method doesn not obtain the row level lock while adding the element to group.
     *
     * @param groupKey     roupKey for which the element to be added.
     * @param groupElement group to be added in the specified group.
     */
    protected void addElementToGroupWithoutLock(long groupKey, E groupElement)
    {
        // validate if the object already exist
        if (! elementsMap.containsKey(groupElement.getElementKey()))
        {
            createElementWithoutLock(groupElement);
        }
        addParentRelationship(groupKey, groupElement);
        addRelationship(groupKey, groupElement);
    }

    /**
     * Add parent relationship.
     *
     * @param groupKey     group key for which the element to be added.
     * @param groupElement element that needs to be added to the group.
     */
    protected void addParentRelationship(long groupKey, E groupElement)
    {
        Set<E> parentGroups = parentGroupsMap.get(groupElement.getElementKey());
        if (parentGroups == null)
        {
            parentGroups = createSetCollection(INITIAL_CAPACITY_OF_GROUP_RELATIONSHIP_SET);
            parentGroupsMap.put(groupElement.getElementKey(), parentGroups);
        }
        parentGroups.add(elementsMap.get(groupKey));
    }

    /**
     * Method to update all the relationship maps.
     *
     * @param groupKey     group key for which the element to be added.
     * @param groupElement element that needs to be added to the group.
     */
    protected void addRelationship(long groupKey, E groupElement)
    {
        // adding new group element in the groupElementsMap
        Set<E> elementsForGroup = groupElementsMap.get(groupKey);
        if (elementsForGroup == null)
        {
            elementsForGroup = createSetCollection(INITIAL_CAPACITY_OF_GROUP_RELATIONSHIP_SET);
            groupElementsMap.put(groupKey, elementsForGroup);
        }
        elementsForGroup.add(groupElement);

        // updating relationship maps cheking if the new element is leaf or not
        if (groupElement.isLeaf())
        {
            // adding new element in the groupLeafElementsMap if the element is leaf element
            Set<E> leafElementsForGroup = groupLeafElementsMap.get(groupKey);
            if (leafElementsForGroup == null)
            {
                leafElementsForGroup = createSetCollection(INITIAL_CAPACITY_OF_LEAF_ELEMENTS_SET);
                groupLeafElementsMap.put(groupKey, leafElementsForGroup);
            }
            leafElementsForGroup.add(groupElement);
        }
        else if (groupElement.isGroup())
        {
            // finding the subgroup for the parent group and adding the new element as subgroup to the
            // existing subgroup set and adding it to the subGroupsMap
            Set<E> subgroups = subGroupsMap.get(groupKey);
            if (subgroups == null)
            {
                subgroups = createSetCollection(INITIAL_CAPACITY_OF_GROUP_RELATIONSHIP_SET);
                subGroupsMap.put(groupKey, subgroups);
            }
            subgroups.add(groupElement);
        }
    }

    /**
     * Removes the element from the cache. This method is used during normal operations by user.
     * Method obtains the row level lock while removing the element.
     *
     * @param groupKey           group key from which the element to be removed.
     * @param removeGroupElement element that needs to be removed from the group.
     */
    public void removeElementFromGroup(long groupKey, E removeGroupElement)
    {
        Object groupLockObject = getElementLockObject(groupKey);

        synchronized (groupLockObject)
        {

            boolean removedPermanently;
            // removing group element in the parentGroupsMap, obtaining lock of the element to update parents
            Object elementLockObject = getElementLockObject(removeGroupElement.getElementKey());

            synchronized (elementLockObject)
            {
                // remove the parent relationship for element
                removedPermanently = removeParentRelationship(removeGroupElement, groupKey);
            }

            removeRelationship(groupKey, removeGroupElement);

            // removing the element permanently from the cache since this if this the last parent
            // for the leaf element, has no more parents
            if (removedPermanently)
            {
                removeElement(removeGroupElement);
            }

        }
    }

    /**
     * Removes the element from the cache. This method is used during cache initialization.
     * Method doesn not obtain the row level lock while removing the element.
     *
     * @param groupKey           group key from which the element to be removed.
     * @param removeGroupElement element that needs to be removed from the group.
     */
    protected void removeElementFromGroupWithoutLock(long groupKey, E removeGroupElement)
    {
        // remove the parent relationship for element
        boolean removedPermanently = removeParentRelationship(removeGroupElement, groupKey);

        // remove all relationship for group
        removeRelationship(groupKey, removeGroupElement);

        // removing the element permanently from the cache since this if this the last parent
        // for the leaf element, has no more parents
        if (removedPermanently)
        {
            removeElement(removeGroupElement);
        }

    }

    /**
     * Remove element from the maps.
     *
     * @param removeGroupElement element that needs to be removed from the group.
     */
    protected void removeElement(E removeGroupElement)
    {
        // removing elemnt from the uniqueElementByHashCodeMap
        Set<E> elements = uniqueElementByHashCodeMap.get(uniqueSearchHashCode(removeGroupElement));
        if (elements != null)
        {
            elements.remove(removeGroupElement);
        }
        // removing element from the elementsMap
        elementsMap.remove(removeGroupElement.getElementKey());

        // removing element lock
        synchronized (elementLocks)
        {
            elementLocks.remove(removeGroupElement.getElementKey());
        }
    }

    /**
     * Method to remove relationship from the relationship maps.
     *
     * @param groupKey           group key from which the element to be removed.
     * @param removeGroupElement element that needs to be removed from the group.
     */
    protected void removeRelationship(long groupKey, E removeGroupElement)

    {

        // removing group element in the groupElementsMap
        Set elementsForGroup = groupElementsMap.get(groupKey);
        if (elementsForGroup != null)
        {
            elementsForGroup.remove(removeGroupElement);
        }

        if (removeGroupElement.isLeaf())
        {
            // updating relationship maps cheking if the new element is leaf or not
            // removing element in the groupLeafElementsMap if the element is leaf element
            Set leafElementsForGroup = groupLeafElementsMap.get(groupKey);
            if (leafElementsForGroup != null)
            {
                leafElementsForGroup.remove(removeGroupElement);
            }
        }
        else if (removeGroupElement.isGroup())
        {
            // finding the subgroup for the parent group and removing element from subgroup to the
            // existing subgroup set and removing it from the subGroupsMap
            Set subgroups = subGroupsMap.get(groupKey);
            if (subgroups != null)
            {
                subgroups.remove(removeGroupElement);
            }
        }
    }

    /**
     * Remove parent relationship and return true for removing permanently
     * if the element doesn't have any other relationship and no children for group element.
     *
     * @param removeGroupElement element that needs to be removed from the group.
     * @param groupKey           group key from which the element to be removed.
     * @return true if element to be removed permanently.
     */
    protected boolean removeParentRelationship(E removeGroupElement, long groupKey)

    {
        boolean removedPermanently = false;
        Set parentGroups = parentGroupsMap.get(removeGroupElement.getElementKey());
        // removing the element permanently if this is the last relationship
        // check for gropu having child is not done assuming validations are done by the caller
        if (parentGroups != null)
        {
            if (parentGroups.size() == 1 && parentGroups.contains(elementsMap.get(groupKey)))
            {
                removedPermanently = true;
            }
            parentGroups.remove(getGroupElementByElementKey(groupKey));
        }
        return removedPermanently;
    }

    /**
     * Adds a new root element in the cache. New root element creates a new cloud type.
     * If root element already exist for the same cloud/group type it replaces with the new root.
     *
     * @param rootElement root element to be added in the cache.
     */
    public void addRootElement(E rootElement)
    {
        // calling private method createElement which doesn the job in system.
        createElement(rootElement);
    }
}
