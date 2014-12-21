package com.cboe.application.inprocess.consumer.proxy;

import com.cboe.application.quote.QuoteCacheFactory;
import com.cboe.application.supplier.proxy.QuoteStatusCollectorProxy;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.domain.util.QuoteKeyBustReportContainer;
import com.cboe.domain.util.QuoteKeyFillReportContainer;
import com.cboe.domain.util.DateWrapper;
import com.cboe.domain.util.UserClassContainer;
import com.cboe.idl.cmiConstants.StatusUpdateReasons;
import com.cboe.idl.cmiOrder.BustReportStruct;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.idl.quote.QuoteAcknowledgeStruct;
import com.cboe.idl.quote.QuoteInfoStruct;
import com.cboe.idl.quote.QuoteAcknowledgeStructV3;
import com.cboe.idl.util.TransactionClockPointStruct;
import com.cboe.idl.constants.TransactionClockPoints;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.QuoteDeleteReportWrapper;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.ProductQueryServiceAdapter;
import com.cboe.interfaces.application.inprocess.QuoteStatusConsumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ListenerProxyQueueControl;
import com.cboe.exceptions.*;

import java.util.Hashtable;

/**
 * @author Jing Chen
 */

public class QuoteStatusConsumerProxy extends QuoteStatusCollectorProxy
{
    private QuoteStatusConsumer quoteStatusConsumer;
    private static int DEFAULT_SIZE = 10;
    private Hashtable possibleResendFilledReports = null;
    private Hashtable possibleResendBustedReports = null;
    private ListenerProxyQueueControl proxyWrapper;

    private ProductQueryServiceAdapter pqAdapter;

    public QuoteStatusConsumerProxy(QuoteStatusConsumer quoteStatusConsumer,
                                       BaseSessionManager sessionManager)
    {
        super(null, sessionManager, quoteStatusConsumer);
        this.quoteStatusConsumer = quoteStatusConsumer;
    }

    private Hashtable getPossibleResendFilledReports()
    {
        if (possibleResendFilledReports == null)
        {
            possibleResendFilledReports = new Hashtable(DEFAULT_SIZE);
        }
        return possibleResendFilledReports;
    }

    private Hashtable getPossibleResendBustedReports()
    {
       if (possibleResendBustedReports == null)
       {
           possibleResendBustedReports = new Hashtable(DEFAULT_SIZE);
       }
       return possibleResendBustedReports;
    }

    public ListenerProxyQueueControl getProxyWrapper()
    {
        if (proxyWrapper == null)
        {
            proxyWrapper = this.getChannelAdapter().getProxyForDelegate(this);
        }
        return proxyWrapper;
    }

    public void channelUpdate(ChannelEvent event)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this,"Got channel update " + event);
        }

        ChannelKey  channelKey  = (ChannelKey)event.getChannel();
        Object      channelData = event.getEventData();

        try
        {
            if (quoteStatusConsumer != null)
            {
                switch (channelKey.channelType)
                {
                    case ChannelType.QUOTE_FILL_REPORT:
                    case ChannelType.QUOTE_FILL_REPORT_BY_FIRM:
                    case ChannelType.QUOTE_FILL_REPORT_BY_TRADING_FIRM:
                        processQuoteFill(channelData);
                        break;
                    case ChannelType.QUOTE_DELETE_REPORT:
                        processQuoteDelete(event);
                        break;
                    case ChannelType.QUOTE_BUST_REPORT:
                    case ChannelType.QUOTE_BUST_REPORT_BY_FIRM:
                    case ChannelType.QUOTE_BUST_REPORT_BY_TRADING_FIRM:                        
                        processQuoteBust(event);
                        break;
                    case ChannelType.QUOTE_STATUS:
                        processQuoteStatus(event);
                        break;
                    case ChannelType.QUOTE_STATUS_UPDATE:
                        processQuoteStatusUpdate(event);
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
        catch(Exception e)
        {
            Log.exception(this, "session:" + getSessionManager(), e);
            lostConnection(event);
        }
    }

    private void processQuoteStatusUpdate(ChannelEvent event) {
        int numQuotes;
        // Call the proxied method passing the extracted QuoteDetailStruct[] from the EventChannelEvent.
       QuoteDetailStruct[] quoteDetails = (QuoteDetailStruct[])event.getEventData();
       numQuotes = quoteDetails.length;
       String smgr = getSessionManager().toString();
       StringBuilder calling = new StringBuilder(smgr.length()+45);
       calling.append("calling acceptQuoteUpdate for ").append(smgr)
              .append(" size:").append(numQuotes);
       Log.information(this, calling.toString());
       for (int i=0; i<numQuotes; i++)
       {
           quoteStatusConsumer.acceptQuoteUpdate(quoteDetails[i], getProxyWrapper().getQueueSize());
       }

    }

    private void processQuoteStatus(ChannelEvent event) {

                // Call the proxied method passing the extracted QuoteDetailStruct[] from the EventChannelEvent.
        QuoteDetailStruct[] quotes = (QuoteDetailStruct[])event.getEventData();
        int numQuotes;
        numQuotes = quotes.length;
        String smgr = getSessionManager().toString();
        StringBuilder calling = new StringBuilder(smgr.length()+45);
        calling.append("calling acceptQuoteStatus for ").append(smgr)
               .append(" size:").append(numQuotes);
        Log.information(this, calling.toString());
        for (int i=0; i<numQuotes; i++)
        {
            quoteStatusConsumer.acceptQuoteStatus(quotes[i], getProxyWrapper().getQueueSize());
        }
    }

    private void processQuoteBust(ChannelEvent event) throws DataValidationException, SystemException, NotFoundException, AuthorizationException, CommunicationException {
        QuoteDetailStruct quote;
        short statusChange;
        QuoteAcknowledgeStructV3  quoteAck = null;
        ProductStruct productStruct;

        String smgr = getSessionManager().toString();
        StringBuilder calling = new StringBuilder(smgr.length()+40);
        calling.append("calling acceptQuoteBustReport for ").append(smgr);
        Log.information(this, calling.toString());
        QuoteKeyBustReportContainer quoteKeyBustReportContainer = (QuoteKeyBustReportContainer)event.getEventData();

        TransactionClockPointStruct[] clockPoints = getTransactionClockPointStructs();
        clockPoints[0].clockPoint = TransactionClockPoints.CAS_RECEIVE;
        clockPoints[0].timestamp = quoteKeyBustReportContainer.getDateTimeAtCreation();
        clockPoints[1].clockPoint = TransactionClockPoints.CAS_SEND;
        clockPoints[1].timestamp = new DateWrapper ().toDateTimeStruct();

        QuoteInfoStruct quoteBustInfo = quoteKeyBustReportContainer.getQuoteInfoStruct();
        BustReportStruct[] quoteBust = quoteKeyBustReportContainer.getBustReportStruct();
        statusChange = quoteKeyBustReportContainer.getStatusChange();
        if (statusChange == StatusUpdateReasons.POSSIBLE_RESEND )
        {
            // Use quote key with user Id as key
        	UserClassContainer qKey = new UserClassContainer(quoteBustInfo.userId,quoteBustInfo.quoteKey);
            Integer oldTransactionNumber = (Integer)getPossibleResendBustedReports().get(qKey);
            if ( oldTransactionNumber != null && oldTransactionNumber.intValue() >= quoteBustInfo.transactionSequenceNumber )
            {
                if (Log.isDebugOn())
                {
                    Log.debug(this, "ignore possible-resend quote bust for " + getSessionManager().getUserId() +
        		            " oldTxNbr:" + oldTransactionNumber + " on quote" + getQuoteInfoString(quoteBustInfo));
                }
                return;
            }
            else
            {
                getPossibleResendBustedReports().put(qKey, Integer.valueOf(quoteBustInfo.transactionSequenceNumber));
            }
        }
        //PQRefactor: used to be ProductQueryManagerImpl.getProduct (synchronized)
        productStruct = getProductQueryServiceAdapter().getProductByKey(quoteBustInfo.productKey);
        String bustInfo = getQuoteInfoString(quoteBustInfo);
        calling.ensureCapacity(smgr.length()+bustInfo.length()+70);
        calling.setLength(0);
        calling.append("calling acceptQuoteBustReport for ").append(smgr).append(bustInfo)
               .append(" queueSize=").append(getProxyWrapper().getQueueSize())
               .append(";statusChange=").append(statusChange);
        Log.information(this, calling.toString());
        quoteStatusConsumer.acceptQuoteBustReport(quoteBust, productStruct, quoteBustInfo.quoteKey, statusChange, getProxyWrapper().getQueueSize());
        calling.setLength(0);
        calling.append("finished calling acceptQuoteBustReport for ").append(smgr).append(bustInfo);
        Log.information(this, calling.toString());
        quoteAck = new QuoteAcknowledgeStructV3();
        quoteAck.acknowledgingUserId = sessionManager.getUserId();
        quoteAck.submittingUserId = quoteBustInfo.userId;
        quoteAck.quoteKey = quoteBustInfo.quoteKey;
        quoteAck.productKey = quoteBustInfo.productKey;
        quoteAck.classKey = productStruct.productKeys.classKey;
        quoteAck.transactionSequenceNumber = quoteBustInfo.transactionSequenceNumber;
        // CAS_ACK

        clockPoints[2].clockPoint = TransactionClockPoints.USER_ACK;
        clockPoints[2].timestamp = new DateWrapper ().toDateTimeStruct();
        ((SessionManager)sessionManager).ackQuoteStatusV3(quoteAck);
    }

    private void processQuoteDelete(ChannelEvent event) {

        QuoteDeleteReportWrapper[] deletedQuotes = (QuoteDeleteReportWrapper[])event.getEventData();
        int numQuotes = deletedQuotes.length;
        String smgr = getSessionManager().toString();
        StringBuilder calling = new StringBuilder(smgr.length()+50);
        calling.append("calling acceptQuoteDeleteReport for ").append(smgr)
               .append(" size:").append(numQuotes);
        Log.information(this, calling.toString());
        for (int i = 0; i < numQuotes; ++i)
        {
            QuoteDetailStruct details = deletedQuotes[i].getQuoteDetailStruct();
            short reason = deletedQuotes[i].getQuoteCancelReportStruct().cancelReason;
            quoteStatusConsumer.acceptQuoteDeleteReport(details, reason, getProxyWrapper().getQueueSize());
        }
    }

    private void processQuoteFill(Object channelData) throws DataValidationException, SystemException, NotFoundException, AuthorizationException, CommunicationException {
        QuoteDetailStruct quote;
        short statusChange;
        QuoteAcknowledgeStructV3  quoteAck = null;

        QuoteKeyFillReportContainer quoteKeyFillReportContainer = (QuoteKeyFillReportContainer)channelData;

        TransactionClockPointStruct[] clockPoints = getTransactionClockPointStructs();
        clockPoints[0].clockPoint = TransactionClockPoints.CAS_RECEIVE;
        clockPoints[0].timestamp = quoteKeyFillReportContainer.getDateTimeAtCreation();
        clockPoints[1].clockPoint = TransactionClockPoints.CAS_SEND;
        clockPoints[1].timestamp = new DateWrapper ().toDateTimeStruct();

        QuoteInfoStruct quoteFillInfo = quoteKeyFillReportContainer.getQuoteInfoStruct();
        FilledReportStruct[] quoteFill = quoteKeyFillReportContainer.getFilledReportStruct();
        statusChange = quoteKeyFillReportContainer.getStatusChange();
        quote = QuoteCacheFactory.find(sessionManager.getUserId()).getQuoteByQuoteKey(quoteFillInfo.quoteKey);
        if (statusChange == StatusUpdateReasons.POSSIBLE_RESEND )
        {
        	// For MMTNs, we do not want to do any poss-resend checks. GJ-9/25/09.
        	if (quoteFillInfo.quoteKey != 0)
        	{
	        	// Use quote key with user Id as key
	        	UserClassContainer qKey = new UserClassContainer(quoteFillInfo.userId,quoteFillInfo.quoteKey);
	            Integer oldTransactionNumber = (Integer)getPossibleResendFilledReports().get(qKey);
	            if ( oldTransactionNumber != null && oldTransactionNumber.intValue() >= quoteFillInfo.transactionSequenceNumber )
	            {
                    if (Log.isDebugOn())
                    {
                        Log.debug(this, "ignore possible-resend quote fill for " + getSessionManager().getUserId() +
	        		            " oldTxNbr:" + oldTransactionNumber + " on quote" + getQuoteInfoString(quoteFillInfo));
                    }
                    return;
	            }
	            else
	            {
	                getPossibleResendFilledReports().put(qKey, Integer.valueOf(quoteFillInfo.transactionSequenceNumber));
	            }
        	}
        }
        else
        {
            if(quote != null)
            	quote.statusChange = StatusUpdateReasons.FILL;
            else
            {
                if (Log.isDebugOn())
                {
                    Log.debug(this, "null returned on getQuoteByQuoteKey() in QuoteCache(" + getSessionManager().getUserId() + ")" +
                		      getQuoteInfoString(quoteFillInfo));
                }
            }
        }
        quoteAck = new QuoteAcknowledgeStructV3();
        //PQRefactor: used to be ProductQueryManagerImpl.getProduct (synchronized)
        ProductStruct product = getProductQueryServiceAdapter().getProductByKey(quoteFillInfo.productKey);
        String smgr = getSessionManager().toString();
        String fillInfo = getQuoteInfoString(quoteFillInfo);
        StringBuilder calling = new StringBuilder(smgr.length()+fillInfo.length()+70);
        calling.append("calling acceptQuoteFilledReport for ").append(smgr).append(fillInfo)
               .append(" queueSize=").append(getProxyWrapper().getQueueSize())
               .append(";statusChange").append(statusChange);
        Log.information(this, calling.toString());
        if(quote == null)
        {
            quoteStatusConsumer.acceptQuoteFilledReport(quoteFill, product, quoteFillInfo.quoteKey, statusChange, getProxyWrapper().getQueueSize());
            quoteAck.classKey = product.productKeys.classKey;
        }
        else
        {
            quoteStatusConsumer.acceptQuoteFilledReport(quoteFill, quote, getProxyWrapper().getQueueSize());
            quoteAck.classKey = quote.productKeys.classKey;
        }
        calling.setLength(0);
        calling.append("finished calling acceptQuoteFilledReport for ").append(smgr).append(fillInfo);
        Log.information(this, calling.toString());
        quoteAck = new QuoteAcknowledgeStructV3();
        quoteAck.acknowledgingUserId = sessionManager.getUserId();
        quoteAck.submittingUserId = quoteFillInfo.userId;
        quoteAck.quoteKey = quoteFillInfo.quoteKey; //filledReport.quoteKey;
        quoteAck.productKey = quoteFillInfo.productKey; //filledReport.filledReport.productKey;
        quoteAck.transactionSequenceNumber = quoteFillInfo.transactionSequenceNumber; //filledReport.filledReport.transactionSequenceNumber;
        // CAS_ACK
        clockPoints[2].clockPoint = TransactionClockPoints.USER_ACK;
        clockPoints[2].timestamp = new DateWrapper ().toDateTimeStruct();
        quoteAck.clockPoints = clockPoints;
        ((SessionManager)sessionManager).ackQuoteStatusV3(quoteAck);
    }

    private String getQuoteInfoString(QuoteInfoStruct quoteInfo)
    {
        StringBuilder toStr = new StringBuilder(60);
//Printed in this format -> :CBOE:690:pkey=123456:seq=1:user=KCD
        toStr.append(':');
        toStr.append(quoteInfo.firm.exchange).append(':');
        toStr.append(quoteInfo.firm.firmNumber).append(':');
        toStr.append(":pkey=").append(quoteInfo.productKey);
        toStr.append(":seq=").append(quoteInfo.transactionSequenceNumber).append(":user=").append(quoteInfo.userId);

        return toStr.toString();
    }

    protected ProductQueryServiceAdapter getProductQueryServiceAdapter()
    {
        if(pqAdapter == null)
        {
            pqAdapter = ServicesHelper.getProductQueryServiceAdapter();
        }
        return pqAdapter;
    }

    private TransactionClockPointStruct[] getTransactionClockPointStructs()
    {
        int CAS_CLOCK_POINT_SIZE = 3;
        TransactionClockPointStruct[] clockPoints = new TransactionClockPointStruct[CAS_CLOCK_POINT_SIZE];
        for (int i = 0; i < clockPoints.length; i++)
        {
            clockPoints[i] = new TransactionClockPointStruct();
        }
	    return clockPoints;
    }
}
