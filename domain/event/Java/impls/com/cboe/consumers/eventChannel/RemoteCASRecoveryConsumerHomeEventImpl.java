package com.cboe.consumers.eventChannel;

/**
 * RemoteCASRecoveryConsumerHomeEventImpl.
 *
 * @author Jing Chen
 */

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.interfaces.events.IECRemoteCASRecoveryConsumerHome;
import com.cboe.interfaces.events.RemoteCASRecoveryConsumer;
import com.cboe.util.ChannelKey;

public class RemoteCASRecoveryConsumerHomeEventImpl extends ClientBOHome implements IECRemoteCASRecoveryConsumerHome
{
    private RemoteCASRecoveryEventConsumerInterceptor consumerProxy;
    private RemoteCASRecoveryEventConsumerImpl event;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private final String CHANNEL_NAME = "RemoteCASRecovery";

    public RemoteCASRecoveryConsumer create()
    {
        return find();
    }

    public RemoteCASRecoveryConsumer find()
    {
        return consumerProxy;
    }

    public void clientStart()
        throws Exception
    {

        if (eventService == null)
        {
            eventService = eventChannelFilterHelper.connectEventService();
        }

        String interfaceRepId = com.cboe.idl.events.RemoteCASRecoveryEventConsumerHelper.id();

        // connect to the event channel without filter and add the constraint filter later on
        // using addConstraint - Eric J. Fredericks
        eventChannelFilterHelper.connectConsumer( CHANNEL_NAME, interfaceRepId, event );
    }

    public void clientInitialize()
    {
        eventChannelFilterHelper = new EventChannelFilterHelper();
        RemoteCASRecoveryConsumerIECImpl consumer = new RemoteCASRecoveryConsumerIECImpl();
        consumer.create(String.valueOf(consumer.hashCode()));

        //Every BObject must be added to the container.
        addToContainer(consumer);
        consumerProxy = new RemoteCASRecoveryEventConsumerInterceptor(consumer);
        if(getInstrumentationEnablementProperty())
        {
            consumerProxy.startInstrumentation(getInstrumentationProperty());
        }
        event = new RemoteCASRecoveryEventConsumerImpl(consumerProxy);
    }


    private String getConstraintString(ChannelKey channelKey)
    {
        return EventChannelFilterHelper.ALL_EVENTS_CONSTRAINT;
    }

    public void addFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        addConstraint(channelKey);
    }

    private void addConstraint(ChannelKey channelKey)
        throws SystemException
    {
        String constraintString = getConstraintString(channelKey);
        eventChannelFilterHelper.addEventFilter(event, channelKey,
                eventChannelFilterHelper.getChannelName(CHANNEL_NAME), constraintString);
    }

    public void removeFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        removeConstraint(channelKey);
    }

    private void removeConstraint(ChannelKey channelKey)
        throws SystemException
    {
        String constraintString = getConstraintString(channelKey);
        eventChannelFilterHelper.removeEventFilter( channelKey, constraintString);
    }
}// EOF
