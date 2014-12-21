//
// -----------------------------------------------------------------------------------
// Source file: ReportingClassFactoryInterface.java
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
 * Factory builder for interfaces of ReportingClassFactory
 */
public interface ReportingClassFactoryInterface
{
    AbstractReportingClassImpl create(ReportingClassStruct reportingClassStruct);

    ReportingClass create(ReportingClassStruct reportingClassStruct, ProductClass containingProductClass);

    SessionReportingClass create(ReportingClassStruct reportingClassStruct,
                                 SessionProductClass containingProductClass);
}
