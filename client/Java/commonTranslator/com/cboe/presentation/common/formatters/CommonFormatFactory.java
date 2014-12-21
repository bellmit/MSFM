//
// -----------------------------------------------------------------------------------
// Source file: CommonFormatFactory.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.interfaces.presentation.common.formatters.ContingencyFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.TradingPropertyFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ExceptionFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.DateFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ReportingClassFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ListingStateFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ProductClassFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ProductFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ProductTypeFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.StrategyLegFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.TradingSessionFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.RoutingPropertyFormatStrategy;

public class CommonFormatFactory
{
    private static ExceptionFormatter exceptionFormatter = null;
    private static TradingPropertyFormatStrategy tradingPropertyFormatter = null;
    private static DateFormatter dateFormatter = null;
    private static ProductTypeFormatter productTypeFormatter = null;
    private static ProductFormatter productFormatter = null;
    private static ProductFormatter notCachedProductFormatter = null;
    private static StrategyLegFormatter strategyLegFormatter = null;
    private static ProductClassFormatter productClassFormatter = null;
    private static ProductClassFormatter notCachedProductClassFormatter = null;
    private static TradingSessionFormatter tradingSessionFormatter = null;
    private static ReportingClassFormatter reportingClassFormatter = null;
    private static ListingStateFormatter listingStateFormatter = null;
    private static RoutingPropertyFormatStrategy routingPropertyFormatter = null;
    private static ContingencyFormatStrategy contingencyFormatter = null;
    
    private CommonFormatFactory(){}

    public static ExceptionFormatStrategy getExceptionFormatStrategy()
    {
        if(exceptionFormatter == null)
        {
            exceptionFormatter = new ExceptionFormatter();
        }
        return exceptionFormatter;
    }

    public static TradingPropertyFormatStrategy getTradingPropertyFormatStrategy()
    {
        if(tradingPropertyFormatter == null)
        {
            tradingPropertyFormatter = new TradingPropertyFormatter();
        }
        return tradingPropertyFormatter;
    }

    public static DateFormatStrategy getDateFormatStrategy()
    {
        if(dateFormatter == null)
        {
            dateFormatter = new DateFormatter();
        }
        return dateFormatter;
    }

    public static ReportingClassFormatStrategy getReportingClassFormatStrategy()
    {
        if (reportingClassFormatter == null)
        {
            reportingClassFormatter = new ReportingClassFormatter();
        }
        return reportingClassFormatter;
    }

    /**
     * Gets a singleton instance of the ListingStateFormatStrategy
     * @return implementation of ListingStateFormatStrategy
     */
    public static ListingStateFormatStrategy getListingStateFormatStrategy()
    {
        if (listingStateFormatter == null)
        {
            listingStateFormatter = new ListingStateFormatter();
        }
        return listingStateFormatter;
    }

    /**
     * Returns a format strategy suitable for ProductClass'es
     */
    public static ProductClassFormatStrategy getProductClassFormatStrategy()
    {
        if (productClassFormatter == null)
        {
            productClassFormatter = new ProductClassFormatter();
        }
        return productClassFormatter;
    }

    /**
     * Returns a cached format strategy suitable for ProductClasses
     * Cache can be turned ON or OFF
     */
    public static ProductClassFormatStrategy getProductClassFormatCachedStrategy(boolean cacheEnabled)
    {
        if (cacheEnabled)
        {
            return getProductClassFormatStrategy();
        }
        else
        {
            return getProductClassFormatNotCachedStrategy();
        }
    }

    /**
     * Returns a NOT CACHED format strategy suitable for ProductClasses
     */
    protected static ProductClassFormatStrategy getProductClassFormatNotCachedStrategy()
    {
        if (notCachedProductClassFormatter == null)
        {
            notCachedProductClassFormatter = new ProductClassFormatter(false);
        }
        return notCachedProductClassFormatter;
    }

    /**
     * Returns a format strategy suitable for Products
     */
    public static ProductFormatStrategy getProductFormatStrategy()
    {
        if (productFormatter == null)
        {
            productFormatter = new ProductFormatter();
        }
        return productFormatter;
    }

    /**
     * Returns a cached format strategy suitable for Products
     * Cache can be turned ON or OFF
     */
    public static ProductFormatStrategy getProductFormatCachedStrategy(boolean cacheEnabled)
    {
        if (cacheEnabled)
        {
            return getProductFormatStrategy();
        }
        else
        {
            return getProductFormatNotCachedStrategy();
        }
    }

    /**
     * Returns a NOT CACHED format strategy suitable for Products
     */
    protected static ProductFormatStrategy getProductFormatNotCachedStrategy()
    {
        if (notCachedProductFormatter == null)
        {
            notCachedProductFormatter = new ProductFormatter(false);
        }
        return notCachedProductFormatter;
    }

    /**
     * Returns a format strategy suitable for ProductTypes
     */
    public static ProductTypeFormatStrategy getProductTypeFormatStrategy()
    {
        if (productTypeFormatter == null)
        {
            productTypeFormatter = new ProductTypeFormatter();
        }
        return productTypeFormatter;
    }

    /**
     * Returns a format strategy suitable for StrategyLegs
     */
    public static StrategyLegFormatStrategy getStrategyLegFormatStrategy()
    {
        if (strategyLegFormatter == null)
        {
            strategyLegFormatter = new StrategyLegFormatter();
        }
        return strategyLegFormatter;
    }

    /**
     * Returns a format strategy suitable for Trading Sessions
     */
    public static TradingSessionFormatStrategy getTradingSessionFormatStrategy()
    {
        if (tradingSessionFormatter == null)
        {
            tradingSessionFormatter = new TradingSessionFormatter();
        }
        return tradingSessionFormatter;
    }
    
    public static synchronized RoutingPropertyFormatStrategy getRoutingPropertyFormatStrategy()
    {
        if (routingPropertyFormatter == null)
        {
            routingPropertyFormatter = new RoutingPropertyFormatter();
        }
        return routingPropertyFormatter;
    }
    
    public static ContingencyFormatStrategy getContingencyFormatStrategy()
    {
        if (contingencyFormatter == null)
        {
        	contingencyFormatter = new ContingencyFormatter();
        }
        return contingencyFormatter;
    }
    
}
