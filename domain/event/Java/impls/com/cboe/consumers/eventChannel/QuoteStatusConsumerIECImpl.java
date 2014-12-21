package com.cboe.consumers.eventChannel;

import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapter;
import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapterFactory;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.domain.util.*;
import com.cboe.idl.cmiOrder.BustReportStruct;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiQuote.QuoteStruct;
import com.cboe.idl.constants.QuoteStatusTypes;
import com.cboe.idl.quote.*;
import com.cboe.idl.cmiConstants.StatusUpdateReasons;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.QuoteStatusConsumerV2;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;

/**
 * OrderStatusConsumer
 *
 * @author Jeff Illian
 * @author Gijo Joseph
 */

public class QuoteStatusConsumerIECImpl extends BObject implements QuoteStatusConsumerV2 {
    // Flags for dispatching to user, firm user, or both
    public static byte DISPATCH_USER = 1;
    public static byte DISPATCH_FIRM = 2;
    public static byte DISPATH_TRADING_FIRM = 4; // 100
    public static byte DISPATCH_ALL = (byte)(DISPATCH_USER | DISPATCH_FIRM |DISPATH_TRADING_FIRM); //111 =7

    private ConcurrentEventChannelAdapter internalEventChannel = null;

    // Delay value for perf testing
    private int blockingDelay = 0;

    /**
     * OrderStatusListener constructor comment.
     */
    public QuoteStatusConsumerIECImpl() {
        super();
        try
        {
//        internalEventChannel = InstrumentedEventChannelAdapterFactory.find();
        	internalEventChannel = ClientIECFactory.findConcurrentEventChannelAdapter(ClientIECFactory.CAS_INSTRUMENTED_IEC);
        }
        catch (Exception e)
        {
        	Log.exception(this, "Exception getting CAS_INSTRUMENTED_IEC!", e);
        }

        try
        {
            String delayProperty = System.getProperty("quoteBlockingDelay", "0");
            blockingDelay = Integer.parseInt(delayProperty);
            if (Log.isDebugOn())
            {
                Log.debug(this, "quoteBlockingDelay value set to " + blockingDelay);
            }
        }
        catch(NumberFormatException e)
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "quoteBlockingDelay value is not an integer");
            }
        }
    }

    private void doAcceptQuoteFillReport(int[] groups, QuoteInfoStruct quoteInfo, short statusChange, FilledReportStruct[] filledQuote, byte dispatchFlag) {
        ChannelKey channelKey = null;
        ChannelEvent event = null;

        if ((dispatchFlag & DISPATCH_USER) == DISPATCH_USER)
        {
            channelKey = new ChannelKey(ChannelKey.QUOTE_FILL_REPORT, quoteInfo.userId);
	        event = internalEventChannel.getChannelEvent(this, channelKey, new QuoteKeyFillReportContainer(groups, quoteInfo, statusChange, filledQuote));
	        internalEventChannel.dispatch(event);
        }
        if((dispatchFlag & DISPATH_TRADING_FIRM) == DISPATH_TRADING_FIRM)
        {
            channelKey = new ChannelKey(ChannelKey.QUOTE_FILL_REPORT_BY_TRADING_FIRM, quoteInfo.userId);
	        event = internalEventChannel.getChannelEvent(this, channelKey, new QuoteKeyFillReportContainer(groups, quoteInfo, statusChange, filledQuote));
	        internalEventChannel.dispatch(event);
        }
        if ((dispatchFlag & DISPATCH_FIRM) == DISPATCH_FIRM)
        {
            ExchangeFirmStructContainer firmKeyContainer = new ExchangeFirmStructContainer(quoteInfo.firm);
	        channelKey = new ChannelKey(ChannelKey.QUOTE_FILL_REPORT_BY_FIRM, firmKeyContainer);
	        event = internalEventChannel.getChannelEvent(this, channelKey, new QuoteKeyFillReportContainer(groups, quoteInfo, statusChange, filledQuote));
	        internalEventChannel.dispatch(event);
        }
    }
    public void acceptQuoteFillReport(int[] groups, QuoteInfoStruct quoteInfo, short statusChange, FilledReportStruct[] filledQuote,String eventInitiator) {
        StringBuilder received = new StringBuilder(90);
        received.append("event received -> QuoteFilledReport-pKey:").append(quoteInfo.productKey)
                .append(" qKey:").append(quoteInfo.quoteKey)
                .append(" user:").append(quoteInfo.userId);
        Log.information(this, received.toString());

        byte dispatchFlag = 0;
        if (statusChange != StatusUpdateReasons.POSSIBLE_RESEND)
        {
            dispatchFlag = DISPATCH_ALL;
        }
        else
        {
            if(eventInitiator.indexOf(":") != -1)
            {
                dispatchFlag = DISPATCH_FIRM;
            }
            else if(eventInitiator.indexOf("&") != -1)
            {
                dispatchFlag = DISPATH_TRADING_FIRM;    
            }
            else
            {
                dispatchFlag = DISPATCH_USER;
            }
        }
        doAcceptQuoteFillReport(groups, quoteInfo, statusChange, filledQuote, dispatchFlag);
    }

    public void acceptQuoteFillReportV3(RoutingParameterStruct routing, QuoteInfoStruct quoteInfo, short statusChange, FilledReportStruct[] filledQuote,String eventInitiator) {
        StringBuilder received = new StringBuilder(90);
        received.append("event received -> QuoteFilledReportV3-pKey:").append(quoteInfo.productKey)
                .append(" qKey:").append(quoteInfo.quoteKey)
                .append(" user:").append(quoteInfo.userId);
        Log.information(this, received.toString());

        byte dispatchFlag = 0;
        if (statusChange != StatusUpdateReasons.POSSIBLE_RESEND)
        {
            dispatchFlag = DISPATCH_ALL;
        }
        else
        {
            if(eventInitiator.indexOf(":") != -1)
            {
                dispatchFlag = DISPATCH_FIRM;
            }
            else if(eventInitiator.indexOf("&") != -1)
            {
                dispatchFlag = DISPATH_TRADING_FIRM;    
            }
            else
            {
                dispatchFlag = DISPATCH_USER;
            }
        }
        doAcceptQuoteFillReport(routing.groups, quoteInfo, statusChange, filledQuote, dispatchFlag);
    }
    
    private void doAcceptQuoteDeleteReport(int[] groups, String userId, int[] quoteKeys, short cancelReason) {
        QuoteKeyCancelReportContainer quoteKeysContainer = new QuoteKeyCancelReportContainer(groups, quoteKeys, cancelReason);
        ChannelKey channelKey = new ChannelKey(ChannelType.QUOTE_DELETE_REPORT, userId);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, quoteKeysContainer);
        internalEventChannel.dispatch(event);
    }

    public void acceptQuoteDeleteReport(int[] groups, String userId, int[] quoteKeys, short cancelReason,String eventInitiator) {
        StringBuilder received = new StringBuilder(80);
        received.append("event received -> QuoteDeleteReport-user:").append(userId)
                .append(" quotes:").append(quoteKeys.length)
                .append(" reason:").append(cancelReason);
        Log.information(this, received.toString());
        doAcceptQuoteDeleteReport(groups, userId, quoteKeys, cancelReason);
    }

    private void doAcceptQuoteDeleteReportV2(int[] groups, String userId, QuoteStruct[] quotes, short cancelReason) {
        QuoteCancelReportContainer quotesContainer = new QuoteCancelReportContainer(groups, quotes, cancelReason);
        ChannelKey channelKey = new ChannelKey(ChannelType.QUOTES_DELETE_REPORTV2, userId);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, quotesContainer);
        internalEventChannel.dispatch(event);
    }

    public void acceptQuoteDeleteReportV2(int[] groups, String userId, QuoteStruct[] quotes, short cancelReason,String eventInitiator) {
        StringBuilder received = new StringBuilder(80);
        received.append("event received -> QuoteDeleteReportV2-user:").append(userId)
                .append(" quotes:").append(quotes.length)
                .append(" reason:").append(cancelReason);
        Log.information(this, received.toString());
        doAcceptQuoteDeleteReportV2(groups, userId, quotes, cancelReason);
    }
    
    public void acceptQuoteDeleteReportV3(RoutingParameterStruct routingParameters, String userId, QuoteStruct[] quotes, short cancelReason,String eventInitiator) {
        StringBuilder received = new StringBuilder(80);
        received.append("event received -> QuoteDeleteReportV3-user:").append(userId)
                .append(" quotes:").append(quotes.length)
                .append(" reason:").append(cancelReason);
        Log.information(this, received.toString());
        doAcceptQuoteDeleteReportV2(routingParameters.groups, userId, quotes, cancelReason);
   }
    
    private void doAcceptQuoteBustReport( int[] groups, QuoteInfoStruct quoteInfo, short statusChange, BustReportStruct[] bustedQuote, byte dispatchFlag) {
        ChannelKey channelKey = null;
        ChannelEvent event = null;

        if ((dispatchFlag & DISPATCH_USER) == DISPATCH_USER)
        {
	        channelKey = new ChannelKey(ChannelKey.QUOTE_BUST_REPORT, quoteInfo.userId);
	        event = internalEventChannel.getChannelEvent(this, channelKey, new QuoteKeyBustReportContainer(groups, quoteInfo, statusChange, bustedQuote));
	        internalEventChannel.dispatch(event);
        }
        if((dispatchFlag & DISPATH_TRADING_FIRM) == DISPATH_TRADING_FIRM)
        {
            channelKey = new ChannelKey(ChannelKey.QUOTE_BUST_REPORT_BY_TRADING_FIRM, quoteInfo.userId);
            event = internalEventChannel.getChannelEvent(this, channelKey, new QuoteKeyBustReportContainer(groups, quoteInfo, statusChange, bustedQuote));
            internalEventChannel.dispatch(event);
        }
        if ((dispatchFlag & DISPATCH_FIRM) == DISPATCH_FIRM)
        {
	        ExchangeFirmStructContainer firmKeyContainer = new ExchangeFirmStructContainer(quoteInfo.firm);
	        channelKey = new ChannelKey(ChannelKey.QUOTE_BUST_REPORT_BY_FIRM, firmKeyContainer);
	        event = internalEventChannel.getChannelEvent(this, channelKey, new QuoteKeyBustReportContainer(groups, quoteInfo, statusChange, bustedQuote));
	        internalEventChannel.dispatch(event);
        }
    }

    public void acceptQuoteBustReport( int[] groups, QuoteInfoStruct quoteInfo, short statusChange, BustReportStruct[] bustedQuote,String eventInitiator) {
        StringBuilder received = new StringBuilder(80);
        received.append("event received -> QuoteBustReport-pKey:").append(quoteInfo.productKey)
                .append("qKey:").append(quoteInfo.quoteKey)
                .append(" user:").append(quoteInfo.userId);
        Log.information(this, received.toString());

        byte dispatchFlag = 0;
        if (statusChange != StatusUpdateReasons.POSSIBLE_RESEND)
        {
            dispatchFlag = DISPATCH_ALL;
        }
        else
        {
            if(eventInitiator.indexOf(":") != -1)
            {
                dispatchFlag = DISPATCH_FIRM;
            }
            else if(eventInitiator.indexOf("&") != -1)
            {
                dispatchFlag = DISPATH_TRADING_FIRM;    
            }
            else
            {
                dispatchFlag = DISPATCH_USER;
            }
        }
        doAcceptQuoteBustReport(groups, quoteInfo, statusChange, bustedQuote, dispatchFlag);
    }

    public void acceptQuoteBustReportV3( RoutingParameterStruct routingParameters, QuoteInfoStruct quoteInfo, short statusChange, BustReportStruct[] bustedQuote,String eventInitiator) {
        StringBuilder received = new StringBuilder(80);
        received.append("event received -> QuoteBustReportV3-pKey:").append(quoteInfo.productKey)
                .append("qKey:").append(quoteInfo.quoteKey)
                .append(" user:").append(quoteInfo.userId);
        Log.information(this, received.toString());

        byte dispatchFlag = 0;
        if (statusChange != StatusUpdateReasons.POSSIBLE_RESEND)
        {
            dispatchFlag = DISPATCH_ALL;
        }
        else
        {
            if(eventInitiator.indexOf(":") != -1)
            {
                dispatchFlag = DISPATCH_FIRM;
            }
            else if(eventInitiator.indexOf("&") != -1)
            {
                dispatchFlag = DISPATH_TRADING_FIRM;    
            }
            else
            {
                dispatchFlag = DISPATCH_USER;
            }
        }
        doAcceptQuoteBustReport(routingParameters.groups, quoteInfo, statusChange, bustedQuote, dispatchFlag);
    }
    
    private void doAcceptQuoteStatusUpdate(RoutingParameterStruct routingParameters,  QuoteStruct quote, short statusChange)
    {
        RoutingGroupQuoteContainer dataContainer = new RoutingGroupQuoteContainer(routingParameters, quote, statusChange);
        ChannelKey channelKey = null;
        ChannelEvent event = null;
        channelKey = new ChannelKey(ChannelKey.QUOTE_STATUS_UPDATE, quote.userId);
        event = internalEventChannel.getChannelEvent(this, channelKey, dataContainer);
        internalEventChannel.dispatch(event);
    }

    public void acceptQuoteStatusUpdate(RoutingParameterStruct routingParameters,  QuoteStruct quote, short statusChange)
    {
        StringBuilder received = new StringBuilder(60);
        received.append("event received -> QuoteStatusUpdate : ")
                .append(quote.userId).append(':').append(quote.quoteKey);
        Log.information(this, received.toString());
        doAcceptQuoteStatusUpdate(routingParameters, quote, statusChange);
    }
    
    
    public void acceptQuoteStatus(short[] seqmap, 
    							com.cboe.idl.quote.GroupQuoteFillReportStruct[] fillReports, 
    							com.cboe.idl.quote.GroupQuoteFillReportV3Struct[] fillReportsV3, 
    							com.cboe.idl.quote.GroupQuoteDeleteReportStruct[] deleteReports, 
    							com.cboe.idl.quote.GroupQuoteDeleteReportV2Struct[] deleteReportsV2, 
    							com.cboe.idl.quote.GroupQuoteDeleteReportV3Struct[] deleteReportsV3, 
    							com.cboe.idl.quote.GroupQuoteBustReportStruct[] bustReports, 
    							com.cboe.idl.quote.GroupQuoteBustReportV3Struct[] bustReportsV3, 
    							com.cboe.idl.quote.GroupQuoteStatusUpdateStruct[] statusUpdates)
    {
    	StringBuilder s = new StringBuilder(220);
    	s.append("event received -> BlockedQuote message: Fill count = ");
    	s.append(fillReports.length);
    	s.append(": FillV3 count = ");
    	s.append(fillReportsV3.length);
    	s.append(": Delete count = ");
    	s.append(deleteReports.length);
    	s.append(": DeleteV2 count = ");
    	s.append(deleteReportsV2.length);
    	s.append(": DeleteV3 count = ");
    	s.append(deleteReportsV3.length);
    	s.append(": Bust count = ");
    	s.append(bustReports.length);
    	s.append(": BustV3 count = ");
    	s.append(bustReportsV3.length);
    	s.append(": StatusUpdates count = ");
    	s.append(statusUpdates.length);   	
        Log.information(this, s.toString());

        // seqmap - 
    	for (short i: seqmap)
    	{
    		int type = i / QuoteStatusTypes.MULTIPLIER;
    		int index = i % QuoteStatusTypes.MULTIPLIER;
            s.setLength(0);

            switch (type)
    		{
    			case QuoteStatusTypes.QUOTE_FILL_REPORT:
    				GroupQuoteFillReportStruct fillReport = fillReports[index];
                    s.append("processing deblocked event -> QuoteFilledReport-pKey:")
                     .append(fillReport.quoteInfo.productKey)
                     .append(" qKey:").append(fillReport.quoteInfo.quoteKey)
                     .append(" user:").append(fillReport.quoteInfo.userId);
                    Log.information(this, s.toString());
    				doAcceptQuoteFillReport(fillReport.groups, fillReport.quoteInfo, fillReport.statusChange, fillReport.filledQuote, 
    						fillReport.isFirmUser == true ? DISPATCH_FIRM : DISPATCH_USER);
    				break;
    			
    			case QuoteStatusTypes.QUOTE_FILL_REPORTV3:
					GroupQuoteFillReportV3Struct fillReportV3 = fillReportsV3[index];
                    s.append("processing deblocked event -> QuoteFilledReportV3-pKey:")
                     .append(fillReportV3.quoteInfo.productKey)
                     .append(" qKey:").append(fillReportV3.quoteInfo.quoteKey)
                     .append(" user:").append(fillReportV3.quoteInfo.userId);
                    Log.information(this, s.toString());
					doAcceptQuoteFillReport(fillReportV3.routingParameters.groups, fillReportV3.quoteInfo, fillReportV3.statusChange, fillReportV3.filledQuote,
							fillReportV3.isFirmUser == true ? DISPATCH_FIRM : DISPATCH_USER);
					break;
				
    			case QuoteStatusTypes.QUOTE_DELETE_REPORT:
					GroupQuoteDeleteReportStruct deleteReport = deleteReports[index];
                    s.append("processing deblocked event -> QuoteDeleteReport-user:")
                     .append(deleteReport.userId)
                     .append(" quotes:").append(deleteReport.quoteKeys.length)
                     .append(" reason:").append(deleteReport.cancelReason);
                    Log.information(this, s.toString());
					doAcceptQuoteDeleteReport(deleteReport.groups, deleteReport.userId, deleteReport.quoteKeys, deleteReport.cancelReason);
					break;
				
    			case QuoteStatusTypes.QUOTE_DELETE_REPORTV2:
					GroupQuoteDeleteReportV2Struct deleteReportV2 = deleteReportsV2[index];
                    s.append("processing deblocked event -> QuoteDeleteReportV2-user:")
                     .append(deleteReportV2.userId)
                     .append(" quotes:").append(deleteReportV2.quotes.length)
                     .append(" reason:").append(deleteReportV2.cancelReason);
                    Log.information(this, s.toString());
			        doAcceptQuoteDeleteReportV2(deleteReportV2.groups, deleteReportV2.userId, deleteReportV2.quotes, deleteReportV2.cancelReason);
					break;
				
    			case QuoteStatusTypes.QUOTE_DELETE_REPORTV3:
					GroupQuoteDeleteReportV3Struct deleteReportV3 = deleteReportsV3[index];
                    s.append("processing deblocked event -> QuoteDeleteReportV2-user:")
                     .append(deleteReportV3.userId)
                     .append(" quotes:").append(deleteReportV3.quotes.length)
                     .append(" reason:").append(deleteReportV3.cancelReason);
                    Log.information(this, s.toString());
			        doAcceptQuoteDeleteReportV2(deleteReportV3.routingParameters.groups, deleteReportV3.userId, deleteReportV3.quotes, deleteReportV3.cancelReason);
					break;
				
    			case QuoteStatusTypes.QUOTE_BUST_REPORT:
					GroupQuoteBustReportStruct bustReport = bustReports[index];
                    s.append("processing deblocked event -> QuoteBustReport-pKey:")
                     .append(bustReport.quoteInfo.productKey)
                     .append("qKey:").append(bustReport.quoteInfo.quoteKey)
                     .append(" user:").append(bustReport.quoteInfo.userId);
                    Log.information(this, s.toString());
					doAcceptQuoteBustReport(bustReport.groups, bustReport.quoteInfo, bustReport.statusChange, bustReport.bustedQuote,
							bustReport.isFirmUser == true ? DISPATCH_FIRM : DISPATCH_USER);
					break;
				
    			case QuoteStatusTypes.QUOTE_BUST_REPORTV3:
					GroupQuoteBustReportV3Struct bustReportV3 = bustReportsV3[index];
                    s.append("processing deblocked event -> QuoteBustReportV3-pKey:")
                     .append(bustReportV3.quoteInfo.productKey)
                     .append("qKey:").append(bustReportV3.quoteInfo.quoteKey)
                     .append(" user:").append(bustReportV3.quoteInfo.userId);
                    Log.information(this, s.toString());
					doAcceptQuoteBustReport(bustReportV3.routingParameters.groups, bustReportV3.quoteInfo, bustReportV3.statusChange, bustReportV3.bustedQuote,
							bustReportV3.isFirmUser == true ? DISPATCH_FIRM : DISPATCH_USER);
					break;
				
    			case QuoteStatusTypes.QUOTE_STATUS_UPDATE:
					GroupQuoteStatusUpdateStruct statusUpdate = statusUpdates[index];
                    s.append("processing deblocked event -> QuoteStatusUpdate : ")
                     .append(statusUpdate.quote.userId)
                     .append(':').append(statusUpdate.quote.quoteKey);
                    Log.information(this, s.toString());
					acceptQuoteStatusUpdate(statusUpdate.routingParameters,  statusUpdate.quote, statusUpdate.statusChange);
					break;
    		}
    	}
    
        // Delay for perf testing
        try
        {
            if(blockingDelay > 0)
            {
                if (Log.isDebugOn())
                {
                    Log.debug(this, "Sleeping for " + blockingDelay + " milliseconds in acceptOrderStatus(...)");
                }
                Thread.sleep(blockingDelay);
            }
        }
        catch(InterruptedException e){}

    }

}
