package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.IECRemoteCASRecapConsumerHome;
import com.cboe.interfaces.events.RemoteCASRecapConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

public class RemoteCASRecapConsumerHomeEventImpl extends ClientBOHome implements IECRemoteCASRecapConsumerHome
{
    private RemoteCASRecapEventConsumerInterceptor consumerProxy;
    private RemoteCASRecapEventConsumerImpl event;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private final String CHANNEL_NAME = "RemoteCASRecap";

    public RemoteCASRecapConsumer create()
    {
        return find();
    }

    public RemoteCASRecapConsumer find()
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

        String interfaceRepId = com.cboe.idl.events.RemoteCASRecapEventConsumerHelper.id();

        // connect to the event channel without filter and add the constraint filter later on
        // using addConstraint - Eric J. Fredericks
        eventChannelFilterHelper.connectConsumer( CHANNEL_NAME, interfaceRepId, event );
    }

    public void clientInitialize()
    {
        eventChannelFilterHelper = new EventChannelFilterHelper();
        RemoteCASRecapConsumerIECImpl consumer = new RemoteCASRecapConsumerIECImpl();
        consumer.create(String.valueOf(consumer.hashCode()));

        //Every BObject must be added to the container.
        addToContainer(consumer);
        consumerProxy = new RemoteCASRecapEventConsumerInterceptor(consumer);
        if( getInstrumentationEnablementProperty())
        {
            consumerProxy.startInstrumentation(getInstrumentationProperty());
        }
        event = new RemoteCASRecapEventConsumerImpl(consumerProxy);
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
        String key = channelKey.key.toString();
        StringBuilder name = new StringBuilder(key.length()+65);

        switch(channelKey.channelType)
        {
            case ChannelType.SUBSCRIBE_RECAP_BY_CLASS:
                name.append(key).append(" in $.subscribeRecapForClass.routingParameters.groups");
                return name.toString();

            case ChannelType.SUBSCRIBE_RECAP_BY_PRODUCT:
                name.append(key).append(" in $.subscribeRecapForProduct.routingParameters.groups");
                return name.toString();

            case ChannelType.UNSUBSCRIBE_RECAP_BY_CLASS:
                name.append(key).append(" in $.unsubscribeRecapForClass.routingParameters.groups");
                return name.toString();

            case ChannelType.UNSUBSCRIBE_RECAP_BY_PRODUCT:
                name.append(key).append(" in $.unsubscribeRecapForProduct.routingParameters.groups");
                return name.toString();

            case ChannelType.SUBSCRIBE_RECAP_BY_CLASS_V2:
                name.append(key).append(" in $.subscribeRecapForClassV2.routingParameters.groups");
                return name.toString();

            case ChannelType.SUBSCRIBE_RECAP_BY_PRODUCT_V2:
                name.append(key).append(" in $.subscribeRecapForProductV2.routingParameters.groups");
                return name.toString();

            case ChannelType.UNSUBSCRIBE_RECAP_BY_CLASS_V2:
                name.append(key).append(" in $.unsubscribeRecapForClassV2.routingParameters.groups");
                return name.toString();

            case ChannelType.UNSUBSCRIBE_RECAP_BY_PRODUCT_V2:
                name.append(key).append(" in $.unsubscribeRecapForProductV2.routingParameters.groups");
                return name.toString();

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
        SessionKeyContainer sessionGroupKey = (SessionKeyContainer) channelKey.key;
        ChannelKey newKey = new ChannelKey(channelKey.channelType, Integer.valueOf(sessionGroupKey.getKey()));
        addConstraint(newKey);
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
        SessionKeyContainer sessionGroupKey = (SessionKeyContainer) channelKey.key;
        ChannelKey newKey = new ChannelKey(channelKey.channelType, Integer.valueOf(sessionGroupKey.getKey()));
        removeConstraint(newKey);
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
