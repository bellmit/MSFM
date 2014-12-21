package com.cboe.proxy.businessServicesClient;

import com.cboe.domain.util.TradingSessionNameHelper;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.businessServices.MarketDataService;
import com.cboe.idl.cmiErrorCodes.CommunicationFailureCodes;
import com.cboe.idl.cmiMarketData.ClassRecapStructV5;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.MarketDataHistoryDetailStruct;
import com.cboe.idl.cmiMarketData.MarketDataHistoryStruct;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.cmiMarketData.ProductAndUnderlyingRecapStruct;
import com.cboe.idl.cmiMarketData.RecapStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.legMarketData.LegMarketDetailStruct;
import com.cboe.idl.legMarketData.LegMarketDetailStructV2;
import com.cboe.idl.marketData.ClassRecapStruct;
import com.cboe.idl.marketData.ClosingQuoteSummaryStruct;
import com.cboe.idl.marketData.CmiManualPriceReportEntryStruct;
import com.cboe.idl.marketData.InternalCurrentMarketStruct;
import com.cboe.idl.marketData.ManualPriceReportEntryStruct;
import com.cboe.idl.marketData.QuoteQueryStruct;
import com.cboe.idl.marketData.QuoteQueryV2Struct;
import com.cboe.idl.marketData.QuoteQueryV3Struct;
import com.cboe.idl.product.ClassSettlementStructV4;
import com.cboe.idl.product.ClassSettlementStruct;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.utilities.RouteNameHelper;
import com.cboe.infrastructureServices.traderService.DirectoryQueryResult;
import com.cboe.util.ExceptionBuilder;

/**
 * MarketDataServiceRoutingProxy is a routing proxy that delgates the incoming requests to the
 * appropriate market data service. The class maintains a table which maps every service to its
 * respective process ( route ).
 *
 * @date December 31, 2008
 *
 */

public class MarketDataServiceClientRoutingProxy extends NonGlobalServiceClientRoutingProxy 
    implements com.cboe.interfaces.businessServices.MarketDataService
{
         
    // Global MDS for underlying recap query
    private MarketDataService globalMDS = null;
    
    // Weather to use the regular routing proxy route map or use Global MDS
    private volatile boolean useRegularMDSRoutingProxy = false;
        
    /**
     * Default constructor - there must always be a 'default constructor', implicit or explicit, for
     * Class.newInstance() to work.
     */
    public MarketDataServiceClientRoutingProxy()
    {
    }
    
    public void initialize()
    {
        super.initialize();

        StringBuilder sb = new StringBuilder(100);
        String constraint = sb.append("routename == ").append(RouteNameHelper.getRemoteRouteName()).toString();
        DirectoryQueryResult[] svcList;
        svcList = getBusinessServicesFor(getServiceTypeName(), constraint);
        
        if (svcList == null)
        {
            Log.alarm(this, "NO Global MDS is available");
            return;
        }
            
        if (svcList.length == 0)
        {
            Log.alarm(this, "NO Global MDS is available.");
            return;
        }
        
        if (svcList.length > 1)
        {
            Log.alarm(this, "Found more than one Global MDS, will use the first one.");
        }
            
        org.omg.CORBA.Object serviceReference = narrow(svcList[0].getObjectReference());
        globalMDS = (MarketDataService) serviceReference;
            
        if (globalMDS == null)
        {
            Log.alarm(this, "Global MDS is null.");
        }
        else
        {
            sb.setLength(0);
            sb.append("Found Global MDS : ").append(globalMDS);
            Log.information(this, sb.toString());
        }
            
        // Now get the value of useRegularMDSRoutingProxy
        String usingRegularMDS = System.getProperty("useRegularMDSRoutingProxy");
               
        if (usingRegularMDS != null)
        {
            if (usingRegularMDS.equalsIgnoreCase("true"))
            {
                useRegularMDSRoutingProxy = true;
            }
        }
        // Default value of useRegularMDSRoutingPRoxy will be false.
            
        sb.setLength(0);
        sb.append("Value of useRegularMDSRoutingProxy for underlyingRecap is : ").append(useRegularMDSRoutingProxy);
        Log.information(this, sb.toString());

        // Register command callback so that value of useRegularMDSRoutingProxy can be changed run
        // time
        try
        {
            getBOHome().registerCommand(this, // Callback object
                    "adminUseRegularMDSRoutingProxy", // External Name
                    "useRegularMDSRoutingProxyCallback", // Method Name
                    "Whether to use regular MDS routing proxy or global proxy", // Desc
                    new String[] { String.class.getName() }, new String[] { "true | false" });
        }
        catch (CBOELoggableException e)
        {
            Log.exception(this, "Could not register command callback ", e);
        }

    }

    public String useRegularMDSRoutingProxyCallback(String value)
    {
        boolean oldUseRegularMDSRoutingProxy = useRegularMDSRoutingProxy;
        String returnValue = "";
        
        if (value != null)
        {
            if (value.equalsIgnoreCase("true"))
            {
                useRegularMDSRoutingProxy = true;
                StringBuilder sb = new StringBuilder(90);
                sb.append("Successfully updated useRegularMDSRoutingProxy for underlyingRecap, FROM:")
                  .append(oldUseRegularMDSRoutingProxy)
                  .append("TO :").append(useRegularMDSRoutingProxy);
                returnValue = sb.toString();
            }
            else if (value.equalsIgnoreCase("false"))
            {
                useRegularMDSRoutingProxy = false;
                StringBuilder sb = new StringBuilder(90);
                sb.append("Successfully updated useRegularMDSRoutingProxy for underlyingRecap, FROM:")
                  .append(oldUseRegularMDSRoutingProxy)
                  .append("TO :").append(useRegularMDSRoutingProxy);
                returnValue = sb.toString();
            }
            else
            {
                returnValue = "Illegal argument, useRegularMDSRoutingProxy is NOT changed.";
            }
        }
        else
        {
            returnValue = "Illegal argument, useRegularMDSRoutingProxy is NOT changed.";
        }

        Log.information(this, returnValue);

        return returnValue;
    }

    /**
     *  Forwards request to delegate based on classkey
     *  @ param String sessionName @ param int classKey @ return CurrentMarketStruct[]
     */
    public CurrentMarketStruct[] getCurrentMarketForClass(String sessionName, int classKey)
            throws SystemException, CommunicationException, DataValidationException,
            AuthorizationException
    {
        if (TradingSessionNameHelper.isUnderlyingSession(sessionName) && !useRegularMDSRoutingProxy)
        {
            return globalMDS.getCurrentMarketForClass(sessionName, classKey);
        }
        else if (TradingSessionNameHelper.isNotApplicableSession(sessionName))
        {
            throw ExceptionBuilder.dataValidationException("No current market data is available.",
                    0);
        }
        else
        {
            MarketDataService service = (MarketDataService) getServiceByClass(sessionName, classKey);
            return service.getCurrentMarketForClass(sessionName, classKey);
        }
    }

    /**
     * Forwards request to delegate based on classkey
     *  @ param String sessionName @ param int classKey @ return CurrentMarketStruct[]
     */
    public InternalCurrentMarketStruct[] getCurrentMarketForClassV3(String sessionName, int classKey)
            throws SystemException, CommunicationException, DataValidationException,
            AuthorizationException
    {
        if (TradingSessionNameHelper.isUnderlyingSession(sessionName) && !useRegularMDSRoutingProxy)
        {
            return globalMDS.getCurrentMarketForClassV3(sessionName, classKey);
        }
        else if (TradingSessionNameHelper.isNotApplicableSession(sessionName))
        {
            throw ExceptionBuilder.dataValidationException("No current market data is available.", 0);
        }
        else
        {
            MarketDataService service = (MarketDataService) getServiceByClass(sessionName, classKey);
            return service.getCurrentMarketForClassV3(sessionName, classKey);
        }
    }

    /**
     * Forwards request to delegate based on productKey
     *  @ param String sessionName @ param String productKey @ return CurrentMarketStruct
     */
    public CurrentMarketStruct getCurrentMarketForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, DataValidationException,
            AuthorizationException, NotFoundException
    {
        if (TradingSessionNameHelper.isUnderlyingSession(sessionName) && !useRegularMDSRoutingProxy)
        {
            return globalMDS.getCurrentMarketForProduct(sessionName, productKey);
        }
        else if (TradingSessionNameHelper.isNotApplicableSession(sessionName))
        {
            throw ExceptionBuilder.dataValidationException("No current market data is available.", 0);
        }
        else
        {
            MarketDataService service = (MarketDataService) getServiceByProduct(sessionName, productKey);
            return service.getCurrentMarketForProduct(sessionName, productKey);
        }
    }

    /**
     * Forwards request to delegate based on productKey
     *  @ param String sessionName @ param String productKey @ return CurrentMarketStruct
     */
    public InternalCurrentMarketStruct getCurrentMarketForProductV3(String sessionName,
            int productKey) throws SystemException, CommunicationException,
            DataValidationException, AuthorizationException, NotFoundException
    {
        if (TradingSessionNameHelper.isUnderlyingSession(sessionName) && !useRegularMDSRoutingProxy)
        {
            return globalMDS.getCurrentMarketForProductV3(sessionName, productKey);
        }
        else if (TradingSessionNameHelper.isNotApplicableSession(sessionName))
        {
            throw ExceptionBuilder.dataValidationException("No current market data is available.", 0);
        }
        else
        {
            MarketDataService service = (MarketDataService) getServiceByProduct(sessionName, productKey);
            return service.getCurrentMarketForProductV3(sessionName, productKey);
        }
    }    
    
        /**
         *  Forwards request to delegate based on classKey and sessionName
     *  @ param String sessionName @ param String classKey @ return NBBOStruct[]
     */
    public NBBOStruct[] getNBBOForClass(String sessionName, int classKey) throws SystemException,
            CommunicationException, DataValidationException, AuthorizationException
    {
        if (TradingSessionNameHelper.isUnderlyingSession(sessionName) && !useRegularMDSRoutingProxy)
        {
            return globalMDS.getNBBOForClass(sessionName, classKey);
        }
        else if (TradingSessionNameHelper.isNotApplicableSession(sessionName))
        {
            throw ExceptionBuilder.dataValidationException("No NBBO data is available.", 0);
        }
        else
        {
            MarketDataService service = (MarketDataService) getServiceByClass(sessionName, classKey);
            return service.getNBBOForClass(sessionName, classKey);
        }
    }

        /**
         *  Forwards request to delegate based on productkey and sesssionName
         *
         * @param sessionName
         * @param productKey
         * @return NBBOStruct
     */
    public NBBOStruct getNBBOForProduct(String sessionName, int productKey) throws SystemException,
            CommunicationException, DataValidationException, AuthorizationException,
            NotFoundException
    {
        try
        {
            if (TradingSessionNameHelper.isUnderlyingSession(sessionName) && !useRegularMDSRoutingProxy)
                {
                    return globalMDS.getNBBOForProduct(sessionName, productKey);
                }
            else if (TradingSessionNameHelper.isNotApplicableSession(sessionName))
                {
                    throw ExceptionBuilder.dataValidationException("No NBBO data is available.", 0);
                }
                else
                {
                    MarketDataService service = (MarketDataService) getServiceByProduct(sessionName, productKey);
                    return service.getNBBOForProduct(sessionName, productKey);
                }
        }
        catch(org.omg.CORBA.COMM_FAILURE cf)
        {
            throw ExceptionBuilder.communicationException("COMM_FAILURE: Failed get NBBO For Product:", CommunicationFailureCodes.SERVER_NOT_AVAILABLE);
        }
        catch(org.omg.CORBA.TRANSIENT trans)
        {
            throw ExceptionBuilder.communicationException("TRANSIENT: Failed get NBBO For Product", CommunicationFailureCodes.SERVER_NOT_AVAILABLE);
        }
        catch(org.omg.CORBA.OBJECT_NOT_EXIST oe)
        {
            throw ExceptionBuilder.communicationException("OBJECT_NOT_EXIST: Failed get NBBO For Product:", CommunicationFailureCodes.SERVER_NOT_AVAILABLE);
        }
    }

        /**
         *  Forwards request to delegate based on productKey
         *
         * @param sessionName,
         * @param productKey,
         * @param timeStruct,
         * @param aShort
     */
    public MarketDataHistoryStruct getProductByTime(String querySessionId, String sessionName,
            int productKey, DateTimeStruct timeStruct, short aShort) throws SystemException,
            CommunicationException, DataValidationException, NotFoundException,
            AuthorizationException
    {
        if (TradingSessionNameHelper.isUnderlyingSession(sessionName) && !useRegularMDSRoutingProxy)
        {
            return globalMDS.getProductByTime(querySessionId, sessionName, productKey, timeStruct,
                    aShort);
        }
        else
        {
            MarketDataService service = (MarketDataService) getServiceByProduct(sessionName, productKey);
            return service.getProductByTime(querySessionId, sessionName, productKey, timeStruct, aShort);
        }
    }

    /* Operation Definition */
    public MarketDataHistoryDetailStruct getDetailProductHistoryByTime(String querySessionId,
            String sessionName, int productKey, DateTimeStruct startTime, short direction)
            throws SystemException, CommunicationException, DataValidationException,
            NotFoundException, AuthorizationException
    {
        if (TradingSessionNameHelper.isUnderlyingSession(sessionName) && !useRegularMDSRoutingProxy)
        {
            return globalMDS.getDetailProductHistoryByTime(querySessionId, sessionName, productKey, startTime, direction);
        }
        else
        {
            MarketDataService service = (MarketDataService) getServiceByProduct(sessionName, productKey);
            return service.getDetailProductHistoryByTime(querySessionId, sessionName, productKey, startTime, direction);
        }
    }

    /* Operation Definition */
    public MarketDataHistoryDetailStruct getPriorityProductHistoryByTime(String querySessionId,
            String sessionName, int productKey, DateTimeStruct startTime, short direction)
            throws SystemException, CommunicationException, DataValidationException,
            NotFoundException, AuthorizationException
    {
        if (TradingSessionNameHelper.isUnderlyingSession(sessionName) && !useRegularMDSRoutingProxy)
        {
            return globalMDS.getPriorityProductHistoryByTime(querySessionId, sessionName, productKey, startTime, direction);
        }
        else
        {
            MarketDataService service = (MarketDataService) getServiceByProduct(sessionName, productKey);
            return service.getPriorityProductHistoryByTime(querySessionId, sessionName, productKey, startTime, direction);
        }
    }

    /**
     * Forwards request to delegate based on productKey
     * 
     * @param sessionName,
     * @param productKey
     */
    public RecapStruct getRecapForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, DataValidationException,
            NotFoundException, AuthorizationException
    {
        if (TradingSessionNameHelper.isUnderlyingSession(sessionName) && !useRegularMDSRoutingProxy)
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "getRecapForClass will go to Global MDS, for session:" + sessionName);
            }
            
            return globalMDS.getRecapForProduct(sessionName, productKey);
        }
        else if (TradingSessionNameHelper.isNotApplicableSession(sessionName))
        {
            throw ExceptionBuilder.dataValidationException("No recap data is available.", 0);
        }
        else
        {
            MarketDataService service = (MarketDataService) getServiceByProduct(sessionName, productKey);
            return service.getRecapForProduct(sessionName, productKey);
        }
    }

        /**
         *  Forwards request to delegate based on classkey
         *
         * @param sessionName,
         * @param classKey
     */
    public ClassRecapStruct getRecapForClass(String sessionName, int classKey)
            throws SystemException, CommunicationException, DataValidationException,
            AuthorizationException
    {
        if (TradingSessionNameHelper.isUnderlyingSession(sessionName) && !useRegularMDSRoutingProxy)
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "getRecapForClass will go to Global MDS, for session:"
                        + sessionName);
            }
            return globalMDS.getRecapForClass(sessionName, classKey);
        }
        else if (TradingSessionNameHelper.isNotApplicableSession(sessionName))
        {
            throw ExceptionBuilder.dataValidationException("No recap data is available.", 0);
        }
        else
        {
            MarketDataService service = (MarketDataService) getServiceByClass(sessionName, classKey);
            return service.getRecapForClass(sessionName, classKey);
        }
    }

    /**
     * Forwards request to delegate based on productKey
     * 
     * @param sessionName,
     * @param productKey
     */
    public RecapStruct getUnderlyingRecapForDerivative(String sessionName, int productKey)
            throws SystemException, CommunicationException, DataValidationException,
            AuthorizationException, NotFoundException
    {
        if (TradingSessionNameHelper.isUnderlyingSession(sessionName) && !useRegularMDSRoutingProxy)
        {
            return globalMDS.getUnderlyingRecapForDerivative(sessionName, productKey);
        }
        else
        {
            MarketDataService service = (MarketDataService) getServiceByProduct(sessionName, productKey);
            return service.getUnderlyingRecapForDerivative(sessionName, productKey);
        }
     }

        /**
         * Return the Service Helper class name
         *
         * @return String, the service helper class name related to this proxy
         */
        protected String getHelperClassName()
        {
            return "com.cboe.idl.businessServices.MarketDataServiceHelper";
        }

    // Added for QPE-2
    public QuoteQueryStruct[] getQuoteQueryDataForProducts(String sessionName, int[] productKeys)
            throws SystemException, CommunicationException, DataValidationException,
            AuthorizationException
    {
        if (TradingSessionNameHelper.isUnderlyingSession(sessionName) && !useRegularMDSRoutingProxy)
         {
            return globalMDS.getQuoteQueryDataForProducts(sessionName, productKeys);
         }
         else
         {
            // Make sure that the product key used for routing is not totally invalid: 
            // 
            int prodKey = productKeys[0];
            for (int i = 0; i < productKeys.length; i++)
            {
                if (productKeys[i] > 0)
                {
                    prodKey = productKeys[i];
                    break;
                }
            }
            MarketDataService service = (MarketDataService) getServiceByProduct(sessionName, prodKey);
            return service.getQuoteQueryDataForProducts(sessionName, productKeys);
         }

    }

    // Added for QPE-2
    public ClosingQuoteSummaryStruct[] getClosingQuotesForClasses(String sessionName,
            int[] reportingClassKeys, int[] classKeys) throws SystemException,
            CommunicationException, DataValidationException, AuthorizationException
         {
        if (TradingSessionNameHelper.isUnderlyingSession(sessionName) && !useRegularMDSRoutingProxy)
        {
            return globalMDS.getClosingQuotesForClasses(sessionName, reportingClassKeys, classKeys);
        }
        else
        {
            MarketDataService service = (MarketDataService) getServiceByClass(sessionName, classKeys[0]);
            return service.getClosingQuotesForClasses(sessionName, reportingClassKeys, classKeys);
        }

    }
    
    public LegMarketDetailStructV2[] getLegMarketDetailForProductsV2(String p_sessionName, 
        	int[] p_productKeys, short isStockLegShortSell) throws SystemException,
        	CommunicationException, DataValidationException, NotFoundException, AuthorizationException
    {
        if( TradingSessionNameHelper.isUnderlyingSession(p_sessionName) && !useRegularMDSRoutingProxy)
        {
           return globalMDS.getLegMarketDetailForProductsV2(p_sessionName, p_productKeys, isStockLegShortSell);
        }
        else
        {
           MarketDataService service = (MarketDataService) getServiceByProduct(p_sessionName, p_productKeys[0] );
           return service.getLegMarketDetailForProductsV2(p_sessionName, p_productKeys, isStockLegShortSell);
        }
    	
    }
    public LegMarketDetailStruct[] getLegMarketDetailForProducts(String p_sessionName, int[] p_productKeys) 
            throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException
    {
        if( TradingSessionNameHelper.isUnderlyingSession(p_sessionName) && !useRegularMDSRoutingProxy)
        {
           return globalMDS.getLegMarketDetailForProducts(p_sessionName, p_productKeys);
        }
        else
        {
           MarketDataService service = (MarketDataService) getServiceByProduct(p_sessionName, p_productKeys[0] );
           return service.getLegMarketDetailForProducts(p_sessionName, p_productKeys);
        }
    }   
    

    public QuoteQueryV2Struct[] getQuoteQueryDataForProductsV2(String sessionName, int[] productKeys)
            throws SystemException, CommunicationException, DataValidationException,
            AuthorizationException
    {
        if (TradingSessionNameHelper.isUnderlyingSession(sessionName) && !useRegularMDSRoutingProxy)
        {
            return globalMDS.getQuoteQueryDataForProductsV2(sessionName, productKeys);
        }
        else
        {
           // Make sure that the product key used for routing is not totally invalid: 
           // 
           int prodKey = productKeys[0];
           for (int i = 0; i < productKeys.length; i++)
           {
               if (productKeys[i] > 0)
               {
                   prodKey = productKeys[i];
                   break;
               }
           }
           MarketDataService service = (MarketDataService) getServiceByProduct(sessionName, prodKey);
           return service.getQuoteQueryDataForProductsV2(sessionName, productKeys);
        }
    }
    
    public QuoteQueryV3Struct[] getQuoteQueryDataForProductsV3(String sessionName, int[] productKeys)
            throws SystemException, CommunicationException, DataValidationException,
            AuthorizationException
    {
        try
        {
            if (TradingSessionNameHelper.isUnderlyingSession(sessionName) && !useRegularMDSRoutingProxy)
            {
                return globalMDS.getQuoteQueryDataForProductsV3(sessionName, productKeys);
            }
            else
            {
               // Make sure that the product key used for routing is not totally invalid: 
               // 
               int prodKey = productKeys[0];
               for (int i = 0; i < productKeys.length; i++)
               {
                   if (productKeys[i] > 0)
                   {
                       prodKey = productKeys[i];
                       break;
                   }
               }
               MarketDataService service = (MarketDataService) getServiceByProduct(sessionName, prodKey);
               return service.getQuoteQueryDataForProductsV3(sessionName, productKeys);
            }
        }
        catch(org.omg.CORBA.COMM_FAILURE cf)
        {
            throw ExceptionBuilder.communicationException("COMM_FAILURE: Failed get Quote Query data for Product:", CommunicationFailureCodes.SERVER_NOT_AVAILABLE);
        }
        catch(org.omg.CORBA.TRANSIENT trans)
        {
            throw ExceptionBuilder.communicationException("TRANSIENT: Failed get Quote Query data for Product:", CommunicationFailureCodes.SERVER_NOT_AVAILABLE);
        }
        catch(org.omg.CORBA.OBJECT_NOT_EXIST oe)
        {
            throw ExceptionBuilder.communicationException("OBJECT_NOT_EXIST: Failed get Quote Query data for Product:", CommunicationFailureCodes.SERVER_NOT_AVAILABLE);
        }
        

    }   

    public void acceptCmiAddManualPriceReport(String p_userId,
            CmiManualPriceReportEntryStruct p_cmiManualPrice) throws SystemException,
            CommunicationException, DataValidationException, AuthorizationException,
            NotAcceptedException
    {
        if (TradingSessionNameHelper.isUnderlyingSession(p_cmiManualPrice.sessionName) && !useRegularMDSRoutingProxy)
        {
            globalMDS.acceptCmiAddManualPriceReport(p_userId, p_cmiManualPrice);
        }
        else
        {
             // Make sure that the product key used for routing is not totally invalid:
            // 
            int prodKey = p_cmiManualPrice.productKeys.productKey;
            MarketDataService service = (MarketDataService) getServiceByProduct(p_cmiManualPrice.sessionName, prodKey);
            service.acceptCmiAddManualPriceReport(p_userId, p_cmiManualPrice);
        }

    }

    public void acceptCmiCancelManualPriceReport(String p_userId,
            CmiManualPriceReportEntryStruct p_cmiManualPrice) throws SystemException,
            CommunicationException, DataValidationException, AuthorizationException,
            NotAcceptedException
    {
        // TODO Auto-generated method stub

    }

    public void acceptCmiUpdateManualPriceReport(String p_userId,
            CmiManualPriceReportEntryStruct p_cmiManualPrice) throws SystemException,
            CommunicationException, DataValidationException, AuthorizationException,
            NotAcceptedException
    {
        // TODO Auto-generated method stub

    }

    
    /**
     * Forwards request to delegate based on classKey.
     * @param userId
     * @param manualPrice
     * @throws SystemException,CommunicationException,AuthorizationException,DataValidationException,NotAcceptedException
     * @author Cognizant Technology Solutions.
     */
    public void acceptManualPriceReport(String userId, ManualPriceReportEntryStruct manualPrice)
    throws    SystemException,
                CommunicationException,
                DataValidationException,
                AuthorizationException,
                NotAcceptedException
    {
         
        if( TradingSessionNameHelper.isUnderlyingSession(manualPrice.sessionName) && !useRegularMDSRoutingProxy)
        {
            globalMDS.acceptManualPriceReport (userId,manualPrice); 
        }
        else if ( TradingSessionNameHelper.isNotApplicableSession(manualPrice.sessionName) )
        {
            throw ExceptionBuilder.dataValidationException("No current market data is available.", 0);
        }
        else
        {
            MarketDataService service = (MarketDataService) getServiceByClass(manualPrice.sessionName, manualPrice.productKeys.classKey );
            service.acceptManualPriceReport(userId,manualPrice);
        }
        
    }

    /**
     * Forwards request to delegate based on classKey.
     * @param sessionName,
     * @param classSettlementStructsV4
     * @return ClassRecapStructV5[]
     * @throws SystemException, CommunicationException, DataValidationException, AuthorizationException
     * @author Cognizant Technology Solutions.
     */
    public ClassRecapStructV5[] getClosingRecapForClassesV5(String sessionName, ClassSettlementStructV4[] classSettlementStructsV4) 
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException 
    {
        ClassRecapStructV5[] classRecapStructV5 = null;
        if( TradingSessionNameHelper.isUnderlyingSession(sessionName) && !useRegularMDSRoutingProxy)
        {
            return globalMDS.getClosingRecapForClassesV5(sessionName,classSettlementStructsV4); 
        }
        else if ( TradingSessionNameHelper.isNotApplicableSession(sessionName) )
        {
            throw ExceptionBuilder.dataValidationException("No current market data is available.", 0);
        }
        else
        {
            MarketDataService service = null;
            StringBuilder sb = new StringBuilder(60);
            // Find the correct MDS to route to. Break after you get one
            outer: for(int i = 0; i < classSettlementStructsV4.length; i++)
            {
                if(classSettlementStructsV4[i].settlementsV4.length != 0)
                {
                    for(int j = 0; j < classSettlementStructsV4[i].settlementsV4.length; j ++)
                    {
                        try
                        {
                            service = (MarketDataService) getServiceByClass(sessionName, classSettlementStructsV4[i].settlementsV4[j].productSettlementStruct.productKeys.classKey );
                            
                            sb.setLength(0);
                            if(service != null)
                            {
                                sb.append("Got MDS for a classkey ")
                                  .append(classSettlementStructsV4[i].settlementsV4[j].productSettlementStruct.productKeys.classKey);
                                Log.information(this, sb.toString());
                                break outer;
                            }
                            else
                            {
                                sb.append("MarketDataService is null for ")
                                  .append(classSettlementStructsV4[i].settlementsV4[j].productSettlementStruct.productKeys.classKey);
                                Log.alarm(this, sb.toString());
                            }
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                            Log.alarm(this, "Market Data Service not found for the class "+classSettlementStructsV4[i].settlementsV4[j].productSettlementStruct.productKeys.classKey);
                        }
                    }
                }
                else
                {
                    sb.setLength(0);
                    sb.append("length of class settlement struct is ")
                      .append(classSettlementStructsV4[i].settlementsV4.length);
                    Log.information(this, sb.toString());
                }
            }
            if(service != null)
            {
                classRecapStructV5 = service.getClosingRecapForClassesV5(sessionName,classSettlementStructsV4);
                return classRecapStructV5;
            }
            else
            {
                Log.alarm(this, "Market data service is not found for sessionName "+sessionName);
                return new ClassRecapStructV5[0];
            }
        }
    }
    
    /**
     * Forwards request to delegate based on productKey.
     * @param sessionName,
     * @param productKey
     * @return ProductAndUnderlyingRecapStruct
     * @throws SystemException, CommunicationException, DataValidationException, AuthorizationException
     * @author Cognizant Technology Solutions.
     */
    public ProductAndUnderlyingRecapStruct getRecapForProductAndUnderlying(String sessionName, int productKey) 
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException 
    {           
        ProductAndUnderlyingRecapStruct productAndUnderlyingRecapStruct = null;
        if( TradingSessionNameHelper.isUnderlyingSession(sessionName) && !useRegularMDSRoutingProxy)
        {
            Log.information(this, "MDS RoutingProxy getRecapForProductAndUnderlying method is called...");
        }
        else if ( TradingSessionNameHelper.isNotApplicableSession(sessionName) )
        {
            throw ExceptionBuilder.dataValidationException("No current market data is available.", 0);
        }
        else
        {
            Log.information(this, "About to get MDS object for a prodKey");
            try
            {
                MarketDataService service = (MarketDataService) getServiceByProduct(sessionName, productKey);
                StringBuilder sb = new StringBuilder(40);
                
                if(service != null)
                {
                    sb.append("Got MDS for prodkey ").append(productKey);
                    Log.information(this, sb.toString());
                    productAndUnderlyingRecapStruct = service.getRecapForProductAndUnderlying(sessionName,productKey);
                }
                else
                {
                    sb.append("MarketDataService is null for ").append(productKey);
                    Log.alarm(this, sb.toString());
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                Log.alarm(this, "Market Data Service not found for the product " + productKey);
            }
        }
        return productAndUnderlyingRecapStruct;
    }

    public void submitMarketDataWork(int p_classKey, Runnable p_workUnit) throws SystemException
    {
        throw new UnsupportedOperationException("submitMarketDataWork");
    }

    public int getNumQueues()
    {
        throw new UnsupportedOperationException("getNumQueues");
    }

    public void submitMarketDataWorkToAllQueues(Runnable p_workUnit) throws SystemException
    {
        throw new UnsupportedOperationException("submitMarketDataWorkToAllQueues");
    }

    public void setCurrentMarketForwardingEnabled(boolean p_enabled) throws SystemException
    {
        throw new UnsupportedOperationException("setCurrentMarketForwardingEnabled");
        
    }
    public void setMarketDataHistoryForwardingEnabled(boolean p_enabled) throws SystemException
    {
        throw new UnsupportedOperationException("setMarketDataHistoryForwardingEnabled");
        
    }

}
