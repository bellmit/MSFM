//
// -----------------------------------------------------------------------------------
// Source file: FormatFactory.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.idl.cmiOrder.ContraPartyStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.interfaces.presentation.common.formatters.*;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Provides a factory for creating strategies that will format objects.
 */
public class FormatFactory
{
    static OrderExtensionsFormatter orderExtensionsFormatter = null;
    static BustFormatter bustFormatter = null;
    static ContingencyFormatter contingencyFormatter = null;
    static OrderFormatter orderFormatter = null;
    static QuoteFormatter quoteFormatter = null;
    static VolumeFormatter volumeFormatter = null;
    static MarketVolumeFormatter marketVolumeFormatter = null;
    static GUILoggerPropertyFormatter GUILoggerPropertyFormatter = null;
    static CancelReasonFormatter cancelReasonFormatter = null;
    static ExchangeFirmFormatter exchangeFirmFormatter = null;
    static ExchangeAcronymFormatter exchangeAcronymFormatter = null;
    static UserRoleFormatter userRoleFormatter = null;
    static ExtensionsFormatter extensionsFormatter = null;
    static CancelRequestFormatter cancelRequestFormatter = null;
    static ExchangeIndicatorFormatter exchangeIndicatorFormatter = null;
    static MarketDataHistoryEntryFormatter marketDataHistoryEntryFormatter = null;
    static OverrideIndicatorFormatter overrideIndicatorFormatStrategy = null;
    static AuctionSubscriptionResultFormatter auctionSubscriptionResultFormatStrategy = null;
    static OperationResultFormatter operationResultFormatter = null;
    static ManualReportingFormatter manualReportingFormatter = null;
    static BooleanFormatter booleanFormatter = null;
    static ErrorCodeFormatter errorCodeFormatter = null;
    static RoutingErrorFormatter routingEerrorFormatter = null;
    static StrategyTypeFormatter strategyTypeFormatter = null;
    static TradeNotificationFormatter tradeNotificationFormatter = null;
    static LocationFormatter locationFormatter = null;


    private FormatFactory()
    {
    }

    /**
     * Gets a singleton instance of the ReportingClassFormatStrategy
     *
     * @return implementation of ReportingClassFormatStrategy
     */
    public static ReportingClassFormatStrategy getReportingClassFormatStrategy()
    {
        return CommonFormatFactory.getReportingClassFormatStrategy();
    }

    /**
     * Gets a singleton instance of the ListingStateFormatStrategy
     *
     * @return implementation of ListingStateFormatStrategy
     */
    public static ListingStateFormatStrategy getListingStateFormatStrategy()
    {
        return CommonFormatFactory.getListingStateFormatStrategy();
    }

    /**
     * Returns a format strategy suitable for formatting ExchangeFirm interfaces and ExchangeFirmStructs.
     *
     * @return implementation of ExchangeFirmFormatStrategy.
     */
    public static ExchangeFirmFormatStrategy getExchangeFirmFormatStrategy()
    {
        if (exchangeFirmFormatter == null)
        {
            exchangeFirmFormatter = new ExchangeFirmFormatter();
        }
        return exchangeFirmFormatter;
    }

    /**
     * Returns a format strategy suitable for formatting ExchangeAcronym interfaces and ExchangeAcronymStructs.
     *
     * @return implementation of ExchangeAcronymFormatStrategy.
     */
    public static ExchangeAcronymFormatStrategy getExchangeAcronymFormatStrategy()
    {
        if (exchangeAcronymFormatter == null)
        {
            exchangeAcronymFormatter = new ExchangeAcronymFormatter();
        }
        return exchangeAcronymFormatter;
    }

    /**
     * Returns a format strategy suitable for formatting order/quote busts.
     *
     * @return implementation of BustFormatStrategy.
     */
    public static BustFormatStrategy getBustFormatStrategy()
    {
        if (bustFormatter == null)
        {
            bustFormatter = new BustFormatter();
        }
        return bustFormatter;
    }

    /**
     * Returns a format strategy suitable for formatting keys and values in an order's extensions field.
     *
     * @return implmentation of OrderExtensionsFormatStrategy
     */
    public static OrderExtensionsFormatStrategy getOrderExtensionsFormatStrategy()
    {
        if (orderExtensionsFormatter == null)
        {
            orderExtensionsFormatter = new OrderExtensionsFormatter();
        }
        return orderExtensionsFormatter;
    }

    /**
     * Returns a format strategy suitable for formatting orders.
     *
     * @return implementation of OrderFormatStrategy.
     */
    public static OrderFormatStrategy getOrderFormatStrategy()
    {
        if (orderFormatter == null)
        {
            orderFormatter = new OrderFormatter();
        }
        return orderFormatter;
    }

    /**
     * Returns a format strategy suitable for formatting quotes.
     *
     * @return implementation of QuoteFormatStrategy.
     */
    public static QuoteFormatStrategy getQuoteFormatStrategy()
    {
        if (quoteFormatter == null)
        {
            quoteFormatter = new QuoteFormatter();
        }
        return quoteFormatter;
    }

    /**
     * Returns a format strategy suitable for formatting volumes.
     */
    public static VolumeFormatStrategy getVolumeFormatStrategy()
    {
        if (volumeFormatter == null)
        {
            volumeFormatter = new VolumeFormatter();
        }
        return volumeFormatter;
    }

    /**
     * Returns a format strategy suitable for formatting volumes.
     */
    public static MarketVolumeFormatStrategy getMarketVolumeFormatStrategy()
    {
        if (marketVolumeFormatter == null)
        {
            marketVolumeFormatter = new MarketVolumeFormatter();
        }
        return marketVolumeFormatter;
    }

    /**
     * Returns a format strategy suitable for ProductClass'es
     */
    public static ProductClassFormatStrategy getProductClassFormatStrategy()
    {
        return CommonFormatFactory.getProductClassFormatStrategy();
    }

    /**
     * Returns a cached format strategy suitable for ProductClasses Cache can be turned ON or OFF
     */
    public static ProductClassFormatStrategy getProductClassFormatCachedStrategy(boolean cacheEnabled)
    {
        return CommonFormatFactory.getProductClassFormatCachedStrategy(cacheEnabled);
    }

    /**
     * Returns a NOT CACHED format strategy suitable for ProductClasses
     */
    protected static ProductClassFormatStrategy getProductClassFormatNotCachedStrategy()
    {
        return CommonFormatFactory.getProductClassFormatNotCachedStrategy();
    }

    /**
     * Returns a format strategy suitable for Products
     */
    public static ProductFormatStrategy getProductFormatStrategy()
    {
        return CommonFormatFactory.getProductFormatStrategy();
    }

    /**
     * Returns a cached format strategy suitable for Products Cache can be turned ON or OFF
     */
    public static ProductFormatStrategy getProductFormatCachedStrategy(boolean cacheEnabled)
    {
        return CommonFormatFactory.getProductFormatCachedStrategy(cacheEnabled);
    }

    /**
     * Returns a NOT CACHED format strategy suitable for Products
     */
    protected static ProductFormatStrategy getProductFormatNotCachedStrategy()
    {
        return CommonFormatFactory.getProductFormatNotCachedStrategy();
    }

    /**
     * Returns a format strategy suitable for ProductTypes
     */
    public static ProductTypeFormatStrategy getProductTypeFormatStrategy()
    {
        return CommonFormatFactory.getProductTypeFormatStrategy();
    }

    /**
     * Returns a format strategy suitable for StrategyLegs
     */
    public static StrategyLegFormatStrategy getStrategyLegFormatStrategy()
    {
        return CommonFormatFactory.getStrategyLegFormatStrategy();
    }

    /**
     * Returns a format strategy suitable for Trading Sessions
     */
    public static TradingSessionFormatStrategy getTradingSessionFormatStrategy()
    {
        return CommonFormatFactory.getTradingSessionFormatStrategy();
    }

    /**
     * Returns a format strategy suitable for Trading Sessions
     */
    public static GUILoggerPropertyFormatStrategy getGUILoggerPropertyFormatStrategy()
    {
        if (GUILoggerPropertyFormatter == null)
        {
            GUILoggerPropertyFormatter = new GUILoggerPropertyFormatter();
        }
        return GUILoggerPropertyFormatter;
    }

    /**
     * Returns a format strategy suitable for Dates
     *
     * @return a <code>DateFormatStrategy</code>
     * @author Luis Torres
     */
    public static DateFormatStrategy getDateFormatStrategy()
    {
        return CommonFormatFactory.getDateFormatStrategy();
    }


    /**
     * Returns a format strategy suitable for Order Contingencies
     *
     * @return a <code>ContingencyFormatStrategy</code>
     * @author Luis Torres
     */
    public static ContingencyFormatStrategy getContingencyFormatStrategy()
    {
        if (contingencyFormatter == null)
        {
            contingencyFormatter = new ContingencyFormatter();
        }
        return contingencyFormatter;
    }

    /**
     * Returns a format strategy suitable for Cancel Reasons
     *
     * @return a <code>ContingencyFormatStrategy</code>
     * @author Alex Brazhnichenko
     */
    public static CancelReasonFormatStrategy getCancelReasonFormatStrategy()
    {
        if (cancelReasonFormatter == null)
        {
            cancelReasonFormatter = new CancelReasonFormatter();
        }
        return cancelReasonFormatter;
    }

    /**
     * Returns a format strategy suitable for User Role formatting
     *
     * @return a <code>UserRoleFormatStrategy</code>
     * @author Alex Brazhnichenko
     */
    public static UserRoleFormatStrategy getUserRoleFormatStrategy()
    {
        if (userRoleFormatter == null)
        {
            userRoleFormatter = new UserRoleFormatter();
        }
        return userRoleFormatter;
    }

    public static ExtensionsFormatStrategy getExtensionsFormatStrategy()
    {
        if (extensionsFormatter == null)
        {
            extensionsFormatter = new ExtensionsFormatter();
        }
        return extensionsFormatter;
    }

    public static CancelRequestFormatStrategy getCancelRequestFormatStrategy()
    {
        if (cancelRequestFormatter == null)
        {
            cancelRequestFormatter = new CancelRequestFormatter();
        }
        return cancelRequestFormatter;
    }

    public static ExchangeIndicatorFormatStrategy getExchangeIndicatorFormatStrategy()
    {
        if (exchangeIndicatorFormatter == null)
        {
            exchangeIndicatorFormatter = new ExchangeIndicatorFormatter();
        }
        return exchangeIndicatorFormatter;
    }

    public static MarketDataHistoryEntryFormatStrategy getMarketDataHistoryEntryFormatStrategy()
    {
        if (marketDataHistoryEntryFormatter == null)
        {
            marketDataHistoryEntryFormatter = new MarketDataHistoryEntryFormatter();
        }
        return marketDataHistoryEntryFormatter;
    }

    public static OverrideIndicatorFormatStrategy getOverrideIndicatorFormatStrategy()
    {
        if (overrideIndicatorFormatStrategy == null)
        {
            overrideIndicatorFormatStrategy = new OverrideIndicatorFormatter();
        }
        return overrideIndicatorFormatStrategy;
    }

    public static AuctionSubscriptionResultFormatStrategy getAuctionSubscriptionResultFormatStrategy()
    {
        if (auctionSubscriptionResultFormatStrategy == null)
        {
            auctionSubscriptionResultFormatStrategy = new AuctionSubscriptionResultFormatter();
        }

        return auctionSubscriptionResultFormatStrategy;
    }

    public static OperationResultFormatStrategy getOperationResultFormatStrategy()
    {
        if (operationResultFormatter == null)
        {
            operationResultFormatter = new OperationResultFormatter();
        }

        return operationResultFormatter;
    }

    public static ManualReportingFormatStrategy getManualReportingFormatStrategy()
    {
        if(manualReportingFormatter == null)
        {
            manualReportingFormatter = new ManualReportingFormatter();
        }
        return manualReportingFormatter;
    }

    public static String getFormattedProduct(Product product)
    {
        String productString;
        ProductFormatStrategy formatter;

        formatter = getProductFormatStrategy();
//        productString = formatter.format(product, ProductFormatStrategy.FULL_PRODUCT_NAME);
        productString = formatter.format(product, ProductFormatStrategy.FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE);
        return productString;
    }

    public static String buildSeriesName(int productKey, String category)
    {
        String seriesName;
        try
        {
            Product product = APIHome.findProductQueryAPI().getProductByKey(productKey);
            seriesName = getFormattedProduct(product);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(category + ".format()", "", e);
            seriesName = "";
        }
        return seriesName;
    }

    public static String getContraParties(ContraPartyStruct[] contras)
    {
        StringBuffer contraPartiesStr = new StringBuffer();
        if (contras != null && contras.length > 0)
        {
            for (int i = 0; i < contras.length; i++)
            {
                contraPartiesStr.append("Contra Party " + (i + 1) + " - Firm: " + getFormattedExchangeFirm(contras[i].firm)
                        + "; User Id: " + getFormattedExchangeAcronym(contras[i].user) + "; Vol: "
                        + FormatFactory.getVolumeFormatStrategy().format(contras[i].quantity));

                if (i < contras.length)
                {
                    contraPartiesStr.append('\n');
                }
            }
        }
        return contraPartiesStr.toString();
    }

    public static String getFormattedExchangeFirm(ExchangeFirmStruct anExchangeFirmStruct)
    {
        String retVal;
        if (anExchangeFirmStruct != null)
        {
            retVal = FormatFactory.getExchangeFirmFormatStrategy().format(anExchangeFirmStruct, ExchangeFirmFormatter.BRIEF);
        }
        else
        {
            retVal = " ";
        }

        return retVal;
    }

    public static String getFormattedExchangeFirm(ExchangeFirm exchangeFirm)
    {
        return getFormattedExchangeFirm(exchangeFirm.getExchangeFirmStruct());
    }

    public static String getFormattedExchangeAcronym(ExchangeAcronymStruct anExchangeAcronymStruct)
    {
        String retVal;
        if (anExchangeAcronymStruct != null)
        {
            retVal = FormatFactory.getExchangeAcronymFormatStrategy().format(anExchangeAcronymStruct, ExchangeAcronymFormatter.BRIEF);
        }
        else
        {
            retVal = " ";
        }

        return retVal;
    }

    public static String getFormattedExchangeAcronym(ExchangeAcronym exchangeAcronym)
    {
        return getFormattedExchangeAcronym(exchangeAcronym.getExchangeAcronymStruct());
    }

    public static String getValidSessions(String[] sessionNames)
    {
        StringBuffer validSessStr = new StringBuffer();
        if (sessionNames != null && sessionNames.length > 0)
        {
            for (int i = 0; i < sessionNames.length; i++)
            {
                validSessStr.append(sessionNames[i]);

                if (i < sessionNames.length - 1)
                {
                    validSessStr.append(", ");
                }
            }
        }
        return validSessStr.toString();
    }

    public static BooleanFormatStrategy getBooleanFormatter()
    {
        if (booleanFormatter == null)
        {
            booleanFormatter = new BooleanFormatter();
        }
        return booleanFormatter;
    }

    public static ErrorCodeFormatStrategy getErrorCodeFormatter()
    {
        if (errorCodeFormatter == null)
        {
            errorCodeFormatter = new ErrorCodeFormatter();
        }
        return errorCodeFormatter;
    }

    public static ErrorCodeFormatStrategy getRoutingErrorFormatter()
    {
        if(routingEerrorFormatter == null)
        {
            routingEerrorFormatter = new RoutingErrorFormatter();
        }
        return routingEerrorFormatter;
    }

    public static StrategyTypeFormatStrategy getStrategyTypeFormatter()
    {
        if (strategyTypeFormatter == null)
        {
            strategyTypeFormatter = new StrategyTypeFormatter();
        }
        return strategyTypeFormatter;
    }

    public static TradeNotificationFormatStrategy getTradeNotificationFormatter()
    {
        if (tradeNotificationFormatter == null)
        {
            tradeNotificationFormatter = new TradeNotificationFormatter();
        }
        return tradeNotificationFormatter;
    }

    /**
     * Create a LocationFormatter.
     * @return a LocationFormatter.
     */
    public static LocationFormatStrategy getLocationFormatter(){
        if (locationFormatter == null){
            locationFormatter = new LocationFormatter();
        }
        return locationFormatter;
    }

}
