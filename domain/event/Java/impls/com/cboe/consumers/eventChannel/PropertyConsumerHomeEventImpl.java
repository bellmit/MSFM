package com.cboe.consumers.eventChannel;


import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.IECPropertyConsumerHome;
import com.cboe.interfaces.events.PropertyConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

public class PropertyConsumerHomeEventImpl extends ClientBOHome implements IECPropertyConsumerHome {
    private PropertyEventConsumerInterceptor propertyEventConsumerInterceptor;
    private PropertyConsumerImpl property;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private final String CHANNEL_NAME = "Property";
    /**
     * PropertyConsumerHomeEventImpl constructor comment.
     */
    public PropertyConsumerHomeEventImpl() {
        super();
    }

    public PropertyConsumer create() {
        return find();
    }


    public PropertyConsumer find() {
        return propertyEventConsumerInterceptor;
    }

    public void clientStart()
        throws Exception
    {

        if (eventService == null){
            eventService = eventChannelFilterHelper.connectEventService();
        }

        String interfaceRepId = com.cboe.idl.events.PropertyEventConsumerHelper.id();

        eventChannelFilterHelper.connectConsumer( CHANNEL_NAME, interfaceRepId, property );
    }

    public void clientInitialize() {
        eventChannelFilterHelper = new EventChannelFilterHelper();
        PropertyConsumerIECImpl propertyConsumer = new PropertyConsumerIECImpl();
        propertyConsumer.create(String.valueOf(propertyConsumer.hashCode()));
        addToContainer(propertyConsumer);
        propertyEventConsumerInterceptor = new PropertyEventConsumerInterceptor(propertyConsumer);
        if(getInstrumentationEnablementProperty())
        {
            propertyEventConsumerInterceptor.startInstrumentation(getInstrumentationProperty());
        }
        property = new PropertyConsumerImpl(propertyEventConsumerInterceptor);
    }

    /**
     * Adds the event channel listener to CBOE property event channel. Constraints based on the
     * ChannelKey will be added as well. Do not make call to addConstraints when this method has
     * already being called.
     * @param channelKey the event channel key
     *
     */
    public void addFilter (ChannelKey channelKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        addConstraint(channelKey);
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
            eventChannelFilterHelper.addEventFilter( property, channelKey,
                    eventChannelFilterHelper.getChannelName(CHANNEL_NAME), constraintString);
        }
    }


    /**
     * Removes the event channel listener from property event channel and the CBOE event channel.
     * @param channelKey the event channel key

     */
    public void removeFilter (ChannelKey channelKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        removeConstraint(channelKey);
    }

    /**
     * Removes constraint based on the channel key
     *
     * @param channelKey the event channel key
     *
     */
    private void removeConstraint(ChannelKey channelKey)
        throws SystemException
    {
        if ( find() != null ) {
            String constraintString = getConstraintString(channelKey);
            eventChannelFilterHelper.removeEventFilter( channelKey, constraintString);
        }
    }

    /**
     * Returns the constraint string for CBOE property event channel based on the channel key
     *
     * @param channelKey the event channel key
     *
     */
    protected String getConstraintString(ChannelKey channelKey)
    {
        String parm = getParmName(channelKey);

        if(parm.equals(""))
        {
            return "";
        }

        else if(parm.equals(EventChannelFilterHelper.ALL_EVENTS_CONSTRAINT))
        {
            return EventChannelFilterHelper.ALL_EVENTS_CONSTRAINT;
        }

        else
        {
        	StringBuilder buf = new StringBuilder(parm.length()+2);
            buf.append("$.").append(parm);
            return buf.toString();
        }

    }

    /**
     * Returns the constraint parameter string based on the channel key
     *
     * @param channelKey the event channel key
     *
     */
    protected String getParmName(ChannelKey channelKey)
    {
        switch (channelKey.channelType)
        {
            case ChannelType.UPDATE_PROPERTY_ENABLEMENT:
            case ChannelType.UPDATE_PROPERTY_RATELIMIT:
                return new StringBuilder(100)
                          .append("acceptPropertyUpdate.propertyGroupStruct.propertyKey=='")
                          .append(channelKey.key).append("'").toString();
            case ChannelType.REMOVE_PROPERTY_ENABLEMENT:
            case ChannelType.REMOVE_PROPERTY_RATELIMIT:
                return new StringBuilder(80)
                          .append("acceptPropertyRemove.propertyKey=='")
                          .append(channelKey.key).append("'").toString();
            case ChannelType.UPDATE_PROPERTY :
            case ChannelType.REMOVE_PROPERTY :
                return EventChannelFilterHelper.ALL_EVENTS_CONSTRAINT;
            default :
                if (Log.isDebugOn())
                {
                    Log.debug("Unknown channel type: " + channelKey.channelType);
                }
                return "";
        }
    } // end of getParmName

    // Unused methods declared in home interface for server usage.
    public void addConsumer(PropertyConsumer consumer, ChannelKey key) {}
    public void removeConsumer(PropertyConsumer consumer, ChannelKey key) {}
    public void removeConsumer(PropertyConsumer consumer) {}
}// EOF
