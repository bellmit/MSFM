//
// -----------------------------------------------------------------------------------
// Source file: OrderRiskControlPreferences.java
//
// PACKAGE: com.cboe.presentation.properties
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2011 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.order.riskControlValidation;

import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.preferences.*;
import com.cboe.interfaces.domain.Price;
import com.cboe.presentation.common.preferences.PreferenceManagerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.userSession.UserSessionFactory;
import com.cboe.presentation.api.APIHome;

/**
 * Provides an interface to get and set the business preferences representing the user's pre-trade risk control configuration.
 */
public class OrderRiskControlPreferences
{
    private static final String CATEGORY = "OrderRiskControlPreferences";

    private OrderRiskControlPreferences()
    {
    }

    public static void clearAllConfirmationProperties()
    {
        PreferenceCollection allPrefs = getPrefManager().getPreferences().getSection(CATEGORY);
        getPrefManager().getPreferences().removePreferences(allPrefs);
    }

    /**
     * @return true if duplicate orders within a user-configured time range should be rejected
     */
    public static boolean isDuplicateOrderCheckEnabled(ProductClass pc)
    {
        return isDuplicateOrderCheckEnabled(pc, true);
    }

    public static boolean isDuplicateOrderCheckEnabled(ProductClass pc, boolean useDefault)
    {
        // boolean enablement prefs are being set for both DUPLICATE_ORDER_ALLOWED_COUNT and DUPLICATE_ORDER_TIME_RANGE, so only need to check either one of them
        return getEnabledPreference(OrderRiskControlPreferenceTypes.DUPLICATE_ORDER_TIME_RANGE, pc, useDefault);
    }

    public static void setDuplicateOrderCheckEnabled(ProductClass pc, boolean enabled)
    {
        // NOTE: there are 2 OrderRiskControlPreferenceTypes related to Duplicate Order validation, so set the enablement boolean pref for both of them
        setEnabledPreference(OrderRiskControlPreferenceTypes.DUPLICATE_ORDER_ALLOWED_COUNT, pc, enabled);
        setEnabledPreference(OrderRiskControlPreferenceTypes.DUPLICATE_ORDER_TIME_RANGE, pc, enabled);
    }

    /**
     * Return the time range (in seconds) to use when checking for duplicate orders
     */
    public static int getDuplicateOrderCheckTimeRange(ProductClass pc)
    {
        return getDuplicateOrderCheckTimeRange(pc, true);
    }

    public static int getDuplicateOrderCheckTimeRange(ProductClass pc, boolean useDefault)
    {
        return getIntPreference(OrderRiskControlPreferenceTypes.DUPLICATE_ORDER_TIME_RANGE, pc, useDefault);
    }

    /**
     * Return the number of duplicate orders that are allowed to be submitted within the ProductClass' time range
     * returned by getDuplicateOrderCheckTimeRange().
     */
    public static int getDuplicateOrderLimit(ProductClass pc)
    {
        return getDuplicateOrderLimit(pc, true);
    }

    public static int getDuplicateOrderLimit(ProductClass pc, boolean useDefault)
    {
        return getIntPreference(OrderRiskControlPreferenceTypes.DUPLICATE_ORDER_ALLOWED_COUNT, pc, useDefault);
    }

    /**
     * Set the number of duplicate orders that are allowed to be submitted within getDuplicateOrderCheckTimeRange().
     */
    public static void setDuplicateOrderLimit(ProductClass pc, int numOrders)
    {
        setIntPreference(OrderRiskControlPreferenceTypes.DUPLICATE_ORDER_ALLOWED_COUNT, pc, numOrders);
    }

    /**
     * Set the time range (in seconds) to use when checking for duplicate orders.
     * @param pc
     * @param seconds
     */
    public static void setDuplicateOrderCheckTimeRange(ProductClass pc, int seconds)
    {
        setIntPreference(OrderRiskControlPreferenceTypes.DUPLICATE_ORDER_TIME_RANGE, pc, seconds);
    }

    /**
     * If this returns true, then the user should be prompted for confirmation when attempting to
     * enter an order with a qty greater than returned by getMaxOrderQuantityConfirmation(productClass).
     */
    public static boolean isSingleOrderQuantityConfirmationEnabled(ProductClass pc)
    {
        return isSingleOrderQuantityConfirmationEnabled(pc, true);
    }

    public static boolean isSingleOrderQuantityConfirmationEnabled(ProductClass pc, boolean useDefault)
    {
        return getEnabledPreference(OrderRiskControlPreferenceTypes.SINGLE_ORDER_QTY, pc, useDefault);
    }

    public static void setSingleOrderQuantityConfirmationEnabled(ProductClass pc, boolean enabled)
    {
        setEnabledPreference(OrderRiskControlPreferenceTypes.SINGLE_ORDER_QTY, pc, enabled);
    }

    /**
     * Return the quantity that the user has configured for the product class.  The user will be
     * prompted for confirmation if attempting to enter an order with a greater qty.
     */
    public static int getSingleOrderQuantityConfirmationLimit(ProductClass pc)
    {
        return getSingleOrderQuantityConfirmationLimit(pc, true);
    }

    public static int getSingleOrderQuantityConfirmationLimit(ProductClass pc, boolean useDefault)
    {
        return getIntPreference(OrderRiskControlPreferenceTypes.SINGLE_ORDER_QTY, pc, useDefault);
    }

    /**
     * @param pc
     * @param orderQty A positive number will set the qty that will trigger a confirmation message during order entry;
     *                 If it's set to 0 (or less than 1) this confirmation will be disabled for the ProductClass.
     */
    public static void setSingleOrderQuantityConfirmationLimit(ProductClass pc, int orderQty)
    {
        setIntPreference(OrderRiskControlPreferenceTypes.SINGLE_ORDER_QTY, pc, orderQty);
    }

    public static boolean isSingleOrderDollarValueConfirmationEnabled(ProductClass pc)
    {
        return isSingleOrderDollarValueConfirmationEnabled(pc, true);
    }

    public static boolean isSingleOrderDollarValueConfirmationEnabled(ProductClass pc, boolean useDefault)
    {
        return getEnabledPreference(OrderRiskControlPreferenceTypes.SINGLE_ORDER_DOLLAR_VALUE, pc, useDefault);
    }

    public static void setSingleOrderDollarValueConfirmationEnabled(ProductClass pc, boolean enabled)
    {
        setEnabledPreference(OrderRiskControlPreferenceTypes.SINGLE_ORDER_DOLLAR_VALUE, pc, enabled);
    }

    public static Price getSingleOrderDollarValueConfirmationLimit(ProductClass pc)
    {
        return getSingleOrderDollarValueConfirmationLimit(pc, true);
    }

    public static Price getSingleOrderDollarValueConfirmationLimit(ProductClass pc, boolean useDefault)
    {
        double value = getDoublePreference(OrderRiskControlPreferenceTypes.SINGLE_ORDER_DOLLAR_VALUE, pc, useDefault);
        return DisplayPriceFactory.create(value);
    }

    public static void setSingleOrderDollarValueConfirmationLimit(ProductClass pc, Price value)
    {
        setDoublePreference(OrderRiskControlPreferenceTypes.SINGLE_ORDER_DOLLAR_VALUE, pc, value == null ? 0.0 : value.toDouble());
    }

    public static boolean isDailyGrossQuantityTradedConfirmationEnabled(ProductClass pc)
    {
        return isDailyGrossQuantityTradedConfirmationEnabled(pc, true);
    }

    public static boolean isDailyGrossQuantityTradedConfirmationEnabled(ProductClass pc, boolean useDefault)
    {
        return getEnabledPreference(OrderRiskControlPreferenceTypes.DAILY_GROSS_TRADED_QTY, pc, useDefault);
    }

    public static void setDailyGrossQuantityTradedConfirmationEnabled(ProductClass pc, boolean enabled)
    {
        setEnabledPreference(OrderRiskControlPreferenceTypes.DAILY_GROSS_TRADED_QTY, pc, enabled);
    }

    public static int getDailyGrossQuantityTradedConfirmationLimit(ProductClass pc)
    {
        return getDailyGrossQuantityTradedConfirmationLimit(pc, true);
    }

    public static int getDailyGrossQuantityTradedConfirmationLimit(ProductClass pc, boolean useDefault)
    {
        return getIntPreference(OrderRiskControlPreferenceTypes.DAILY_GROSS_TRADED_QTY, pc, useDefault);
    }

    public static void setDailyGrossQuantityTradedConfirmationLimit(ProductClass pc, int quantity)
    {
        setIntPreference(OrderRiskControlPreferenceTypes.DAILY_GROSS_TRADED_QTY, pc, quantity);
    }

    public static boolean isDailyNetQuantityTradedConfirmationEnabled(ProductClass pc)
    {
        return isDailyNetQuantityTradedConfirmationEnabled(pc, true);
    }

    public static boolean isDailyNetQuantityTradedConfirmationEnabled(ProductClass pc, boolean useDefault)
    {
        return getEnabledPreference(OrderRiskControlPreferenceTypes.DAILY_NET_TRADED_QTY, pc, useDefault);
    }

    public static void setDailyNetQuantityTradedConfirmationEnabled(ProductClass pc, boolean enabled)
    {
        setEnabledPreference(OrderRiskControlPreferenceTypes.DAILY_NET_TRADED_QTY, pc, enabled);
    }

    public static int getDailyNetQuantityTradedConfirmationLimit(ProductClass pc)
    {
        return getDailyNetQuantityTradedConfirmationLimit(pc, true);
    }

    public static int getDailyNetQuantityTradedConfirmationLimit(ProductClass pc, boolean useDefault)
    {
        return getIntPreference(OrderRiskControlPreferenceTypes.DAILY_NET_TRADED_QTY, pc, useDefault);
    }

    public static void setDailyNetQuantityTradedConfirmationLimit(ProductClass pc, int quantity)
    {
        setIntPreference(OrderRiskControlPreferenceTypes.DAILY_NET_TRADED_QTY, pc, quantity);
    }

    public static boolean isDailyGrossDollarValueConfirmationEnabled(ProductClass pc)
    {
        return isDailyGrossDollarValueConfirmationEnabled(pc, true);
    }

    public static boolean isDailyGrossDollarValueConfirmationEnabled(ProductClass pc, boolean useDefault)
    {
        return getEnabledPreference(OrderRiskControlPreferenceTypes.DAILY_GROSS_DOLLAR_VALUE, pc, useDefault);
    }

    public static void setDailyGrossDollarValueConfirmationEnabled(ProductClass pc, boolean enabled)
    {
        setEnabledPreference(OrderRiskControlPreferenceTypes.DAILY_GROSS_DOLLAR_VALUE, pc, enabled);
    }

    public static Price getDailyGrossDollarValueConfirmationLimit(ProductClass pc)
    {
        return getDailyGrossDollarValueConfirmationLimit(pc, true);
    }

    public static Price getDailyGrossDollarValueConfirmationLimit(ProductClass pc, boolean useDefault)
    {
        return getPricePreference(OrderRiskControlPreferenceTypes.DAILY_GROSS_DOLLAR_VALUE, pc, useDefault);
    }

    public static void setDailyGrossDollarValueConfirmationLimit(ProductClass pc, Price dollarValue)
    {
        setPricePreference(OrderRiskControlPreferenceTypes.DAILY_GROSS_DOLLAR_VALUE, pc, dollarValue);
    }

    public static boolean isDailyNetDollarValueConfirmationEnabled(ProductClass pc)
    {
        return isDailyNetDollarValueConfirmationEnabled(pc, true);
    }

    public static boolean isDailyNetDollarValueConfirmationEnabled(ProductClass pc, boolean useDefault)
    {
        return getEnabledPreference(OrderRiskControlPreferenceTypes.DAILY_NET_DOLLAR_VALUE, pc, useDefault);
    }

    public static void setDailyNetDollarValueConfirmationEnabled(ProductClass pc, boolean enabled)
    {
        setEnabledPreference(OrderRiskControlPreferenceTypes.DAILY_NET_DOLLAR_VALUE, pc, enabled);
    }

    public static Price getDailyNetDollarValueConfirmationLimit(ProductClass pc)
    {
        return getDailyNetDollarValueConfirmationLimit(pc, true);
    }

    public static Price getDailyNetDollarValueConfirmationLimit(ProductClass pc, boolean useDefault)
    {
        return getPricePreference(OrderRiskControlPreferenceTypes.DAILY_NET_DOLLAR_VALUE, pc, useDefault);
    }

    public static void setDailyNetDollarValueConfirmationLimit(ProductClass pc, Price dollarValue)
    {
        setPricePreference(OrderRiskControlPreferenceTypes.DAILY_NET_DOLLAR_VALUE, pc, dollarValue);
    }

    //
    // private convenience methods to get/set preference values
    //

    /**
     * if useDefault is true, the default ProductClass' value will be returned if risk control is disabled for this OrderRiskControlPreferenceType and ProductClass.
     */
    private static double getDoublePreference(OrderRiskControlPreferenceTypes confirmationType, ProductClass pc, boolean useDefault)
    {
        double retVal = 0.0;
        boolean riskControlEnabledForClass = getEnabledPreference(confirmationType, pc, false);
        // if risk control is enabled for this type/class, return the value (even if it's zero) instead of going to the default
        if (riskControlEnabledForClass)
        {
            useDefault = false;
        }
        // when using the default, if this OrderRiskControlPreferenceTypes is disabled for this pc, then return the default value rather than this pc's value
        if (!useDefault || riskControlEnabledForClass)
        {
            String prefName = buildPrefName(confirmationType.getBusinessPrefPrefix(), pc);
            try
            {
                retVal = getPrefManager().getDoublePreference(prefName);
            }
            catch (PreferenceNotFoundException e)
            {
                // it's ok if the preference doesn't exist; it just means the user hasn't configured the property for this ProductClass
                GUILoggerHome.find().debug(CATEGORY, GUILoggerBusinessProperty.ORDER_ENTRY, "No double preference found for " + prefName);
            }
        }

        // if this wasn't the default ProductClass, check if default is set
        if (useDefault && !pc.isDefaultProductClass())
        {
            retVal = getDoublePreference(confirmationType, APIHome.findProductQueryAPI().getDefaultProductClass(), false);
        }
        return retVal;
    }

    private static void setDoublePreference(OrderRiskControlPreferenceTypes confirmationType, ProductClass pc, double value)
    {
        getPrefManager().setPreference(buildPrefName(confirmationType.getBusinessPrefPrefix(), pc), value);
        auditLogConfirmationPropertyChange(confirmationType, pc);
    }

    /**
     * if useDefault is true, the default ProductClass' value will be returned if risk control is disabled for this OrderRiskControlPreferenceType and ProductClass.
     */
    private static int getIntPreference(OrderRiskControlPreferenceTypes confirmationType, ProductClass pc, boolean useDefault)
    {
        int retVal = 0;
        boolean riskControlEnabledForClass = getEnabledPreference(confirmationType, pc, false);
        // if risk control is enabled for this type/class, return the value (even if it's zero) instead of going to the default
        if (riskControlEnabledForClass)
        {
            useDefault = false;
        }
        // when using the default, if this OrderRiskControlPreferenceTypes is disabled for this pc, then return the default value rather than this pc's value
        if (!useDefault || riskControlEnabledForClass)
        {
            String prefName = buildPrefName(confirmationType.getBusinessPrefPrefix(), pc);
            try
            {
                retVal = getPrefManager().getIntPreference(prefName);
            }
            catch (PreferenceNotFoundException e)
            {
                // it's ok if the preference doesn't exist; it just means the user hasn't configured the property for this ProductClass
                GUILoggerHome.find().debug(CATEGORY, GUILoggerBusinessProperty.ORDER_ENTRY, "No int preference found for " + prefName);
            }
        }

        // if this wasn't the default ProductClass, check if default is set
        if (useDefault && !pc.isDefaultProductClass())
        {
            retVal = getIntPreference(confirmationType, APIHome.findProductQueryAPI().getDefaultProductClass(), false);
        }
        return retVal;
    }

    /**
     * Save a boolean preference whose value flags whether the corresponding OrderConfirmationTypes preference is enabled for the ProductClass.
     * @param confirmationType
     * @param pc
     * @param prefEnabled
     */
    private static void setEnabledPreference(OrderRiskControlPreferenceTypes confirmationType, ProductClass pc, boolean prefEnabled)
    {
        String prefName = buildEnablementProperty(confirmationType, pc);
        getPrefManager().setPreference(prefName, prefEnabled);
        auditLogEnablementPropertyChange(confirmationType, pc);
    }

    private static boolean getEnabledPreference(OrderRiskControlPreferenceTypes confirmationType, ProductClass pc, boolean useDefault)
    {
        boolean retVal = false;
        String prefName = buildEnablementProperty(confirmationType, pc);
        try
        {
            retVal = getPrefManager().getBooleanPreference(prefName);
        }
        catch (PreferenceNotFoundException e)
        {
            // it's ok if the preference doesn't exist; it just means the user hasn't configured the property for this ProductClass
            GUILoggerHome.find().debug(CATEGORY, GUILoggerBusinessProperty.ORDER_ENTRY, "No enablement preference found for " + prefName);
        }

        // if this wasn't the default ProductClass, check if default is enabled
        if (useDefault && !retVal && !pc.isDefaultProductClass())
        {
            retVal = getEnabledPreference(confirmationType, APIHome.findProductQueryAPI().getDefaultProductClass(), false);
        }
        return retVal;
    }

    private static void setIntPreference(OrderRiskControlPreferenceTypes confirmationType, ProductClass pc, int value)
    {
        getPrefManager().setPreference(buildPrefName(confirmationType.getBusinessPrefPrefix(), pc), value);
        auditLogConfirmationPropertyChange(confirmationType, pc);
    }

    private static Price getPricePreference(OrderRiskControlPreferenceTypes confirmationType, ProductClass pc, boolean useDefault)
    {
        return DisplayPriceFactory.create(getDoublePreference(confirmationType, pc, useDefault));
    }

    private static void setPricePreference(OrderRiskControlPreferenceTypes confirmationType, ProductClass pc, Price value)
    {
        setDoublePreference(confirmationType, pc, value.toDouble());
    }

    private static void auditLogEnablementPropertyChange(OrderRiskControlPreferenceTypes confirmationType, ProductClass pc)
    {
        String prefName = buildEnablementProperty(confirmationType, pc);
        CBOEPreference pref = getPrefManager().getPreferences().getPreference(prefName);
        if (pref != null)
        {
            GUILoggerHome.find().audit(CATEGORY, "User '" + UserSessionFactory.findUserSession().getUserModel() + "' set " + confirmationType.name() + " enablement BusinessPreference for ProductClass '" + pc.toString() + " (classKey=" + pc.getClassKey() + ") to '" + pref.getValue() + "'");
        }
    }

    private static void auditLogConfirmationPropertyChange(OrderRiskControlPreferenceTypes confirmationType, ProductClass pc)
    {
        CBOEPreference pref = getPrefManager().getPreferences().getPreference(buildPrefName(confirmationType.getBusinessPrefPrefix(), pc));
        if (pref != null)
        {
            GUILoggerHome.find().audit(CATEGORY, "User '" + UserSessionFactory.findUserSession().getUserModel() + "' set " + confirmationType.name() + " confirmation limit BusinessPreference for ProductClass '" + pc.toString() + " (classKey=" + pc.getClassKey() + ") to '" + pref.getValue() + "'");
        }
    }

    private static BusinessPreferenceManager getPrefManager()
    {
        return PreferenceManagerHome.findBusinessPreferenceManager();
    }

    private static String buildPrefName(String prefix, ProductClass pc)
    {
        return CATEGORY + PreferenceConstants.DEFAULT_PATH_SEPARATOR + prefix + PreferenceConstants.DEFAULT_PATH_SEPARATOR + pc.getClassKey();
    }

    private static String buildEnablementProperty(OrderRiskControlPreferenceTypes confirmationType, ProductClass pc)
    {
        return buildPrefName(confirmationType.getBusinessPrefPrefix(), pc) + PreferenceConstants.DEFAULT_PATH_SEPARATOR + "Enabled";
    }
}
