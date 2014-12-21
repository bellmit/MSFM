//
// -----------------------------------------------------------------------------------
// Source file: TradingPropertyGroup.java
//
// PACKAGE: com.cboe.interfaces.domain.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.tradingProperty;

import java.beans.IntrospectionException;
import java.beans.PropertyChangeListener;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;

import com.cboe.util.event.EventChannelListener;

/**
 * Defines a group that contains TradingProperty's.
 */
public interface TradingPropertyGroup extends Cloneable
{
    int INFINITE_TRADING_PROPERTIES_ALLOWED = 0;

    String PROPERTY_GROUP_CHANGE_EVENT = "PropertyGroup";
    String TRADING_PROPERTY_CHANGE_EVENT = "TradingProperties";

    Object clone() throws CloneNotSupportedException;

    /**
     * Provides the maximum number of Trading Properties a particular group implementation may allow.
     * @return the maximum number of unique Trading Properties a group implementation may allow. The implementation
     * should return INFINITE_TRADING_PROPERTIES_ALLOWED, if a maximum is not enforced.
     */
    int getMaxTradingPropertiesAllowed();

    /**
     * Registers a listener to be informed whenever changes to this TradingPropertyGroup occur. These callbacks are
     * different than EventChannelListener for the subscribe method. The PropertyChangeListener will be informed
     * of internal changes, not reflective of an event channel event.
     * @param listener to register
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Removes a previous registration.
     * @param listener
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Provides immutable getter for Trading Session Name
     * @return Trading Session Name that this TradingProperty is for
     */
    String getSessionName();

    /**
     * Provides immutable getter for class key
     * @return class key that this TradingProperty is for
     */
    int getClassKey();

    /**
     * Provides immutable getter for version number
     * @return version number that this TradingProperty is for
     */
    int getVersionNumber();

    /**
     * Provides the setter for the conversion of the PropertyServicePropertyGroup, representing a PropertyGroupStruct,
     * to this TradingPropertyGroup. The PropertyServicePropertyGroup, representing a PropertyGroupStruct, is the
     * transport mechanism used to move TradingPropertyGroup's across CORBA, using the PropertyService IDL. This set
     * and the corresponding get, act as the translation mechanism for transport across CORBA.
     * @param propertyGroup to translate the TradingPropertyGroup values from
     * @throws DataValidationException is thrown when the passed PropertyServicePropertyGroup does not reflect the
     * Trading Properties appropriately for the implementing TradingPropertyGroup.
     */
    void setPropertyGroup(PropertyServicePropertyGroup propertyGroup) throws DataValidationException;

    /**
     * Provides the getter for the conversion of this TradingPropertyGroup to a PropertyServicePropertyGroup,
     * representing a PropertyGroupStruct. The PropertyServicePropertyGroup, representing a PropertyGroupStruct, is
     * the transport mechanism used to move TradingPropertyGroup's across CORBA, using the PropertyService IDL.
     * This get and the corresponding set, act as the translation mechanism for transport across CORBA.
     * @return PropertyServicePropertyGroup that represents the value from the implementing TradingPropertyGroup.
     */
    PropertyServicePropertyGroup getPropertyGroup();

    /**
     * Saves this TradingPropertyGroup to the persistence.
     * @throws SystemException forwarded from the save API
     * @throws CommunicationException forwarded from the save API
     * @throws AuthorizationException forwarded from the save API
     * @throws DataValidationException forwarded from the save API
     * @throws TransactionFailedException forwarded from the save API
     */
    void save()
            throws SystemException, CommunicationException, AuthorizationException,
                   DataValidationException, TransactionFailedException, NotFoundException;

    /**
     * Deletes this TradingPropertyGroup from the persistence.
     * @throws SystemException forwarded from the remove API
     * @throws CommunicationException forwarded from the remove API
     * @throws AuthorizationException forwarded from the remove API
     * @throws DataValidationException forwarded from the remove API
     * @throws TransactionFailedException forwarded from the remove API
     */
    void delete()
            throws SystemException, CommunicationException, AuthorizationException,
                   DataValidationException, TransactionFailedException, NotFoundException;

    /**
     * Gets all the implementation specific TradingProperty's for this group.
     */
    TradingProperty[] getAllTradingProperties();

    /**
     * Create a new implementation specific TradingProperty.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @return new TradingProperty
     */
    TradingProperty createNewTradingProperty(String sessionName, int classKey);

    /**
     * Adds a TradingProperty to this group. Implementations are responsible for cardinality and equality to existing
     * TradingProperty's.
     * @param tradingProperty to add
     */
    void addTradingProperty(TradingProperty tradingProperty);

    /**
     * Removes a TradingProperty from this group. Implementations are responsible for cardinality and equality to
     * existing TradingProperty's.
     * @param tradingProperty to remove
     * @return TradingProperty removed
     */
    TradingProperty removeTradingProperty(TradingProperty tradingProperty);

    /**
     * Updates a TradingProperty in this group. Implementations are responsible for cardinality and equality to
     * existing TradingProperty's.
     * @param tradingProperty to update with
     * @return TradingProperty that was replaced with the updated one.
     */
    TradingProperty updateTradingProperty(TradingProperty tradingProperty);

    /**
     * Gets the TradingPropertyType for this group that identifies the type of this group.
     */
    TradingPropertyType getTradingPropertyType();

    /**
     * Gets the Class that implements the GUI representation of this group.
     * @return May return null if this group does not support its own customizer.
     * @exception IntrospectionException that could be returned from the Introspector
     */
    Class getCustomizerClass()
            throws IntrospectionException;

    /**
     * Subscribe an EventChannelListener to update/remove events on the is property group
     */
    void subscribe(EventChannelListener listener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Unsubscribe an EventChannelListener to update/remove events on the is property group
     */
    void unsubscribe(EventChannelListener listener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
}