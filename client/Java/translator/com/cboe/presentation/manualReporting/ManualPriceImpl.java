//
// -----------------------------------------------------------------------------------
// Source file: ManualPriceImpl.java
//
// PACKAGE: com.cboe.presentation.manualReporting
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.manualReporting;

import com.cboe.client.util.PriceHelper;
import com.cboe.domain.util.PriceFactory;
import com.cboe.domain.util.StructBuilder;
import com.cboe.domain.util.ProductStructBuilder;
import com.cboe.idl.cmiConstants.ExchangeStrings;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.marketData.ManualPriceReportEntryStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.dateTime.Time;
import com.cboe.interfaces.presentation.common.formatters.ManualReportingFormatStrategy;
import com.cboe.interfaces.presentation.manualReporting.ManualPrice;
import com.cboe.interfaces.presentation.product.ProductKeys;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionReportingClass;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.dateTime.TimeImpl;
import com.cboe.presentation.common.formatters.FormatFactory;
import com.cboe.presentation.product.ProductHelper;
import com.cboe.presentation.product.ProductKeysFactory;
import com.cboe.presentation.user.ExchangeAcronymFactory;

public class ManualPriceImpl extends AbstractMutableBusinessModel implements ManualPrice
{
    protected ManualPriceReportEntryStruct manualPriceReportEntryStruct;

    protected String displayName = null;
    protected Time tradeTime;
    protected Price price;
    protected ExchangeAcronym buyerBroker;
    protected ExchangeAcronym sellerBroker;
    protected SessionProduct sessionProduct;
    protected SessionReportingClass sessionReportingClass;
    protected SessionProductClass sessionProductClass;

    static private ManualReportingFormatStrategy formatter = null;

    protected ManualPriceImpl()
    {
        this(new ManualPriceReportEntryStruct(new ProductKeysStruct(-1,-1, ProductTypes.OPTION,-1),
                    "W_MAIN",
                    PriceHelper.createPriceStruct(0.0),
                    0,
                    StructBuilder.buildExchangeAcronymStruct(ExchangeStrings.CBOE,""),
                    StructBuilder.buildExchangeAcronymStruct(ExchangeStrings.CBOE,""),
                    "",
                    StructBuilder.buildTimeStruct()));
    }

    protected ManualPriceImpl(ManualPriceReportEntryStruct manualPriceReportEntryStruct)
    {
        super();
        this.manualPriceReportEntryStruct = manualPriceReportEntryStruct;
        if(formatter == null)
        {
            formatter = FormatFactory.getManualReportingFormatStrategy();
        }
    }

    protected ManualPriceImpl(SessionProduct sessionProduct)
    {
        this(new ManualPriceReportEntryStruct(sessionProduct.getProductKeysStruct(),
                    sessionProduct.getTradingSessionName(),
                    PriceHelper.createPriceStruct(0.0),
                    0,
                    StructBuilder.buildExchangeAcronymStruct(ExchangeStrings.CBOE,""),
                    StructBuilder.buildExchangeAcronymStruct(ExchangeStrings.CBOE,""),
                    "",
                    StructBuilder.buildTimeStruct()));

        setSessionProduct(sessionProduct);
    }

    protected ManualPriceImpl(SessionProductClass sessionProductClass)
    {
        this(new ManualPriceReportEntryStruct(new ProductKeysStruct(-1,
                                                                    sessionProductClass.getClassKey(),
                                                                    sessionProductClass.getProductType(),
                                                                    -1),
                                            sessionProductClass.getTradingSessionName(),
                                            PriceHelper.createPriceStruct(0.0),
                                            0,
                                            StructBuilder.buildExchangeAcronymStruct(ExchangeStrings.CBOE,""),
                                            StructBuilder.buildExchangeAcronymStruct(ExchangeStrings.CBOE,""),
                                            "",
                                            StructBuilder.buildTimeStruct()));

        setSessionProductClass(sessionProductClass);
    }

    protected ManualPriceImpl(SessionReportingClass sessionReportingClass)
    {
        this(new ManualPriceReportEntryStruct(new ProductKeysStruct(-1,
                                                                    sessionReportingClass.getProductClassKey(),
                                                                    sessionReportingClass.getProductType(),
                                                                    sessionReportingClass.getClassKey()),
                                            sessionReportingClass.getTradingSessionName(),
                                            PriceHelper.createPriceStruct(0.0),
                                            0,
                                            StructBuilder.buildExchangeAcronymStruct(ExchangeStrings.CBOE,""),
                                            StructBuilder.buildExchangeAcronymStruct(ExchangeStrings.CBOE,""),
                                            "",
                                            StructBuilder.buildTimeStruct()));

        setSessionReportingClass(sessionReportingClass);
    }

    public Time getTradeTime()
    {
        if(tradeTime == null)
        {
            tradeTime = new TimeImpl(manualPriceReportEntryStruct.tradeTime);
        }
        return tradeTime;
    }

    public ProductKeys getProductKeys()
    {
        ProductKeys productKeys = null;
        if(getSessionProduct() != null)
        {
            productKeys = ProductKeysFactory.createProductKeys(getSessionProduct().getProductKeysStruct());
        }
        return productKeys;
    }

    public String getSessionName()
    {
        String sessionName = null;
        if(getSessionProduct() != null)
        {
            sessionName = getSessionProduct().getTradingSessionName();
        }

        return sessionName;
    }

    public String getSalePrefix()
    {
        return getStruct().salePrefix;
    }

    public Price getLastSalePrice()
    {
        if(price ==null)
        {
            price = PriceFactory.create(getStruct().price);
        }
        return price;
    }

    public int getLastSaleVolume()
    {
        return getStruct().volume;
    }

    public ExchangeAcronym getBuyerBroker()
    {
        if(buyerBroker == null)
        {
            buyerBroker = ExchangeAcronymFactory.createExchangeAcronym(getStruct().buyerBroker);
        }
        return buyerBroker;
    }

    public ExchangeAcronym getSellerBroker()
    {
        if(sellerBroker == null)
        {
            sellerBroker = ExchangeAcronymFactory.createExchangeAcronym(getStruct().sellerBroker);
        }
        return sellerBroker;
    }

    public ManualPriceReportEntryStruct getStruct()
    {
        return manualPriceReportEntryStruct;
    }

    public SessionProduct getSessionProduct()
    {
        if(sessionProduct == null )
        {
            if(getStruct().productKeys.productKey>0)
            {
                sessionProduct = ProductHelper.getSessionProduct(getStruct().sessionName,
                                                             getStruct().productKeys.productKey);
            }
        }
        return sessionProduct;
    }

    public SessionReportingClass getSessionReportingClass()
    {
        if(sessionReportingClass == null )
        {
            SessionProductClass sessionProductClass = getSessionProductClass();
            SessionProduct sessionProduct = getSessionProduct();
            if(sessionProductClass != null && sessionProduct != null)
            {
                for(SessionReportingClass rpt:sessionProductClass.getSessionReportingClasses())
                {
                    if(sessionProduct.getProductKeysStruct().reportingClass == rpt.getClassKey())
                    {
                        sessionReportingClass = rpt;
                        break;
                    }
                }
            }
        }
        return sessionReportingClass;
    }

    public SessionProductClass getSessionProductClass()
    {
        if(sessionProductClass == null )
        {
            if(getStruct().productKeys.classKey>0)
            {
                sessionProductClass = ProductHelper.getSessionProductClass(getStruct().sessionName,
                                                             getStruct().productKeys.classKey);
            }
        }
        return sessionProductClass;
    }

    public void setSessionProduct(SessionProduct sessionProduct)
    {
        Object oldValue = getProductKeys();
        this.sessionProduct = sessionProduct;
        if(sessionProduct == null)
        {
            getStruct().productKeys.productKey = -1;
        }
        else
        {
            getStruct().productKeys = ProductStructBuilder.cloneProductKeys(sessionProduct.getProductKeysStruct());
        }
        setModified(true);
        firePropertyChange(PROPERTY_PRODUCT_KEYS, oldValue, getStruct().productKeys);
    }

    public void setSessionReportingClass(SessionReportingClass sessionReportingClass)
    {
        this.sessionReportingClass = sessionReportingClass;
        if(sessionReportingClass != null)
        {
            getStruct().productKeys.reportingClass = sessionReportingClass.getClassKey();
        }
        setSessionProduct(null);
    }

    public void setSessionProductClass(SessionProductClass sessionProductClass)
    {
        this.sessionProductClass = sessionProductClass;
        if(sessionProductClass != null)
        {
            getStruct().productKeys.classKey = sessionProductClass.getClassKey();
        }
        setSessionReportingClass(null);
    }

    public void setTradeTime(Time time)
    {
        Object oldValue = getTradeTime();
        getStruct().tradeTime = time.getTimeStruct();
        tradeTime = time;
        setModified(true);
        firePropertyChange(PROPERTY_TRADE_TIME, oldValue, time);
    }

    public void setSalePrefix(String prefix)
    {
        Object oldValue = getSalePrefix();
        getStruct().salePrefix = prefix;
        setModified(true);
        firePropertyChange(PROPERTY_SALE_PREFIX, oldValue, prefix);
    }

    public void setLastSalePrice(Price lastSalePrice)
    {
        Object oldValue = getLastSalePrice();
        getStruct().price = lastSalePrice.toStruct();
        this.price = lastSalePrice;
        setModified(true);
        firePropertyChange(PROPERTY_SALE_PRICE, oldValue, lastSalePrice);
    }

    public void setLastSaleVolume(int lastSaleVolume)
    {
        Object oldValue = getLastSaleVolume();
        getStruct().volume = lastSaleVolume;
        setModified(true);
        firePropertyChange(PROPERTY_SALE_VOLUME, oldValue, lastSaleVolume);
    }

    public void setBuyerBroker(ExchangeAcronym broker)
    {
        Object oldValue = getBuyerBroker();
        getStruct().buyerBroker = broker.getExchangeAcronymStruct();
        buyerBroker = broker;
        setModified(true);
        firePropertyChange(PROPERTY_BUYER_BROKER, oldValue, broker);
    }

    public void setSellerBroker(ExchangeAcronym broker)
    {
        Object oldValue = getSellerBroker();
        getStruct().sellerBroker = broker.getExchangeAcronymStruct();
        sellerBroker = broker;
        setModified(true);
        firePropertyChange(PROPERTY_SELLER_BROKER, oldValue, broker);
    }
    /**
     * Returns a String representation of this Quote.
     */
    public String toString()
    {
        if(displayName == null)
        {
            displayName = formatter.format(getStruct(), formatter.BRIEF);
        }
        return displayName;
    }
}
