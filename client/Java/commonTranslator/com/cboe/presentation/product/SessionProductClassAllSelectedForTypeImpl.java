//
// -----------------------------------------------------------------------------------
// Source file: SessionProductClassAllSelectedForTypeImpl.java
//
// PACKAGE: com.cboe.presentation.product
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.presentation.common.formatters.ProductTypes;

public class SessionProductClassAllSelectedForTypeImpl extends SessionProductClassAllSelectedImpl
{
    private short productType;
    private String formattedString;

    protected SessionProductClassAllSelectedForTypeImpl(String sessionName, short productType)
    {
        super(sessionName);
        this.productType = productType;
    }

    public short getProductType()
    {
        return productType;
    }

    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);

        if (isEqual)
        {
            if (obj instanceof SessionProductClassAllSelectedForTypeImpl)
            {
                isEqual = getProductType() == ((SessionProductClassAllSelectedImpl) obj).getProductType();
            }
            else
            {
                isEqual = false;
            }
        }
        return isEqual;
    }

    public String toString()
    {
        if (formattedString == null)
        {
            formattedString = ALL_CLASSES_SELECTED_TEXT + " (" + getTradingSessionName() + ") (" + ProductTypes.toString(getProductType()) + ")";
        }
        return formattedString;
    }
}
