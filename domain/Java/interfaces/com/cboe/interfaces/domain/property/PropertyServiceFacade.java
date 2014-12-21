//
// -----------------------------------------------------------------------------------
// Source file: PropertyServiceFacade.java
//
// PACKAGE: com.cboe.interfaces.domain.property;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.property;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;

import com.cboe.util.event.EventChannelListener;

public interface PropertyServiceFacade
{
    /**
     * Get the property service property group.
     * @param category of the group to get.
     * @param key for the category to get.
     */
    PropertyServicePropertyGroup getPropertyGroup(String category, String key)
            throws SystemException, AuthorizationException, CommunicationException, NotFoundException;

    /**
     * Retrieves all properties associated with a certain category and property key. The propertyKey will be considered
     * a partial key.
     * @param category of properties
     * @param partialKey to use for partial search
     * @param partialKeySearchType defines how to use the partialKey for a particular search type
     */
    PropertyServicePropertyGroup[] getPropertyGroupsForPartialKey(String category, String partialKey,
                                                                  short partialKeySearchType)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Save a full property group.
     * @param propertyGroup to save
     * @return A refreshed copy of the property group
     */
    PropertyServicePropertyGroup savePropertyGroup(PropertyServicePropertyGroup propertyGroup)
            throws SystemException, AuthorizationException, CommunicationException;

    /**
     * Remove a full property group.
     * @param category of the group to remove.
     * @param key in the category of the group to remove.
     */
    void removePropertyGroup(String category, String key)
            throws SystemException, AuthorizationException, CommunicationException;

    /**
     * Subscribe to property change events.
     * The event delivery implementation for this method MUST support delivering the events in the order that
     * subscribers were registered. In some cases, this means that a FIFO collection must be used. The first
     * subscriber must be the first one to receive the event. The events should NOT be delivered asynchronously.
     * An event delivery to a subscriber should complete before the next subscriber receives the event.
     * @param category to subscribe to events for.
     * @param listener object for sending events to.
     */
    void subscribe(String category, EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Subscribe to property change events.
     * The event delivery implementation for this method MUST support delivering the events in the order that
     * subscribers were registered. In some cases, this means that a FIFO collection must be used. The first
     * subscriber must be the first one to receive the event. The events should NOT be delivered asynchronously.
     * An event delivery to a subscriber should complete before the next subscriber receives the event.
     * @param category to subscribe to events for.
     * @param propertyKey to subscribe to events for.
     * @param listener object for sending events to.
     */
    void subscribe(String category, String propertyKey, EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Unsubscribe to property change events.
     * The event delivery implementation for this method MUST support delivering the events in the order that
     * subscribers were registered. In some cases, this means that a FIFO collection must be used. The first
     * subscriber must be the first one to receive the event. The events should NOT be delivered asynchronously.
     * An event delivery to a subscriber should complete before the next subscriber receives the event.
     * @param category to unsubscribe to events for.
     * @param listener object to unsubscribe, that will stop receiving events.
     */
    void unsubscribe(String category, EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Unsubscribe to property change events.
     * The event delivery implementation for this method MUST support delivering the events in the order that
     * subscribers were registered. In some cases, this means that a FIFO collection must be used. The first
     * subscriber must be the first one to receive the event. The events should NOT be delivered asynchronously.
     * An event delivery to a subscriber should complete before the next subscriber receives the event.
     * @param category to unsubscribe to events for.
     * @param propertyKey to unsubscribe to events for.
     * @param listener object to unsubscribe, that will stop receiving events.
     */
    void unsubscribe(String category, String propertyKey, EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
}
