package com.cboe.cfix.fix.fix42.session;

/**
 * FixMarketDataRequestDecoder.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.cfix.cas.fix42.*;
import com.cboe.cfix.cas.marketData.*;
import com.cboe.cfix.fix.fix42.generated.fields.*;
import com.cboe.cfix.fix.fix42.generated.messages.*;
import com.cboe.cfix.interfaces.*;
import com.cboe.client.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiSession.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.interfaces.cfix.*;

public final class FixMarketDataRequestDecoder
{
    public static final int bitmaskValidEntries_ExpectedOpeningPrice       = BitHelper.makeBitMask(FixMDEntryTypeField.flyweightOpeningPrice().bitIndex());
    public static final int bitmaskValidEntries_ExpectedOpeningPrice_count = BitHelper.countOnes(bitmaskValidEntries_ExpectedOpeningPrice);
    public static final int bitmaskValidEntries_Ticker                     = BitHelper.makeBitMask(FixMDEntryTypeField.flyweightTrade().bitIndex());
    public static final int bitmaskValidEntries_Ticker_count               = BitHelper.countOnes(bitmaskValidEntries_Ticker);
    public static final int bitmaskValidEntries_CurrentMarket              = BitHelper.makeBitMask(FixMDEntryTypeField.flyweightBid().bitIndex(),
                                                                                                   FixMDEntryTypeField.flyweightOffer().bitIndex());
    public static final int bitmaskValidEntries_CurrentMarket_count        = BitHelper.countOnes(bitmaskValidEntries_CurrentMarket);
    public static final int bitmaskValidEntries_Nbbo                       = BitHelper.makeBitMask(FixMDEntryTypeField.flyweightBid().bitIndex(),
                                                                                                   FixMDEntryTypeField.flyweightOffer().bitIndex());
    public static final int bitmaskValidEntries_Nbbo_count                 = BitHelper.countOnes(bitmaskValidEntries_Nbbo);
    public static final int bitmaskValidEntries_BookDepth                  = BitHelper.makeBitMask(FixMDEntryTypeField.flyweightBid().bitIndex(),
                                                                                                   FixMDEntryTypeField.flyweightOffer().bitIndex());
    public static final int bitmaskValidEntries_BookDepth_count            = BitHelper.countOnes(bitmaskValidEntries_BookDepth);
    public static final int bitmaskValidEntries_Recap                      = BitHelper.makeBitMask(FixMDEntryTypeField.flyweightBid().bitIndex(),
                                                                                                   FixMDEntryTypeField.flyweightOffer().bitIndex(),
                                                                                                   FixMDEntryTypeField.flyweightTrade().bitIndex(),
                                                                                                   FixMDEntryTypeField.flyweightOpeningPrice().bitIndex(),
                                                                                                   FixMDEntryTypeField.flyweightClosingPrice().bitIndex(),
                                                                                                   FixMDEntryTypeField.flyweightTradingSessionHighPrice().bitIndex(),
                                                                                                   FixMDEntryTypeField.flyweightTradingSessionLowPrice().bitIndex());
    public static final int bitmaskValidEntries_Recap_count                = BitHelper.countOnes(bitmaskValidEntries_Recap);

    public static SessionProductStruct buildSessionProductStruct(FixMarketDataRequestMessage fixMarketDataRequestMessage, int i, int debugFlags)
    {
        FixMarketDataRequestMessage.RelatedSymGroup relatedSymGroup = fixMarketDataRequestMessage.groupRelatedSym[i];

        // if SecurityID is specified, should use it
        if (relatedSymGroup.fieldSecurityID != null)
        {
            try
            {
                return SessionProductStructCache.getSessionProductStruct(relatedSymGroup.fieldTradingSessionID.getValue(), relatedSymGroup.fieldSecurityID.intValue());
            }
            catch (DataValidationException ex)
            {
                if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByProduct: EXCEPTION getProductBySessionForKey() SecurityID");}
                return null;
            }
            catch (NotFoundException ex)
            {
                if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByProduct: EXCEPTION getProductBySessionForKey() SecurityID");}
                return null;
            }
            catch (Exception ex)
            {
                if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByProduct: EXCEPTION getProductBySessionForKey() SecurityID");}
                Log.exception(ex);
                return null;
            }
        }

        if (relatedSymGroup.fieldIDSource != null && !relatedSymGroup.fieldIDSource.isExchangeSymbol())
        {
            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByProduct: NOTNULL IDSource AND BAD ExchangeSymbol");}
            return null;
        }

        if (relatedSymGroup.fieldSecurityType != null)
        {
            try
            {
                switch (FixCmiMapper.mapFixToCmiSecurityType(relatedSymGroup.fieldSecurityType))
                {
                    case ProductTypes.STRATEGY:
                        if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByProduct: Strategy has NULL SecurityID");}
                        return null;
                    case ProductTypes.OPTION:
                        if (relatedSymGroup.fieldPutOrCall == null)
                        {
                            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByProduct: Option has NULL PutOrCall");}
                            return null;
                        }
                        if (relatedSymGroup.fieldStrikePrice == null)
                        {
                            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByProduct: Option has NULL StrikePrice");}
                            return null;
                        }
                        if (relatedSymGroup.fieldMaturityMonthYear == null)
                        {
                            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByProduct: Option has NULL MaturityMonthYear");}
                            return null;
                        }
                        break;
                    case ProductTypes.EQUITY:
                        if (relatedSymGroup.fieldPutOrCall != null)
                        {
                            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByProduct: Equity has NOTNULL PutOrCall");}
                            return null;
                        }
                        if (relatedSymGroup.fieldStrikePrice != null)
                        {
                            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByProduct: Equity has NOTNULL StrikePrice");}
                            return null;
                        }
                        if (relatedSymGroup.fieldMaturityMonthYear != null)
                        {
                            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByProduct: Equity has NOTNULL MaturityMonthYear");}
                            return null;
                        }
                        if (relatedSymGroup.fieldMaturityDay != null)
                        {
                            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByProduct: Equity has NOTNULL MaturityDay");}
                            return null;
                        }
                        break;
                    case ProductTypes.INDEX:
                        if (relatedSymGroup.fieldPutOrCall != null)
                        {
                            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByProduct: Index has NOTNULL PutOrCall");}
                            return null;
                        }
                        if (relatedSymGroup.fieldStrikePrice != null)
                        {
                            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByProduct: Index has NOTNULL StrikePrice");}
                            return null;
                        }
                        if (relatedSymGroup.fieldMaturityMonthYear != null)
                        {
                            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByProduct: Index has NOTNULL MaturityMonthYear");}
                            return null;
                        }
                        if (relatedSymGroup.fieldMaturityDay != null)
                        {
                            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByProduct: Index has NOTNULL MaturityDay");}
                            return null;
                        }
                        break;
                    case ProductTypes.FUTURE:

                        // Code to enable/disable specific sessions for FUTURES - VB
                        if (!relatedSymGroup.fieldTradingSessionID.isCOF_MAIN())
                        {
                            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByProduct: Future has NOTNULL MaturityMonthYear");}
                            return null;

                        }
                        /* Historic code to disable code for FUTURES subscription based on Product - VB
                        if (relatedSymGroup.fieldMaturityMonthYear != null)
                        {
                            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByProduct: Future has NOTNULL MaturityMonthYear");}
                            return null;
                        }
                        if (relatedSymGroup.fieldMaturityDay != null)
                        {
                            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByProduct: Future has NOTNULL MaturityDay");}
                            return null;
                        }
                        */
                        break;
                    default:
                        if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByProduct: (" + FixCmiMapper.mapFixToCmiSecurityType(relatedSymGroup.fieldSecurityType) + ") UNHANDLED SecurityType");}
                        return null;
                }
            }
            catch (Exception ex)
            {
                if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByProduct: Exception");}
                Log.exception(ex);
                return null;
            }
        }

        try
        {
            return SessionProductStructCache.getProductByName(relatedSymGroup.fieldTradingSessionID.getValue(), mapFixToProductName(fixMarketDataRequestMessage, i));
        }
        catch (DataValidationException ex)
        {
            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByProduct: EXCEPTION(DataValidationException) getProductBySessionForName()");}
            return null;
        }
        catch (NotFoundException ex)
        {
            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByProduct: EXCEPTION(NotFoundException) getProductBySessionForName()");}
            return null;
        }
        catch (Exception ex)
        {
            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByProduct: EXCEPTION(" + ClassHelper.getClassNameFinalPortion(ex) + ") getProductBySessionForName()");}
            Log.exception(ex);
            return null;
        }
    }

    public static SessionClassStruct buildSessionClassStruct(FixMarketDataRequestMessage fixMarketDataRequestMessage, int i, int debugFlags)
    {
        FixMarketDataRequestMessage.RelatedSymGroup relatedSymGroup = fixMarketDataRequestMessage.groupRelatedSym[i];

        if (relatedSymGroup.fieldSecurityID != null)
        {
            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByClass: has NOTNULL SecurityID");}
            return null;
        }

        if (relatedSymGroup.fieldPutOrCall != null)
        {
            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByClass: has NOTNULL PutOrCall");}
            return null;
        }

        if (relatedSymGroup.fieldStrikePrice != null)
        {
            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByClass: has NOTNULL StrikePrice");}
            return null;
        }

        if (relatedSymGroup.fieldMaturityMonthYear != null)
        {
            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByClass: has NOTNULL MaturityMonthYear");}
            return null;
        }

        if (relatedSymGroup.fieldMaturityDay != null)
        {
            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByClass: has NOTNULL MaturityDay");}
            return null;
        }

        String session = null;
        short  type    = 0;
        String symbol  = null;

        try
        {
            session = relatedSymGroup.fieldTradingSessionID.getValue();
            symbol  = relatedSymGroup.fieldSymbol.getValue();
            type    = FixCmiMapper.mapFixToCmiSecurityType(relatedSymGroup.fieldSecurityType);

            return SessionProductStructCache.getClassBySymbol(session, type, symbol);
        }
        catch (DataValidationException ex)
        {
            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByClass: EXCEPTION(DataValidationException) getClassBySessionForSymbol(session(" + session + "),type(" + type + "),symbol(" + symbol + ")");}
            return null;
        }
        catch (NotFoundException ex)
        {
            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByClass: EXCEPTION(NotFoundException) getClassBySessionForSymbol(session(" + session + "),type(" + type + "),symbol(" + symbol + ")");}
            return null;
        }
        catch (Exception ex)
        {
            if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.MARKET_DATA_DECODER)) {Log.information(Thread.currentThread().getName() + " NOTDECODED ByClass: EXCEPTION(" + ClassHelper.getClassNameFinalPortion(ex) + ") getClassBySessionForSymbol(session(" + session + "),type(" + type + "),symbol(" + symbol + ")");}
            Log.exception(ex);
            return null;
        }
    }

    public static ProductNameStruct mapFixToProductName(FixMarketDataRequestMessage fixMarketDataRequestMessage, int i) throws DataValidationException
    {
        FixMarketDataRequestMessage.RelatedSymGroup relatedSymGroup = fixMarketDataRequestMessage.groupRelatedSym[i];

        ProductNameStruct productName = new ProductNameStruct();

        switch (FixCmiMapper.mapFixToCmiSecurityType(relatedSymGroup.fieldSecurityType))
        {
            case ProductTypes.OPTION:
                productName.reportingClass = relatedSymGroup.fieldSymbol.getValue();
                productName.expirationDate = DateHelper.convertMaturityToDateStruct(relatedSymGroup.fieldMaturityMonthYear.intValue(), relatedSymGroup.fieldMaturityDay == null ? 1 : relatedSymGroup.fieldMaturityDay.intValue());
                productName.exercisePrice  = FixCmiMapper.mapFixToCmiExercisePrice(productName.reportingClass, relatedSymGroup.fieldStrikePrice);
                productName.optionType     = FixCmiMapper.mapFixToCmiPutOrCall(relatedSymGroup.fieldPutOrCall);
                productName.productSymbol  = "";
                break;
            case ProductTypes.EQUITY:
                productName.reportingClass = "";
                productName.expirationDate = DateHelper.convertMaturityToDateStruct(0, 0);
                productName.exercisePrice  = PriceHelper.NO_PRICE_STRUCT;
                productName.optionType     = 0;
                productName.productSymbol  = relatedSymGroup.fieldSymbol.getValue();
                break;
            case ProductTypes.FUTURE:
                productName.reportingClass = relatedSymGroup.fieldSymbol.getValue();
                if (relatedSymGroup.fieldMaturityMonthYear != null)
                {
                    productName.expirationDate = DateHelper.convertMaturityToDateStruct(relatedSymGroup.fieldMaturityMonthYear.intValue(), relatedSymGroup.fieldMaturityDay == null ? 1 : relatedSymGroup.fieldMaturityDay.intValue());
                }
                else
                {
                    productName.expirationDate = DateHelper.convertMaturityToDateStruct(0, 0);
                }
                productName.exercisePrice  = PriceHelper.NO_PRICE_STRUCT;
                productName.optionType     = '0';
                productName.productSymbol  = "";
                break;
        }

        return productName;
    }

    public static int getMarketDataOverlayPolicy(FixMarketDataRequestMessage fixMarketDataRequestMessage)
    {
        if (fixMarketDataRequestMessage.fieldCboeApplicationQueueActionRequest != null &&
           !fixMarketDataRequestMessage.fieldCboeApplicationQueueActionRequest.isOverlayLast())
        {
            return OverlayPolicyMarketDataListIF.ALWAYS_OVERLAY_POLICY;
        }

        return OverlayPolicyMarketDataListIF.NEVER_OVERLAY_POLICY;
    }

    public static int getMarketDataRequestType(FixMarketDataRequestMessage fixMarketDataRequestMessage)
    {
        if (fixMarketDataRequestMessage.fieldSubscriptionRequestType.isSnapshot())
        {
            if (areValidEntries(fixMarketDataRequestMessage, bitmaskValidEntries_BookDepth, bitmaskValidEntries_BookDepth_count))
            {
                return CfixMarketDataDispatcherIF.MarketDataType_BookDepthSnapshot;
            }
        }
        else if (fixMarketDataRequestMessage.fieldMarketDepth.isFullBook())
        {
            if (areValidEntries(fixMarketDataRequestMessage, bitmaskValidEntries_BookDepth, bitmaskValidEntries_BookDepth_count))
            {
                return CfixMarketDataDispatcherIF.MarketDataType_BookDepth;
            }

            if (areValidEntries(fixMarketDataRequestMessage, bitmaskValidEntries_Recap, bitmaskValidEntries_Recap_count))
            {
                return CfixMarketDataDispatcherIF.MarketDataType_Recap;
            }

            if (fixMarketDataRequestMessage.fieldOpenCloseSettleFlag != null &&
                fixMarketDataRequestMessage.fieldOpenCloseSettleFlag.isExpectedOpen() &&
                areValidEntries(fixMarketDataRequestMessage, bitmaskValidEntries_ExpectedOpeningPrice, bitmaskValidEntries_ExpectedOpeningPrice_count))
            {
                return CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice;
            }
        }
        else if (fixMarketDataRequestMessage.fieldMarketDepth.isTopOfBook())
        {
            if (fixMarketDataRequestMessage.fieldScope != null &&
                fixMarketDataRequestMessage.fieldScope.isLocal() &&
                areValidEntries(fixMarketDataRequestMessage, bitmaskValidEntries_CurrentMarket, bitmaskValidEntries_CurrentMarket_count))
            {
                return CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket;
            }

            if (areValidEntries(fixMarketDataRequestMessage, bitmaskValidEntries_Ticker, bitmaskValidEntries_Ticker_count))
            {
                return CfixMarketDataDispatcherIF.MarketDataType_Ticker;
            }

            if (fixMarketDataRequestMessage.fieldScope != null &&
                fixMarketDataRequestMessage.fieldScope.isNational() &&
                areValidEntries(fixMarketDataRequestMessage, bitmaskValidEntries_Nbbo, bitmaskValidEntries_Nbbo_count))
            {
                return CfixMarketDataDispatcherIF.MarketDataType_Nbbo;
            }
        }

        return CfixMarketDataDispatcherIF.MarketDataType_Unknown;
    }

    public static boolean areValidEntries(FixMarketDataRequestMessage fixMarketDataRequestMessage, int mdEntryTypesBitmask, int mdEntryTypesCount)
    {
        if (fixMarketDataRequestMessage.fieldNoMDEntryTypes.intValue() != mdEntryTypesCount)
        {
            return false;
        }

        while (--mdEntryTypesCount >= 0)
        {
            mdEntryTypesBitmask = BitHelper.clearBitAt(mdEntryTypesBitmask, fixMarketDataRequestMessage.groupMDEntryTypes[mdEntryTypesCount].fieldMDEntryType.bitIndex());
        }

        return mdEntryTypesBitmask == 0;
    }
}
