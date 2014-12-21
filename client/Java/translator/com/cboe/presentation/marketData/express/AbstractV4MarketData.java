//
// -----------------------------------------------------------------------------------
// Source file: AbstractV4MarketData.java
//
// PACKAGE: com.cboe.presentation.marketData.express
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.marketData.express;

import org.omg.CORBA.UserException;

import com.cboe.interfaces.presentation.marketData.express.V4MarketData;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.ProductClass;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Represents a piece of V4 Market Data for an Exchange and Product.
 */
public abstract class AbstractV4MarketData implements V4MarketData
{
    protected String identifierString;
    protected Product product;
    protected ProductClass productClass;
    protected int seqNum = -1;

    protected AbstractV4MarketData(int messageSeqNumber)
    {
        setMessageSequenceNumber(messageSeqNumber);
    }

    public void setMessageSequenceNumber(int msgSeqNum)
    {
        seqNum = msgSeqNum;
    }

    public int getMessageSequenceNumber()
    {
        return seqNum;
    }

    /**
     * Returns a String to uniquely identify this V4MarketData, based on its Exchange and Product
     */
    public String getIdentifierString()
    {
        if(identifierString == null)
        {
            StringBuffer sb = new StringBuffer(getExchange());
            sb.append(getProductKey());
            identifierString = sb.toString();
        }
        return identifierString;
    }

    public Product getContainedProduct()
    {
        if(product == null)
        {
            if(getProductKey() == 0)
            {
                return APIHome.findProductQueryAPI().getDefaultProduct();
            }
            else
            {
                try
                {
                    product = APIHome.findProductQueryAPI().getProductByKey(getProductKey());
                }
                catch(UserException e)
                {
                    GUILoggerHome.find().exception("Could not find Product for productKey " + getProductKey(), e);
                }
            }
        }
        return product;
    }

    public ProductClass getContainedProductClass()
    {
        if(productClass == null)
        {
            if(getProductClassKey() == 0)
            {
                return APIHome.findProductQueryAPI().getDefaultProductClass();
            }
            else
            {
                try
                {
                    productClass = APIHome.findProductQueryAPI().getProductClassByKey(getProductClassKey());
                }
                catch(UserException e)
                {
                    GUILoggerHome.find().exception("Could not find ProductClass for classKey " + getProductClassKey(), e);
                }
            }
        }
        return productClass;
    }

    public String getProductRenderString()
    {
        return getContainedProduct().toString();
    }

    public int hashCode()
    {
        return getIdentifierString().hashCode();
    }

    /**
     * Two instances of RecapV4 are considered equal if they're for the same exchange and product.
     * @return true if exchanges and productKeys are equal
     */
    public boolean equals(Object obj)
    {
        boolean retVal = false;
        if(obj != null && obj instanceof V4MarketData)
        {
            V4MarketData other = (V4MarketData) obj;
            if(other.getExchange().equals(getExchange()) &&
                    other.getProductKey() == getProductKey())
            {
                retVal = true;
            }
        }
        return retVal;
    }
}
