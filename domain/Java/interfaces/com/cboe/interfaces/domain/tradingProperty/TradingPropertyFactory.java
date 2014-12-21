//
// -----------------------------------------------------------------------------------
// Source file: TradingPropertyFactory.java
//
// PACKAGE: com.cboe.interfaces.domain.tradingProperty;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.tradingProperty;

import java.lang.reflect.InvocationTargetException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.util.event.EventChannelListener;

/**
 * Defines a contract to provide factory methods for obtaining and creating TradingPropertyGroup's and
 * TradingProperty's.
 */
public interface TradingPropertyFactory
{
    /**
     * Subscribes the listener to events for the TradingPropertyGroup identified.
     * @param sessionName of TradingPropertyGroup to subscribe to
     * @param classKey of TradingPropertyGroup to subscribe to
     * @param tradingPropertyName of TradingPropertyGroup to subscribe to
     * @param listener to subscribe
     */
    void subscribe(String sessionName, int classKey, String tradingPropertyName, EventChannelListener listener)
            throws SystemException, DataValidationException, CommunicationException, AuthorizationException;

    /**
     * Unsubscribes a listener to events for the TradingPropertyGroup identified.
     * @param sessionName of TradingPropertyGroup to unsubscribe to
     * @param classKey of TradingPropertyGroup to unsubscribe to
     * @param tradingPropertyName of TradingPropertyGroup to unsubscribe to
     * @param listener to unsubscribe
     */
    void unsubscribe(String sessionName, int classKey, String tradingPropertyName, EventChannelListener listener)
            throws SystemException, DataValidationException, CommunicationException, AuthorizationException;

    /**
     * Provides for the creation of a new trading property group that did not exist before.
     * @param sessionName to build group for
     * @param classKey to build group for
     * @param tradingPropertyName to build group for
     * @return an appropriate implementation of a new instance of a TradingPropertyGroup based on the values passed.
     * The intention would be that this TradingPropertyGroup was not obtained from the host source.
     * @throws DataValidationException should be thrown if tradingPropertyName is not a known type.
     * @throws InvocationTargetException If any exception occurs from instantiating the appropriate domain wrapper
     *                                   object, it is returned as the cause in this exception.
     */
    TradingPropertyGroup createNewTradingPropertyGroup(String sessionName, int classKey, String tradingPropertyName)
            throws DataValidationException, InvocationTargetException;

    /**
     * Gets the TradingPropertyGroup for a specific sessionName, classKey and tradingPropertyName.
     * @param sessionName         to get group for
     * @param classKey            to get group for
     * @param tradingPropertyName of specific group to get
     * @return the appropriate implementation instance of a TradingPropertyGroup based on the values passed
     * @throws InvocationTargetException If any exception occurs from instantiating the appropriate domain wrapper
     *                                   object, it is returned as the cause in this exception.
     * @throws DataValidationException   This exception is forwarded from the TradingPropertyGroup.setPropertyGroup
     *                                   interface. It will probably indicate that the raw data used to create the
     *                                   TradingPropertyGroup was not of a valid format. Could also be thrown if
     *                                   tradingPropertyName is not a known type.
     * @throws SystemException           forwarded from PropertyServiceFacadeHome
     * @throws NotFoundException         forwarded from PropertyServiceFacadeHome
     * @throws CommunicationException    forwarded from PropertyServiceFacadeHome
     * @throws AuthorizationException    forwarded from PropertyServiceFacadeHome
     */
    TradingPropertyGroup getTradingPropertyGroup(String sessionName, int classKey, String tradingPropertyName)
        throws InvocationTargetException, DataValidationException, SystemException, NotFoundException,
               CommunicationException, AuthorizationException, TransactionFailedException;

   /**
    * Gets the TradingPropertyGroup for a specific sessionName, classKey and tradingPropertyName.
    * This method is exposed to avoid a behavioral bug. Do Not use this method in TradingPropertyGroup.getGroup() implementation.
    * @param sessionName         to get group for
    * @param classKey            to get group for
    * @param tradingPropertyName of specific group to get
    * @param withDefault designates if this implementation should query for the default class key, if the specified
    * class key was not found and itself was not the default class key. True if it should, false to just return exception.
    * @return the appropriate implementation instance of a TradingPropertyGroup based on the values passed
    */
    TradingPropertyGroup getTradingPropertyGroup(   String sessionName, int classKey,
                                                    String tradingPropertyName, boolean withDefault)
            throws InvocationTargetException, DataValidationException, SystemException, NotFoundException,
                   CommunicationException, AuthorizationException, TransactionFailedException;

    /**
     * Gets all the TradingPropertyGroup's for a specific sessionName and tradingPropertyName, for all classes.
     * @param sessionName         to get groups for
     * @param tradingPropertyName of specific groups to get
     * @return an array of the appropriate implementation instance's of a TradingPropertyGroup based on the values
     *         passed. They will be all of the same type.
     * @throws InvocationTargetException If any exception occurs from instantiating the appropriate domain wrapper
     *                                   object, it is returned as the cause in this exception.
     * @throws DataValidationException   This exception is forwarded from the TradingPropertyGroup.setPropertyGroup
     *                                   interface. It will probably indicate that the raw data used to create the
     *                                   TradingPropertyGroup was not of a valid format. Could also be thrown if
     *                                   tradingPropertyName is not a known type.
     * @throws SystemException           forwarded from PropertyServiceFacadeHome
     * @throws CommunicationException    forwarded from PropertyServiceFacadeHome
     * @throws AuthorizationException    forwarded from PropertyServiceFacadeHome
     */
    TradingPropertyGroup[] getTradingPropertyGroupsForAllClasses(String sessionName, String tradingPropertyName)
        throws InvocationTargetException, DataValidationException, SystemException, CommunicationException,
               AuthorizationException, TransactionFailedException;

    /**
     * Gets all the TradingPropertyGroup's for a specific sessionName and classKey.
     * @param sessionName to get groups for
     * @param classKey    to get groups for
     * @return an array of the appropriate implementation instances of TradingPropertyGroup's based on the values
     *         passed.
     * @throws InvocationTargetException If any exception occurs from instantiating the appropriate domain wrapper
     *                                   object, it is returned as the cause in this exception.
     * @throws DataValidationException   This exception is forwarded from the TradingPropertyGroup.setPropertyGroup
     *                                   interface. It will probably indicate that the raw data used to create the
     *                                   TradingPropertyGroup's was not of a valid format.
     * @throws SystemException           forwarded from PropertyServiceFacadeHome
     * @throws CommunicationException    forwarded from PropertyServiceFacadeHome
     * @throws AuthorizationException    forwarded from PropertyServiceFacadeHome
     */
    TradingPropertyGroup[] getAllTradingPropertyGroups(String sessionName, int classKey)
            throws InvocationTargetException, DataValidationException, SystemException, CommunicationException,
                   AuthorizationException, TransactionFailedException;

    /**
     * Gets all the TradingProperty's from the TradingPropertyGroup obtained for a specific category, sessionName,
     * classKey and tradingPropertyName.
     * @param sessionName         to get group for
     * @param classKey            to get group for
     * @param tradingPropertyName of specific group to get
     * @return an array of all the TradingProperty's from the appropriate implementation instance of a
     *         TradingPropertyGroup based on the values passed. WARNING! WITHOUT THE TradingPropertyGroup THESE WILL NOT
     *         BE ABLE TO BE SAVED, IF MODIFIED.
     * @throws InvocationTargetException If any exception occurs from instantiating the appropriate domain wrapper
     *                                   object, it is returned as the cause in this exception.
     * @throws DataValidationException   This exception is forwarded from the TradingPropertyGroup.setPropertyGroup
     *                                   interface. It will probably indicate that the Property's from the PropertyGroup
     *                                   or the PropertyGroup itself was not of a valid format. Could also be thrown if
     *                                   tradingPropertyName is not a known type.
     * @throws SystemException           forwarded from PropertyServiceFacadeHome
     * @throws CommunicationException    forwarded from PropertyServiceFacadeHome
     * @throws AuthorizationException    forwarded from PropertyServiceFacadeHome
     * @throws NotFoundException The TradingPropertyGroup did not exist to get TradingProperty's for.
     */
    TradingProperty[] getAllTradingProperties(String sessionName, int classKey, String tradingPropertyName)
            throws InvocationTargetException, DataValidationException, SystemException, CommunicationException,
                   AuthorizationException, NotFoundException, TransactionFailedException;

    /**
     * Gets all the TradingProperty's from all the TradingPropertyGroup's obtained for a specific sessionName and
     * classKey.
     * @param sessionName to get groups for
     * @param classKey    to get groups for
     * @return an array of all the TradingProperty's from all the appropriate implementation instances of
     *         TradingPropertyGroup's based on the values passed. WARNING! WITHOUT THE TradingPropertyGroup THESE WILL
     *         NOT BE ABLE TO BE SAVED, IF MODIFIED.
     * @throws InvocationTargetException If any exception occurs from instantiating the appropriate domain wrapper
     *                                   object, it is returned as the cause in this exception.
     * @throws DataValidationException   This exception is forwarded from the TradingPropertyGroup.setPropertyGroup
     *                                   interface. It will probably indicate that the Property's from the PropertyGroup
     *                                   or the PropertyGroup itself was not of a valid format.
     * @throws SystemException           forwarded from PropertyServiceFacadeHome
     * @throws CommunicationException    forwarded from PropertyServiceFacadeHome
     * @throws AuthorizationException    forwarded from PropertyServiceFacadeHome
     */
    TradingProperty[] getAllTradingProperties(String sessionName, int classKey)
            throws InvocationTargetException, DataValidationException, SystemException, CommunicationException,
                   AuthorizationException, TransactionFailedException;

    /**
     * Converts the integer of a property type to a String as defined by the TradingPropertyTypes
     * @param propertyType to convert
     * @return String representation
     */
    String getPropertyName(int propertyType);

    /**
     * Converts the String name of a property type to an integer as defined by the TradingPropertyTypes
     * @param propertyName to convert
     * @return int representation
     */
    int getPropertyType(String propertyName);

    /**
     * Converts the String name of a property type to an integer as defined by the TradingPropertyTypes
     * @param allClassesPropertyName (in xxxAllClasses format) to convert
     * @return int representation
     */
    int getPropertyTypeAllClassesName(String allClassesPropertyName);

    /**
     * Will build the appropriate String key to use for the property service propertyKey, combined from the passed
     * arguments. This method is meant for class key based queries.
     */
    String buildTradingPropertyKey(String sessionName, int classKey, String tradingPropertyName);

    /**
     * Will build the appropriate String key to use for the property service propertyKey, combined from the passed
     * arguments. This method is meant for class key based queries.
     */
    String buildTradingPropertyKey(TradingPropertyGroup group);

    /**
     * Will build the appropriate String key to use for the property service propertyKey, combined from the passed
     * arguments. This method is meant for queries that are not class key based or for queries for all classes.
     */
    String buildTradingPropertyKey(String sessionName, String tradingPropertyName);

    /**
     * Will find the tradingPropertyName parsed from the passed fully qualified tradingPropertyKey.
     * @param tradingPropertyKey to parse
     * @return tradingPropertyName that the passed tradingPropertyKey represents
     */
    String getTradingPropertyKeyPropertyName(String tradingPropertyKey);

    /**
     * Will find the sessionName parsed from the passed fully qualified tradingPropertyKey.
     * @param tradingPropertyKey to parse
     * @return sessionName that the passed tradingPropertyKey represents
     */
    String getTradingPropertyKeySessionName(String tradingPropertyKey);

    /**
     * Will find the classKey parsed from the passed fully qualified tradingPropertyKey.
     * @param tradingPropertyKey to parse
     * @return classKey that the passed tradingPropertyKey represents
     */
    int getTradingPropertyKeyClassKey(String tradingPropertyKey);

    /**
     * Will determine the number of elements in the passed fully qualified tradingPropertyKey.
     * @param tradingPropertyKey to parse for the number of elements
     * @return number of parsed elements
     */
    int getTradingPropertyKeyParsedLen(String tradingPropertyKey);
}
