package com.cboe.consumers.eventChannel;

/**
 * @author Jeff Illian
 */
import com.cboe.interfaces.events.*;
import com.cboe.util.event.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.idl.cmiUser.UserStruct;
import com.cboe.idl.user.UserDefinitionStruct;
import com.cboe.idl.user.UserEnablementStruct;
import com.cboe.idl.user.UserSummaryStruct;
import com.cboe.idl.firm.FirmStruct;
import com.cboe.domain.util.SessionProfileUserEventStructContainer;
import com.cboe.idl.user.SessionProfileUserDefinitionStruct;
import com.cboe.idl.user.UserFirmAffiliationStruct;
import com.cboe.idl.cmiUser.SessionProfileUserStruct;

public class CacheUpdateConsumerIECImpl extends BObject implements CacheUpdateConsumer {
    private EventChannelAdapter internalEventChannel = null;
    private static final Integer INT_0 = 0;

    public CacheUpdateConsumerIECImpl() {
        super();
        internalEventChannel = EventChannelAdapterFactory.find();
    }

    public void acceptUserUpdate(UserStruct userUpdate,
                                 UserDefinitionStruct userDefinitionUpdate,
                                 UserEnablementStruct userEnablementUpdate)
    {
        // do nothing, we don't let it dispatch the old events, since below we let it dispatch the new events
    }
    public void acceptSessionProfileUserUpdate(SessionProfileUserStruct updatedUser, SessionProfileUserDefinitionStruct updatedUserDefinition, UserEnablementStruct updatedUserEnablement)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> acceptSessionProfileUserUpdate, userId =  " + updatedUserDefinition.userId);
        }

        SessionProfileUserEventStructContainer userContainer = new SessionProfileUserEventStructContainer(updatedUser,
                                                                                                          updatedUserDefinition,
                                                                                                          updatedUserEnablement);
        ChannelKey channelKey = new ChannelKey(ChannelKey.USER_EVENT_ADD_USER, INT_0);
        ChannelEvent event =  EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, userContainer);
        internalEventChannel.dispatch(event);

        // dispatch on usedId based channel
        channelKey = new ChannelKey(ChannelKey.USER_EVENT_ADD_USER, updatedUserEnablement.userId);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, userContainer);
        internalEventChannel.dispatch(event);
    }

    public void acceptUserFirmAffiliationDelete(UserFirmAffiliationStruct userFirmAffiliationStruct)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "event received -> acceptUserFirmAffiliationDelete, userId=" +
                    userFirmAffiliationStruct.userAcronym.exchange + "/" + userFirmAffiliationStruct.userAcronym.acronym +
                    " firm=" + userFirmAffiliationStruct.affiliatedFirm);
        }
        
        ChannelKey channelKey = new ChannelKey(ChannelKey.USER_EVENT_USER_FIRM_AFFILIATION_DELETE, INT_0);
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, userFirmAffiliationStruct);
        internalEventChannel.dispatch(event);
    }

    public void acceptUserFirmAffiliationUpdate(UserFirmAffiliationStruct userFirmAffiliationStruct)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "event received -> acceptUserFirmAffiliationUpdate, userId=" +
                    userFirmAffiliationStruct.userAcronym.exchange + "/" + userFirmAffiliationStruct.userAcronym.acronym +
                    " firm=" + userFirmAffiliationStruct.affiliatedFirm);
        }
        
        ChannelKey channelKey = new ChannelKey(ChannelKey.USER_EVENT_USER_FIRM_AFFILIATION_UPDATE, INT_0);
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, userFirmAffiliationStruct);
        internalEventChannel.dispatch(event);
    }

    public void acceptUserDeletion(UserSummaryStruct userSummary) {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> acceptUserDeletion,userId =  " + userSummary.userId);
        }
        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.USER_EVENT_DELETE_USER, INT_0);

        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, userSummary);
        internalEventChannel.dispatch(event);
    }

    public void acceptFirmUpdate(FirmStruct firmData) {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> acceptFirmUpdate,firm name =  " + firmData.fullName);
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.USER_EVENT_ADD_FIRM, INT_0);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, firmData);
        internalEventChannel.dispatch(event);
    }

    public void acceptFirmDeletion(FirmStruct firmData) {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> acceptFirmDeletion,firm name =  " + firmData.fullName);
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.USER_EVENT_DELETE_FIRM, INT_0);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, firmData);
        internalEventChannel.dispatch(event);
    }

}
