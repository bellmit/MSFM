package com.cboe.cfix.fix.fix42.session;

import com.cboe.cfix.util.FutureExecutionIF;
import com.cboe.cfix.util.OverlayPolicyFactory;
import com.cboe.cfix.fix.fix42.generated.messages.FixMarketDataRequestMessage;
import com.cboe.cfix.fix.util.FixMarketDataRejectStruct;
import com.cboe.cfix.cas.shared.CfixServicesHelper;
import com.cboe.cfix.cas.marketData.CfixMDXMarketDataConsumerHolderInstrumentedImpl;
import com.cboe.cfix.cas.marketData.CfixMDXMarketDataConsumerHolderImpl;
import com.cboe.cfix.interfaces.FixFieldIF;
import com.cboe.interfaces.cfix.*;
import com.cboe.client.util.Latch;
import com.cboe.client.util.BitHelper;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.exceptions.*;

/**
 * FixMDXMarketDataFutureExecution.java
 *
 * @author Dmitry Volpyansky / Vivek Beniwal
 *
 */

public class FixMDXMarketDataFutureExecution implements FutureExecutionIF
{
    protected int                               futureExecutionStatus = FutureExecutionIF.MAIN_STATE_READY;
    protected FixMarketDataRequestMessage       fixMarketDataRequestMessage;
    protected FixMDXMarketDataFutureExecution   fixMarketDataFutureExecutionToUnsubscribe;
    protected CfixMDXMarketDataQueryIF          cfixMDXMarketDataQuery;
    protected FixMDXSession                     fixSession;
    protected Latch                             cancelAndUndoLatch = new Latch();
    protected boolean                           cancelAndUndo;
    protected int[]                             unsubscribeArray;
    protected int                               unsubscribeArrayLength;

    public FixMDXMarketDataFutureExecution(FixMDXSession fixSession, FixMarketDataRequestMessage fixMarketDataRequestMessage)
    {
        this.fixSession                  = fixSession;
        this.fixMarketDataRequestMessage = fixMarketDataRequestMessage;
    }

    public FixMDXMarketDataFutureExecution(FixMDXSession fixSession, FixMDXMarketDataFutureExecution fixMarketDataFutureExecutionToUnsubscribe)
    {
        this.fixSession                                = fixSession;
        this.fixMarketDataFutureExecutionToUnsubscribe = fixMarketDataFutureExecutionToUnsubscribe;
    }

    public int getStatusBits()
    {
        return futureExecutionStatus;
    }

    public int cancelAndUndo(long millisToWaitForAcknowlegement)
    {
        cancelAndUndo = true;

        if (BitHelper.isBitMaskSet(futureExecutionStatus, FutureExecutionIF.MAIN_STATE_RUNNING))
        {
            cancelAndUndoLatch.acquire(millisToWaitForAcknowlegement);
        }

        return futureExecutionStatus;
    }

    public String getMdReqID()
    {
        return fixMarketDataRequestMessage.fieldMDReqID.getValue();
    }

    public FixMarketDataRequestMessage getFixMarketDataRequestMessage()
    {
        return fixMarketDataRequestMessage;
    }

    public void run()
    {
        CfixMarketDataConsumer cachedCfixMarketDataConsumer = null;

        if (Log.isDebugOn()) Log.debug(Thread.currentThread().getName() + " Future Starting");

        OverlayPolicyFactory overlayPolicyFactory = fixSession.getOverlayPolicyFactory();

        try
        {
            // Vivek : Should return an object of the CfixMDXMarketDataQueryProxy type
            cfixMDXMarketDataQuery = fixSession.getCfixSessionManager().getCfixMDXMarketDataQuery();
            // Vivek : The consumer is the FixMDXSession object
            cachedCfixMarketDataConsumer = cfixMDXMarketDataQuery.getCfixMarketDataConsumer();
        }
        catch (Exception ex)
        {
            Log.exception(ex);
        }

        if (cfixMDXMarketDataQuery == null || cachedCfixMarketDataConsumer == null || cancelAndUndo || fixSession.terminated())
        {
            futureExecutionStatus = BitHelper.orBits(FutureExecutionIF.MAIN_STATE_FINISHED, FutureExecutionIF.CURRENT_STATE_UNDO);

            cancelAndUndoLatch.release();

            fixSession = null;

            if (Log.isDebugOn()) Log.debug(Thread.currentThread().getName() + " Future Ending (Finished|Undo)");

            return;
        }

        if (fixMarketDataFutureExecutionToUnsubscribe != null)
        {
            cancelAndUndoLatch.release();

            unsubscribeMdReqID(fixMarketDataFutureExecutionToUnsubscribe.getMdReqID(),
                    fixMarketDataFutureExecutionToUnsubscribe.unsubscribeArray, fixMarketDataFutureExecutionToUnsubscribe.unsubscribeArrayLength);

            fixMarketDataFutureExecutionToUnsubscribe.futureExecutionStatus = BitHelper.orBits(FutureExecutionIF.MAIN_STATE_FINISHED, FutureExecutionIF.CURRENT_STATE_UNDO);

            if (Log.isDebugOn()) Log.debug(Thread.currentThread().getName() + " Future Ending Unsubscribed (Finished|Undo)");

            return;
        }

        futureExecutionStatus = BitHelper.orBits(FutureExecutionIF.MAIN_STATE_RUNNING, FutureExecutionIF.CURRENT_STATE_RUNNING);

        Exception                         exception                        = null;
        String                            exceptionMessage                 = "";
        SessionProductStruct              sessionProductStruct             = null;
        SessionClassStruct                sessionClassStruct               = null;
        String                            mdReqID                          = fixMarketDataRequestMessage.fieldMDReqID.getValue();
        String                            targetCompID                     = fixSession.getTargetCompID();
        String                            sessionName                      = fixSession.getSessionName();
        CfixProductConfigurationService   cfixProductConfigurationService;
        OverlayPolicyMarketDataListIF     cfixOverlayPolicyMarketDataList;
        CfixMDXMarketDataConsumerHolder   cfixFixMDXMarketDataConsumerHolder;

        MethodInstrumentor methodInstrumentor = null;

        unsubscribeArray       = new int[fixMarketDataRequestMessage.groupRelatedSym.length];
        unsubscribeArrayLength = 0;

        do
        {
            try
            {
                int requestType = FixMarketDataRequestDecoder.getMarketDataRequestType(fixMarketDataRequestMessage);
                if (requestType == CfixMarketDataDispatcherIF.MarketDataType_Unknown)
                {
                    reject(cachedCfixMarketDataConsumer, FixMarketDataRejectStruct.UnknownSymbol, mdReqID,
                            "Can't build a product or class from supplied information (Could Not Determine Market Data Request)");
                    break;
                }
                //IF we start handling NBBOs, then can remove this, and the rest of the code should work correctly
                else if (requestType == CfixMarketDataDispatcherIF.MarketDataType_Nbbo)
                {
                    reject(cachedCfixMarketDataConsumer, FixMarketDataRejectStruct.InsufficientPermissions, mdReqID, "Engine Does Not Handle NBBO Subscriptions");
                    break;
                }
                /*
                else if (requestType == CfixMarketDataDispatcherIF.MarketDataType_BookDepth)
                {
                    reject(cachedCfixMarketDataConsumer, FixMarketDataRejectStruct.InsufficientPermissions, mdReqID, "Engine Does Not Handle BookDepth Subscriptions");
                    break;
                }
                */

                //int overlayPolicy = FixMarketDataRequestDecoder.getMarketDataOverlayPolicy(fixMarketDataRequestMessage);
                int overlayPolicy = fixSession.getOverlayPolicyFactory().getOverlayPolicy();

                cfixProductConfigurationService = CfixServicesHelper.getCfixProductConfigurationService();

                for (int i = 0; !fixSession.terminated() && !cancelAndUndo && i < fixMarketDataRequestMessage.groupRelatedSym.length; i++)
                {
                    switch (requestType)
                    {
                        //VivekB:  Current Market now uses CurrentMarketStructV4 -
                        case CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket:

                            FixMarketDataRequestMessage.RelatedSymGroup relatedSymGroup = fixMarketDataRequestMessage.groupRelatedSym[i];

                            if (relatedSymGroup.fieldTradingSessionID != null &&
                                "underlying".equalsIgnoreCase(relatedSymGroup.fieldTradingSessionID.getValue()))
                            {
                                reject(cachedCfixMarketDataConsumer, FixMarketDataRejectStruct.InsufficientPermissions, mdReqID,
                                                    "Engine Does Not Handle CurrentMarket Subscriptions for the Underlying (Equity) Session");
                            }

                            // Vivek : The subscription will be either by class or product - but not both.
                            sessionClassStruct = FixMarketDataRequestDecoder.buildSessionClassStruct(fixMarketDataRequestMessage, i, fixSession.getDebugFlags());
                            if (sessionClassStruct != null)
                            {
                                if(Log.isDebugOn())
                                {
                                    Log.debug("FixMDXMarketDataFutureExecution: Subscribing for Current Market By Class.");
                                }
                                if (validateClassKey(mdReqID, cfixProductConfigurationService.getConfiguredTargetCompID(sessionClassStruct.classStruct.classKey, targetCompID), targetCompID, cachedCfixMarketDataConsumer))
                                {
                                    //VivekB: switched to using overlayPolicyFactory.createOverlayPolicyMarketDataCurrentMarketStructV4List(overlayPolicy, mdReqID) - to use CurrentMarketStructV4
                                    cfixOverlayPolicyMarketDataList = overlayPolicyFactory.createOverlayPolicyMarketDataCurrentMarketStructV4List(overlayPolicy, mdReqID);
                                    methodInstrumentor = FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().getInstance(new StringBuffer().append(targetCompID).append("/").append(sessionName).append("/").append(sessionClassStruct.sessionName).append("/").append(sessionClassStruct.classStruct.classSymbol).append("-").append(sessionClassStruct.classStruct.classKey).append("/CurrentMarket").toString() , null);
                                    methodInstrumentor.setPrivate(true);
                                    cfixFixMDXMarketDataConsumerHolder = getCfixMDXMarketDataConsumerHolder(cachedCfixMarketDataConsumer, cfixOverlayPolicyMarketDataList, sessionClassStruct, methodInstrumentor);
                                    cfixMDXMarketDataQuery.subscribeCurrentMarketByClass(cfixFixMDXMarketDataConsumerHolder);
                                    unsubscribeArray[unsubscribeArrayLength++] = CfixMarketDataDispatcherIF.MarketDataType_CurrentMarketByClass;
                                }

                                continue;
                            }

                            sessionProductStruct = FixMarketDataRequestDecoder.buildSessionProductStruct(fixMarketDataRequestMessage, i, fixSession.getDebugFlags());
                            if (sessionProductStruct != null)
                            {
                                if(Log.isDebugOn())
                                {
                                    Log.debug("FixMDXMarketDataFutureExecution: Subscribing for Current Market By Product.");
                                }
                                if (validateClassKey(mdReqID, cfixProductConfigurationService.getConfiguredTargetCompID(sessionProductStruct.productStruct.productKeys.classKey, targetCompID), targetCompID, cachedCfixMarketDataConsumer))
                                {
                                    //VivekB: switched to using overlayPolicyFactory.createOverlayPolicyMarketDataCurrentMarketStructV4List(overlayPolicy, mdReqID) - to use CurrentMarketStructV4
                                    cfixOverlayPolicyMarketDataList = overlayPolicyFactory.createOverlayPolicyMarketDataCurrentMarketStructV4List(overlayPolicy, mdReqID);
                                    methodInstrumentor = FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().getInstance(new StringBuffer().append(targetCompID).append("/").append(sessionName).append("/").append(sessionProductStruct.sessionName).append("/").append(sessionProductStruct.productStruct.productName.reportingClass).append("-").append(sessionProductStruct.productStruct.productKeys.classKey).append("/").append(sessionProductStruct.productStruct.productName.expirationDate.year).append(sessionProductStruct.productStruct.productName.expirationDate.month).append("_").append(sessionProductStruct.productStruct.productName.exercisePrice.whole).append(".").append(sessionProductStruct.productStruct.productName.exercisePrice.fraction).append("_").append(sessionProductStruct.productStruct.productName.optionType).append("-").append(sessionProductStruct.productStruct.productKeys.productKey).append("/").append("/CurrentMarket").toString() , null);
                                    methodInstrumentor.setPrivate(true);
                                    cfixFixMDXMarketDataConsumerHolder = getCfixMDXMarketDataConsumerHolder(cachedCfixMarketDataConsumer, cfixOverlayPolicyMarketDataList, sessionProductStruct, methodInstrumentor);
                                    cfixMDXMarketDataQuery.subscribeCurrentMarketByProduct(cfixFixMDXMarketDataConsumerHolder);
                                    unsubscribeArray[unsubscribeArrayLength++] = CfixMarketDataDispatcherIF.MarketDataType_CurrentMarketByProduct;
                                }

                                continue;
                            }
                            break;
                        // NBBO requests are rejected above - NBBO data is currently not provided
                        case CfixMarketDataDispatcherIF.MarketDataType_Nbbo:
                            sessionClassStruct = FixMarketDataRequestDecoder.buildSessionClassStruct(fixMarketDataRequestMessage, i, fixSession.getDebugFlags());
                            if (sessionClassStruct != null)
                            {
                                if (validateClassKey(mdReqID, cfixProductConfigurationService.getConfiguredTargetCompID(sessionClassStruct.classStruct.classKey, targetCompID), targetCompID, cachedCfixMarketDataConsumer))
                                {
                                    cfixOverlayPolicyMarketDataList = overlayPolicyFactory.createOverlayPolicyMarketDataNbboStructList(overlayPolicy, mdReqID);
                                    methodInstrumentor = FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().getInstance(new StringBuffer().append(targetCompID).append("/").append(sessionName).append("/").append(sessionClassStruct.sessionName).append("/").append(sessionClassStruct.classStruct.classSymbol).append("-").append(sessionClassStruct.classStruct.classKey).append("/Nbbo").toString() , null);
                                    methodInstrumentor.setPrivate(true);
                                    cfixFixMDXMarketDataConsumerHolder = getCfixMDXMarketDataConsumerHolder(cachedCfixMarketDataConsumer, cfixOverlayPolicyMarketDataList, sessionClassStruct, methodInstrumentor);
                                    cfixMDXMarketDataQuery.subscribeNbboByClass(cfixFixMDXMarketDataConsumerHolder);
                                    unsubscribeArray[unsubscribeArrayLength++] = CfixMarketDataDispatcherIF.MarketDataType_NbboByProduct;
                                }

                                continue;
                            }

                            sessionProductStruct = FixMarketDataRequestDecoder.buildSessionProductStruct(fixMarketDataRequestMessage, i, fixSession.getDebugFlags());
                            if (sessionProductStruct != null)
                            {
                                if (validateClassKey(mdReqID, cfixProductConfigurationService.getConfiguredTargetCompID(sessionProductStruct.productStruct.productKeys.classKey, targetCompID), targetCompID, cachedCfixMarketDataConsumer))
                                {
                                    cfixOverlayPolicyMarketDataList = overlayPolicyFactory.createOverlayPolicyMarketDataNbboStructList(overlayPolicy, mdReqID);
                                    methodInstrumentor = FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().getInstance(new StringBuffer().append(targetCompID).append("/").append(sessionName).append("/").append(sessionProductStruct.sessionName).append("/").append(sessionProductStruct.productStruct.productName.reportingClass).append("-").append(sessionProductStruct.productStruct.productKeys.classKey).append("/").append(sessionProductStruct.productStruct.productName.expirationDate.year).append(sessionProductStruct.productStruct.productName.expirationDate.month).append("_").append(sessionProductStruct.productStruct.productName.exercisePrice.whole).append(".").append(sessionProductStruct.productStruct.productName.exercisePrice.fraction).append("_").append(sessionProductStruct.productStruct.productName.optionType).append("-").append(sessionProductStruct.productStruct.productKeys.productKey).append("/").append("/Nbbo").toString() , null);
                                    methodInstrumentor.setPrivate(true);
                                    cfixFixMDXMarketDataConsumerHolder = getCfixMDXMarketDataConsumerHolder(cachedCfixMarketDataConsumer, cfixOverlayPolicyMarketDataList, sessionProductStruct, methodInstrumentor);
                                    cfixMDXMarketDataQuery.subscribeNbboByProduct(cfixFixMDXMarketDataConsumerHolder);
                                    unsubscribeArray[unsubscribeArrayLength++] = CfixMarketDataDispatcherIF.MarketDataType_NbboByProduct;
                                }

                                continue;
                            }
                            break;
                        // VivekB: - Recap Subscription goes to MDX -
                        case CfixMarketDataDispatcherIF.MarketDataType_Recap:
                            sessionClassStruct = FixMarketDataRequestDecoder.buildSessionClassStruct(fixMarketDataRequestMessage, i, fixSession.getDebugFlags());
                            if (sessionClassStruct != null)
                            {
                                if (validateClassKey(mdReqID, cfixProductConfigurationService.getConfiguredTargetCompID(sessionClassStruct.classStruct.classKey, targetCompID), targetCompID, cachedCfixMarketDataConsumer))
                                {
                                    // VivekB: For MDX : switched to using overlayPolicyFactory.createOverlayPolicyMarketDataRecapContainerV4List(overlayPolicy, mdReqID) - to use RecapContainerV4
                                    cfixOverlayPolicyMarketDataList = overlayPolicyFactory.createOverlayPolicyMarketDataRecapContainerV4List(overlayPolicy, mdReqID);
                                    //cfixOverlayPolicyMarketDataList = overlayPolicyFactory.createOverlayPolicyMarketDataRecapStructList(overlayPolicy, mdReqID);
                                    methodInstrumentor = FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().getInstance(new StringBuffer().append(targetCompID).append("/").append(sessionName).append("/").append(sessionClassStruct.sessionName).append("/").append(sessionClassStruct.classStruct.classSymbol).append("-").append(sessionClassStruct.classStruct.classKey).append("/Recap").toString() , null);
                                    methodInstrumentor.setPrivate(true);
                                    cfixFixMDXMarketDataConsumerHolder = getCfixMDXMarketDataConsumerHolder(cachedCfixMarketDataConsumer, cfixOverlayPolicyMarketDataList, sessionClassStruct, methodInstrumentor);
                                    cfixMDXMarketDataQuery.subscribeRecapByClass(cfixFixMDXMarketDataConsumerHolder);
                                    unsubscribeArray[unsubscribeArrayLength++] = CfixMarketDataDispatcherIF.MarketDataType_RecapByProduct;
                                }

                                continue;
                            }

                            sessionProductStruct = FixMarketDataRequestDecoder.buildSessionProductStruct(fixMarketDataRequestMessage, i, fixSession.getDebugFlags());
                            if (sessionProductStruct != null)
                            {
                                if (validateClassKey(mdReqID, cfixProductConfigurationService.getConfiguredTargetCompID(sessionProductStruct.productStruct.productKeys.classKey, targetCompID), targetCompID, cachedCfixMarketDataConsumer))
                                {
                                    // VivekB: For MDX : switched to using overlayPolicyFactory.createOverlayPolicyMarketDataRecapContainerV4List(overlayPolicy, mdReqID) - to use RecapContainerV4
                                    cfixOverlayPolicyMarketDataList = overlayPolicyFactory.createOverlayPolicyMarketDataRecapContainerV4List(overlayPolicy, mdReqID);
                                    //cfixOverlayPolicyMarketDataList = overlayPolicyFactory.createOverlayPolicyMarketDataRecapStructList(overlayPolicy, mdReqID);
                                    methodInstrumentor = FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().getInstance(new StringBuffer().append(targetCompID).append("/").append(sessionName).append("/").append(sessionProductStruct.sessionName).append("/").append(sessionProductStruct.productStruct.productName.reportingClass).append("-").append(sessionProductStruct.productStruct.productKeys.classKey).append("/").append(sessionProductStruct.productStruct.productName.expirationDate.year).append(sessionProductStruct.productStruct.productName.expirationDate.month).append("_").append(sessionProductStruct.productStruct.productName.exercisePrice.whole).append(".").append(sessionProductStruct.productStruct.productName.exercisePrice.fraction).append("_").append(sessionProductStruct.productStruct.productName.optionType).append("-").append(sessionProductStruct.productStruct.productKeys.productKey).append("/").append("/Recap").toString() , null);
                                    methodInstrumentor.setPrivate(true);
                                    cfixFixMDXMarketDataConsumerHolder = getCfixMDXMarketDataConsumerHolder(cachedCfixMarketDataConsumer, cfixOverlayPolicyMarketDataList, sessionProductStruct, methodInstrumentor);
                                    cfixMDXMarketDataQuery.subscribeRecapByProduct(cfixFixMDXMarketDataConsumerHolder);
                                    unsubscribeArray[unsubscribeArrayLength++] = CfixMarketDataDispatcherIF.MarketDataType_RecapByProduct;
                                }

                                continue;
                            }
                            break;

                        case CfixMarketDataDispatcherIF.MarketDataType_BookDepthSnapshot:
                            sessionProductStruct = FixMarketDataRequestDecoder.buildSessionProductStruct(fixMarketDataRequestMessage, i, fixSession.getDebugFlags());
                            if (sessionProductStruct != null)
                            {
                                if (validateClassKey(mdReqID, cfixProductConfigurationService.getConfiguredTargetCompID(sessionProductStruct.productStruct.productKeys.classKey, targetCompID), targetCompID, cachedCfixMarketDataConsumer))
                                {
                                    cfixOverlayPolicyMarketDataList = overlayPolicyFactory.createOverlayPolicyMarketDataBookDepthStructList(overlayPolicy, mdReqID);
                                    cfixFixMDXMarketDataConsumerHolder = getCfixMDXMarketDataConsumerHolder(cachedCfixMarketDataConsumer, cfixOverlayPolicyMarketDataList, sessionProductStruct, methodInstrumentor);
                                    cfixMDXMarketDataQuery.snapshotBookDepthByProduct(cfixFixMDXMarketDataConsumerHolder);
                                }

                                continue;
                            }
                            break;

                        case CfixMarketDataDispatcherIF.MarketDataType_BookDepth:
                            sessionClassStruct = FixMarketDataRequestDecoder.buildSessionClassStruct(fixMarketDataRequestMessage, i, fixSession.getDebugFlags());
                            if (sessionClassStruct != null)
                            {
                                if (validateClassKey(mdReqID, cfixProductConfigurationService.getConfiguredTargetCompID(sessionClassStruct.classStruct.classKey, targetCompID), targetCompID, cachedCfixMarketDataConsumer))
                                {
                                    cfixOverlayPolicyMarketDataList = overlayPolicyFactory.createOverlayPolicyMarketDataBookDepthStructList(overlayPolicy, mdReqID);
                                    methodInstrumentor = FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().getInstance(new StringBuffer().append(targetCompID).append("/").append(sessionName).append("/").append(sessionClassStruct.sessionName).append("/").append(sessionClassStruct.classStruct.classSymbol).append("-").append(sessionClassStruct.classStruct.classKey).append("/BookDepth").toString() , null);
                                    methodInstrumentor.setPrivate(true);
                                    cfixFixMDXMarketDataConsumerHolder = getCfixMDXMarketDataConsumerHolder(cachedCfixMarketDataConsumer, cfixOverlayPolicyMarketDataList, sessionClassStruct, methodInstrumentor);
                                    cfixMDXMarketDataQuery.subscribeBookDepthByClass(cfixFixMDXMarketDataConsumerHolder);
                                    unsubscribeArray[unsubscribeArrayLength++] = CfixMarketDataDispatcherIF.MarketDataType_BookDepthByClass;
                                }

                                continue;
                            }

                            sessionProductStruct = FixMarketDataRequestDecoder.buildSessionProductStruct(fixMarketDataRequestMessage, i, fixSession.getDebugFlags());
                            if (sessionProductStruct != null)
                            {
                                if (validateClassKey(mdReqID, cfixProductConfigurationService.getConfiguredTargetCompID(sessionProductStruct.productStruct.productKeys.classKey, targetCompID), targetCompID, cachedCfixMarketDataConsumer))
                                {
                                    cfixOverlayPolicyMarketDataList = overlayPolicyFactory.createOverlayPolicyMarketDataBookDepthStructList(overlayPolicy, mdReqID);
                                    methodInstrumentor = FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().getInstance(new StringBuffer().append(targetCompID).append("/").append(sessionName).append("/").append(sessionProductStruct.sessionName).append("/").append(sessionProductStruct.productStruct.productName.reportingClass).append("-").append(sessionProductStruct.productStruct.productKeys.classKey).append("/").append(sessionProductStruct.productStruct.productName.expirationDate.year).append(sessionProductStruct.productStruct.productName.expirationDate.month).append("_").append(sessionProductStruct.productStruct.productName.exercisePrice.whole).append(".").append(sessionProductStruct.productStruct.productName.exercisePrice.fraction).append("_").append(sessionProductStruct.productStruct.productName.optionType).append("-").append(sessionProductStruct.productStruct.productKeys.productKey).append("/").append("/BookDepth").toString() , null);
                                    methodInstrumentor.setPrivate(true);
                                    cfixFixMDXMarketDataConsumerHolder = getCfixMDXMarketDataConsumerHolder(cachedCfixMarketDataConsumer, cfixOverlayPolicyMarketDataList, sessionProductStruct, methodInstrumentor);
                                    cfixMDXMarketDataQuery.subscribeBookDepthByProduct(cfixFixMDXMarketDataConsumerHolder);
                                    unsubscribeArray[unsubscribeArrayLength++] = CfixMarketDataDispatcherIF.MarketDataType_BookDepthByProduct;
                                }

                                continue;
                            }
                            break;
                        // VivekB: Ticker subscription goes to MDX -
                        case CfixMarketDataDispatcherIF.MarketDataType_Ticker:
                            sessionClassStruct = FixMarketDataRequestDecoder.buildSessionClassStruct(fixMarketDataRequestMessage, i, fixSession.getDebugFlags());
                            if (sessionClassStruct != null)
                            {
                                if (validateClassKey(mdReqID, cfixProductConfigurationService.getConfiguredTargetCompID(sessionClassStruct.classStruct.classKey, targetCompID), targetCompID, cachedCfixMarketDataConsumer))
                                {
                                    // VivekB: For subscriptions to MDX - switched to using overlayPolicyFactory.createOverlayPolicyMarketDataTickerStructV4List - to use TickerStructV4 if needed
                                    cfixOverlayPolicyMarketDataList = overlayPolicyFactory.createOverlayPolicyMarketDataTickerStructV4List(overlayPolicy, mdReqID);
                                    methodInstrumentor = FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().getInstance(new StringBuffer().append(targetCompID).append("/").append(sessionName).append("/").append(sessionClassStruct.sessionName).append("/").append(sessionClassStruct.classStruct.classSymbol).append("-").append(sessionClassStruct.classStruct.classKey).append("/Ticker").toString() , null);
                                    methodInstrumentor.setPrivate(true);
                                    cfixFixMDXMarketDataConsumerHolder = getCfixMDXMarketDataConsumerHolder(cachedCfixMarketDataConsumer, cfixOverlayPolicyMarketDataList, sessionClassStruct, methodInstrumentor);
                                    cfixMDXMarketDataQuery.subscribeTickerByClass(cfixFixMDXMarketDataConsumerHolder);
                                    unsubscribeArray[unsubscribeArrayLength++] = CfixMarketDataDispatcherIF.MarketDataType_TickerByClass;
                                }

                                continue;
                            }

                            sessionProductStruct = FixMarketDataRequestDecoder.buildSessionProductStruct(fixMarketDataRequestMessage, i, fixSession.getDebugFlags());
                            if (sessionProductStruct != null)
                            {
                                if (validateClassKey(mdReqID, cfixProductConfigurationService.getConfiguredTargetCompID(sessionProductStruct.productStruct.productKeys.classKey, targetCompID), targetCompID, cachedCfixMarketDataConsumer))
                                {
                                    // VivekB: For subscriptions to MDX - switched to using overlayPolicyFactory.createOverlayPolicyMarketDataTickerStructV4List - to use TickerStructV4 if needed
                                    cfixOverlayPolicyMarketDataList = overlayPolicyFactory.createOverlayPolicyMarketDataTickerStructV4List(overlayPolicy, mdReqID);
                                    methodInstrumentor = FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().getInstance(new StringBuffer().append(targetCompID).append("/").append(sessionName).append("/").append(sessionProductStruct.sessionName).append("/").append(sessionProductStruct.productStruct.productName.reportingClass).append("-").append(sessionProductStruct.productStruct.productKeys.classKey).append("/").append(sessionProductStruct.productStruct.productName.expirationDate.year).append(sessionProductStruct.productStruct.productName.expirationDate.month).append("_").append(sessionProductStruct.productStruct.productName.exercisePrice.whole).append(".").append(sessionProductStruct.productStruct.productName.exercisePrice.fraction).append("_").append(sessionProductStruct.productStruct.productName.optionType).append("-").append(sessionProductStruct.productStruct.productKeys.productKey).append("/").append("/Ticker").toString() , null);
                                    methodInstrumentor.setPrivate(true);
                                    cfixFixMDXMarketDataConsumerHolder = getCfixMDXMarketDataConsumerHolder(cachedCfixMarketDataConsumer, cfixOverlayPolicyMarketDataList, sessionProductStruct, methodInstrumentor);
                                    cfixMDXMarketDataQuery.subscribeTickerByProduct(cfixFixMDXMarketDataConsumerHolder);
                                    unsubscribeArray[unsubscribeArrayLength++] = CfixMarketDataDispatcherIF.MarketDataType_TickerByProduct;
                                }

                                continue;
                            }
                            break;

                        case CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice:
                            sessionClassStruct = FixMarketDataRequestDecoder.buildSessionClassStruct(fixMarketDataRequestMessage, i, fixSession.getDebugFlags());
                            if (sessionClassStruct != null)
                            {
                                if (validateClassKey(mdReqID, cfixProductConfigurationService.getConfiguredTargetCompID(sessionClassStruct.classStruct.classKey, targetCompID), targetCompID, cachedCfixMarketDataConsumer))
                                {
                                    cfixOverlayPolicyMarketDataList = overlayPolicyFactory.createOverlayPolicyMarketDataExpectedOpeningPriceStructList(overlayPolicy, mdReqID);
                                    methodInstrumentor = FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().getInstance(new StringBuffer().append(targetCompID).append("/").append(sessionName).append("/").append(sessionClassStruct.sessionName).append("/").append(sessionClassStruct.classStruct.classSymbol).append("-").append(sessionClassStruct.classStruct.classKey).append("/EOP").toString() , null);
                                    methodInstrumentor.setPrivate(true);
                                    cfixFixMDXMarketDataConsumerHolder = getCfixMDXMarketDataConsumerHolder(cachedCfixMarketDataConsumer, cfixOverlayPolicyMarketDataList, sessionClassStruct, methodInstrumentor);
                                    cfixMDXMarketDataQuery.subscribeExpectedOpeningPriceByClass(cfixFixMDXMarketDataConsumerHolder);
                                    unsubscribeArray[unsubscribeArrayLength++] = CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPriceByProduct;
                                }

                                continue;
                            }

                            sessionProductStruct = FixMarketDataRequestDecoder.buildSessionProductStruct(fixMarketDataRequestMessage, i, fixSession.getDebugFlags());
                            if (sessionProductStruct != null)
                            {
                                if (validateClassKey(mdReqID, cfixProductConfigurationService.getConfiguredTargetCompID(sessionProductStruct.productStruct.productKeys.classKey, targetCompID), targetCompID, cachedCfixMarketDataConsumer))
                                {
                                    cfixOverlayPolicyMarketDataList = overlayPolicyFactory.createOverlayPolicyMarketDataExpectedOpeningPriceStructList(overlayPolicy, mdReqID);
                                    methodInstrumentor = FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().getInstance(new StringBuffer().append(targetCompID).append("/").append(sessionName).append("/").append(sessionProductStruct.sessionName).append("/").append(sessionProductStruct.productStruct.productName.reportingClass).append("-").append(sessionProductStruct.productStruct.productKeys.classKey).append("/").append(sessionProductStruct.productStruct.productName.expirationDate.year).append(sessionProductStruct.productStruct.productName.expirationDate.month).append("_").append(sessionProductStruct.productStruct.productName.exercisePrice.whole).append(".").append(sessionProductStruct.productStruct.productName.exercisePrice.fraction).append("_").append(sessionProductStruct.productStruct.productName.optionType).append("-").append(sessionProductStruct.productStruct.productKeys.productKey).append("/").append("/EOP").toString() , null);
                                    methodInstrumentor.setPrivate(true);
                                    cfixFixMDXMarketDataConsumerHolder = getCfixMDXMarketDataConsumerHolder(cachedCfixMarketDataConsumer, cfixOverlayPolicyMarketDataList, sessionProductStruct, methodInstrumentor);
                                    cfixMDXMarketDataQuery.subscribeExpectedOpeningPriceByProduct(cfixFixMDXMarketDataConsumerHolder);
                                    unsubscribeArray[unsubscribeArrayLength++] = CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPriceByProduct;
                                }

                                continue;
                            }
                            break;
                    }

                    reject(cachedCfixMarketDataConsumer, FixMarketDataRejectStruct.UnknownSymbol, mdReqID, "Can't build a product or class from supplied information");
                }
            }
            catch (DataValidationException ex)
            {
                Log.exception(ex);
                exception = ex;
                exceptionMessage = ex.details.message;
            }
            catch (SystemException ex)
            {
                Log.exception(ex);
                exception = ex;
                exceptionMessage = ex.details.message;
            }
            catch (CommunicationException ex)
            {
                Log.exception(ex);
                exception = ex;
                exceptionMessage = ex.details.message;
            }
            catch (AuthorizationException ex)
            {
                Log.exception(ex);
                exception = ex;
                exceptionMessage = ex.details.message;
            }
            catch (NotFoundException ex)
            {
                Log.exception(ex);
                exception = ex;
                exceptionMessage = ex.details.message;
            }
            catch (NotAcceptedException ex)
            {
                Log.exception(ex);
                exception = ex;
                exceptionMessage = ex.details.message;
            }
            catch (AlreadyExistsException ex)
            {
                Log.exception(ex);
                exception = ex;
                exceptionMessage = ex.details.message;
            }
            catch (Exception ex)
            {
                Log.exception(ex);
                exception = ex;
                exceptionMessage = ex.getMessage();
            }
        }
        while (false);

        if (cancelAndUndo || fixSession.terminated())
        {
            if (Log.isDebugOn()) Log.debug(Thread.currentThread().getName() + " Future Starting Rollback");

            futureExecutionStatus = BitHelper.orBits(FutureExecutionIF.MAIN_STATE_FINISHED, FutureExecutionIF.CURRENT_STATE_UNDO);

            unsubscribeMdReqID(mdReqID, unsubscribeArray, unsubscribeArrayLength);

            cancelAndUndoLatch.release();

            if (Log.isDebugOn()) Log.debug(Thread.currentThread().getName() + " Future Ending Rollback (Finished|Undo)");

            return;
        }

        if (exception != null && !fixSession.terminated())
        {
            futureExecutionStatus = BitHelper.orBits(FutureExecutionIF.MAIN_STATE_FINISHED, FutureExecutionIF.CURRENT_STATE_ABORTED);

            if (exceptionMessage == null)
            {
                exceptionMessage = "";
            }

            reject(cachedCfixMarketDataConsumer, FixMarketDataRejectStruct.UnknownSymbol, mdReqID, "Can't build a product or class from supplied information (Aborted Due To Exception) ExceptionMessage(" + exceptionMessage.replace('\n',',').replace(FixFieldIF.SOHchar, '?') + ")");
        }

        futureExecutionStatus = BitHelper.orBits(FutureExecutionIF.MAIN_STATE_FINISHED, FutureExecutionIF.CURRENT_STATE_SUCCEEDED);

        cancelAndUndoLatch.release();

        if (Log.isDebugOn()) Log.debug(Thread.currentThread().getName() + " Future Ending (Finished|Succeeded)");
    }

    protected void reject(CfixMarketDataConsumer cfixFixMarketDataConsumer, char rejectReason, String mdReqID, String text)
    {
        try
        {
            cfixFixMarketDataConsumer.acceptMarketDataReject(new FixMarketDataRejectStruct(rejectReason, text, mdReqID));
        }
        catch (Exception ex)
        {
            Log.exception("Can't Send MarketDataReject Message RejectReason(" + rejectReason + ") mdReqID(" + mdReqID + ") text(" + text + ")", ex);
        }
    }

    protected void reject(CfixMarketDataConsumer cfixFixMarketDataConsumer, char rejectReason, String mdReqID, String text, String targetCompID)
    {
        try
        {
            cfixFixMarketDataConsumer.acceptMarketDataReject(new FixMarketDataRejectStruct(rejectReason, text, mdReqID, targetCompID));
        }
        catch (Exception ex)
        {
            Log.exception("Can't Send MarketDataReject Message RejectReason(" + rejectReason + ") mdReqID(" + mdReqID + ") text(" + text + ") targetCompID(" + targetCompID + ")", ex);
        }
    }

    protected void unsubscribeMdReqID(String mdReqID, int[] unsubscribeArray, int unsubscribeArraySize)
    {
        if (unsubscribeArray == null || unsubscribeArraySize < 1)
        {
            return;
        }

        for (int i = 0; i < unsubscribeArraySize; i++)
        {
            try
            {
                switch (BitHelper.clearBits(unsubscribeArray[i], CfixMarketDataDispatcherIF.MarketDataType_Descriptors))
                {
                    case CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket:        cfixMDXMarketDataQuery.unsubscribeCurrentMarket(mdReqID);        break;
                    case CfixMarketDataDispatcherIF.MarketDataType_Nbbo:                 cfixMDXMarketDataQuery.unsubscribeNbbo(mdReqID);                 break;
                    case CfixMarketDataDispatcherIF.MarketDataType_Recap:                cfixMDXMarketDataQuery.unsubscribeRecap(mdReqID);                break;
                    case CfixMarketDataDispatcherIF.MarketDataType_BookDepth:            cfixMDXMarketDataQuery.unsubscribeBookDepth(mdReqID);            break;
                    case CfixMarketDataDispatcherIF.MarketDataType_Ticker:               cfixMDXMarketDataQuery.unsubscribeTicker(mdReqID);               break;
                    case CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice: cfixMDXMarketDataQuery.unsubscribeExpectedOpeningPrice(mdReqID); break;
                }
            }
            catch (DataValidationException ex)
            {
                Log.exception(Thread.currentThread().getName() + " Exception Unsubscribing", ex);
            }
            catch (Exception ex)
            {

            }
        }
    }

    protected boolean validateClassKey(String mdReqID, String foundTargetCompID, String targetCompID, CfixMarketDataConsumer cachedCfixMarketDataConsumer)
    {
        if (targetCompID.equals(foundTargetCompID))
        {
            return true;
        }

        if (foundTargetCompID != null)
        {
            reject(cachedCfixMarketDataConsumer, FixMarketDataRejectStruct.UseSpecifiedEngine,      mdReqID, "Reroute to alternate Market Data Provider", foundTargetCompID);
        }
        else
        {
            reject(cachedCfixMarketDataConsumer, FixMarketDataRejectStruct.InsufficientPermissions, mdReqID, "No alternate Market Data Provider found");
        }

        return false;
    }

    protected CfixMDXMarketDataConsumerHolder getCfixMDXMarketDataConsumerHolder(CfixMarketDataConsumer cfixFixMarketDataConsumer, OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList, SessionProductStruct sessionProductStruct, MethodInstrumentor methodInstrumentor)
    {
        if (methodInstrumentor != null){
            return (new CfixMDXMarketDataConsumerHolderInstrumentedImpl(cfixFixMarketDataConsumer, cfixOverlayPolicyMarketDataList, sessionProductStruct, methodInstrumentor));
        }
        else
        {
            return (new CfixMDXMarketDataConsumerHolderImpl(cfixFixMarketDataConsumer, cfixOverlayPolicyMarketDataList, sessionProductStruct));
        }
    }

    protected CfixMDXMarketDataConsumerHolder getCfixMDXMarketDataConsumerHolder(CfixMarketDataConsumer cfixFixMarketDataConsumer, OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList, SessionClassStruct sessionClassStruct, MethodInstrumentor methodInstrumentor)
    {
        if (methodInstrumentor != null){
            return (new CfixMDXMarketDataConsumerHolderInstrumentedImpl(cfixFixMarketDataConsumer, cfixOverlayPolicyMarketDataList, sessionClassStruct, methodInstrumentor));
        }
        else
        {
            return (new CfixMDXMarketDataConsumerHolderImpl(cfixFixMarketDataConsumer, cfixOverlayPolicyMarketDataList, sessionClassStruct));
        }
    }
}
