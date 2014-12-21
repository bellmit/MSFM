//
// -----------------------------------------------------------------------------------
// Source file: AbstractProductClass.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.ReportingClass;

import com.cboe.presentation.common.businessModels.AbstractBusinessModel;

/**
 * Abstract implementation of ProductClass.
 */
abstract class AbstractProductClass extends AbstractBusinessModel implements ProductClass
{
    /**
     *  Default constructor.
     */
    protected AbstractProductClass()
    {
        super();
    }

    /**
     * Gets all the reporting classes for this product class. Fixed to return a zero length array.
     * @return a zero length array of reporting classes
     */
    public ReportingClass[] getReportingClasses()
    {
        return new ReportingClass[0];
    }

    /**
     * Returns a hash code for this Product
     */
    public int hashCode()
    {
        return getClassKey();
    }

    /**
     * Determines if this ProductClass is invalid, either it has been marked inactive or has been removed from the
     * system, but some data structures still reference the classkey
     */
    public boolean isValid()
    {
        return true;
    }
}