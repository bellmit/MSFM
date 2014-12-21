//
// -----------------------------------------------------------------------------------
// Source file: AbstractReportingClassImpl.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;

import com.cboe.interfaces.presentation.common.formatters.ReportingClassFormatStrategy;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.ReportingClass;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;


/**
 * Defines a wrapper impl for a ReportingClassStruct
 */
public abstract class AbstractReportingClassImpl extends AbstractBusinessModel implements ReportingClass
{
    protected ProductClass containingProductClass;
    protected ReportingClassFormatStrategy formatter = null;
    protected String cachedToString = null;

    /**
     * If <code>obj</code> is an instance of ReportingClass and has the same
     * class key true is returned, false otherwise.
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
        else if(obj instanceof ReportingClass)
        {
            isEqual = getClassKey().equals(((ReportingClass)obj).getClassKey());
        }
        else
        {
            isEqual = false;
        }

        return isEqual;
    }

    /**
     * Returns a hash code for this ReportingClass
     */
    public int hashCode()
    {
        return getClassKey();
    }

    /**
     * Returns a String representation of this ReportingClass
     */
    public String toString()
    {
        if(cachedToString == null)
        {
            cachedToString = formatter.format(this, formatter.CLASS_TYPE_NAME);
        }
        return cachedToString;
    }

    /**
     * Gets the ProductClass for this reporting class
     * @return ProductClass that represents this reporting class
     */
    public ProductClass getProductClass()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if(containingProductClass == null)
        {
            containingProductClass = APIHome.findProductQueryAPI().getProductClassByKey(getProductClassKey().intValue());
        }
        return containingProductClass;
    }

    /**
     * Clones this product by returning another instance that represents a
     * ProductStruct that was also cloned.
     */
    public Object clone() throws CloneNotSupportedException
    {
        // shouldn't be used anywhere
        return null;
    }

    /**
     * Sets the containing ProductClass
     * @param productClass that contains this reporting class
     */
    protected void setContainingProductClass(ProductClass productClass)
    {
        containingProductClass = productClass;
    }
}
