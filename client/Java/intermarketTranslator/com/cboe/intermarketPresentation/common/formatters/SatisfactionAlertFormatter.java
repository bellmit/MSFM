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

import java.text.ParseException;
import java.util.*;

import com.cboe.idl.cmiConstants.ExtensionFields;
import com.cboe.idl.cmiIntermarketMessages.SatisfactionAlertStruct;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.SatisfactionAlert;
import com.cboe.interfaces.presentation.common.formatters.DateFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ExtensionsFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ProductFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.SatisfactionAlertFormatStrategy;
import com.cboe.interfaces.presentation.order.OrderId;
import com.cboe.interfaces.presentation.product.SessionProduct;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.formatters.*;
import com.cboe.presentation.common.formatters.FormatFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.exchange.GUIExchangeHome;

import com.cboe.intermarketPresentation.intermarketMessages.SatisfactionAlertFactory;

/**
 * @author torresl@cboe.com
 */
class SatisfactionAlertFormatter extends AbstractCommonStylesFormatter implements SatisfactionAlertFormatStrategy
{

    private final String Category = this.getClass().getName();

    public SatisfactionAlertFormatter()
    {
        super();
        initialize();
    }

    private void initialize()
    {
        setDefaultStyle(FULL_STYLE_NAME);
    }

    public String format(SatisfactionAlertStruct alertStruct)
    {
        return format(alertStruct, getDefaultStyle());
    }

    public String format(SatisfactionAlertStruct alertStruct, String style)
    {
        return format(SatisfactionAlertFactory.createSatisfactionAlert(alertStruct), style);
    }

    public String format(SatisfactionAlert alert)
    {
        return format(alert, getDefaultStyle());
    }

    public String format(SatisfactionAlert alert, String style)
    {
        validateStyle(style);
        StringBuffer buffer = new StringBuffer(500);
        String delimiter = getDelimiterForStyle(style);
        boolean brief = isBrief(style);

        if (! brief)
        {
            Utility.portWarningToBePorted(Category + ".format(SatisfactionAlert)");                                     // TODO: remove when Jasper port is completed
        }

        if (brief)
        {
            buffer.append(GUIExchangeHome.find().findOrCreateExchange(alert.getLastSale().exchangeSymbol).getExchange()).append(delimiter);
            try
            {
                SessionProduct sessionProduct = APIHome.findProductQueryAPI().getProductByKeyForSession(alert.getSessionName(), alert.getLastSale().productKeys.productKey);
                buffer.append(sessionProduct.getTradingSessionName()).append(delimiter);
                buffer.append(ProductTypes.toString(sessionProduct.getProductType())).append(delimiter);
         //       buffer.append(CommonFormatFactory.getProductFormatStrategy().format(sessionProduct, ProductFormatStrategy.FULL_PRODUCT_NAME));
                buffer.append(CommonFormatFactory.getProductFormatStrategy().format(sessionProduct,ProductFormatStrategy.FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE));
                buffer.append(delimiter);
            }
            catch (Exception e)
            {
                buffer.append("INVALID SESSION PRODUCT [SESSION:").append(alert.getSessionName());
                buffer.append(" PRODUCT KEY=").append(alert.getLastSale().productKeys.productKey).append("] ");
                GUILoggerHome.find().exception(e);
            }

            buffer.append(Sides.toString(alert.getSide(), Sides.BUY_SELL_FORMAT)).append(" ");
            buffer.append(alert.getTradedThroughQuantity()).append(" @ ").append(alert.getTradedThroughPrice().toString());
            buffer.append(delimiter);
        }
        else
        {
            buffer.append("Alert Type: ").append(AlertTypes.toString(alert.getAlertHeader().getAlertType()));
            buffer.append(delimiter).append("Alert Time: ");
            buffer.append(
                    CommonFormatFactory.getDateFormatStrategy().format(
                            alert.getAlertHeader().getAlertCreationTime(),
                            DateFormatStrategy.DATE_FORMAT_24_HOURS_STYLE));
            buffer.append(delimiter);
            buffer.append("Alert ID: ").append(alert.getAlertHeader().getAlertId().getHighId());
            buffer.append(":").append(alert.getAlertHeader().getAlertId().getLowId());
            buffer.append(delimiter);
            buffer.append("Exchange: ").append( GUIExchangeHome.find().findOrCreateExchange(alert.getLastSale().exchangeSymbol).getExchange() );
            buffer.append(" - ").append(GUIExchangeHome.find().findOrCreateExchange(alert.getLastSale().exchangeSymbol).getFullName());
            buffer.append(delimiter);
            buffer.append("Product: ");
            try
            {
                SessionProduct sessionProduct = APIHome.findProductQueryAPI().getProductByKeyForSession(alert.getSessionName(), alert.getLastSale().productKeys.productKey);
                buffer.append(CommonFormatFactory.getProductFormatStrategy().format(sessionProduct, ProductFormatStrategy.FULL_PRODUCT_NAME_WITH_SESSION_AND_TYPE));
                buffer.append(delimiter);
            }
            catch (Exception e)
            {
                buffer.append("INVALID SESSION PRODUCT [SESSION:").append(alert.getSessionName());
                buffer.append(" PRODUCT KEY=").append(alert.getLastSale().productKeys.productKey).append("] ");
                GUILoggerHome.find().exception(e);
            }

            if(alert.getAlertHeader().getExtensions().length()>0)
            {
                buffer.append(FormatFactory.getExtensionsFormatStrategy().format(alert.getAlertHeader().getExtensions(), ExtensionsFormatStrategy.FULL_STYLE_NAME));
                buffer.append(delimiter);
            }

            if(alert.getExtensions().length()>0)
            {
                String expirationTime = alert.getExtensionField(ExtensionFields.EXPIRATION_TIME);
                Date expiration = null;
                if(expirationTime != null)
                {
                    try
                    {
                        expiration = CommonFormatFactory.getDateFormatStrategy().getDateFormat(DateFormatStrategy.DATE_FORMAT_TIMER_EXPIRATION_STYLE).parse(expirationTime);
                        buffer.append("Expiration Time: ").append(CommonFormatFactory.getDateFormatStrategy().format(expiration));
                        buffer.append(delimiter);
                    }
                    catch (ParseException e)
                    {
                        GUILoggerHome.find().exception(e, e.getMessage());
                    }
                }
            }
            OrderId[] orderIds = alert.getTradedThroughOrders();
            buffer.append("Trade Through Details:").append(delimiter);
            buffer.append("Order Info: ");
            buffer.append(Sides.toString(alert.getSide(), Sides.BUY_SELL_FORMAT)).append(" ");
            buffer.append(alert.getTradedThroughQuantity()).append(" @ ").append(alert.getTradedThroughPrice());
            buffer.append(delimiter);
            buffer.append("Order").append(orderIds.length>1 ? "s" : "").append(" Details: ").append(delimiter);
            for (int i = 0; i < orderIds.length; i++)
            {
                OrderId orderId = orderIds[i];
                buffer.append(delimiter);
                buffer.append("Order Id: ").append(orderId.getCboeId().getHighId());
                buffer.append(":").append(orderId.getCboeId().getLowId()).append(" ");
                buffer.append(delimiter);
                buffer.append("Branch/Sequence: ").append(orderId.getFormattedBranchSequence());
                buffer.append(delimiter);
                buffer.append("GiveUp Firm: ").append(orderId.getExecutingOrGiveUpFirm().getExchange());
                buffer.append(".").append(orderId.getExecutingOrGiveUpFirm().getFirm());
                buffer.append(delimiter);
                buffer.append("Correspondent Firm: ").append(orderId.getCorrespondentFirm());
                buffer.append(delimiter);
            }
        }

        return buffer.toString();
    }
}
