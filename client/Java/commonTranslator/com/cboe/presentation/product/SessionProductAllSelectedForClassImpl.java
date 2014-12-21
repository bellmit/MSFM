//
// -----------------------------------------------------------------------------------
// Source file: SessionProductAllSelectedForClassImpl.java
//
// PACKAGE: com.cboe.presentation.product
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.presentation.common.formatters.ProductTypes;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.idl.cmiProduct.ProductKeysStruct;

public class SessionProductAllSelectedForClassImpl extends SessionProductAllSelectedImpl
{
    private static final String ALL_PRODUCTS_SELECTED_TEXT = "<All Products>";
    private SessionProductClass productClass;
    private String formattedString;
    private ProductKeysStruct keyStruct;

    protected SessionProductAllSelectedForClassImpl(SessionProductClass productClass)
    {
        super(productClass.getTradingSessionName());
        this.productClass = productClass;
    }

    public short getProductType()
    {
        return productClass.getProductType();
    }

    public boolean equals(Object obj)
    {
        boolean isEqual;
        // not starting with checking super.equals() because it will return true for ANY SessionProducts
        // that are both instances of ProductDefaultImpl (regardless of trading session, productType, etc.)
        if(obj instanceof SessionProductAllSelectedForClassImpl)
        {
            isEqual = productClass.equals(((SessionProductAllSelectedForClassImpl) obj).productClass);
        }
        else
        {
            isEqual = super.equals(obj);
        }
        return isEqual;
    }

    @Override
    public ProductKeysStruct getProductKeysStruct()
    {
        if(keyStruct == null)
        {
            keyStruct = new ProductKeysStruct(getProductKey(), productClass.getClassKey(), productClass.getProductType(), 0);
        }
        return keyStruct;
    }

    public String toString()
    {
        if (formattedString == null)
        {
            formattedString = ALL_PRODUCTS_SELECTED_TEXT + " ("+productClass.getClassSymbol()+") (" + getTradingSessionName() + ") (" + ProductTypes.toString(getProductType()) + ")";
        }
        return formattedString;
    }

}
