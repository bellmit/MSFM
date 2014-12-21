package com.cboe.application.quote.common;

import static com.cboe.application.shared.LoggingUtil.createQuoteLog;

import java.util.Calendar;
import java.util.Map;

import org.omg.CORBA.UserException;

import com.cboe.application.quote.QuoteCache;
import com.cboe.application.quote.QuoteCacheFactory;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.TransactionTimingUtil;
import com.cboe.application.util.QuoteCallSnapshot;
import com.cboe.domain.util.TimeServiceWrapper;
import com.cboe.domain.rateMonitor.RateManager;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiConstants.ActivityReasons;
import com.cboe.idl.cmiConstants.QuoteUpdateControlValues;
import com.cboe.idl.cmiConstants.StatusUpdateReasons;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiQuote.ClassQuoteResultStructV3;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.idl.cmiQuote.QuoteStruct;
import com.cboe.idl.cmiQuote.QuoteStructV3;
import com.cboe.idl.cmiQuote.QuoteStructV4;
import com.cboe.idl.cmiQuote.RFQStruct;
import com.cboe.idl.constants.OperationTypes;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.ProductQueryServiceAdapter;
import com.cboe.interfaces.application.TCSProcessWatcherManager;
import com.cboe.interfaces.application.UserEnablement;
import com.cboe.interfaces.application.UserQuoteService;
import com.cboe.interfaces.businessServices.MarketMakerQuoteService;
import com.cboe.interfaces.domain.RateMonitorHome;
import com.cboe.interfaces.domain.RateMonitorTypeConstants;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ExceptionBuilder;

/**
 * This class implements UserQuoteService which contains the common user quote service that is shared by cmi and non cmi
 * users. It contains the userEnablement, rateMonitor checking of each call and the calls to the server services.
 * @author Jing Chen
 * @author Gijo Joseph
 * @version 1/18/2008
 */
public class UserQuoteServiceImpl extends BObject implements UserQuoteService
{
    BaseSessionManager sessionManager;
    protected MarketMakerQuoteService     quoteService;
    protected RateMonitorHome             rateMonitorHome;
    protected String thisUserId;
    protected String thisExchange;
    protected String thisAcronym;
    protected UserEnablement              userEnablement;
    protected Map allSessionConstraints;
    protected QuoteCache quoteCache;
    private ProductQueryServiceAdapter pqAdapter;
    protected RateManager rateManager;

    private final TCSProcessWatcherManager pwManager;
    
    private static final ClassQuoteResultStructV3[] EMPTY_ClassQuoteResultStructV3_ARRAY = new ClassQuoteResultStructV3[0];

    public static final String RATE_MONITOR_CACHE_SESSION_NAME = "rateMonitorCacheSessionName";
    public static final String RATE_MONITOR_CACHE_SESSION_NAME_DEFAULT = "";
    public static final String RATE_MONITOR_CACHE_SESSION_NAME_DELIMITATE = ",";

    public UserQuoteServiceImpl(BaseSessionManager sessionManager, Map sessionConstraints)
    {
        this.sessionManager = sessionManager;
        this.allSessionConstraints = sessionConstraints;
        pqAdapter = ServicesHelper.getProductQueryServiceAdapter();
        try
        {
            thisUserId = sessionManager.getUserId();
            thisExchange = sessionManager.getValidSessionProfileUserV2().userInfo.userAcronym.exchange;
            thisAcronym = sessionManager.getValidSessionProfileUserV2().userInfo.userAcronym.acronym;
            Short[] monitorTypes = new Short[]{ RateMonitorTypeConstants.ACCEPT_QUOTE, RateMonitorTypeConstants.QUOTES, RateMonitorTypeConstants.QUOTE_BLOCK_SIZE};
            rateManager = new RateManager(sessionConstraints,thisUserId,thisExchange,thisAcronym,monitorTypes);
        }
        catch(UserException e)
        {
            Log.alarm(this, "fatal error in getting the userId");
        }
        userEnablement = ServicesHelper.getUserEnablementService(thisUserId, thisExchange, thisAcronym);
        quoteCache = QuoteCacheFactory.find(thisUserId);

        //set the quote delete report dispatch to available in QuoteCache
        quoteCache.setQuoteDeleteReportDispatch(getQuoteDeleteReportDispatch(sessionConstraints));

        quoteService = ServicesHelper.getMarketMakerQuoteService();
        
        pwManager = getProcessWatcherManager();
    }

    public void verifyUserQuoteEntryEnablementForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        userEnablement.verifyUserEnablementForProduct(sessionName, productKey, OperationTypes.QUOTE_QUOTEENTRY);
    }

    public void verifyUserQuoteEntryEnablementForSession(String sessionName)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        userEnablement.verifyUserEnablementForSession(sessionName, OperationTypes.QUOTE_QUOTEENTRY);
    }
    public void verifyUserQuoteEntryEnablementForClass(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        userEnablement.verifyUserEnablement(sessionName, classKey, OperationTypes.QUOTE_QUOTEENTRY);
    }

    public void verifyUserQuoteStatusEnablementForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        userEnablement.verifyUserEnablementForProduct(sessionName, productKey, OperationTypes.QUOTE_QUOTESTATUS);
    }

    public void verifyUserQuoteStatusEnablementForClass(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        userEnablement.verifyUserEnablement(sessionName, classKey, OperationTypes.QUOTE_QUOTESTATUS);
    }

    public void verifyUserRFQEnablementForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        userEnablement.verifyUserEnablementForProduct(sessionName, productKey, OperationTypes.QUOTE_RFQ);
    }

    public void verifyUserRFQEnablementForClass(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        userEnablement.verifyUserEnablement(sessionName, classKey, OperationTypes.QUOTE_RFQ);
    }

    // This method is supported ONLY for FIX.
    public int acceptQuote(QuoteStruct quote)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        QuoteStruct[] quoteStructs = {quote};
        String sessionName = quote.sessionName;
        int classKey;
        try
        {
            classKey = pqAdapter.getProductByKey(quote.productKey).productKeys.classKey;
        }
        catch(NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
        QuoteCallSnapshot.classKeyLockWaitStart();
    	int semUsed = QuoteSemaphoreHandler.acquireQuoteEntryAccess(thisUserId, sessionName, classKey);
        QuoteCallSnapshot.classKeyLockWaitEnd();
        QuoteCallSnapshot.setSemaphoresUsed(semUsed);
        try
        {
            QuoteStructV3[] quoteStructsV3 = preProcessMassQuote(sessionName, classKey, quoteStructs);
        	internal_processMassQuotesForClassV3(classKey, quoteStructsV3);
        }
        finally
        {
        	QuoteSemaphoreHandler.releaseQuoteEntryAccess(thisUserId, sessionName, classKey);
            QuoteCallSnapshot.classKeyLockHoldEnd();
        }
        return classKey;
    }

    public int acceptQuoteV7(QuoteStructV4 quoteV4)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        QuoteStructV4[] quoteStructs = {quoteV4};
        String sessionName = quoteV4.quoteV3.quote.sessionName;
        int classKey;
        try
        {
            classKey = pqAdapter.getProductByKey(quoteV4.quoteV3.quote.productKey).productKeys.classKey;
        }
        catch(NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
        QuoteCallSnapshot.classKeyLockWaitStart();
    	int semUsed = QuoteSemaphoreHandler.acquireQuoteEntryAccess(thisUserId, sessionName, classKey);
        QuoteCallSnapshot.classKeyLockWaitEnd();
        QuoteCallSnapshot.setSemaphoresUsed(semUsed);
        try
        {
            preProcessMassQuoteV7(sessionName, classKey, quoteStructs);
        	internal_processMassQuotesForClassV7(classKey, quoteStructs);
        }
        finally
        {
        	QuoteSemaphoreHandler.releaseQuoteEntryAccess(thisUserId, sessionName, classKey);
            QuoteCallSnapshot.classKeyLockHoldEnd();
        }
        return classKey;
    }

    private QuoteStructV3[] preProcessMassQuote(String sessionName, int classKey, QuoteStruct[] quoteStructs)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException
    {
    	int len = quoteStructs.length;
    	int validQuoteLen =0;

        QuoteStructV3[] quoteStructsV3 = new QuoteStructV3[len];
        for(int i=0; i<len; i++)
        {
            quoteStructsV3[i] = convertToV3Struct(quoteStructs[i]);
            // exclude the cancel quote
            if (quoteStructsV3[i].quote.bidQuantity > 0 || quoteStructsV3[i].quote.askQuantity > 0){
    			validQuoteLen++;
    		}
        }
		monitorUserEnablementAndRate(sessionName, classKey, len, validQuoteLen);
        return quoteStructsV3;
    }

    private void preProcessMassQuoteV3(String sessionName, int classKey, QuoteStructV3[] quoteStructs)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException
    {
    	int len = quoteStructs.length;
    	int validQuoteLen = 0;

        for(int i=0; i<len; i++)
        {
            quoteStructs[i].quote.transactionSequenceNumber = 1;
            quoteStructs[i].quote.userId = thisUserId;
            quoteStructs[i].quote.quoteKey = quoteCache.generateQuoteKey();
//          exclude the cancel quote
            if (quoteStructs[i].quote.bidQuantity > 0 || quoteStructs[i].quote.askQuantity > 0){
    			validQuoteLen++;
    		}
        }
		monitorUserEnablementAndRate(sessionName, classKey, len, validQuoteLen);
    }

    private void preProcessMassQuoteV7(String sessionName, int classKey, QuoteStructV4[] quoteStructs)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException
    {
    	int len = quoteStructs.length;
    	int validQuoteLen = 0;

        for(int i=0; i<len; i++)
        {
            quoteStructs[i].quoteV3.quote.transactionSequenceNumber = 1;
            quoteStructs[i].quoteV3.quote.userId = thisUserId;
            quoteStructs[i].quoteV3.quote.quoteKey = quoteCache.generateQuoteKey();
//          exclude the cancel quote
            if (quoteStructs[i].quoteV3.quote.bidQuantity > 0 || quoteStructs[i].quoteV3.quote.askQuantity > 0){
    			validQuoteLen++;
    		}
        }
		monitorUserEnablementAndRate(sessionName, classKey, len, validQuoteLen);
    }

    private int monitorUserEnablementAndRate(String sessionName, int classKey, int len, int numQuotes)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException
    {
        if (numQuotes > 0)
        {
            verifyUserQuoteEntryEnablementForClass(sessionName, classKey);
        }
        rateManager.canAcceptBlock(sessionName, RateMonitorTypeConstants.QUOTE_BLOCK_SIZE,len);
        if (numQuotes > 0)
        {
        	rateManager.monitorQuoteRate(sessionName, numQuotes);
        }
        return len;
    }

    public ClassQuoteResultStructV3[] acceptQuotesForClassV3(int classKey, QuoteStructV3[] quotes)
            throws SystemException, CommunicationException, DataValidationException, NotAcceptedException, AuthorizationException, TransactionFailedException
    {
        if(quotes.length > 0)
        {

            String sessionName = quotes[0].quote.sessionName;
            QuoteCallSnapshot.classKeyLockWaitStart();
        	int semUsed = QuoteSemaphoreHandler.acquireQuoteEntryAccess(thisUserId, sessionName, classKey);
            QuoteCallSnapshot.classKeyLockWaitEnd();
            QuoteCallSnapshot.setSemaphoresUsed(semUsed);
            try
            {
                preProcessMassQuoteV3(sessionName, classKey, quotes);
            	return internal_processMassQuotesForClassV3(classKey, quotes);
            }
            finally
            {
            	QuoteSemaphoreHandler.releaseQuoteEntryAccess(thisUserId, sessionName, classKey);
                QuoteCallSnapshot.classKeyLockHoldEnd();
            }
        }
        else
        {
            return EMPTY_ClassQuoteResultStructV3_ARRAY;
        }
    }

    public ClassQuoteResultStructV3[] acceptQuotesForClassV7(int classKey, QuoteStructV4[] quotes)
            throws SystemException, CommunicationException, DataValidationException, NotAcceptedException, AuthorizationException, TransactionFailedException
    {
        if(quotes.length > 0)
        {

            String sessionName = quotes[0].quoteV3.quote.sessionName;
            QuoteCallSnapshot.classKeyLockWaitStart();
        	int semUsed = QuoteSemaphoreHandler.acquireQuoteEntryAccess(thisUserId, sessionName, classKey);
            QuoteCallSnapshot.classKeyLockWaitEnd();
            QuoteCallSnapshot.setSemaphoresUsed(semUsed);
            try
            {
                preProcessMassQuoteV7(sessionName, classKey, quotes);
            	return internal_processMassQuotesForClassV7(classKey, quotes);
            }
            finally
            {
            	QuoteSemaphoreHandler.releaseQuoteEntryAccess(thisUserId, sessionName, classKey);
                QuoteCallSnapshot.classKeyLockHoldEnd();
            }
        }
        else
        {
            return EMPTY_ClassQuoteResultStructV3_ARRAY;
        }
    }

    public void cancelAllQuotes(String sessionName)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        Log.information(this, createQuoteLog("cancelAllQuotes", sessionManager.toString(), sessionName));
        processCancelAllQuotes(sessionName, true);
    }

    public int cancelQuote(String sessionName, int productKey)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        Log.information(this, createQuoteLog("cancelQuote", sessionManager.toString(), sessionName, productKey));
        return processSingleQuoteCancel(sessionName, productKey, true);
    }

    public void cancelQuotesByClass(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, NotAcceptedException, NotFoundException
    {
        Log.information(this, createQuoteLog("cancelQuotesByClass", sessionManager.toString(), classKey, sessionName));
        processCancelQuotesByClass(classKey, sessionName, true);
    }

    public int cancelQuoteV5(java.lang.String sessionName, int productKey, boolean sendCancelReports)
    	throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException, com.cboe.exceptions.TransactionFailedException, com.cboe.exceptions.NotAcceptedException, com.cboe.exceptions.NotFoundException
    {
        String smgr = sessionManager.toString();
        StringBuilder calling = new StringBuilder(smgr.length()+sessionName.length()+85);
        calling.append("calling cancelQuoteV5 for ").append(smgr)
               .append(" session:").append(sessionName)
               .append(" productKey:").append(productKey)
               .append(" sendCancelReports:").append(sendCancelReports);
        Log.information(this, calling.toString());

        return processSingleQuoteCancel(sessionName, productKey, sendCancelReports);
    }

    public void cancelQuotesByClassV5(java.lang.String sessionName, int classKey, boolean sendCancelReports)
    	throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException, com.cboe.exceptions.TransactionFailedException, com.cboe.exceptions.NotAcceptedException, com.cboe.exceptions.NotFoundException
    {
        String smgr = sessionManager.toString();
        StringBuilder calling = new StringBuilder(smgr.length()+sessionName.length()+90);
        calling.append("calling cancelQuotesByClassV5 for ").append(smgr)
               .append(" session:").append(sessionName)
               .append(" classKey:").append(classKey)
               .append(" sendCancelReports:").append(sendCancelReports);
        Log.information(this, calling.toString());

        processCancelQuotesByClass(classKey, sessionName, sendCancelReports);
    }

    public void cancelAllQuotesV5(java.lang.String sessionName, boolean sendCancelReports)
    	throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException, com.cboe.exceptions.NotAcceptedException, com.cboe.exceptions.TransactionFailedException
    {
        String smgr = sessionManager.toString();
        StringBuilder calling = new StringBuilder(smgr.length()+sessionName.length()+70);
        calling.append("calling cancelAllQuotesV5 for ").append(smgr)
               .append(" session:").append(sessionName)
               .append(" sendCancelReports:").append(sendCancelReports);
        Log.information(this, calling.toString());
        processCancelAllQuotes(sessionName, sendCancelReports);
    }


    public QuoteDetailStruct getQuote(String sessionName, int productKey)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException, NotFoundException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling getQuote for " + sessionManager + " productKey:" + productKey);
        }
        verifyUserQuoteEntryEnablementForProduct(sessionName, productKey);
        QuoteDetailStruct theQuote = quoteCache.getQuote(sessionName, productKey);
        if (theQuote == null)
        {
            throw ExceptionBuilder.notFoundException("No Quote Found for product: " + productKey, 1);
        }
        return theQuote;
    }

    public RFQStruct[] getRFQ(String sessionName, int classKey)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        verifyUserRFQEnablementForClass(sessionName, classKey);
        int[] classKeys = {classKey};
        try
        {
        	TransactionTimingUtil.setEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }
        //TCS - Before we query the server check if the server is down
        checkSystemAvailability(classKey, sessionName);
        RFQStruct[] rfqs = quoteService.getRFQ(sessionName, classKeys);
        return rfqs;
    }

    public void publishUnAckedQuotes()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ServicesHelper.getQuoteStatusAdminPublisher().subscribeQuoteStatus(thisUserId);
    }

    public void publishUnAckedQuotesForClass(int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        int[] groups = ServicesHelper.getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct paramStruct = new RoutingParameterStruct(groups, "UNKNOW_SESSION", classKey, (short)0);
        ServicesHelper.getQuoteStatusAdminPublisher().publishUnackedQuoteStatus(paramStruct, thisUserId);
    }
    /**
     * retrieve the quote delete report dispatch property value from sessionConstraints map.
     * @param sessionConstraints
     * @author Yaowapa Krueya
     */
    private boolean getQuoteDeleteReportDispatch(Map sessionConstraints)
    {

        Object constraint = sessionConstraints.get(UserQuoteServiceHomeImpl.QUOTE_DELETE_REPORT_PROPERTY_NAME);
        if (constraint != null)
        {
            return ((Boolean)constraint).booleanValue();
        }
        return true;
    }
    protected RateMonitorHome getRateMonitorHome()
    {
        if (rateMonitorHome == null )
        {
           try {
                rateMonitorHome = (RateMonitorHome)HomeFactory.getInstance().findHome(RateMonitorHome.HOME_NAME);
            }
            catch (CBOELoggableException e) {
                    Log.exception(this, "session : " + sessionManager, e);
                    // a really ugly way to get around the missing exception in the interface...
                    throw new NullPointerException("Could not find RateMonitor Home");
            }
        }

        return rateMonitorHome;
    }

    protected QuoteStructV3 convertToV3Struct(QuoteStruct quote)
    {
        QuoteStructV3 quoteStructV3 = new QuoteStructV3();
        quote.transactionSequenceNumber = 1;
        quote.userId = thisUserId;
        quote.quoteKey = quoteCache.generateQuoteKey();
        quoteStructV3.quote = quote;
        quoteStructV3.quoteUpdateControlId = QuoteUpdateControlValues.CONTROL_DISABLED;
        return quoteStructV3;
    }

    protected int generateQuoteKeyBase()
    {
        Calendar calendar = TimeServiceWrapper.getCalendar();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        int currentSec = calendar.get(Calendar.SECOND);
        int currentMil = calendar.get(Calendar.MILLISECOND);
        int currentTimeInMil = currentHour*60*60*60 + currentMinute*60*60 + currentSec*60 + currentMil;
        return currentTimeInMil;
    }

     protected QuoteDetailStruct buildQuoteDetailStruct(QuoteStruct theQuoteStruct)
           throws SystemException, CommunicationException, DataValidationException, AuthorizationException
     {
        try
        {
            QuoteDetailStruct ret = new QuoteDetailStruct();
            ret.quote = theQuoteStruct;
            ProductStruct product = pqAdapter.getProductByKey(theQuoteStruct.productKey);
            ret.productName = product.productName;
            ret.productKeys = product.productKeys;
            ret.statusChange = StatusUpdateReasons.NEW;
            return ret;
        }
        catch(NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
     }
    protected QuoteDetailStruct buildQuoteDetailStructV3(QuoteStructV3 theQuoteStruct)
          throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
       try
       {
           QuoteDetailStruct ret = new QuoteDetailStruct();
           ret.quote = theQuoteStruct.quote;
           ProductStruct product = pqAdapter.getProductByKey(theQuoteStruct.quote.productKey);
           ret.productName = product.productName;
           ret.productKeys = product.productKeys;
           ret.statusChange = StatusUpdateReasons.NEW;
           return ret;
       }
       catch(NotFoundException e)
       {
           throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
       }
    }

    protected void processCancelAllQuotes(String sessionName, boolean sendCancelReports)
            throws SystemException, CommunicationException, AuthorizationException, NotAcceptedException, TransactionFailedException
    {
        try
        {
        	QuoteCallSnapshot.quoteCacheLockWaitStart();
        	QuoteSemaphoreHandler.acquireQuoteCancelAllAccess(thisUserId);
        	QuoteCallSnapshot.quoteCacheLockWaitEnd();
        	try
            {
                internal_processCancelAllQuotes(sessionName, sendCancelReports);
            }
        	finally
        	{
                    QuoteSemaphoreHandler.releaseQuoteCancelAllAccess(thisUserId);
                    QuoteCallSnapshot.quoteCacheLockHoldEnd();
        	}

        }
        catch (DataValidationException e)
        {
            Log.exception(this, "session : " + sessionManager, e);
            throw ExceptionBuilder.systemException("Bad user id = " + thisUserId, 0);
        }
    }


    protected int processSingleQuoteCancel(String sessionName, int productKey, boolean sendCancelReports)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        int classKey = getClassKeyByProductKey(productKey);

        QuoteCallSnapshot.classKeyLockWaitStart();
    	QuoteSemaphoreHandler.acquireSingleQuoteCancelAccess(thisUserId, sessionName, classKey);
        QuoteCallSnapshot.classKeyLockWaitEnd();
        try
        {
        	internal_processSingleQuoteCancel(sessionName, productKey, sendCancelReports);
        }
        finally
        {
        	QuoteSemaphoreHandler.releaseSingleQuoteCancelAccess(thisUserId, sessionName, classKey);
        	QuoteCallSnapshot.classKeyLockHoldEnd();
        }
        return classKey;
    }

    protected void processCancelQuotesByClass(int classKey, String sessionName, boolean sendCancelReports)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, NotAcceptedException, NotFoundException
    {
        QuoteCallSnapshot.classKeyLockWaitStart();
    	QuoteSemaphoreHandler.acquireQuoteCancelByClassAccess(thisUserId, sessionName, classKey);
    	QuoteCallSnapshot.classKeyLockWaitEnd();
        try
        {
            internal_processCancelQuotesByClass(classKey, sessionName, sendCancelReports);
        }
        finally
        {
        	QuoteSemaphoreHandler.releaseQuoteCancelByClassAccess(thisUserId, sessionName, classKey);
        	QuoteCallSnapshot.classKeyLockHoldEnd();
        }
    }

    private void internal_processCancelAllQuotes(String sessionName, boolean sendCancelReports)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException, NotAcceptedException, TransactionFailedException
    {
        try
        {
        	TransactionTimingUtil.resetEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to re-set EntityID! Exception details: " + e.getMessage());
        }
        
        QuoteCallSnapshot.startServerCall();
        quoteService.cancelAllQuotes(thisUserId, sessionName);
        QuoteCallSnapshot.endServerCall();

        quoteCache.cancelAllQuotes(sessionName, sendCancelReports);
    }

    private void internal_processSingleQuoteCancel(String sessionName, int productKey, boolean sendCancelReports)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        try
        {
        	TransactionTimingUtil.resetEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to re-set EntityID! Exception details: " + e.getMessage());
        }
        if ( !quoteCache.containsQuote( sessionName, productKey) )
        {
            String smgr = sessionManager.toString();
            StringBuilder qnex = new StringBuilder(smgr.length()+sessionName.length()+80);
            qnex.append("Quote does not exist for ").append(smgr)
                .append(" session:").append(sessionName)
                .append(" productKey:").append(productKey)
                .append(" in CAS, calling svr");
            Log.information(this, qnex.toString());
            try
            {
                QuoteCallSnapshot.startServerCall();
                quoteService.cancelQuote(thisUserId, sessionName, productKey);
                QuoteCallSnapshot.endServerCall();
            }
            catch (com.cboe.exceptions.TransactionFailedException e)
            {
                Log.information(this, "Exception thrown:Quote does not exist for " + sessionManager + " session:" + sessionName + " productKey:" + productKey + " in svr");
            }
        }
        else
        {
            QuoteCallSnapshot.startServerCall();
            quoteService.cancelQuote(thisUserId, sessionName, productKey);
            QuoteCallSnapshot.endServerCall();
            quoteCache.cancelQuote(sessionName, productKey, ActivityReasons.USER, sendCancelReports);
        }
    }

    private void internal_processCancelQuotesByClass(int classKey, String sessionName, boolean sendCancelReports)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, NotAcceptedException, NotFoundException
    {
        int[] classKeys = {classKey};

        try
        {
        	TransactionTimingUtil.resetEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to re-set EntityID! Exception details: " + e.getMessage());
        }
        QuoteCallSnapshot.startServerCall();
        quoteService.cancelQuotesByClass(thisUserId, sessionName, classKeys);
        QuoteCallSnapshot.endServerCall();

        quoteCache.cancelQuotesByClass(sessionName, classKey, sendCancelReports);
    }

    protected int getClassKeyByProductKey(int productKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        int classKey;
        try
        {
            classKey = pqAdapter.getProductByKey(productKey).productKeys.classKey;
        }
        catch(NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
        return classKey;
    }

    private ClassQuoteResultStructV3[] internal_processMassQuotesForClassV3(int classKey, QuoteStructV3[] quoteStructs)
            throws SystemException, CommunicationException, DataValidationException, NotAcceptedException, AuthorizationException
    {
        int len = quoteStructs.length;
        QuoteDetailStruct[] quoteDetails = new QuoteDetailStruct[len];
        ClassQuoteResultStructV3[] resultStructs = null;

        try
        {
            TransactionTimingUtil.resetTTContext();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to re-set EntityID! Exception details: " + e.getMessage());
        }
        
        //TCS - Before we query the server check if the server is down
        checkSystemAvailability(classKey, quoteStructs !=null && quoteStructs.length>0?quoteStructs[0].quote.sessionName:"");
        
        QuoteCallSnapshot.startServerCall();
        resultStructs = quoteService.acceptQuotesForClassV3(classKey, quoteStructs, sessionManager.getSessionKey());
        QuoteCallSnapshot.endServerCall();
        int length = resultStructs.length;

        // Count how many of the quotes were accepted.
        int numAccepted = 0;
        for (int i = 0; i < length; ++i)
        {
            if (resultStructs[i].quoteResult.errorCode == 0) // not error
            {
                quoteDetails[numAccepted] = buildQuoteDetailStructV3(quoteStructs[i]);
                quoteDetails[numAccepted].statusChange = StatusUpdateReasons.NEW;
                ++numAccepted;
            }
        }

        QuoteCallSnapshot.setSizeParams(quoteStructs.length, numAccepted);
        if (numAccepted > 0)
        {
            // If every quote was accepted, we can simply pass along the
            // 'quoteDetails' array.  Otherwise, we need to sift out the
            // accepted quotes and put then into a separate array.
            QuoteDetailStruct[] acceptedQuotes;
            if (numAccepted == len)
            {
                acceptedQuotes = quoteDetails;
            }
            else
            {
                // Forward only the accepted quotes to the quote cache.
                acceptedQuotes = new QuoteDetailStruct[numAccepted];
                int n = 0;
                for (int i = 0; i < resultStructs.length; ++i)
                {
                    if (resultStructs[i].quoteResult.errorCode == 0) // not error
                    {
                        acceptedQuotes[n] = quoteDetails[n];
                        n++;
                    }
                }
            }
            quoteCache.addQuotes(acceptedQuotes);
        }

        return resultStructs;
    }

    private ClassQuoteResultStructV3[] internal_processMassQuotesForClassV7(int classKey, QuoteStructV4[] quoteStructs)
            throws SystemException, CommunicationException, DataValidationException, NotAcceptedException, AuthorizationException
    {
        int len = quoteStructs.length;
        QuoteDetailStruct[] quoteDetails = new QuoteDetailStruct[len];
        ClassQuoteResultStructV3[] resultStructs = null;

        try
        {
        	TransactionTimingUtil.resetEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to re-set EntityID! Exception details: " + e.getMessage());
        }
        //TCS - Before we query the server check if the server is down
        checkSystemAvailability(classKey, quoteStructs !=null && quoteStructs.length>0?quoteStructs[0].quoteV3.quote.sessionName:"");
        QuoteCallSnapshot.startServerCall();
        resultStructs = quoteService.acceptQuotesForClassV4(classKey, quoteStructs, sessionManager.getSessionKey());
        QuoteCallSnapshot.endServerCall();
        int length = resultStructs.length;

        // Count how many of the quotes were accepted.
        int numAccepted = 0;
        for (int i = 0; i < length; ++i)
        {
            if (resultStructs[i].quoteResult.errorCode == 0) // not error
            {
                quoteDetails[numAccepted] = buildQuoteDetailStructV3(quoteStructs[i].quoteV3);
                quoteDetails[numAccepted].statusChange = StatusUpdateReasons.NEW;
                ++numAccepted;
            }
        }

        QuoteCallSnapshot.setSizeParams(quoteStructs.length, numAccepted);
        if (numAccepted > 0)
        {
            // If every quote was accepted, we can simply pass along the
            // 'quoteDetails' array.  Otherwise, we need to sift out the
            // accepted quotes and put then into a separate array.
            QuoteDetailStruct[] acceptedQuotes;
            if (numAccepted == len)
            {
                acceptedQuotes = quoteDetails;
            }
            else
            {
                // Forward only the accepted quotes to the quote cache.
                acceptedQuotes = new QuoteDetailStruct[numAccepted];
                int n = 0;
                for (int i = 0; i < resultStructs.length; ++i)
                {
                    if (resultStructs[i].quoteResult.errorCode == 0) // not error
                    {
                        acceptedQuotes[n] = quoteDetails[n];
                        n++;
                    }
                }
            }
            quoteCache.addQuotes(acceptedQuotes);
        }

        return resultStructs;
    }

	
	private void checkSystemAvailability(int classKey, String sessionName) throws SystemException{
    	if(!pwManager.isServerDownListEmpty() && pwManager.isProcessDown(classKey, sessionName, false)) {
    		throw ExceptionBuilder.systemException("Cluster Down for classkey:<"+classKey+">", -1);
    	}
	}
    
    private TCSProcessWatcherManager getProcessWatcherManager() {
    	try {
    		return ServicesHelper.getTCSProcessWatcherManagerHome().create();
    	}catch(Exception e) {
    		Log.alarm(this, "Exception while creating TCSProcessWatcherManagerHome");
    	}
    	return null;
	}

}
