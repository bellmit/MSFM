//
// ------------------------------------------------------------------------
// FILE: AlertFormatter.java
//
// PACKAGE: com.cboe.intermarketPresentation.common.formatters
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.intermarketPresentation.common.formatters;

import com.cboe.idl.cmiIntermarketMessages.AlertStruct;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.Alert;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.ExchangeMarket;
import com.cboe.interfaces.presentation.common.formatters.AlertFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.DateFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ExchangeMarketFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ExtensionsFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ProductFormatStrategy;
import com.cboe.interfaces.presentation.product.SessionProduct;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.formatters.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.intermarketPresentation.intermarketMessages.AlertFactory;

/**
 * @author torresl@cboe.com
 */
class AlertFormatter extends AbstractCommonStylesFormatter implements AlertFormatStrategy
{

    public AlertFormatter()
    {
        super();
        initialize();
    }

    private void initialize()
    {
        setDefaultStyle(FULL_STYLE_NAME);
    }

    public String format(AlertStruct alertStruct)
    {
        return format(alertStruct, getDefaultStyle());
    }

    public String format(AlertStruct alertStruct, String style)
    {
        return format(AlertFactory.createAlert(alertStruct), style);
    }

    public String format(Alert alert)
    {
        return format(alert, getDefaultStyle());
    }

    public String format(Alert alert, String style)
    {
        // TODO: implement formatter
        validateStyle(style);
        StringBuffer buffer = new StringBuffer(500);
        boolean brief = isBrief(style);
        String delimiter = getDelimiterForStyle(style);
        if( brief )
        {
            buffer.append(AlertTypes.toString(alert.getAlertHeader().getAlertType()));
            buffer.append(delimiter);
            buffer.append(
                    CommonFormatFactory.getDateFormatStrategy().format(
                            alert.getAlertHeader().getAlertCreationTime(),
                            DateFormatStrategy.DATE_FORMAT_24_HOURS_STYLE));
            buffer.append(delimiter);
            buffer.append(AlertResolutions.toString(alert.getResolution(), AlertResolutions.TRADERS_FORMAT));
            buffer.append(delimiter);
            try
            {
                SessionProduct sessionProduct = APIHome.findProductQueryAPI().getProductByKeyForSession(alert.getSessionName(), alert.getProductKeys().getProductKey());
                buffer.append(CommonFormatFactory.getProductFormatStrategy().format(sessionProduct, ProductFormatStrategy.PRODUCT_NAME_WO_TYPE));
            }
            catch (Exception e)
            {
                GUILoggerHome.find().exception(e);
            }
            buffer.append("Trade Id: ").append(alert.getTradeId().getHighId());
            buffer.append(":").append(alert.getTradeId().getLowId());
            buffer.append(delimiter);
            buffer.append("Order Id: ").append(alert.getOrderId().getCboeId().getHighId());
            buffer.append(":").append(alert.getOrderId().getCboeId().getLowId());
        }
        else
        {
            buffer.append("Alert Type: ").append(AlertTypes.toString(alert.getAlertHeader().getAlertType()));
            buffer.append(" ").append(
                    CommonFormatFactory.getDateFormatStrategy().format(
                            alert.getAlertHeader().getAlertCreationTime(),
                            DateFormatStrategy.DATE_FORMAT_24_HOURS_STYLE));
            buffer.append(" Resolution: ");
            buffer.append(AlertResolutions.toString(alert.getResolution(), AlertResolutions.TRADERS_FORMAT));
            buffer.append("Alert ID: ").append(alert.getAlertHeader().getAlertId().getHighId());
            buffer.append(":").append(alert.getAlertHeader().getAlertId().getLowId());
            buffer.append(delimiter);
            buffer.append("Comments: ").append(alert.getComments().length()==0 ? "[NONE]": alert.getComments());
            buffer.append(delimiter);
            buffer.append("NBBO Agent ID: ").append(alert.getNbboAgentId());
            buffer.append(" Updated By: ").append(alert.getUpdatedById());
            buffer.append(delimiter);
            try
            {
                SessionProduct sessionProduct = APIHome.findProductQueryAPI().getProductByKeyForSession(alert.getSessionName(), alert.getProductKeys().getProductKey());
                buffer.append("Product Info: ");
                buffer.append(CommonFormatFactory.getProductFormatStrategy().format(sessionProduct, ProductFormatStrategy.FULL_PRODUCT_NAME_WITH_SESSION_AND_TYPE));
                buffer.append(delimiter);
            }
            catch (Exception e)
            {
                GUILoggerHome.find().exception(e);
            }
            buffer.append("Trade Id: ").append(alert.getTradeId().getHighId());
            buffer.append(":").append(alert.getTradeId().getLowId());
            buffer.append(delimiter);
            buffer.append("Order Id: ").append(alert.getOrderId().getCboeId().getHighId());
            buffer.append(":").append(alert.getOrderId().getCboeId().getLowId()).append(" ");
            buffer.append(" ");
            buffer.append("Branch/Sequence: ").append(alert.getOrderId().getFormattedBranchSequence());
            buffer.append(delimiter);
            buffer.append("GiveUp Firm: ").append(alert.getOrderId().getExecutingOrGiveUpFirm().getExchange());
            buffer.append(".").append(alert.getOrderId().getExecutingOrGiveUpFirm().getFirm());
            buffer.append(" ");
            buffer.append("Correspondent Firm: ").append(alert.getOrderId().getCorrespondentFirm());
            buffer.append(delimiter);
            buffer.append("CBOE Marketable Order: ").append(
                    Boolean.toString(alert.getCboeMarketableOrder()).toUpperCase()
            );

            buffer.append(com.cboe.presentation.common.formatters.FormatFactory.getExtensionsFormatStrategy().format("", ExtensionsFormatStrategy.FULL_STYLE_NAME));

            ExchangeMarket[] exchangeMarkets = alert.getExchangeMarket();
            for (int i = 0; i < exchangeMarkets.length; i++)
            {
                ExchangeMarket exchangeMarket = exchangeMarkets[i];
                buffer.append(
                        FormatFactory.getExchangeMarketFormatStrategy().format(
                                exchangeMarket,
                                ExchangeMarketFormatStrategy.FULL_STYLE));
                buffer.append(delimiter);

            }
        }
        return buffer.toString();
    }
}
