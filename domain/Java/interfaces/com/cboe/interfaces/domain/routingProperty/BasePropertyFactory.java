package com.cboe.interfaces.domain.routingProperty;
// -----------------------------------------------------------------------------------
// Source file: BasePropertyFactory
//
// PACKAGE: com.cboe.interfaces.domain.routingProperty
// 
// Created: Jun 20, 2006 2:31:54 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.lang.reflect.InvocationTargetException;

import org.omg.CORBA.UserException;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.util.event.EventChannelListener;

public interface BasePropertyFactory
{

    /**
     * Attempts to find a previously created FirmRoutingPropertyClassType keyed by the passed propertyName.
     *
     * @param propertyName to find BasePropertyClassType for
     * @return found BasePropertyClassType for passed propertyName
     * @throws DataValidationException will be thrown if BasePropertyClassType could not be found for passed
     *                                 propertyName
     */
    BasePropertyType findPropertyType(String propertyName) throws DataValidationException;

    /**
     * Subscribes the listener to events for the BasePropertyGroup identified.
     *
     * @param basePropertyKey of the BasePropertyGroup to subscribe to
     * @param listener        to subscribe
     */
    void subscribe(BasePropertyKey basePropertyKey, EventChannelListener listener)
            throws SystemException, DataValidationException, CommunicationException, AuthorizationException;

    /**
     * Unsubscribes a listener to events for the BasePropertyGroup identified.
     *
     * @param basePropertyKey of the BasePropertyGroup to unsubscribe to
     * @param listener        to unsubscribe
     */
    void unsubscribe(BasePropertyKey basePropertyKey, EventChannelListener listener)
            throws SystemException, DataValidationException, CommunicationException, AuthorizationException;

    void subscribeForAll(EventChannelListener listener)
            throws SystemException, DataValidationException, CommunicationException, AuthorizationException;

    void unsubscribeForAll(EventChannelListener listener)
            throws SystemException, DataValidationException, CommunicationException, AuthorizationException;

    BasePropertyGroup createNewPropertyGroup(BasePropertyKey key)
            throws DataValidationException, InvocationTargetException;

    BasePropertyKey buildKey(BasePropertyType type, String[] arguments) throws InvocationTargetException;

    BasePropertyKey buildKey(BasePropertyType type, String propertyKey) throws InvocationTargetException;

    BasePropertyKey buildKey(String propertyKey) throws InvocationTargetException, DataValidationException;

    MutableBasePropertyKey buildKey(BasePropertyType type) throws InvocationTargetException;

    /**
     * This method will return a Partial Routing Key
     *
     * @return PartialPropertyKey
     */
    BasePropertyKey buildKey(String[] arguments);

    BasePropertyGroup[] getAllPropertyGroupsForFirm(String sessionName, String firmAcronym, String exchangeArconym)
            throws InvocationTargetException, DataValidationException, SystemException, NotFoundException,
            CommunicationException, AuthorizationException, TransactionFailedException;

    BasePropertyGroup[] getAllPropertyGroupsForClass(String sessionName, String className)
            throws InvocationTargetException, DataValidationException, SystemException, NotFoundException,
            CommunicationException, AuthorizationException, TransactionFailedException;

    BasePropertyGroup[] getAllPropertyGroupsForSession(String sessionName)
            throws InvocationTargetException, DataValidationException, SystemException, NotFoundException,
            CommunicationException, AuthorizationException, TransactionFailedException;

    BasePropertyGroup[] getAllRoutingPropertyGroupsForKey(String key)
            throws InvocationTargetException, DataValidationException, SystemException, NotFoundException,
            CommunicationException, AuthorizationException, TransactionFailedException;

    BasePropertyGroup getPropertyGroup(BasePropertyKey key)
            throws InvocationTargetException, DataValidationException, SystemException, NotFoundException,
            CommunicationException, AuthorizationException, TransactionFailedException;

    BasePropertyGroup[] copyPropertiesToFirm(BasePropertyGroup[] groups, String firmNumber, String exchange)
            throws CloneNotSupportedException, DataValidationException, InvocationTargetException;

    void copyFirmProperties(BasePropertyGroup[] fromGroups, String session, ExchangeFirmStruct toFirm)
            throws UserException, InvocationTargetException, CloneNotSupportedException;

    void copyClassProperties(BasePropertyGroup[] fromGroups, String session, int toClass)
            throws UserException, InvocationTargetException, CloneNotSupportedException;

    void copyPostStationProperties(BasePropertyGroup[] fromGroups, String session, int toPost, int toStation)
            throws UserException, InvocationTargetException, CloneNotSupportedException;
}
