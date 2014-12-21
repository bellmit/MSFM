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
import com.cboe.interfaces.application.inprocess.QuoteStatusV2Consumer;
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

public class QuoteStatusV2ConsumerProxy extends QuoteStatusCollectorProxy
{
    private QuoteStatusV2Consumer quoteStatusConsumer;
    private static int DEFAULT_SIZE = 10;
    private Hashtable possibleResendFilledReports = null;
    private Hashtable possibleResendBustedReports = null;
    private ListenerProxyQueueControl proxyWrapper;

    private ProductQueryServiceAdapter pqAdapter;

    public QuoteStatusV2ConsumerProxy(QuoteStatusV2Consumer quoteStatusConsumer,
                                       BaseSessionManager sessionManager)
    {
        super(null, sessionManager, quoteStatusConsumer);
        this.quoteStatusConsumer = quoteStatusConsumer;
        // Log.debug(" MMTN -for FIX- CB_MMHH_QUOTE_FILL_REPORT_V2 constructor");
    }

    private Hashtable getPossibleResendFilledReports()
    {
        if (possibleResendFilledReports == null)
        {
            possibleResendFilledReports = new Hashtable(DEFAULT_SIZE);
        }
        return possibleResendFilledReports;
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
                    case ChannelType.CB_MMHH_QUOTE_FILL_REPORT_V2:
                        // Log.debug(" MMTN -for FIX- CB_MMHH_QUOTE_FILL_REPORT_V2: channelUpdate");
                        // This is the only "inprocess" channel expected at this time
                        processQuoteFill(channelData);
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
            else {
                String user = getSessionManager().getUserId();
                String fillInfo = getQuoteInfoString(quoteFillInfo);
                StringBuilder sb = new StringBuilder(user.length()+fillInfo.length()+60);
                sb.append("null returned on getQuoteByQuoteKey() in QuoteCache(").append(user).append(")").append( fillInfo);
                String nullReturned = sb.toString();
                if (Log.isDebugOn())
                {
                    Log.debug(this, nullReturned);
                }
                Log.alarm(this, nullReturned);
                return;
            }
        }

        quoteAck = new QuoteAcknowledgeStructV3();
        //PQRefactor: used to be ProductQueryManagerImpl.getProduct (synchronized)
        ProductStruct product = getProductQueryServiceAdapter().getProductByKey(quoteFillInfo.productKey);
        String smgr = getSessionManager().toString();
        String fillInfo = getQuoteInfoString(quoteFillInfo);
        StringBuilder calling = new StringBuilder(smgr.length()+fillInfo.length()+80);
        calling.append("calling acceptQuoteFilledReport for ").append(smgr).append(fillInfo)
               .append(" queueSize=").append(getProxyWrapper().getQueueSize())
               .append(";statusChange").append(statusChange);
        Log.information(this, calling.toString());
        quoteStatusConsumer.acceptQuoteFilledReport(quoteFill, quote, getProxyWrapper().getQueueSize());
        quoteAck.classKey = quote.productKeys.classKey;
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
