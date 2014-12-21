//
// ------------------------------------------------------------------------
// FILE: HeldOrderFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.intermarketPresentation.common.formatters;

import com.cboe.idl.cmiConstants.ReportTypes;
import com.cboe.idl.cmiIntermarketMessages.FillRejectStruct;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderCancelReportStruct;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderCancelRequestStruct;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderDetailStruct;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderFilledReportStruct;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderStruct;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiOrder.OrderContingencyStruct;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.ExchangeMarket;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.FillReject;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.HeldOrder;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.HeldOrderCancelReport;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.HeldOrderCancelRequest;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.HeldOrderDetail;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.HeldOrderFilledReport;
import com.cboe.interfaces.presentation.common.formatters.CancelRequestFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ExchangeMarketFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.HeldOrderFormatStrategy;
import com.cboe.interfaces.presentation.order.FilledReport;
import com.cboe.interfaces.presentation.common.exchange.Exchange;

import com.cboe.presentation.common.formatters.Formatter;
import com.cboe.presentation.common.formatters.StatusUpdateReasons;
import com.cboe.presentation.common.formatters.Utility;

import com.cboe.intermarketPresentation.intermarketMessages.FillRejectFactory;
import com.cboe.intermarketPresentation.intermarketMessages.HeldOrderCancelReportFactory;
import com.cboe.intermarketPresentation.intermarketMessages.HeldOrderCancelRequestFactory;
import com.cboe.intermarketPresentation.intermarketMessages.HeldOrderDetailFactory;
import com.cboe.intermarketPresentation.intermarketMessages.HeldOrderFactory;
import com.cboe.intermarketPresentation.intermarketMessages.HeldOrderFilledReportFactory;

/**
 * @author torresl@cboe.com
 */
class HeldOrderFormatter extends Formatter implements HeldOrderFormatStrategy
{
    private final String Category = this.getClass().getName();

    protected static final String BRIEF_STYLE_DELIMITER = " ";
    protected static final String FULL_STYLE_DELIMITER = "\n";
    public HeldOrderFormatter()
    {
        super();
        initialize();
    }

    private void initialize()
    {
        addStyle(BRIEF_INFO_NAME, BRIEF_INFO_DESCRIPTION);
        addStyle(BRIEF_INFO_LEAVES_NAME, BRIEF_INFO_LEAVES_DESCRIPTION);
        addStyle(FULL_INFO_NAME, FULL_INFO_DESCRIPTION);
        addStyle(FULL_INFO_TWO_COLUMN_NAME, FULL_INFO_TWO_COLUMN_DESCRIPTION);
        setDefaultStyle(FULL_INFO_TWO_COLUMN_NAME);
    }
    public String format(HeldOrderStruct heldOrderStruct)
    {
        return format(heldOrderStruct, getDefaultStyle());
    }

    public String format(HeldOrderStruct heldOrderStruct, String style)
    {
        return format(HeldOrderFactory.createHeldOrder(heldOrderStruct), style);
    }

    public String format(HeldOrder heldOrder)
    {
        return format(heldOrder, getDefaultStyle());
    }

    public String format(HeldOrder heldOrder, String style)
    {
        validateStyle(style);

        StringBuffer buffer = new StringBuffer();
        // format the order struct

        if (! isBriefStyle(style))
        {
            Utility.portWarningToBePorted(Category + ".format(HeldOrder)");                                             // TODO: remove when Jasper port is completed
        }

        Exchange exchange = heldOrder.getAwayExchange();
        if (exchange != null)
        {
            if (! isBriefStyle(style))
            {
                buffer.append("Exchange: ").append(exchange.getExchange());
                buffer.append(" - ").append(exchange.getFullName());
                buffer.append(FULL_STYLE_DELIMITER);
            }
            else
            {
                buffer.append(exchange.getExchange());
                buffer.append(BRIEF_STYLE_DELIMITER);
            }
        }
        buffer.append(com.cboe.presentation.common.formatters.FormatFactory.getOrderFormatStrategy().format(heldOrder, style));
        // now format the exchange/volume struct
        ExchangeMarket[] exchangeMarkets = heldOrder.getCurrentMarketBest();
        if( ! isBriefStyle(style) )
        {
            for (int i = 0; i < exchangeMarkets.length; i++)
            {
                ExchangeMarket exchangeMarket = exchangeMarkets[i];
                buffer.append(FormatFactory.getExchangeMarketFormatStrategy().format(exchangeMarket, ExchangeMarketFormatStrategy.FULL_STYLE_TWO_COLUMN_NAME));
            }
        }

        return buffer.toString();
    }

    private String commonFormat(HeldOrderFilledReport heldOrderFilledReport, String style)
    {
        StringBuffer buffer = new StringBuffer();
        if(isBriefStyle(style) == false)
        {
            HeldOrderDetail heldOrderDetail = heldOrderFilledReport.getHeldOrderDetail();
            buffer.append(format(heldOrderDetail, style)).append(" ");
            buffer.append("\nFill Details\n");
            buffer.append("----------------------------------------------------------------------\n");
        }
        return buffer.toString();
    }
    public String format(HeldOrderFilledReport heldOrderFilledReport, String style)
    {
        validateStyle(style);

        if (! isBriefStyle(style))
        {
            Utility.portWarningToBePorted(Category + ".format(HeldOrderFilledReport)");                                 // TODO: remove when Jasper port is completed
        }

        StringBuffer buffer = new StringBuffer();
        buffer.append(commonFormat(heldOrderFilledReport, style));
        HeldOrderDetail heldOrderDetail = heldOrderFilledReport.getHeldOrderDetail();
        OrderContingencyStruct orderContingencyStruct = heldOrderDetail.getHeldOrder().getContingency().getStruct();
        FilledReport[] filledReports = heldOrderFilledReport.getFilledReports();
        for (int i = 0; i < filledReports.length; i++)
        {
            FilledReportStruct filledReportStruct = filledReports[i].getStruct();
            if(filledReportStruct.fillReportType == ReportTypes.FILL_REJECT)
            {
                buffer.append("FILL REJECTED").append(FULL_STYLE_DELIMITER);
            }
            buffer.append(
                    com.cboe.presentation.common.formatters.FormatFactory.getOrderFormatStrategy().format(
                            filledReportStruct, orderContingencyStruct, style)
            );
            buffer.append(FULL_STYLE_DELIMITER);
        }
        return buffer.toString();
    }

    public String format(HeldOrderFilledReport heldOrderFilledReport, int filledReportIndex)
    {
        return format(heldOrderFilledReport, filledReportIndex, getDefaultStyle());
    }

    public String format(HeldOrderFilledReport heldOrderFilledReport, int filledReportIndex, String style)
    {
        validateStyle(style);
        StringBuffer buffer = new StringBuffer();

        if (! isBriefStyle(style))
        {
            Utility.portWarningToBePorted(Category + ".format(HeldOrderFilledReport,int)");                             // TODO: remove when Jasper port is completed
        }

        buffer.append(commonFormat(heldOrderFilledReport, style));
        HeldOrderDetail heldOrderDetail = heldOrderFilledReport.getHeldOrderDetail();
        OrderContingencyStruct orderContingencyStruct = heldOrderDetail.getHeldOrder().getContingency().getStruct();
        FilledReport[] filledReports = heldOrderFilledReport.getFilledReports();
        String delimiter = isBriefStyle(style) ? BRIEF_STYLE_DELIMITER : FULL_STYLE_DELIMITER;
        if(filledReportIndex>=0 && filledReportIndex <filledReports.length)
        {
            FilledReportStruct filledReportStruct = filledReports[filledReportIndex].getStruct();
            if(filledReportStruct.fillReportType == ReportTypes.FILL_REJECT)
            {
                buffer.append("FILL REJECTED").append(delimiter);
            }
            buffer.append(
                    com.cboe.presentation.common.formatters.FormatFactory.getOrderFormatStrategy().format(
                            filledReportStruct, orderContingencyStruct, style)
            );
            buffer.append(delimiter);
        }
        return buffer.toString();
    }


    public String format(HeldOrderCancelReport heldOrderCancelReport)
    {
        return format(heldOrderCancelReport, getDefaultStyle());
    }

    public String format(HeldOrderCancelReportStruct heldOrderCancelReportStruct)
    {
        return format(heldOrderCancelReportStruct, getDefaultStyle());
    }

    public String format(HeldOrderCancelReportStruct heldOrderCancelReportStruct, String style)
    {
        return format(
                HeldOrderCancelReportFactory.createHeldOrderCancelReport(heldOrderCancelReportStruct),
                style);
    }

    public String format(HeldOrderCancelReport heldOrderCancelReport, String style)
    {
        validateStyle(style);;
        StringBuffer buffer = new StringBuffer();
        String delimiter = FULL_STYLE_DELIMITER;
        boolean brief = isBriefStyle(style);

        if (! brief)
        {
            Utility.portWarningToBePorted(Category + ".format(HeldOrderCancelReport)");                                 // TODO: remove when Jasper port is completed
        }

        if(brief)
        {
           delimiter = BRIEF_STYLE_DELIMITER;
        }
        buffer.append(format(heldOrderCancelReport.getHeldOrderDetail(), style));
        if(!brief)
        {
            int column2Margin = 38;
            int sizeFieldAdded = 0;
            buffer.append(delimiter);
            buffer.append("Cancel Request Id: ").append(heldOrderCancelReport.getCancelRequestId().toString()).append(delimiter);

            buffer.append("Cancelled Vol: ");
            sizeFieldAdded = addFieldWithSizeLimit(buffer, com.cboe.presentation.common.formatters.FormatFactory.getVolumeFormatStrategy().format(heldOrderCancelReport.getCancelReport().getCancelledQuantity().intValue()), column2Margin - 1 - 15, false);
            buffer = appendSpace(buffer, column2Margin - 15 - sizeFieldAdded);

            buffer.append("Too Late For Cancel Vol: ").append(com.cboe.presentation.common.formatters.FormatFactory.getVolumeFormatStrategy().format(heldOrderCancelReport.getCancelReport().getTlcQuantity().intValue())).append('\n');

            buffer.append("Total Cancelled Vol: ");
            sizeFieldAdded = addFieldWithSizeLimit(buffer, com.cboe.presentation.common.formatters.FormatFactory.getVolumeFormatStrategy().format(heldOrderCancelReport.getCancelReport().getTotalCancelledQuantity().intValue()), column2Margin - 1 - 21, false);
            buffer = appendSpace(buffer, column2Margin - 21 - sizeFieldAdded);

            buffer.append("Mismatched Vol: ").append(com.cboe.presentation.common.formatters.FormatFactory.getVolumeFormatStrategy().format(heldOrderCancelReport.getCancelReport().getMismatchedQuantity().intValue())).append('\n');

            buffer.append("Cancel Reason: ");
            sizeFieldAdded = addFieldWithSizeLimit(buffer, com.cboe.presentation.common.formatters.FormatFactory.getCancelReasonFormatStrategy().format(heldOrderCancelReport.getCancelReport().getCancelReason().shortValue()), column2Margin - 1 - 15, false);
            buffer = appendSpace(buffer, column2Margin - 15 - sizeFieldAdded);

            buffer.append("Session: ").append(heldOrderCancelReport.getCancelReport().getSessionName()).append('\n');

            buffer.append("Date/Time: ").append(Utility.toString(heldOrderCancelReport.getCancelReport().getTimeSent().getDateTimeStruct(), "yyyy/MM/dd HH:mm:ss.S"));
        }
        else
        {
            buffer.append(com.cboe.presentation.common.formatters.FormatFactory.getVolumeFormatStrategy().format(heldOrderCancelReport.getCancelReport().getCancelledQuantity().intValue())).append(' ');
            buffer.append("cancelled, ");
            buffer.append(com.cboe.presentation.common.formatters.FormatFactory.getVolumeFormatStrategy().format(heldOrderCancelReport.getCancelReport().getTotalCancelledQuantity().intValue())).append(' ');
            buffer.append("total cancelled");
        }
        return buffer.toString();
    }

    public String format(HeldOrderFilledReport heldOrderFilledReport)
    {
        return format(heldOrderFilledReport, getDefaultStyle());
    }

    public String format(HeldOrderFilledReportStruct heldOrderFilledReportStruct)
    {
        return format(heldOrderFilledReportStruct, getDefaultStyle());
    }

    public String format(HeldOrderFilledReportStruct heldOrderFilledReportStruct, String style)
    {
        return format(
                HeldOrderFilledReportFactory.createHeldOrderFilledReport(heldOrderFilledReportStruct),
                style);
    }

    public String format(HeldOrderDetail heldOrderDetail, String style)
    {
        validateStyle(style);

        if (! isBriefStyle(style))
        {
            Utility.portWarningToBePorted(Category + ".format(HeldOrderDetail)");                                       // TODO: remove when Jasper port is completed
        }

        StringBuffer buffer = new StringBuffer();
        HeldOrder heldOrder = heldOrderDetail.getHeldOrder();
        String delimiter = FULL_STYLE_DELIMITER;
        boolean brief = isBriefStyle(style);
        if(!brief)
        {
            buffer.append("Update Reason: ");
        }
        buffer.append(StatusUpdateReasons.toString(heldOrderDetail.getStatusChange().shortValue()));
        if( ! brief )
        {
            buffer.append(delimiter);
            buffer.append(format(heldOrder, style));
        }
        return buffer.toString();
    }

    public String format(HeldOrderDetailStruct heldOrderDetailStruct)
    {
        return format(heldOrderDetailStruct, getDefaultStyle());
    }

    public String format(HeldOrderDetailStruct heldOrderDetailStruct, String style)
    {
        return format(
                HeldOrderDetailFactory.createHeldOrderDetail(heldOrderDetailStruct),
                style);
    }

    public String format(HeldOrderDetail heldOrderDetail)
    {
        return format(heldOrderDetail, getDefaultStyle());
    }


    public String format(FillRejectStruct fillRejectStruct)
    {
        return format(fillRejectStruct, getDefaultStyle());
    }

    public String format(FillRejectStruct fillRejectStruct, String style)
    {
        return  format(
                FillRejectFactory.createFillReject(fillRejectStruct),
                style
        );
    }

    public String format(FillReject fillReject)
    {
        return format(fillReject, getDefaultStyle());
    }

    public String format(FillReject fillReject, String style)
    {
        if (! isBriefStyle(style))
        {
            Utility.portWarningToBePorted(Category + ".format(FillReject)");                                            // TODO: remove when Jasper port is completed
        }

        // TODO: IMPLEMENT
        validateStyle(style);
        StringBuffer buffer = new StringBuffer();
        if(! isBriefStyle(style) ) // full style
        {
            buffer.append("Reject Reason: ");
            buffer.append(
                    com.cboe.presentation.common.formatters.FormatFactory.getCancelReasonFormatStrategy().format(
                        fillReject.getRejectReason().shortValue()));
            buffer.append(FULL_STYLE_DELIMITER);
            buffer.append("Trade ID: ").append(fillReject.getTradeId().toString());
            buffer.append("Transaction Sequence Number: ").append(fillReject.getTransactionSequenceNumber());
            buffer.append(FULL_STYLE_DELIMITER);

            buffer.append(fillReject.getTradeId().toString()).append(FULL_STYLE_DELIMITER);
            buffer.append(com.cboe.presentation.common.formatters.FormatFactory.getOrderFormatStrategy().format(fillReject.getOrder(), style));
//            buffer.append(FULL_STYLE_DELIMITER);
//            buffer.append(FormatFactory.getExtensionsFormatStrategy().format(fillReject.getExtensions()));
        }
        else // brief style
        {
            buffer.append(
                    com.cboe.presentation.common.formatters.FormatFactory.getCancelReasonFormatStrategy().format(
                            fillReject.getRejectReason().shortValue()));
            buffer.append(BRIEF_STYLE_DELIMITER);
            buffer.append(com.cboe.presentation.common.formatters.FormatFactory.getOrderFormatStrategy().format(fillReject.getOrder(), style));
        }
        return buffer.toString();
    }

    public String format(HeldOrderCancelRequestStruct heldOrderCancelRequestStruct)
    {
        return format(heldOrderCancelRequestStruct, getDefaultStyle());
    }

    public String format(HeldOrderCancelRequestStruct heldOrderCancelRequestStruct, String style)
    {
        return  format(
                HeldOrderCancelRequestFactory.createHeldOrderCancelRequest(heldOrderCancelRequestStruct),
                style);
    }

    public String format(HeldOrderCancelRequest heldOrderCancelRequest)
    {
        return format(heldOrderCancelRequest, getDefaultStyle());
    }

    public String format(HeldOrderCancelRequest heldOrderCancelRequest, String style)
    {
        if (! isBriefStyle(style))
        {
            Utility.portWarningToBePorted(Category + ".format(HeldOrderCancelRequest)");                                // TODO: remove when Jasper port is completed
        }

        validateStyle(style);
        StringBuffer buffer = new StringBuffer(200);
        String delimiter = FULL_STYLE_DELIMITER;
        boolean brief = isBriefStyle(style);
        if(brief)
        {
            delimiter = BRIEF_STYLE_DELIMITER;
            buffer.append(com.cboe.presentation.common.formatters.FormatFactory.getCancelRequestFormatStrategy().format(
                    heldOrderCancelRequest.getCancelRequest(),
                    CancelRequestFormatStrategy.BRIEF_STYLE_NAME));
        }
        else
        {
            buffer.append("Cancel Request Id: ").append(heldOrderCancelRequest.getCancelRequestId().toString());
            buffer.append(delimiter);
            buffer.append(com.cboe.presentation.common.formatters.FormatFactory.getCancelRequestFormatStrategy().format(
                    heldOrderCancelRequest.getCancelRequest(),
                    CancelRequestFormatStrategy.FULL_STYLE_NAME));
        }
        return buffer.toString();
    }


    protected boolean isBriefStyle(String style)
    {
        return style.equals(BRIEF_INFO_LEAVES_NAME) || style.equals(BRIEF_INFO_NAME);
    }
}
