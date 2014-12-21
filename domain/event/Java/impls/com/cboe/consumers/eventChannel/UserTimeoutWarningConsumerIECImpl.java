package com.cboe.consumers.eventChannel;

/**
 * UserTimeoutWarningConsumer listener object listens on the CBOE event channel as an BBBOConsumer.
 * There will only be a single best book listener per CAS.
 *
 * @author Connie Feng
 */
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.infrastructureServices.orbService.*;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.infrastructureServices.traderService.*;
import org.omg.CORBA.Object;
import org.omg.CORBA.Any;

public class UserTimeoutWarningConsumerIECImpl extends BObject implements com.cboe.interfaces.events.UserTimeoutWarningConsumer{
    private EventChannelAdapter internalEventChannel;
    /**
     * MarketBestListener constructor comment.
     */
    public UserTimeoutWarningConsumerIECImpl() {
        super();
        internalEventChannel = EventChannelAdapterFactory.find();
    }
    /**
     * This method is called by the CORBA event channel when a BBBO event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Jeff Illian
     */
    public void acceptUserTimeoutWarning(String userName) {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> acceptUserTimeoutWarning : " + userName);
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.USER_SECURITY_TIMEOUT, userName);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, userName);
        internalEventChannel.dispatch(event);
    }

    public Object get_typed_consumer() {
        //Implement this org.omg.CosTypedEventComm.TypedPushConsumerOperations method;
        return null;
    }

    public void push(Any parm1) throws org.omg.CosEventComm.Disconnected {
        //Implement this org.omg.CosEventComm.PushConsumerOperations method;
    }

    public void disconnect_push_consumer() {
        //Implement this org.omg.CosEventComm.PushConsumerOperations method;
    }
 }
