package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.IECRemoteCASExpectedOpeningPriceConsumerHome;
import com.cboe.interfaces.events.RemoteCASExpectedOpeningPriceConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

public class RemoteCASExpectedOpeningPriceConsumerHomeEventImpl extends ClientBOHome implements IECRemoteCASExpectedOpeningPriceConsumerHome
{
    private RemoteCASExpectedOpeningPriceEventConsumerInterceptor consumerProxy;
    private RemoteCASExpectedOpeningPriceEventConsumerImpl event;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private final String CHANNEL_NAME = "RemoteCASExpectedOpeningPrice";

    public RemoteCASExpectedOpeningPriceConsumer create()
    {
        return find();
    }

    public RemoteCASExpectedOpeningPriceConsumer find()
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

        String interfaceRepId = com.cboe.idl.events.RemoteCASExpectedOpeningPriceEventConsumerHelper.id();

        // connect to the event channel without filter and add the constraint filter later on
        // using addConstraint - Eric J. Fredericks
        eventChannelFilterHelper.connectConsumer( CHANNEL_NAME, interfaceRepId, event );
    }

    public void clientInitialize()
    {
        eventChannelFilterHelper = new EventChannelFilterHelper();
        RemoteCASExpectedOpeningPriceConsumerIECImpl consumer = new RemoteCASExpectedOpeningPriceConsumerIECImpl();
        consumer.create(String.valueOf(consumer.hashCode()));

        //Every BObject must be added to the container.
        addToContainer(consumer);
        consumerProxy = new RemoteCASExpectedOpeningPriceEventConsumerInterceptor(consumer);
        if(getInstrumentationEnablementProperty())
        {
            consumerProxy.startInstrumentation(getInstrumentationProperty());
        }
        event = new RemoteCASExpectedOpeningPriceEventConsumerImpl(consumerProxy);
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
        StringBuilder name = new StringBuilder(key.length()+80);
        switch(channelKey.channelType)
        {
            case ChannelType.SUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS_V2:
                name.append(key).append(" in $.subscribeExpectedOpeningPriceForClassV2.routingParameters.groups");
                return name.toString();

            case ChannelType.SUBSCRIBE_EXPECTED_OPENING_PRICE_BY_PRODUCT_V2:
                name.append(key).append(" in $.subscribeExpectedOpeningPriceForProductV2.routingParameters.groups");
                return name.toString();

            case ChannelType.UNSUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS_V2:
                name.append(key).append(" in $.unsubscribeExpectedOpeningPriceForClassV2.routingParameters.groups");
                return name.toString();

            case ChannelType.UNSUBSCRIBE_EXPECTED_OPENING_PRICE_BY_PRODUCT_V2:
                name.append(key).append(" in $.unsubscribeExpectedOpeningPriceForProductV2.routingParameters.groups");
                return name.toString();

            case ChannelType.SUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS:
                name.append(key).append(" in $.subscribeExpectedOpeningPriceForClass.routingParameters.groups");
                return name.toString();

            case ChannelType.UNSUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS:
                name.append(key).append(" in $.unsubscribeExpectedOpeningPriceForClass.routingParameters.groups");
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
