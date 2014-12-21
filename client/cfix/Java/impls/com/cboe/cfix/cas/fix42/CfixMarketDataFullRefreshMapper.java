package com.cboe.cfix.cas.fix42;

/**
 * CfixMarketDataFullRefreshMapper.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.util.*;

import com.cboe.application.product.*;
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
import com.cboe.util.*;

public class CfixMarketDataFullRefreshMapper implements CfixMarketDataMapperIF
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
    
    protected static final MarketVolumeStruct[]   cached_MarketVolumeStructArray_ZERO   = new MarketVolumeStruct[]{new MarketVolumeStruct((short) 0, 0, false)};
    protected static final ExchangeVolumeStruct[] cached_ExchangeVolumeStructArray_ZERO = new ExchangeVolumeStruct[]{new ExchangeVolumeStruct("W", 0)};

    public CfixMarketDataFullRefreshMapper()
    {
        productToSessionProductStructMap = new IntObjectMap(8192);
    }

    public CfixMarketDataFullRefreshMapper(IntObjectMap productToSessionProductStructMap)
    {
        this.productToSessionProductStructMap = productToSessionProductStructMap;
    }

    public void initialize(CfixSessionManager cfixSessionManager, String propertyPrefix, Properties properties) throws Exception
    {
        this.cfixSessionManager = cfixSessionManager;

        cfixStrikePriceHelper   = CfixServicesHelper.getCfixStrikePriceHelperHome().find();
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
                mapMarketData(fixMessageBuilder, cfixOverlayPolicyMarketDataHolder.getCurrentMarketStruct(currentIndex));
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
                mapMarketData(fixMessageBuilder, cfixOverlayPolicyMarketDataHolder.getRecapStruct(currentIndex));
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
                mapMarketData(fixMessageBuilder, cfixOverlayPolicyMarketDataHolder.getTickerStruct(currentIndex));
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
    
    protected void mapMarketData(FixMessageBuilderIF fixMessageBuilder, CurrentMarketStruct currentMarketStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = getSessionProductStruct(currentMarketStruct.sessionName, currentMarketStruct.productKeys);

        buildCommonHeader(fixMessageBuilder, sessionProductStruct.productStruct.productName.reportingClass, sessionProductStruct.productStruct.productName, sessionProductStruct.productStruct.productKeys);

        MarketVolumeStruct[] bidSizeSequence;
        MarketVolumeStruct[] askSizeSequence;
        boolean              sentSessionName = false;

        /// If there is no CurrentMarket, we may get back a CurrentMarketStruct that has an empty bidSizeSequence and/or an empty askSizeSequence.
        //  For this, we want to build up the appropriate struct(s) with zero volume so we can get this information back to the requesting party.
        if (currentMarketStruct.bidSizeSequence.length == 0)
        {
            bidSizeSequence = cached_MarketVolumeStructArray_ZERO;
        }
        else
        {
            bidSizeSequence = currentMarketStruct.bidSizeSequence;
        }

        if (currentMarketStruct.askSizeSequence.length == 0)
        {
            askSizeSequence = cached_MarketVolumeStructArray_ZERO;
        }
        else
        {
            askSizeSequence = currentMarketStruct.askSizeSequence;
        }
        
        fixMessageBuilder.append(FixNoMDEntriesField.TagIDAsChars, calculateAdjustedSequenceSize(bidSizeSequence) + calculateAdjustedSequenceSize(askSizeSequence));

        for (int sequenceIndex = 0; sequenceIndex < bidSizeSequence.length; sequenceIndex++)
        {
            // if can't handle UDF messages, then skip CUSTOMER and PROFESSIONAL completely
            switch (bidSizeSequence[sequenceIndex].volumeType)
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
            fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars, currentMarketStruct.bidPrice);

            sentSessionName = fillInCurrentMarketStructData(fixMessageBuilder, currentMarketStruct, bidSizeSequence[sequenceIndex], sentSessionName, sequenceIndex == 0, currentMarketStruct.bidIsMarketBest);
        }

        for (int sequenceIndex = 0; sequenceIndex < askSizeSequence.length; sequenceIndex++)
        {
            // if can't handle UDF messages, then skip CUSTOMER and PROFESSIONAL completely
            switch (askSizeSequence[sequenceIndex].volumeType)
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
            fixMessageBuilder.append(FixMDEntryPxField.TagIDAsChars, currentMarketStruct.askPrice);

            sentSessionName = fillInCurrentMarketStructData(fixMessageBuilder, currentMarketStruct, askSizeSequence[sequenceIndex], sentSessionName, sequenceIndex == 0, currentMarketStruct.askIsMarketBest);
        }

//        fixMessageBuilder.append(FixCboeDebugTextField.TagIDAsChars, "CurrentMarket");
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

//        fixMessageBuilder.append(FixCboeDebugTextField.TagIDAsChars, "BookDepth");
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

//        fixMessageBuilder.append(FixCboeDebugTextField.TagIDAsChars, "ExpectedOpeningPrice");
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

//        fixMessageBuilder.append(FixCboeDebugTextField.TagIDAsChars, "Nbbo");
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

//        fixMessageBuilder.append(FixCboeDebugTextField.TagIDAsChars, "Recap");
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

//        fixMessageBuilder.append(FixCboeDebugTextField.TagIDAsChars, "Ticker");
    }

    protected void mapMarketDataReject(FixMessageBuilderIF fixMessageBuilder, char rejectReason, String rejectText, String mdReqID) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, AlreadyExistsException
    {
        fixMessageBuilder.append(FixMDReqIDField.TagIDAsChars,        mdReqID);
        fixMessageBuilder.append(FixMDReqRejReasonField.TagIDAsChars, rejectReason);
        fixMessageBuilder.append(FixTextField.TagIDAsChars,           rejectText);
    }

    protected boolean fillInCurrentMarketStructData(FixMessageBuilderIF fixMessageBuilder, CurrentMarketStruct currentMarketStruct, MarketVolumeStruct marketVolumeStruct, boolean sentSessionName, boolean printMarketBest, boolean marketBest)
    {
        switch (marketVolumeStruct.volumeType)
        {
            case VolumeTypes.CUSTOMER_ORDER: 
            case VolumeTypes.PROFESSIONAL_ORDER:
                fixMessageBuilder.append(FixMDEntrySizeField.taggedchars_value0);
                break;
            default:
                fixMessageBuilder.append(FixMDEntrySizeField.TagIDAsChars, marketVolumeStruct.quantity);
                break;
        }
        
        if (!sentSessionName)
        {
            // Send time in only first repeating group
            fixMessageBuilder.appendDateInFixUTCTimeOnlyFormat(FixMDEntryTimeField.TagIDAsChars,  currentMarketStruct.sentTime);
            
            // Initialize the TradingSessionID in only the first repeating group
            fixMessageBuilder.append(FixTradingSessionIDField.TagIDAsChars,  currentMarketStruct.sessionName);
            sentSessionName = true;
        }

        if (printMarketBest)
        {
            // The IsMarketBest is only populated in the first repeating group that contains information
            if (marketBest)
            {
                fixMessageBuilder.append(FixQuoteConditionField.taggedchars_ConsolidatedBest);
            }
            else
            {
                fixMessageBuilder.append(FixQuoteConditionField.taggedchars_ExchangeBest);
            }
        }

        switch (marketVolumeStruct.volumeType)
        {
            case VolumeTypes.IOC:                fixMessageBuilder.append(FixTimeInForceField.taggedchars_ImmediateOrCancel);                      break;
            case VolumeTypes.FOK:                fixMessageBuilder.append(FixTimeInForceField.taggedchars_FillOrKill);                             break;
            case VolumeTypes.AON:                fixMessageBuilder.append(FixExecInstField.taggedchars_AllOrNone);                                 break;
            case VolumeTypes.CUSTOMER_ORDER:     fixMessageBuilder.append(FixCboeCustomerSizeField.TagIDAsChars,     marketVolumeStruct.quantity); break;
            case VolumeTypes.PROFESSIONAL_ORDER: fixMessageBuilder.append(FixCboeProfessionalSizeField.TagIDAsChars, marketVolumeStruct.quantity); break;
        }

        /// FIX protocol looks for the NumberOfOrders field to describe the actual number of orders.
        /// We only have a 0 or 1 coming back from the cmi (single or multiple) so 1 or 2 in the FIX field NumberOfOrders must be sufficient.
        fixMessageBuilder.append(FixNumberOfOrdersField.TagIDAsChars, marketVolumeStruct.multipleParties ? 2 : 1);
        
        return sentSessionName;
    }
    
    protected int calculateAdjustedSequenceSize(MarketVolumeStruct[] sequence)
    {
        int sequenceSize = 0;
        
        for (int sequenceIndex = 0; sequenceIndex < sequence.length; sequenceIndex++)
        {
            switch (sequence[sequenceIndex].volumeType)
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
