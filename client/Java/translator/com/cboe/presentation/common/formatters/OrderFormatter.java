//
// -----------------------------------------------------------------------------------
// Source file: OrderFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.idl.cmiConstants.ExtensionFields;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiConstants.ReportTypes;
import com.cboe.idl.cmiOrder.CancelReportStruct;
import com.cboe.idl.cmiOrder.ContraPartyStruct;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiOrder.LegOrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderContingencyStruct;
import com.cboe.idl.cmiOrder.OrderFilledReportStruct;
import com.cboe.idl.cmiOrder.OrderStruct;

import com.cboe.interfaces.presentation.common.formatters.CancelReasonFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ContingencyFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.DateFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.OrderExtensionsFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.OrderFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ProductClassFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ProductFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.VolumeFormatStrategy;
import com.cboe.interfaces.presentation.omt.OrderCancelMessageElement;
import com.cboe.interfaces.presentation.order.Order;
import com.cboe.interfaces.presentation.order.OrderId;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.SessionProduct;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.product.ProductHelper;

public class OrderFormatter extends Formatter implements OrderFormatStrategy
{
    private final String Category = getClass().getName();
    private VolumeFormatStrategy volumeFormatter = FormatFactory.getVolumeFormatStrategy();
    private ProductClassFormatStrategy productClassFormatter = FormatFactory.getProductClassFormatStrategy();
    private ProductFormatStrategy productFormatter = FormatFactory.getProductFormatStrategy();
    private CancelReasonFormatStrategy cancelReasonFormatter = FormatFactory.getCancelReasonFormatStrategy();
    private ContingencyFormatStrategy contingencyFormatter = FormatFactory.getContingencyFormatStrategy();

    OrderFormatter()
    {
        addStyle(FULL_INFO_NAME, FULL_INFO_DESCRIPTION);
        addStyle(FULL_INFO_TWO_COLUMN_NAME, FULL_INFO_TWO_COLUMN_DESCRIPTION);
        addStyle(BRIEF_INFO_NAME, BRIEF_INFO_DESCRIPTION);
        addStyle(BRIEFER_INFO_NAME, BRIEFER_INFO_DESCRIPTION);
        addStyle(BRIEF_INFO_LEAVES_NAME, BRIEF_INFO_LEAVES_DESCRIPTION);
        addStyle(BRIEFER_INFO_LEAVES_NAME, BRIEFER_INFO_LEAVES_DESCRIPTION);
        addStyle(BRIEF_INFO_NAME_OMT, BRIEF_INFO_DESCRIPTION_OMT);
        addStyle(BRIEF_INFO_FBSCID, BRIEF_INFO_DESCRIPTION_FBSCID);
        addStyle(HELP_DESK_INFO, HELP_DESK_INFO_DESCRIPTION);

        setDefaultStyle(FULL_INFO_NAME);
    }

    public String format(CancelReportStruct orderCancel)
    {
        return format(orderCancel, getDefaultStyle());
    }

    public String format(CancelReportStruct orderCancel, String styleName)
    {
        validateStyle(styleName);

        if (styleName.equalsIgnoreCase(FULL_INFO_NAME)
                || styleName.equalsIgnoreCase(FULL_INFO_TWO_COLUMN_NAME))
        {
            Utility.portWarningPorted(Category + ".format(CancelReportStruct)");                                        // TODO: remove when Jasper port is completed
        }

        if (styleName.equalsIgnoreCase(FULL_INFO_NAME))                                                                  // TODO: remove when Jasper port is completed
        {
            return formatCancelReportWithFullInfo(orderCancel);
        }
        else if (styleName.equalsIgnoreCase(FULL_INFO_TWO_COLUMN_NAME))                                                  // TODO: remove when Jasper port is completed
        {
            return formatCancelReportWithFullInfoTwoCol(orderCancel);
        }
        else if(styleName.equalsIgnoreCase(BRIEF_INFO_NAME))
        {
            return formatCancelReportWithBriefInfo(orderCancel);
        }
        else if(styleName.equalsIgnoreCase(BRIEFER_INFO_NAME))
        {
            return formatCancelReportWithBrieferInfo(orderCancel);
        }
        else if(styleName.equalsIgnoreCase(BRIEF_INFO_NAME_OMT))
        {
            return formatCancelReportForOMT(orderCancel);
        }
        else if(styleName.equalsIgnoreCase(HELP_DESK_INFO))
        {
            return formatCancelReportForHelpDesk(orderCancel);
        }
        return "";
    }

    private String formatCancelReportForHelpDesk(CancelReportStruct orderCancel)
    {
        StringBuffer orderCancelText = new StringBuffer(1000);
        Product product = null;
            short reportType = orderCancel.cancelReportType;
            String strategyDesc;
            String cxlRejDesc;
            String rejDesc;
            String cancelDesc;
            String cxlQtyDesc;
            String totCxlQtyDesc;

                strategyDesc = "STRATEGY LEG ORDER CANCEL ";
                cxlRejDesc   = "ORDER CANCEL REQUEST REJECTED - ";
                rejDesc      = "ORDER REJECTED - ";
                cancelDesc   = "ORDER CANCEL - ";
                cxlQtyDesc   = "canceled, ";
        totCxlQtyDesc = "total canceled";

        switch(reportType)
            {
            case ReportTypes.STRATEGY_LEG_REPORT:
                SessionProduct legProduct = ProductHelper
                        .getSessionProduct(orderCancel.sessionName, orderCancel.productKey);
                orderCancelText.append(buildStringForProduct(strategyDesc, legProduct, orderCancel.productKey,
                                                             orderCancel.sessionName));
                break;
            case ReportTypes.CANCEL_ORDER_REQUEST_REJECT:
                orderCancelText.append(cxlRejDesc);
                break;
            case ReportTypes.NEW_ORDER_REJECT:
                orderCancelText.append(rejDesc);
                break;
            default:
                orderCancelText.append(cancelDesc);
                break;
        }

        if(reportType != ReportTypes.STRATEGY_LEG_REPORT)
        {
            try
            {
                product = APIHome.findProductQueryAPI().getProductByKey(orderCancel.productKey);
            }
            catch(Exception e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }

            if(product != null)
            {
                int classKey = product.getProductKeysStruct().classKey;
                try
                {
                    ProductClass productClass =
                            APIHome.findProductQueryAPI().getProductClassByKey(classKey);
                    if(productClass != null)
                    {
                        orderCancelText.append(productClassFormatter.format(productClass,
                                                                          ProductClassFormatter.CLASS_TYPE_NAME))
                                .append(' ');
                    }
                }
                catch(Exception e)
                {
                    GUILoggerHome.find().exception(Category + ".format()", "", e);
                }
                orderCancelText.append(FormatFactory.buildSeriesName(orderCancel.productKey, Category))
                            .append(' ');
            }
        }

        orderCancelText.append(volumeFormatter.format(orderCancel.cancelledQuantity)).append(' ');
        orderCancelText.append(cxlQtyDesc);
        orderCancelText.append(volumeFormatter.format(orderCancel.totalCancelledQuantity))
                .append(' ');
        orderCancelText.append(totCxlQtyDesc);
        return orderCancelText.toString();
    }

    private String formatCancelReportForOMT(CancelReportStruct orderCancel)
    {
        StringBuffer orderCancelText = new StringBuffer(1000);
            short reportType = orderCancel.cancelReportType;
            switch(reportType)
            {
                case ReportTypes.STRATEGY_LEG_REPORT:
                    SessionProduct legProduct = ProductHelper
                            .getSessionProduct(orderCancel.sessionName, orderCancel.productKey);
                    orderCancelText.append(buildStringForProduct("STRATEGY LEG ",
                                                                 legProduct, orderCancel.productKey,
                                                                 orderCancel.sessionName));
                    break;
                case ReportTypes.CANCEL_ORDER_REQUEST_REJECT:
                    orderCancelText.append("ORDER CANCEL REQUEST REJECTED - ");
                    break;
                case ReportTypes.NEW_ORDER_REJECT:
                    orderCancelText.append("ORDER REJECTED - ");
                    break;
                default:
                    break;
            }
            orderCancelText.append(volumeFormatter.format(orderCancel.cancelledQuantity))
                    .append(' ');
            orderCancelText.append("cancelled, ");
            orderCancelText.append(volumeFormatter.format(orderCancel.totalCancelledQuantity))
                    .append(' ');
            orderCancelText.append("total cancelled.");
            orderCancelText.append(" Cancel Reason: ")
                    .append(cancelReasonFormatter.format(orderCancel.cancelReason));
        return orderCancelText.toString();
    }

    private String formatCancelReportWithBriefInfo(CancelReportStruct orderCancel)
    {
        StringBuffer orderCancelText = new StringBuffer(1000);
        short reportType = orderCancel.cancelReportType;

        String strategyDesc = "STRATEGY LEG ORDER CANCEL ";
        String cxlRejDesc   = "ORDER CANCEL REQUEST REJECTED - ";
        String rejDesc      = "ORDER REJECTED - ";
        String cancelDesc   = "ORDER CANCEL - ";
        String cxlQtyDesc   = "canceled, ";
        String totCxlQtyDesc= "total canceled";

        switch(reportType)
            {
                case ReportTypes.STRATEGY_LEG_REPORT:
                SessionProduct legProduct = ProductHelper
                        .getSessionProduct(orderCancel.sessionName, orderCancel.productKey);
                orderCancelText.append(buildStringForProduct(strategyDesc,
                                                          legProduct, orderCancel.productKey,
                                                          orderCancel.sessionName));
                break;
            case ReportTypes.CANCEL_ORDER_REQUEST_REJECT:
                orderCancelText.append(cxlRejDesc);
                break;
            case ReportTypes.NEW_ORDER_REJECT:
                orderCancelText.append(rejDesc);
                break;
            default:
                orderCancelText.append(cancelDesc);
                break;
        }
        orderCancelText.append(volumeFormatter.format(orderCancel.cancelledQuantity)).append(' ');
        orderCancelText.append(cxlQtyDesc);
        orderCancelText.append(volumeFormatter.format(orderCancel.totalCancelledQuantity)).append(' ');
        orderCancelText.append(totCxlQtyDesc);
        return orderCancelText.toString();
    }

    private String formatCancelReportWithBrieferInfo(CancelReportStruct orderCancel)
    {
        StringBuffer orderCancelText = new StringBuffer(1000);
        short reportType = orderCancel.cancelReportType;

        String cxlRejDesc = "CREJ ";
        String rejDesc = "REJ ";
        String cxlQtyDesc = "Cxl ";
        String totCxlQtyDesc = ", totCxl ";
        String leavesDesc = ", lvs ";

        switch(reportType)
        {
            case ReportTypes.CANCEL_ORDER_REQUEST_REJECT:
                orderCancelText.append(cxlRejDesc);
                break;
            case ReportTypes.NEW_ORDER_REJECT:
                orderCancelText.append(rejDesc);
                break;
            default:
                break;
        }
        orderCancelText.append(cxlQtyDesc);
        orderCancelText.append(volumeFormatter.format(orderCancel.cancelledQuantity));
        orderCancelText.append(totCxlQtyDesc);
        orderCancelText.append(volumeFormatter.format(orderCancel.totalCancelledQuantity));
        orderCancelText.append(leavesDesc);
        return orderCancelText.toString();
    }

    private String formatCancelReportWithFullInfoTwoCol(CancelReportStruct orderCancel)
    {
        StringBuffer orderCancelText = new StringBuffer(1000);
        int column2Margin = 38;
        int sizeFieldAdded = 0;

        ProductClass productClass = null;
        Product product = null;
        try
            {
                orderCancelText.append("Class: ");

            // CBOqa02917 - Used to show the strategy name, but now shows the leg info
            product = APIHome.findProductQueryAPI().getProductByKey(orderCancel.productKey);

            if (product != null)
            {
                productClass = APIHome.findProductQueryAPI().getProductClassByKey(product.getProductKeysStruct().classKey);
            }
            if (productClass != null)
            {
                sizeFieldAdded = addFieldWithSizeLimit(orderCancelText, productClassFormatter.format(productClass, productClassFormatter.CLASS_TYPE_NAME), column2Margin - 1 - 7, false);
                orderCancelText = appendSpace(orderCancelText, column2Margin - 7 - sizeFieldAdded);
            } else
            {
                orderCancelText.append("(Not Available)");
                orderCancelText = appendSpace(orderCancelText, column2Margin - 7 - 15);
            }
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(Category + ".format()", "", e);
            orderCancelText.append("(Not Available)");
            orderCancelText = appendSpace(orderCancelText, column2Margin - 7 - 15);
        }

        orderCancelText.append("Product: ");
        if (product != null)
        {
            String productString = FormatFactory.getFormattedProduct(product);
            orderCancelText.append(productString).append('\n');
        } else
        {
            orderCancelText.append("(Not Available)").append('\n');
        }

        orderCancelText = appendSpace(orderCancelText, column2Margin);
        orderCancelText.append("Underlying Symbol: ");
        if (productClass != null)
        {
            orderCancelText.append(productClass.getUnderlyingProduct().getProductNameStruct().productSymbol).append('\n');
        } else
        {
            orderCancelText.append("(Not Available)").append('\n');
        }

        orderCancelText.append("Cancelled Vol: ");
        sizeFieldAdded = addFieldWithSizeLimit(orderCancelText, volumeFormatter.format(orderCancel.cancelledQuantity), column2Margin - 1 - 15, false);
        orderCancelText = appendSpace(orderCancelText, column2Margin - 15 - sizeFieldAdded);

        orderCancelText.append("Too Late For Cancel Vol: ").append(volumeFormatter.format(orderCancel.tlcQuantity)).append('\n');

        orderCancelText.append("Total Cancelled Vol: ");
        sizeFieldAdded = addFieldWithSizeLimit(orderCancelText, volumeFormatter.format(orderCancel.totalCancelledQuantity), column2Margin - 1 - 21, false);
        orderCancelText = appendSpace(orderCancelText, column2Margin - 21 - sizeFieldAdded);

        orderCancelText.append("Mismatched Vol: ").append(volumeFormatter.format(orderCancel.mismatchedQuantity)).append('\n');

        orderCancelText.append("Cancel Reason: ");
        sizeFieldAdded = addFieldWithSizeLimit(orderCancelText, cancelReasonFormatter.format(orderCancel.cancelReason), column2Margin - 1 - 15, false);
        orderCancelText = appendSpace(orderCancelText, column2Margin - 15 - sizeFieldAdded);

        orderCancelText.append("Session: ").append(orderCancel.sessionName).append('\n');

        orderCancelText.append("Date/Time: ").append(Utility.toString(orderCancel.timeSent, "yyyy/MM/dd HH:mm:ss.S"));
        return orderCancelText.toString();
    }

    private String formatCancelReportWithFullInfo(CancelReportStruct orderCancel)
    {
        StringBuffer orderCancelText = new StringBuffer(1000);
        ProductClass productClass = null;
        Product product = null;
        try
            {
                orderCancelText.append("Class: ");

            product = APIHome.findProductQueryAPI().getProductByName(APIHome.findOrderQueryAPI().getOrderById(orderCancel.orderId).productInformation);
            if (product != null)
            {
                productClass = APIHome.findProductQueryAPI().getProductClassByKey(product.getProductKeysStruct().classKey);
            }
            if (productClass != null)
            {
                orderCancelText.append(productClassFormatter.format(productClass, productClassFormatter.CLASS_TYPE_NAME)).append('\n');
                orderCancelText.append("Underlying Symbol: ").append(productClass.getUnderlyingProduct().getProductNameStruct().productSymbol).append('\n');
            } else
            {
                orderCancelText.append("(Not Available)").append('\n');
                orderCancelText.append("Underlying Symbol: ").append("(Not Available)").append('\n');
            }
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(Category + ".format()", "", e);
            orderCancelText.append("(Not Available)").append('\n');
            orderCancelText.append("Underlying Symbol: ").append("(Not Available)").append('\n');
        }

        orderCancelText.append("Product: ");
        if (product != null)
        {
            String productString = FormatFactory.getFormattedProduct(product);
            orderCancelText.append(productString).append('\n');
        } else
        {
            orderCancelText.append("(Not Available)").append('\n');
        }

        orderCancelText.append("Cancelled Vol: ").append(volumeFormatter.format(orderCancel.cancelledQuantity)).append('\n');
        orderCancelText.append("Too Late For Cancel Vol: ").append(volumeFormatter.format(orderCancel.tlcQuantity)).append('\n');
        orderCancelText.append("Total Cancelled Vol: ").append(volumeFormatter.format(orderCancel.totalCancelledQuantity)).append('\n');
        orderCancelText.append("Mismatched Vol: ").append(volumeFormatter.format(orderCancel.mismatchedQuantity)).append('\n');
        orderCancelText.append("Cancel Reason: ").append(cancelReasonFormatter.format(orderCancel.cancelReason)).append('\n');
        orderCancelText.append("Session: ").append(orderCancel.sessionName).append('\n');
        orderCancelText.append("Date/Time: ").append(Utility.toString(orderCancel.timeSent, "yyyy/MM/dd HH:mm:ss.S")).append('\n');
        return orderCancelText.toString();
    }

    public String format(CancelReportStruct orderCancel, OrderStruct order, String styleName)
    {
        validateStyle(styleName);
        StringBuffer orderCancelText = new StringBuffer(1000);

        VolumeFormatStrategy volumeFormatter = FormatFactory.getVolumeFormatStrategy();
        CancelReasonFormatStrategy cancelReasonFormatter = FormatFactory.getCancelReasonFormatStrategy();

        if(styleName.equalsIgnoreCase(BRIEF_INFO_NAME_OMT))
        {
            short reportType = orderCancel.cancelReportType;
            switch(reportType)
            {
                case ReportTypes.STRATEGY_LEG_REPORT:
                    SessionProduct legProduct = ProductHelper
                            .getSessionProduct(orderCancel.sessionName, orderCancel.productKey);
                    orderCancelText.append(buildStringForProduct("STRATEGY LEG ", legProduct,
                                                                 orderCancel.productKey,
                                                                 orderCancel.sessionName));
                    break;
                case ReportTypes.CANCEL_ORDER_REQUEST_REJECT:
                    orderCancelText.append("ORDER CANCEL REQUEST REJECTED - ");
                    break;
                case ReportTypes.NEW_ORDER_REJECT:
                    orderCancelText.append("ORDER REJECTED - ");
                    break;
                default:
                    break;
            }
            orderCancelText.append(volumeFormatter.format(orderCancel.cancelledQuantity))
                    .append(' ');
            orderCancelText.append("cancelled, ");
            orderCancelText.append(volumeFormatter.format(orderCancel.totalCancelledQuantity))
                    .append(' ');
            orderCancelText.append("total cancelled, ");
            orderCancelText.append(volumeFormatter.format(order.leavesQuantity));
            orderCancelText.append(" remaining.");
            orderCancelText.append(" Cancel Reason: ")
                    .append(cancelReasonFormatter.format(orderCancel.cancelReason));
        }
        return orderCancelText.toString();
    }

    public String format(OrderFilledReportStruct orderFilled, int reportIndex)
    {
        return format(orderFilled, getDefaultStyle(), reportIndex);
    }

    public String format(FilledReportStruct orderFill, OrderContingencyStruct contingency, String styleName)
    {
        validateStyle(styleName);

        if (styleName.equalsIgnoreCase(FULL_INFO_NAME)
                || styleName.equalsIgnoreCase(FULL_INFO_TWO_COLUMN_NAME))
        {
            Utility.portWarningPorted(Category + ".format(FilledReportStruct,OrderContingencyStruct)");                 // TODO: remove when Jasper port is completed
        }

        if (styleName.equalsIgnoreCase(FULL_INFO_NAME))                                                                  // TODO: remove when Jasper port is completed
        {
            return formatFilledReportStructWithFullInfo(orderFill);
        }
        else if (styleName.equalsIgnoreCase(FULL_INFO_TWO_COLUMN_NAME))                                                  // TODO: remove when Jasper port is completed
        {
            return formatFilledReportStructWithFullInfoTwoCol(orderFill);
        } else if (styleName.equalsIgnoreCase(BRIEF_INFO_NAME))
        {
            return formatFilledReportStructWithBriefInfo(orderFill, contingency);
        } else if (styleName.equalsIgnoreCase(BRIEF_INFO_LEAVES_NAME))
        {
            return formatFilledReportBriefInfoLeaves(orderFill, contingency);
        }
        else if(styleName.equalsIgnoreCase(BRIEF_INFO_NAME_OMT))
        {
            return formatFilledReportForOMT(orderFill, contingency);
        }
        else if(styleName.equalsIgnoreCase(HELP_DESK_INFO))
        {
            return formatFilledReportForHelpDesk(orderFill, contingency);
        }
        return "";
    }

    private String formatFilledReportForHelpDesk(FilledReportStruct orderFill,
                                                     OrderContingencyStruct contingency)
    {
        StringBuffer orderFillText = new StringBuffer(1000);
        short reportType = orderFill.fillReportType;
        Product product = null;
        String strategyDesc;
        String fillRejDesc;
        String fillDesc;
        String leavesDesc;
        String side;
        boolean showContingency = contingency != null;
        boolean showTradeId = true;
        boolean showOrsid = true;
        boolean showLeaves = true;
        boolean showSide = true;
        strategyDesc = "STRATEGY LEG ORDER FILLED ";
        fillRejDesc = "ORDER FILLED REJECT - ";
        fillDesc = "ORDER FILLED - ";
        leavesDesc = "; leaves ";
        side = Utility.sideToString(orderFill.side);

        switch(reportType)
        {
            case ReportTypes.STRATEGY_LEG_REPORT:
                /**
                 * Since the session product lookup for the stock leg of a buy-write order will
                 * likely fail, since session lookup using W_MAIN will fail, it was decided to just
                 * use a sessionless product lookup for all legs of a strategy report, which can be
                 * achieved by passing a null value to the buildStringForProduct() method.
                 */
                orderFillText.append(buildStringForProduct(strategyDesc, null, orderFill.productKey,
                                                           orderFill.sessionName));
                break;
            case ReportTypes.FILL_REJECT:
                orderFillText.append(fillRejDesc);
                break;
            default:
                orderFillText.append(fillDesc);
                break;
        }

        if(reportType != ReportTypes.STRATEGY_LEG_REPORT)
        {
            try
            {
                product = APIHome.findProductQueryAPI().getProductByKey(orderFill.productKey);
            }
            catch(Exception e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }

            if(product != null)
            {
                int classKey = product.getProductKeysStruct().classKey;
                try
                {
                    ProductClass productClass =
                            APIHome.findProductQueryAPI().getProductClassByKey(classKey);
                    if(productClass != null)
                    {
                        orderFillText.append(productClassFormatter.format(productClass,
                                                                          ProductClassFormatter.CLASS_TYPE_NAME))
                                .append(' ');
                    }
                }
                catch(Exception e)
                {
                    GUILoggerHome.find().exception(Category + ".format()", "", e);
                }
                orderFillText.append(FormatFactory.buildSeriesName(orderFill.productKey, Category))
                        .append(' ');
            }
        }

        // No product information in this formatter with the contingency parameter.
        // The OrderFilledReportStruct formatter includes the product information.
        if(showSide)
        {
            orderFillText.append(side).append(' ');
        }
        orderFillText.append(volumeFormatter.format(orderFill.tradedQuantity)).append(' ');

        if(showContingency)
        {
            ContingencyFormatStrategy contingencyFormatter = FormatFactory.getContingencyFormatStrategy();
            orderFillText.append(contingencyFormatter.format(contingency, ContingencyFormatStrategy.BRIEF))
                    .append(' ');
        }

        orderFillText.append("@ ");
        if(orderFill.price.type != PriceTypes.NO_PRICE)
        {
            orderFillText.append(DisplayPriceFactory.create(orderFill.price));
        }
        else
        {
            orderFillText.append("No Price");
        }
        if(showLeaves)
        {
            orderFillText.append(leavesDesc)
                    .append(volumeFormatter.format(orderFill.leavesQuantity));
        }
        if(showTradeId)
        {
            orderFillText.append(" Trade ID: ").append(orderFill.tradeId.highCboeId).append(":")
                    .append(orderFill.tradeId.lowCboeId);
        }
        else if(showOrsid)
        {
            //orderFillText.append("; ORS:").append(orderFill.orsId);
            orderFillText.append("; ORS:").append(formatOrsIdForDisplay(orderFill.orsId));
        }
        return orderFillText.toString();
    }

    private String formatFilledReportForOMT(FilledReportStruct orderFill, OrderContingencyStruct contingency)
    {
        StringBuffer orderFillText = new StringBuffer(1000);
        short reportType = orderFill.fillReportType;

        switch(reportType)
            {
                case ReportTypes.STRATEGY_LEG_REPORT:
                /**
                 * Since the session product lookup for the stock leg of a buy-write order will
                 * likely fail, since session lookup using W_MAIN will fail, it was decided to just
                 * use a sessionless product lookup for all legs of a strategy report, which can be
                 * achieved by passing a null value to the buildStringForProduct() method.
                 */
                orderFillText.append(buildStringForProduct("STRATEGY LEG ", null, orderFill.productKey, orderFill.sessionName));
                break;
            case ReportTypes.FILL_REJECT:
                orderFillText.append("FILL REJECT - ");
                break;
            default:
                break;
        }
        // No product information in this formatter with the contingency parameter.
        // The OrderFilledReportStruct formatter includes the product information.
        orderFillText.append(Sides.toString(orderFill.side, Sides.BOT_SOLD_FORMAT)).append(' ');
        orderFillText.append(volumeFormatter.format(orderFill.tradedQuantity)).append(' ');

        if(contingency != null)
        {
            ContingencyFormatStrategy contingencyFormatter = FormatFactory.getContingencyFormatStrategy();
            orderFillText.append(contingencyFormatter.format(contingency,
                                                             ContingencyFormatStrategy.BRIEF))
                    .append(' ');
        }

        orderFillText.append("@ ");
        if(orderFill.price.type != PriceTypes.NO_PRICE)
        {
            orderFillText.append(DisplayPriceFactory.create(orderFill.price));
        }
        else
        {
            orderFillText.append("No Price");
        }
        orderFillText.append(", leaves ")
                .append(volumeFormatter.format(orderFill.leavesQuantity));
        return orderFillText.toString();
    }

    private String formatFilledReportBrieferInfoLeaves(OrderFilledReportStruct orderFilled, int reportIndex)
    {
        StringBuffer orderFillText = new StringBuffer(1000);
        FilledReportStruct orderFill = orderFilled.filledReport[reportIndex];
        short reportType = orderFill.fillReportType;
        String side;
        boolean showSide = true;
        String fillRejDesc = "REJ ";
        String leavesDesc = ", Lvs ";
        String makesDesc = ", makes ";
        int makesQty = orderFilled.filledOrder.orderStruct.tradedQuantity;
        switch(orderFill.side)
        {
            case Sides.BUY:
                side = "Bot";
                break;
            case Sides.SELL:
                side = "Sld";
                break;
            default:
                side = Utility.sideToString(orderFill.side);
        }

        switch(reportType)
        {
            case ReportTypes.STRATEGY_REPORT:
                showSide = false;
                orderFillText.append("Trd ");
                break;
            case ReportTypes.STRATEGY_LEG_REPORT:
                LegOrderDetailStruct thisLeg = null;
                for(LegOrderDetailStruct leg : orderFilled.filledOrder.orderStruct.legOrderDetails)
                {
                    if(leg.productKey == orderFill.productKey)
                    {
                        thisLeg = leg;
                        break;
                    }
                }
                if(thisLeg != null)
                {
                    // Use leg info rather than order info
                    makesQty = thisLeg.tradedQuantity;
                }
                break;
            case ReportTypes.FILL_REJECT:
                orderFillText.append(fillRejDesc);
                break;
            default:
                break;
        }

        if(showSide)
        {
            orderFillText.append(side).append(' ');
        }
        orderFillText.append(volumeFormatter.format(orderFill.tradedQuantity)).append(' ');

        orderFillText.append("@ ");
        if(orderFill.price.type != PriceTypes.NO_PRICE)
        {
            orderFillText.append(DisplayPriceFactory.create(orderFill.price));
        }
        else
        {
            orderFillText.append("No Price");
        }
        // orderFillText.append(makesDesc).append(volumeFormatter.format(makesQty));
        orderFillText.append(leavesDesc).append(volumeFormatter.format(orderFill.leavesQuantity));
        return orderFillText.toString();
    }

    private String formatFilledReportBriefInfoLeaves(FilledReportStruct orderFill,
                                                     OrderContingencyStruct contingency)
    {
        StringBuffer orderFillText = new StringBuffer(1000);
        short reportType = orderFill.fillReportType;
        Product product = null;
        String strategyDesc;
        String fillRejDesc;
        String fillDesc;
        String leavesDesc;
        String side;
        boolean showContingency;
        boolean showTradeId;
        boolean showOrsid = true;
        boolean showLeaves = true;
        boolean showSide = true;
        strategyDesc    = "STRATEGY LEG ORDER FILLED ";
        fillRejDesc     = "ORDER FILLED REJECT - ";
        fillDesc        = "ORDER FILLED - ";
        leavesDesc      = "; leaves ";
        side            = Utility.sideToString(orderFill.side);
        showContingency = contingency != null;
        //showTradeId     = true;   // Trade ID unwanted, per Mike Trees
        showTradeId     = false;
        switch(reportType)
            {
                case ReportTypes.STRATEGY_LEG_REPORT:
                showOrsid = false;      // because it doesn't fit, and is displayed within order detail report
                showLeaves = false;     // because it doesn't fit; will be displayed in its own line
                /**
                 * Since the session product lookup for the stock leg of a buy-write order will
                 * likely fail, since session lookup using W_MAIN will fail, it was decided to just
                 * use a sessionless product lookup for all legs of a strategy report, which can be
                 * achieved by passing a null value to the buildStringForProduct() method.
                 */
                orderFillText.append(buildStringForProduct(strategyDesc, null, orderFill.productKey, orderFill.sessionName));
                break;
            case ReportTypes.STRATEGY_REPORT:
                showSide = false;
                orderFillText.append("Trd ");
                break;
            case ReportTypes.FILL_REJECT:
                orderFillText.append(fillRejDesc);
                break;
            default:
                orderFillText.append(fillDesc);
                break;
        }

        if(reportType != ReportTypes.STRATEGY_LEG_REPORT)
        {
            try
            {
                product = APIHome.findProductQueryAPI().getProductByKey(orderFill.productKey);
            }
            catch(Exception e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }

            if(product != null)
            {
                int classKey = product.getProductKeysStruct().classKey;
                try
                {
                    ProductClass productClass = APIHome.findProductQueryAPI().getProductClassByKey(classKey);
                    if(productClass != null)
                    {
                        orderFillText.append(productClassFormatter.format(productClass,
                                                                          ProductClassFormatter.CLASS_TYPE_NAME)).append(' ');
                    }
                }
                catch(Exception e)
                {
                    GUILoggerHome.find().exception(Category + ".format()", "", e);
                }
                if(reportType != ReportTypes.STRATEGY_REPORT)
                {
                    orderFillText.append(FormatFactory.buildSeriesName(orderFill.productKey, Category)).append(' ');
                }
            }
        }

        // No product information in this formatter with the contingency parameter.
        // The OrderFilledReportStruct formatter includes the product information.
        if(showSide)
        {
                orderFillText.append(side).append(' ');
        }
        orderFillText.append(volumeFormatter.format(orderFill.tradedQuantity)).append(' ');

        if (showContingency)
        {
            ContingencyFormatStrategy contingencyFormatter = FormatFactory.getContingencyFormatStrategy();
            orderFillText.append(contingencyFormatter.format(contingency, ContingencyFormatStrategy.BRIEF)).append(' ');
        }

        orderFillText.append("@ ");
        if (orderFill.price.type != PriceTypes.NO_PRICE)
        {
            orderFillText.append(DisplayPriceFactory.create(orderFill.price));
        } else
        {
            orderFillText.append("No Price");
        }
        if (showLeaves)
        {
            orderFillText.append(leavesDesc).append(volumeFormatter.format(orderFill.leavesQuantity));
        }
        if (showTradeId)
        {
            orderFillText.append(" Trade ID: ").append(orderFill.tradeId.highCboeId).append(":")
                    .append(orderFill.tradeId.lowCboeId);
        }
        else if (showOrsid)
        {
            orderFillText.append("; ORS:").append(orderFill.orsId);
        }
        return orderFillText.toString();
    }

    private String formatFilledReportStructWithBriefInfo(FilledReportStruct orderFill,
                                                         OrderContingencyStruct contingency)
    {
        // No product information in this formatter with the contingency parameter.
        // The OrderFilledReportStruct formatter includes the product information.
        StringBuffer orderFillText = new StringBuffer(1000);
        orderFillText.append(Utility.sideToString(orderFill.side)).append(' ');
        orderFillText.append(volumeFormatter.format(orderFill.tradedQuantity)).append(' ');

        if (contingency != null)
        {
            ContingencyFormatStrategy contingencyFormatter = FormatFactory.getContingencyFormatStrategy();
            orderFillText.append(contingencyFormatter.format(contingency, ContingencyFormatStrategy.BRIEF)).append(' ');
        }

        orderFillText.append("@ ");
        if (orderFill.price.type != PriceTypes.NO_PRICE)
        {
            orderFillText.append(DisplayPriceFactory.create(orderFill.price));
        } else
        {
            orderFillText.append("No Price");
        }
        return orderFillText.toString();
    }

    private String formatFilledReportStructWithFullInfoTwoCol(FilledReportStruct orderFill)
    {
        StringBuffer orderFillText = new StringBuffer(1000);
            int column2Margin = 38;
            int sizeFieldAdded = 0;

            ProductClass productClass = null;
            Product product = null;
            try
            {
                orderFillText.append("Class: ");

                product = APIHome.findProductQueryAPI().getProductByKey(orderFill.productKey);
                if (product != null)
                {
                    productClass = APIHome.findProductQueryAPI().getProductClassByKey(product.getProductKeysStruct().classKey);
                }
                if (productClass != null)
                {
                    sizeFieldAdded = addFieldWithSizeLimit(orderFillText, productClassFormatter.format(productClass, productClassFormatter.CLASS_TYPE_NAME), column2Margin - 1 - 7, false);
                    orderFillText = appendSpace(orderFillText, column2Margin - 7 - sizeFieldAdded);
                } else
                {
                    orderFillText.append("(Not Available)");
                    orderFillText = appendSpace(orderFillText, column2Margin - 7 - 15);
                }
            }
            catch (Exception e)
            {
                GUILoggerHome.find().exception(Category + ".format()", "", e);
                orderFillText.append("(Not Available)");
                orderFillText = appendSpace(orderFillText, column2Margin - 7 - 15);
            }

            orderFillText.append("Product: ");
            if (product != null)
            {
                String productString = FormatFactory.getFormattedProduct(product);
                orderFillText.append(productString).append('\n');
            } else
            {
                orderFillText.append("(Not Available)").append('\n');
            }

            orderFillText.append("Session: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderFillText, orderFill.sessionName,
                    column2Margin - 1 - 9, false);
            orderFillText = appendSpace(orderFillText, column2Margin - 9 - sizeFieldAdded);

            orderFillText.append("Underlying Symbol: ");
            if (productClass != null)
            {
                orderFillText.append(productFormatter.format(productClass.getUnderlyingProduct())).append('\n');
            } else
            {
                orderFillText.append("(Not Available)").append('\n');
            }

            orderFillText.append("Side: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderFillText, Utility.sideToString(orderFill.side),
                    column2Margin - 1 - 6, false);
            orderFillText = appendSpace(orderFillText, column2Margin - 6 - sizeFieldAdded);

            orderFillText.append("Trade ID: ").append(orderFill.tradeId.highCboeId).append(":");
            orderFillText.append(orderFill.tradeId.lowCboeId).append('\n');

            orderFillText.append("Executed Vol: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderFillText, volumeFormatter.format(orderFill.tradedQuantity),
                    column2Margin - 1 - 14, false);
            orderFillText = appendSpace(orderFillText, column2Margin - 14 - sizeFieldAdded);

            orderFillText.append("Vol Rem: ").append(volumeFormatter.format(orderFill.leavesQuantity)).append('\n');

            orderFillText.append("Price:  ");
            if (orderFill.price.type != PriceTypes.NO_PRICE)
            {
                sizeFieldAdded = addFieldWithSizeLimit(orderFillText, DisplayPriceFactory.create(orderFill.price).toString(),
                        column2Margin - 1 - 8, false);
            } else
            {
                sizeFieldAdded = addFieldWithSizeLimit(orderFillText, "No Price",
                        column2Margin - 1 - 8, false);
            }
            orderFillText = appendSpace(orderFillText, column2Margin - 8 - sizeFieldAdded);

            orderFillText.append("Position: ").append(PositionEffects.toString(orderFill.positionEffect)).append('\n');

            orderFillText.append("Date/Time: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderFillText, Utility.toString(orderFill.timeSent, "yyyy/MM/dd HH:mm:ss.S"),
                    column2Margin - 1 - 11, false);
            orderFillText = appendSpace(orderFillText, column2Margin - 11 - sizeFieldAdded);

            orderFillText.append("Firm: ").append(FormatFactory.getFormattedExchangeFirm(orderFill.executingOrGiveUpFirm)).append('\n');

            orderFillText.append("Originator: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderFillText, FormatFactory.getFormattedExchangeAcronym(orderFill.originator),
                    column2Margin - 1 - 12, false);
            orderFillText = appendSpace(orderFillText, column2Margin - 12 - sizeFieldAdded);

            orderFillText.append("Executing Broker: ").append(orderFill.executingBroker).append('\n');

            orderFillText.append("CMTA: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderFillText, FormatFactory.getFormattedExchangeFirm(orderFill.cmta),
                    column2Margin - 1 - 6, false);
            orderFillText = appendSpace(orderFillText, column2Margin - 6 - sizeFieldAdded);

            orderFillText.append("Account: ").append(orderFill.account).append('\n');

            orderFillText.append("Subaccount: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderFillText, orderFill.subaccount,
                    column2Margin - 1 - 12, false);
            orderFillText = appendSpace(orderFillText, column2Margin - 12 - sizeFieldAdded);

            orderFillText.append("User Acronym: ").append(FormatFactory.getFormattedExchangeAcronym(orderFill.userAcronym)).append('\n');

            orderFillText.append("User ID: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderFillText, orderFill.userId,
                    column2Margin - 1 - 9, false);
            orderFillText = appendSpace(orderFillText, column2Margin - 9 - sizeFieldAdded);

            orderFillText.append("User Assigned ID: ").append(orderFill.userAssignedId).append('\n');

            orderFillText.append("ORS Id: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderFillText, formatOrsIdForDisplay(orderFill.orsId),
                    column2Margin - 1 - 8, false);
            orderFillText = appendSpace(orderFillText, column2Margin - 8 - sizeFieldAdded);

            orderFillText.append("Extensions: ").append(orderFill.extensions).append('\n');

            String optionalData = orderFill.optionalData;
            orderFillText.append("Optional Data: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderFillText, optionalData, column2Margin + column2Margin - 15, true);
            orderFillText.append('\n');

            while (sizeFieldAdded < optionalData.length())
            {
                orderFillText = appendSpace(orderFillText, 15);
                sizeFieldAdded = sizeFieldAdded + addFieldWithSizeLimit(orderFillText, optionalData.substring(sizeFieldAdded), column2Margin + column2Margin - 15, true);

                if (sizeFieldAdded < optionalData.length())
                {
                    orderFillText.append('\n');
                }
            }

            ContraPartyStruct[] contras = orderFill.contraParties;
            for (int j = 0; j < contras.length; j++)
            {
                orderFillText.append("Contra Party").append(j + 1).append(" - ");
                orderFillText.append("Firm: ").append(FormatFactory.getFormattedExchangeFirm(contras[j].firm)).append("; ");
                orderFillText.append("User Id: ").append(FormatFactory.getFormattedExchangeAcronym(contras[j].user)).append("; ");
                orderFillText.append("Vol: ").append(volumeFormatter.format(contras[j].quantity));

                if (j < contras.length)
                {
                    orderFillText.append('\n');
                }
            }
        return orderFillText.toString();
    }

    private String formatFilledReportStructWithFullInfo(FilledReportStruct orderFill)
    {
        StringBuffer orderFillText = new StringBuffer(1000);
        ProductClass productClass = null;
        Product product = null;
        try
            {
                orderFillText.append("Class: ");

            product = APIHome.findProductQueryAPI().getProductByKey(orderFill.productKey);
            if (product != null)
            {
                productClass = APIHome.findProductQueryAPI().getProductClassByKey(product.getProductKeysStruct().classKey);
            }
            if (productClass != null)
            {
                orderFillText.append(productClassFormatter.format(productClass, productClassFormatter.CLASS_TYPE_NAME)).append('\n');
                orderFillText.append("Underlying Symbol: ").append(productFormatter.format(productClass.getUnderlyingProduct())).append('\n');
            } else
            {
                orderFillText.append("(Not Available)").append('\n');
                orderFillText.append("Underlying Symbol: ").append("(Not Available)").append('\n');
            }
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(Category + ".format()", "", e);
            orderFillText.append("(Not Available)").append('\n');
            orderFillText.append("Underlying Symbol: ").append("(Not Available)").append('\n');
        }

        orderFillText.append("Product: ");
        if (product != null)
        {
            String productString = FormatFactory.getFormattedProduct(product);
            orderFillText.append(productString).append('\n');
        } else
        {
            orderFillText.append("(Not Available)").append('\n');
        }

        orderFillText.append("Session: ").append(orderFill.sessionName).append('\n');
        orderFillText.append("Side: ").append(Utility.sideToString(orderFill.side)).append('\n');
        orderFillText.append("Trade ID: ").append(orderFill.tradeId.highCboeId).append(":").append(orderFill.tradeId.lowCboeId).append('\n');
        orderFillText.append("Executed Vol: ").append(volumeFormatter.format(orderFill.tradedQuantity)).append('\n');
        orderFillText.append("Vol Rem: ").append(volumeFormatter.format(orderFill.leavesQuantity)).append('\n');
        if (orderFill.price.type != PriceTypes.NO_PRICE)
        {
            orderFillText.append("Price:  ").append(DisplayPriceFactory.create(orderFill.price).toString()).append('\n');
        } else
        {
            orderFillText.append("Price:  No Price\n");
        }
        orderFillText.append("Position: ").append(PositionEffects.toString(orderFill.positionEffect)).append('\n');
        orderFillText.append("Date/Time: ").append(Utility.toString(orderFill.timeSent, "yyyy/MM/dd HH:mm:ss.S")).append('\n');
        orderFillText.append("Firm: ").append(FormatFactory.getFormattedExchangeFirm(orderFill.executingOrGiveUpFirm)).append('\n');
        orderFillText.append("Originator: ").append(FormatFactory.getFormattedExchangeAcronym(orderFill.originator)).append('\n');
        orderFillText.append("Executing Broker: ").append(orderFill.executingBroker).append('\n');
        orderFillText.append("CMTA: ").append(FormatFactory.getFormattedExchangeFirm(orderFill.cmta)).append('\n');
        orderFillText.append("Account: ").append(orderFill.account).append('\n');
        orderFillText.append("Subaccount: ").append(orderFill.subaccount).append('\n');
        orderFillText.append("User Acronym: ").append(FormatFactory.getFormattedExchangeAcronym(orderFill.userAcronym)).append('\n');
        orderFillText.append("User ID: ").append(orderFill.userId).append('\n');
        orderFillText.append("User Assigned ID: ").append(orderFill.userAssignedId).append('\n');
        // orderFillText.append("ORS Id: ").append(orderFill.orsId).append('\n');
        orderFillText.append("; ORS Id:").append(formatOrsIdForDisplay(orderFill.orsId)).append('\n');
        orderFillText.append("Extensions: ").append(orderFill.extensions).append('\n');
        orderFillText.append("Optional Data: ").append(orderFill.optionalData).append('\n');

        ContraPartyStruct[] contras = orderFill.contraParties;
        for (int j = 0; j < contras.length; j++)
        {
            orderFillText.append("Contra Party").append(j + 1).append(" - ");
            orderFillText.append("Firm: ").append(FormatFactory.getFormattedExchangeFirm(contras[j].firm)).append("; ");
            orderFillText.append("User Id: ").append(FormatFactory.getFormattedExchangeAcronym(contras[j].user)).append("; ");
            orderFillText.append("Vol: ").append(volumeFormatter.format(contras[j].quantity));

            if (j < contras.length)
            {
                orderFillText.append('\n');
            }
        }
        return orderFillText.toString();
    }

    public String format(OrderFilledReportStruct orderFilled, String styleName, int reportIndex)
    {
        validateStyle(styleName);
        StringBuffer orderFillText = new StringBuffer(1000);
        FilledReportStruct orderFill = orderFilled.filledReport[reportIndex];

        // VolumeFormatStrategy volumeFormatter = FormatFactory.getVolumeFormatStrategy();
        //ProductClassFormatStrategy productClassFormatter = FormatFactory.getProductClassFormatStrategy();
        // ProductFormatStrategy productFormatter = FormatFactory.getProductFormatStrategy();

        if (styleName.equalsIgnoreCase(FULL_INFO_NAME)
                || styleName.equalsIgnoreCase(FULL_INFO_TWO_COLUMN_NAME))
        {
            Utility.portWarningPorted(Category + ".format(OrderFilledReportStruct)");                                   // TODO: remove when Jasper port is completed
        }

        if (styleName.equalsIgnoreCase(FULL_INFO_NAME))
        {
            orderFillText.append(format(orderFill, null, styleName));                                                   // TODO: remove when Jasper port is completed
        } else if (styleName.equalsIgnoreCase(FULL_INFO_TWO_COLUMN_NAME))
        {
            orderFillText.append(format(orderFill, null, styleName));                                                   // TODO: remove when Jasper port is completed
        } else if (styleName.equalsIgnoreCase(BRIEF_INFO_NAME)|| styleName.equalsIgnoreCase(BRIEF_INFO_LEAVES_NAME))
        {   // use the same format for both styles
/*
            ProductClass productClass = null;
            Product product = null;
            try
            {
                // CBOqa02917 - Used to show the leg info but now shows the strategy name
                product = APIHome.findProductQueryAPI().getProductByName(APIHome.findOrderQueryAPI().getOrderById(orderFilled.filledOrder.orderStruct.orderId).productInformation);
                if (product != null)
                {
                    productClass = APIHome.findProductQueryAPI().getProductClassByKey(product.getProductKeysStruct().classKey);
                }
                if (productClass != null)
                {
                    orderFillText.append(productClassFormatter.format(productClass, ProductClassFormatter.CLASS_TYPE_NAME)).append(' ');
                }
            }
            catch (Exception e)
            {
                GUILoggerHome.find().exception(Category + ".format()", "", e);
            }

            if (product != null)
            {
                String productString = FormatFactory.getFormattedProduct(product);
                orderFillText.append(productString).append(' ');
            }
*/
            // NOTE:
            // The BRIEF_INFO_NAME style for a FilledReport formatter used to contain leaves information,
            // but it was fixed to not include it and a BRIEF_INFO_LEAVES_NAME style was created.
            // As this call used the BRIEF_INFO_NAME, which printed leaves, it was changed to BRIEF_INFO_LEAVES
            // so the format is the same.
            orderFillText.append(format(orderFill, orderFilled.filledOrder.orderStruct.contingency, BRIEF_INFO_LEAVES_NAME));
        }
        else if(styleName.equalsIgnoreCase(BRIEFER_INFO_LEAVES_NAME))
        {
            orderFillText.append(formatFilledReportBrieferInfoLeaves(orderFilled, reportIndex));
        }
        else 
        {
            orderFillText.append(format(orderFill, orderFilled.filledOrder.orderStruct.contingency, styleName));
        }

        return orderFillText.toString();
    }

    public String format(Order order)
    {
        return format(order, getDefaultStyle());
    }

    public String format(Order order, String styleName)
    {
        validateStyle(styleName);
        ContingencyFormatStrategy contingencyFormatter = FormatFactory.getContingencyFormatStrategy();
        StringBuffer orderText = new StringBuffer(1000);
        VolumeFormatStrategy volumeFormatter = FormatFactory.getVolumeFormatStrategy();
        ProductClassFormatStrategy productClassFormatter = FormatFactory.getProductClassFormatStrategy();
        ProductFormatStrategy productFormatter = FormatFactory.getProductFormatStrategy();

        if (styleName.equalsIgnoreCase(FULL_INFO_NAME)
                || styleName.equalsIgnoreCase(FULL_INFO_TWO_COLUMN_NAME))
        {
            Utility.portWarningPorted(Category + ".format(Order)");                                                     // TODO: remove when Jasper port is completed
        }

        if (styleName.equalsIgnoreCase(FULL_INFO_NAME))                                                                  // TODO: remove when Jasper port is completed
        {
            orderText.append("Firm: ").append(FormatFactory.getFormattedExchangeFirm(order.getOrderId().getExecutingOrGiveUpFirm())).append('\n');
            orderText.append("Correspondent Firm: ").append(order.getOrderId().getCorrespondentFirm()).append('\n');
            orderText.append("Branch Seq. #: ").append(order.getOrderId().getBranch()).append(' ');
            orderText.append(order.getOrderId().getBranchSequenceNumber()).append('\n');

            String orderDate = order.getOrderId().getOrderDate();
            orderText.append("Order Date: ").append(orderDate.substring(0, 4)).append('/');
            orderText.append(orderDate.substring(4, 6)).append('/');
            orderText.append(orderDate.substring(6, 8)).append('\n');
            orderText.append("Time in Force: ");
            orderText.append(TimesInForce.toString(order.getTimeInForce(), TimesInForce.TRADERS_FORMAT));
            orderText.append('\n');
            // expire time has no meaning in version 1
            //        orderText.append("Expire Time: ").append(Utility.toString(order.expireTime, "yyyy/MM/dd HH:mm:ss.S")).append('\n');
            orderText.append("Received Time: ");
            orderText.append(FormatFactory.getDateFormatStrategy().format(order.getReceivedTime(), DateFormatStrategy.DATE_FORMAT_24_HOURS_REVERSE_STYLE)).append('\n');
            orderText.append("Current Active Session: ").append(order.getActiveSession()).append('\n');

            String[] validSessions = order.getSessionNames();
            if (validSessions.length > 0)
            {
                orderText.append("Valid Sessions: ");

                for (int i = 0; i < validSessions.length; i++)
                {
                    orderText.append(validSessions[i]);

                    if (i < validSessions.length - 1)
                    {
                        orderText.append(", ");
                    } else
                    {
                        orderText.append('\n');
                    }
                }
            }

            orderText.append("Side: ").append(Sides.toString(order.getSide(), Sides.NO_BID_FORMAT)).append('\n');
            orderText.append("Crossing Order: ").append(order.isCross().booleanValue() ? "Yes" : "No").append('\n');

            ProductClass productClass = null;
            try
            {
                orderText.append("Class: ");

                productClass = APIHome.findProductQueryAPI().getProductClassByKey(order.getClassKey());

                if (productClass != null)
                {
                    orderText.append(productClassFormatter.format(productClass, productClassFormatter.CLASS_TYPE_NAME)).append('\n');
                    orderText.append("Underlying Symbol: ").append(productFormatter.format(productClass.getUnderlyingProduct())).append('\n');
                } else
                {
                    orderText.append("(Not Available)").append('\n');
                    orderText.append("Underlying Symbol: ").append("(Not Available)").append('\n');
                }
            }
            catch (Exception e)
            {
                GUILoggerHome.find().exception(Category + ".format()", e);
                orderText.append("(Not Available)").append('\n');
                orderText.append("Underlying Symbol: ").append("(Not Available)").append('\n');
            }

            orderText.append("Product: ").append(FormatFactory.buildSeriesName(order.getProductKey(), Category)).append('\n');
            orderText.append("Original Qty: ").append(volumeFormatter.format(order.getOriginalQuantity())).append('\n');
            orderText.append("Executed Total Qty: ").append(volumeFormatter.format(order.getTradedQuantity())).append('\n');
            orderText.append("Executed Session Qty: ").append(volumeFormatter.format(order.getSessionTradedQuantity())).append('\n');
            orderText.append("Canceled Total Qty: ").append(volumeFormatter.format(order.getCancelledQuantity())).append('\n');
            orderText.append("Canceled Session Qty: ").append(volumeFormatter.format(order.getSessionCancelledQuantity())).append('\n');
            if (!order.getPrice().isNoPrice())
            {
                orderText.append("Price: ").append(order.getPrice()).append('\n');
            } else
            {
                orderText.append("Price: No Price\n");
            }
            orderText.append("Session Average Price: ").append(order.getSessionAveragePrice()).append('\n');

            orderText.append("Contingency: ").append(contingencyFormatter.format(order.getContingency().getStruct(), ContingencyFormatStrategy.FULL)).append('\n');
            orderText.append("Position: ").append(PositionEffects.toString(order.getPositionEffect())).append('\n');
            orderText.append("State: ").append(OrderStates.toString(order.getState())).append('\n');
            orderText.append("Coverage: ").append(CoverageTypes.toString(order.getCoverage())).append('\n');
            orderText.append("Originator: ").append(FormatFactory.getFormattedExchangeAcronym(order.getOriginator())).append('\n');
            orderText.append("Origin Type: ").append(OrderOrigins.toString(
                    order.getOrderOriginType()));
            if (order.getOrderOriginType() == OrderOrigins.PRINCIPAL)
            {
                orderText.append(" / ").append(order.getAwayExchange());
            }
            orderText.append('\n');
            orderText.append("CMTA: ").append(FormatFactory.getFormattedExchangeFirm(order.getCmta())).append('\n');
            orderText.append("Account: ").append(order.getAccount()).append('\n');
            orderText.append("Subaccount: ").append(order.getSubaccount()).append('\n');
            orderText.append("User ID: ").append(order.getUserId()).append('\n');
            orderText.append("User Assigned ID: ").append(order.getUserAssignedId()).append('\n');
            orderText.append("Source: ").append(Sources.toString(order.getSource())).append('\n');
            //orderText.append("ORS Id: ").append(order.getOrsId()).append('\n');
            orderText.append("ORS Id: ").append(formatOrsIdForDisplay(order.getOrsId())).append('\n');
            orderText.append("Optional Data: ").append(order.getOptionalData());
        } else
        if (styleName.equalsIgnoreCase(FULL_INFO_TWO_COLUMN_NAME))                                                  // TODO: remove when Jasper port is completed
        {
            int column2Margin = 41;
            int sizeFieldAdded = 0;

            orderText.append("Firm: ");
            String exFirm = FormatFactory.getFormattedExchangeFirm(order.getOrderId().getExecutingOrGiveUpFirm());
            sizeFieldAdded = addFieldWithSizeLimit(orderText, exFirm, column2Margin - 1 - 6, false);
            orderText = appendSpace(orderText, column2Margin - 6 - sizeFieldAdded);

            orderText.append("Correspondent Firm: ").append(order.getOrderId().getCorrespondentFirm()).append('\n');

            orderText.append("Branch Seq. #: ");
            String brSeq = order.getOrderId().getBranch() + " " + order.getOrderId().getBranchSequenceNumber();
            sizeFieldAdded = addFieldWithSizeLimit(orderText, brSeq, column2Margin - 1 - 15, false);
            orderText = appendSpace(orderText, column2Margin - 15 - sizeFieldAdded);

            String orderDate = order.getOrderId().getOrderDate();
            orderText.append("Order Date: ").append(orderDate.substring(0, 4)).append('/');
            orderText.append(orderDate.substring(4, 6)).append('/');
            orderText.append(orderDate.substring(6, 8)).append('\n');

            orderText.append("Time in Force: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderText, Utility.timeInForceToString(order.getTimeInForce()),
                    column2Margin - 1 - 15, false);
            orderText = appendSpace(orderText, column2Margin - 15 - sizeFieldAdded);

            orderText.append("Received Time: ").append(FormatFactory.getDateFormatStrategy().format(order.getReceivedTime(), DateFormatStrategy.DATE_FORMAT_24_HOURS_REVERSE_STYLE));
            orderText.append('\n');

            // expire time has no meaning in version 1
            //        orderText.append("Expire Time: ").append(Utility.toString(order.expireTime, "yyyy/MM/dd HH:mm:ss.S")).append('\n');

            orderText.append("Crossing Order: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderText, order.isCross() ? "Yes" : "No", column2Margin - 1 - 16, false);
            orderText = appendSpace(orderText, column2Margin - 16 - sizeFieldAdded);

            orderText.append("Original Vol: ").append(volumeFormatter.format(order.getOriginalQuantity())).append('\n');

            orderText.append("Position: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderText, PositionEffects.toString(order.getPositionEffect()),
                    column2Margin - 1 - 10, false);
            orderText = appendSpace(orderText, column2Margin - 10 - sizeFieldAdded);

            orderText.append("Executed Total Vol: ").append(volumeFormatter.format(order.getTradedQuantity()));
            orderText.append('\n');

            orderText.append("Canceled Total Vol: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderText, volumeFormatter.format(order.getCancelledQuantity()),
                    column2Margin - 1 - 20, false);
            orderText = appendSpace(orderText, column2Margin - 20 - sizeFieldAdded);

            orderText.append("Executed Session Vol: ").append(volumeFormatter.format(order.getSessionTradedQuantity()));
            orderText.append('\n');

            orderText.append("Canceled Session Vol: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderText, volumeFormatter.format(order.getSessionCancelledQuantity()),
                    column2Margin - 1 - 22, false);
            orderText = appendSpace(orderText, column2Margin - 22 - sizeFieldAdded);

            if (!order.getPrice().isNoPrice())
            {
                orderText.append("Price: ").append(order.getPrice().toString());
            } else
            {
                orderText.append("Price: No Price");
            }
            orderText.append('\n');

            orderText.append("Session Average Price: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderText, order.getSessionAveragePrice().toString(),
                    column2Margin - 1 - 23, false);
            orderText = appendSpace(orderText, column2Margin - 23 - sizeFieldAdded);

            orderText.append("State: ").append(OrderStates.toString(order.getState()));
            orderText.append('\n');

            orderText.append("Contingency: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderText, contingencyFormatter.format(order.getContingency().getStruct(), ContingencyFormatStrategy.FULL),
                    column2Margin - 1 - 13, false);
            orderText = appendSpace(orderText, column2Margin - 13 - sizeFieldAdded);

            orderText.append("Coverage: ").append(CoverageTypes.toString(order.getCoverage()));
            orderText.append('\n');

            orderText.append("Origin Type: ");
            String origin = OrderOrigins.toString(order.getOrderOriginType());
            if (order.getOrderOriginType() == OrderOrigins.PRINCIPAL)
            {
                origin += " / ";
                origin += order.getAwayExchange().toString();
            }
            sizeFieldAdded = addFieldWithSizeLimit(orderText, origin,
                    column2Margin - 1 - 13, false);
            orderText = appendSpace(orderText, column2Margin - 13 - sizeFieldAdded);

            orderText.append("Account: ").append(order.getAccount());
            orderText.append('\n');

            orderText.append("Subaccount: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderText, order.getSubaccount(),
                    column2Margin - 1 - 12, false);
            orderText = appendSpace(orderText, column2Margin - 12 - sizeFieldAdded);

            //orderText.append("ORS Id: ").append(order.getOrsId());
            orderText.append("ORS Id: ").append(formatOrsIdForDisplay(order.getOrsId()));
            orderText.append('\n');

            orderText.append("CMTA: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderText, FormatFactory.getFormattedExchangeFirm(order.getCmta()),
                    column2Margin - 1 - 6, false);
            orderText = appendSpace(orderText, column2Margin - 6 - sizeFieldAdded);

            orderText.append("Originator: ").append(FormatFactory.getFormattedExchangeAcronym(order.getOriginator()));
            orderText.append('\n');

            orderText.append("Source: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderText, Sources.toString(order.getSource()),
                    column2Margin - 1 - 8, false);
            orderText = appendSpace(orderText, column2Margin - 8 - sizeFieldAdded);

            orderText.append("User Id: ").append(order.getUserId());
            orderText.append('\n');

            orderText.append("User Assigned ID: ").append(order.getUserAssignedId()).append('\n');

            String optionalData = order.getOptionalData();
            orderText.append("Optional Data: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderText, optionalData, column2Margin + column2Margin - 15, true);
            orderText.append('\n');

            while (sizeFieldAdded < optionalData.length())
            {
                orderText = appendSpace(orderText, 15);
                sizeFieldAdded += addFieldWithSizeLimit(orderText,
                                                        optionalData.substring(sizeFieldAdded),
                                                        column2Margin + column2Margin - 15, true);

                if (sizeFieldAdded < optionalData.length())
                {
                    orderText.append('\n');
                }
            }
        } else if (styleName.equalsIgnoreCase(BRIEF_INFO_NAME))
        {
            ProductClass productClass = null;
            try
            {
                productClass = APIHome.findProductQueryAPI().getProductClassByKey(order.getClassKey());

                if (productClass != null)
                {
                    orderText.append(productClassFormatter.format(productClass, ProductClassFormatter.CLASS_TYPE_NAME)).append(' ');
                }
            }
            catch (Exception e)
            {
                GUILoggerHome.find().exception(Category + ".format()", "", e);
            }

            if(order.getLegOrderDetails().length == 0)
            {
                orderText.append(FormatFactory.buildSeriesName(order.getProductKey(), Category))
                        .append(' ');
            }

            orderText.append(Utility.sideToString(order.getSide())).append(' ');
            orderText.append(volumeFormatter.format(order.getOriginalQuantity())).append(' ');
            orderText.append("@ ");
            orderText.append(order.getPrice());
            orderText.append(' ').append(contingencyFormatter.format(order.getContingency().getStruct(), ContingencyFormatStrategy.BRIEF));
        } else if (styleName.equalsIgnoreCase(BRIEF_INFO_LEAVES_NAME))
        {
            ProductClass productClass = null;
            try
            {
                productClass = APIHome.findProductQueryAPI().getProductClassByKey(order.getClassKey());

                if (productClass != null)
                {
                    orderText.append(productClassFormatter.format(productClass, ProductClassFormatter.CLASS_TYPE_NAME)).append(' ');
                }
            }
            catch (Exception e)
            {
                GUILoggerHome.find().exception(Category + ".format()", "", e);
            }

            if(order.getLegOrderDetails().length == 0)
            {
                orderText.append(FormatFactory.buildSeriesName(order.getProductKey(), Category))
                        .append(' ');
            }

            orderText.append(Utility.sideToString(order.getSide())).append(' ');
            orderText.append(volumeFormatter.format(order.getLeavesQuantity())).append(' ');
            orderText.append("@ ");
            if (!order.getPrice().isNoPrice())
            {
                orderText.append(order.getPrice());
            } else
            {
                orderText.append("No Price");
            }
            orderText.append(' ').append(contingencyFormatter.format(order.getContingency().getStruct(), ContingencyFormatStrategy.BRIEF));
        }
        else if (styleName.equalsIgnoreCase(BRIEF_INFO_NAME_OMT)) {

            try
            {
                ProductClass productClass = APIHome.findProductQueryAPI().getProductClassByKey(order.getClassKey());
                if (productClass != null)
                {
                    orderText.append(productClassFormatter.format(productClass, ProductClassFormatStrategy.CLASS_TYPE_NAME)).append(' ');
                }
                if(order.getLegOrderDetails().length == 0)
                {
                    orderText.append(FormatFactory.buildSeriesName(order.getProductKey(), Category));
                }
            }
            catch (Exception e)
            {
                GUILoggerHome.find().exception(Category + ".format()", "", e);
            }
        }

        return orderText.toString();

        /* Shawn uncomment later ???

        StringBuffer headerText  = new StringBuffer(1000);
        String       quantityStr = null;
        String       priceStr    = null;
        if(styleName.equalsIgnoreCase(BRIEF_INFO_NAME))
        {
            quantityStr = volumeFormatter.format(order.getOriginalQuantity().intValue());
            priceStr = order.getPrice().toString();
        }
        else if(styleName.equalsIgnoreCase(BRIEF_INFO_LEAVES_NAME))
        {
            quantityStr = volumeFormatter.format(order.getLeavesQuantity().intValue());
            if (!order.getPrice().isNoPrice())
            {
                priceStr = order.getPrice().toString();
            }
            else
            {
                priceStr = "No Price";       // Shawn why not for BRIEF_INFO_NAME ??
            }
        }

        ProductClass productClass = null;
        try
        {
            productClass = APIHome.findProductQueryAPI().getProductClassByKey(order.getClassKey().intValue());

            if(productClass != null)
            {
                headerText.append(productClassFormatter.format(productClass, ProductClassFormatStrategy.CLASS_TYPE_NAME)).append(' ');
            }
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(Category + ".format()", "", e);
        }

        headerText.append(Utility.buildSeriesName(order.getProductKey().intValue(), Category)).append(' ');
        headerText.append(Utility.sideToString(order.getSide().charValue())).append(' ');
        headerText.append(quantityStr).append(" @ ");
        headerText.append(priceStr).append(' ');
        headerText.append(contingencyFormatter.format(order.getContingency().getStruct(), ContingencyFormatStrategy.BRIEF));

        return headerText.toString();

        ?????? */
    }

    public String format(OrderStruct order)
    {
        return format(order, getDefaultStyle());
    }

    public String format(OrderStruct order, String styleName)
    {
        validateStyle(styleName);

        if (styleName.equalsIgnoreCase(FULL_INFO_NAME)
                || styleName.equalsIgnoreCase(FULL_INFO_TWO_COLUMN_NAME))
        {
            Utility.portWarningPorted(Category + ".format(OrderStruct)");                                               // TODO: remove when Jasper port is completed
        }

        if (styleName.equalsIgnoreCase(FULL_INFO_NAME))                                                                  // TODO: remove when Jasper port is completed
        {
            return formatOrderStructFullInfo(order);
        } else
        if (styleName.equalsIgnoreCase(FULL_INFO_TWO_COLUMN_NAME))                                                  // TODO: remove when Jasper port is completed
        {
            return formatOrderStructFullInfoTwoCol(order);
        } else if (styleName.equalsIgnoreCase(BRIEF_INFO_NAME))
        {
            return formatOrderStructBriefInfo(order);
        } else if (styleName.equalsIgnoreCase(BRIEF_INFO_LEAVES_NAME))
        {
            return formatOrderStructBriefInfoLeaves(order);
        }
        else if(styleName.equalsIgnoreCase(HELP_DESK_INFO))
        {
            return formatOrderStructBriefInfoLeaves(order);
        }
        return "";
    }

    private String formatOrderStructBriefInfoLeaves(OrderStruct order)
    {
        StringBuffer orderText = new StringBuffer(1000);
        ProductClass productClass = null;
        try
            {
                productClass = APIHome.findProductQueryAPI().getProductClassByKey(order.classKey);

            if (productClass != null)
            {
                orderText.append(productClassFormatter.format(productClass, ProductClassFormatter.CLASS_TYPE_NAME)).append(' ');
            }
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(Category + ".format()", "", e);
        }

        orderText.append(FormatFactory.buildSeriesName(order.productKey, Category)).append(' ');

        orderText.append(Utility.sideToString(order.side)).append(' ');
        orderText.append(volumeFormatter.format(order.leavesQuantity)).append(' ');
        orderText.append("@ ");
        if (order.price.type != PriceTypes.NO_PRICE)
        {
            orderText.append(DisplayPriceFactory.create(order.price));
        } else
        {
            orderText.append("No Price");
        }
        orderText.append(' ').append(contingencyFormatter.format(order.contingency, ContingencyFormatStrategy.BRIEF));
        return orderText.toString();
    }

    private String formatOrderStructBriefInfo(OrderStruct order)
    {
        StringBuffer orderText = new StringBuffer(1000);
        ProductClass productClass = null;
        try
            {
                productClass = APIHome.findProductQueryAPI().getProductClassByKey(order.classKey);

            if (productClass != null)
            {
                orderText.append(productClassFormatter.format(productClass, ProductClassFormatter.CLASS_TYPE_NAME)).append(' ');
            }
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(Category + ".format()", "", e);
        }

        orderText.append(FormatFactory.buildSeriesName(order.productKey, Category)).append(' ');
        orderText.append(Utility.sideToString(order.side)).append(' ');
        orderText.append(volumeFormatter.format(order.originalQuantity)).append(' ');
        orderText.append("@ ");
        if (order.price.type != PriceTypes.NO_PRICE)
        {
            orderText.append(DisplayPriceFactory.create(order.price));
        } else
        {
            orderText.append("No Price");
        }
        orderText.append(' ').append(contingencyFormatter.format(order.contingency, ContingencyFormatStrategy.BRIEF));
        return orderText.toString();
    }

    private String formatOrderStructFullInfoTwoCol(OrderStruct order)
    {
        StringBuffer orderText = new StringBuffer(1000);
            int column2Margin = 41;
            int sizeFieldAdded = 0;

            orderText.append("Firm: ");
            String exFirm = FormatFactory.getFormattedExchangeFirm(order.orderId.executingOrGiveUpFirm);
            sizeFieldAdded = addFieldWithSizeLimit(orderText, exFirm, column2Margin - 1 - 6, false);
            orderText = appendSpace(orderText, column2Margin - 6 - sizeFieldAdded);

            orderText.append("Correspondent Firm: ").append(order.orderId.correspondentFirm).append('\n');

            orderText.append("Branch Seq. #: ");
            String brSeq = order.orderId.branch + " " + order.orderId.branchSequenceNumber;
            sizeFieldAdded = addFieldWithSizeLimit(orderText, brSeq, column2Margin - 1 - 15, false);
            orderText = appendSpace(orderText, column2Margin - 15 - sizeFieldAdded);

            orderText.append("Order Date: ").append(order.orderId.orderDate.substring(0, 4)).append('/');
            orderText.append(order.orderId.orderDate.substring(4, 6)).append('/');
            orderText.append(order.orderId.orderDate.substring(6, 8)).append('\n');

            orderText.append("Time in Force: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderText, Utility.timeInForceToString(order.timeInForce),
                    column2Margin - 1 - 15, false);
            orderText = appendSpace(orderText, column2Margin - 15 - sizeFieldAdded);

            orderText.append("Received Time: ").append(Utility.toString(order.receivedTime, "yyyy/MM/dd HH:mm:ss.S"));
            orderText.append('\n');

            // expire time has no meaning in version 1
            //        orderText.append("Expire Time: ").append(Utility.toString(order.expireTime, "yyyy/MM/dd HH:mm:ss.S")).append('\n');

            orderText.append("Crossing Order: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderText, order.cross ? "Yes" : "No", column2Margin - 1 - 16, false);
            orderText = appendSpace(orderText, column2Margin - 16 - sizeFieldAdded);

            orderText.append("Original Vol: ").append(volumeFormatter.format(order.originalQuantity)).append('\n');

            orderText.append("Position: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderText, PositionEffects.toString(order.positionEffect),
                    column2Margin - 1 - 10, false);
            orderText = appendSpace(orderText, column2Margin - 10 - sizeFieldAdded);

            orderText.append("Executed Total Vol: ").append(volumeFormatter.format(order.tradedQuantity));
            orderText.append('\n');

            orderText.append("Canceled Total Vol: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderText, volumeFormatter.format(order.cancelledQuantity),
                    column2Margin - 1 - 20, false);
            orderText = appendSpace(orderText, column2Margin - 20 - sizeFieldAdded);

            orderText.append("Executed Session Vol: ").append(volumeFormatter.format(order.sessionTradedQuantity));
            orderText.append('\n');

            orderText.append("Canceled Session Vol: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderText, volumeFormatter.format(order.sessionCancelledQuantity),
                    column2Margin - 1 - 22, false);
            orderText = appendSpace(orderText, column2Margin - 22 - sizeFieldAdded);

            if (order.price.type != PriceTypes.NO_PRICE)
            {
                orderText.append("Price: ").append(DisplayPriceFactory.create(order.price).toString());
            } else
            {
                orderText.append("Price: No Price");
            }
            orderText.append('\n');

            orderText.append("Session Average Price: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderText, DisplayPriceFactory.create(order.sessionAveragePrice).toString(),
                    column2Margin - 1 - 23, false);
            orderText = appendSpace(orderText, column2Margin - 23 - sizeFieldAdded);

            orderText.append("State: ").append(OrderStates.toString(order.state));
            orderText.append('\n');

            orderText.append("Contingency: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderText, contingencyFormatter.format(order.contingency, ContingencyFormatStrategy.FULL),
                    column2Margin - 1 - 13, false);
            orderText = appendSpace(orderText, column2Margin - 13 - sizeFieldAdded);

            orderText.append("Coverage: ").append(CoverageTypes.toString(order.coverage));
            orderText.append('\n');

            orderText.append("Origin Type: ");
            String origin = OrderOrigins.toString(order.orderOriginType, OrderOrigins.CODE_FORMAT);
            if (order.orderOriginType == OrderOrigins.PRINCIPAL)
            {
                origin += " / ";
                origin += FormatFactory.getOrderExtensionsFormatStrategy().format(order.extensions,
                                                                                  ExtensionFields.EXCHANGE_DESTINATION,
                                                                                  OrderExtensionsFormatStrategy.BRIEF_INFO_NAME);
            }
            sizeFieldAdded = addFieldWithSizeLimit(orderText, origin,
                    column2Margin - 1 - 13, false);
            orderText = appendSpace(orderText, column2Margin - 13 - sizeFieldAdded);

            orderText.append("Account: ").append(order.account);
            orderText.append('\n');

            orderText.append("Subaccount: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderText, order.subaccount,
                    column2Margin - 1 - 12, false);
            orderText = appendSpace(orderText, column2Margin - 12 - sizeFieldAdded);

            //orderText.append("ORS Id: ").append(order.orsId);
            orderText.append("ORS Id: ").append(formatOrsIdForDisplay(order.orsId));
            orderText.append('\n');

            orderText.append("CMTA: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderText, FormatFactory.getFormattedExchangeFirm(order.cmta),
                    column2Margin - 1 - 6, false);
            orderText = appendSpace(orderText, column2Margin - 6 - sizeFieldAdded);

            orderText.append("Originator: ").append(FormatFactory.getFormattedExchangeAcronym(order.originator));
            orderText.append('\n');

            orderText.append("Source: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderText, Sources.toString(order.source),
                    column2Margin - 1 - 8, false);
            orderText = appendSpace(orderText, column2Margin - 8 - sizeFieldAdded);

            orderText.append("User Id: ").append(order.userId);
            orderText.append('\n');

            orderText.append("User Assigned ID: ").append(order.userAssignedId).append('\n');

            orderText.append("Optional Data: ");
            sizeFieldAdded = addFieldWithSizeLimit(orderText, order.optionalData, column2Margin + column2Margin - 15, true);
            orderText.append('\n');

            while (sizeFieldAdded < order.optionalData.length())
            {
                orderText = appendSpace(orderText, 15);
                sizeFieldAdded += addFieldWithSizeLimit(orderText, order.optionalData.substring(sizeFieldAdded), column2Margin + column2Margin - 15, true);

                if (sizeFieldAdded < order.optionalData.length())
                {
                    orderText.append('\n');
                }
            }
        return orderText.toString();
    }

    private String formatOrderStructFullInfo(OrderStruct order)
            {
        StringBuffer orderText = new StringBuffer(1000);
        orderText.append("Firm: ").append(FormatFactory.getFormattedExchangeFirm(order.orderId.executingOrGiveUpFirm)).append('\n');
        orderText.append("Correspondent Firm: ").append(order.orderId.correspondentFirm).append('\n');
        orderText.append("Branch Seq. #: ").append(order.orderId.branch).append(' ');
        orderText.append(order.orderId.branchSequenceNumber).append('\n');

        orderText.append("Order Date: ").append(order.orderId.orderDate.substring(0, 4)).append('/');
        orderText.append(order.orderId.orderDate.substring(4, 6)).append('/');
        orderText.append(order.orderId.orderDate.substring(6, 8)).append('\n');

        orderText.append("Time in Force: ");
        orderText.append(TimesInForce.toString(order.timeInForce, TimesInForce.TRADERS_FORMAT));
        orderText.append('\n');

        // expire time has no meaning in version 1
        //        orderText.append("Expire Time: ").append(Utility.toString(order.expireTime, "yyyy/MM/dd HH:mm:ss.S")).append('\n');

        orderText.append("Received Time: ").append(Utility.toString(order.receivedTime, "yyyy/MM/dd HH:mm:ss.S")).append('\n');

        orderText.append("Current Active Session: ").append(order.activeSession).append('\n');

        String[] validSessions = order.sessionNames;
        if (validSessions.length > 0)
        {
            orderText.append("Valid Sessions: ");

            for (int i = 0; i < validSessions.length; i++)
            {
                orderText.append(validSessions[i]);

                if (i < validSessions.length - 1)
                {
                    orderText.append(", ");
                } else
                {
                    orderText.append('\n');
                }
            }
        }

        orderText.append("Side: ").append(Sides.toString(order.side, Sides.NO_BID_FORMAT)).append('\n');
        orderText.append("Crossing Order: ").append(order.cross ? "Yes" : "No").append('\n');

        ProductClass productClass = null;
        try
            {
                orderText.append("Class: ");

            productClass = APIHome.findProductQueryAPI().getProductClassByKey(order.classKey);

            if (productClass != null)
            {
                orderText.append(productClassFormatter.format(productClass, productClassFormatter.CLASS_TYPE_NAME)).append('\n');
                orderText.append("Underlying Symbol: ").append(productFormatter.format(productClass.getUnderlyingProduct())).append('\n');
            } else
            {
                orderText.append("(Not Available)").append('\n');
                orderText.append("Underlying Symbol: ").append("(Not Available)").append('\n');
            }
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(Category + ".format()", e);
            orderText.append("(Not Available)").append('\n');
            orderText.append("Underlying Symbol: ").append("(Not Available)").append('\n');
        }

        orderText.append("Product: ").append(FormatFactory.buildSeriesName(order.productKey, Category)).append('\n');
        orderText.append("Original Qty: ").append(volumeFormatter.format(order.originalQuantity)).append('\n');
        orderText.append("Executed Total Qty: ").append(volumeFormatter.format(order.tradedQuantity)).append('\n');
        orderText.append("Executed Session Qty: ").append(volumeFormatter.format(order.sessionTradedQuantity)).append('\n');
        orderText.append("Canceled Total Qty: ").append(volumeFormatter.format(order.cancelledQuantity)).append('\n');
        orderText.append("Canceled Session Qty: ").append(volumeFormatter.format(order.sessionCancelledQuantity)).append('\n');
        if (order.price.type != PriceTypes.NO_PRICE)
        {
            orderText.append("Price: ").append(DisplayPriceFactory.create(order.price)).append('\n');
        } else
        {
            orderText.append("Price: No Price\n");
        }
        orderText.append("Session Average Price: ").append(DisplayPriceFactory.create(order.sessionAveragePrice)).append('\n');

        orderText.append("Contingency: ").append(contingencyFormatter.format(order.contingency, ContingencyFormatStrategy.FULL)).append('\n');
        orderText.append("Position: ").append(PositionEffects.toString(order.positionEffect)).append('\n');
        orderText.append("State: ").append(OrderStates.toString(order.state)).append('\n');
        orderText.append("Coverage: ").append(CoverageTypes.toString(order.coverage)).append('\n');
        orderText.append("Originator: ").append(FormatFactory.getFormattedExchangeAcronym(order.originator)).append('\n');
        orderText.append("Origin Type: ").append(OrderOrigins.toString(order.orderOriginType));
        if (order.orderOriginType == OrderOrigins.PRINCIPAL)
        {
            orderText.append(" / ").append(FormatFactory.getOrderExtensionsFormatStrategy().format(order.extensions, ExtensionFields.EXCHANGE_DESTINATION, OrderExtensionsFormatStrategy.BRIEF_INFO_NAME));
        }
        orderText.append('\n');
        orderText.append("CMTA: ").append(FormatFactory.getFormattedExchangeFirm(order.cmta)).append('\n');
        orderText.append("Account: ").append(order.account).append('\n');
        orderText.append("Subaccount: ").append(order.subaccount).append('\n');
        orderText.append("User ID: ").append(order.userId).append('\n');
        orderText.append("User Assigned ID: ").append(order.userAssignedId).append('\n');
        orderText.append("Source: ").append(Sources.toString(order.source)).append('\n');
        //orderText.append("ORS Id: ").append(order.orsId).append('\n');
        orderText.append("ORS Id: ").append(formatOrsIdForDisplay(order.orsId)).append('\n');
        orderText.append("Optional Data: ").append(order.optionalData);
        return orderText.toString();
    }

    public String format(OrderCancelMessageElement aMessageElement)
    {
        VolumeFormatStrategy volumeFormatter = FormatFactory.getVolumeFormatStrategy();
        StringBuilder orderCancelText = new StringBuilder(format(aMessageElement.getOrder(), BRIEF_INFO_NAME_OMT));
        orderCancelText.append(' ').append(volumeFormatter.format(aMessageElement.getCancelRequest().getQuantity()));

        return orderCancelText.toString();
    }

    /**
     * Defines a method for formatting Order Id.
     * @param OrderId to format.
     * @param styleName to use for formatting
     * @return formatted string
     */
    public String format(OrderId id, String styleName)
    {
        validateStyle(styleName);
        StringBuilder orderText = new StringBuilder(80);
        if(styleName.equalsIgnoreCase(BRIEF_INFO_FBSCID))                                                  // TODO: remove when Jasper port is completed
        {
            orderText.append(id.getBranch()).append(':');
            orderText.append(id.getBranchSequenceNumber()).append(' ');
            orderText.append(id.getCorrespondentFirm()).append(' ');
            orderText.append(FormatFactory.getFormattedExchangeFirm(id.getExecutingOrGiveUpFirm())).append(' ');
            String orderDate = id.getOrderDate();
            orderText.append(orderDate.substring(0, 4)).append('/');
            orderText.append(orderDate.substring(4, 6)).append('/');
            orderText.append(orderDate.substring(6, 8)).append(' ');
        }
        return orderText.toString();
    }

    public static String buildStringForProduct(String msgTypeStr, SessionProduct legProductResult, int prodKey, String sessionName)
    {
        String returnString = msgTypeStr + " (";
        String endString = ") - ";
        if(legProductResult != null)
        {
            returnString += legProductResult.toString();
        }
        else
        {
            Product prod = ProductHelper.getProduct(prodKey);
            if(prod != null)
            {
                returnString += prod.toString();
            }
            else
            {
                returnString += sessionName + " product key: " + prodKey;
            }
        }
        return returnString + endString;
    }

    public static String formatOrsIdForDisplay(String orsid)
    {
        //convert letters to lower case, except for letter 'L'.
        return orsid.toLowerCase().replace('l','L');
    }
}
