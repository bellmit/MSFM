package com.cboe.application.supplier.proxy;

import com.cboe.application.supplier.QuoteStatusSupplierFactory;
import com.cboe.domain.instrumentedChannel.supplier.proxy.InstrumentedGMDSupplierProxy;
import com.cboe.domain.util.DateWrapper;
import com.cboe.domain.util.QuoteInfoBustReportContainer;
import com.cboe.domain.util.QuoteInfoFilledReportContainer;
import com.cboe.domain.util.UserClassContainer;
import com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer;
import com.cboe.idl.cmiConstants.StatusUpdateReasons;
import com.cboe.idl.cmiQuote.QuoteCancelReportStruct;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.idl.constants.TransactionClockPoints;
import com.cboe.idl.quote.QuoteAcknowledgeStructV3;
import com.cboe.idl.quote.QuoteInfoStruct;
import com.cboe.idl.util.TransactionClockPointStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.QuoteDeleteReportWrapper;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.domain.GMDProxyHome;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;

import java.util.Hashtable;

/**
 * QuoteStatusConsumerProxy serves as a proxy to the QuoteStatusConsumer
 * object on the presentation side in com.cboe.presentation.consumer.  The
 * QuoteStatusSupplier on the CAS uses this proxy object to communicate to
 * the GUI callback object.
 *
 * @see com.cboe.consumers.internalPresentation.QuoteStatusConsumerImpl
 * @see com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/25/1999
 */

public class QuoteStatusConsumerProxy extends InstrumentedGMDSupplierProxy
{
    private static int DEFAULT_SIZE = 10;
    private Hashtable possibleResendFilledReports = null;
    private Hashtable possibleResendBustedReports = null;

    /**
     * QuoteStatusConsumerProxy constructor.
     *
     * @param quoteStatusConsumer a reference to the proxied implementation object.
     * @param sessionManager a reference to the SessionManager managing subscriptions for this proxy.
     */
    public QuoteStatusConsumerProxy(CMIQuoteStatusConsumer quoteStatusConsumer,
                                    BaseSessionManager sessionManager,
                                    boolean gmdProxy,
                                    GMDProxyHome home)
    {
        super(sessionManager, QuoteStatusSupplierFactory.find(sessionManager), gmdProxy, home, quoteStatusConsumer);
        interceptor = new QuoteStatusConsumerInterceptor(quoteStatusConsumer);
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
            Log.debug(this,"calling channelUpdate for " + getSessionManager());
        }
        BaseSessionManager      baseSessionManager;
        QuoteAcknowledgeStructV3 quoteAck = null;

        if (event != null)
        {
            boolean sendingAckToServer = false;
            try
            {
                ChannelKey key = (ChannelKey) event.getChannel();
                baseSessionManager = getSessionManager();
                String smgr = baseSessionManager.toString();
                StringBuilder calling;
                String quoteinfo;

                switch(key.channelType)
                {
                    case ChannelType.CB_QUOTE_BY_CLASS:
                    case ChannelType.CB_ALL_QUOTES:
                         // Call the proxied method passing the extracted QuoteDetailStruct[] from the EventChannelEvent.
                        ((QuoteStatusConsumerInterceptor)interceptor).acceptQuoteStatus((QuoteDetailStruct[])event.getEventData());
                    break;

                    case ChannelType.CB_QUOTE_CANCEL_REPORT:
                    case ChannelType.CB_QUOTE_CANCEL_REPORT_BY_CLASS:
                    case ChannelType.CB_QUOTE_CANCEL_REPORT_BY_FIRM:
                        calling = new StringBuilder(smgr.length()+40);
                        calling.append("calling acceptQuoteCancelReport for ").append(smgr);
                        Log.information(this, calling.toString());
                        // Call the proxied method passing the extracted
                        // QuoteFilledReportStruct objects from the EventChannelEvent.
                        QuoteDeleteReportWrapper[] deletedQuotes = (QuoteDeleteReportWrapper[])event.getEventData();

                        for (int i = 0; i < deletedQuotes.length; ++i)
                        {
                            QuoteCancelReportStruct quoteCancelReport = deletedQuotes[i].getQuoteCancelReportStruct();
                            ((QuoteStatusConsumerInterceptor)interceptor).acceptQuoteCancelReport(quoteCancelReport);
                        }
                    break;

                    case ChannelType.CB_QUOTE_FILLED_REPORT:
                    case ChannelType.CB_QUOTE_FILLED_REPORT_BY_CLASS:
                    case ChannelType.CB_QUOTE_FILLED_REPORT_BY_FIRM:
                    case ChannelType.CB_QUOTE_FILLED_REPORT_BY_TRADING_FIRM:
                        // Call the proxied method passing the extracted QuoteFilledReportStruct from the EventChannelEvent.

                        QuoteInfoFilledReportContainer quoteInfoFilledReportContainer = (QuoteInfoFilledReportContainer)event.getEventData();
                        QuoteInfoStruct quoteFillInfo = quoteInfoFilledReportContainer.getQuoteInfoStruct();

                        TransactionClockPointStruct[] clockPoints = null;
                        if (getGMDStatus())
                        {
                            clockPoints = getTransactionClockPointStructs();
                            clockPoints[0].clockPoint = TransactionClockPoints.CAS_RECEIVE;
                            clockPoints[0].timestamp = quoteInfoFilledReportContainer.getDateTimeAtCreation();
                            clockPoints[1].clockPoint = TransactionClockPoints.CAS_SEND;
                            clockPoints[1].timestamp = new DateWrapper ().toDateTimeStruct();
                        }
                        if (quoteInfoFilledReportContainer.getQuoteFilledReportStruct().statusChange == StatusUpdateReasons.POSSIBLE_RESEND )
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
                        quoteinfo = getQuoteInfoString(quoteFillInfo);
                        calling = new StringBuilder(smgr.length()+quoteinfo.length()+55);
                        calling.append("calling acceptQuoteFilledReport for ").append(smgr).append(quoteinfo)
                               .append(";statuschange=").append(quoteInfoFilledReportContainer.getQuoteFilledReportStruct().statusChange);
                        Log.information(this, calling.toString());
                        ((QuoteStatusConsumerInterceptor)interceptor).acceptQuoteFilledReport(quoteInfoFilledReportContainer.getQuoteFilledReportStruct());
                        calling.setLength(0);
                        calling.append("finished calling acceptQuoteFilledReport for ").append(smgr).append(quoteinfo);
                        Log.information(this, calling.toString());

                        if (getGMDStatus())
                        {
                            sendingAckToServer = true;
                            quoteAck = new QuoteAcknowledgeStructV3();
                            quoteAck.acknowledgingUserId = baseSessionManager.getUserId();
                            quoteAck.submittingUserId = quoteFillInfo.userId;
                            quoteAck.quoteKey = quoteFillInfo.quoteKey; //filledReport.quoteKey;
                            quoteAck.productKey = quoteFillInfo.productKey; //filledReport.filledReport.productKey;
                            quoteAck.classKey = quoteInfoFilledReportContainer.getQuoteFilledReportStruct().productKeys.classKey;//filledReport.productKeys.classKey;
                            quoteAck.transactionSequenceNumber = quoteFillInfo.transactionSequenceNumber; //filledReport.filledReport.transactionSequenceNumber;
                            clockPoints[2].clockPoint = TransactionClockPoints.USER_ACK;
                            clockPoints[2].timestamp = new DateWrapper().toDateTimeStruct();
                            quoteAck.clockPoints = clockPoints;
                            ((SessionManager)baseSessionManager).ackQuoteStatusV3(quoteAck);
                        }
                    break;

                    case ChannelType.CB_QUOTE_BUST_REPORT:
                    case ChannelType.CB_QUOTE_BUST_REPORT_BY_FIRM:
                    case ChannelType.CB_QUOTE_BUST_REPORT_BY_CLASS:
                    case ChannelType.CB_QUOTE_BUST_REPORT_BY_TRADING_FIRM:
                        QuoteInfoBustReportContainer quoteInfoBustReportContainer = (QuoteInfoBustReportContainer)event.getEventData();;
                        QuoteInfoStruct quoteBustInfo = quoteInfoBustReportContainer.getQuoteInfoStruct();

                        TransactionClockPointStruct[] bustTimes = null;
                        if (getGMDStatus())
                        {
                            bustTimes = getTransactionClockPointStructs();
                            quoteAck = new QuoteAcknowledgeStructV3();
                            bustTimes[0].clockPoint = TransactionClockPoints.CAS_RECEIVE;
                            bustTimes[0].timestamp = quoteInfoBustReportContainer.getDateTimeAtCreation();
                            bustTimes[1].clockPoint = TransactionClockPoints.CAS_SEND;
                            bustTimes[1].timestamp = new DateWrapper().toDateTimeStruct();
                            quoteAck.clockPoints = bustTimes;
                        }
                        if (quoteInfoBustReportContainer.getQuoteBustReportStruct().statusChange == StatusUpdateReasons.POSSIBLE_RESEND )
                        {
                            // Use quote key with user Id as key
                        	UserClassContainer qKey = new UserClassContainer(quoteBustInfo.userId,quoteBustInfo.quoteKey);
                            Integer oldTransactionNumber = (Integer)getPossibleResendBustedReports().get(qKey);
                            if ( oldTransactionNumber != null && oldTransactionNumber.intValue() >= quoteBustInfo.transactionSequenceNumber )
                            {
                                if (Log.isDebugOn())
                                {
                                    Log.debug(this, "ignore possible-resend quote bust for " + this.getSessionManager().getUserId() +
                        		        " oldTxNbr:" + oldTransactionNumber + " on quote" + getQuoteInfoString(quoteBustInfo));
                                }
                            	return;
                            }
                            else
                            {
                                getPossibleResendBustedReports().put(qKey, Integer.valueOf(quoteBustInfo.transactionSequenceNumber));
                            }
                        }
                        quoteinfo = getQuoteInfoString(quoteBustInfo);
                        calling = new StringBuilder(smgr.length()+quoteinfo.length()+55);
                        calling.append("calling acceptQuoteBustReport for ").append(smgr).append(quoteinfo)
                               .append(";statuschange=").append(quoteInfoBustReportContainer.getQuoteBustReportStruct().statusChange);
                        Log.information(this, calling.toString());
                        ((QuoteStatusConsumerInterceptor)interceptor).acceptQuoteBustReport(quoteInfoBustReportContainer.getQuoteBustReportStruct());
                        calling.setLength(0);
                        calling.append("finished calling acceptQuoteBustReport for ").append(smgr).append(quoteinfo);
                        Log.information(this, calling.toString());

                        if (getGMDStatus())
                        {
                            sendingAckToServer = true;
                            quoteAck.acknowledgingUserId = baseSessionManager.getUserId();
                            quoteAck.submittingUserId = quoteBustInfo.userId;
                            quoteAck.quoteKey = quoteBustInfo.quoteKey;
                            quoteAck.productKey = quoteBustInfo.productKey;
                            quoteAck.classKey = quoteInfoBustReportContainer.getQuoteBustReportStruct().productKeys.classKey;
                            quoteAck.transactionSequenceNumber = quoteBustInfo.transactionSequenceNumber;
                            quoteAck.clockPoints[2].clockPoint = TransactionClockPoints.USER_ACK;
                            quoteAck.clockPoints[2].timestamp = new DateWrapper().toDateTimeStruct();
                            ((SessionManager)baseSessionManager).ackQuoteStatusV3(quoteAck);
                        }
                    break;

                    default:
                    break;
                }
            }
            catch(Exception e)
            {
                Log.exception(this, "session:" + getSessionManager(), e);
                if (!sendingAckToServer)
                {
                    // Could not send to user, drop the connection.
                    lostConnection(event);
                }
                // else failure to ACK server means the server will try again later.
            }
        }
        else
        {
            Log.information(this, "Null event");
        }
    }

    public String getMethodName(ChannelEvent event)
    {
        String method = "";

        ChannelKey key = (ChannelKey) event.getChannel();

        switch(key.channelType)
        {
            case ChannelType.CB_QUOTE_BY_CLASS:
            case ChannelType.CB_ALL_QUOTES:
                 // Call the proxied method passing the extracted QuoteDetailStruct[] from the EventChannelEvent.
                method = "acceptQuoteStatus";
                break;

            case ChannelType.CB_QUOTE_FILLED_REPORT:
            case ChannelType.CB_QUOTE_FILLED_REPORT_BY_CLASS:
            case ChannelType.CB_QUOTE_FILLED_REPORT_BY_FIRM:
            case ChannelType.CB_QUOTE_FILLED_REPORT_BY_TRADING_FIRM:
                // Call the proxied method passing the extracted QuoteFilledReportStruct from the EventChannelEvent.
                method = "acceptQuoteFilledReport";
                break;

            case ChannelType.CB_QUOTE_BUST_REPORT:
            case ChannelType.CB_QUOTE_BUST_REPORT_BY_FIRM:
            case ChannelType.CB_QUOTE_BUST_REPORT_BY_CLASS:
            case ChannelType.CB_QUOTE_BUST_REPORT_BY_TRADING_FIRM:
                method = "acceptQuoteBustReport";
                break;

            default:
                break;
        }
        return method;
    }

    private String getQuoteInfoString(QuoteInfoStruct quoteInfo)
    {
        StringBuilder toStr = new StringBuilder(60);
        //Printed in this format -> :CBOE:690:pkey=123456:seq=1:user=KCD
        toStr.append(':');
        toStr.append(quoteInfo.firm.exchange).append(':');
        toStr.append(quoteInfo.firm.firmNumber).append(':');
        toStr.append(quoteInfo.userId);
        toStr.append(":pkey=").append(quoteInfo.productKey);
        toStr.append(":qkey=").append(quoteInfo.quoteKey);
        toStr.append(":seq=").append(quoteInfo.transactionSequenceNumber);

        return toStr.toString();
    }

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.QUOTE;
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
