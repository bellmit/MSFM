//
// ------------------------------------------------------------------------
// Source file: AlertImpl.java
//
// PACKAGE: com.cboe.internalPresentation.alert
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------

package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.ExchangeMarketStruct;
import com.cboe.idl.cmiIntermarketMessages.AlertStruct;
import com.cboe.idl.cmiIntermarketMessages.AlertHdrStruct;
import com.cboe.idl.cmiMarketData.ExchangeVolumeStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.ExchangeMarket;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.Alert;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.AlertHeader;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.presentation.order.OrderId;
import com.cboe.interfaces.presentation.product.ProductKeys;
import com.cboe.interfaces.presentation.util.CBOEId;
import com.cboe.intermarketPresentation.intermarketMessages.ExchangeMarketFactory;
import com.cboe.presentation.common.formatters.AlertResolutions;
import com.cboe.presentation.common.formatters.AlertTypes;
import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.dateTime.DateTimeImpl;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.formatters.ExchangeMarketInfoType;
import com.cboe.presentation.common.formatters.ProductTypes;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.order.OrderIdFactory;
import com.cboe.presentation.product.ProductKeysImpl;
import com.cboe.presentation.user.ExchangeFirmFactory;
import com.cboe.presentation.util.CBOEIdImpl;
import com.cboe.domain.util.ExtensionsHelper;

import java.util.Calendar;
import java.text.ParseException;


/**
 * @author torresl@cboe.com
 */
class AlertImpl extends AbstractMutableBusinessModel implements Alert
{
    protected String              resolution;
    protected String              comments;
    protected OrderId             orderId;
    protected String              nbboAgentId;
    protected String              updatedById;
    protected CBOEId              tradeId;
    protected ProductKeys         productKeys;
    protected ExchangeMarket[]    exchangeMarkets;
    protected boolean             cboeMarketableOrder;
    protected AlertStruct         alertStruct;
    protected AlertHeader         alertHeader;
    protected ExtensionsHelper    extensionsHelper;
    public AlertImpl(AlertStruct alertStruct)
    {
        this.alertStruct = alertStruct;
        initialize();
    }
    public AlertImpl(boolean populateWithDefaultData)
    {
        this.alertStruct = new AlertStruct();
        if(populateWithDefaultData)
        {
            Calendar cal = Calendar.getInstance();
            int high = cal.get(Calendar.MINUTE) + (cal.get(Calendar.HOUR_OF_DAY) + 1)* 60;
            int low = cal.get(Calendar.MILLISECOND) + (cal.get(Calendar.SECOND) + 1) * 1000;
            alertStruct.alertHdr = new AlertHdrStruct();
            alertStruct.alertHdr.alertId = new CboeIdStruct(high, low);
            alertStruct.alertHdr.alertCreationTime = new DateTimeImpl(cal.getTime().getTime()).getDateTimeStruct();
            alertStruct.alertHdr.alertType = AlertTypes.NBBO_TRADE_THROUGH;
            alertStruct.alertHdr.sessionName = "W_AM1";
            alertStruct.alertHdr.hdrExtensions = "";
            alertStruct.resolution = AlertResolutions.NOT_RESOLVED;
            alertStruct.comments = "";
            alertStruct.orderId = new OrderIdStruct();
            alertStruct.orderId.executingOrGiveUpFirm = ExchangeFirmFactory.createExchangeFirm("CBOE", "123").getExchangeFirmStruct();
            alertStruct.orderId.branch = "BRN";
            alertStruct.orderId.branchSequenceNumber = 1;
            alertStruct.orderId.correspondentFirm = "AAA";
            alertStruct.orderId.highCboeId = 123;
            alertStruct.orderId.lowCboeId = 456;
            alertStruct.nbboAgentId = "CCC";
            alertStruct.updatedById = "";
            alertStruct.tradeId = new CboeIdStruct(123, 456);
            alertStruct.productKeys = new ProductKeysStruct();
            alertStruct.productKeys.productKey = 0;
            alertStruct.productKeys.productType = ProductTypes.OPTION;
            alertStruct.productKeys.reportingClass = 0;
            alertStruct.exchangeMarket = getTestExchangeMarketStruct();

            alertStruct.cboeMarketableOrder = false;
            alertStruct.extensions = "";
            initialize();
        }
    }
    private ExchangeMarketStruct[] getTestExchangeMarketStruct()
    {
        ExchangeMarketStruct[] exchangeMarketStructs = new ExchangeMarketStruct[2];
        // first entry
        ExchangeVolumeStruct[] ask1ExchangeVolumeStructs = new ExchangeVolumeStruct[2];
        ask1ExchangeVolumeStructs[0] = new ExchangeVolumeStruct();
        ask1ExchangeVolumeStructs[0].exchange = "CBOE";
        ask1ExchangeVolumeStructs[0].volume = 10;
        ask1ExchangeVolumeStructs[1] = new ExchangeVolumeStruct();
        ask1ExchangeVolumeStructs[1].exchange = "LIFFE"; //ExchangeStrings.
        ask1ExchangeVolumeStructs[1].volume = 15;

        ExchangeVolumeStruct[] bid1ExchangeVolumeStructs = new ExchangeVolumeStruct[2];
        bid1ExchangeVolumeStructs[0] = new ExchangeVolumeStruct();
        bid1ExchangeVolumeStructs[0].exchange = "ISE";
        bid1ExchangeVolumeStructs[0].volume = 17;
        bid1ExchangeVolumeStructs[1] = new ExchangeVolumeStruct();
        bid1ExchangeVolumeStructs[1].exchange = "CBOE"; //ExchangeStrings.
        bid1ExchangeVolumeStructs[1].volume = 20;

        exchangeMarketStructs[0] = new ExchangeMarketStruct();
        exchangeMarketStructs[0].askExchangeVolumes = ask1ExchangeVolumeStructs;
        exchangeMarketStructs[0].bidExchangeVolumes = bid1ExchangeVolumeStructs;
        exchangeMarketStructs[0].bestAskPrice = DisplayPriceFactory.create("1.50").toStruct();
        exchangeMarketStructs[0].bestBidPrice = DisplayPriceFactory.create("1.25").toStruct();
        exchangeMarketStructs[0].marketInfoType = ExchangeMarketInfoType.NBBO_ORDER_RECEIVED;
        // second entry
        ExchangeVolumeStruct[] ask2ExchangeVolumeStructs = new ExchangeVolumeStruct[2];
        ask2ExchangeVolumeStructs[0] = new ExchangeVolumeStruct();
        ask2ExchangeVolumeStructs[0].exchange = "CME";
        ask2ExchangeVolumeStructs[0].volume = 20;
        ask2ExchangeVolumeStructs[1] = new ExchangeVolumeStruct();
        ask2ExchangeVolumeStructs[1].exchange = "CBOT"; //ExchangeStrings.
        ask2ExchangeVolumeStructs[1].volume = 25;

        ExchangeVolumeStruct[] bid2ExchangeVolumeStructs = new ExchangeVolumeStruct[2];
        bid2ExchangeVolumeStructs[0] = new ExchangeVolumeStruct();
        bid2ExchangeVolumeStructs[0].exchange = "CBOT";
        bid2ExchangeVolumeStructs[0].volume = 27;
        bid2ExchangeVolumeStructs[1] = new ExchangeVolumeStruct();
        bid2ExchangeVolumeStructs[1].exchange = "CME"; //ExchangeStrings.
        bid2ExchangeVolumeStructs[1].volume = 30;

        exchangeMarketStructs[1] = new ExchangeMarketStruct();
        exchangeMarketStructs[1].askExchangeVolumes = ask2ExchangeVolumeStructs;
        exchangeMarketStructs[1].bidExchangeVolumes = bid2ExchangeVolumeStructs;
        exchangeMarketStructs[1].bestAskPrice = DisplayPriceFactory.create("1.75").toStruct();
        exchangeMarketStructs[1].bestBidPrice = DisplayPriceFactory.create("1.00").toStruct();
        exchangeMarketStructs[1].marketInfoType = ExchangeMarketInfoType.BBO_ORDER_RECEIVED;


        return exchangeMarketStructs;
    }
    private void initialize()
    {
        alertHeader         = AlertHeaderFactory.createAlertHeader(alertStruct.alertHdr);
        resolution          = alertStruct.resolution;
        if(resolution.trim().length()==0)
        {
            // empty string, replace with NR
            resolution = AlertResolutions.NOT_RESOLVED;
        }
        comments            = alertStruct.comments;
        orderId             = OrderIdFactory.createOrderId(alertStruct.orderId);
        nbboAgentId         = alertStruct.nbboAgentId;
        updatedById         = alertStruct.updatedById;
        tradeId             = new CBOEIdImpl(alertStruct.tradeId);
        productKeys         = new ProductKeysImpl(alertStruct.productKeys);
        cboeMarketableOrder = alertStruct.cboeMarketableOrder;
        exchangeMarkets     = new ExchangeMarket[alertStruct.exchangeMarket.length];
        for (int i = 0; i < alertStruct.exchangeMarket.length; i++)
        {
            exchangeMarkets[i]= ExchangeMarketFactory.createExchangeMarket(alertStruct.exchangeMarket[i]);
        }
        try
        {
            getExtensionsHelper().setExtensions(alertStruct.extensions);
        }
        catch (ParseException e)
        {
            GUILoggerHome.find().exception(e, e.getMessage());
        }
    }

    public AlertHeader getAlertHeader()
    {
        return alertHeader;
    }

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

    public String getResolution()
    {
        return resolution;
    }

    public String getComments()
    {
        return comments;
    }

    public OrderId getOrderId()
    {
        return orderId;
    }

    public String getNbboAgentId()
    {
        return nbboAgentId;
    }

    public String getUpdatedById()
    {
        return updatedById;
    }

    public CBOEId getTradeId()
    {
        return tradeId;
    }

    public String getSessionName()
    {
        return getAlertHeader().getSessionName();
    }

    public ProductKeys getProductKeys()
    {
        return productKeys;
    }

    public ExchangeMarket[] getExchangeMarket()
    {
        return exchangeMarkets;
    }

    public boolean getCboeMarketableOrder()
    {
        return cboeMarketableOrder;
    }

    public String getExtensions()
    {
        return getExtensionsHelper().toString();
    }

    public void setResolution(String resolution)
    {
        String oldResolution = this.resolution;
        this.resolution = resolution;
        setModified(true);
        firePropertyChange(RESOLUTION_PROPERTY, oldResolution, resolution);
    }

    public void setComments(String comments)
    {
        String oldComments = this.comments;
        this.comments = comments;
        setModified(true);
        firePropertyChange(COMMENTS_PROPERTY, oldComments, comments);
    }

    public Object clone()
    {
        return new AlertImpl(getStruct());
    }
    protected ExchangeMarketStruct[] getExchangeMarketStructs()
    {
        ExchangeMarketStruct[] structs = new ExchangeMarketStruct[exchangeMarkets.length];
        for (int i = 0; i < structs.length; i++)
        {
            structs[i] = exchangeMarkets[i].getStruct();
        }
        return structs;
    }
    public AlertStruct getStruct()
    {
        AlertStruct struct = new AlertStruct();
        struct.alertHdr = getAlertHeader().getStruct();
        struct.resolution = resolution;
        struct.comments = comments;
        struct.orderId = orderId.getStruct();
        struct.nbboAgentId = nbboAgentId;
        struct.updatedById = updatedById;
        struct.tradeId = tradeId.getStruct();
        struct.productKeys = productKeys.getStruct();
        struct.exchangeMarket = getExchangeMarketStructs();
        struct.cboeMarketableOrder = cboeMarketableOrder;
        struct.extensions = getExtensions();

        return struct;
    }

    public Object getKey()
    {
        return getAlertHeader().getAlertId();
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
