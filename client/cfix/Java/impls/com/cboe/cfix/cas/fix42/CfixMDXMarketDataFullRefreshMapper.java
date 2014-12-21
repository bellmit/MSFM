package com.cboe.cfix.cas.fix42;

/**
 * CfixMarketDataFullRefreshMapper.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.util.*;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.cfix.cas.marketData.*;
import com.cboe.cfix.cas.shared.*;
import com.cboe.cfix.fix.fix42.generated.fields.*;
import com.cboe.cfix.fix.fix42.generated.messages.*;
import com.cboe.cfix.interfaces.*;
import com.cboe.client.util.*;
import com.cboe.client.util.collections.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiSession.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.interfaces.cfix.*;
import com.cboe.interfaces.domain.RecapContainerV4IF;
import com.cboe.util.*;

public class CfixMDXMarketDataFullRefreshMapper implements CfixMarketDataMapperIF
{
    public int                                 currentIndex;
    public int                                 maxIndex;
    public OverlayPolicyMarketDataHolderIF     cfixOverlayPolicyMarketDataHolder;
    public CfixSessionManager                  cfixSessionManager;
    public CfixStrikePriceHelper               cfixStrikePriceHelper;
    public CfixMarketDataMDReqIDHelper         cfixMarketDataMDReqIDHelper;
    public char[]                              mdReqIDAsChars;
    public final IntObjectMap                  productToSessionProductStructMap;
    public boolean                             handleUDF = false;

    protected static final MarketVolumeStructV4[]   cached_MarketVolumeStructArray_ZERO   = new MarketVolumeStructV4[]{new MarketVolumeStructV4((short) 0, 0, MultiplePartiesIndicators.NO)};
    protected static final ExchangeVolumeStruct[] cached_ExchangeVolumeStructArray_ZERO = new ExchangeVolumeStruct[]{new ExchangeVolumeStruct("W", 0)};

    public static final String W_MAINStr = "W_MAIN";
    public static final String ONE_MAINStr = "ONE_MAIN";
    public static final String EQUITYStr =  "Underlying";
    public static final String C2_MAINStr = "C2_MAIN";

    private boolean isSessionC2 = false;

    public CfixMDXMarketDataFullRefreshMapper()
    {
        productToSessionProductStructMap = new IntObjectMap(8192);
    }

    public CfixMDXMarketDataFullRefreshMapper(IntObjectMap productToSessionProductStructMap)
    {
        this.productToSessionProductStructMap = productToSessionProductStructMap;

    }

    public void initialize(CfixSessionManager cfixSessionManager, String propertyPrefix, Properties properties) throws Exception
    {
        this.cfixSessionManager = cfixSessionManager;

        cfixStrikePriceHelper   = CfixServicesHelper.getCfixStrikePriceHelperHome().find();

        isSessionC2 = CfixServicesHelper.getSessionC2();
    }

    public void reset(OverlayPolicyMarketDataHolderIF cfixOverlayPolicyMarketDataHolder, CfixMarketDataMDReqIDHelper cfixMarketDataMDReqIDHelper) throws Exception
    {
        this.cfixOverlayPolicyMarketDataHolder = cfixOverlayPolicyMarketDataHolder;
        this.currentIndex                      = 0;
        this.maxIndex                          = cfixOverlayPolicyMarketDataHolder.size();
        this.cfixMarketDataMDReqIDHelper       = cfixMarketDataMDReqIDHelper;
        this.mdReqIDAsChars                    = StringHelper.stringGetChars(cfixOverlayPolicyMarketDataHolder.getMdReqID());
    }

    public void setUDFSupportIndicator(char udfSupportIndicator)
    {
        switch (udfSupportIndicator)
        {
            case FixCboeUDFSupportIndicatorField.SupportUDFInRepeatingGroupsInAllMessages:
            case FixCboeUDFSupportIndicatorField.SupportUDFInRepeatingGroupsInMarketDataMessage:
                handleUDF = true;
                break;
            default:
                handleUDF = false;
                break;
        }
        if (Log.isDebugOn()){Log.debug("CfixMDXMarketDataFullRefreshMapper -> setUDFSupportIndicator : handleUDF is set to " + handleUDF);}
    }

    public char[] getMsgTypeAsChars()
    {
        return FixMarketDataSnapshotFullRefreshMessage.MsgTypeAsChars;
    }

    public boolean build(FixMessageBuilderIF fixMessageBuilder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, AlreadyExistsException
    {
        if (currentIndex == maxIndex)
        {
            return false;
        }

        //        if (cfixOverlayPolicyMarketDataHolder.getOverlaid().timesChanged(currentIndex) > 1)
        //        {
        //            Log.information(Thread.currentThread().getName() + " XXX Overlaid: " + cfixOverlayPolicyMarketDataHolder.getOverlaid().timesChanged(currentIndex));
        //        }

        switch (cfixOverlayPolicyMarketDataHolder.getMarketDataType())
        {
            case CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket:
                fixMessageBuilder.append(FixMDReqIDField.TagIDAsChars, mdReqIDAsChars);
                mapMarketData(fixMessageBuilder, cfixOverlayPolicyMarketDataHolder.getCurrentMarketStructV4(currentIndex));
                if (cfixOverlayPolicyMarketDataHolder.getOverlaid().timesChanged(currentIndex) > 1)
                {
                    fixMessageBuilder.append(FixCboeApplicationQueueActionTakenField.taggedchars_OverlaidLast);
                }
                break;

            case CfixMarketDataDispatcherIF.MarketDataType_Nbbo:
                fixMessageBuilder.append(FixMDReqIDField.TagIDAsChars, mdReqIDAsChars);
                mapMarketData(fixMessageBuilder, cfixOverlayPolicyMarketDataHolder.getNbboStruct(currentIndex));
                if (cfixOverlayPolicyMarketDataHolder.getOverlaid().timesChanged(currentIndex) > 1)
                {
                    fixMessageBuilder.append(FixCboeApplicationQueueActionTakenField.taggedchars_OverlaidLast);
                }
                break;

            case CfixMarketDataDispatcherIF.MarketDataType_Recap:
                fixMessageBuilder.append(FixMDReqIDField.TagIDAsChars, mdReqIDAsChars);
                //mapMarketData(fixMessageBuilder, cfixOverlayPolicyMarketDataHolder.getRecapStruct(currentIndex));
                mapMarketData(fixMessageBuilder, cfixOverlayPolicyMarketDataHolder.getRecapContainerV4(currentIndex));
                if (cfixOverlayPolicyMarketDataHolder.getOverlaid().timesChanged(currentIndex) > 1)
                {
                    fixMessageBuilder.append(FixCboeApplicationQueueActionTakenField.taggedchars_OverlaidLast);
                }
                break;

            case CfixMarketDataDispatcherIF.MarketDataType_BookDepth:
                fixMessageBuilder.append(FixMDReqIDField.TagIDAsChars, mdReqIDAsChars);
                mapMarketData(fixMessageBuilder, cfixOverlayPolicyMarketDataHolder.getBookDepthStruct(currentIndex));
                if (cfixOverlayPolicyMarketDataHolder.getOverlaid().timesChanged(currentIndex) > 1)
                {
                    fixMessageBuilder.append(FixCboeApplicationQueueActionTakenField.taggedchars_OverlaidLast);
                }
                break;

            case CfixMarketDataDispatcherIF.MarketDataType_BookDepthUpdate:
                fixMessageBuilder.append(FixMDReqIDField.TagIDAsChars, mdReqIDAsChars);
                mapMarketData(fixMessageBuilder, cfixOverlayPolicyMarketDataHolder.getBookDepthStruct(currentIndex));
                if (cfixOverlayPolicyMarketDataHolder.getOverlaid().timesChanged(currentIndex) > 1)
                {
                    fixMessageBuilder.append(FixCboeApplicationQueueActionTakenField.taggedchars_OverlaidLast);
                }
                break;

            case CfixMarketDataDispatcherIF.MarketDataType_Ticker:
                fixMessageBuilder.append(FixMDReqIDField.TagIDAsChars, mdReqIDAsChars);
                //mapMarketData(fixMessageBuilder, cfixOverlayPolicyMarketDataHolder.getTickerStruct(currentIndex));
                mapMarketData(fixMessageBuilder, cfixOverlayPolicyMarketDataHolder.getTickerStructV4(currentIndex));
                if (cfixOverlayPolicyMarketDataHolder.getOverlaid().timesChanged(currentIndex) > 1)
                {
                    fixMessageBuilder.append(FixCboeApplicationQueueActionTakenField.taggedchars_OverlaidLast);
                }
                break;

            case CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice:
                fixMessageBuilder.append(FixMDReqIDField.TagIDAsChars, mdReqIDAsChars);
                mapMarketData(fixMessageBuilder, cfixOverlayPolicyMarketDataHolder.getExpectedOpeningPriceStruct(currentIndex));
                if (cfixOverlayPolicyMarketDataHolder.getOverlaid().timesChanged(currentIndex) > 1)
                {
                    fixMessageBuilder.append(FixCboeApplicationQueueActionTakenField.taggedchars_OverlaidLast);
                }
                break;

            default:
                Log.alarm(Thread.currentThread().getName() + " MISUNDERSTOOD cfixFixMarketDataConsumerPacket.getMarketDataType(" + cfixOverlayPolicyMarketDataHolder.getMarketDataType() + ")");
                return false;
        }

        ++currentIndex;

        return true;
    }

    protected void buildCommonHeader(FixMessageBuilderIF fixMessageBuilder, String symbol, ProductNameStruct productName, ProductKeysStruct productKeys) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, AlreadyExistsException
    {
        fixMessageBuilder.append(FixSymbolField.TagIDAsChars, symbol);

        fixMessageBuilder.append(FixSecurityIDField.TagIDAsChars, productKeys.productKey);

        fixMessageBuilder.append(FixIDSourceField.taggedchars_ExchangeSymbol);

        CmiFixMapper.mapCmiToFixSecurityType(fixMessageBuilder, productKeys.productType);

        switch (productKeys.productType)
        {
            case ProductTypes.OPTION:
                CmiFixMapper.mapCmiToFixMaturityMonthYear(fixMessageBuilder, productName.expirationDate);
                CmiFixMapper.mapCmiToFixMaturityDay(fixMessageBuilder,       productName.expirationDate);
                CmiFixMapper.mapCmiToFixPutOrCall(fixMessageBuilder,         productName.optionType);
                fixMessageBuilder.append(FixStrikePriceField.TagIDAsChars,   cfixStrikePriceHelper.adjustStrikePrice(symbol, productName.exercisePrice));
                break;
            case ProductTypes.FUTURE:
                CmiFixMapper.mapCmiToFixMaturityMonthYear(fixMessageBuilder, productName.expirationDate);
                CmiFixMapper.mapCmiToFixMaturityDay(fixMessageBuilder,       productName.expirationDate);
                break;
            case ProductTypes.STRATEGY:
                CmiFixMapper.mapCmiToFixSecurityDesc(fixMessageBuilder,      (ServicesHelper.getProductQueryServiceAdapter().getStrategyByKey(productKeys.productKey)).strategyType);
                break;
            case ProductTypes.EQUITY:
                //DO WE NEED ANYTHING FOR THIS???
                break;
        }

        fixMessageBuilder.append(FixSecurityExchangeField.taggedchars_Cboe);
    }

    protected SessionProductStruct getSessionProductStruct(String sessionName, ProductKeysStruct productKeyStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionProductStruct sessionProductStruct = (SessionProductStruct) productToSessionProductStructMap.getValueForKey(productKeyStruct.productKey);
        if (sessionProductStruct == null)
        {
            sessionProductStruct = SessionProductStructCache.getSessionProductStruct(sessionName, productKeyStruct.productKey);
            if (sessionProductStruct == null)
            {
                throw ExceptionBuilder.dataValidationException("Can't Build Since No Detail Is Available For ProductKey(" + productKeyStruct.productKey + ") In Session(" + sessionName + ")", 0);
            }

            SessionProductStruct sessionProductStructs[] = SessionProductStructCache.getSessionProductStructsFromClassKey(sessionName, productKeyStruct.classKey);
            if (sessionProductStructs != null)
            {
                for (int i = 0; i < sessionProductStructs.length; i++)
                {
                    productToSessionProductStructMap.putKeyValue(sessionProductStructs[i].productStruct.productKeys.classKey, sessionProductStructs[i]);
                }
            }

            productToSessionProductStructMap.putKeyValue(productKeyStruct.productKey, sessionProductStruct);
        }

        return sessionProductStruct;
    }

    protected SessionProductStruct getSessionProductStruct(String sessionName, CurrentMarketStructV4 currentMarketStructV4) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionProductStruct sessionProductStruct = (SessionProductStruct) productToSessionProductStructMap.getValueForKey(currentMarketStructV4.productKey);

        if (sessionProductStruct == null)
        {
            sessionProductStruct = SessionProductStructCache.getSessionProductStruct(sessionName, currentMarketStructV4.productKey);
            if (sessionProductStruct == null)
            {
                throw ExceptionBuilder.dataValidationException("Can't Build Since No Detail Is Available For ProductKey(" + currentMarketStructV4.productKey + ") In Session(" + sessionName + ")", 0);
            }

            SessionProductStruct sessionProductStructs[] = SessionProductStructCache.getSessionProductStructsFromClassKey(sessionName, currentMarketStructV4.classKey);
            if (sessionProductStructs != null)
            {
                for (int i = 0; i < sessionProductStructs.length; i++)
                {
                    productToSessionProductStructMap.putKeyValue(sessionProductStructs[i].productStruct.productKeys.classKey, sessionProductStructs[i]);
                }
            }

            productToSessionProductStructMap.putKeyValue(currentMarketStructV4.productKey, sessionProductStruct);
        }

        return sessionProductStruct;
    }

    protected SessionProductStruct getSessionProductStruct(String sessionName, TickerStructV4 tickerStructV4) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionProductStruct sessionProductStruct = (SessionProductStruct) productToSessionProductStructMap.getValueForKey(tickerStructV4.productKey);

        if (sessionProductStruct == null)
        {
            sessionProductStruct = SessionProductStructCache.getSessionProductStruct(sessionName, tickerStructV4.productKey);
            if (sessionProductStruct == null)
            {
                throw ExceptionBuilder.dataValidationException("Can't Build Since No Detail Is Available For ProductKey(" + tickerStructV4.productKey + ") In Session(" + sessionName + ")", 0);
            }

            SessionProductStruct sessionProductStructs[] = SessionProductStructCache.getSessionProductStructsFromClassKey(sessionName, tickerStructV4.classKey);
            if (sessionProductStructs != null)
            {
                for (int i = 0; i < sessionProductStructs.length; i++)
                {
                    productToSessionProductStructMap.putKeyValue(sessionProductStructs[i].productStruct.productKeys.classKey, sessionProductStructs[i]);
                }
            }

            productToSessionProductStructMap.putKeyValue(tickerStructV4.productKey, sessionProductStruct);
        }

        return sessionProductStruct;
    }

    protected SessionProductStruct getSessionProductStruct(String sessionName, int productKey, int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionProductStruct sessionProductStruct = (SessionProductStruct) productToSessionProductStructMap.getValueForKey(productKey);

        if (sessionProductStruct == null)
        {
            sessionProductStruct = SessionProductStructCache.getSessionProductStruct(sessionName, productKey);
            if (sessionProductStruct == null)
            {
                throw ExceptionBuilder.dataValidationException("Can't Build Since No Detail Is Available For ProductKey(" + productKey + ") In Session(" + sessionName + ")", 0);
            }

            SessionProductStruct sessionProductStructs[] = SessionProductStructCache.getSessionProductStructsFromClassKey(sessionName, classKey);
            if (sessionProductStructs != null)
            {
                for (int i = 0; i < sessionProductStructs.length; i++)
                {
                    productToSessionProductStructMap.putKeyValue(sessionProductStructs[i].productStruct.productKeys.classKey, sessionProductStructs[i]);
                }
            }

            productToSessionProductStructMap.putKeyValue(productKey, sessionProductStruct);
        }

        return sessionProductStruct;
    }

    protected void mapMarketData(FixMessageBuilderIF fixMessageBuilder, CurrentMarketStructV4 currentMarketStructV4) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, AlreadyExistsException
    {
        String sessionName;
        //todo VivekB: verify that this is enough. Do we even trade Futures on C2?
        if (this.isSessionC2)
        {
            switch (currentMarketStructV4.productType)
            {
                case ProductTypes.OPTION:
                    sessionName = C2_MAINStr;
                    break;
                case ProductTypes.FUTURE:
                    sessionName = ONE_MAINStr;
                    break;
                case ProductTypes.STRATEGY:
                    sessionName = C2_MAINStr;
                    break;
                case ProductTypes.EQUITY:
                    sessionName = EQUITYStr;
                    break;
                case ProductTypes.INDEX:
                    sessionName = C2_MAINStr;
                    break;
                default:
                    sessionName = C2_MAINStr;
                    break;
            }
        }
        else {
            switch (currentMarketStructV4.productType)
            {
                case ProductTypes.OPTION:
                    sessionName = W_MAINStr;
                    break;
                case ProductTypes.FUTURE:
                    sessionName = ONE_MAINStr;
                    break;
                case ProductTypes.STRATEGY:
                    sessionName = W_MAINStr;
                    break;
                case ProductTypes.EQUITY:
                    sessionName = EQUITYStr;
                    break;
                case ProductTypes.INDEX:
                    sessionName = W_MAINStr;
                    break;
                default:
                    sessionName = W_MAINStr;
                    break;
            }

        }



        SessionProductStruct sessionProductStruct = getSessionProductStruct(sessionName, currentMarketStructV4);

        buildCommonHeader(fixMessageBuilder, sessionProductStruct.productStruct.productName.reportingClass, sessionProductStruct.productStruct.productName, sessionProductStruct.productStruct.productKeys);

        MarketVolumeStructV4[] bidSizeSequenceV4;
        MarketVolumeStructV4[] askSizeSequenceV4;
        boolean              sentSessionName = false;

        /// If there is no CurrentMarket, we may get back a CurrentMarketStruct that has an empty bidSizeSequence and/or an empty askSizeSequence.
        //  For this, we want to build up the appropriate struct(s) with zero volume so we can get this information back to the requesting party.
        if (currentMarketStructV4.bidSizeSequence.length == 0)
        {
            bidSizeSequenceV4 = cached_MarketVolumeStructArray_ZERO;
        }
        else
        {
            bidSizeSequenceV4 = currentMarketStructV4.bidSizeSequence;
        }

        if (currentMarketStructV4.askSizeSequence.length == 0)
        {
            askSizeSequenceV4 = cached_MarketVolumeStructArray_ZERO;
        }
        else
        {
            askSizeSequenceV4 = currentMarketStructV4.askSizeSequence;
        }

        fixMessageBuilder.append(FixNoMDEntriesField.TagIDAsChars, calculateAdjustedSequenceSize(bidSizeSequenceV4) + calculateAdjustedSequenceSize(askSizeSequenceV4));

        if (Log.isDebugOn()){Log.debug("CfixMDXMarketDataFullRefreshMapper -> mapMarketData : mapping CM : length of bidSizeSequenceV4 " + bidSizeSequenceV4.length);}
        for (int sequenceIndex = 0; sequenceIndex < bidSizeSequenceV4.length; sequenceIndex++)
        {
            // if can't handle UDF messages, then skip CUSTOMER and PROFESSIONAL completely
            if (Log.isDebugOn()){Log.debug("CfixMDXMarketDataFullRefreshMapper -> mapMarketData : mapping CM : volumeType is " + bidSizeSequenceV4[sequenceIndex].volumeType);}
            switch (bidSizeSequenceV4[sequenceIndex].volumeType)
            {
                case VolumeTypes.CUSTOMER_ORDER:
                case VolumeTypes.PROFESSIONAL_ORDER:
                    if (!handleUDF)
                    {
                        continue;
                    }
                    break;
            }

            fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_Bid);

            if (currentMarketStructV4.bidPrice == PriceConstants.NO_PRICE)
                fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars, 0);
            else
                fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars, currentMarketStructV4.bidPrice, currentMarketStructV4.priceScale);

            sentSessionName = fillInCurrentMarketStructData(fixMessageBuilder, currentMarketStructV4, bidSizeSequenceV4[sequenceIndex], sentSessionName, sessionName, sequenceIndex == 0);
        }

        for (int sequenceIndex = 0; sequenceIndex < askSizeSequenceV4.length; sequenceIndex++)
        {
            // if can't handle UDF messages, then skip CUSTOMER and PROFESSIONAL completely
            switch (askSizeSequenceV4[sequenceIndex].volumeType)
            {
                case VolumeTypes.CUSTOMER_ORDER:
                case VolumeTypes.PROFESSIONAL_ORDER:
                    if (!handleUDF)
                    {
                        continue;
                    }
                    break;
            }

            fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_Offer);

            if (currentMarketStructV4.askPrice == PriceConstants.NO_PRICE)
                fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars, 0);
            else
                fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars, currentMarketStructV4.askPrice, currentMarketStructV4.priceScale);

            sentSessionName = fillInCurrentMarketStructData(fixMessageBuilder, currentMarketStructV4, askSizeSequenceV4[sequenceIndex], sentSessionName, sessionName, sequenceIndex == 0);
        }
    }

    protected void mapMarketData(FixMessageBuilderIF fixMessageBuilder, BookDepthStruct bookDepthStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = getSessionProductStruct(bookDepthStruct.sessionName, bookDepthStruct.productKeys);

        buildCommonHeader(fixMessageBuilder, sessionProductStruct.productStruct.productName.reportingClass, sessionProductStruct.productStruct.productName, sessionProductStruct.productStruct.productKeys);

        int NumberEntriesPerSide = 1; // Each entry in the book depth side may have multiple entries per side depending on business rules. At this time we are only going to have one entry per side.

        int noMDEntries = NumberEntriesPerSide * (bookDepthStruct.buySideSequence.length + bookDepthStruct.sellSideSequence.length);

        if (noMDEntries == 0)
        {
            fixMessageBuilder.append(FixNoMDEntriesField.TagIDAsChars, 2);

            fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_Bid);
            fixMessageBuilder.append(FixMDEntryPxField.taggedchars_value0);
            fixMessageBuilder.append(FixMDEntrySizeField.taggedchars_value0);
            // Initialize the TradingSessionID in only the first repeating group
            fixMessageBuilder.append(FixTradingSessionIDField.TagIDAsChars,  bookDepthStruct.sessionName);

            fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_Offer);
            fixMessageBuilder.append(FixMDEntryPxField.taggedchars_value0);
            fixMessageBuilder.append(FixMDEntrySizeField.taggedchars_value0);

            return;
        }

        fixMessageBuilder.append(FixNoMDEntriesField.TagIDAsChars, noMDEntries);

        for (int sequenceIndex = 0; sequenceIndex < bookDepthStruct.buySideSequence.length; sequenceIndex++)
        {
            fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_Bid);
            fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars,     bookDepthStruct.buySideSequence[sequenceIndex].price);
            fixMessageBuilder.append(FixMDEntrySizeField.TagIDAsChars,   bookDepthStruct.buySideSequence[sequenceIndex].totalVolume);
            fixMessageBuilder.append(FixTextField.TagIDAsChars,          bookDepthStruct.buySideSequence[sequenceIndex].contingencyVolume);

            if (sequenceIndex == 0)
            {
                // Initialize the TradingSessionID in only the first repeating group
                fixMessageBuilder.append(FixTradingSessionIDField.TagIDAsChars,  bookDepthStruct.sessionName);
            }
        }

        for (int sequenceIndex = 0; sequenceIndex < bookDepthStruct.sellSideSequence.length; sequenceIndex++)
        {
            fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_Offer);
            fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars,     bookDepthStruct.sellSideSequence[sequenceIndex].price);
            fixMessageBuilder.append(FixMDEntrySizeField.TagIDAsChars,   bookDepthStruct.sellSideSequence[sequenceIndex].totalVolume);
            fixMessageBuilder.append(FixTextField.TagIDAsChars,          bookDepthStruct.sellSideSequence[sequenceIndex].contingencyVolume);
        }
    }

    protected void mapMarketData(FixMessageBuilderIF fixMessageBuilder, ExpectedOpeningPriceStruct expectedOpeningPriceStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = getSessionProductStruct(expectedOpeningPriceStruct.sessionName, expectedOpeningPriceStruct.productKeys);

        buildCommonHeader(fixMessageBuilder, sessionProductStruct.productStruct.productName.reportingClass, sessionProductStruct.productStruct.productName, sessionProductStruct.productStruct.productKeys);

        fixMessageBuilder.append(FixNoMDEntriesField.taggedchars_value1);
        fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_OpeningPrice);

        fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars, cfixStrikePriceHelper.adjustStrikePrice(sessionProductStruct.productStruct.productName.reportingClass, expectedOpeningPriceStruct.expectedOpeningPrice));

        fixMessageBuilder.append(FixMDEntrySizeField.TagIDAsChars, expectedOpeningPriceStruct.imbalanceQuantity);

        fixMessageBuilder.append(FixTradingSessionIDField.TagIDAsChars, expectedOpeningPriceStruct.sessionName);

        CmiFixMapper.mapCmiToFixTradeConditionEOP(fixMessageBuilder, expectedOpeningPriceStruct.eopType);

        fixMessageBuilder.append(FixOpenCloseSettleFlagField.taggedchars_ExpectedOpen);

	    if (expectedOpeningPriceStruct.legalMarket)
	    {
            fixMessageBuilder.append(FixCboeLegalMarketField.taggedchars_YesLegal);
	    }
	    else
	    {
            fixMessageBuilder.append(FixCboeLegalMarketField.taggedchars_NotLegal);
	    }
    }

    protected void mapMarketData(FixMessageBuilderIF fixMessageBuilder, NBBOStruct nbboStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = getSessionProductStruct(nbboStruct.sessionName, nbboStruct.productKeys);

        buildCommonHeader(fixMessageBuilder, sessionProductStruct.productStruct.productName.reportingClass, sessionProductStruct.productStruct.productName, sessionProductStruct.productStruct.productKeys);

        ExchangeVolumeStruct[] bidExchangeVolume;
        ExchangeVolumeStruct[] askExchangeVolume;

        // If there is no NBBO, we may get back a NBBOStruct that has an empty bidExchangeVolume and/or an empty askExchangeVolume.
        // For this, we want to build up the appropriate struct(s) with zero volume so we can get this information back to the requesting party.
        if (nbboStruct.bidExchangeVolume.length == 0)
        {
            bidExchangeVolume = cached_ExchangeVolumeStructArray_ZERO;
        }
        else
        {
            bidExchangeVolume = nbboStruct.bidExchangeVolume;
        }

        if (nbboStruct.askExchangeVolume.length == 0)
        {
            askExchangeVolume = cached_ExchangeVolumeStructArray_ZERO;
        }
        else
        {
            askExchangeVolume = nbboStruct.askExchangeVolume;
        }

        fixMessageBuilder.append(FixNoMDEntriesField.TagIDAsChars, askExchangeVolume.length + bidExchangeVolume.length);

        for (int sequenceIndex = 0; sequenceIndex < bidExchangeVolume.length; sequenceIndex++)
        {
            fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_Bid);
            fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars,                                nbboStruct.bidPrice);
            fixMessageBuilder.append(FixMDEntrySizeField.TagIDAsChars,                              bidExchangeVolume[sequenceIndex].volume);
            fixMessageBuilder.appendDateInFixUTCTimeOnlyFormat(FixMDEntryTimeField.TagIDAsChars,    nbboStruct.sentTime);
            fixMessageBuilder.append(FixTradingSessionIDField.TagIDAsChars,                         nbboStruct.sessionName);
            fixMessageBuilder.append(FixQuoteConditionField.taggedchars_ConsolidatedBest);

            CmiFixMapper.mapCmiToFixMDMkt(fixMessageBuilder, nbboStruct.bidExchangeVolume[sequenceIndex].exchange);
        }

        for (int sequenceIndex = 0; sequenceIndex < askExchangeVolume.length; sequenceIndex++)
        {
            fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_Offer);
            fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars,                                nbboStruct.askPrice);
            fixMessageBuilder.append(FixMDEntrySizeField.TagIDAsChars,                              askExchangeVolume[sequenceIndex].volume);
            fixMessageBuilder.appendDateInFixUTCTimeOnlyFormat(FixMDEntryTimeField.TagIDAsChars,    nbboStruct.sentTime);
            fixMessageBuilder.append(FixTradingSessionIDField.TagIDAsChars,                         nbboStruct.sessionName);
            fixMessageBuilder.append(FixQuoteConditionField.taggedchars_ConsolidatedBest);

            CmiFixMapper.mapCmiToFixMDMkt(fixMessageBuilder, nbboStruct.askExchangeVolume[sequenceIndex].exchange);
        }
    }

    protected void mapMarketData(FixMessageBuilderIF fixMessageBuilder, RecapStruct recapStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = getSessionProductStruct(recapStruct.sessionName, recapStruct.productKeys);

        buildCommonHeader(fixMessageBuilder, sessionProductStruct.productStruct.productName.reportingClass, sessionProductStruct.productStruct.productName, sessionProductStruct.productStruct.productKeys);

        fixMessageBuilder.append(FixNoMDEntriesField.TagIDAsChars, '8');

        //last sale + populate TradingSessionID (only populated in the first entry)
        fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_Trade);
        fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars,                                recapStruct.lastSalePrice);
        fixMessageBuilder.append(FixMDEntrySizeField.TagIDAsChars,                              recapStruct.lastSaleVolume);
        fixMessageBuilder.appendDateInFixUTCTimeOnlyFormat(FixMDEntryTimeField.TagIDAsChars,    recapStruct.tradeTime);
        CmiFixMapper.mapCmiToFixTickDirection(fixMessageBuilder,                                recapStruct.tickDirection);

        fixMessageBuilder.append(FixTradingSessionIDField.TagIDAsChars,                         recapStruct.sessionName);
        CmiFixMapper.mapCmiToFixTradeCondition(fixMessageBuilder,                               recapStruct.recapPrefix);

        //bid price
        fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_Bid);
        fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars,                                recapStruct.bidPrice);
        fixMessageBuilder.append(FixMDEntrySizeField.TagIDAsChars,                              recapStruct.bidSize);
        fixMessageBuilder.appendDateInFixUTCTimeOnlyFormat(FixMDEntryTimeField.TagIDAsChars,    recapStruct.bidTime);

        //ask price
        fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_Offer);
        fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars,                                recapStruct.askPrice);
        fixMessageBuilder.append(FixMDEntrySizeField.TagIDAsChars,                              recapStruct.askSize);
        fixMessageBuilder.appendDateInFixUTCTimeOnlyFormat(FixMDEntryTimeField.TagIDAsChars,    recapStruct.askTime);

        //low price
        fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_TradingSessionLowPrice);
        fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars,                                recapStruct.lowPrice);
        fixMessageBuilder.append(FixMDEntrySizeField.taggedchars_value0);

        //high price
        fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_TradingSessionHighPrice);
        fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars,                                recapStruct.highPrice);
        fixMessageBuilder.append(FixMDEntrySizeField.taggedchars_value0);

        //open price
        fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_OpeningPrice);
        fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars,                                recapStruct.openPrice);
        fixMessageBuilder.append(FixMDEntrySizeField.taggedchars_value0);

        //close price
        fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_ClosingPrice);
        fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars,                                recapStruct.closePrice);
        fixMessageBuilder.append(FixMDEntrySizeField.taggedchars_value0);

        //previous close
        fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_SettlementPrice);
        fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars,                                recapStruct.previousClosePrice);
        fixMessageBuilder.append(FixMDEntrySizeField.taggedchars_value0);

        fixMessageBuilder.append(FixCboeOpenInterestField.TagIDAsChars,                         recapStruct.openInterest);
    }

    protected void mapMarketData(FixMessageBuilderIF fixMessageBuilder, RecapContainerV4IF recapContainerV4) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = getSessionProductStruct(recapContainerV4.getSessionName(), recapContainerV4.getProductKey(), recapContainerV4.getClassKey());

        buildCommonHeader(fixMessageBuilder, sessionProductStruct.productStruct.productName.reportingClass, sessionProductStruct.productStruct.productName, sessionProductStruct.productStruct.productKeys);

        fixMessageBuilder.append(FixNoMDEntriesField.TagIDAsChars, '8');

        //last sale + populate TradingSessionID (only populated in the first entry)
        fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_Trade);
        fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars,                                recapContainerV4.getLastSalePrice());
        fixMessageBuilder.append(FixMDEntrySizeField.TagIDAsChars,                              recapContainerV4.getLastSaleVolume());
        fixMessageBuilder.append(FixMDEntryTimeField.TagIDAsChars,                              recapContainerV4.getTradeTime());
        CmiFixMapper.mapCmiToFixTickDirection(fixMessageBuilder,                                recapContainerV4.getTickDirection());

        fixMessageBuilder.append(FixTradingSessionIDField.TagIDAsChars,                         recapContainerV4.getSessionName());

        CmiFixMapper.mapCmiToFixTradeCondition(fixMessageBuilder,                               recapContainerV4.getRecapPrefix());

        //bid price
        fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_Bid);
        fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars,                                recapContainerV4.getBidPrice());
        fixMessageBuilder.append(FixMDEntrySizeField.TagIDAsChars,                              recapContainerV4.getBidSize());
        fixMessageBuilder.append(FixMDEntryTimeField.TagIDAsChars,                              recapContainerV4.getBidTime());

        //ask price
        fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_Offer);
        fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars,                                recapContainerV4.getAskPrice());
        fixMessageBuilder.append(FixMDEntrySizeField.TagIDAsChars,                              recapContainerV4.getAskSize());
        fixMessageBuilder.append(FixMDEntryTimeField.TagIDAsChars,                              recapContainerV4.getAskTime());

        //low price
        fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_TradingSessionLowPrice);
        fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars,                                recapContainerV4.getLowPrice());
        fixMessageBuilder.append(FixMDEntrySizeField.taggedchars_value0);

        //high price
        fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_TradingSessionHighPrice);
        fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars,                                recapContainerV4.getHighPrice());
        fixMessageBuilder.append(FixMDEntrySizeField.taggedchars_value0);

        //open price
        fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_OpeningPrice);
        fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars,                                recapContainerV4.getOpenPrice());
        fixMessageBuilder.append(FixMDEntrySizeField.taggedchars_value0);

        //close price
        fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_ClosingPrice);
        fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars,                                recapContainerV4.getClosePrice());
        fixMessageBuilder.append(FixMDEntrySizeField.taggedchars_value0);

        //previous close
        fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_SettlementPrice);
        fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars,                                recapContainerV4.getPreviousClosePrice());
        fixMessageBuilder.append(FixMDEntrySizeField.taggedchars_value0);

        // Open Interest is not supported by V4
        if (recapContainerV4.getOpenInterest() != -1)
            fixMessageBuilder.append(FixCboeOpenInterestField.TagIDAsChars,                         recapContainerV4.getOpenInterest());
    }

    protected void mapMarketData(FixMessageBuilderIF fixMessageBuilder, TickerStruct tickerStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = getSessionProductStruct(tickerStruct.sessionName, tickerStruct.productKeys);

        buildCommonHeader(fixMessageBuilder, sessionProductStruct.productStruct.productName.reportingClass, sessionProductStruct.productStruct.productName, sessionProductStruct.productStruct.productKeys);

        fixMessageBuilder.append(FixNoMDEntriesField.taggedchars_value1);
        fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_Trade);

        fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars,         tickerStruct.lastSalePrice);
        fixMessageBuilder.append(FixMDEntrySizeField.TagIDAsChars,       tickerStruct.lastSaleVolume);
        fixMessageBuilder.append(FixTradingSessionIDField.TagIDAsChars,  tickerStruct.sessionName);
        CmiFixMapper.mapCmiToFixTradeCondition(fixMessageBuilder,        tickerStruct.salePrefix);
        if (tickerStruct.salePostfix.length() > 0)
        {
            fixMessageBuilder.append(FixTextField.TagIDAsChars,          tickerStruct.salePostfix);
        }
    }

    protected void mapMarketData(FixMessageBuilderIF fixMessageBuilder, TickerStructV4 tickerStructV4) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, AlreadyExistsException
    {
        String sessionName;
        if (this.isSessionC2)
        {
            switch (tickerStructV4.productType)
            {
                case ProductTypes.OPTION:
                    sessionName = C2_MAINStr;
                    break;
                case ProductTypes.FUTURE:
                    sessionName = ONE_MAINStr;
                    break;
                case ProductTypes.STRATEGY:
                    sessionName = C2_MAINStr;
                    break;
                case ProductTypes.EQUITY:
                    sessionName = EQUITYStr;
                    break;
                case ProductTypes.INDEX:
                    sessionName = C2_MAINStr;
                    break;
                default:
                    sessionName = C2_MAINStr;
                    break;
            }
        }
        else {
            switch (tickerStructV4.productType)
            {
                case ProductTypes.OPTION:
                    sessionName = W_MAINStr;
                    break;
                case ProductTypes.FUTURE:
                    sessionName = ONE_MAINStr;
                    break;
                case ProductTypes.STRATEGY:
                    sessionName = W_MAINStr;
                    break;
                case ProductTypes.EQUITY:
                    sessionName = EQUITYStr;
                    break;
                case ProductTypes.INDEX:
                    sessionName = W_MAINStr;
                    break;
                default:
                    sessionName = W_MAINStr;
                    break;
            }

        }

        SessionProductStruct sessionProductStruct = getSessionProductStruct(sessionName, tickerStructV4);

        buildCommonHeader(fixMessageBuilder, sessionProductStruct.productStruct.productName.reportingClass, sessionProductStruct.productStruct.productName, sessionProductStruct.productStruct.productKeys);

        fixMessageBuilder.append(FixNoMDEntriesField.taggedchars_value1);
        fixMessageBuilder.append(FixMDEntryTypeField.taggedchars_Trade);
        if (tickerStructV4.tradePrice == PriceConstants.NO_PRICE)
            fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars, 0);
        else
            fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars, tickerStructV4.tradePrice, tickerStructV4.priceScale);
        fixMessageBuilder.append(FixMDEntrySizeField.TagIDAsChars,       tickerStructV4.tradeVolume);
        fixMessageBuilder.append(FixTradingSessionIDField.TagIDAsChars,  sessionName);
        CmiFixMapper.mapCmiToFixTradeCondition(fixMessageBuilder,        tickerStructV4.salePrefix);
        if (tickerStructV4.salePostfix.length() > 0)
        {
            fixMessageBuilder.append(FixTextField.TagIDAsChars,          tickerStructV4.salePostfix);
        }
    }


    protected void mapMarketDataReject(FixMessageBuilderIF fixMessageBuilder, char rejectReason, String rejectText, String mdReqID) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, AlreadyExistsException
    {
        fixMessageBuilder.append(FixMDReqIDField.TagIDAsChars,        mdReqID);
        fixMessageBuilder.append(FixMDReqRejReasonField.TagIDAsChars, rejectReason);
        fixMessageBuilder.append(FixTextField.TagIDAsChars,           rejectText);
    }

    protected boolean fillInCurrentMarketStructData(FixMessageBuilderIF fixMessageBuilder, CurrentMarketStructV4 currentMarketStructV4, MarketVolumeStructV4 marketVolumeStructV4, boolean sentSessionName, String sessionName, boolean printMarketBest)
    {
        switch (marketVolumeStructV4.volumeType)
        {
            case VolumeTypes.CUSTOMER_ORDER:
            case VolumeTypes.PROFESSIONAL_ORDER:
                fixMessageBuilder.append(FixMDEntrySizeField.taggedchars_value0);
                break;
            default:
                fixMessageBuilder.append(FixMDEntrySizeField.TagIDAsChars, marketVolumeStructV4.quantity);
                break;
        }

        if (!sentSessionName)
        {
            // Send time in only first repeating group
            fixMessageBuilder.appendDateInFixUTCTimeOnlyFormat(FixMDEntryTimeField.TagIDAsChars,  currentMarketStructV4.sentTime);

            // Initialize the TradingSessionID in only the first repeating group
            fixMessageBuilder.append(FixTradingSessionIDField.TagIDAsChars,  sessionName);
            sentSessionName = true;
        }

        if (printMarketBest)
        {
            // The IsMarketBest is only populated in the first repeating group that contains information

            fixMessageBuilder.append(FixQuoteConditionField.taggedchars_ExchangeBest);

        }

        switch (marketVolumeStructV4.volumeType)
        {
            case VolumeTypes.IOC:                fixMessageBuilder.append(FixTimeInForceField.taggedchars_ImmediateOrCancel);                      break;
            case VolumeTypes.FOK:                fixMessageBuilder.append(FixTimeInForceField.taggedchars_FillOrKill);                             break;
            case VolumeTypes.AON:                fixMessageBuilder.append(FixExecInstField.taggedchars_AllOrNone);                                 break;
            case VolumeTypes.CUSTOMER_ORDER:     fixMessageBuilder.append(FixCboeCustomerSizeField.TagIDAsChars,     marketVolumeStructV4.quantity); break;
            case VolumeTypes.PROFESSIONAL_ORDER: fixMessageBuilder.append(FixCboeProfessionalSizeField.TagIDAsChars, marketVolumeStructV4.quantity); break;
        }

        /// FIX protocol looks for the NumberOfOrders field to describe the actual number of orders.
        /// We only have a 0 or 1 coming back from the cmi (single or multiple) so 1 or 2 in the FIX field NumberOfOrders must be sufficient.
        fixMessageBuilder.append(FixNumberOfOrdersField.TagIDAsChars, (marketVolumeStructV4.multipleParties == MultiplePartiesIndicators.YES) ? 2 : 1);

        return sentSessionName;
    }

    protected int calculateAdjustedSequenceSize(MarketVolumeStructV4[] sequenceV4)
    {
        int sequenceSize = 0;

        for (int sequenceIndex = 0; sequenceIndex < sequenceV4.length; sequenceIndex++)
        {
            switch (sequenceV4[sequenceIndex].volumeType)
            {
                case VolumeTypes.CUSTOMER_ORDER:
                case VolumeTypes.PROFESSIONAL_ORDER:
                    // if the client can handle UDF messages,
                    // then include CUSTOMER and PROFESSIONAL MarketVolumes in the total
                    if (handleUDF)
                    {
                        sequenceSize++;
                    }
                    break;
                default:
                    sequenceSize++;
                    break;
            }
        }

        return sequenceSize;
    }
}
