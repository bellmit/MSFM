//
// -----------------------------------------------------------------------------------
// Source file: OrderRiskControlValidationHelper.java
//
// PACKAGE: com.cboe.presentation.properties.orderEntryConfirmation
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2011 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.order.riskControlValidation;

import com.cboe.interfaces.presentation.validation.ValidationResult;
import com.cboe.interfaces.presentation.validation.ValidationErrorCodes;
import com.cboe.interfaces.presentation.order.Order;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.domain.Price;
import com.cboe.presentation.validation.ValidationResultImpl;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.product.ProductHelper;
import com.cboe.idl.cmiOrder.LightOrderEntryStruct;
import com.cboe.idl.cmiOrder.OrderDetailStruct;

import java.util.List;

/**
 * Provides the interface to validate an order against the user-configured risk control preferences.
 */
public class OrderRiskControlValidationHelper
{
    /**
     * Order should not be submitted if the returned ValidationResult contains any error messages.
     */
    public static ValidationResult validateOrder(Order order)
    {
        ValidationResult result = new ValidationResultImpl();
        checkForDuplicateOrders(result, order.getSessionProduct(), order);
        if (result.isValid())
        {
            validate(result, order.getOriginalQuantity(), order.getPrice(), order.getSessionProductClass());
        }
        return result;
    }

    public static ValidationResult validateLightOrder(LightOrderEntryStruct lightOrder)
    {
        ValidationResult result = new ValidationResultImpl();
        SessionProduct product = ProductHelper.getSessionProduct(lightOrder.activeSession, lightOrder.productKey);
        checkForDuplicateOrders(result, product, lightOrder);
        if (result.isValid())
        {
            validate(result, lightOrder.originalQuantity, DisplayPriceFactory.create(lightOrder.Price), product);
        }
        return result;
    }

    private static void validate(ValidationResult result, int qty, Price orderPrice, SessionProduct product)
    {
        ProductClass pc = ProductHelper.getProductClass(product.getProductKeysStruct().classKey);
        validate(result, qty, orderPrice, pc);
    }

    private static void validate(ValidationResult result, int qty, Price orderPrice, ProductClass productClass)
    {
        validateSingleOrderLimits(result, qty, orderPrice, productClass);
        validateGrossTradedLimits(result, productClass);
        validateNetTradedLimits(result, productClass);
    }

    private static void checkForDuplicateOrders(ValidationResult result, SessionProduct product, Order order)
    {
        checkForDuplicateOrders(result, product, order.getOriginalQuantity(), order.getPrice(), order.getSide());
    }

    private static void checkForDuplicateOrders(ValidationResult result, SessionProduct product, LightOrderEntryStruct lightOrder)
    {
        checkForDuplicateOrders(result, product, lightOrder.originalQuantity, DisplayPriceFactory.create(lightOrder.Price), lightOrder.side);
    }

    private static void checkForDuplicateOrders(ValidationResult result, SessionProduct product, int quantity, Price price, char side)
    {
        ProductClass productClass = ProductHelper.getProductClass(product.getProductKeysStruct().classKey);
        if (OrderRiskControlPreferences.isDuplicateOrderCheckEnabled(productClass))
        {
            int millis = OrderRiskControlPreferences.getDuplicateOrderCheckTimeRange(productClass);
            int numDupesAllowed = OrderRiskControlPreferences.getDuplicateOrderLimit(productClass);
            List<OrderDetailStruct> orders = APIHome.findOrderQueryAPI().getOrders(product, quantity, price, side, millis);

            if (orders.size() > 0 && // don't reject if orders.size() == 0 and numDupesAllowed == 0
                    orders.size() >= numDupesAllowed)
            {
                result.setErrorCode(ValidationErrorCodes.ORDER_INVALID);
                result.setErrorMessage(orders.size() + " identical order" + (orders.size() == 1 ? "" : "s") +
                        " (qty, price, side) already entered within last " + OrderRiskControlPreferences.getDuplicateOrderCheckTimeRange(productClass) +
                        " milliseconds - max allowed is " + numDupesAllowed);
            }
        }
    }

    /**
     * Check the Order's quantity and price against the configured limits.
     */
    private static void validateSingleOrderLimits(ValidationResult result, int qty, Price orderPrice, ProductClass productClass)
    {
        // the total dollarValue of the order is the price * qty * 100
        Price dollarValue = DisplayPriceFactory.create(orderPrice.toDouble() * 100 * qty);
        // check against single-order limit
        if (OrderRiskControlPreferences.isSingleOrderQuantityConfirmationEnabled(productClass))
        {
            int maxQuantity = OrderRiskControlPreferences.getSingleOrderQuantityConfirmationLimit(productClass);
            if (qty > maxQuantity)
            {
                String reason = "Qty "+ qty + " is greater than " + maxQuantity;
                result.addConfirmationMessage(reason);
            }
        }

        if (OrderRiskControlPreferences.isSingleOrderDollarValueConfirmationEnabled(productClass))
        {
            Price maxPrice = OrderRiskControlPreferences.getSingleOrderDollarValueConfirmationLimit(productClass);
            if (dollarValue.greaterThan(maxPrice))
            {
                String reason = "Dollar Value $" + dollarValue + " is greater than $" + maxPrice.toString();
                result.addConfirmationMessage(reason);
            }
        }
    }

    /**
     * Check the current gross traded quantity and price against the configured limits.  The Order
     * is really only used to get the ProductClass, and to log order details if the validation
     * fails.
     */
    private static void validateGrossTradedLimits(ValidationResult result, ProductClass productClass)
    {
        // check the daily gross limit (this compares the total gross traded for the day against the user-configured
        // limit. I.e., it doesn't actually take the current order's value into account)
        if (OrderRiskControlPreferences.isDailyGrossQuantityTradedConfirmationEnabled(productClass))
        {
            int grossTradedQuantity = APIHome.findOrderFillCountAPI().getGrossOrderQuantityTraded(productClass);
            int maxQuantity = OrderRiskControlPreferences.getDailyGrossQuantityTradedConfirmationLimit(productClass);
            if (grossTradedQuantity > maxQuantity)
            {
                String reason = "Gross traded qty " + grossTradedQuantity + " has exceeded " + maxQuantity;
                result.addConfirmationMessage(reason);
            }
        }

        // check the daily gross limit (this compares the total gross traded for the day against the user-configured
        // limit. I.e., it doesn't actually take the current order's value into account)
        if (OrderRiskControlPreferences.isDailyGrossDollarValueConfirmationEnabled(productClass))
        {
            Price grossDollarValueTraded = APIHome.findOrderFillCountAPI().getGrossOrderDollarValueTraded(productClass);
            Price maxPrice = OrderRiskControlPreferences.getDailyGrossDollarValueConfirmationLimit(productClass);
            if (grossDollarValueTraded.greaterThan(maxPrice))
            {
                String reason = "Gross traded dollar value $" + grossDollarValueTraded.toString() + " has exceeded $" + maxPrice.toString();
                result.addConfirmationMessage(reason);
            }
        }
    }

    /**
     * Check the current net traded quantity and price (daily cumulative long - short) against the
     * configured limits.  The Order is really only used to get the ProductClass, and to log order
     * details if the validation fails.
     */
    private static void validateNetTradedLimits(ValidationResult result, ProductClass productClass)
    {
        // check the daily net limit; this compares the total net traded for the day against the user-configured
        // limit. I.e., it doesn't actually take the current order's value into account)
        if (OrderRiskControlPreferences.isDailyNetQuantityTradedConfirmationEnabled(productClass))
        {
            int netTradedQuantity = APIHome.findOrderFillCountAPI().getNetOrderQuantityTraded(productClass);
            int maxQuantity = OrderRiskControlPreferences.getDailyNetQuantityTradedConfirmationLimit(productClass);
            if (netTradedQuantity > maxQuantity)
            {
                String reason = "Net traded qty " + netTradedQuantity + " has exceeded " + maxQuantity;
                result.addConfirmationMessage(reason);
            }
        }

        // check the daily net limit; this compares the total net traded for the day against the user-configured
        // limit. I.e., it doesn't actually take the current order's value into account)
        if (OrderRiskControlPreferences.isDailyNetDollarValueConfirmationEnabled(productClass))
        {
            Price netDollarValueTraded = APIHome.findOrderFillCountAPI().getNetOrderDollarValueTraded(productClass);
            Price maxPrice = OrderRiskControlPreferences.getDailyNetDollarValueConfirmationLimit(productClass);
            if (netDollarValueTraded.greaterThan(maxPrice))
            {
                String reason = "Net traded dollar value $" + netDollarValueTraded.toString() + " has exceeded $" + maxPrice.toString();
                result.addConfirmationMessage(reason);
            }
        }
    }
}
