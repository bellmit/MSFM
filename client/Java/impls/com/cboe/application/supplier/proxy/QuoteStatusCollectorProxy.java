package com.cboe.application.supplier.proxy;

import com.cboe.idl.quote.QuoteAcknowledgeStruct;
import com.cboe.domain.util.QuoteKeyFillReportContainer;
import com.cboe.domain.util.QuoteKeyBustReportContainer;
import com.cboe.domain.util.GroupQuoteKeySequenceContainer;
import com.cboe.interfaces.application.*;

import com.cboe.idl.cmiSession.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiUtil.CallbackInformationStruct;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.interfaces.application.IORMaker;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ChannelAdapter;
import com.cboe.domain.supplier.proxy.BaseSupplierProxy;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

import com.cboe.application.supplier.*;
/**
 * QuoteStatusCollectorProxy serves as a proxy to the OrderQueryConsumer
 * object on the presentation side in com.cboe.presentation.consumer.  The
 * QuoteStatusSupplier on the CAS uses this proxy object to communicate to
 * the GUI callback object.
 *
 *
 * @author Keith A. Korecky
 */

public class QuoteStatusCollectorProxy extends InstrumentedCollectorProxy
{
    // the CORBA callback object.
    private QuoteStatusCollector quoteStatusCollector;

    /**
     * QuoteStatusCollectorProxy constructor.
     *
     * @author Keith A. Korecky
     *
     * @param QuoteStatusCollector a reference to the proxied implementation object.
     * @param stringIOR - stringified IOR for BaseConsumerProxy hash table usage
     */
    public QuoteStatusCollectorProxy(QuoteStatusCollector quoteStatusCollector, BaseSessionManager sessionManager, Object hashKey )
    {
        super( sessionManager, QuoteStatusCollectorSupplierFactory.find(), hashKey );
        this.quoteStatusCollector = quoteStatusCollector;
    }

    /**
     * This method is called by ChannelThreadCommand object.  It takes the passed
     * EventChannelEvent, parses out the relevant data for the proxied object,
     * and calls the proxied objects callback method passing in the appropriate
     * data.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param event the ChannelEvent containing the data to send the listener.
     */
    public void channelUpdate(ChannelEvent event)
    {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this,"Got channel update " + event);
    	}

        ChannelKey  channelKey  = (ChannelKey)event.getChannel();
        Object      channelData = (Object)event.getEventData();

        if (quoteStatusCollector != null)
        {
            switch (channelKey.channelType)
            {
                case ChannelType.QUOTE_FILL_REPORT:
                case ChannelType.QUOTE_FILL_REPORT_BY_FIRM:
                case ChannelType.QUOTE_FILL_REPORT_BY_TRADING_FIRM:                    
                    QuoteKeyFillReportContainer quoteKeyFillReportContainer = (QuoteKeyFillReportContainer)channelData;
                    quoteStatusCollector.acceptQuoteFillReport( quoteKeyFillReportContainer.getQuoteInfoStruct(),
                                                                quoteKeyFillReportContainer.getFilledReportStruct(),
                                                                quoteKeyFillReportContainer.getStatusChange() );
                break;

                case ChannelType.QUOTE_DELETE_REPORT:
                    QuoteDeleteReportWrapper[] deletedQuotes = (QuoteDeleteReportWrapper[])channelData;
                    quoteStatusCollector.acceptQuoteDeleteReports(deletedQuotes);
                break;

                case ChannelType.QUOTE_BUST_REPORT:
                case ChannelType.QUOTE_BUST_REPORT_BY_FIRM:
                case ChannelType.QUOTE_BUST_REPORT_BY_TRADING_FIRM:
                    QuoteKeyBustReportContainer quoteKeyBustReportContainer = (QuoteKeyBustReportContainer)channelData;
                    quoteStatusCollector.acceptQuoteBustReport ( quoteKeyBustReportContainer.getQuoteInfoStruct(),
                                            quoteKeyBustReportContainer.getBustReportStruct(),
                                            quoteKeyBustReportContainer.getStatusChange() );
                break;

                case ChannelType.QUOTE_STATUS:
                case ChannelType.QUOTE_STATUS_UPDATE:
                    QuoteDetailStruct[] quoteDetails = (QuoteDetailStruct[])channelData;
                    quoteStatusCollector.acceptAddQuotes(quoteDetails);
                break;

                default :
                	if (Log.isDebugOn())
                	{
                		Log.debug(this, "Wrong Channel : " + channelKey.channelType);
                	}
                break;
            }
        }
    }

    public CallbackInformationStruct getCallbackInformationStruct(ChannelEvent event)
    {
        String interfaceName = "QuoteStatusCollector";
        String method = "";
        String methodValue = "";

        CallbackInformationStruct callbackInfo = new CallbackInformationStruct(
                                                            interfaceName,
                                                            method,
                                                            methodValue,
                                                            getHashKey().toString());
        return callbackInfo;
    }

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.QUOTE;
    }

}
