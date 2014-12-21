package com.cboe.application.supplier.proxy;

import com.cboe.application.supplier.BookDepthCollectorSupplierFactory;
import com.cboe.idl.cmiMarketData.BookDepthStruct;
import com.cboe.idl.cmiUtil.CallbackInformationStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.BookDepthCollector;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;

/**
 * BookDepthCollectorProxy serves as a proxy to the BookDepthConsumer
 * object on the presentation side in com.cboe.presentation.consumer.  The
 * BookDepthSupplier on the CAS uses this proxy object to communicate to
 * the GUI callback object.
 *
 * @author William Wei
 */

public class BookDepthCollectorProxy extends InstrumentedCollectorProxy
{
    // the CORBA callback object.
    private BookDepthCollector bookDepthCollector;

    /**
     * BookDepthCollectorProxy constructor.
     *
     * @param bookDepthCollector a reference to the proxied implementation object.
     * @param sessionManager the SessionManager managing subscriptions for this proxy.
     * @param hashKey object to supply hash code for BaseSupplierProxy hash table usage.
     */
    public BookDepthCollectorProxy(BookDepthCollector bookDepthCollector, BaseSessionManager sessionManager, Object hashKey)
    {
        super( sessionManager, BookDepthCollectorSupplierFactory.find(), hashKey );
        setHashKey(bookDepthCollector);
        this.bookDepthCollector = bookDepthCollector;
    }

    /**
     * This method is called by ChannelThreadCommand object.  It takes the passed
     * EventChannelEvent, parses out the relevant data for the proxied object,
     * and calls the proxied objects callback method passing in the appropriate
     * data.
     *
     * @param event the ChannelEvent containing the data to send the listener.
     */
    public void channelUpdate(ChannelEvent event)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this,"BookDepthCollectorProxy: channelUupdate " + event);
        }
        ChannelKey channelKey = (ChannelKey)event.getChannel();
        if (bookDepthCollector != null)
        {
            switch (channelKey.channelType)
            {
                case ChannelType.BOOK_DEPTH_BY_CLASS:
                    BookDepthStruct[] bookDepthStructs = (BookDepthStruct[])event.getEventData();
                    bookDepthCollector.acceptBookDepthsForClass( bookDepthStructs );
                    break;

                default :
                    if (Log.isDebugOn())
                    {
                        Log.debug(this, "Illegal internal publish channel : " + channelKey.channelType);
                    }
                    break;
            }
        }
    }

    public CallbackInformationStruct getCallbackInformationStruct(ChannelEvent event)
    {
        return null;
    }

    public void startMethodInstrumentation(boolean privateOnly){}
    public void stopMethodInstrumentation(){}

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.BOOK_DEPTH;
    }
}
