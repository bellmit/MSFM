//
// -----------------------------------------------------------------------------------
// Source file: CacheUpdateCallbackConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.internalPresentation;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.internalPresentation;

import com.cboe.idl.cmiUser.SessionProfileUserStruct;
import com.cboe.idl.cmiUser.UserStruct;
import com.cboe.idl.firm.FirmStruct;
import com.cboe.idl.user.SessionProfileUserDefinitionStruct;
import com.cboe.idl.user.UserDefinitionStruct;
import com.cboe.idl.user.UserEnablementStruct;
import com.cboe.idl.user.UserFirmAffiliationStruct;
import com.cboe.idl.user.UserSummaryStruct;

import com.cboe.interfaces.callback.CacheUpdateCallbackConsumer;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;

import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.internalPresentation.common.logging.GUILoggerSABusinessProperty;
import com.cboe.internalPresentation.user.UserFirmAffiliationFactory;

/**
 * This is the implementation of the consumer for user events.
 */
public class CacheUpdateCallbackConsumerImpl implements CacheUpdateCallbackConsumer
{
    private EventChannelAdapter eventChannel = null;

    public CacheUpdateCallbackConsumerImpl(EventChannelAdapter eventChannel)
    {
        this.eventChannel = eventChannel;
    }

    public void acceptUserUpdate(UserStruct updatedUser,
                                 UserDefinitionStruct updatedUserDefinition,
                                 UserEnablementStruct updatedUserEnablement)
    {
        Object[] args = {updatedUser, updatedUserDefinition, updatedUserEnablement};

        GUILoggerHome.find().alarm(getClass().getName() + ":acceptUserUpdate method call is invalid. " +
                                   "Incorrect version to call.", args);
    }

    public void acceptSessionProfileUserUpdate(SessionProfileUserStruct updatedUser,
                                               SessionProfileUserDefinitionStruct updatedUserDefinition,
                                               UserEnablementStruct updatedUserEnablement)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
        {
            Object[] args = {updatedUser, updatedUserDefinition, updatedUserEnablement};

            GUILoggerHome.find().debug(getClass().getName() + ":acceptSessionProfileUserUpdate",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT,
                                       args);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_USER_EVENT_ADD_USER, new Integer(0));
        ChannelEvent event = eventChannel.getChannelEvent(this, key, updatedUserDefinition);
        eventChannel.dispatch(event);
    }

    public void acceptUserDeletion(UserSummaryStruct userSummary)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
        {
            Object[] args = {userSummary};

            GUILoggerHome.find().debug(getClass().getName() + ":acceptUserDeletion",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT,
                                       args);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_USER_EVENT_DELETE_USER, new Integer(0));
        ChannelEvent event = eventChannel.getChannelEvent(this, key, userSummary);
        eventChannel.dispatch(event);
    }

    public void acceptFirmUpdate(FirmStruct updateFirm)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.FIRM_MAINTENANCE))
        {
            Object[] args = {updateFirm};

            GUILoggerHome.find().debug(getClass().getName() + ":acceptFirmUpdate",
                                       GUILoggerSABusinessProperty.FIRM_MAINTENANCE,
                                       args);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_USER_EVENT_ADD_FIRM, new Integer(0));
        ChannelEvent event = eventChannel.getChannelEvent(this, key, updateFirm);
        eventChannel.dispatch(event);
    }

    public void acceptFirmDeletion(FirmStruct deletedFirm)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.FIRM_MAINTENANCE))
        {
            Object[] args = {deletedFirm};

            GUILoggerHome.find().debug(getClass().getName() + ":acceptFirmDeletion",
                                       GUILoggerSABusinessProperty.FIRM_MAINTENANCE,
                                       args);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_USER_EVENT_DELETE_FIRM, new Integer(0));
        ChannelEvent event = eventChannel.getChannelEvent(this, key, deletedFirm);
        eventChannel.dispatch(event);
    }

    /**
     * The callback method used by the SACAS to publish firm affiliation updates
     * @param userFirmAffiliationStruct
     */
    public void acceptUserFirmAffiliationDelete(UserFirmAffiliationStruct userFirmAffiliationStruct)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
        {
            GUILoggerHome.find().debug(getClass().getName() + ":acceptUserFirmAffiliationDelete()",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, userFirmAffiliationStruct);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_USER_EVENT_USER_FIRM_AFFILIATION_DELETE, new Integer(0));
        ChannelEvent event = eventChannel.getChannelEvent(this, key, UserFirmAffiliationFactory.create(userFirmAffiliationStruct));
        eventChannel.dispatch(event);
    }

    /**
     * The callback method used by the SACAS to publish firm affiliation deletes
     * @param userFirmAffiliationStruct
     */
    public void acceptUserFirmAffiliationUpdate(UserFirmAffiliationStruct userFirmAffiliationStruct)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
        {
            GUILoggerHome.find().debug(getClass().getName() + ":acceptUserFirmAffiliationUpdate()",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, userFirmAffiliationStruct);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_USER_EVENT_USER_FIRM_AFFILIATION_UPDATE, new Integer(0));
        ChannelEvent event = eventChannel.getChannelEvent(this, key, UserFirmAffiliationFactory.create(userFirmAffiliationStruct));
        eventChannel.dispatch(event);
    }
}
