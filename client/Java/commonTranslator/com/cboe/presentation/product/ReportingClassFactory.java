//
// -----------------------------------------------------------------------------------
// Source file: ReportingClassFactory.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.idl.cmiProduct.ReportingClassStruct;

import com.cboe.interfaces.presentation.product.ReportingClass;

/**
 * Factory builder for interfaces of ReportingClass
 */
public class ReportingClassFactory extends AbstractReportingClassFactory
{
    /**
     * Creates an instance of a ReportingClass from a ReportingClassStruct.
     * @param reportingClassStruct to wrap in instance of ReportingClass
     * @return ReportingClass to represent the ReportingClassStruct
     */
    public AbstractReportingClassImpl create(ReportingClassStruct reportingClassStruct)
    {
        if(reportingClassStruct == null)
        {
            throw new IllegalArgumentException("ReportingClassStruct can not be NULL.");
        }
        AbstractReportingClassImpl reportingClass;
        reportingClass = new ReportingClassImpl(reportingClassStruct);

        return reportingClass;
    }
}
