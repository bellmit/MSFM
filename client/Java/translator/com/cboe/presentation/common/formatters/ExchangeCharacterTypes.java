// -----------------------------------------------------------------------------------
// Source file: ExchangeCharacterTypes
//
// PACKAGE: com.cboe.presentation.common.formatters
// 
// Created: Jul 21, 2004 2:57:52 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import java.util.*;

import com.cboe.interfaces.presentation.marketData.ExchangeVolume;

import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Provides a mapping between the CMi ExchangeStrings constants, and the chars that should be used to represent each Exchange.
 */
public class ExchangeCharacterTypes
{
    public static final char UNKNOWN_CHAR = '?';

    // constants used when getting property values from the AppPropertiesFile
    public static final String EXCHANGE_CHARACTER_SECTION = "ExchangeCharacters";
    public static final String MARKET_DATA_HISTORY_SECTION = "MarketDataHistory";
    public static final String EXCHANGE_ORDER_PROPERTY_NAME = "ExchangeOrder";

    // String containing all the Exchange characters in the correct order, as determined by the list of Exchanges from getExchangeOrder(). (caching the String instead of rebuilding it each time it's requested)
    private static String exchangeCharsOrderString;

    /**
     * Used to build a string of exchanges in the order of the array
     */
    private static String[] exchangeOrder;

    // cash the exchange-to-char property values as they're lazily loaded from the AppPropertiesFile
    private static Map<String, Character> exchangeCharacters;

    /**
     * This methods sorts the array of ExchangeVolume in the order of the ExchangeOrder array
     * and converts the the Exchanges of the ExchangeVolume to their character representation
     *
     * @see this.getExchangeOrder() for the order of exchanges
     * @param volume - array of ExchangeVolumes to get the Exchanges from
     * @return an ordered String of Exchange Character Symbols
     */
    public static String toString(ExchangeVolume[] volume)
    {
        StringBuffer retVal = new StringBuffer();
        String[] exchangeOrder = getExchangeOrder();
        for (int i=0; i< exchangeOrder.length; i++)
        {
            for (int j=0; j<volume.length; j++)
            {
                String exchange = volume[j].getExchange();

                if (exchangeOrder[i].compareToIgnoreCase(exchange) == 0)
                {
                    char value = getExchangeCharacter(exchange);
                    if(value != UNKNOWN_CHAR)
                    {
                        retVal.append(value);
                    }
                    break;
                }
            }
        }

        return retVal.toString().trim();
    }

    /**
     *
     * @param exchange - a String from com.cboe.idl.cmiConstants.ExchangeStrings
     * @return the character representation of the Exchange
     */
    public static synchronized char getExchangeCharacter(String exchange)
    {
        Character exchangeChar = getExchangeCharactersMap().get(exchange);
        if(exchangeChar == null)
        {
            exchangeChar = UNKNOWN_CHAR;
            if(AppPropertiesFileFactory.isAppPropertiesAvailable())
            {
                String propertyStr = AppPropertiesFileFactory.find().getValue(EXCHANGE_CHARACTER_SECTION, exchange);
                if(propertyStr != null && propertyStr.length() >= 0)
                {
                    exchangeChar = propertyStr.charAt(0);
                }
                else
                {
                    GUILoggerHome.find().alarm("AppProperties.getExchangeCharacter(): Property not found for section='" +
                                               EXCHANGE_CHARACTER_SECTION + "' key='" + exchange + "'");
                }
            }
            getExchangeCharactersMap().put(exchange, exchangeChar);
        }

        return exchangeChar;
    }

    /**
     * @return an ordered array of the Exchange Strings, as determined by the EXCHANGE_ORDER_PROPERTY_NAME property in the AppPropertiesFile
     */
    public static synchronized String[] getExchangeOrder()
    {
        if(exchangeOrder == null || exchangeOrder.length == 0)
        {
            exchangeOrder = new String[0];
            if(AppPropertiesFileFactory.isAppPropertiesAvailable())
            {
                String propertyStr = AppPropertiesFileFactory.find()
                        .getValue(MARKET_DATA_HISTORY_SECTION, EXCHANGE_ORDER_PROPERTY_NAME);
                if(propertyStr != null && propertyStr.length() >= 0)
                {
                    exchangeOrder = propertyStr.split("[,\\s]");
                }
                else
                {
                    GUILoggerHome.find().alarm("AppProperties.getMDHistoryExchangeOrder(): Property not found for section='" +
                                               MARKET_DATA_HISTORY_SECTION + "' key='" + EXCHANGE_ORDER_PROPERTY_NAME + "'");
                }
            }
        }
        return exchangeOrder;
    }

    /**
     * @return a String containing all of the exchange chars in the correct order, as determined by getExchangeOrder().
     */
    public static synchronized String getExchangeCharsOrderString()
    {
        if(exchangeCharsOrderString == null || exchangeCharsOrderString.length() == 0)
        {
            String[] exchangeOrder = getExchangeOrder();
            StringBuilder sb = new StringBuilder(exchangeOrder.length);
            for(String exchange : exchangeOrder)
            {
                sb.append(getExchangeCharacter(exchange));
            }
            exchangeCharsOrderString = sb.toString();
        }
        return exchangeCharsOrderString;
    }

    private static synchronized Map<String, Character> getExchangeCharactersMap()
    {
        if(exchangeCharacters == null)
        {
            exchangeCharacters = new HashMap<String, Character>(getExchangeOrder().length);
        }
        return exchangeCharacters;
    }

} // -- end of class ExchangeCharacterTypes
