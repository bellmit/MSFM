//
// -----------------------------------------------------------------------------------
// Source file: StrategyLegFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import java.text.DecimalFormat;

import com.cboe.idl.cmiOrder.LegOrderDetailStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStructV2;

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.common.formatters.ProductFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.StrategyLegFormatStrategy;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.StrategyLeg;

import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Implements the StrategyLegFormatStrategy
 */
class StrategyLegFormatter extends Formatter implements StrategyLegFormatStrategy
{
    /**
     * Constructor, defines styles and sets initial default style
     */
    public StrategyLegFormatter()
    {
        super();

        addStyle(FULL_STRATEGY_LEG_NAME, FULL_STRATEGY_LEG_DESCRIPTION);
        addStyle(BRIEF_STRATEGY_LEG_NAME, BRIEF_STRATEGY_LEG_DESCRIPTION);
        setDefaultStyle(FULL_STRATEGY_LEG_NAME);
    }

    /**
     * Formats a Strategy Leg
     * @param strategy leg to format
     */
    public String format(StrategyLeg leg)
    {
        return formatFullStrategyLegName(leg);
    }

    /**
     * Formats a Strategy Leg
     * @param strategy leg to format
     * @param style name
     */
    public String format(StrategyLeg leg, String style)
    {
        if (style == BRIEF_STRATEGY_LEG_NAME)
        {
            return formatBriefStrategyLegName(leg);                    
        }
        return formatFullStrategyLegName(leg);
    }

    /**
     * Formats a Product
     * @param product to format
     * @param LegOrderDetail that provides the price
     */
    public String format(StrategyLeg leg, LegOrderEntryStructV2 legOrdEntryStruct)
    {
        return formatStrategyLegNameAndPrice(leg, legOrdEntryStruct);
    }

    /**
     * Formats a strategy leg
     * @param StrategyLeg to format (from product; provides the ratio)
     * @param LegOrderDetailStruct that provides the side
     */
    public String format(StrategyLeg leg, LegOrderDetailStruct legOrdDetailStruct)
    {
        return formatBriefStrategyLegName(leg, legOrdDetailStruct);
    }

    /**
     * Formats a Strategy using BRIEF_STRATEGY_LEG_NAME style
     * @param strategy leg to format
     */
    protected String formatBriefStrategyLegName(StrategyLeg leg)
    {
        Product legProduct = leg.getProduct();
        ProductFormatStrategy productFormatter = CommonFormatFactory.getProductFormatStrategy();

        StringBuffer returnBuffer = new StringBuffer();
        returnBuffer.append(leg.getRatioQuantity());
        returnBuffer.append(": " + leg.getSide());
        returnBuffer.append(" " + productFormatter.format(legProduct, ProductFormatStrategy.PRODUCT_NAME_BRIEF_TYPE));
        return returnBuffer.toString();
    }

    /**
     * Formats a Strategy using BRIEF_STRATEGY_LEG_NAME style
     * @param StrategyLeg leg to format (from product)
     * @param LegOrderDetailStruct leg to format (from order)
     */
    protected String formatBriefStrategyLegName(StrategyLeg leg, LegOrderDetailStruct legDetail)
    {
        String returnStr = "";

        if(leg.getProductKey() != legDetail.productKey)
        {
            GUILoggerHome.find()
                    .alarm("StrategyLegFormatter: strategy leg products given do not match!" +
                           "(product keys: " + leg.getProductKey() + ", " + legDetail.productKey + ")");
        }
        else
        {
            Product legProduct = leg.getProduct();
            ProductFormatStrategy productFormatter = CommonFormatFactory.getProductFormatStrategy();

            StringBuffer returnBuffer = new StringBuffer();
            returnBuffer.append(leg.getRatioQuantity());
            returnBuffer.append(": " + legDetail.side);
            returnBuffer.append(" " + productFormatter
                    .format(legProduct, ProductFormatStrategy.PRODUCT_NAME_BRIEF_TYPE));
            returnBuffer.append(" " + PositionEffects.toString(legDetail.positionEffect));
            returnStr = returnBuffer.toString();
        }
        return returnStr;
    }

    /**
     * Formats a Strategy using FULL_STRATEGY_LEG_NAME style
     * @param strategy leg to format
     */
    protected String formatFullStrategyLegName(StrategyLeg leg)
    {
        Product legProduct = leg.getProduct();
        ProductFormatStrategy productFormatter = CommonFormatFactory.getProductFormatStrategy();

        StringBuffer returnBuffer = new StringBuffer();
        returnBuffer.append(leg.getRatioQuantity());
        returnBuffer.append(": " + Utility.sideToString(leg.getSide()) + " ");
        returnBuffer.append(" " + productFormatter.format(legProduct));
        return returnBuffer.toString();
    }

    /**
     * Formats a Strategy using FULL_STRATEGY_LEG_NAME style
     * @param strategy leg to format
     * @param LegOrderEntryStruct that contains the price for the strategy leg above (product IDs must match!)
     */
    protected String formatStrategyLegNameAndPrice(StrategyLeg leg, LegOrderEntryStructV2 legOrdEntryStruct)
    {
        String legPriceStr, returnStr;

        if (leg.getProductKey() != legOrdEntryStruct.legOrderEntry.productKey)
        {
            GUILoggerHome.find().alarm("StrategyLegFormatter: strategy leg products given do not match!"
                    + "(product keys: " + leg.getProductKey() + ", " + legOrdEntryStruct.legOrderEntry.productKey + ")");
            legPriceStr =  "";
        }
        else
        {
            DecimalFormat numberFormat = new DecimalFormat("0.00");
            Price legPrice = DisplayPriceFactory.create(legOrdEntryStruct.legOrderEntry.mustUsePrice);
            legPriceStr = numberFormat.format(legPrice.toDouble());
        }

        returnStr = formatFullStrategyLegName(leg) + "  " + legPriceStr;
        return returnStr;
    }

}
