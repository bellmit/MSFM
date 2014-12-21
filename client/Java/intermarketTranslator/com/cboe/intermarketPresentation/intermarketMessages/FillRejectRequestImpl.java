//
// ------------------------------------------------------------------------
// FILE: FillRejectRequestImpl.java
//
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.FillRejectRequest;
import com.cboe.interfaces.presentation.order.OrderId;
import com.cboe.interfaces.domain.Price;
import com.cboe.domain.util.ExtensionsHelper;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.formatters.ExtensionFields;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.order.OrderIdFactory;
import com.cboe.idl.cmiIntermarketMessages.FillRejectRequestStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiUtil.PriceStruct;

import java.text.ParseException;

/**
 * @author torresl@cboe.com
 */
public class FillRejectRequestImpl implements FillRejectRequest
{
    protected OrderId orderId;
    protected Short rejectReason;
    protected Price tradePrice;
    protected Integer tradedQuantity;
    protected ExtensionsHelper extensionsHelper;
    protected FillRejectRequestStruct fillRejectRequestStruct;
    protected boolean isCreatedFromStruct;
    public FillRejectRequestImpl(FillRejectRequestStruct fillRejectRequestStruct)
    {
        super();
        this.fillRejectRequestStruct = fillRejectRequestStruct;
        initializeFromStruct();
    }
    public FillRejectRequestImpl()
    {
        super();
        initialize();
    }
    private void initializeFromStruct()
    {
        this.orderId = OrderIdFactory.createOrderId(fillRejectRequestStruct.orderId);
        this.rejectReason = new Short(fillRejectRequestStruct.rejectReason);
        this.tradedQuantity = new Integer(fillRejectRequestStruct.tradedQuantity);
        this.tradePrice = DisplayPriceFactory.create(fillRejectRequestStruct.tradePrice);
        setExtensions(fillRejectRequestStruct.fillReportExtensions);
        isCreatedFromStruct = true;
    }
    private void initialize()
    {
        orderId = null;
        rejectReason = null;
        tradedQuantity = null;
        tradePrice = null;
    }
    public OrderId getOrderId()
    {
        return orderId;
    }

    public void setOrderId(OrderId orderId)
    {
        this.orderId = orderId;
    }

    public Short getRejectReason()
    {
        return rejectReason;
    }

    public void setRejectReason(Short rejectReason)
    {
        this.rejectReason = rejectReason;
    }

    public Price getTradePrice()
    {
        return tradePrice;
    }

    public void setTradePrice(Price tradePrice)
    {
        this.tradePrice = tradePrice;
    }

    public Integer getTradedQuantity()
    {
        return tradedQuantity;
    }

    public void setTradedQuantity(Integer tradedQuantity)
    {
        this.tradedQuantity = tradedQuantity;
    }

    public String getExtensions()
    {
        return getExtensionsHelper().toString();
    }

    public void setExtensions(String extensions)
    {
        try
        {
            getExtensionsHelper().setExtensions(extensions);
        }
        catch (ParseException e)
        {
            GUILoggerHome.find().exception(e, e.getMessage());
        }
    }

    public String getAwayExchangeExecutionId()
    {
        return getExtensionsHelper().getValue(ExtensionFields.AWAY_EXCHANGE_EXEC_ID);
    }

    public void setAwayExchangeExecutionId(String awayExchangeExecutionId)
    {
        try
        {
            getExtensionsHelper().setValue(ExtensionFields.AWAY_EXCHANGE_EXEC_ID, awayExchangeExecutionId);
        }
        catch (ParseException e)
        {
            GUILoggerHome.find().exception(e, e.getMessage());
        }
    }

    public String getExecutionReceiptTime()
    {
        return getExtensionsHelper().getValue(ExtensionFields.EXECUTION_RECEIPT_TIME);
    }

    public void setExecutionReceiptTime(String executionReceiptTime)
    {
        try
        {
            getExtensionsHelper().setValue(ExtensionFields.EXECUTION_RECEIPT_TIME, executionReceiptTime);
        }
        catch (ParseException e)
        {
            GUILoggerHome.find().exception(e, e.getMessage());
        }
    }

    protected ExtensionsHelper getExtensionsHelper()
    {
        if (extensionsHelper == null)
        {
            extensionsHelper = new ExtensionsHelper();
        }
        return extensionsHelper;
    }

    public FillRejectRequestStruct getStruct()
    {
        FillRejectRequestStruct struct = new FillRejectRequestStruct();
        struct.orderId = getOrderId().getStruct();
        struct.rejectReason = getRejectReason().shortValue();
        struct.tradedQuantity = getTradedQuantity().intValue();
        struct.tradePrice = getTradePrice().toStruct();
        struct.fillReportExtensions = getExtensions();
        return struct;
    }

    public boolean isCreatedFromStruct()
    {
        return isCreatedFromStruct;
    }
}
