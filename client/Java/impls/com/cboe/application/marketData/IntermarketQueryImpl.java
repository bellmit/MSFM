package com.cboe.application.marketData;

import java.util.*;

import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.IntermarketQuery;
import com.cboe.interfaces.application.UserSessionLogoutCollector;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.UserEnablement;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.domain.util.RateMonitorKeyContainer;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.ExceptionBuilder;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessorFactory;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessor;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.idl.cmiIntermarketMessages.CurrentIntermarketStruct;
import com.cboe.idl.cmiIntermarketMessages.AdminStruct;
import com.cboe.idl.cmiIntermarketMessages.BookDepthDetailedStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiErrorCodes.NotAcceptedCodes;
import com.cboe.idl.cmiErrorCodes.AuthenticationCodes;
import com.cboe.idl.constants.OperationTypes;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.exceptions.*;
import com.cboe.interfaces.domain.RateMonitorTypeConstants;
import com.cboe.interfaces.domain.RateMonitor;
import com.cboe.interfaces.domain.RateMonitorHome;

public class IntermarketQueryImpl extends BObject implements IntermarketQuery,
        UserSessionLogoutCollector {

    private SessionManager              sessionManager;
    private RateMonitorHome             rateMonitorHome;

    private Map allSessionConstraints;

    // Event Channel Processors
    private UserSessionLogoutProcessor logoutProcessor;

    protected String userId;
    protected String exchange;
    protected String acronym;

    /**
     * IntermarketQueryImpl constructor comment.
     */
    public IntermarketQueryImpl(Map sessionConstraints)
    {
        super();
        this.allSessionConstraints = sessionConstraints;
    }

    public void create( String name )
    {
        super.create(name);
    }

    /**
     * Initialization called by the home.  Allows any exceptions
     * thrown during object creation to be returned to the home.
     *
     * @exception Exception
     */
    public void initialize() throws Exception {

    }

    /**
     * sets the session manager
     *
     * @param session SessionManager
     */
    public void setSessionManager(SessionManager session)
    {
        sessionManager = session;

        logoutProcessor = UserSessionLogoutProcessorFactory.create(this);
        EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, session);
        LogoutServiceFactory.find().addLogoutListener(session, this);

        try {
            this.userId = sessionManager.getValidSessionProfileUser().userId;
            this.exchange = sessionManager.getValidSessionProfileUserV2().userInfo.userAcronym.exchange;
            this.acronym = sessionManager.getValidSessionProfileUserV2().userInfo.userAcronym.acronym;
        }
        catch (Exception e)
        {
            Log.exception(this, "session : " + sessionManager, e);
        }
    }
    public void acceptUserSessionLogout()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptUserSessionLogout for " + sessionManager);
        }
        EventChannelAdapterFactory.find().removeListenerGroup(this);
        EventChannelAdapterFactory.find().removeChannel(this);
        LogoutServiceFactory.find().logoutComplete(sessionManager,this);

        logoutProcessor.setParent(null);
        logoutProcessor = null;
        sessionManager = null;
    }

    public CurrentIntermarketStruct[] getIntermarketByClassForSession(int classKey, String sessionName)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling getIntermarketByClassForSession: " + classKey + " " + sessionName);
        }

        checkCallingRate(sessionName, "getIntermarketByClassForSession", RateMonitorTypeConstants.QUERY_INTERMARKET);

        getUserEnablementService().verifyUserEnablement(sessionName, classKey, OperationTypes.INTERMARKETQUERY);

        CurrentIntermarketStruct[] currentIntermarket;
        currentIntermarket = ServicesHelper.getIntermarketDataService().getIntermarketForClass(classKey, sessionName);
        return currentIntermarket;

    }

    public AdminStruct[] getAdminMessage(String sessionName, int productKey, int adminMessageKey, String sourceExchange)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling getAdminMessage " + productKey + " " + sessionName + " " + adminMessageKey);
        }
        checkCallingRate(sessionName, "getAdminMessage", RateMonitorTypeConstants.QUERY_INTERMARKET);

        AdminStruct[] adminMessage;
        adminMessage = ServicesHelper.getIntermarketControlService().getAdminMessages(sessionName, productKey, adminMessageKey, sourceExchange, userId);
        return adminMessage;
    }

    public CurrentIntermarketStruct getIntermarketByProductForSession(int productKey, String sessionName)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling getIntermarketForProduct " + productKey + " " + sessionName);
        }
        checkCallingRate(sessionName, "getIntermarketByProductForSession", RateMonitorTypeConstants.QUERY_INTERMARKET);
        verifyUserEnablementByProductKey(sessionName, productKey);
        CurrentIntermarketStruct currentIntermarket;

        currentIntermarket = ServicesHelper.getIntermarketDataService().getIntermarketForProduct(productKey, sessionName);
        return currentIntermarket;

    }

    public BookDepthDetailedStruct getDetailedOrderBook(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "getDetailedOrderBook " + productKey + " " + sessionName);
        }
        checkCallingRate(sessionName, "getDeatiledOrderBook", RateMonitorTypeConstants.BOOK_DEPTH_INTERMARKET);

        verifyUserEnablementByProductKey(sessionName, productKey);
        BookDepthDetailedStruct bookDepthDetail;

        bookDepthDetail = ServicesHelper.getNBBOAgentService().getDetailedOrderBook(sessionName, productKey,this.userId);

        return bookDepthDetail;

    }

    public short getOrderBookStatus(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException
    {
        checkCallingRate(sessionName, "getOrderBookStatus", RateMonitorTypeConstants.BOOK_DEPTH_INTERMARKET);
        verifyUserEnablementByProductKey(sessionName, productKey);
        return ServicesHelper.getNBBOAgentService().getOrderBookStatus(sessionName, productKey, userId);
    }

    public BookDepthDetailedStruct showMarketableOrderBookAtPrice(String sessionName, int productKey, PriceStruct priceStruct)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException
    {
        checkCallingRate(sessionName, "getOrderBookStatus", RateMonitorTypeConstants.BOOK_DEPTH_INTERMARKET);
        verifyUserEnablementByProductKey(sessionName, productKey);
        throw ExceptionBuilder.authorizationException("showMarketableOrderBookAtPrice NOT IMPLEMENTED!", AuthenticationCodes.FUNCTION_NOT_IMPLEMENTED);
    }

    private UserEnablement getUserEnablementService()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
         return ServicesHelper.getUserEnablementService(userId, exchange, acronym);
    }

    private RateMonitorHome getRateMonitorHome()
    {
        if (rateMonitorHome == null )
        {
           try {
                rateMonitorHome = (RateMonitorHome)HomeFactory.getInstance().findHome(RateMonitorHome.HOME_NAME);
            }
            catch (CBOELoggableException e) {
                Log.exception(this, "session : " + sessionManager, e);
                throw new NullPointerException("Could not find RateMonitor Home");
            }
        }

        return rateMonitorHome;
    }

    private void verifyUserEnablementByProductKey(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        try
        {
           ProductStruct product = sessionManager.getProductQuery().getProductByKey(productKey);
           int classKey = product.productKeys.classKey;

           getUserEnablementService().verifyUserEnablement(sessionName, classKey, OperationTypes.INTERMARKETQUERY);
        }
        catch( NotFoundException e )
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }

    }
    private void checkCallingRate(String sessionName, String methodName, short rateMonitorType) throws NotAcceptedException
    {
        Map sessionConstraints = (Map)allSessionConstraints.get(sessionName);

        int windowSize = 0;
        long windowMilliSecondPeriod = 0;
        if(sessionConstraints != null)
        {
            Object constraint = sessionConstraints.get(IntermarketQueryHomeImpl.CALL_WINDOW_INTERVAL_PROPERTY_NAME);
            if(constraint != null)
            {
                windowMilliSecondPeriod = ((Long)constraint).longValue();
            }
            constraint = sessionConstraints.get(IntermarketQueryHomeImpl.CALL_WINDOW_SIZE_PROPERTY_NAME);
            if(constraint != null)
            {
                windowSize = ((Integer)constraint).intValue();
            }
        }

        if(windowSize > 0 && windowMilliSecondPeriod > 0)
        {
            RateMonitorKeyContainer rateMonitorKeyForCalls =
                    new RateMonitorKeyContainer(userId, exchange, acronym, sessionName, rateMonitorType );
            RateMonitor rateMonitorForCalls =
                    getRateMonitorHome().find(rateMonitorKeyForCalls, windowSize, windowMilliSecondPeriod);
            if(rateMonitorForCalls.canAccept(System.currentTimeMillis()) == false)
            {
                StringBuilder msg = new StringBuilder(120);
                msg.append(methodName).append(" rejected. Call limit exceeded for ").append(sessionName);
                msg.append('.');
                msg.append(" Rate:").append(rateMonitorForCalls.getWindowSize()).append(", Within:").append(rateMonitorForCalls.getWindowMilliSecondPeriod());
                msg.append("millis.");
                throw ExceptionBuilder.notAcceptedException(msg.toString(),
                                                            NotAcceptedCodes.RATE_EXCEEDED);
            }
        }

    }

}
