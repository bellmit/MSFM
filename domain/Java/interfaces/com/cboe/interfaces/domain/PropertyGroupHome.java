package com.cboe.interfaces.domain;
import com.cboe.idl.property.*;
import com.cboe.exceptions.*;
import com.cboe.interfaces.domain.PropertyGroup;


/**
 * A manager for PropertyGroup instances.
 *
 * @author Rizwan Ebrahim
 */
public interface PropertyGroupHome
{
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "PropertyGroupHome";
    public static final int BYPASSED_VERSION_NUM = -1;

    public PropertyGroup create(PropertyGroupStruct newPropertyGroup) throws AlreadyExistsException, DataValidationException;

	/**
	 * Searches for all defined property groups
	 *
	 * @return all PropertyGroups
	 */
	public PropertyGroup[] findAll();

	/**
	 *
	 *
	 * @return all PropertyGroups
	 */
	public PropertyGroup[] findByCategory(String category) throws DataValidationException, SystemException, NotFoundException;

    /**
	 *
	 *
	 * @return all PropertyGroups
	 */
	public PropertyGroup findByCategoryPropertyKey(String category, String propertyKey) throws DataValidationException, SystemException , NotFoundException;

    /**
     * Searches for property groups with partial key lookup
     * @param category property category
     * @param propertyKey partial property key
     * @return all PropertyGroups
     */
    public PropertyGroup[] findByCategoryPartialPropertyKey(String category, String propertyKey) throws DataValidationException, SystemException, NotFoundException;

    /**
	 *
	 *
	 * @return all PropertyGroups
	 */
	public String[] findPropertyKeys(String category) throws DataValidationException, SystemException , NotFoundException;

	public void removePropertyGroup(String category, String propertyKey) throws DataValidationException, SystemException, NotFoundException;


    public PropertyGroup setProperties(PropertyGroupStruct propertyGroupStruct) throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException, DataValidationException;


    /**
     * Creates CORBA struct containing property group information sent to clients.
     *
     *
     */
    public PropertyGroupStruct toPropertyGroupStruct(PropertyGroup propertyGroup);



    
}
