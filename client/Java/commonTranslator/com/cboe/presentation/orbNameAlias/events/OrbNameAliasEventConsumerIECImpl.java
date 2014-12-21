package com.cboe.presentation.orbNameAlias.events;

import com.cboe.idl.clusterInfo.LogicalOrbNameStruct;
import com.cboe.idl.clusterInfo.OrbNameAliasStruct;

import com.cboe.exceptions.ExceptionDetails;

import com.cboe.interfaces.events.OrbNameAliasConsumer;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

import com.cboe.presentation.orbNameAlias.LogicalNameImpl;
import com.cboe.presentation.orbNameAlias.OrbNameAliasImpl;

public class OrbNameAliasEventConsumerIECImpl implements OrbNameAliasConsumer{
    protected EventChannelAdapter eventChannel;

    public OrbNameAliasEventConsumerIECImpl()
    {
        eventChannel = null;
    }

    protected EventChannelAdapter getEventChannelAdapter()
    {
        if (eventChannel == null)
        {
            eventChannel = EventChannelAdapterFactory.find();
        }
        return eventChannel;
    }
    
    public void acceptOrbNameAliases(long requestId, OrbNameAliasStruct[] aliases)
    {
        dispatchEvent(ChannelType.IC_ACCEPT_ORB_NAME_ALIASES, requestId, aliases);
    }

    public void acceptNewOrbNameAlias(long requestId, OrbNameAliasStruct alias)
    {
        OrbNameAliasImpl orbNameAlias = new OrbNameAliasImpl(alias.orbName,alias.displayName,alias.clusterName,alias.subClusterName);
        dispatchEvent(ChannelType.IC_ACCEPT_NEW_ORB_NAME_ALIAS, requestId, orbNameAlias);
    }

    public void acceptChangedOrbNameAlias(long requestId, OrbNameAliasStruct alias)
    {
        OrbNameAliasImpl orbNameAlias = new OrbNameAliasImpl(alias.orbName,alias.displayName,alias.clusterName,alias.subClusterName);
        dispatchEvent(ChannelType.IC_ACCEPT_CHANGED_ORB_NAME_ALIAS, requestId, orbNameAlias);
    }

    public void acceptDeleteOrbNameAlias(long requestId, OrbNameAliasStruct alias)
    {
        OrbNameAliasImpl orbNameAlias = new OrbNameAliasImpl(alias.orbName,alias.displayName,alias.clusterName,alias.subClusterName);

        dispatchEvent(ChannelType.IC_ACCEPT_DELETE_ORB_NAME_ALIAS, requestId, orbNameAlias);
    }

    public void acceptLogicalOrbNames(long requestId, LogicalOrbNameStruct[] names)
    {
        dispatchEvent(ChannelType.IC_ACCEPT_LOGICAL_ORB_NAMES, requestId, names);
    }

    public void acceptNewLogicalOrbName(long requestId, LogicalOrbNameStruct name)
    {
        LogicalNameImpl logicalName = new LogicalNameImpl(name.logicalName, name.orbName);
        dispatchEvent(ChannelType.IC_ACCEPT_NEW_LOGICAL_ORB_NAME, requestId, logicalName);
    }

    public void acceptChangedLogicalOrbName(long requestId, LogicalOrbNameStruct name)
    {
        LogicalNameImpl logicalName = new LogicalNameImpl(name.logicalName, name.orbName);
        dispatchEvent(ChannelType.IC_ACCEPT_CHANGED_LOGICAL_ORB_NAME, requestId, logicalName);
    }

    public void acceptDeleteLogicalOrbName(long requestId, LogicalOrbNameStruct name)
    {
        LogicalNameImpl logicalName = new LogicalNameImpl(name.logicalName, name.orbName);
        dispatchEvent(ChannelType.IC_ACCEPT_DELETE_LOGICAL_ORB_NAME, requestId, logicalName);
    }

    protected void dispatchEvent(int channelType, long requestId, Object object)
    {
        ChannelEvent channelEventAll =
                getEventChannelAdapter().getChannelEvent(this, new ChannelKey(channelType, new Integer(0)), object);
        ChannelEvent channelEventByRequest =
                getEventChannelAdapter().getChannelEvent(this, new ChannelKey(channelType, new Long(requestId)),
                                                         object);
        getEventChannelAdapter().dispatch(channelEventByRequest);
        getEventChannelAdapter().dispatch(channelEventAll);
    }

    protected void dispatchException(long requestId, ExceptionDetails exceptionDetails, String exceptionName, int channelType)
    {
        dispatchEvent(channelType, requestId, exceptionDetails);
    }

    public void acceptAlreadyExistsException(long requestId, ExceptionDetails exceptionDetails)
    {
        dispatchException(requestId, exceptionDetails, "acceptAlreadyExistsException",
                          ChannelType.IC_ACCEPT_ALREADY_EXISTS_EXCEPTION);
    }

    public void acceptDataValidationException(long requestId, ExceptionDetails exceptionDetails)
    {
        dispatchException(requestId, exceptionDetails, "acceptDataValidationException",
                          ChannelType.IC_ACCEPT_DATA_VALIDATION_EXCEPTION);
    }

    public void acceptNotAcceptedException(long requestId, ExceptionDetails exceptionDetails)
    {
        dispatchException(requestId, exceptionDetails, "acceptNotAcceptedException",
                          ChannelType.IC_ACCEPT_NOT_ACCEPTED_EXCEPTION);
    }

    public void acceptNotFoundException(long requestId, ExceptionDetails exceptionDetails)
    {
        dispatchException(requestId, exceptionDetails, "acceptNotFoundException",
                          ChannelType.IC_ACCEPT_NOT_FOUND_EXCEPTION);
    }

    public void acceptSystemException(long requestId, ExceptionDetails exceptionDetails)
    {
        dispatchException(requestId, exceptionDetails, "acceptSystemException",
                          ChannelType.IC_ACCEPT_SYSTEM_EXCEPTION);
    }

    public void acceptTransactionFailedException(long requestId, ExceptionDetails exceptionDetails)
    {
        dispatchException(requestId, exceptionDetails, "acceptTransactionFailedException",
                          ChannelType.IC_ACCEPT_TRANSACTION_FAILED_EXCEPTION);
    }
}
