//
// ------------------------------------------------------------------------
// FILE: SatisfactionAlertImpl.java
//
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.SatisfactionAlert;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.AlertHeader;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.order.OrderId;
import com.cboe.interfaces.presentation.util.CBOEId;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.idl.cmiIntermarketMessages.SatisfactionAlertStruct;
import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.order.OrderIdFactory;
import com.cboe.domain.util.ExtensionsHelper;

import java.text.ParseException;

/**
 * @author torresl@cboe.com
 */
class SatisfactionAlertImpl implements SatisfactionAlert
{
    protected AlertHeader alertHeader;
    protected int tradedThroughQuantity;
    protected Price tradedThroughPrice;
    protected char side;
    protected TickerStruct lastSale;
    protected OrderId[] tradedThroughOrders;
    protected SatisfactionAlertStruct satisfactionAlertStruct;
    protected ExtensionsHelper extensionsHelper;
    public SatisfactionAlertImpl(SatisfactionAlertStruct satisfactionAlertStruct)
    {
        super();
        this.satisfactionAlertStruct = satisfactionAlertStruct;
        initialize();
    }

    private void initialize()
    {
        alertHeader = AlertHeaderFactory.createAlertHeader(satisfactionAlertStruct.alertHdr);
        tradedThroughQuantity = satisfactionAlertStruct.tradedThroughquantity;
        tradedThroughPrice = DisplayPriceFactory.create(satisfactionAlertStruct.tradedThroughPrice);
        side = satisfactionAlertStruct.side;
        lastSale = satisfactionAlertStruct.lastSale;
        tradedThroughOrders = new OrderId[satisfactionAlertStruct.tradedThroughOrders.length];
        for (int i = 0; i < satisfactionAlertStruct.tradedThroughOrders.length; i++)
        {
            OrderIdStruct tradedThroughOrderIdStruct = satisfactionAlertStruct.tradedThroughOrders[i];
            tradedThroughOrders[i] = OrderIdFactory.createOrderId(tradedThroughOrderIdStruct);
        }
        try
        {
            getExtensionsHelper().setExtensions(satisfactionAlertStruct.extensions);
        }
        catch (ParseException e)
        {
            GUILoggerHome.find().exception(e, e.getMessage());
        }
    }

    // accessors for header information
    public CBOEId getAlertId()
    {
        return getAlertHeader().getAlertId();
    }

    public DateTime getAlertCreationTime()
    {
        return getAlertHeader().getAlertCreationTime();
    }

    public short getAlertType()
    {
        return getAlertHeader().getAlertType();
    }

    public String getSessionName()
    {
        return getAlertHeader().getSessionName();
    }

    public AlertHeader getAlertHeader()
    {
        return alertHeader;
    }

    public int getTradedThroughQuantity()
    {
        return tradedThroughQuantity;
    }

    public Price getTradedThroughPrice()
    {
        return tradedThroughPrice;
    }

    public char getSide()
    {
        return side;
    }

    public TickerStruct getLastSale()
    {
        return lastSale;
    }

    public OrderId[] getTradedThroughOrders()
    {
        return tradedThroughOrders;
    }

    public String getExtensions()
    {
        return getExtensionsHelper().toString();
    }

    public SatisfactionAlertStruct getStruct()
    {
        return satisfactionAlertStruct;
    }

    protected ExtensionsHelper getExtensionsHelper()
    {
        if(extensionsHelper == null)
        {
            extensionsHelper = new ExtensionsHelper();
        }
        return extensionsHelper;
    }

    public String getExtensionField(String fieldName)
    {
        return getExtensionsHelper().getValue(fieldName);
    }
}
