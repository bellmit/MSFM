//
// -----------------------------------------------------------------------------------
// Source file: ManualQuoteImpl.java
//
// PACKAGE: com.cboe.presentation.manualReporting
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.manualReporting;

import com.cboe.client.util.PriceHelper;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiConstants.Sides;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiUtil.KeyValueStruct;
import com.cboe.idl.quote.ManualQuoteDetailStruct;
import com.cboe.idl.quote.ManualQuoteStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.common.formatters.ManualReportingFormatStrategy;
import com.cboe.interfaces.presentation.manualReporting.ManualQuote;
import com.cboe.interfaces.presentation.manualReporting.ManualQuoteDetail;
import com.cboe.interfaces.presentation.product.ProductKeys;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.product.SessionReportingClass;
import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.formatters.FormatFactory;
import com.cboe.presentation.product.ProductHelper;
import com.cboe.presentation.product.ProductKeysFactory;
import com.cboe.domain.util.ProductStructBuilder;

/**
 * Quote implementation for a QuoteStruct from the API.
 */
class ManualQuoteImpl extends AbstractMutableBusinessModel implements ManualQuote
{
    protected String displayName = null;
    protected Price price = null;
    protected ProductKeys productKeys = null;
    protected ManualQuoteDetail manualQuoteDetail = null;
    static private ManualReportingFormatStrategy formatter = null;

    protected SessionProduct sessionProduct = null;
    protected SessionReportingClass sessionReportingClass = null;
    protected SessionProductClass sessionProductClass = null;

    protected ManualQuoteStruct manualQuoteStruct = null;
    /**
     * Constructors
     */
    protected ManualQuoteImpl()
    {
        this(new ManualQuoteStruct("W_MAIN",
                    new ProductKeysStruct(-1,-1, ProductTypes.OPTION,-1),
                    Sides.UNSPECIFIED,
                    PriceHelper.createPriceStruct(0.0),
                    0,
                    false,
                    new ManualQuoteDetailStruct(),
                    new KeyValueStruct[]{}));
    }

    protected ManualQuoteImpl(ManualQuoteStruct manualQuoteStruct)
    {
        super();
        this.manualQuoteStruct = manualQuoteStruct;
        if(formatter == null)
        {
            formatter = FormatFactory.getManualReportingFormatStrategy();
        }
        setSide(Sides.SELL);
        setOverrideIndicator(false);
    }

    protected ManualQuoteImpl(SessionProduct sessionProduct)
    {
        this(new ManualQuoteStruct(sessionProduct.getTradingSessionName(),
                    sessionProduct.getProductKeysStruct(),
                    Sides.UNSPECIFIED,
                    PriceHelper.createPriceStruct(0.0),
                    0,
                    false,
                    new ManualQuoteDetailStruct(),
                    new KeyValueStruct[]{}));

        setSessionProduct(sessionProduct);
    }

    protected ManualQuoteImpl(SessionProductClass sessionProductClass)
    {
        this(new ManualQuoteStruct(sessionProductClass.getTradingSessionName(),
                    new ProductKeysStruct(-1,
                                          sessionProductClass.getClassKey(),
                                          sessionProductClass.getProductType(),
                                          -1),
                    Sides.UNSPECIFIED,
                    PriceHelper.createPriceStruct(0.0),
                    0,
                    false,
                    new ManualQuoteDetailStruct(),
                    new KeyValueStruct[]{}));

        setSessionProductClass(sessionProductClass);
    }

    protected ManualQuoteImpl(SessionReportingClass sessionReportingClass)
    {
        this(new ManualQuoteStruct(sessionReportingClass.getTradingSessionName(),
                    new ProductKeysStruct(-1,
                                          sessionReportingClass.getProductClassKey(),
                                          sessionReportingClass.getProductType(),
                                          sessionReportingClass.getClassKey()),
                    Sides.UNSPECIFIED,
                    PriceHelper.createPriceStruct(0.0),
                    0,
                    false,
                    new ManualQuoteDetailStruct(),
                    new KeyValueStruct[]{}));

        setSessionReportingClass(sessionReportingClass);
    }

    /**
     * Get the session name for this ManualQuote.
     * @return session name String represented struct
     */
    public String getSessionName()
    {
        String sessionName = null;
        if(getSessionProduct() != null)
        {
            sessionName = getSessionProduct().getTradingSessionName();
        }

        return sessionName;
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


    public char getSide()
    {
        return getStruct().side;
    }

    /**
     * Get the price for this ManualQuote.
     * @return Price
     */
    public Price getPrice()
    {
        if(price == null)
        {
            price = DisplayPriceFactory.create(getStruct().price);
        }
        return price;
    }

    /**
     * Get the bid quantity for this ManualQuote.
     * @return quantity
     */
    public int getSize()
    {
        return getStruct().size;
    }

    /**
     * Get the override indicator for this ManualQuote.
     * @return quantity
     */
    public boolean isOverrideIndicator()
    {
        return getStruct().overrideIndicator;
    }

    public ManualQuoteDetail getManualQuoteDetail() {
        if(manualQuoteDetail == null)
        {
            manualQuoteDetail = ManualReportingFactory.create(getStruct().details);
        }
        return manualQuoteDetail;
    }

    public KeyValueStruct[] getExtensions()
    {
        return getStruct().extensions;
    }
    
    /**
     * Get the SessionProduct for this Quote.
     * @return SessionProduct
     */
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

    /**
     * Set the Side for this ManualQuote.
     * @param side
     */
    public void setSide(char side)
    {
        Object oldValue = getSide();
        getStruct().side = side;
        setModified(true);
        firePropertyChange(PROPERTY_SIDE, oldValue, side);
    }

    /**
     * Set the price for this ManualQuote.
     * @param price
     */
    public void setPrice(Price price)
    {
        Object oldValue = getPrice();
        this.price = price;
        getStruct().price = price.toStruct();
        setModified(true);
        firePropertyChange(PROPERTY_PRICE, oldValue, price);
    }

    /**
     * Set the volume for this ManualQuote.
     * @param size
     */
    public void setSize(int size)
    {
        Object oldValue = getSize();
        getStruct().size = size;
        setModified(true);
        firePropertyChange(PROPERTY_SIZE, oldValue, size);
    }

    /**
     * Set the override for ManualQuote
     * @param override
     */
    public void setOverrideIndicator(boolean override)
    {
        Object oldValue = isOverrideIndicator();
        getStruct().overrideIndicator = override;
        setModified(true);
        firePropertyChange(PROPERTY_OVERRIDE, oldValue, override);
    }

    /**
     * Set the ManualQuoteDetail for this ManualQuote
     * @param manualQuoteDetail
     */
    public void setManualQuoteDetail(ManualQuoteDetail manualQuoteDetail)
    {
        this.manualQuoteDetail = manualQuoteDetail;
        getStruct().details = manualQuoteDetail.getStruct();
        setModified(true);
    }


    /**
     * Set the extensions for ManualQuote
     * @param keyValues
     */
    public void setExtensions(KeyValueStruct[] keyValues)
    {
        getStruct().extensions = keyValues;
        setModified(true);
    }

    public ManualQuoteStruct getStruct() {
        return manualQuoteStruct;
    }

    public Object clone()
    {
        return new ManualQuoteImpl(getStruct());
    }

    /**
     * If <code>obj</code> is an instance of Quote and has the same
     * quote key true is returned, false otherwise.
     * @param obj to compare
     * @return true if equal, false if not.
     */
    public boolean equals(Object obj)
    {
        boolean isEqual;

        if(this == obj)
        {
            isEqual = true;
        }
        else if(obj instanceof ManualQuote)
        {
            isEqual = getKey() == ((ManualQuote)obj).getKey();
        }
        else
        {
            isEqual = false;
        }

        return isEqual;
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
