// -----------------------------------------------------------------------------------
// Source file: ExchangeIndicatorFormatter
//
// PACKAGE: com.cboe.presentation.common.formatters
// 
// Created: Jul 12, 2004 1:29:21 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.interfaces.presentation.common.formatters.ExchangeIndicatorFormatStrategy;
import com.cboe.interfaces.presentation.marketData.ExchangeIndicator;

public class ExchangeIndicatorFormatter extends Formatter implements ExchangeIndicatorFormatStrategy
{
    public static final short CLEAR            = com.cboe.idl.cmiConstants.ExchangeIndicatorTypes.CLEAR;
    public static final short HALTED           = com.cboe.idl.cmiConstants.ExchangeIndicatorTypes.HALTED;
    public static final short FAST_MARKET      = com.cboe.idl.cmiConstants.ExchangeIndicatorTypes.FAST_MARKET;
    public static final short OPENING_ROTATION = com.cboe.idl.cmiConstants.ExchangeIndicatorTypes.OPENING_ROTATION;

    public static final String CLEAR_STRING_FULL            = "Clear";
    public static final String HALTED_STRING_FULL           = "Halted";
    public static final String FAST_MARKET_STRING_FULL      = "Fast Market";
    public static final String OPENING_ROTATION_STRING_FULL = "Opening Rotation";
    public static final String UNKNOWN_STRING_FULL          = "Unknown";

    public static final String CLEAR_STRING_BRIEF            = " ";
    public static final String HALTED_STRING_BRIEF           = "H";
    public static final String FAST_MARKET_STRING_BRIEF      = "F";
    public static final String OPENING_ROTATION_STRING_BRIEF = "O";
    public static final String UNKNOWN_STRING_BRIEF          = "?";

    public ExchangeIndicatorFormatter()
    {
        super();
        addStyle(FULL_INDICATOR, FULL_INDICATOR_DESC);
        addStyle(BRIEF_INDICATOR, BRIEF_INDICATOR_DESC);
        addStyle(BRIEF_INDICATOR_SORTED_ARRAY, BRIEF_INDICATOR_SORTED_ARRAY_DESC);

        setDefaultStyle(FULL_INDICATOR);
    }

    public String format(ExchangeIndicator indicator)
    {
        return format(indicator, getDefaultStyle());
    }

    public String format(ExchangeIndicator indicator, String styleName)
    {
        validateStyle(styleName);
        String retVal = "";

        if (styleName.equals(FULL_INDICATOR))
        {
            retVal = formatFullIndicator(indicator);
        }
        else if (styleName.equals(BRIEF_INDICATOR))
        {
            retVal = formatBriefIndicator(indicator);
        }

        return retVal;
    }
    public String format(ExchangeIndicator[] indicator)
    {
        return format(indicator, getDefaultStyle());
    }

    public String format(ExchangeIndicator[] indicator, String styleName)
    {
        validateStyle(styleName);
        StringBuffer buffer = new StringBuffer();

        if (styleName.equals(FULL_INDICATOR))
        {
            for (int i=0; i<indicator.length; i++)
            {
                buffer.append(formatFullIndicator(indicator[i]));
                buffer.append(' ');
            }
        }
        else if (styleName.equals(BRIEF_INDICATOR))
        {
            for (int i=0; i<indicator.length; i++)
            {
                buffer.append(formatBriefIndicator(indicator[i]));
            }
        }
        else if (styleName.equals(BRIEF_INDICATOR_SORTED_ARRAY))
        {
            String[] exchangeOrder = ExchangeCharacterTypes.getExchangeOrder();
            if (indicator.length == 0)
            {
                for (int i=0; i< exchangeOrder.length; i++)
                {
                    buffer.append(UNKNOWN_STRING_BRIEF);
                }
            }
            else
            {
                for (int i=0; i< exchangeOrder.length; i++)
                {
                    boolean found = false;

                    for (int j=0; j<indicator.length; j++)
                    {
                        if (exchangeOrder[i].compareToIgnoreCase(indicator[j].getExchange()) == 0)
                        {
                            buffer.append(formatBriefIndicator(indicator[j]));
                            found = true;
                            break;
                        }
                    }

                    if (!found)
                    {
                        buffer.append(UNKNOWN_STRING_BRIEF);
                    }
                }
            }
        }

        return buffer.toString();
    }

    protected String formatFullIndicator(ExchangeIndicator indicator)
    {
        switch (indicator.getMarketCondition())
        {
            case CLEAR:
                return CLEAR_STRING_FULL;
            case HALTED:
                return HALTED_STRING_FULL;
            case FAST_MARKET:
                return FAST_MARKET_STRING_FULL;
            case OPENING_ROTATION:
                return OPENING_ROTATION_STRING_FULL;
            default:
                return new StringBuffer().append(UNKNOWN_STRING_FULL).append("[ ").append(indicator.getMarketCondition()).append(" ]").toString();
        }
    }

    protected String formatBriefIndicator(ExchangeIndicator indicator)
    {
        switch (indicator.getMarketCondition())
        {
            case CLEAR:
                return CLEAR_STRING_BRIEF;
            case HALTED:
                return HALTED_STRING_BRIEF;
            case FAST_MARKET:
                return FAST_MARKET_STRING_BRIEF;
            case OPENING_ROTATION:
                return OPENING_ROTATION_STRING_BRIEF;
            default:
                return UNKNOWN_STRING_BRIEF;
        }
    }

//  public static void main(String[] args)
//  {
//      ExchangeIndicatorStruct[] structs = new ExchangeIndicatorStruct[6];
//
//      structs[0] = new ExchangeIndicatorStruct(ExchangeCharacterTypes.AMEX, ExchangeIndicatorFormatter.CLEAR);
//      structs[1] = new ExchangeIndicatorStruct(ExchangeCharacterTypes.CBOE, ExchangeIndicatorFormatter.FAST_MARKET);
//      structs[2] = new ExchangeIndicatorStruct(ExchangeCharacterTypes.PSE,  ExchangeIndicatorFormatter.CLEAR);
//      structs[3] = new ExchangeIndicatorStruct(ExchangeCharacterTypes.ISE,  ExchangeIndicatorFormatter.CLEAR);
//      structs[4] = new ExchangeIndicatorStruct(ExchangeCharacterTypes.PHLX, ExchangeIndicatorFormatter.HALTED);
//      structs[5] = new ExchangeIndicatorStruct(ExchangeCharacterTypes.BOX,  ExchangeIndicatorFormatter.CLEAR);
//
//      ExchangeIndicator[] eStructs = new ExchangeIndicator[6];
//
//      for (int i=0; i<eStructs.length; i++)
//      {
//        eStructs[i] = ExchangeIndicatorFactory.create(structs[i]);
//      }
//
//      ExchangeIndicatorFormatter format = new ExchangeIndicatorFormatter();
//      String output = format.format(eStructs, ExchangeIndicatorFormatStrategy.BRIEF_INDICATOR_SORTED_ARRAY);
//      System.out.println("Results:" + output);
//  }

}
