package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface Event Channel Consumer Homes should implement
 * @author Connie Feng
 */
public interface EventChannelConsumerManager
{
    /**
    * Adds the event channel listner to the CBOE event channel and the
    * Internal Event Channel(IEC) that is used to process events received
    * from the CBOE event channel.
    * @param listener the event channel listener for IEC
    * @param channelKey the CBOE event channel type and key information for event filter
    * @return none
    * @author Connie Feng
    */
    public void addFilter( ChannelKey filterKey ) throws
        SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
    * Removes the event channel listner from the CBOE event channel and the
    * Internal Event Channel(IEC) that is used to process events received
    * from the CBOE event channel.
    * @param listener the event channel listener for IEC
    * @param channelKey the CBOE event channel type and key information for event filter
    * @return none
    * @author Connie Feng
    */
    public void removeFilter( ChannelKey filterKey ) throws
        SystemException, CommunicationException, AuthorizationException, DataValidationException;

}

