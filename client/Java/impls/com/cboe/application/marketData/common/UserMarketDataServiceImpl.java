package com.cboe.application.marketData.common;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.domain.util.RateMonitorKeyContainer;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiErrorCodes.NotAcceptedCodes;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.constants.OperationTypes;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.UserEnablement;
import com.cboe.interfaces.application.UserMarketDataService;
import com.cboe.interfaces.businessServices.OrderBookService;
import com.cboe.interfaces.businessServices.MarketDataService;
import com.cboe.interfaces.domain.RateMonitor;
import com.cboe.interfaces.domain.RateMonitorHome;
import com.cboe.interfaces.domain.RateMonitorTypeConstants;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ExceptionBuilder;
import org.omg.CORBA.UserException;

import java.util.HashMap;
import java.util.Map;

/**
 * This class implements UserMarketDataService which contains the common user marketData service that is shared by cmi
 * and non cmi users. It contains the userEnablement, rateMonitor checking of each call and the calls to the server services.
 * @author Jing Chen
 */
public class UserMarketDataServiceImpl extends BObject implements UserMarketDataService
{
    protected BaseSessionManager sessionManager;
    protected OrderBookService orderBookService;
    protected MarketDataService marketDataService;
    protected String thisUserId;
    protected String thisExchange;
    protected String thisAcronym;
    protected Map allSessionConstraints;
    protected UserEnablement userEnablement;
    protected RateMonitorHome rateMonitorHome;
    protected HashMap listeners;

    public UserMarketDataServiceImpl(BaseSessionManager session, Map sessionConstraints)
    {
        sessionManager = session;
        allSessionConstraints = sessionConstraints;
        listeners = new HashMap(11);
        try
        {
            thisUserId = sessionManager.getUserId();
            thisExchange = sessionManager.getValidSessionProfileUserV2().userInfo.userAcronym.exchange;
            thisAcronym = sessionManager.getValidSessionProfileUserV2().userInfo.userAcronym.acronym;
        }
        catch(UserException e)
        {
            Log.exception(this, "fatal error in getting userId from session:"+sessionManager, e);
        }
    }

    public BookDepthStruct getBookDepth(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException
    {
        getUserEnablementService().verifyUserEnablementForProduct(sessionName, productKey, OperationTypes.MARKETQUERY_BOOKDEPTH);
        monitorBookDepthCallRate(sessionName);
        BookDepthStruct bookDepth = getOrderBookService().getBookDepth(sessionName, productKey, false);
        return bookDepth;
    }

    public BookDepthStructV2 getBookDepthDetails(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling getBookDepthDetails for sessionName" + sessionName + "productKey:"+productKey);
        }
        getUserEnablementService().verifyUserEnablementForProduct(sessionName, productKey, OperationTypes.MARKETQUERY_BOOKDEPTH);
        monitorBookDepthCallRate(sessionName);
        BookDepthStructV2 bookDepth = getOrderBookService().getBookDepthV2(sessionName, productKey, false);
        return bookDepth;
    }

    public MarketDataHistoryStruct getMarketDataHistoryByTime(String sessionName, int productKey, DateTimeStruct startTime, short direction)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling getMarketDataHistoryByTime for " + sessionManager);
        }
        getUserEnablementService().verifyUserEnablementForProduct(sessionName, productKey, OperationTypes.MARKETQUERY_MARKETDATAHISTORY);
        // Create a unique name for the query session - user name with hash
        // code of session manager should be unique.
        StringBuilder qsid = new StringBuilder(thisUserId.length()+12);
        qsid.append(thisUserId ).append( ':' ).append( sessionManager.hashCode());
        String querySessionId = qsid.toString();
        MarketDataHistoryStruct marketDataHistory = getMarketDataService().getProductByTime(querySessionId, sessionName, productKey, startTime, direction);
        return marketDataHistory;
    }

    public MarketDataHistoryDetailStruct getDetailMarketDataHistoryByTime(String sessionName, int productKey, DateTimeStruct startTime, short direction)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling getMarketDataHistoryByTime for " + sessionManager);
        }
        getUserEnablementService().verifyUserEnablementForProduct(sessionName, productKey, OperationTypes.MARKETQUERY_DETAILMDHISTORY);
        StringBuilder qsid = new StringBuilder(thisUserId.length()+12);
        qsid.append(thisUserId).append(':').append(sessionManager.hashCode());
        String querySessionId = qsid.toString();
        MarketDataHistoryDetailStruct marketDataHistory = getMarketDataService().getDetailProductHistoryByTime(querySessionId, sessionName, productKey, startTime, direction);
        return marketDataHistory;
    }
    public MarketDataHistoryDetailStruct getPriorityMarketDataHistoryByTime(String sessionName, int productKey, DateTimeStruct startTime, short direction)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling getMarketDataHistoryByTime for " + sessionManager);
        }
        getUserEnablementService().verifyUserEnablementForProduct(sessionName, productKey, OperationTypes.MARKETQUERY_PRIORITYMDHISTORY);
        StringBuilder qsid = new StringBuilder(thisUserId.length()+12);
        qsid.append(thisUserId).append(':').append(sessionManager.hashCode());
        String querySessionId = qsid.toString();
        MarketDataHistoryDetailStruct marketDataHistory = getMarketDataService().getPriorityProductHistoryByTime(querySessionId, sessionName, productKey, startTime, direction);
        return marketDataHistory;
    }

    public CurrentMarketStruct[] getCurrentMarketsForClass(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        getUserEnablementService().verifyUserEnablement(sessionName, classKey, OperationTypes.MARKETQUERY_CURRENTMARKET);
        return getMarketDataService().getCurrentMarketForClass(sessionName, classKey);
    }

    public CurrentMarketStruct getCurrentMarketForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        CurrentMarketStruct currentMarketStruct = null;
        try
        {
            currentMarketStruct = getMarketDataService().getCurrentMarketForProduct(sessionName, productKey);
        }
        catch(NotFoundException e)
        {
            Log.exception(this, e);
        }
        return currentMarketStruct;
    }

    public NBBOStruct[] getNBBOsForClass(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        return getMarketDataService().getNBBOForClass(sessionName, classKey);
    }

    public NBBOStruct getNBBOForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        NBBOStruct nbbo = null;
        try
        {
            nbbo = getMarketDataService().getNBBOForProduct(sessionName, productKey);
        }
        catch(NotFoundException e)
        {
            Log.exception(this, e);
        }
        return nbbo;
    }

    public RecapStruct[] getRecapsForClass(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        return getMarketDataService().getRecapForClass(sessionName, classKey).productRecaps;
    }

    public RecapStruct getRecapForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        RecapStruct recap = null;
        try
        {
            recap = getMarketDataService().getRecapForProduct(sessionName, productKey);
        }
        catch(NotFoundException e)
        {
            Log.exception(this, e);
        }
        return recap;
    }

    public BookDepthStruct[] getBookDepthsForClass(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        BookDepthStruct[] bookDepths = null;
        try
        {
            bookDepths = getOrderBookService().getBookDepthByClass(sessionName, classKey, true);
        }
        catch (NotFoundException e)
        {
            Log.exception(this, e);
        }
        return bookDepths;
    }

    public BookDepthStruct getBookDepthForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        BookDepthStruct bookDepth = null;
        try
        {
            bookDepth = getOrderBookService().getBookDepth(sessionName, productKey, true);
        }
        catch (NotFoundException e)
        {
            Log.exception(this, e);
        }
        return bookDepth;
    }

    public void verifyUserNBBOEnablement(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        getUserEnablementService().verifyUserEnablement(sessionName, classKey, OperationTypes.MARKETQUERY_NBBO);
    }
    public void verifyUserExpectdOpeningPriceEnablement(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        getUserEnablementService().verifyUserEnablement(sessionName, classKey, OperationTypes.MARKETQUERY_EXPECTEDOPENINGPRICE);
    }
    public void verifyUserRecapEnablement(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        getUserEnablementService().verifyUserEnablement(sessionName, classKey, OperationTypes.MARKETQUERY_RECAP);
    }
    public void verifyUserTickerEnablement(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        getUserEnablementService().verifyUserEnablement(sessionName, classKey, OperationTypes.MARKETQUERY_TICKER);
    }
    public void verifyUserBookDepthEnablement(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        getUserEnablementService().verifyUserEnablement(sessionName, classKey, OperationTypes.MARKETQUERY_BOOKDEPTH);
    }

    public void verifyUserCurrentMarketEnablement(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        getUserEnablementService().verifyUserEnablement(sessionName, classKey, OperationTypes.MARKETQUERY_CURRENTMARKET);
    }

    protected UserEnablement getUserEnablementService()
    {
        if(userEnablement == null)
        {
            userEnablement = ServicesHelper.getUserEnablementService(thisUserId, thisExchange, thisAcronym);
        }
        return userEnablement;
    }

    protected RateMonitorHome getRateMonitorHome()
    {
        if (rateMonitorHome == null)
        {
            try
            {
                rateMonitorHome = (RateMonitorHome) HomeFactory.getInstance().findHome(RateMonitorHome.HOME_NAME);
            } catch (CBOELoggableException e)
            {
                Log.exception(this, "session : " + sessionManager, e);
                // a really ugly way to get around the missing exception in the interface...
                throw new NullPointerException("Could not find RateMonitor Home");
            }
        }

        return rateMonitorHome;
    }

    protected OrderBookService getOrderBookService()
    {
        if(orderBookService == null)
        {
            orderBookService = ServicesHelper.getOrderBookService();
        }
        return orderBookService;
    }

    protected MarketDataService getMarketDataService()
    {
        if(marketDataService == null)
        {
            marketDataService = ServicesHelper.getMarketDataService();
        }
        return marketDataService;
    }

    protected void monitorBookDepthCallRate(String sessionName) throws NotAcceptedException
    {
        Map sessionConstraints = (Map)allSessionConstraints.get(sessionName);

        int windowSize = 0;
        long windowMilliSecondPeriod = 0;
        if(sessionConstraints != null)
        {
            Object constraint = sessionConstraints.get(UserMarketDataServiceHomeImpl.BOOK_DEPTH_CALL_WINDOW_INTERVAL_PROPERTY_NAME);
            if(constraint != null)
            {
                windowMilliSecondPeriod = ((Long)constraint).longValue();
            }
            constraint = sessionConstraints.get(UserMarketDataServiceHomeImpl.BOOK_DEPTH_CALL_WINDOW_SIZE_PROPERTY_NAME);
            if(constraint != null)
            {
                windowSize = ((Integer)constraint).intValue();
            }
        }

        if(windowSize > 0 && windowMilliSecondPeriod > 0)
        {
            RateMonitorKeyContainer rateMonitorKey =
                    new RateMonitorKeyContainer(thisUserId, thisExchange, thisAcronym, sessionName, RateMonitorTypeConstants.GET_BOOK_DEPTH);
            RateMonitor rateMonitor = getRateMonitorHome().find(rateMonitorKey, windowSize, windowMilliSecondPeriod);
            if (rateMonitor.canAccept(System.currentTimeMillis()) == false)
            {
                StringBuilder msg = new StringBuilder(100);
                msg.append("getBookDepth rejected. Call limit exceeded for ").append(sessionName).append('.');
                msg.append(" Rate:").append(rateMonitor.getWindowSize()).append(", Within:").append(rateMonitor.getWindowMilliSecondPeriod());
                msg.append("millis.");
                throw ExceptionBuilder.notAcceptedException(msg.toString(),
                                                            NotAcceptedCodes.RATE_EXCEEDED);
            }
        }
    }
}
