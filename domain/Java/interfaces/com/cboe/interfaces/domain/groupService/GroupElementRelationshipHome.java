//
// -----------------------------------------------------------------------------------
// Source file: GroupElementRelationshipHome.java
//
// PACKAGE: com.cboe.interfaces.domain.groupService
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.groupService;

import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;

import java.util.List;

/**
 * A home interface to represent a relationship between a group and an element.
 *
 * @author Antony Jesuraj
 * @author Cherian Mathew - Refactoring and documentation
 */
public interface GroupElementRelationshipHome
{
    public static final String HOME_NAME = "GroupElementRelationshipHome";

    /**
     * Creates a new relationship between two elements
     *
     * @param groupElementKey - The key of the group to which the child relationship has to be created
     * @param childElementKey - The key of the child element for which the relationship has be to created
     * @return Returns the relationship details
     * @throws SystemException - If an error occurred while creating the relationship
     */
    public GroupElementRelationship create(long groupElementKey, long childElementKey) throws SystemException;

    /**
     * Finds the group relationship details for a given group element key
     *
     * @param groupElementKey - The key of the group
     * @return Returns the group relationship details for a given group element key
     * @throws SystemException - If an error occurred while performing the find operation
     */
    public List findElementsForGroup(long groupElementKey) throws SystemException;

    /**
     * Removes the relationship between two elements
     *
     * @param groupElementKey - The key of the group element
     * @param elementKey      - The key of the child element
     * @throws NotFoundException - If the elements are not found
     * @throws SystemException   - If an error occurred while removing the relationship
     */
    public void removeElementFromGroup(long groupElementKey, long elementKey) throws NotFoundException, SystemException;
}
