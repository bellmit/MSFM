// $Workfile$ com.cboe.application.quote.QuoteCache.java
// $Revision$
/* $Log$
*   Increment 6                             11/27/2000      desaik
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.

package com.cboe.application.quote;

import java.util.*;

import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.quote.QuoteInfoStruct;
import com.cboe.idl.cmiConstants.ReportTypes;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;

import com.cboe.domain.util.*;

import com.cboe.application.shared.*;
import com.cboe.client.util.CollectionHelper;
import com.cboe.interfaces.application.*;

import com.cboe.exceptions.*;

import com.cboe.util.channel.*;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

import com.cboe.application.supplier.QuoteStatusCollectorSupplier;
import com.cboe.application.supplier.QuoteStatusCollectorSupplierFactory;
import com.cboe.application.util.QuoteCallSnapshot;
import com.cboe.application.cache.UserCacheListener;
import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;

public class QuoteCache extends UserCacheListener
{
    private Map<SessionKeyContainer, QuoteDetailStruct> quoteCollectionMap;
    private Map<Integer, SessionKeyContainer> quoteKeyMap;
    private Map<SessionKeyContainer, Map<SessionKeyContainer, SessionKeyContainer>> classKeyProductMap;
    private Map<SessionKeyContainer, QuoteDetailStruct> deletedReportMap;
    private Map<UserClassContainer, QuoteInfoStruct> filledReportMap;
    private Map<UserClassContainer, QuoteInfoStruct> bustedReportMap;
    private Map<Integer, Integer> quoteUpdateStatusMap;
    


    private int     quoteCollectionDefaultSize;
    private String  userId                                      = null;
    private ExchangeFirmStruct  firmKey                         = null;
    private ExchangeFirmStructContainer firmKeyContainer        = null;
    private QuoteStatusCollectorSupplier quoteStatusSupplier    = null;
    private Object quoteKeyLock;
    private int quoteKeyBase;
    private boolean isQuoteDeleteReportDispatch;

    private ProductQueryServiceAdapter pqAdapter;

    private static final QuoteDetailStruct[] EMPTY_QuoteDetailStruct_ARRAY = new QuoteDetailStruct[0];

    /** default map size*/
    private static int DEFAULT_SIZE = 500;
    /** default report cancel */
    private boolean DEFAULT_REPORT = true;
    private List<String> groupMembers;
    private boolean isTradingFirm;
    private ConcurrentEventChannelAdapter internalEventChannel;

   /**
     * Initialize the quote collections.
     */
    public QuoteCache( String userId)
    {
        super(userId);
        this.userId = userId;
        init( DEFAULT_SIZE );        
        subscribeForEventsForUser( userId );
        quoteKeyLock = new Object();
        quoteKeyBase = generateQuoteKeyBase();
    }

    private Map<SessionKeyContainer, QuoteDetailStruct> getQuoteMap()
    {
        return quoteCollectionMap;
    }

    /**
     * gets the quote collection key map
     * @return HashMap containing quote key maps for the current user session
     */
     private Map<Integer, SessionKeyContainer> getQuoteKeyMap()
     {
         return quoteKeyMap;
     }

    
    /**
     * gets the class key to products collection map
     * @return HashMap containing the map between class keys and product keys
     */
     private Map<SessionKeyContainer, Map<SessionKeyContainer, SessionKeyContainer>> getClassKeyProductMap()
     {
         return classKeyProductMap;
     }
    
    /**
    * gets the quote fill report product key map
    * @return HashMap containing last delete report for the product
    */
    private Map<SessionKeyContainer, QuoteDetailStruct> getDeletedReportMap()
    {
        return deletedReportMap;
    }

    /**
    * gets the quote fill report product key map
    * @return HashMap containing last fill report for the product
    */
    private Map<UserClassContainer, QuoteInfoStruct> getFilledReportMap()
    {
        return filledReportMap;
    }
    
   
    /**
    * gets the quote bust report product key map
    * @return HashMap containing last bust report for the product
    */
    private Map<UserClassContainer, QuoteInfoStruct> getBustedReportMap()
    {
        return bustedReportMap;
    }
    
   

    /**
    * gets the quote bust report product key map
    * @return HashMap containing last bust report for the product
    */
    private Map<Integer, Integer> getUpdateStatusMap()
    {
        return quoteUpdateStatusMap;
    }

    /**
    * removes the quote from collection key map
    * @param theQuote detail information to be removed
    */
    private void removeFromKeyMap(QuoteDetailStruct theQuote)
    {
        if (theQuote != null )
        {
        	getQuoteKeyMap().remove(Integer.valueOf(theQuote.quote.quoteKey));
        }
    }

    /**
    * removes the quote from collection map
    * @param productKey the product key for the quote to be removed
    * @return QuoteDetailStruct the removed quote information
    */
    private QuoteDetailStruct removeFromQuoteMap(String sessionName, int productKey)
    {
        QuoteDetailStruct ret = null;

        ret = getQuoteMap().remove(new SessionKeyContainer(sessionName, productKey));

        return ret;
    }
    /**
    * removes the quote from class key to products map
    * @param quoteDetail the quote to be removed
    */
    private void removeFromClassKeyProductMap(QuoteDetailStruct quoteDetail)
    {
        Map<SessionKeyContainer, SessionKeyContainer> products = getClassKeyProductMap().get(new SessionKeyContainer(quoteDetail.quote.sessionName, quoteDetail.productKeys.classKey));

        if (products != null )
        {
            products.remove(new SessionKeyContainer(quoteDetail.quote.sessionName, quoteDetail.productKeys.productKey));
        }
    }

    /**
    * adds a quote to the class to products map
    * @param theQuote the quote information
    */
    private void addClassProductMap(QuoteDetailStruct theQuote)
    {
        SessionKeyContainer productKey = new SessionKeyContainer(theQuote.quote.sessionName, theQuote.productKeys.productKey);
        SessionKeyContainer classKey = new SessionKeyContainer(theQuote.quote.sessionName, theQuote.productKeys.classKey);

        synchronized (this)
        {
	        Map<SessionKeyContainer, SessionKeyContainer> products = getClassKeyProductMap().get(classKey);
	
	        if (products == null )
	        {
	            products = new HashMap<SessionKeyContainer, SessionKeyContainer>();
	            getClassKeyProductMap().put(classKey, products);
	        }
	
	        products.put(productKey, productKey);
	    }
    }

    private void init(int defaultSize)
    {
        quoteCollectionDefaultSize  = defaultSize;
        quoteCollectionMap          = new HashMap<SessionKeyContainer, QuoteDetailStruct>(quoteCollectionDefaultSize);
        quoteKeyMap                 = new HashMap<Integer, SessionKeyContainer>(quoteCollectionDefaultSize);
        classKeyProductMap          = new HashMap<SessionKeyContainer, Map<SessionKeyContainer,SessionKeyContainer>>(quoteCollectionDefaultSize);
        deletedReportMap            = new HashMap<SessionKeyContainer, QuoteDetailStruct>(quoteCollectionDefaultSize);
        filledReportMap             = new HashMap<UserClassContainer, QuoteInfoStruct>(quoteCollectionDefaultSize);
        bustedReportMap             = new HashMap<UserClassContainer, QuoteInfoStruct>(quoteCollectionDefaultSize);
        quoteUpdateStatusMap        = new HashMap<Integer, Integer>(quoteCollectionDefaultSize);
        try
        {
        	internalEventChannel = ClientIECFactory.findConcurrentEventChannelAdapter(ClientIECFactory.CAS_INSTRUMENTED_IEC);
        }
        catch (Exception e)
        {
        	Log.exception("Exception getting CAS_INSTRUMENTED_IEC!", e);
        }        
        quoteStatusSupplier         = QuoteStatusCollectorSupplierFactory.create();
    }

    protected int generateQuoteKeyBase()
    {
        Calendar calendar = TimeServiceWrapper.getCalendar();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        int currentSec = calendar.get(Calendar.SECOND);
        int currentMil = calendar.get(Calendar.MILLISECOND);
        int currentTimeInMil = (currentHour*60*60*60 + currentMinute*60*60 + currentSec*60)*1000 + currentMil;
        return currentTimeInMil;
    }

    public int generateQuoteKey()
    {
        synchronized(quoteKeyLock)
        {
            return ++quoteKeyBase;
        }
    }

    public QuoteStatusCollectorSupplier getQuoteStatusCollectorSupplier()
    {
        return quoteStatusSupplier;
    }

    private void subscribeForEventsForUser( String userId )
    {
        ChannelKey channelKey;

        channelKey = new ChannelKey(ChannelType.QUOTE_FILL_REPORT, userId);
        internalEventChannel.addChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.QUOTE_DELETE_REPORT, userId);
        internalEventChannel.addChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.QUOTES_DELETE_REPORTV2, userId);
        internalEventChannel.addChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.QUOTE_BUST_REPORT, userId);
        internalEventChannel.addChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.QUOTE_STATUS_UPDATE, userId);
        internalEventChannel.addChannelListener(this, this, channelKey);
    }

    private void subscribeForEventsForTradingFirm( String userId )
    {
        ChannelKey channelKey;

        channelKey = new ChannelKey(ChannelType.QUOTE_FILL_REPORT_BY_TRADING_FIRM, userId);
        internalEventChannel.addChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.QUOTE_BUST_REPORT_BY_TRADING_FIRM, userId);
        internalEventChannel.addChannelListener(this, this, channelKey);
    }


    private void subscribeForEventsForFirm( ExchangeFirmStructContainer firmKeyStructContainer )
    {
        ChannelKey channelKey;

        channelKey = new ChannelKey(ChannelType.QUOTE_FILL_REPORT_BY_FIRM, firmKeyStructContainer);
        internalEventChannel.addChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.QUOTE_BUST_REPORT_BY_FIRM, firmKeyStructContainer);
        internalEventChannel.addChannelListener(this, this, channelKey);
    }
    ///////////////////// public methods ////////////////////////////

    public void setFirmKey(ExchangeFirmStruct firmKey)
    {
        if (Log.isDebugOn()) {Log.debug("QuoteCache -> calling setFirmKey : userId=" + userId );}
        this.firmKey = firmKey;
        this.firmKeyContainer = new ExchangeFirmStructContainer(firmKey);
        subscribeForEventsForFirm(firmKeyContainer);
    }

    /*
     * Subscribe all order status event for each group members.
     * Only be called for a firm display user.
     */
    public void setFirmGroupMembers(List<String> grpMembers) {
        groupMembers = grpMembers;
        isTradingFirm  = true;
        for(String user : grpMembers) {
            subscribeForEventsForTradingFirm(user);
        }
        subscribeForEventsForTradingFirm(userId);
    }

    public ExchangeFirmStruct getFirmKey()
    {
        return this.firmKey;
    }

    public String getUserID()
    {
        return this.userId;
    }

    /** Cleaning up of cache data based on userId
     *
     *  @author Keval D. Desai
     *  @version 12/4/00
     */
    public synchronized void cacheCleanUp()
    {
        //Please Do Not Nullify Any Internal References -- Hybrid Failover!

        if (Log.isDebugOn()) {Log.debug("QuoteCache -> calling cacheCleanUp: userId=" + userId);}
        internalEventChannel.removeChannelListener(this);
        getQuoteMap().clear();
        getDeletedReportMap().clear();
        getQuoteKeyMap().clear();
        getClassKeyProductMap().clear();
        getFilledReportMap().clear();
        getBustedReportMap().clear();
        getUpdateStatusMap().clear();
    }

    public void channelUpdate(ChannelEvent event)
    {
        ChannelKey  channelKey = (ChannelKey)event.getChannel();
        Object      channelData = (Object)event.getEventData();

        if (Log.isDebugOn())
        {
        	Log.debug("QuoteCache(" + userId + ") -> received event " + channelKey + ":" + channelData);
        }

        switch (channelKey.channelType)
        {
            case ChannelType.QUOTE_FILL_REPORT:
            case ChannelType.QUOTE_FILL_REPORT_BY_FIRM:
            case ChannelType.QUOTE_FILL_REPORT_BY_TRADING_FIRM:
                QuoteKeyFillReportContainer quoteKeyFillReportContainer = (QuoteKeyFillReportContainer)channelData;
                acceptQuoteFillReport(  channelKey.channelType,
                                        quoteKeyFillReportContainer.getQuoteInfoStruct(),
                                        quoteKeyFillReportContainer.getFilledReportStruct(),
                                        quoteKeyFillReportContainer.getStatusChange() );
            break;

            case ChannelType.QUOTE_DELETE_REPORT:
                QuoteKeyCancelReportContainer quoteKeyCancelReportContainer = (QuoteKeyCancelReportContainer)channelData;
                acceptQuoteDeleteReport( quoteKeyCancelReportContainer.getQuoteKeys(), quoteKeyCancelReportContainer.getCancelReason() );
                break;

           case ChannelType.QUOTES_DELETE_REPORTV2:
                QuoteCancelReportContainer quoteCancelReportContainer = (QuoteCancelReportContainer)channelData;
                acceptQuoteDeleteReportV2( quoteCancelReportContainer.getQuotes(), quoteCancelReportContainer.getCancelReason() );
                break;

            case ChannelType.QUOTE_BUST_REPORT:
            case ChannelType.QUOTE_BUST_REPORT_BY_FIRM:
            case ChannelType.QUOTE_BUST_REPORT_BY_TRADING_FIRM:
                QuoteKeyBustReportContainer quoteKeyBustReportContainer = (QuoteKeyBustReportContainer)channelData;
                acceptQuoteBustReport ( channelKey.channelType,
                                        quoteKeyBustReportContainer.getQuoteInfoStruct(),
                                        quoteKeyBustReportContainer.getBustReportStruct(),
                                        quoteKeyBustReportContainer.getStatusChange() );
            break;
            case ChannelType.QUOTE_STATUS_UPDATE:
                RoutingGroupQuoteContainer quoteStatusContainer = (RoutingGroupQuoteContainer)channelData;
                acceptQuoteUpdate(channelKey.channelType,
                        quoteStatusContainer.getQuoteStruct(),
                        quoteStatusContainer.getStatusChange());
            break;
            default :
                if (Log.isDebugOn()) {Log.debug("User Based Quote Cache -> Wrong Channel : " + channelKey.channelType + ": userId=" + userId);}
            break;
        }
    }

    private void acceptQuoteFillReport(int channelType, QuoteInfoStruct quoteInfo, FilledReportStruct[] filled, short statusChange)
    {
    	if (Log.isDebugOn())
    	{
    		Log.debug("QuoteCache(" + userId + ") -> calling acceptQuoteFillReport:" +
    				     getQuoteInfoString(quoteInfo) + ":channelType=" + channelType +
    				     ":statusChange=" + statusChange + ":quoteKey=" + quoteInfo.quoteKey);
    	}

    	if(!isProductValid(quoteInfo.productKey))
        {
            Log.alarm("QuoteCache -> " + userId + " received Fill Report for quote:"+quoteInfo.quoteKey+" with invalid productKey:"+quoteInfo.productKey);
            return;
        }
        // Use quote key and user Id as key
    	UserClassContainer qKey = new UserClassContainer(quoteInfo.userId, quoteInfo.quoteKey);
        boolean update = false;

        // quoteKey will be 0 for MMTNs. They need to be skipped from the transaction number check.
        if (statusChange == StatusUpdateReasons.POSSIBLE_RESEND || quoteInfo.quoteKey == 0)
        {
            update = true;
        }
        else
        {
                QuoteInfoStruct cachedFill = this.getFillReport(qKey);

                // to avoid duplicate message, check on the cached last fill report.
                // if there is no record or the new fill quote key is greater than the cached fill quote key
                // and the filled quote transaction sequence number is greater than the cached one
                // publish the fill report.

                if ( cachedFill == null || cachedFill.transactionSequenceNumber < quoteInfo.transactionSequenceNumber )
                {
                    this.updateFill(qKey, quoteInfo);
                    update = true;
                }
        }
        if (update)
        {
            QuoteKeyFillReportContainer container;
            container = new QuoteKeyFillReportContainer(CollectionHelper.EMPTY_int_ARRAY, quoteInfo, statusChange, filled);
            dispatchQuoteStatus(container, channelType, quoteInfo.userId );

            // don't apply fill since firm display doesn't have the quote in quoteCollectionMap
            // MMTNs also do not have the corresponding quote in the quote cache.
            if(isTradingFirm || quoteInfo.quoteKey == 0)
                return;

            synchronized(this)
            {
	            for ( int i=0; i<filled.length; i++)
	            {
	                if ( filled[i].fillReportType == ReportTypes.REGULAR_REPORT || filled[i].fillReportType == ReportTypes.STRATEGY_REPORT )
	                {
	                    QuoteDetailStruct quoteDetail = this.applyFill(quoteInfo, filled[i]);
	
	                    if ( quoteDetail != null && quoteDetail.quote.bidQuantity == 0 && quoteDetail.quote.askQuantity == 0 )
	                    {
	                        removeQuote(quoteDetail.quote.sessionName, quoteDetail.quote.productKey, false);
	                    }
	                }
	            }
            }
        }
    }

    private QuoteDetailStruct getDeleteReport(SessionKeyContainer productContainer)
    {
        synchronized (getDeletedReportMap())
        {
             return getDeletedReportMap().get(productContainer);
        }
    }

    private void updateDeleteReport(SessionKeyContainer productContainer, QuoteDetailStruct quote)
    {
        synchronized (getDeletedReportMap())
        {
             getDeletedReportMap().put(productContainer, quote);
        }
    }

    private void acceptQuoteDeleteReportV2(QuoteStruct[] quotes, short cancelReason)
     {
        boolean reportCancel = true;
        int numQuotes = quotes.length;
        if ( numQuotes > 0 )
        {
            if (Log.isDebugOn()) {Log.debug("QuoteCache -> calling acceptQuoteDeleteReportV2: userId=" + userId + " reason=" + cancelReason + " size=" + quotes.length + " " + quotes[0].quoteKey);}
        }
        int deletedForReport = 0;
        QuoteDetailStruct[] quoteDetails = new QuoteDetailStruct[numQuotes];
        for ( int i = 0; i < numQuotes; i++ )
        {
            SessionKeyContainer productContainer = new SessionKeyContainer(quotes[i].sessionName, quotes[i].productKey);
            QuoteDetailStruct cachedDelete = getDeleteReport(productContainer);
            if ( cachedDelete == null || cachedDelete.quote.quoteKey < quotes[i].quoteKey )
            {
                QuoteDetailStruct theQuote = getQuoteByQuoteKey(quotes[i].quoteKey);
                if(theQuote != null && theQuote.quote.quoteKey == quotes[i].quoteKey )
                {
                    quoteDetails[deletedForReport] = theQuote;
                }
                else
                {
                    if (Log.isDebugOn()) {Log.debug("QuoteCache -> deleted quote not found in cache " );}
                    quoteDetails[deletedForReport] = buildQuoteDetailStruct(quotes[i]);
                }
                updateDeleteReport(productContainer, quoteDetails[deletedForReport]);
                deletedForReport ++;
             }
        }
        if ( deletedForReport > 0 )
        {
            if ( deletedForReport == numQuotes )
            {
                reportCancel = isReportCancel(isQuoteDeleteReportDispatch, cancelReason);
                deleteQuotes(quoteDetails, quoteDetails[0].quote.sessionName, cancelReason, reportCancel);
            }
            else
            {
                QuoteDetailStruct[] deletedQuotes = new QuoteDetailStruct[deletedForReport];
                for ( int i = 0; i < deletedForReport; i++ )
                {
                    deletedQuotes[i] = quoteDetails[i];
                }
                reportCancel = isReportCancel(isQuoteDeleteReportDispatch, cancelReason);
                deleteQuotes(deletedQuotes, deletedQuotes[0].quote.sessionName, cancelReason, reportCancel);
            }
        }
    }

    // If server is publisheding delete meesage via old method, CAS won't forward the delete message if that quote is not found in
    // the CAS cache.
    // NOTE: This method won't be needed if server is not pulbishing delete report via old method any more.
    private void acceptQuoteDeleteReport(int[] quoteKeys, short cancelReason)
    {
        boolean reportCancel = true;
        // log the event received message
        if (Log.isDebugOn()) {Log.debug("QuoteCache -> calling acceptQuoteDeleteReport: userId=" + userId + " reason=" + cancelReason + " size=" + quoteKeys.length);}

        int numQuotes = quoteKeys.length;
        QuoteDetailStruct[] quoteDetails = new QuoteDetailStruct[numQuotes];
        int deletedForReport = 0;
        for ( int i = 0; i < numQuotes; i++ )
        {
            QuoteDetailStruct theQuote = getQuoteByQuoteKey(quoteKeys[i]);
            if(theQuote != null)
            {
                SessionKeyContainer productContainer = new SessionKeyContainer(theQuote.quote.sessionName, theQuote.quote.productKey);
                QuoteDetailStruct cachedDelete = getDeleteReport(productContainer);
                if ( cachedDelete == null || cachedDelete.quote.quoteKey > theQuote.quote.quoteKey )
                {
                    quoteDetails[deletedForReport] = theQuote;
                    updateDeleteReport(productContainer, quoteDetails[deletedForReport]);
                    deletedForReport ++;
                }
            }
        }
        if ( deletedForReport > 0 )
        {
            if ( deletedForReport == numQuotes )
            {
                reportCancel = isReportCancel(isQuoteDeleteReportDispatch, cancelReason);
                deleteQuotes(quoteDetails, quoteDetails[0].quote.sessionName, cancelReason, reportCancel);
            }
            else
            {
                QuoteDetailStruct[] deletedQuotes = new QuoteDetailStruct[deletedForReport];
                for ( int i = 0; i < deletedForReport; i++ )
                {
                    deletedQuotes[i] = quoteDetails[i];
                }
                reportCancel = isReportCancel(isQuoteDeleteReportDispatch, cancelReason);
                deleteQuotes(deletedQuotes, deletedQuotes[0].quote.sessionName, cancelReason, reportCancel);
             }
        }
    }

    private void acceptQuoteBustReport(int channelType, QuoteInfoStruct quoteInfo, BustReportStruct[] bustedData, short statusChange)
    {
        if (Log.isDebugOn())
        {
        	Log.debug("QuoteCache(" + userId + ") -> calling acceptQuoteBustReport:" +
        			  getQuoteInfoString(quoteInfo) + ":channelType=" + channelType +
				      ":statusChange=" + statusChange);
        }

        if(!isProductValid(quoteInfo.productKey))
        {
            Log.alarm("QuoteCache -> " + userId + " received Bust Report for quote:"+quoteInfo.quoteKey+" with invalid productKey:"+quoteInfo.productKey);
            return;
        }
        // quote key and user Id as key
        UserClassContainer qKey = new UserClassContainer(quoteInfo.userId, quoteInfo.quoteKey);
        boolean update = false;

        if (statusChange == StatusUpdateReasons.POSSIBLE_RESEND)
        {
            update = true;
        }
        else
        {

            QuoteInfoStruct cachedBust = this.getBustReport(qKey);

            // to avoid duplicate message, check on the cached last bust report.
            // if there is no record or the bust quote transaction sequence number is greater than the cached one
            // publish the bust report. ** this is per the server sending the transactionSequence number based on
            // the CURRENT quote even for an old quote to b```e busted.
            if ( cachedBust == null || cachedBust.transactionSequenceNumber < quoteInfo.transactionSequenceNumber )
            {
                // always apply the report to the cache first
                this.updateBust(qKey, quoteInfo);
                update = true;
            }
        }
        if (update)
        {
            QuoteKeyBustReportContainer container;
            container = new QuoteKeyBustReportContainer(CollectionHelper.EMPTY_int_ARRAY, quoteInfo, statusChange, bustedData);
            dispatchQuoteStatus(container, channelType, quoteInfo.userId);

            // don't apply bust since firm display doesn't have the quote in quoteCollectionMap
            if(isTradingFirm)
                return;

            for ( int i=0; i<bustedData.length; i++)
            {
                if ( bustedData[i].bustReportType == ReportTypes.REGULAR_REPORT || bustedData[i].bustReportType == ReportTypes.STRATEGY_REPORT)
                {
                    this.applyBust(quoteInfo);
                }
            }
        }
    }

    private void acceptQuoteUpdate(int channelType, QuoteStruct quote, short statusChange)
    {
        if (Log.isDebugOn()) {Log.debug("QuoteCache -> calling acceptQuoteUpdate: userId=" + quote.userId + ": quoteKey=" + quote.quoteKey);}
        if(!isProductValid(quote.productKey))
        {
            Log.alarm("QuoteCache -> " + userId + " received acceptQuoteUpdate for quote:"+quote.quoteKey+" with invalid productKey:"+quote.productKey);
            return;
        }
        Integer qKey = quote.quoteKey;
        Integer transSeq = quote.transactionSequenceNumber;
        Integer cachedSeq = this.getUpdateStatusReport(qKey);
        if ( cachedSeq == null || cachedSeq.intValue() < quote.transactionSequenceNumber )
        {
             this.updateStatus(qKey, transSeq);
             QuoteDetailStruct quoteDetail = this.buildQuoteDetailStruct(quote);
             quoteDetail.statusChange = statusChange;
             QuoteDetailStruct[] quoteDetails = {quoteDetail};
             dispatchQuoteStatus(quoteDetails, channelType, quote.userId );
        }
    }
    /**
    * adds a quote to the collection
    *
    * If there is an old quote, it will be remove and the new quote will be
    * added.  The quote manager does not keep the quote history.
    *
    * @param theQuote the quote information
    */
    public synchronized void addQuote(QuoteDetailStruct theQuote)
    {
        QuoteDetailStruct[] array = new QuoteDetailStruct[1];
        array[0] = theQuote;

        addQuotes(array);
    }

    /**
     * Adds an array of quotes to the collection.
     *
     * If there are any old quotes, they will be removed and the new quote(s)
     * will be added.  The quote manager does not keep the quote history.
     *
     * 03/19/2007 - add quote to remove previos quote cache for Block Quote Cancel.
     *
     * @param theQuotes The array of quotes.
     */
    public void addQuotes(QuoteDetailStruct[] theQuotes)
    {
    	StringBuilder cancelQuoteStr = new StringBuilder();
    	int          cancelQuoteCounter = 0;

    	QuoteCallSnapshot.quoteCacheLockWaitStart();
    	synchronized (this)
    	{
        	QuoteCallSnapshot.quoteCacheLockWaitEnd();
	        if (Log.isDebugOn()) {Log.debug("QuoteCache -> calling addQuote: userId=" + userId + " (" + theQuotes.length + " quotes)");}

	        for (int i = 0; i < theQuotes.length; ++i)
	        {
                // Get the individual quote.
	            QuoteDetailStruct theQuote = theQuotes[i];

                // poke the quote to see what the bid and ask quantities are.
	            // If they are zero - remove previous quote from cache.
	            if (theQuote.quote.bidQuantity == 0 && theQuote.quote.askQuantity == 0)
	            {
	            	cancelQuoteCounter ++;
	            	cancelQuoteStr.append(" ").append(theQuote.quote.productKey);
	            	removeFromQuoteMap(theQuote.quote.sessionName, theQuote.quote.productKey);
	                removeFromClassKeyProductMap(theQuote);
	                removeFromKeyMap(theQuote);

	                //theQuote.quote.statusChange = StatusUpdateReasons.CANCEL;

	            }
	            // else continue the old way
	            else
	            {
	            	//SessionKeyContainer is a holder object - contains SessionName - eg. W_MAIN, W_STOCK & the Quotes product key
		            SessionKeyContainer sessionKeyContainer = new SessionKeyContainer(theQuote.quote.sessionName, theQuote.quote.productKey);

		            getQuoteKeyMap().put(theQuote.quote.quoteKey, sessionKeyContainer);
	            	QuoteDetailStruct prevQuote = getQuoteMap().put(sessionKeyContainer, theQuote);

	            	// check for a previous quote and thereby a previous quote key
	            	// remove previous quote key - to reduce memory overhead/footprint
	            	if ( prevQuote != null )
	            	{
	            		getQuoteKeyMap().remove( Integer.valueOf( prevQuote.quote.quoteKey ) );
	            	}

	            	addClassProductMap(theQuote);
	            }
	        }
	        if (cancelQuoteCounter !=0)
	        {
                StringBuilder counts = new StringBuilder(userId.length()+cancelQuoteStr.length()+80);
                counts.append("QuoteCache -> addQuotes userId:").append(userId)
                      .append(" totalItems:").append(theQuotes.length)
                      .append(" numCancels:").append(cancelQuoteCounter)
                      .append(" cancelProductKeys:").append(cancelQuoteStr);
                Log.information(counts.toString());
	        }
	        // calling the new clone method that skips deep cloning the prices, poductkey, productname.
	        QuoteDetailStruct[] container = com.cboe.application.quote.common.QuoteStructBuilder.cloneQuoteDetailStructs(theQuotes);
	        dispatchQuoteStatus(container, ChannelType.QUOTE_STATUS, userId);
    	}
        QuoteCallSnapshot.quoteCacheLockHoldEnd();
    }

    /**
    * gets the quote from the collection given the product information
    * @param productKey the product key information
    * @return QuoteDetailStruct containing the quote information
    */
    public synchronized QuoteDetailStruct getQuote(String sessionName, int productKey)
    {
        if (Log.isDebugOn()) {Log.debug("QuoteCache -> calling getQuote: userId=" + userId);}
        return getQuoteMap().get(new SessionKeyContainer(sessionName, productKey));
    }

    /**
    * checks if there is a quote for the given product key
    * @param productKey the product key information
    * @return boolean if quote for the product exists
    */
    public synchronized boolean containsQuote( String sessionName, int productKey )
    {
        if (Log.isDebugOn()) {Log.debug("QuoteCache -> calling containsQuote: userId=" + userId);}
        return ( getQuoteMap().containsKey( new SessionKeyContainer(sessionName, productKey)) );
    }

    /**
    * gets the quote from the collection given the quote key
    * @param quoteKey the quote key information
    * @return QuoteDetailStruct containing the quote information
    */
    public QuoteDetailStruct getQuoteByQuoteKey(int quoteKey)
    {
        if (Log.isDebugOn()) {Log.debug("QuoteCache -> calling getQuoteByQuoteKey: userId=" + userId);}
        synchronized (this)
        {
	        SessionKeyContainer sysQuoteKey = getQuoteKeyMap().get(Integer.valueOf(quoteKey));
	        if (sysQuoteKey == null)
	        {
	            return null;
	        }
	        else
	        {
	            return getQuoteMap().get(sysQuoteKey);
	        }
        }
    }

    /**
    * gets all quote from the collection for the current user
    *
    * @return QuoteDetailStruct[] containing all quote information
    */
    public synchronized QuoteDetailStruct[] getAllQuotes(String sessionName)
    {
        Iterator<QuoteDetailStruct> iterator = getQuoteMap().values().iterator();

        ArrayList<QuoteDetailStruct> quoteDetails = new ArrayList<QuoteDetailStruct>();

        while( iterator.hasNext() )
        {
            QuoteDetailStruct quote = iterator.next();
            if (quote.quote.sessionName.equals(sessionName)) {
                quoteDetails.add(quote);
            }
        }
        QuoteDetailStruct[] theQuotes = new QuoteDetailStruct[quoteDetails.size()];
        if (Log.isDebugOn()) 
        {
        	Log.debug("QuoteCache -> calling getAllQuotes: userId=" + userId + "sessionName:" + sessionName + " quotes " + quoteDetails.size());
        }
        return quoteDetails.toArray(theQuotes);
    }

    /**
    * gets all quote from the collection for the current user
    *
    * @return QuoteDetailStruct[] containing all quote information
    */
    public synchronized QuoteDetailStruct[] getAllQuotes()
    {
        if (Log.isDebugOn()) 
        {
        	Log.debug("QuoteCache -> calling getAllQuotes: userId=" + userId);
        }
        QuoteDetailStruct[] theQuotes = new QuoteDetailStruct[getQuoteMap().size()];
        return getQuoteMap().values().toArray(theQuotes);
    }

    /**
    * gets quotes from the collection for the classes
    *
    * @return QuoteDetailStruct[] containing all quote information
    */
    public synchronized QuoteDetailStruct[] getQuotesForClass(String sessionName, int classKey)
    {
        if (Log.isDebugOn()) {Log.debug("QuoteCache -> calling getQuotesForClass: userId=" + userId + " classKey=" + classKey + " sessionName=" + sessionName);}
        Map<SessionKeyContainer, SessionKeyContainer> products = getClassKeyProductMap().get(new SessionKeyContainer(sessionName, classKey));

        if (products == null )
        {
            return EMPTY_QuoteDetailStruct_ARRAY;
        }
        else
        {
            Iterator<SessionKeyContainer> iterator = products.values().iterator();
            QuoteDetailStruct[] quoteDetails = new QuoteDetailStruct[products.size()];

            int i = 0;
            while( iterator.hasNext() )
            {
                SessionKeyContainer productKey = iterator.next();
                quoteDetails[i++] = getQuote(productKey.getSessionName(), productKey.getKey());
            }

            return quoteDetails;
        }
    }

    /**
    * gets all quotes from the collection for given class
    * quote cache need to modified for better performance !!!
    *
    * @return QuoteDetailStruct[] containing all quote information
    */
    public synchronized QuoteDetailStruct[] getQuotesForClass(int classKey)
    {
        if (Log.isDebugOn()) {Log.debug("QuoteCache -> calling getQuotesForClass: userId=" + userId + " classKey=" + classKey);}
        Iterator<QuoteDetailStruct> iterator = getQuoteMap().values().iterator();

        ArrayList<QuoteDetailStruct> quoteDetails = new ArrayList<QuoteDetailStruct>();
        while( iterator.hasNext() )
        {
            QuoteDetailStruct quote = iterator.next();
            if (quote.productKeys.classKey == classKey ){
                quoteDetails.add(quote);
            }
        }
        QuoteDetailStruct[] theQuotes = new QuoteDetailStruct[quoteDetails.size()];
        return quoteDetails.toArray(theQuotes);
    }

    /**
    * cancels the quote from the collection given the product information
    *
    * @param productKey the product key information
    */
    public void cancelQuote(String sessionName, int productKey, short cancelReason)
    {

        cancelQuote(sessionName, productKey, cancelReason, true);
    }

    /** Cancel the quote for the given product.
     * @param sessionName Trading session containing the product.
     * @param productKey Key identifying the product.
     * @param cancelReason Reason to report in callback to acceptQuoteCancelReport.
     * @param reportCancel If true, callback to acceptQuoteCancelReport; if
     *     false, do not callback.
     */
    public void cancelQuote(String sessionName, int productKey, short cancelReason, boolean reportCancel)
    {
        QuoteCallSnapshot.quoteCacheLockWaitStart();
    	synchronized (this)
    	{
            QuoteCallSnapshot.quoteCacheLockWaitEnd();
	        if (Log.isDebugOn()) {Log.debug("QuoteCache -> calling cancelQuote: userId=" + userId + ": productKey=" + productKey);}
	        QuoteDetailStruct theQuote = removeFromQuoteMap(sessionName, productKey);
	        if (theQuote != null)
	        {
	            QuoteDetailStruct[] quoteDetails = new QuoteDetailStruct[1];
	            quoteDetails[0] = theQuote;
                //Check to see the user is eligible for quote delete report
                reportCancel = reportCancel && isReportCancel(isQuoteDeleteReportDispatch, cancelReason);
	            deleteQuotes(quoteDetails, sessionName, cancelReason, reportCancel);
	        }
    	}
        QuoteCallSnapshot.quoteCacheLockHoldEnd();
    }

    public synchronized void cancelAllQuotes(String sessionName, boolean sendCancelReports)
    {
        boolean reportCancel = true;

        if (Log.isDebugOn()) {Log.debug("QuoteCache -> calling cancelAllQuotes: userId=" + userId);}
        QuoteDetailStruct[] quotes = getAllQuotes(sessionName);
        //Check to see the user is eligible for quote delete report
        reportCancel = sendCancelReports? isReportCancel(isQuoteDeleteReportDispatch, ActivityReasons.USER): false;
        deleteQuotes(quotes, sessionName, ActivityReasons.USER, reportCancel);
    }

    public void cancelQuotesByClass(String sessionName, int classKey, boolean sendCancelReports)
    {
        QuoteCallSnapshot.quoteCacheLockWaitStart();
    	synchronized (this)
    	{
            QuoteCallSnapshot.quoteCacheLockWaitEnd();
            cancelQuotesByClass(sessionName, classKey, ActivityReasons.USER, sendCancelReports);
		}
        QuoteCallSnapshot.quoteCacheLockHoldEnd();
    }

    public void cancelQuotesByClass(String sessionName, int classKey, short activityReason)
    {
    	cancelQuotesByClass(sessionName, classKey, activityReason, true);
    }

    public synchronized void cancelQuotesByClass(String sessionName, int classKey, short activityReason, boolean sendCancelReports)
    {
        boolean reportCancel = true;
        if (Log.isDebugOn()) {Log.debug("QuoteCache -> calling cancelQuotesByClass: userId=" + userId + ": classKey=" + classKey);}
        QuoteDetailStruct[] quotes = getQuotesForClass(sessionName, classKey);
        // if sendCancelReports is false, that means it is requested through the new API to not to deliver a report;
        // so, do not send one then. If set true, follow the existing logic to decide whether to send one or not.
        reportCancel = sendCancelReports? isReportCancel(isQuoteDeleteReportDispatch, activityReason): false;
        deleteQuotes(quotes, sessionName, activityReason, reportCancel);
    }

    private synchronized void deleteQuotes(QuoteDetailStruct[] quotes, String sessionName, short cancelReason, boolean reportCancel)
    {
        int numQuotes = quotes.length;
        if (Log.isDebugOn()) {Log.debug("QuoteCache -> calling deleteQuotes: userId=" + userId + " size=" + quotes.length + " cancelReason=" + cancelReason);}
        if ( numQuotes == 0)
            return;


        QuoteDeleteReportWrapper[] container = new QuoteDeleteReportWrapperImpl[numQuotes];

        for (int i = 0; i < numQuotes; i++)
        {
            QuoteDetailStruct quote = quotes[i];

            removeFromQuoteMap(sessionName, quote.quote.productKey);
            removeFromClassKeyProductMap(quote);
            removeFromKeyMap(quote);

            quote.statusChange = StatusUpdateReasons.CANCEL;

            QuoteCancelReportStruct cancelReport = new QuoteCancelReportStruct();
            cancelReport.quoteKey       = quote.quote.quoteKey;
            cancelReport.productKeys    = quote.productKeys;
            cancelReport.productName    = quote.productName;
            cancelReport.statusChange   = quote.statusChange;
            cancelReport.cancelReason   = cancelReason;

            container[i] = new QuoteDeleteReportWrapperImpl(quote, cancelReport);
        }
        if (reportCancel)
        {
            dispatchQuoteStatus(container, ChannelType.QUOTE_DELETE_REPORT, userId);
        }
    }

    /**
    * cancels the quote from the collection given the product information
    *
    * @param productKey the product key information
    */
    private synchronized void removeQuote(String sessionName, int productKey, boolean reportCancel)
    {
        if (Log.isDebugOn()) {Log.debug("QuoteCache -> calling removeQuote: userId=" + userId);}
        cancelQuote(sessionName, productKey, ActivityReasons.USER, reportCancel);
    }

    /**
    * updates the last fill report for product information
    *
    * @param quoteInfo the filled report information
    */
    private void updateFill(UserClassContainer key, QuoteInfoStruct quoteInfo)
    {
    	synchronized (getFilledReportMap())
    	{
    		getFilledReportMap().put(key, quoteInfo);
    	}
    }

    /**
    * updates the last bust report for product information
    *
    * @param quoteInfo the bust report information
    */
    private void updateBust(UserClassContainer key, QuoteInfoStruct quoteInfo)
    {    	
    	synchronized (getBustedReportMap())
    	{
    		getBustedReportMap().put(key, quoteInfo);
    	}
    }

    private void updateStatus(Integer key, Integer transactionSeq)
    {
        synchronized(getUpdateStatusMap())
        {    	
        	getUpdateStatusMap().put(key, transactionSeq);
        }
    }

    /**
    * applies the fill report for quote information
    *
    * @param filledQuote the filled quote report struct
    * @return QuoteDetailStruct.  Null if the quote does not exist
    */
    private QuoteDetailStruct applyFill(QuoteInfoStruct quoteInfo, FilledReportStruct filledQuote)
    {
// modified to NOT use the quoteKeyMap cache - memory reduction
//
//        QuoteDetailStruct theQuote = getQuoteByQuoteKey(quoteInfo.quoteKey);
        SessionKeyContainer sessionKeyContainer = new SessionKeyContainer( filledQuote.sessionName, quoteInfo.productKey );
        synchronized (this)
        {
	        QuoteDetailStruct theQuote = getQuoteMap().get( sessionKeyContainer );
	
	        // now check if there is a cached quote detail to publish quote status information
	        if (theQuote != null )
	        {
	            theQuote.quote.transactionSequenceNumber = quoteInfo.transactionSequenceNumber;
	            if(SidesSpecifier.isBuyEquivalent(filledQuote.side))
	            {
	                theQuote.quote.bidQuantity = filledQuote.leavesQuantity;
	            }
	            else
	            {
	                theQuote.quote.askQuantity = filledQuote.leavesQuantity;
	            }
	        }
	
	        return theQuote;
        }
    }

    /**
    * applies the bust report for quote information
    *
    * @param quoteInfo the bust quote report struct
    * @return QuoteDetailStruct.  Null if the quote does not exist
    */
    private QuoteDetailStruct applyBust(QuoteInfoStruct quoteInfo)
    {
    	synchronized(this)
    	{
	        QuoteDetailStruct theQuote = getQuoteByQuoteKey(quoteInfo.quoteKey);
	        if (theQuote != null )
	        {
	            theQuote.quote.transactionSequenceNumber = quoteInfo.transactionSequenceNumber;
	        }
	        return theQuote;
    	}
    }

    /**
    * retrieves the last fill report for product information
    *
    * @param quoteKey the quote key.
    * @return
    */
    public QuoteInfoStruct getFillReport(UserClassContainer quoteKey)
    {
        if (Log.isDebugOn()) {Log.debug("QuoteCache -> calling getFillReport: userId=" + userId);}
    	synchronized (getFilledReportMap())
    	{
	        return getFilledReportMap().get(quoteKey);
    	}
    }


    /**
    * retrieves the last bust report for product information
    *
    * @param quoteKey the quote key.
    * @return
    */
    public QuoteInfoStruct getBustReport(UserClassContainer quoteKey)
    {
        if (Log.isDebugOn()) {Log.debug("QuoteCache -> calling getBustReport: userId=" + userId);}
    	synchronized (getBustedReportMap())
    	{
	        return getBustedReportMap().get(quoteKey);
    	}
    }

    public Integer getUpdateStatusReport(Integer quoteKey)
    {
        if (Log.isDebugOn()) {Log.debug("QuoteCache -> calling getStatusReport: userId=" + userId);}
        synchronized(getUpdateStatusMap())
        {
        	return getUpdateStatusMap().get(quoteKey);
        }
    }

    /**
    * removed the quote from the collection given the quoteKey information
    *
    * @param quoteKey the quote information to be removed
    */
    public synchronized void cancelQuoteByQuoteKey(int quoteKey)
    {
        if (Log.isDebugOn()) {Log.debug("QuoteCache -> calling cancelQuoteByQuoteKey: userId=" + userId);}
        SessionKeyContainer productKey = getQuoteKeyMap().remove(Integer.valueOf(quoteKey));

        QuoteDetailStruct quoteDetail = removeFromQuoteMap(productKey.getSessionName(), productKey.getKey());
        removeFromClassKeyProductMap(quoteDetail);
    }

    private boolean isProductValid(int productKey)
    {
        boolean isValid = false;
        try
        {
            ProductStruct product = ServicesHelper.getProductQueryServiceAdapter().getProductByKey(productKey);
            if(product != null)
            {
                isValid = true;
            }
        }
        catch (org.omg.CORBA.UserException e)
        {
            Log.exception(e);
        }
        return isValid;
    }

    private void dispatchQuoteStatus(Object channalData, int channelType, String quoteUserId )
    {
        ChannelKey dispatchChannelKey;
        switch (channelType)
        {
            case ChannelType.QUOTE_FILL_REPORT:
            case ChannelType.QUOTE_DELETE_REPORT:
            case ChannelType.QUOTE_BUST_REPORT:
            case ChannelType.QUOTE_STATUS:
            case ChannelType.QUOTE_STATUS_UPDATE:
            case ChannelType.QUOTE_FILL_REPORT_BY_TRADING_FIRM:
            case ChannelType.QUOTE_BUST_REPORT_BY_TRADING_FIRM:
                if ( userId.equals(quoteUserId) || isTradingFirm)
                {
                    dispatchChannelKey = new ChannelKey(channelType, userId);
                    ChannelEvent event = internalEventChannel.getChannelEvent(this, dispatchChannelKey, channalData);
                    quoteStatusSupplier.dispatch(event);
                }
                break;
            case ChannelType.QUOTE_FILL_REPORT_BY_FIRM:
            case ChannelType.QUOTE_BUST_REPORT_BY_FIRM:
                if ( firmKey != null)
                {
                    dispatchChannelKey = new ChannelKey(channelType, firmKeyContainer);
                    ChannelEvent event = internalEventChannel.getChannelEvent(this, dispatchChannelKey, channalData);
                    quoteStatusSupplier.dispatch(event);
                }
                break;
            default :
                Log.alarm("User Based Quote Cache -> Calling dispatchQuoteStatus, Wrong Channel : " + channelType + ": userId=" + userId);
            break;
        }
    }

  /**
     * builds the quote detail struct based on the quote struct.
     * will need to go to the product service to get the product name struct
     * @author Connie Feng
     */
     private QuoteDetailStruct buildQuoteDetailStruct(QuoteStruct theQuoteStruct)
     {
      QuoteDetailStruct ret = new QuoteDetailStruct();
      ProductStruct product;
      ret.quote = theQuoteStruct;

      try
      {
//PQRefactor: this used to be synchronized
//            product = ProductQueryManagerImpl.getProduct(theQuoteStruct.productKey);
          product = getProductQueryServiceAdapter().getProductByKey(theQuoteStruct.productKey);
      }
      catch(NotFoundException e)
      {
          Log.exception(e);
          product = ProductStructBuilder.buildProductStruct();
      }
      catch(SystemException e)
      {
          Log.exception(e);
          product = ProductStructBuilder.buildProductStruct();
       }
      catch(AuthorizationException e)
      {
          Log.exception(e);
          product = ProductStructBuilder.buildProductStruct();
      }
      catch(DataValidationException e)
      {
          Log.exception(e);
          product = ProductStructBuilder.buildProductStruct();
      }
      catch(CommunicationException e)
      {
          Log.exception(e);
          product = ProductStructBuilder.buildProductStruct();
      }

      ret.productName = product.productName;

      ret.productKeys = product.productKeys;
      ret.statusChange = StatusUpdateReasons.CANCEL;

      return ret;
     }

    protected ProductQueryServiceAdapter getProductQueryServiceAdapter()
    {
        if(pqAdapter == null)
        {
            pqAdapter = ServicesHelper.getProductQueryServiceAdapter();
        }
        return pqAdapter;
    }
    /**
     * 
     * @param isQuoteDeleteReportDispatch
     * @author Yaowapa Krueya
     */
    public void setQuoteDeleteReportDispatch(boolean isQuoteDeleteReportDispatch)
    {
        this.isQuoteDeleteReportDispatch = isQuoteDeleteReportDispatch;
        
    }
    /**
     * 
     * @param isQuoteDeleteReportDispatch, cancelReason
     * @author Yaowapa Krueya
     */
    private boolean isReportCancel(boolean isQuoteDeleteReportDispatch, short cancelReason)
    {
        
        if (!isQuoteDeleteReportDispatch && cancelReason == ActivityReasons.USER)
        {
            return false;   
        }
        return DEFAULT_REPORT;
    }
    
   
    
    private String getQuoteInfoString(QuoteInfoStruct quoteInfo)
    {
        StringBuilder toStr = new StringBuilder(60);
        //Printed in this format -> :CBOE:690:KCD:pkey=123456:seq=1
        toStr.append(':');
        toStr.append(quoteInfo.firm.exchange).append(':');
        toStr.append(quoteInfo.firm.firmNumber).append(':');
        toStr.append(quoteInfo.userId);
        toStr.append(":pkey=").append(quoteInfo.productKey);
        toStr.append(":qkey=").append(quoteInfo.quoteKey);
        toStr.append(":seq=").append(quoteInfo.transactionSequenceNumber);

        return toStr.toString();
    }

    public List<String> getGroupMembers()
    {
        return groupMembers;
    }
}
