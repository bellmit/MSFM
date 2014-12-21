//
// -----------------------------------------------------------------------------------
// Source file: SessionReportingClassImpl.java
//
// PACKAGE: com.cboe.presentation.product
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.idl.cmiProduct.ReportingClassStruct;

import com.cboe.interfaces.presentation.product.SessionReportingClass;
import com.cboe.interfaces.presentation.product.SessionProductClass;


/*
 * Defines a wrapper impl for a ReportingClassStruct with session information from the containing SessionProductClass
 */
public class SessionReportingClassImpl extends ReportingClassImpl implements SessionReportingClass
{
    protected SessionProductClass containingProductClass;
    private int cachedHashCode = -1;
    /**
     * Constructor
     * @param ReportingClassStruct to represent
     * @param SessionProductClass that contains this ReportingClassStruct to obtain session info from
     */
    protected SessionReportingClassImpl(ReportingClassStruct reportingClassStruct,
                                        SessionProductClass containingProductClass)
    {
        super(reportingClassStruct);

        if(containingProductClass == null)
        {
            throw new IllegalArgumentException("SessionProductClass can not be NULL.");
        }
        this.containingProductClass = containingProductClass;
        super.setContainingProductClass(containingProductClass);
    }

    /**
     * Returns a hash code for this SessionReportingClass
     */
    public int hashCode()
    {
        if(cachedHashCode == -1)
        {
            cachedHashCode = ProductHelper.getHashCode(getTradingSessionName(), super.hashCode());
        }
        return cachedHashCode;
    }

    /**
     * If <code>obj</code> is equal according to my super class, then if it is an
     * instance of SessionReportingClass and has the same containing SessionProductClass true is returned,
     * false otherwise.
     * @param obj to compare
     * @return true if equal, false if not.
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = false;

        if(super.equals(obj))
        {
            if(obj instanceof SessionReportingClass)
            {
                isEqual = getSessionProductClass().equals(((SessionReportingClass)obj).getSessionProductClass());
            }
        }

        return isEqual;
    }

    /**
     * Gets the SessionProductClass that contains this reporting class
     * @return SessionProductClass that represents this reporting class
     */
    public SessionProductClass getSessionProductClass()
    {
        return containingProductClass;
    }

    /**
     * Gets the session name for the SessionProductClass that contains this ReportingClass
     */
    public String getTradingSessionName()
    {
        return getSessionProductClass().getTradingSessionName();
    }
}
