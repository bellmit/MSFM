//
// -----------------------------------------------------------------------------------
// Source file: OrderFillCountAPIImpl.java
//
// PACKAGE: com.cboe.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2011 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import com.cboe.interfaces.presentation.api.OrderFillCountAPI;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.domain.Price;
import com.cboe.util.event.EventChannelListener;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelKey;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.formatters.Sides;
import com.cboe.presentation.common.formatters.ActivityTypes;
import com.cboe.presentation.common.formatters.ActivityFieldTypes;
import com.cboe.presentation.product.ProductHelper;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiConstants.OptionTypes;
import com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct;
import com.cboe.idl.cmiTraderActivity.ActivityRecordStruct;
import com.cboe.idl.cmiTraderActivity.ActivityFieldStruct;
import org.omg.CORBA.UserException;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * Provides an interface to get the current daily counts of the user's Gross and Net order fill
 * quantities and dollar values.
 *
 * The counts are updated as orders are filled or busted.
 */
public class OrderFillCountAPIImpl implements OrderFillCountAPI, EventChannelListener
{
    private static final String CATEGORY = "OrderFillCountAPIImpl";

    private Map<Integer, Integer> grossQuantities;
    private Map<Integer, Integer> netQuantities;

    private Map<Integer, Double> grossDollarValues;
    private Map<Integer, Double> netDollarValues;

    private final Object updateLockObject = new Object();

    public OrderFillCountAPIImpl()
    {
        grossQuantities = Collections.synchronizedMap(new HashMap<Integer, Integer>());
        netQuantities = Collections.synchronizedMap(new HashMap<Integer, Integer>());

        grossDollarValues = Collections.synchronizedMap(new HashMap<Integer, Double>());
        netDollarValues = Collections.synchronizedMap(new HashMap<Integer, Double>());

        initializeAndSubscribe();
    }

    /**
     * Return the user's total cumulative gross dollar value of all orders that have traded today for the ProductClass.
     */
    public Price getGrossOrderDollarValueTraded(ProductClass pc)
    {
        double retVal = 0.0;
        // changed to now using synchronized maps, so we don't need to enclose in a sync block
        Double d = grossDollarValues.get(pc.getClassKey());
        if (d != null)
        {
            retVal = d;
        }
        return DisplayPriceFactory.create(retVal);
    }

    /**
     * Return the user's total gross order quantity that has traded today for the ProductClass.
     */
    public int getGrossOrderQuantityTraded(ProductClass pc)
    {
        int retVal = 0;
        Integer i = grossQuantities.get(pc.getClassKey());
        if (i != null)
        {
            retVal = i;
        }
        return retVal;
    }

    /**
     * Return the user's total cumulative net dollar value of all orders that have traded today for the ProductClass.
     *
     * Net dollar value is the dollar value of long positions (long call/short put, long stock, long futures) - the
     * dollar value of short positions (short call/long put, short stock, short futures).
     */
    public Price getNetOrderDollarValueTraded(ProductClass pc)
    {
        double retVal = 0.0;
        Double d = netDollarValues.get(pc.getClassKey());
        if (d != null)
        {
            retVal = d;
        }
        return DisplayPriceFactory.create(retVal);
    }

    /**
     * Return the user's total net order quantity that has traded today for the ProductClass.
     *
     * Net quantity is long positions (long call/short put, long stock, long futures) - short positions
     * (short call/long put, short stock, short futures)
     */
    public int getNetOrderQuantityTraded(ProductClass pc)
    {
        int retVal = 0;
        Integer i = netQuantities.get(pc.getClassKey());
        if (i != null)
        {
            retVal = i;
        }
        return retVal;
    }

    public void channelUpdate(ChannelEvent event)
    {
        int channelType = ((ChannelKey) event.getChannel()).channelType;
        Object eventData = event.getEventData();

        switch (channelType)
        {
            case ChannelKey.CB_FILLED_REPORT_BY_FIRM:
            {
                processOrderFilledReport((OrderFilledReportStruct) eventData);
                GUILoggerHome.find().debug("OrderFillCountAPIImpl.channelUpdate() received CB_FILLED_REPORT_BY_FIRM", GUILoggerBusinessProperty.ORDER_QUERY, eventData);
                break;
            }
            case ChannelKey.CB_FILLED_REPORT:
            {
                processOrderFilledReport((OrderFilledReportStruct) eventData);
                GUILoggerHome.find().debug("OrderFillCountAPIImpl.channelUpdate() received CB_FILLED_REPORT", GUILoggerBusinessProperty.ORDER_QUERY, eventData);
                break;
            }
            case ChannelKey.CB_ORDER_BUST_REPORT_BY_FIRM:
            {
                processOrderBustReport((OrderBustReportStruct) eventData);
                GUILoggerHome.find().debug("OrderFillCountAPIImpl.channelUpdate() received CB_ORDER_BUST_REPORT_BY_FIRM", GUILoggerBusinessProperty.ORDER_QUERY, eventData);
                break;
            }
            case ChannelKey.CB_ORDER_BUST_REPORT:
            {
                processOrderBustReport((OrderBustReportStruct) eventData);
                GUILoggerHome.find().debug("OrderFillCountAPIImpl.channelUpdate() received CB_ORDER_BUST_REPORT", GUILoggerBusinessProperty.ORDER_QUERY, eventData);
                break;
            }
            case ChannelKey.CB_ORDER_BUST_REINSTATE_REPORT_BY_FIRM:
            {
                processOrderBustReinstateReport((OrderBustReinstateReportStruct) eventData);
                GUILoggerHome.find().debug("OrderFillCountAPIImpl.channelUpdate() received CB_ORDER_BUST_REINSTATE_REPORT_BY_FIRM", GUILoggerBusinessProperty.ORDER_QUERY, eventData);
                break;
            }
            case ChannelKey.CB_ORDER_BUST_REINSTATE_REPORT:
            {
                processOrderBustReinstateReport((OrderBustReinstateReportStruct) eventData);
                GUILoggerHome.find().debug("OrderFillCountAPIImpl.channelUpdate() received CB_ORDER_BUST_REINSTATE_REPORT", GUILoggerBusinessProperty.ORDER_QUERY, eventData);
                break;
            }
            case ChannelKey.CB_ALL_ORDERS:
            case ChannelKey.CB_ALL_ORDERS_V2:
            {
                // do nothing; only interested in fills and busts
                break;
            }
            default:
            {
                GUILoggerHome.find().alarm("OrderFillCountAPIImpl.channelUpdate()", "Received unexpected channelType: " + channelType);
            }
        }
    }

    // update cached counts for each of the OrderBustReportStruct's contained BustReportStructs
    private void processOrderBustReport(OrderBustReportStruct orderBust)
    {
        ProductClass pc = ProductHelper.getProductClass(orderBust.bustedOrder.orderStruct.classKey);
        Product p = ProductHelper.getProduct(orderBust.bustedOrder.orderStruct.productKey);
        for (int i = 0; i < orderBust.bustedReport.length; i++)
        {
            int bustQty = orderBust.bustedReport[i].bustedQuantity;
            char side = orderBust.bustedOrder.orderStruct.side;
            updateCachedCounts(TradeActivityType.Bust, pc, p, side, bustQty, DisplayPriceFactory.create(orderBust.bustedReport[i].price));
        }
    }

    // update cached counts for a bust/reinstate
    private void processOrderBustReinstateReport(OrderBustReinstateReportStruct bustReinstate)
    {
        ProductClass pc = ProductHelper.getProductClass(bustReinstate.reinstatedOrder.orderStruct.classKey);
        Product p = ProductHelper.getProduct(bustReinstate.reinstatedOrder.orderStruct.productKey);
        int bustQty = bustReinstate.bustReinstatedReport.bustedQuantity;
        char side = bustReinstate.reinstatedOrder.orderStruct.side;
        updateCachedCounts(TradeActivityType.Bust, pc, p, side, bustQty, DisplayPriceFactory.create(bustReinstate.bustReinstatedReport.price));
    }

    // update cached counts for each of the OrderFilledReportStruct's contained FilledReportStructs
    private void processOrderFilledReport(OrderFilledReportStruct orderFill)
    {
        ProductClass pc = ProductHelper.getProductClass(orderFill.filledOrder.orderStruct.classKey);
        Product p = ProductHelper.getProduct(orderFill.filledOrder.orderStruct.productKey);
        for (int i = 0; i < orderFill.filledReport.length; i++)
        {
            int fillQty = orderFill.filledReport[i].tradedQuantity;
            char side = orderFill.filledReport[i].side;
            updateCachedCounts(TradeActivityType.Fill, pc, p, side, fillQty, DisplayPriceFactory.create(orderFill.filledReport[i].price));
        }
    }

    private void updateCachedCounts(TradeActivityType tradeActivityType, ProductClass pc, Product p, char side, int qty, Price price)
    {
        // sync block to prevent multiple channel update threads from modifying the maps at the
        // same time; order fills and busts shouldn't be very high frequency, so this shouldn't cause
        // any performance issues
        synchronized (updateLockObject)
        {
            int classKey = pc.getClassKey();
            double orderValue = 100 * qty * price.toDouble();

            // lookup the cached values
            int grossQty = grossQuantities.get(classKey) == null ? 0 : grossQuantities.get(classKey);
            double grossDollarValue = grossDollarValues.get(classKey) == null ? 0.0 : grossDollarValues.get(classKey);
            int netQty = netQuantities.get(classKey) == null ? 0 : netQuantities.get(classKey);
            double netDollarValue = netDollarValues.get(classKey) == null ? 0.0 : netDollarValues.get(classKey);

            // calculate Position
            PositionType position = calculatePosition(p, side);
            StringBuilder debugMsg = new StringBuilder();
            debugMsg.append("updateCachedCounts():\n\tActivityType='").append(tradeActivityType.name()).append("' OrderPosition='").append(position.name()).append("'. Counts are updated as follows: ");
            // calculate new values
            switch (tradeActivityType)
            {
                case Fill:
                    // Gross: add for Fills
                    appendLogging(debugMsg, "Gross Qty", AddOrSubstract.ADD, grossQty, qty, grossQty + qty);
                    grossQty += qty;
                    appendLogging(debugMsg, "Gross Dollar Value", AddOrSubstract.ADD, grossDollarValue, orderValue, grossDollarValue + orderValue);
                    grossDollarValue += orderValue;
                    // Net: add Long positions and subtract Short for Fills
                    if (position == PositionType.Long)
                    {
                        appendLogging(debugMsg, "Net Qty", AddOrSubstract.ADD, netQty, qty, netQty + qty);
                        netQty += qty;

                        appendLogging(debugMsg, "Net Dollar Value", AddOrSubstract.ADD, netDollarValue, orderValue, netDollarValue + orderValue);
                        netDollarValue += orderValue;
                    }
                    else
                    {
                        appendLogging(debugMsg, "Net Qty", AddOrSubstract.SUBTRACT, netQty, qty, netQty - qty);
                        netQty -= qty;

                        appendLogging(debugMsg, "Net Dollar Value", AddOrSubstract.SUBTRACT, netDollarValue, orderValue, netDollarValue - orderValue);
                        netDollarValue -= orderValue;
                    }
                    break;
                case Bust:
                    // Gross: subtract for Busts
                    appendLogging(debugMsg, "Gross Qty", AddOrSubstract.SUBTRACT, grossQty, qty, grossQty - qty);
                    grossQty -= qty;
                    appendLogging(debugMsg, "Gross Dollar Value", AddOrSubstract.SUBTRACT, grossDollarValue, orderValue, grossDollarValue - orderValue);
                    grossDollarValue -= orderValue;
                    // Net: subtract Long positions and add Short for Busts
                    if (position == PositionType.Long)
                    {
                        appendLogging(debugMsg, "Net Qty", AddOrSubstract.SUBTRACT, netQty, qty, netQty - qty);
                        netQty -= qty;

                        appendLogging(debugMsg, "Net Dollar Value", AddOrSubstract.SUBTRACT, netDollarValue, orderValue, netDollarValue - orderValue);
                        netDollarValue -= orderValue;
                    }
                    else
                    {
                        appendLogging(debugMsg, "Net Qty", AddOrSubstract.ADD, netQty, qty, netQty + qty);
                        netQty += qty;

                        appendLogging(debugMsg, "Net Dollar Value", AddOrSubstract.ADD, netDollarValue, orderValue, netDollarValue + orderValue);
                        netDollarValue += orderValue;
                    }
                    break;
            }

            GUILoggerHome.find().audit(CATEGORY, debugMsg.toString());

            // save the updated Gross values
            grossQuantities.put(classKey, grossQty);
            grossDollarValues.put(classKey, grossDollarValue);

            // save the updated Net values
            netDollarValues.put(classKey, netDollarValue);
            netQuantities.put(classKey, netQty);
        }
    }

    private void initializeAndSubscribe()
    {
        try
        {
            // Can't use OrderQueryAPI.getOrderFilledReports() or getOrderBustReports() to populate
            // the cache because it only returns the Fills that have been received since the user
            // logged in the current session, so to be guaranteed we get all the user's fill and
            // bust data, we need to go off order history for each of the user's orders
//            OrderFilledReportStruct[] fills = APIHome.findOrderQueryAPI().getOrderFilledReports(this);
//            for (OrderFilledReportStruct fill : fills)
//            {
//                updateCounts(fill);
//            }

//            OrderBustReportStruct[] busts = APIHome.findOrderQueryAPI().getOrderBustReports(this);
//            for (OrderBustReportStruct bust : busts)
//            {
//                updateCounts(bust);
//            }

            // subscribe for new fills and busts
            APIHome.findOrderQueryAPI().subscribeOrderFilledReport(this);
            APIHome.findOrderQueryAPI().subscribeOrderBustReport(this);

            // populate the counts for previous fills and busts
            OrderDetailStruct[] allOrders = APIHome.findOrderQueryAPI().getAllOrders(this);
            for (OrderDetailStruct order : allOrders)
            {
                // Can't use OrderQueryAPI.getOrderFilledReportsByOrderId() because it only returns
                // the Fills that have been received since the user logged in the current session,
                // so to be guaranteed we get all the user's fill data, we need to go off order
                // history for each of the user's orders
                //    OrderFilledReportStruct[] orderFills = APIHome.findOrderQueryAPI().getOrderFilledReportsByOrderId(OrderIdFactory.createOrderId(order.orderStruct.orderId));
                //    for (OrderFilledReportStruct orderFill : orderFills)
                //    {
                //        updateCounts(orderFill);
                //    }
                ProductClass pc = ProductHelper.getProductClass(order.orderStruct.classKey);
                Product p = ProductHelper.getProduct(order.orderStruct.productKey);
                ActivityHistoryStruct orderHistory = APIHome.findOrderQueryAPI().queryOrderHistory(order.orderStruct.orderId);
                char side = order.orderStruct.side;
                for (ActivityRecordStruct ars : orderHistory.activityRecords)
                {
                    if (ars.entryType == ActivityTypes.FILL_ORDER)
                    {
                        int fillQty = 0;
                        Price price = null;
                        for (ActivityFieldStruct afs : ars.activityFields)
                        {
                            if (afs.fieldType == ActivityFieldTypes.TRADED_QUANTITY)
                            {
                                fillQty = Integer.parseInt(afs.fieldValue);
                            }
                            else if (afs.fieldType == ActivityFieldTypes.PRICE)
                            {
                                price = DisplayPriceFactory.create(Double.parseDouble(afs.fieldValue));
                            }
                            // don't bother with the rest of the fields once we've found what we need
                            if (fillQty > 0 && price != null)
                            {
                                break;
                            }
                        }
                        if (price != null && fillQty > 0)
                        {
                            updateCachedCounts(TradeActivityType.Fill, pc, p, side, fillQty, price);
                        }
                        else
                        {
                            GUILoggerHome.find().audit("OrderFillCountAPIImpl.initializeAndSubscribe()",
                                    "Could not accurately update position data: Did not find required ActivityFields in ActivityRecords for Order br/seq='" +
                                    order.orderStruct.orderId.branch + ":" + order.orderStruct.orderId.branchSequenceNumber + "' high:low=" +
                                            order.orderStruct.orderId.highCboeId + ":" + order.orderStruct.orderId.lowCboeId +
                                            ActivityFieldTypes.toString(ActivityFieldTypes.TRADED_QUANTITY) + "='" + fillQty + "' " +
                                            ActivityFieldTypes.toString(ActivityFieldTypes.PRICE) + "='" + price + "'");
                        }
                    }
                    else if (ars.entryType == ActivityTypes.BUST_ORDER_FILL)
                    {
                        int bustQty = 0;
                        Price price = null;
                        for (ActivityFieldStruct afs : ars.activityFields)
                        {
                            if (afs.fieldType == ActivityFieldTypes.BUSTED_QUANTITY)
                            {
                                bustQty = Integer.parseInt(afs.fieldValue);
                            }
                            else if (afs.fieldType == ActivityFieldTypes.PRICE)
                            {
                                price = DisplayPriceFactory.create(Double.parseDouble(afs.fieldValue));
                            }
                            // don't bother with the rest of the fields once we've found what we need
                            if (bustQty > 0 && price != null)
                            {
                                break;
                            }
                        }
                        if (price != null && bustQty > 0)
                        {
                            updateCachedCounts(TradeActivityType.Bust, pc, p, side, bustQty, price);
                        }
                        else
                        {
                            GUILoggerHome.find().audit("OrderFillCountAPIImpl.initializeAndSubscribe()",
                                    "Could not accurately update position data: Did not find required ActivityFields in ActivityRecords for Order br/seq='" +
                                    order.orderStruct.orderId.branch + ":" + order.orderStruct.orderId.branchSequenceNumber + "' high:low=" +
                                            order.orderStruct.orderId.highCboeId + ":" + order.orderStruct.orderId.lowCboeId +
                                            ActivityFieldTypes.toString(ActivityFieldTypes.BUSTED_QUANTITY) + "='" + bustQty + "' " +
                                            ActivityFieldTypes.toString(ActivityFieldTypes.PRICE) + "='" + price + "'");
                        }
                    }
                }
            }
        }
        catch (UserException e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Error subscribing for user's orders; will not be able to perform risk control validation based on the daily traded net/gross quantity and dollar value.");
        }
    }

    // calculate the postition for this Product and Side
    private PositionType calculatePosition(Product p, char side)
    {
        /*
            Options:
                Long positions = buy calls or sell puts
                Short positions = sell calls or buy puts
            Equities and Futures:
                Long positions = buy
                Short positions = sell
         */
        PositionType retVal;

        boolean isBuy = Sides.isBuyEquivalent(side);
        boolean isSell = !isBuy;

        switch (p.getProductType())
        {
            case ProductTypes.OPTION:
            {
                boolean isCall = p.getProductNameStruct().optionType == OptionTypes.CALL;
                boolean isPut = !isCall;

                if ((isBuy && isCall) ||
                        (isSell && isPut))
                {
                    retVal = PositionType.Long;
                }
                else // (isSell && isCall) || (isBuy && isPut)
                {
                    retVal = PositionType.Short;
                }
                break;
            }
            default:  // Equities, Futures, etc.
            {
                if (isBuy)
                {
                    retVal = PositionType.Long;
                }
                else
                {
                    retVal = PositionType.Short;
                }
                break;
            }
        }
        GUILoggerHome.find().information(CATEGORY, GUILoggerBusinessProperty.ORDER_QUERY, "calculatePosition(): " + Sides.toString(side, Sides.BUY_SELL_FORMAT) + " '" + p.toString() + "' = " + retVal.name());

        return retVal;
    }

    private void appendLogging(StringBuilder sb, String desc, AddOrSubstract operation, Number previous, Number update, Number result)
    {
        sb.append("\n\t\t").append(operation.name()).append(' ').append(desc).append(": ").append(previous).append(' ').append(operation.operator);
        sb.append(' ').append(update).append(' ').append('=').append(' ').append(result);
    }

    private enum PositionType
    {
        Long, Short;
    }

    private enum TradeActivityType
    {
        Fill, Bust;
    }

    enum AddOrSubstract
    {
        ADD('+'),
        SUBTRACT('-');

        public char operator;
        AddOrSubstract(char operator)
        {
            this.operator = operator;
        }
    }
}
