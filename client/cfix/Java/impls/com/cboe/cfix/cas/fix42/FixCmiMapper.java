package com.cboe.cfix.cas.fix42;

/**
 * FixCmiMapper.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.cfix.cas.shared.*;
import com.cboe.cfix.fix.fix42.generated.fields.*;
import com.cboe.cfix.interfaces.*;
import com.cboe.client.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.util.*;

public final class FixCmiMapper
{
    public static PriceStruct mapFixToCmiExercisePrice(String symbol, FixStrikePriceField fieldStrikePrice)
    {
        if (fieldStrikePrice == null)
        {
            return PriceHelper.NO_PRICE_STRUCT;
        }

        return CfixServicesHelper.getCfixStrikePriceHelperHome().find().adjustStrikePrice(symbol, fieldStrikePrice.priceStructValue());
    }
    public static String mapFixToCmiMDMkt(FixMDMktField fieldMDMkt) throws DataValidationException
    {
        if (fieldMDMkt == null || fieldMDMkt.isCboe())  return ExchangeStrings.CBOE;
        if (fieldMDMkt.isNasdaq())                      return ExchangeStrings.NASD;
        if (fieldMDMkt.isAmex())                        return ExchangeStrings.AMEX;
        if (fieldMDMkt.isNyse())                        return ExchangeStrings.NYSE;
        if (fieldMDMkt.isPhil())                        return ExchangeStrings.PHLX;
        if (fieldMDMkt.isPacific())                     return ExchangeStrings.PSE;

        throw ExceptionBuilder.dataValidationException("can't mapFixToCmiMDMkt(" + fieldMDMkt + ")", 0);
    }

    public static short mapFixToCmiSecurityType(FixSecurityTypeField fieldSecurityType) throws DataValidationException
    {
        if (fieldSecurityType != null)
        {
            if (fieldSecurityType.isFuture())              return ProductTypes.FUTURE;
            if (fieldSecurityType.isOption())              return ProductTypes.OPTION;
            if (fieldSecurityType.isMultiLeg())            return ProductTypes.STRATEGY;
            if (fieldSecurityType.isIndex())               return ProductTypes.INDEX;
            if (fieldSecurityType.isCommonStock())         return ProductTypes.EQUITY;
            if (fieldSecurityType.isWarrant())             return ProductTypes.WARRANT;
            if (fieldSecurityType.isVolatilityIndex())     return ProductTypes.VOLATILITY_INDEX;
            if (fieldSecurityType.isUsTreasuryBill())      return ProductTypes.DEBT;
            if (fieldSecurityType.isCommodity())           return ProductTypes.COMMODITY;
            if (fieldSecurityType.isLinkedNote())          return ProductTypes.LINKED_NOTE;
            if (fieldSecurityType.isUnitInvestmentTrust()) return ProductTypes.UNIT_INVESTMENT_TRUST;
            if (fieldSecurityType.isCommodity())           return ProductTypes.COMMODITY;
        }

        throw ExceptionBuilder.dataValidationException("can't mapFixToCmiSecurityType(" + fieldSecurityType + ")", 0);
    }

    public static void mapFixToCmiStrikePrice(FixMessageBuilderIF fixMessageBuilder, PriceStruct cmiExercisePrice)
    {
        fixMessageBuilder.append(FixStrikePriceField.TagIDAsChars, cmiExercisePrice);
    }

    public static char mapFixToCmiPutOrCall(FixPutOrCallField fieldPutOrCall) throws DataValidationException
    {
        if (fieldPutOrCall != null)
        {
            if (fieldPutOrCall.isPut())  return OptionTypes.PUT;
            if (fieldPutOrCall.isCall()) return OptionTypes.CALL;
        }

        throw ExceptionBuilder.dataValidationException("can't mapCmiToFixPutOrCall(" + fieldPutOrCall + ")", 0);
    }

    public static int mapFixToCmiSecurityDesc(FixSecurityDescField fieldSecurityDesc)
    {
        if (fieldSecurityDesc != null)
        {
            if (fieldSecurityDesc.isStraddle())       return StrategyTypes.STRADDLE;
            if (fieldSecurityDesc.isPseudoStraddle()) return StrategyTypes.PSEUDO_STRADDLE;
            if (fieldSecurityDesc.isVertical())       return StrategyTypes.VERTICAL;
            if (fieldSecurityDesc.isRatio())          return StrategyTypes.RATIO;
            if (fieldSecurityDesc.isTime())           return StrategyTypes.TIME;
            if (fieldSecurityDesc.isDiagonal())       return StrategyTypes.DIAGONAL;
            if (fieldSecurityDesc.isCombo())          return StrategyTypes.COMBO;
            if (fieldSecurityDesc.isUnknown())        return StrategyTypes.UNKNOWN;
        }

        return StrategyTypes.UNKNOWN;
    }

    public static short mapFixToCmiTradeCondition(FixTradeConditionField fieldTradeCondition)
    {
        if (fieldTradeCondition != null)
        {
            if (fieldTradeCondition.isImbalanceMoreBuyers())   return ExpectedOpeningPriceTypes.MORE_BUYERS;
            if (fieldTradeCondition.isImbalanceMoreSellers())  return ExpectedOpeningPriceTypes.MORE_SELLERS;
            if (fieldTradeCondition.isNoOpeningTrade())        return ExpectedOpeningPriceTypes.NO_OPENING_TRADE;
            if (fieldTradeCondition.isMultipleOpeningPrices()) return ExpectedOpeningPriceTypes.MULTIPLE_OPENING_PRICES;
            if (fieldTradeCondition.isNeedQuoteToOpen())       return ExpectedOpeningPriceTypes.NEED_QUOTE_TO_OPEN;
            if (fieldTradeCondition.isNeedDpmQuoteToOpen())    return ExpectedOpeningPriceTypes.NEED_DPM_QUOTE_TO_OPEN;
            if (fieldTradeCondition.isPriceNotInQuoteRange())  return ExpectedOpeningPriceTypes.PRICE_NOT_IN_QUOTE_RANGE;
            if (fieldTradeCondition.isOpeningPrice())          return ExpectedOpeningPriceTypes.OPENING_PRICE;
        }

        return ExpectedOpeningPriceTypes.OPENING_PRICE;
    }
}
