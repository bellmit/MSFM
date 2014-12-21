package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.IECRemoteCASCallbackRemovalConsumerHome;
import com.cboe.interfaces.events.RemoteCASCallbackRemovalConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

public class RemoteCASCallbackRemovalConsumerHomeEventImpl extends ClientBOHome implements IECRemoteCASCallbackRemovalConsumerHome
{
    private RemoteCASCallbackRemovalEventConsumerInterceptor consumerProxy;
    private RemoteCASCallbackRemovalEventConsumerImpl event;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private final String CHANNEL_NAME = "RemoteCASCallbackRemoval";

    public RemoteCASCallbackRemovalConsumer create()
    {
        return find();
    }

    public RemoteCASCallbackRemovalConsumer find()
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

        String interfaceRepId = com.cboe.idl.events.RemoteCASCallbackRemovalEventConsumerHelper.id();

        // connect to the event channel without filter and add the constraint filter later on
        // using addConstraint - Eric J. Fredericks
        eventChannelFilterHelper.connectConsumer( CHANNEL_NAME, interfaceRepId, event );
    }

    public void clientInitialize()
    {
        eventChannelFilterHelper = new EventChannelFilterHelper();
        RemoteCASCallbackRemovalConsumerIECImpl consumer = new RemoteCASCallbackRemovalConsumerIECImpl();
        consumer.create(String.valueOf(consumer.hashCode()));

        //Every BObject must be added to the container.
        addToContainer(consumer);
        consumerProxy = new RemoteCASCallbackRemovalEventConsumerInterceptor(consumer);
        if(getInstrumentationEnablementProperty())
        {
            consumerProxy.startInstrumentation(getInstrumentationProperty());
        }
        event = new RemoteCASCallbackRemovalEventConsumerImpl(consumerProxy);
    }

    /**
     * Returns the constraint string based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Eric J. Fredericks
     */
    private String getConstraintString(ChannelKey channelKey)
    {
        return getParmName(channelKey);
    }

    /**
     * Returns the constraint parameter string based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Eric J. Fredericks
     */
    private String getParmName(ChannelKey channelKey)
    {
        switch(channelKey.channelType)
        {
            case ChannelType.MDCAS_CALLBACK_REMOVAL:
                return new StringBuilder(80)
                          .append("$.acceptCallbackRemoval.casOrigin=='")
                          .append(channelKey.key)
                          .append("'").toString();

            default:
                Log.alarm(this, "Invalid Channel Type for filtering for context: " + channelKey.channelType);
                return EventChannelFilterHelper.NO_EVENTS_CONSTRAINT;
        }

    }

    /**
     * Adds a  Filter to the internal event channel. Constraints based on the
     * ChannelKey will be added as well. Do not make call to addConstraints when this method has
     * already being called.
     *
     * @param channelKey the event channel key
     *
     * @author Eric J. Fredericks
     */
    public void addFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        addConstraint(channelKey);
    }

    /**
     * Adds constraint based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Eric J. Fredericks
     */
    private void addConstraint(ChannelKey channelKey)
        throws SystemException
    {
        String constraintString = getConstraintString(channelKey);
        eventChannelFilterHelper.addEventFilter(event, channelKey,
                eventChannelFilterHelper.getChannelName(CHANNEL_NAME), constraintString);
    }

    /**
     * Removes the event channel Filter from the CBOE event channel.
     *
     * @param channelKey the event channel key
     *
     * @author Eric J. Fredericks
     */
    public void removeFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        removeConstraint(channelKey);
    }

    /**
     * Removes constraint based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Eric J. Fredericks
     */
    private void removeConstraint(ChannelKey channelKey)
        throws SystemException
    {
        String constraintString = getConstraintString(channelKey);
        eventChannelFilterHelper.removeEventFilter( channelKey, constraintString);
    }
}// EOF
