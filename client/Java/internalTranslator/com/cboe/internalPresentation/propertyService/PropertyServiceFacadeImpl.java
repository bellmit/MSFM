//
// -----------------------------------------------------------------------------------
// Source file: PropertyServiceFacadeImpl.java
//
// PACKAGE: com.cboe.internalPresentation.propertyService;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.propertyService;

import com.cboe.idl.property.PropertyGroupStruct;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.ExceptionDetails;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;

import com.cboe.interfaces.domain.property.PropertyServiceFacade;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;

import com.cboe.util.event.EventChannelListener;

import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.internalPresentation.api.SystemAdminAPIFactory;
import com.cboe.internalPresentation.common.logging.GUILoggerSABusinessProperty;

import com.cboe.domain.property.PropertyFactory;

/**
 * This class can be used for the client proxy to the property service.
 */
public class PropertyServiceFacadeImpl implements PropertyServiceFacade
{
    /**
     * Retrieves all properties associated with a certain category and property key. The propertyKey will be considered
     * a partial key.
     * @param category of properties
     * @param partialKey to use for partial search
     * @param partialKeySearchType defines how to use the partialKey for a particular search type
     */
    public PropertyServicePropertyGroup[] getPropertyGroupsForPartialKey(String category, String partialKey,
                                                                         short partialKeySearchType)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PROPERTY_SERVICE))
        {
            Object[] argObj = new Object[3];
            argObj[0] = category;
            argObj[1] = partialKey;
            argObj[2] = new Short(partialKeySearchType);

            GUILoggerHome.find().debug("PropertyServiceFacadeImpl::getPropertyGroupsForPartialKey",
                                       GUILoggerSABusinessProperty.PROPERTY_SERVICE, argObj);
        }

        PropertyGroupStruct[] structs = SystemAdminAPIFactory.find().getPropertiesForPartialKey(category,
                                                                                                partialKey,
                                                                                                partialKeySearchType);

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PROPERTY_SERVICE))
        {
            GUILoggerHome.find().debug("PropertyServiceFacadeImpl::getPropertyGroupsForPartialKey",
                                       GUILoggerSABusinessProperty.PROPERTY_SERVICE, structs);
        }

        PropertyServicePropertyGroup[] groups = new PropertyServicePropertyGroup[structs.length];
        for(int i = 0; i < structs.length; i++)
        {
            PropertyGroupStruct struct = structs[i];
            groups[i] = PropertyFactory.createPropertyGroup(struct);
        }

        return groups;
    }

    /**
     * Get the property service property group.
     * @param category of the group to get.
     * @param key for the category to get.
     */
    public PropertyServicePropertyGroup getPropertyGroup(String category, String key)
            throws SystemException, AuthorizationException, CommunicationException, NotFoundException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PROPERTY_SERVICE))
        {
            Object[] argObj = new Object[2];
            argObj[0] = category;
            argObj[1] = key;

            GUILoggerHome.find().debug("PropertyServiceFacadeImpl::getPropertyGroup",
                                       GUILoggerSABusinessProperty.PROPERTY_SERVICE, argObj);
        }

        PropertyGroupStruct struct = SystemAdminAPIFactory.find().getProperties(category,key);

        if (GUILoggerHome.find().isDebugOn() &&
            GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PROPERTY_SERVICE))
        {
            GUILoggerHome.find().debug("PropertyServiceFacadeImpl::getPropertyGroup",
                                       GUILoggerSABusinessProperty.PROPERTY_SERVICE, struct);
        }

        PropertyServicePropertyGroup group = PropertyFactory.createPropertyGroup(struct);

        return group;
    }

    /**
     * Save a full property group.
     * @param propertyGroup to save
     * @return A refreshed copy of the property group
     */
    public PropertyServicePropertyGroup savePropertyGroup(PropertyServicePropertyGroup propertyGroup)
            throws SystemException, AuthorizationException, CommunicationException
    {
        if(propertyGroup == null)
        {
            throw new SystemException("Property group may not be null.", new ExceptionDetails());
        }
        PropertyGroupStruct pgs = propertyGroup.getStruct();

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PROPERTY_SERVICE))
        {
            Object[] argObj = new Object[1];
            argObj[0] = pgs;

            GUILoggerHome.find().debug("PropertyServiceFacadeImpl::savePropertyGroup",
                                       GUILoggerSABusinessProperty.PROPERTY_SERVICE, argObj);
        }

        PropertyGroupStruct struct = SystemAdminAPIFactory.find().setProperties(pgs);

        if (GUILoggerHome.find().isDebugOn() &&
            GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PROPERTY_SERVICE))
        {
            GUILoggerHome.find().debug("PropertyServiceFacadeImpl::savePropertyGroup",
                                       GUILoggerSABusinessProperty.PROPERTY_SERVICE, struct);
        }

        PropertyServicePropertyGroup group = PropertyFactory.createPropertyGroup(struct);

        return group;
    }

    /**
     * Remove a full property group.
     * @param category of the group to remove.
     * @param key in the category of the group to remove.
     */
    public void removePropertyGroup(String category, String key)
            throws SystemException, AuthorizationException, CommunicationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PROPERTY_SERVICE))
        {
            Object[] argObj = new Object[2];
            argObj[0] = category;
            argObj[1] = key;

            GUILoggerHome.find().debug("PropertyServiceFacadeImpl::removePropertyGroup",
                                       GUILoggerSABusinessProperty.PROPERTY_SERVICE, argObj);
        }

        SystemAdminAPIFactory.find().removeProperties(category,key);
    }

    /**
     * Subscribe to property change events. The event delivery implementation for this method MUST support delivering
     * the events in the order that subscribers were registered. In some cases, this means that a FIFO collection must
     * be used. The first subscriber must be the first one to receive the event. The events should NOT be delivered
     * asynchronously. An event delivery to a subscriber should complete before the next subscriber receives the event.
     * Since this implementation will use the IEC, this requirement is satisified.
     * @param category to subscribe to events for.
     * @param listener object for sending events to.
     */
    public void subscribe(String category, EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PROPERTY_SERVICE))
        {
            Object[] argObj = new Object[2];
            argObj[0] = category;
            argObj[1] = listener;

            GUILoggerHome.find().debug("PropertyServiceFacadeImpl::subscribe",
                                       GUILoggerSABusinessProperty.PROPERTY_SERVICE, argObj);
        }

        SystemAdminAPIFactory.find().subscribePropertyService(category,listener);
    }

    /**
     * Unsubscribe to property change events. The event delivery implementation for this method MUST support delivering
     * the events in the order that subscribers were registered. In some cases, this means that a FIFO collection must
     * be used. The first subscriber must be the first one to receive the event. The events should NOT be delivered
     * asynchronously. An event delivery to a subscriber should complete before the next subscriber receives the event.
     * Since this implementation will use the IEC, this requirement is satisified.
     * @param category to unsubscribe to events for.
     * @param listener object to unsubscribe, that will stop receiving events.
     */
    public void unsubscribe(String category, EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PROPERTY_SERVICE))
        {
            Object[] argObj = new Object[2];
            argObj[0] = category;
            argObj[1] = listener;

            GUILoggerHome.find().debug("PropertyServiceFacadeImpl::unsubscribe",
                                       GUILoggerSABusinessProperty.PROPERTY_SERVICE, argObj);
        }

        SystemAdminAPIFactory.find().unsubscribePropertyService(category,listener);
    }

    /**
     * Subscribe to property change events. The event delivery implementation for this method MUST support delivering
     * the events in the order that subscribers were registered. In some cases, this means that a FIFO collection must
     * be used. The first subscriber must be the first one to receive the event. The events should NOT be delivered
     * asynchronously. An event delivery to a subscriber should complete before the next subscriber receives the event.
     * Since this implementation will use the IEC, this requirement is satisified.
     * @param category to subscribe to events for.
     * @param propertyKey to subscribe to events for.
     * @param listener object for sending events to.
     */
    public void subscribe(String category, String propertyKey, EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PROPERTY_SERVICE))
        {
            Object[] argObj = new Object[3];
            argObj[0] = category;
            argObj[1] = propertyKey;
            argObj[2] = listener;

            GUILoggerHome.find().debug("PropertyServiceFacadeImpl::subscribe",
                                       GUILoggerSABusinessProperty.PROPERTY_SERVICE, argObj);
        }

        SystemAdminAPIFactory.find().subscribePropertyService(category,propertyKey,listener);
    }

    /**
     * Unsubscribe to property change events. The event delivery implementation for this method MUST support delivering
     * the events in the order that subscribers were registered. In some cases, this means that a FIFO collection must
     * be used. The first subscriber must be the first one to receive the event. The events should NOT be delivered
     * asynchronously. An event delivery to a subscriber should complete before the next subscriber receives the event.
     * Since this implementation will use the IEC, this requirement is satisified.
     * @param category to unsubscribe to events for.
     * @param propertyKey to unsubscribe to events for.
     * @param listener object to unsubscribe, that will stop receiving events.
     */
    public void unsubscribe(String category, String propertyKey, EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PROPERTY_SERVICE))
        {
            Object[] argObj = new Object[3];
            argObj[0] = category;
            argObj[1] = propertyKey;
            argObj[2] = listener;

            GUILoggerHome.find().debug("PropertyServiceFacadeImpl::unsubscribe",
                                       GUILoggerSABusinessProperty.PROPERTY_SERVICE, argObj);
        }

        SystemAdminAPIFactory.find().unsubscribePropertyService(category,propertyKey,listener);
    }
}
