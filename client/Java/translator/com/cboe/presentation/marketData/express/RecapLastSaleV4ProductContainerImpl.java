//
// -----------------------------------------------------------------------------------
// Source file: RecapLastSaleV4ProductContainerImpl.java
//
// PACKAGE: com.cboe.presentation.marketData.express
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.marketData.express;

import org.omg.CORBA.UserException;

import com.cboe.interfaces.presentation.marketData.express.RecapLastSaleV4ProductContainer;
import com.cboe.interfaces.presentation.marketData.express.LastSaleV4;
import com.cboe.interfaces.presentation.marketData.express.RecapV4;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.ProductClass;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Wraps a RecapV4 and LastSaleV4 for a single Product.
 */
public class RecapLastSaleV4ProductContainerImpl implements RecapLastSaleV4ProductContainer
{
    private String identifierString;
    private String exchange;
    private int productKey;

    private RecapV4 recap;
    private LastSaleV4 lastSale;

    private Product product;
    private ProductClass productClass;

    public RecapLastSaleV4ProductContainerImpl()
    {
        this(new RecapV4Impl(), new LastSaleV4Impl());
    }

    public RecapLastSaleV4ProductContainerImpl(LastSaleV4 lastSale)
    {
        this.exchange = lastSale.getExchange();
        this.productKey = lastSale.getProductKey();

        this.lastSale = lastSale;
        this.recap = new RecapV4Impl(lastSale.getContainedProduct(), lastSale.getExchange());
    }

    public RecapLastSaleV4ProductContainerImpl(RecapV4 recap)
    {
        this.exchange = recap.getExchange();
        this.productKey = recap.getProductKey();

        this.recap = recap;
        this.lastSale = new LastSaleV4Impl(recap.getContainedProduct(), recap.getExchange());
    }

    public RecapLastSaleV4ProductContainerImpl(RecapV4 recap, LastSaleV4 lastSale)
    {
        if(recap.getProductKey() != lastSale.getProductKey() ||
           !recap.getExchange().equals(lastSale.getExchange()))
        {
            throw new IllegalArgumentException("RecapV4 and LastSaleV4 must be for the same Exchange and Product");
        }
        this.productKey = recap.getProductKey();
        this.exchange = recap.getExchange();
        this.recap = recap;
        this.lastSale = lastSale;
    }

    public Product getContainedProduct()
    {
        if(product == null)
        {
            try
            {
                product = APIHome.findProductQueryAPI().getProductByKey(getProductKey());
            }
            catch(UserException e)
            {
                GUILoggerHome.find().exception("Could not find Product for RecapLastSaleV4 bestMarket productKey " +
                                               getProductKey(), e);
            }
        }
        return product;
    }

    public ProductClass getContainedProductClass()
    {
        if(productClass == null)
        {
            try
            {
                productClass = APIHome.findProductQueryAPI().getProductClassByKey(getContainedProduct().getProductKeysStruct().classKey);
            }
            catch(UserException e)
            {
                GUILoggerHome.find()
                        .exception("Could not find ProductClass for classKey " +
                                   getContainedProduct().getProductKeysStruct().classKey, e);
            }
        }
        return productClass;
    }

    public String getProductRenderString()
    {
        return getContainedProduct().toString();
    }

    /**
     * Returns a String to uniquely identify this RecapLastSaleV4ProductContainer
     */
    public String getIdentifierString()
    {
        if(identifierString == null)
        {
            identifierString = getProductKey() + getExchange();
        }
        return identifierString;
    }

    public String getExchange()
    {
        return this.exchange;
    }

    public LastSaleV4 getLastSaleV4()
    {
        return this.lastSale;
    }

    public int getProductKey()
    {
        return this.productKey;
    }

    public RecapV4 getRecapV4()
    {
        return this.recap;
    }

    public void setLastSaleV4(LastSaleV4 lastSale)
    {
        if(!lastSale.getExchange().equals(getExchange()) ||
           lastSale.getProductKey() != getProductKey())
        {
            throw new IllegalArgumentException(
                    "LastSaleV4 ('" + lastSale.getExchange() + "'," + lastSale.getProductKey() + ") must be for the Exchange '" +
                    getExchange() + "' and productKey " + getProductKey());
        }
        if(this.lastSale != null)
        {
            this.lastSale.setLastSaleStructV4(lastSale.getLastSaleStructV4());
        }
        else
        {
            this.lastSale = lastSale;
        }
    }

    public void setRecapV4(RecapV4 recap)
    {
        if(!recap.getExchange().equals(getExchange()) ||
           recap.getProductKey() != getProductKey())
        {
            throw new IllegalArgumentException(
                    "RecapV4 ('" + recap.getExchange() + "'," + recap.getProductKey() + ") must be for the Exchange '" +
                    getExchange() + "' and productKey " + getProductKey());
        }
        if(this.recap != null)
        {
            this.recap.setRecapStructV4(recap.getRecapStructV4());
        }
        else
        {
            this.recap = recap;
        }
    }

    public int hashCode()
    {
        return getIdentifierString().hashCode();
    }

    public boolean equals(Object obj)
    {
        boolean retVal = false;
        if(obj != null)
        {
            RecapLastSaleV4ProductContainer other = (RecapLastSaleV4ProductContainer) obj;
            if(other.getIdentifierString().equals(getIdentifierString()))
            {
                retVal = true;
            }
        }
        return retVal;
    }
}
