//
// -----------------------------------------------------------------------------------
// Source file: HeldOrderDetailImpl.java
//
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.domain.util.DateWrapper;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderDetailStruct;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderStruct;
import com.cboe.idl.cmiIntermarketMessages.ExchangeMarketStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.formatters.ProductTypes;
import com.cboe.presentation.common.formatters.StatusUpdateReasons;
import com.cboe.presentation.common.time.TimeSyncWrapper;
import com.cboe.presentation.product.ProductNameFactory;
import com.cboe.presentation.order.OrderFactory;

import java.util.Date;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.HeldOrderDetail;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.HeldOrder;
import com.cboe.interfaces.presentation.product.ProductName;
import com.cboe.intermarketPresentation.common.formatters.FormatFactory;

class HeldOrderDetailImpl implements HeldOrderDetail
{
    protected ProductName productName;
    protected Short statusChange;
    protected HeldOrder heldOrder;
    protected HeldOrderDetailStruct heldOrderDetailStruct;
    protected String displayString = null;
    public HeldOrderDetailImpl(HeldOrderDetailStruct heldOrderDetailStruct)
    {
        this.heldOrderDetailStruct = heldOrderDetailStruct;
        initialize();
    }

    private void initialize()
    {
        displayString = null;
        if(heldOrderDetailStruct.productInformation != null)
        {
            productName = ProductNameFactory.createProductName(heldOrderDetailStruct.productInformation);
        }
        else
        {
            String reportingClass = "";
            PriceStruct exercisePrice = DisplayPriceFactory.create(0.0).toStruct();
            DateStruct expirationDate = new DateWrapper(new Date(TimeSyncWrapper.getCorrectedTimeMillis())).toDateStruct();
            char optionType = (char)ProductTypes.OPTION;
            String productSymbol = "";

            ProductNameStruct productNameStruct = new ProductNameStruct(reportingClass, exercisePrice, expirationDate, optionType, productSymbol);
            productName = ProductNameFactory.createProductName(productNameStruct);
        }
        statusChange = new Short(heldOrderDetailStruct.statusChange);
        if(heldOrderDetailStruct.heldOrder != null)
        {
            heldOrder = HeldOrderFactory.createHeldOrder(heldOrderDetailStruct.heldOrder);
        }
        else
        {
            HeldOrderStruct hos = new HeldOrderStruct();
            hos.order = OrderFactory.createDefaultOrderStruct();
            hos.currentMarketBest = new ExchangeMarketStruct[0];
            heldOrder = HeldOrderFactory.createHeldOrder(hos);
        }
    }

    public ProductName getProductInformation()
    {
        return productName;
    }

    public Short getStatusChange()
    {
        return statusChange;
    }

    public HeldOrder getHeldOrder()
    {
        return heldOrder;
    }

    /**
     * Gets the underlying struct
     * @return HeldOrderDetailStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public HeldOrderDetailStruct getStruct()
    {
        return heldOrderDetailStruct;
    }

    public void setStruct(HeldOrderDetailStruct heldOrderDetailStruct)
    {
        this.heldOrderDetailStruct = heldOrderDetailStruct;
        initialize();
    }

    public String toString()
    {
        if(displayString == null)
        {
            displayString = FormatFactory.getHeldOrderFormatStrategy().format(this);
        }
        return displayString;
    }
}
