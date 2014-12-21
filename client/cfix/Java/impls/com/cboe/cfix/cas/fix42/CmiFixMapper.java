package com.cboe.cfix.cas.fix42;

/**
 * CmiFixMapper.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.cfix.fix.fix42.generated.fields.*;
import com.cboe.cfix.interfaces.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.util.*;

public final class CmiFixMapper
{
    public static void mapCmiToFixTickDirection(FixMessageBuilderIF fixMessageBuilder, char cmiTickDirection) throws DataValidationException
    {
        switch (cmiTickDirection)
        {
            case '+': fixMessageBuilder.append(FixTickDirectionField.taggedchars_PlusTick);  break;
            case '-': fixMessageBuilder.append(FixTickDirectionField.taggedchars_MinusTick); break;
            case '\0':
            case ' ':
                break;
            default: throw ExceptionBuilder.dataValidationException("can't mapCmiToFixTickDirection(" + cmiTickDirection + ")", 0);
        }
    }

    public static void mapCmiToFixMDMkt(FixMessageBuilderIF fixMessageBuilder, String cmiExchange) throws DataValidationException
    {
        if (cmiExchange == null || cmiExchange.length() == 0 || ExchangeStrings.CBOE.equals(cmiExchange))
        {
            fixMessageBuilder.append(FixMDMktField.taggedchars_Cboe);
        }
        else if (ExchangeStrings.NASD.equals(cmiExchange))
        {
            fixMessageBuilder.append(FixMDMktField.taggedchars_Nasdaq);
        }
        else if (ExchangeStrings.AMEX.equals(cmiExchange))
        {
            fixMessageBuilder.append(FixMDMktField.taggedchars_Amex);
        }
        else if (ExchangeStrings.NYSE.equals(cmiExchange))
        {
            fixMessageBuilder.append(FixMDMktField.taggedchars_Nyse);
        }
        else if (ExchangeStrings.PHLX.equals(cmiExchange))
        {
            fixMessageBuilder.append(FixMDMktField.taggedchars_Phil);
        }
        else if (ExchangeStrings.PSE.equals(cmiExchange))
        {
            fixMessageBuilder.append(FixMDMktField.taggedchars_Pacific);
        }
/** we don't have the following in the CboeFIX42.xml file
        else if (ExchangeStrings.BSE.equals(cmiExchange))
        {
            fixMessageBuilder.append(FixMDMktField.taggedchars_Bse);
        }
        else if (ExchangeStrings.CBOT.equals(cmiExchange))
        {
            fixMessageBuilder.append(FixMDMktField.taggedchars_CBOT);
        }
        else if (ExchangeStrings.CHX.equals(cmiExchange))
        {
            fixMessageBuilder.append(FixMDMktField.taggedchars_Chx);
        }
        else if (ExchangeStrings.CME.equals(cmiExchange))
        {
            fixMessageBuilder.append(FixMDMktField.taggedchars_Cme);
        }
        else if (ExchangeStrings.CSE.equals(cmiExchange))
        {
            fixMessageBuilder.append(FixMDMktField.taggedchars_Cse);
        }
        else if (ExchangeStrings.ISE.equals(cmiExchange))
        {
            fixMessageBuilder.append(FixMDMktField.taggedchars_Ise);
        }
        else if (ExchangeStrings.LIFFE.equals(cmiExchange))
        {
            fixMessageBuilder.append(FixMDMktField.taggedchars_Liffe);
        }
        else if (ExchangeStrings.NQLX.equals(cmiExchange))
        {
            fixMessageBuilder.append(FixMDMktField.taggedchars_Nqlx);
        }
        else if (ExchangeStrings.NYME.equals(cmiExchange))
        {
            fixMessageBuilder.append(FixMDMktField.taggedchars_Nyme);
        }
        else if (ExchangeStrings.ONE.equals(cmiExchange))
        {
            fixMessageBuilder.append(FixMDMktField.taggedchars_One);
        }
*/
        else
        {
            throw ExceptionBuilder.dataValidationException("can't mapCmiToFixMDMkt(" + cmiExchange + ")", 0);
        }
    }

    public static void mapCmiToFixSecurityType(FixMessageBuilderIF fixMessageBuilder, short cmiProductType) throws DataValidationException
    {
        switch (cmiProductType)
        {
           case ProductTypes.EQUITY:                fixMessageBuilder.append(FixSecurityTypeField.taggedchars_CommonStock); break;
           case ProductTypes.OPTION:                fixMessageBuilder.append(FixSecurityTypeField.taggedchars_Option); break;
           case ProductTypes.FUTURE:                fixMessageBuilder.append(FixSecurityTypeField.taggedchars_Future); break;
           case ProductTypes.WARRANT:               fixMessageBuilder.append(FixSecurityTypeField.taggedchars_Warrant); break;
           case ProductTypes.VOLATILITY_INDEX:      fixMessageBuilder.append(FixSecurityTypeField.taggedchars_VolatilityIndex); break;
           case ProductTypes.INDEX:                 fixMessageBuilder.append(FixSecurityTypeField.taggedchars_Index); break;
           case ProductTypes.STRATEGY:              fixMessageBuilder.append(FixSecurityTypeField.taggedchars_MultiLeg); break;
           case ProductTypes.DEBT:                  fixMessageBuilder.append(FixSecurityTypeField.taggedchars_UsTreasuryBill); break;
           case ProductTypes.COMMODITY:             fixMessageBuilder.append(FixSecurityTypeField.taggedchars_Commodity); break;
           case ProductTypes.LINKED_NOTE:           fixMessageBuilder.append(FixSecurityTypeField.taggedchars_LinkedNote); break;
           case ProductTypes.UNIT_INVESTMENT_TRUST: fixMessageBuilder.append(FixSecurityTypeField.taggedchars_UnitInvestmentTrust); break;
           default: throw ExceptionBuilder.dataValidationException("can't mapCmiToFixSecurityType(" + cmiProductType + ")", 0);
        }
    }

    public static void mapCmiToFixStrikePrice(FixMessageBuilderIF fixMessageBuilder, PriceStruct cmiExercisePrice)
    {
        fixMessageBuilder.append(FixStrikePriceField.TagIDAsChars, cmiExercisePrice);
    }

    public static void mapCmiToFixMaturityMonthYear(FixMessageBuilderIF fixMessageBuilder, DateStruct cmiDateStruct)
    {
        fixMessageBuilder.append(FixMaturityMonthYearField.TagIDAsChars, (int) cmiDateStruct.year * 100 + cmiDateStruct.month);
    }

    public static void mapCmiToFixMaturityDay(FixMessageBuilderIF fixMessageBuilder, DateStruct cmiDateStruct)
    {
        fixMessageBuilder.append(FixMaturityDayField.TagIDAsChars, (int) cmiDateStruct.day);
    }

    public static void mapCmiToFixPutOrCall(FixMessageBuilderIF fixMessageBuilder, char cmiOptionType) throws DataValidationException
    {
        switch (cmiOptionType)
        {
           case     OptionTypes.PUT:  fixMessageBuilder.append(FixPutOrCallField.taggedchars_Put);  break;
           case     OptionTypes.CALL: fixMessageBuilder.append(FixPutOrCallField.taggedchars_Call); break;
           default: throw ExceptionBuilder.dataValidationException("can't mapCmiToFixPutOrCall(" + cmiOptionType + ")", 0);
        }
    }

    public static void mapCmiToFixSecurityDesc(FixMessageBuilderIF fixMessageBuilder, int cmiStrategyType)
    {
        switch (cmiStrategyType)
        {
           case     StrategyTypes.STRADDLE:        fixMessageBuilder.append(FixSecurityDescField.taggedchars_Straddle);       break;
           case     StrategyTypes.PSEUDO_STRADDLE: fixMessageBuilder.append(FixSecurityDescField.taggedchars_PseudoStraddle); break;
           case     StrategyTypes.VERTICAL:        fixMessageBuilder.append(FixSecurityDescField.taggedchars_Vertical);       break;
           case     StrategyTypes.RATIO:           fixMessageBuilder.append(FixSecurityDescField.taggedchars_Ratio);          break;
           case     StrategyTypes.TIME:            fixMessageBuilder.append(FixSecurityDescField.taggedchars_Time);           break;
           case     StrategyTypes.DIAGONAL:        fixMessageBuilder.append(FixSecurityDescField.taggedchars_Diagonal);       break;
           case     StrategyTypes.COMBO:           fixMessageBuilder.append(FixSecurityDescField.taggedchars_Combo);          break;
           case     StrategyTypes.UNKNOWN:         fixMessageBuilder.append(FixSecurityDescField.taggedchars_Unknown);        break;
           default:                                fixMessageBuilder.append(FixSecurityDescField.taggedchars_Unknown);        break;
        }
    }
    public static void mapCmiToFixTradeConditionEOP(FixMessageBuilderIF fixMessageBuilder, short cmiEopType)
    {
        switch (cmiEopType)
        {
	        // After HOpE - MORE_BUYERS --> NEED_MORE_SELLERS ==2 & MORE_SELLERS --> NEED_MORE_BUYERS == 3
            case ExpectedOpeningPriceTypes.NEED_MORE_SELLERS:        fixMessageBuilder.append(FixTradeConditionField.taggedchars_ImbalanceMoreBuyers); break;
            case ExpectedOpeningPriceTypes.NEED_MORE_BUYERS:         fixMessageBuilder.append(FixTradeConditionField.taggedchars_ImbalanceMoreSellers); break;

            case ExpectedOpeningPriceTypes.NO_OPENING_TRADE:         fixMessageBuilder.append(FixTradeConditionField.taggedchars_NoOpeningTrade); break;
	        case ExpectedOpeningPriceTypes.MULTIPLE_OPENING_PRICES:  fixMessageBuilder.append(FixTradeConditionField.taggedchars_MultipleOpeningPrices); break;
	        case ExpectedOpeningPriceTypes.NEED_QUOTE_TO_OPEN:       fixMessageBuilder.append(FixTradeConditionField.taggedchars_NeedQuoteToOpen); break;
	        case ExpectedOpeningPriceTypes.NEED_DPM_QUOTE_TO_OPEN:   fixMessageBuilder.append(FixTradeConditionField.taggedchars_NeedDpmQuoteToOpen); break;
	        case ExpectedOpeningPriceTypes.PRICE_NOT_IN_QUOTE_RANGE: fixMessageBuilder.append(FixTradeConditionField.taggedchars_PriceNotInQuoteRange); break;
            case ExpectedOpeningPriceTypes.PRICE_NOT_IN_BOTR_RANGE:  fixMessageBuilder.append(FixTradeConditionField.taggedchars_PriceNotInBOTRRange); break;
	        case ExpectedOpeningPriceTypes.OPENING_PRICE:            fixMessageBuilder.append(FixTradeConditionField.taggedchars_OpeningPrice); break;
            default:                                                 fixMessageBuilder.append(FixTradeConditionField.taggedchars_OpeningPrice); break;
        }
    }
    public static void mapCmiToFixTradeCondition(FixMessageBuilderIF fixMessageBuilder, String condition)
    {
        if (condition.trim().length() > 1)
        {
            fixMessageBuilder.append(FixTradeConditionField.TagIDAsChars, condition);
        }
    }
}
