/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Oct 11, 2002
 * Time: 12:16:38 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.domain.util.UserSessionClassContainer;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.IECNBBOAgentAdminConsumerHome;
import com.cboe.interfaces.events.NBBOAgentAdminConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

public class NBBOAgentAdminConsumerHomeEventImpl extends ClientBOHome implements IECNBBOAgentAdminConsumerHome {
    private NBBOAgentAdminEventConsumerInterceptor nbboAgentAdminEventConsumerInterceptor;
    private NBBOAgentAdminEventConsumerImpl nbboAgentAdminEvent;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private final String CHANNEL_NAME = "NBBOAgentAdmin";

    /**
     * NBBOAgentAdminConsumerHomeEventImpl constructor comment.
     */
    public NBBOAgentAdminConsumerHomeEventImpl() {
        super();
    }

    public NBBOAgentAdminConsumer create() {
        return find();
    }

    public NBBOAgentAdminConsumer find() {
        return nbboAgentAdminEventConsumerInterceptor;
    }

    public void clientStart()
        throws Exception
    {

        if (eventService == null){
            eventService = eventChannelFilterHelper.connectEventService();
        }
        String interfaceRepId = com.cboe.idl.events.NBBOAgentAdminEventConsumerHelper.id();
        // connect to the event channel without filter and add the constraint filter later on
        // using addConstraint - Connie Feng
        eventChannelFilterHelper.connectConsumer( CHANNEL_NAME, interfaceRepId, nbboAgentAdminEvent );
    }

    public void clientInitialize()
        throws Exception
    {
        eventChannelFilterHelper = new EventChannelFilterHelper();
        NBBOAgentAdminConsumerIECImpl nbboAgentAdminConsumer = new NBBOAgentAdminConsumerIECImpl();
        nbboAgentAdminConsumer.create(String.valueOf(nbboAgentAdminConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(nbboAgentAdminConsumer);

        nbboAgentAdminEventConsumerInterceptor = new NBBOAgentAdminEventConsumerInterceptor(nbboAgentAdminConsumer);
        if(getInstrumentationEnablementProperty())
        {
            nbboAgentAdminEventConsumerInterceptor.startInstrumentation(getInstrumentationProperty());
        }
        nbboAgentAdminEvent = new NBBOAgentAdminEventConsumerImpl(nbboAgentAdminEventConsumerInterceptor);
    }

    /**
     * Adds a  Filter to the internal event channel. Constraints based on the
     * ChannelKey will be added as well. Do not make call to addConstraints when this method has
     * already being called.
     *
     * @param channelKey the event channel key
     *
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
     */
    private void addConstraint(ChannelKey channelKey)
        throws SystemException
    {
        if ( find() != null )
        {
            String constraintString = getConstraintString(channelKey);

            eventChannelFilterHelper.addEventFilter( nbboAgentAdminEvent, channelKey,
                    eventChannelFilterHelper.getChannelName(CHANNEL_NAME), constraintString);
        }
    }// end of addConstraint


    /**
     * Removes the event channel Filter from the CBOE event channel.
     *
     * @param channelKey the event channel key
     *
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
     */
    private void removeConstraint(ChannelKey channelKey)
        throws SystemException
    {
        if ( find() != null ) {
            String constraintString = getConstraintString(channelKey);
            eventChannelFilterHelper.removeEventFilter( channelKey, constraintString);
        }
    }// end of addConstraint

    /**
     * Returns the constraint string based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    protected String getConstraintString(ChannelKey channelKey)
    {
        String parm = getParmName(channelKey);

        if(parm.equals(EventChannelFilterHelper.ALL_EVENTS_CONSTRAINT) ||
           parm.equals(EventChannelFilterHelper.NO_EVENTS_CONSTRAINT))
        {
            return parm;
        }

        StringBuilder buf = new StringBuilder(parm.length()+2);
        buf.append("$.").append(parm);
        return buf.toString();
    }


    /**
     * Returns the constraint parameter string based on the channel key
     *
     * @param channelKey the event channel key
     *
     */
    protected String getParmName(ChannelKey channelKey)
    {
        UserSessionClassContainer key = (UserSessionClassContainer)channelKey.key;
        StringBuilder name = new StringBuilder(140);
        switch (channelKey.channelType)
        {
            case ChannelType.NBBO_AGENT_FORCED_OUT :
                name.append("acceptForcedTakeOver.userId=='").append(key.getUserId()).append("'")
                    .append(" and $.acceptForcedTakeOver.classKey==").append(key.getClassKey())
                    .append(" and $.acceptForcedTakeOver.sessionName=='").append(key.getSessionName()).append("'");
                return name.toString();
            case ChannelType.NBBO_AGENT_REMINDER :
                name.append("acceptReminder.userId=='").append(key.getUserId()).append("'")
                    .append(" and $.acceptReminder.classKey==").append(key.getClassKey())
                    .append(" and $.acceptReminder.sessionName=='").append(key.getSessionName()).append("'");
                return name.toString();

            default :
                Log.alarm(this, "Invalid Channel Type for filtering for context: " + channelKey.channelType);
                return EventChannelFilterHelper.NO_EVENTS_CONSTRAINT;
        }
    } // end of getParmName

    // Unused methods declared in home interface for server usage.
    public void addConsumer(NBBOAgentAdminConsumer consumer, ChannelKey key) {}
    public void removeConsumer(NBBOAgentAdminConsumer consumer, ChannelKey key) {}
    public void removeConsumer(NBBOAgentAdminConsumer consumer) {}
}// EOF