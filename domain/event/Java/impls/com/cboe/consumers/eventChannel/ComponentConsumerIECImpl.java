package com.cboe.consumers.eventChannel;

/**
 * @author Keval Desai
 */
import com.cboe.interfaces.events.*;
import com.cboe.util.event.*;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

public class ComponentConsumerIECImpl extends BObject implements ComponentConsumer
{
    private EventChannelAdapter internalEventChannel = null;
    private static final Integer INT_0 = 0;
    /**
     * constructor comment.
     */
    public ComponentConsumerIECImpl() {
        super();
        internalEventChannel = EventChannelAdapterFactory.find();
    }

/**
 *  This is not implemented because it informs the Component ORB connection established. Not the real process startup
 */
    public void acceptComponentEstablished(String componentName)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> acceptComponentEstablished for :: " + componentName + ". Do nothing.");
        }
    }

/**
 *  In Future, if CAS needs to be notified for an individual Regd components, then need to
 *  implement following method.
 */
    public void acceptComponentFailed(String componentName)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> acceptComponentFailed for :: "+componentName+". Do nothing.");
        }
    }

/**
 *  Implemented to Notify the CAS about it's interested Component Startup.
 */
    public void acceptComponentIsMaster(String componentName, boolean isMaster)
    {
        if(isMaster)
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "event received -> acceptComponentIsMaster for :: " + componentName);
            }

            ChannelKey channelKey = null;
            ChannelEvent event = null;

            channelKey = new ChannelKey(ChannelKey.CB_COMPONENT_UP, INT_0);
            event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, componentName);
            internalEventChannel.dispatch(event);
        }
        else
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "event received -> acceptComponentIsMaster for :: " + componentName + ". Do nothing.");
            }
        }
    }
/**
 *  As per CAS point of view, it is interested for Notification of Failure of 'All' Regd Components.
 *  so, acceptComponentFailed() has not been implemented, insted following method allRegisteredComponentsFailed()
 *  has been implemented - that will notify the CAS about FE loss. So, CAS can log out all the Users.
 */

    public void allRegisteredComponentsFailed()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> allRegisteredComponentsFailed ");
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.CB_COMPONENT_DOWN, INT_0);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, "");
        internalEventChannel.dispatch(event);
    }

    public void allRegisteredComponentsNotMaster()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> allRegisteredComponentsNotMaster ");
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.CB_COMPONENT_DOWN, INT_0);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, "");
        internalEventChannel.dispatch(event);
    }

    public void acceptComponentAdded(String componentName, int componentType, String parentComponentName, int currentState)
    {
        //do nothing for now.
    }

    public void acceptComponentRemoved(String componentName, String[] parentComponents)
    {
        //do nothing for now.
    }
}
