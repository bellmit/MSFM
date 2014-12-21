//
// -----------------------------------------------------------------------------------
// Source file: AbstractReportingClassFactory.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.idl.cmiProduct.ReportingClassStruct;

import com.cboe.interfaces.presentation.product.ReportingClass;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.SessionReportingClass;
import com.cboe.interfaces.presentation.product.SessionProductClass;

/**
 * Factory builder for interfaces of ReportingClass
 */
public abstract class AbstractReportingClassFactory implements ReportingClassFactoryInterface
{

    /**
     * Creates an instance of a ReportingClass from a ReportingClassStruct.
     * @param reportingClassStruct to wrap in instance of ReportingClass
     * @param containingProductClass that contains this passed reportingClassStruct
     * @return ReportingClass to represent the ReportingClassStruct
     */
    public ReportingClass create(ReportingClassStruct reportingClassStruct, ProductClass containingProductClass)
    {
        AbstractReportingClassImpl reportingClass = (AbstractReportingClassImpl)create(reportingClassStruct);
        reportingClass.setContainingProductClass(containingProductClass);

        return reportingClass;
    }

    /**
     * Creates an instance of a SessionReportingClass from a ReportingClassStruct.
     * @param reportingClassStruct to wrap in instance of ReportingClass
     * @param containingProductClass that contains this passed reportingClassStruct
     * @return ReportingClass to represent the ReportingClassStruct
     */
    public SessionReportingClass create(ReportingClassStruct reportingClassStruct,
                                        SessionProductClass containingProductClass)
    {
        if(reportingClassStruct == null)
        {
            throw new IllegalArgumentException("ReportingClassStruct can not be NULL.");
        }
        if(containingProductClass == null)
        {
            throw new IllegalArgumentException("SessionProductClass can not be NULL.");
        }

        SessionReportingClass reportingClass = new SessionReportingClassImpl(reportingClassStruct, containingProductClass);

        return reportingClass;
    }
}
