package com.cboe.consumers.eventChannel;


import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.GroupElementConsumer;
import com.cboe.interfaces.events.IECGroupElementConsumerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;


public class GroupElementConsumerHomeEventImpl extends ClientBOHome implements IECGroupElementConsumerHome {
    private GroupElementEventConsumerInterceptor groupElementEventConsumerInterceptor;
    private GroupElementConsumerImpl groupElementConsumerImpl;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private final String CHANNEL_NAME = "GroupElement";
    
    private final Integer INT_0 = 0;
    /**
     * GroupElementConsumerHomeEventImpl constructor comment.
     */
    public GroupElementConsumerHomeEventImpl() {
        super();
    }

    public GroupElementConsumer create() {
        return find();
    }

    /**
     * Return the GroupElementConsumer  (If first time, create it and bind it to the orb).
     * @return GroupElementConsumer
     */
    public GroupElementConsumer find() {
        return groupElementEventConsumerInterceptor;
    }

    public void clientStart()
        throws Exception
    {
        if (eventService == null){
            eventService = eventChannelFilterHelper.connectEventService();
        }
        String interfaceRepId = com.cboe.idl.events.GroupElementEventConsumerHelper.id();
        // connect to the event channel without filter and add the constraint filter later on
        // using addConstraint - Connie Feng
        eventChannelFilterHelper.connectConsumer( CHANNEL_NAME, interfaceRepId, groupElementConsumerImpl );
    }

    public void clientInitialize() {
        eventChannelFilterHelper = new EventChannelFilterHelper();
        GroupElementConsumerIECImpl groupElementConsumer = new GroupElementConsumerIECImpl();
        groupElementConsumer.create(String.valueOf(groupElementConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(groupElementConsumer);
        groupElementEventConsumerInterceptor = new GroupElementEventConsumerInterceptor(groupElementConsumer);
        if(getInstrumentationEnablementProperty())
        {
            groupElementEventConsumerInterceptor.startInstrumentation(getInstrumentationProperty());
        }
        groupElementConsumerImpl = new GroupElementConsumerImpl(groupElementEventConsumerInterceptor);
    }

    /**
     * Adds the event channel listener to the internal event channel. Constraints based on the
     * ChannelKey will be added as well. Do not make call to addConstraints when this method has
     * already being called.
     *
     * @param channelKey the event channel key
     */
    public void addFilter (ChannelKey channelKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Received channel key for add is: " + channelKey);
            Log.debug(this, "Incrementing reference count and/or adding filter for: " + channelKey);
        }
        
        //addConstraint(channelKey);
    }// end of addEventChannelListener

    /**
     * Adds constraint based on the channel key
     *
     * @param channelKey the event channel key
     */
    private void addConstraint(ChannelKey channelKey)
        throws SystemException
    {
        if ( find() != null )
        {
            String constraintString = getConstraintString(channelKey);
            if(Log.isDebugOn())
            {
                Log.debug(this, "add constraint string is: \"" + constraintString + "\"");
            }
            eventChannelFilterHelper.addEventFilter( groupElementConsumerImpl, channelKey,
                    eventChannelFilterHelper.getChannelName(CHANNEL_NAME), constraintString);
        }
    }// end of addConstraint


    /**
     * Removes the event channel listener to the internal event channel and the CBOE event channel.
     *
     * @param channelKey the event channel key
     */
    public void removeFilter (ChannelKey channelKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Received channel key for remove is: " + channelKey);
            Log.debug(this, "Decrementing reference count and/or removing filter for: " + channelKey);
        }
        
        removeConstraint(channelKey);
    }// end of removeEventChannelListener

    /**
     * Removes constraint based on the channel key
     *
     * @param channelKey the event channel key
     */
    private void removeConstraint(ChannelKey channelKey)
        throws SystemException
    {
        if ( find() != null ) {
            String constraintString = getConstraintString(channelKey);
            if(Log.isDebugOn())
            {
                Log.debug(this, "remove constraint string is: \"" + constraintString + "\"");
            }
            eventChannelFilterHelper.removeEventFilter( channelKey, constraintString);
        }
    }

    /**
     * Returns the constraint string based on the channel key
     *
     * @param channelKey the event channel key
     */
    protected String getConstraintString(ChannelKey channelKey)
    {
        switch (channelKey.channelType)
        {

            case ChannelType.GROUP_ADD_ELEMENT:
            case ChannelType.GROUP_UPDATE_ELEMENT:
            case ChannelType.GROUP_REMOVE_ELEMENT:
                if (!channelKey.key.equals(INT_0))
                {
                    Log.alarm(this, "Invalid key: " + channelKey.key + " for context: " + channelKey.channelType);
                }
                return EventChannelFilterHelper.ALL_EVENTS_CONSTRAINT;
            default :
                Log.alarm(this, "Invalid Channel Type for filtering for context: " + channelKey.channelType);
                return EventChannelFilterHelper.NO_EVENTS_CONSTRAINT;
        }
    }

    // Unused methods declared in home interface for server usage.
    public void addConsumer(GroupElementConsumer consumer, ChannelKey key) {}
    public void removeConsumer(GroupElementConsumer consumer, ChannelKey key) {}
    public void removeConsumer(GroupElementConsumer consumer) {}
}// EOF
