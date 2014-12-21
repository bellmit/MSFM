package com.cboe.consumers.callback;

import com.cboe.application.shared.*;
import com.cboe.interfaces.callback.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.*;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

/**
 * This is the implementation of the CMINBBOConsumer callback object which
 * receives market best data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 *
 * @author Jimmy Wang
 * @version 06/07/2000
 */

public class NBBOConsumerImpl implements NBBOConsumer
{
    public static final int LOG_COUNT = 100;
    private EventChannelAdapter eventChannel = null;
    private int channelType = 0;
    protected int count;

    /**
     * CurrentMarketConsumerImpl constructor.
     *
     * @author Jimmy
     *
     * @param channelType the channel type to publish on.
     * @param eventChannel the event channel to publish to.
     */
    public NBBOConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();

        this.eventChannel = eventChannel;
        this.count = 0;
    }

    /**
     * The callback method used by the CAS to publish NBBO data.
     *
     * @author Jimmy Wang
     *
     * @param NBBO the NBBO data to publish to all subscribed listeners
     */
    public void acceptNBBO(NBBOStruct[] NBBOs)
    {
        ChannelKey key = null;
        ChannelEvent event = null;

        for (int i = 0; i < NBBOs.length; i++)
        {
            key = new ChannelKey(ChannelType.CB_NBBO_BY_PRODUCT, new SessionKeyContainer(NBBOs[i].sessionName, NBBOs[i].productKeys.productKey));
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, NBBOs[i]);
            eventChannel.dispatch(event);

            key = new ChannelKey(ChannelType.CB_NBBO_BY_CLASS, new SessionKeyContainer(NBBOs[i].sessionName, NBBOs[i].productKeys.classKey));
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, NBBOs[i]);
            eventChannel.dispatch(event);

            this.count++;
            if(GUILoggerHome.find().isDebugOn() && this.count % LOG_COUNT == 0 )
            {
                String item = NBBOs[i].sessionName + "."+ NBBOs[i].productKeys.productKey;
                GUILoggerHome.find().debug(this.getClass().getName() + ".acceptNBBO() Count for "+item+" ",
                                       GUILoggerBusinessProperty.COMMON,String.valueOf(this.count));
            }

        }
    }
}
