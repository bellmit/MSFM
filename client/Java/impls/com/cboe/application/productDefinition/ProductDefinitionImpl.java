// $Workfile$ com.cboe.application.productDefinition.ProductDefinitionImpl.java
// $Revision$
// Last Modification on:  09/07/1999 03:20:14 fengc
/* $Log$
*   Initial Version                         03/15/1999      fengc
*   Increment 4                             09/07/1999      fengc
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.

package com.cboe.application.productDefinition;

import com.cboe.application.cache.CacheFactory;
import com.cboe.application.product.cache.ProductCacheKeyFactory;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessor;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessorFactory;
import com.cboe.application.tradingSession.cache.TradingSessionCacheKeyFactory;
import com.cboe.client.util.StrategyLegsWrapper;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.domain.startup.ClientRoutingBOHome;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiSession.SessionStrategyLegStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiStrategy.StrategyLegStruct;
import com.cboe.idl.cmiStrategy.StrategyRequestStruct;
import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.constants.OperationTypes;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.ProductDefinition;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.UserEnablement;
import com.cboe.interfaces.application.UserSessionLogoutCollector;
import com.cboe.interfaces.businessServices.ProductQueryService;
import com.cboe.interfaces.businessServices.TradingSessionService;
import com.cboe.interfaces.internalBusinessServices.ProductRoutingServiceHome;
import com.cboe.interfaces.internalBusinessServices.ProductRoutingService;
import com.cboe.util.event.EventChannelAdapterFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
* <b>Description</b>
* <p>
*    Implemetation of the Product Query interface
* </p>
*/
public class ProductDefinitionImpl extends BObject
                                     implements ProductDefinition, UserSessionLogoutCollector
{
    private TradingSessionService   tradingSessionService;
    private ProductQueryService     productQueryService;

    private SessionManager          currentSession;
    private UserSessionLogoutProcessor logoutProcessor;

    private static AtomicInteger theTotalStrategyCallsCount = new AtomicInteger(0);
    private static AtomicInteger theGlobalStrategyCallCount = new AtomicInteger(0);

    /**
     * ProductDefinitionImpl constructor.
     */
    public ProductDefinitionImpl()
    {
        super();
        // init the product query service
        getTradingSessionService();
    }// end of constructor

    protected void setSessionManager(SessionManager session)
    {
        currentSession = session;
        logoutProcessor = UserSessionLogoutProcessorFactory.create(this);
        EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, session);
        LogoutServiceFactory.find().addLogoutListener(session, this);
    }
    /**
     * Returns reference to product query service.
     * The Home should return the ProductQueryServiceCacheProxy.
     *
     * @author Connie Feng
     */
    private TradingSessionService getTradingSessionService()
    {
        if (tradingSessionService == null )
        {
            tradingSessionService = ServicesHelper.getTradingSessionService();
        }

        return tradingSessionService;
    }// end of getQueryService

    /**
     * Returns reference to product query service.
     * The Home should return the ProductQueryService.
     *
     * @author Keval Desai
     */
    private ProductQueryService getProductQueryService()
    {
        if (productQueryService == null )
        {
            productQueryService  = ServicesHelper.getProductQueryService ();
        }

        return productQueryService;
    }// end of getQueryService

    /////////////// IDL exported methods ////////////////////////////////////
    /**
    * @description
    * Request a standard trading strategy
    *
    * @usage
    * You are required to select a standard strategy
    *
    *
    * @returns product structure for the standard strategy
    * exception NotFoundException
    * exception InvalidProductException
    * exception CreateFailedException
    */
    public SessionStrategyStruct acceptStrategy(String sessionName, StrategyRequestStruct strategyRequest)
       throws SystemException, CommunicationException, AuthorizationException,DataValidationException
    {
        theTotalStrategyCallsCount.incrementAndGet();
        //verify if it is enabled
        getUserEnablementService().verifyUserEnablementForSession(sessionName, OperationTypes.PRODUCTDEFINITION);
        SessionStrategyStruct strategyStruct = null;
        StringBuilder msg = new StringBuilder(300);
        msg.append("calling acceptStrategy Cache lookup time:"); 
        long x= System.nanoTime();
        StrategyLegsWrapper key =null;
        try{
            key =new StrategyLegsWrapper(strategyRequest.strategyLegs);
            // The StrategyLegsWrapper will also modify the incoming side on the user request from H and X to S.
             // The requests to the server will also be modified. 
            // We will not lookup our cache if the sides on the strategy are anything other than B,S, H or X
            if (key.isValidStrategy()){
                strategyStruct = (SessionStrategyStruct) CacheFactory.getSessionStrategyCache(sessionName).
                                                        find(TradingSessionCacheKeyFactory.getStrategyKeyByLegs(), key);
            }else
                strategyStruct = null;
        }catch(NotFoundException nfe){
            Log.exception(this,nfe);
        }catch(DataValidationException dve){
            Log.exception(this,dve);
        }
        long y= System.nanoTime();
        msg.append(y-x);

        if(strategyStruct==null){ //go to global call
            strategyStruct = getTradingSessionService().acceptStrategy(sessionName, strategyRequest);
            theGlobalStrategyCallCount.incrementAndGet();
            x= System.nanoTime();
            msg.append(" Global lookup time:").append(x-y);
            CacheFactory.updateSessionStrategyCache(sessionName, strategyStruct);
            StrategyStruct strategy = new StrategyStruct();
            strategy.product= strategyStruct.sessionProductStruct.productStruct;
            SessionStrategyLegStruct[] sessionStrategyLegs = strategyStruct.sessionStrategyLegs;
            strategy.strategyLegs = new StrategyLegStruct[sessionStrategyLegs.length];
            for(int i=0;i<sessionStrategyLegs.length;i++)
            { 
                SessionStrategyLegStruct temp=   sessionStrategyLegs[i];
                strategy.strategyLegs[i]= new StrategyLegStruct(temp.product,temp.ratioQuantity,temp.side);
            }
            CacheFactory.updateStrategyCache(strategy);
            msg.append(" Cache update time:").append(System.nanoTime()-x);
            try{
                if (!ClientRoutingBOHome.clientIsRemote()){
                    ProductRoutingService routingService = ServicesHelper.getProductRoutingService();
                    routingService.saveStrategyProduct(strategyStruct);
                }
            }
            catch(NullPointerException e)
            {
                Log.exception(e);
            }
        }
        msg.append(" nanos ");
        msg.append("SK:").append(key.hashCode());
        msg.append(" ProductKey:").append(strategyStruct.sessionProductStruct.productStruct.productKeys.productKey);
        Log.information(this,msg.toString());
        return strategyStruct;
    }

    public static int getGlobalStrategyCallCount(){
        return theGlobalStrategyCallCount.get();
    }

    public static int getTotalStrategyCallCount(){
        return theTotalStrategyCallsCount.get();
    }

    /**
     * Build the Strategy Request struct
     *
     * @returns com.cboe.idl.cmiStrategy.StrategyRequestStruct
     * @param   strategyType (out of 8 different types)
     * @param   anchorProduct
     * @param   priceIncrement
     * @param   monthIncrement
     * @throws SystemException
     * @throws CommunicationException
     * @throws DataValidationException
     * @throws AuthorizationException
     */
     public StrategyRequestStruct buildStrategyRequestByName(short strategyType, ProductNameStruct anchorProduct, PriceStruct priceIncrement, short monthIncrement)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling buildStrategyRequestByName for " + currentSession);
        }
        return getProductQueryService().buildStrategyRequestByName(strategyType, anchorProduct, priceIncrement, monthIncrement);
    }

    /**
     * Build the Strategy Request struct
     *
     * @returns com.cboe.idl.cmiStrategy.StrategyRequestStruct
     * @param   strategyType (out of 8 different types)
     * @param   anchorProductKey
     * @param   priceIncrement
     * @param   monthIncrement
     * @throws SystemException
     * @throws CommunicationException
     * @throws DataValidationException
     * @throws AuthorizationException
     */
     public StrategyRequestStruct buildStrategyRequestByProductKey(short strategyType, int anchorProductKey, PriceStruct priceIncrement, short monthIncrement)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling buildStrategyRequestByProductKey for " + currentSession);
        }
        return getProductQueryService().buildStrategyRequestByProductKey(strategyType, anchorProductKey, priceIncrement, monthIncrement);
    }

    public void acceptUserSessionLogout() {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptUserSessionLogout for " + currentSession);
        }
        // Do any individual service clean up needed for logout
        EventChannelAdapterFactory.find().removeListenerGroup(this);
        LogoutServiceFactory.find().logoutComplete(currentSession,this);
        logoutProcessor.setParent(null);
        logoutProcessor = null;

        tradingSessionService = null;
        currentSession = null;

    }

    private UserEnablement getUserEnablementService()
    throws SystemException, CommunicationException, AuthorizationException
    {
       return ServicesHelper.getUserEnablementService(currentSession.getValidSessionProfileUser().userId
                                                      , currentSession.getValidSessionProfileUser().userAcronym.exchange
                                                      , currentSession.getValidSessionProfileUser().userAcronym.acronym);
    }

}// EOF
