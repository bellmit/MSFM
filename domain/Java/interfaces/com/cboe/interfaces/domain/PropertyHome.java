package com.cboe.interfaces.domain;
import com.cboe.idl.property.*;
import com.cboe.exceptions.*;
import com.cboe.interfaces.domain.Property;


/**
 * A manager for Property instances.
 *
 * @author Rizwan Ebrahim
 */
public interface PropertyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "PropertyHome";


    public Property create(PropertyStruct newProperty) throws AlreadyExistsException, DataValidationException;

    public Property[] findAll();

    /**
     *
     *
     * @return all Property
     */
    public Property[] findByPropertyGroup(long propertyGroup) throws DataValidationException, SystemException,NotFoundException;

    /**
     *                   
     *
     * @return all PropertyGroups
     */
    public Property findByPropertyName(String propertyName) throws DataValidationException, SystemException, NotFoundException;
    
    /**
     *                   
     *
     * @return all PropertyGroups[]
     */
    public Property[] findByPartialPropertyName( String partialName, short partialQueryType )
        throws NotFoundException;
    
    /**
     * Creates CORBA struct containing property group information sent to clients.
     *
     *
     */
    public PropertyStruct toPropertyStruct(Property property);

    public String removeProperty(long propertyGroup)throws DataValidationException, SystemException, NotFoundException;

    public String setProperties(long propertyGroup, PropertyStruct[] propertyStruct) throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException, DataValidationException;


}
