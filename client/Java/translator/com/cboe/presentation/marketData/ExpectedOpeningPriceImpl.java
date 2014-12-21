//
// -----------------------------------------------------------------------------------
// Source file: ExpectedOpeningPriceImpl.java
//
// PACKAGE: com.cboe.presentation.marketData
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.marketData;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct;

import com.cboe.interfaces.presentation.marketData.ExpectedOpeningPrice;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.domain.Price;

import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.api.APIHome;

import com.cboe.domain.util.StructBuilder;
import com.cboe.domain.util.ClientProductStructBuilder;

public class ExpectedOpeningPriceImpl implements ExpectedOpeningPrice
{
    private ExpectedOpeningPriceStruct struct;
    private Price expectedOpeningPrice;
    private SessionProduct product;

    public ExpectedOpeningPriceImpl()
    {
        this(new ExpectedOpeningPriceStruct(ClientProductStructBuilder.buildProductKeysStruct(), "", (short) 0,
                                            StructBuilder.buildPriceStruct(), 0, false));
    }

    public ExpectedOpeningPriceImpl(ExpectedOpeningPriceStruct struct)
    {
        if(struct == null)
        {
            throw new IllegalArgumentException("ExpectedOpeningPriceStruct cannot be null");
        }
        this.struct = struct;
    }

    /**
     * Two EOPs are considered equal if they are for the same Product.
     */
    public boolean equals(Object obj)
    {
        boolean retVal = false;
        if(obj instanceof ExpectedOpeningPriceImpl)
        {
            retVal = getProductKey() == ((ExpectedOpeningPriceImpl)obj).getProductKey();
        }
        return retVal;
    }

    /**
     * Returns the productKey for the contained Product.
     */
    public int hashCode()
    {
        return getProductKey();
    }

    /**
     * Updates the struct for this wrapper.  The new struct must be for the same
     * productKey as the original EOP struct for this wrapper.
     * @param struct
     */
    public void updateEOPStruct(ExpectedOpeningPriceStruct struct)
    {
        if(struct.productKeys.productKey != getProductKey())
        {
            throw new IllegalArgumentException("Error: New ExpectedOpeningPriceStruct is for productKey " + struct.productKeys
                    .productKey + ", but original struct for this wrapper was for productKey " + getProductKey());
        }
        this.struct = struct;
        this.expectedOpeningPrice = null;
    }

    public short getEOPType()
    {
        return struct.eopType;
    }

    public Price getExpectedOpeningPrice()
    {
        if(expectedOpeningPrice == null)
        {
            expectedOpeningPrice = DisplayPriceFactory.create(struct.expectedOpeningPrice);
        }
        return expectedOpeningPrice;
    }

    public ExpectedOpeningPriceStruct getExpectedOpeningPriceStruct()
    {
        return struct;
    }

    public int getImbalanceQuantity()
    {
        return struct.imbalanceQuantity;
    }

    public SessionProduct getProduct()
    {
        if(product == null)
        {
            try
            {
                product = APIHome.findProductQueryAPI().getProductByKeyForSession(struct.sessionName, getProductKey());
            }
            catch(UserException e)
            {
                GUILoggerHome.find().exception("Error trying to get Product for session='" + struct.sessionName +
                                               "' productKey=" + struct.productKeys.productKey, e);
            }
        }
        return product;
    }

    public int getProductKey()
    {
        return struct.productKeys.productKey;
    }

    public boolean isLegalMarket()
    {
        return struct.legalMarket;
    }
}
