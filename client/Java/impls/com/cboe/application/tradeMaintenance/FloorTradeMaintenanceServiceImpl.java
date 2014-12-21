package com.cboe.application.tradeMaintenance;

import com.cboe.application.shared.LoggingUtil;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.TransactionTimingUtil;
import com.cboe.application.supplier.QuoteStatusV2Supplier;
import com.cboe.application.supplier.QuoteStatusV2SupplierFactory;
import com.cboe.idl.constants.TradeTypes;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.FloorTradeMaintenanceService;
import com.cboe.interfaces.application.SessionManagerV6;
import com.cboe.interfaces.application.UserQuoteService;
import com.cboe.interfaces.internalBusinessServices.TradeMaintenanceService;
import com.cboe.interfaces.internalBusinessServices.TradeMaintenanceServiceHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelListener;
import org.omg.CORBA.UserException;

/**
 * Created by IntelliJ IDEA.
 * User: josephg
 * Date: Feb. 27, 2009
 */
public class FloorTradeMaintenanceServiceImpl extends BObject implements FloorTradeMaintenanceService
{
    protected SessionManagerV6 sessionManager;
    protected TradeMaintenanceService tms;
    protected String userId;
    protected String exchange;
    protected String acronym;
    protected UserQuoteService userQuoteService;
    QuoteStatusV2Supplier quoteStatusV2Supplier;

    /**
     * Public constructor
     * @param sessionManager
     */
    public FloorTradeMaintenanceServiceImpl(SessionManagerV6 sessionManager)
    {
        this.sessionManager = sessionManager;
        tms = initTradeMaintenanceService();
        userQuoteService = ServicesHelper.getUserQuoteService(sessionManager);
        quoteStatusV2Supplier = QuoteStatusV2SupplierFactory.create(sessionManager);

        try
        {
            userId = sessionManager.getUserId();
            exchange = sessionManager.getValidSessionProfileUserV2().userInfo.userAcronym.exchange;
            acronym = sessionManager.getValidSessionProfileUserV2().userInfo.userAcronym.acronym;
        }
        catch (UserException e)
        {
            Log.exception(this, "fatal error in getting userId from session:" + sessionManager, e);
        }
    }


    /**
     * Retrieves the server side implementation on Trade Maintenance Service
     */
    private TradeMaintenanceService initTradeMaintenanceService()
    {
        if (tms == null)
        {
            try
            {
                TradeMaintenanceServiceHome home = (TradeMaintenanceServiceHome) HomeFactory.getInstance().findHome(TradeMaintenanceServiceHome.ADMIN_HOME_NAME);

                tms = (TradeMaintenanceService) home.find();
            }
            catch (CBOELoggableException e)
            {
                throw new NullPointerException("Could not find TradeMaintenanceServiceHome");
            }
        }
        return tms;
    }

    /**
     * sends MMHH trade to server
     * @param floorTradeEntryStruct
     * @return CboeIdStruct on successful execution of method call.
     * @throws com.cboe.exceptions.SystemException
     * @throws com.cboe.exceptions.CommunicationException
     * @throws com.cboe.exceptions.AuthorizationException
     * @throws com.cboe.exceptions.DataValidationException
     * @throws com.cboe.exceptions.NotAcceptedException
     * @throws com.cboe.exceptions.TransactionFailedException
     */
    public com.cboe.idl.cmiUtil.CboeIdStruct acceptFloorTrade(com.cboe.idl.cmiTrade.FloorTradeEntryStruct floorTradeEntryStruct) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException, com.cboe.exceptions.NotAcceptedException, com.cboe.exceptions.TransactionFailedException
    {
        long startTime = System.currentTimeMillis();
        long entityId = 0L;
        try
        {
            entityId = TransactionTimingUtil.setEntityID();
        }
        catch (Exception e)
        {
            Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }
        Log.information(this, LoggingUtil.createFloorTradeLog("acceptFloorTrade", entityId, userId, floorTradeEntryStruct));
        // validate productKey?
        long serverStartTime = System.currentTimeMillis();
        com.cboe.idl.cmiUtil.CboeIdStruct tradeId = tms.acceptFloorTrade(floorTradeEntryStruct, userId, TradeTypes.HANDHELD_TRADE);
        long endTime = System.currentTimeMillis();
        StringBuilder returning = new StringBuilder(userId.length()+floorTradeEntryStruct.executingMarketMaker.acronym.length()+120);
        returning.append("acceptFloorTrade returning UID:").append(userId)
                 .append(" UA:").append(floorTradeEntryStruct.executingMarketMaker.acronym)
                 .append(" TradeID:").append(tradeId.highCboeId).append(":").append(tradeId.lowCboeId)
                 .append(" EID:").append(entityId)
                 .append(" TT:").append(endTime - startTime)
                 .append(" ST:").append(endTime - serverStartTime);
        Log.information(this, returning.toString());
        return tradeId;
    }

    /**
     * Deletes floor trade. An alternative for update.
     * @param sessionName
     * @param productKey
     * @param tradeId
     * @param user
     * @param firm
     * @param reason
     * @throws com.cboe.exceptions.SystemException
     * @throws com.cboe.exceptions.CommunicationException
     * @throws com.cboe.exceptions.AuthorizationException
     * @throws com.cboe.exceptions.DataValidationException
     * @throws com.cboe.exceptions.NotAcceptedException
     * @throws com.cboe.exceptions.NotFoundException
     * @throws com.cboe.exceptions.TransactionFailedException
     */
    public void deleteFloorTrade(java.lang.String sessionName, int productKey, com.cboe.idl.cmiUtil.CboeIdStruct tradeId, com.cboe.idl.cmiUser.ExchangeAcronymStruct user, com.cboe.idl.cmiUser.ExchangeFirmStruct firm, java.lang.String reason) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException, com.cboe.exceptions.NotAcceptedException, com.cboe.exceptions.NotFoundException, com.cboe.exceptions.TransactionFailedException
    {
        long startTime = System.currentTimeMillis();
        long entityId = 0L;
        try
        {
            entityId = TransactionTimingUtil.setEntityID();
        }
        catch (Exception e)
        {
            Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }
        StringBuilder calling = new StringBuilder(userId.length()+user.acronym.length()+135);
        calling.append("deleteFloorTrade calling UID:").append(userId)
               .append(" UA:").append(user.acronym)
               .append(" PK:").append(productKey)
               .append(" TradeID: ").append(tradeId.highCboeId).append(":").append(tradeId.lowCboeId)
               .append(" EID:").append(entityId);
        Log.information(this, calling.toString());
        long serverStartTime = System.currentTimeMillis();
        tms.deleteFloorTrade(sessionName, productKey, tradeId, user, firm, reason);
        long endTime = System.currentTimeMillis();
        calling.setLength(0);
        calling.append("deleteFloorTrade returning UID:").append(userId)
               .append(" UA:").append(user.acronym)
               .append(" PK:").append(productKey)
               .append(" TradeID: ").append(tradeId.highCboeId).append(":").append(tradeId.lowCboeId)
               .append(" EID:").append(entityId)
               .append(" TT:").append(endTime - startTime)
               .append(" ST:").append(endTime - serverStartTime);
        Log.information(this, calling.toString());
    }

    /**
     * Subscribes for MMTN. MMTN are published as quote fill repots with quoeId=0.
     * @param cmiQuoteStatusConsumer    consumer
     * @param classKey A valid classkey or 0. If it 0 subscribe for all the classes.
     * @param gmdConsumer
     * @throws com.cboe.exceptions.SystemException
     * @throws com.cboe.exceptions.CommunicationException
     * @throws com.cboe.exceptions.AuthorizationException
     * @throws com.cboe.exceptions.DataValidationException
     */
    public void subscribeForFloorTradeReportsByClass(com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer cmiQuoteStatusConsumer, int classKey, boolean gmdConsumer) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        StringBuilder calling = new StringBuilder(userId.length()+80);
        calling.append("calling subscribeForFloorTradeReportsByClass for UID:").append(userId)
               .append(" CK:").append(classKey)
               .append(" gmd:").append(gmdConsumer);
        Log.information(this, calling.toString());
        ChannelListener proxyListener = ServicesHelper.getQuoteStatusConsumerProxy(cmiQuoteStatusConsumer, sessionManager, gmdConsumer);
        if (classKey != 0)
        {
            Integer theKey = classKey;
            ServicesHelper.getProductQueryServiceAdapter().checkProductCacheLoaded(classKey);
            ChannelKey channelKey = new ChannelKey(ChannelType.CB_MMHH_QUOTE_FILL_REPORT_V2, theKey);
            quoteStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, theKey);
            userQuoteService.publishUnAckedQuotesForClass(classKey);


        }
        else
        {
            Integer theKey = 0;
            ChannelKey channelKey = new ChannelKey(ChannelType.CB_MMHH_QUOTE_FILL_REPORT_V2, theKey);
            quoteStatusV2Supplier.addChannelListener(this, proxyListener, channelKey);
            userQuoteService.publishUnAckedQuotes();

        }
        calling.setLength(0);
        calling.append("returning subscribeForFloorTradeReportsByClass for UID:").append(userId)
               .append(" CK:").append(classKey)
               .append(" gmd:").append(gmdConsumer);
        Log.information(this, calling.toString());

    }

    /**
     * unsubscribes MMTNs. MMTN are published as quote fill repots with quoeId=0.
     * @param cmiQuoteStatusConsumer    consumer  
     * @param classKey A valid classkey or 0. If it 0 subscribe for all the classes.
     * @throws com.cboe.exceptions.SystemException
     * @throws com.cboe.exceptions.CommunicationException
     * @throws com.cboe.exceptions.AuthorizationException
     * @throws com.cboe.exceptions.DataValidationException
     */
    public void unsubscribeForFloorTradeReportsByClass(com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer cmiQuoteStatusConsumer, int classKey) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        StringBuilder calling = new StringBuilder(userId.length()+75);
        calling.append("calling unsubscribeForFloorTradeReportsByClass for UID:").append(userId)
               .append(" CK:").append(classKey);
        Log.information(this, calling.toString());
        ChannelListener proxyListener = ServicesHelper.getQuoteStatusConsumerProxy(cmiQuoteStatusConsumer, sessionManager, true);
        if (classKey != 0)
        {

            Integer theKey = classKey;
            ServicesHelper.getProductQueryServiceAdapter().checkProductCacheLoaded(classKey);
            ChannelKey channelKey = new ChannelKey(ChannelType.CB_MMHH_QUOTE_FILL_REPORT_V2, theKey);
            quoteStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, theKey);

        }
        else
        {
            Integer theKey = 0;
            ChannelKey channelKey = new ChannelKey(ChannelType.CB_MMHH_QUOTE_FILL_REPORT_V2, theKey);
            quoteStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, theKey);
        }

        calling.setLength(0);
        calling.append("returning unsubscribeForFloorTradeReportsByClass for UID:").append(userId)
               .append(" CK:").append(classKey);
        Log.information(this, calling.toString());

    }

}
