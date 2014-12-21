//
// -----------------------------------------------------------------------------------
// Source file: ProductCusipImpl.java
//
// PACKAGE: com.cboe.internalPresentation.productDefinition
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.product;

import java.util.Date;
import com.cboe.interfaces.internalPresentation.product.ProductCusip;

/**
 * This class encapsulates a product CUSIP string as well as the last time it was refreshed from server.
 * @author      Shawn Khosravani
 * @version     1.0  March 24, 2005
 * @see         com.cboe.interfaces.internalPresentation.product.ProductCusip
 * @see         com.cboe.interfaces.internalPresentation.product.ProductModel
 * @see         com.cboe.internalPresentation.productDefinition.UpdateProductCusipPanel
 */

public class ProductCusipImpl implements ProductCusip
{
    private String cusipValue;
    private Date   lastRefreshTime;


    public ProductCusipImpl ()
    {
        this("", null);
    }

    public ProductCusipImpl (String value)
    {
        this(value, null);
    }

    public ProductCusipImpl (String value, Date date)
    {
        cusipValue      = value;
        lastRefreshTime = date;
    }

    /**
     * implements Cloenable. overrides Object's protected method with a public one, and it does not
     * throw CloneNotSupportedException (i.e. it must implement cloneable)
     *
     * @return an instance of ProductCusipImpl whose content is set using the member fields cusipValue and lastRefreshTime
     */

    public Object clone()
    {
        return new ProductCusipImpl(cusipValue, lastRefreshTime);
    }

    public String getCusipValue()
    {
        return cusipValue;
    }

    public void setCusipValue(String newValue)
    {
        if (newValue == null)
        {
            newValue = "";
        }
        cusipValue = newValue;
    }

    public Date getLastRefreshTime()
    {
        return lastRefreshTime;
    }

    public void setLastRefreshTime(Date newDate)
    {
        // if (newDate == null) newDate = new Date();

        lastRefreshTime = newDate;
    }

    public String toString()
    {
        return "Cusip=[" + cusipValue + "] lastRefreshTime=[" + lastRefreshTime + "]";
    }

} // end class ProductCusipImpl
