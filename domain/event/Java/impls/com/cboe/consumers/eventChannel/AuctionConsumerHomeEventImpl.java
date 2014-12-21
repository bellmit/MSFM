package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.AuctionConsumer;
import com.cboe.interfaces.events.IECAuctionConsumerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

/** Home class for creating AuctionEventConsumerImpl and AuctionConsumerIECImpl
 * objects. We can have one home class for both types of object because each
 * object of one type corresponds to exactly one object of the other type.
 */
public class AuctionConsumerHomeEventImpl
        extends ClientBOHome implements IECAuctionConsumerHome
{
    private AuctionEventConsumerImpl auctionEvent;
    private AuctionEventConsumerInterceptor auctionEventConsumerInterceptor;
    private AuctionConsumer auctionConsumer;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private final String CHANNEL_NAME = "Auction";

    public AuctionConsumerHomeEventImpl()
    {
        super();
        if (Log.isDebugOn())
        {
            Log.debug("constructor::AuctionConsumerHomeEventImpl");
        }
    }

    public AuctionConsumer create()
    {
        return find();
    }

    public AuctionConsumer find()
    {
        return auctionEventConsumerInterceptor;
    }

    public void clientStart ()
        throws Exception
    {
        createConsumer();

        String interfaceRepId =
                com.cboe.idl.events.AuctionEventConsumerHelper.id();

        // Connect to the event channel now, add constraint filter later.
        eventChannelFilterHelper.connectConsumer(CHANNEL_NAME, interfaceRepId, auctionEvent);
    }

    private void createConsumer()
    {
        ((AuctionConsumerIECImpl)auctionConsumer).create(
                String.valueOf(auctionConsumer.hashCode()));
        // Every BObject must be added to the container.
        addToContainer((AuctionConsumerIECImpl)auctionConsumer);
    }

    private void initializeConsumer()
    {
       auctionConsumer = new AuctionConsumerIECImpl();
    }

    public void clientInitialize()
    {
        initializeConsumer();

        auctionEventConsumerInterceptor = new AuctionEventConsumerInterceptor(auctionConsumer);
        if(getInstrumentationEnablementProperty())
        {
            auctionEventConsumerInterceptor.startInstrumentation(getInstrumentationProperty());
        }
        auctionEvent = new AuctionEventConsumerImpl(auctionEventConsumerInterceptor);
        
        eventChannelFilterHelper = new EventChannelFilterHelper();
        eventService = eventChannelFilterHelper.connectEventService();
    }

    /** Add a Filter to the Internal Event Channel. Constraints based on the
     * ChannelKey will be added as well. Do not call addConstraints when this
     * method is already being called.
     * @param channelKey The event channel key containing our exact
     * filtering requirements.
     */
    public void addFilter(ChannelKey channelKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        // Add the filter to the CBOE event channel
        addConstraint(channelKey);
    }

    /** Add constraint to the IEC based on the channel key.
     * @param channelKey The event channel key containing our exact filtering
     * requirements.
     */
    private void addConstraint(ChannelKey channelKey)
        throws SystemException
    {
        if (find() != null)
        {
            String constraintString = getConstraintString(channelKey);
            if (Log.isDebugOn())
            {
                Log.debug(this, "constraintString::" + constraintString);
            }

            eventChannelFilterHelper.addEventFilter(
                    auctionEvent, channelKey,
                    eventChannelFilterHelper.getChannelName(CHANNEL_NAME),
                    constraintString);
        }
    }

    /** Remove the event channel Filter from the CBOE event channel.
     * @param channelKey the event channel key
     */
    public void removeFilter(ChannelKey channelKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        removeConstraint(channelKey);
    }

    /** Remove constraint based on the channel key.
     * @param channelKey the event channel key
     */
    private void removeConstraint(ChannelKey channelKey)
        throws SystemException
    {
        if (find() != null)
        {
            String constraintString = getConstraintString(channelKey);
            eventChannelFilterHelper.removeEventFilter(channelKey, constraintString);
        }
    }

    /** Return the constraint string based on the channel key.
     * @param channelKey the event channel key
     */
    private String getConstraintString(ChannelKey channelKey)
    {
        return getParmName(channelKey);
    }

    /** Return the constraint parameter string based on the channel key.
     * @param channelKey the event channel key
     */
    private String getParmName(ChannelKey channelKey)
    {
    	if (Log.isDebugOn())
        {
            Log.debug(this, "channel type " + channelKey.channelType);
        }
        String key = channelKey.key.toString();
        StringBuilder name = new StringBuilder(key.length()+50);
        switch (channelKey.channelType)
        { 
            case ChannelType.AUCTION:
                // Simple filter (one equality test) is very efficient
                name.append("$.acceptAuction.routingParameters.classKey == ").append(key);
                return name.toString();
            case ChannelType.AUCTION_USER:
                // Not-simple filter (less efficient but we have no choice)
                name.append(key).append(" in $.acceptAuction.activeUserKeys");
                return name.toString();
            case ChannelType.DAIM_USER:
                // Not-simple filter (less efficient but we have no choice)
                name.append(key).append(" in $.acceptDirectedAIMAuction.activeUserKeys");
                return name.toString();
            default:
                Log.alarm(this, "Invalid Channel Type for filtering for context: " + channelKey.channelType);
                return EventChannelFilterHelper.NO_EVENTS_CONSTRAINT;
        }
    }

    // Unused methods declared in home interface for server usage.
    public void addConsumer(AuctionConsumer consumer, ChannelKey key) {}
    public void removeConsumer(AuctionConsumer consumer, ChannelKey key) {}
    public void removeConsumer(AuctionConsumer consumer) {}
}
