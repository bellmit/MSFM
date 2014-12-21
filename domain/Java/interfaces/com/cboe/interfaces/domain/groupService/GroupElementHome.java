package com.cboe.interfaces.domain.groupService;

import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.groupElement.ElementEntryStruct;

import java.util.Collection;

/**
 * GroupElement Home Interface. Provides CRUD on GroupElement.
 *
 * @author Antony Jesuraj
 * @author Cherian Mathew - Refactoring and documentation
 */

public interface GroupElementHome
{
    public static final String HOME_NAME = "GroupElementHome";

    /**
     * Creates an new element
     *
     * @param struct - The struct containing all the attributes of the element to be created
     * @return Returns The newly created element
     * @throws SystemException - If any error occurred while creating the new element
     */
    public GroupElement create(ElementEntryStruct struct) throws SystemException;

    /**
     * Finds an element with a given element key
     *
     * @param elementKey - The key of the element to find
     * @return Returns the element if found
     * @throws NotFoundException - If an element with the key was not found
     * @throws SystemException   - If an exception occurred while performing the find operation
     */
    public GroupElement findElementByKey(long elementKey) throws NotFoundException, SystemException;

    /**
     * Finds all the groups and details
     *
     * @return Returns all the groups and details
     * @throws SystemException - If an exception occurred while performing the find operation
     */
    public Collection findAll() throws SystemException;

    /**
     * Find the group details for a given <code>elementDataType</code>
     *
     * @param elementDataType - The dataype of the group
     * @return Returns the details of the group
     * @throws SystemException   - If an exception occurred while performing the find operation
     */
    public Collection findAllByDataType(short elementDataType) throws SystemException;

    /**
     * Removes an element permanently
     *
     * @param elementKey - The key of the element to be removed
     * @throws NotFoundException - If the element with the given key doesn't exists
     * @throws SystemException   - If an exception occurred while performing the remove operation
     */
    public void remove(long elementKey) throws NotFoundException, SystemException;
}
