//
// -----------------------------------------------------------------------------------
// Source file: SessionProductClassDefaultImpl.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.idl.cmiConstants.ClassStates;
import com.cboe.idl.cmiSession.SessionClassStruct;

import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.presentation.product.AllSessionProductClass;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.SessionReportingClass;
import com.cboe.interfaces.presentation.tradingSession.DefaultTradingSession;

import com.cboe.presentation.common.formatters.CommonFormatFactory;

import com.cboe.domain.util.SessionKeyContainer;

/**
 * SessionProductClass implementation for a Default SessionProductClass for the OPTION type.
 */
class SessionProductClassDefaultImpl extends ProductClassDefaultImpl implements AllSessionProductClass
{
    private SessionKeyWrapper sessionKeyWrapper;
    private String sessionName;

    protected SessionProductClassDefaultImpl()
    {
        this(DefaultTradingSession.DEFAULT);
    }

    protected SessionProductClassDefaultImpl(String sessionName)
    {
        super();
        setTradingSessionName(sessionName);
    }

    /**
     * Returns a hash code for this Product
     */
    public int hashCode()
    {
        return ProductHelper.getHashCode(getTradingSessionName(), getClassKey());
    }

    public boolean isDefaultSession()
    {
        return getTradingSessionName().equals(DefaultTradingSession.DEFAULT);
    }

    /**
     * Get the state for this SessionProductClass.
     * @return Returns ClassStates.NOT_IMPLEMENTED
     */
    public short getState()
    {
        return ClassStates.NOT_IMPLEMENTED;
    }

    public void setTradingSessionName(String sessionName)
    {
        if(sessionName == null || sessionName.length() == 0)
        {
            throw new IllegalArgumentException("Invalid sessionName. May not be null or empty string.");
        }
        this.sessionName = sessionName;
    }

    public String getTradingSessionName()
    {
        return sessionName;
    }

    /**
     * Get the class state transaction sequence number for this SessionProductClass.
     * @return zero is returned
     */
    public int getClassStateTransactionSequenceNumber()
    {
        return 0;
    }

    /**
     * Does nothing for this implementation.
     */
    public void setState(short state)
    {}

    /**
     * Does nothing for this implementation.
     */
    public void setClassStateTransactionSequenceNumber(int sequenceNumber)
    {}

    /**
     * Get the SessionClassStruct that this SessionProductClass represents.
     * @return Null is returned
     * @deprecated
     */
    public SessionClassStruct getSessionClassStruct()
    {
        return null;
    }

    public void updateProductClass(ProductClass newProductClass)
    {}

    /**
     * Gets all the reporting classes for this session product class. Fixed to return a zero length array.
     * @return a zero length array of reporting classes
     */
    public SessionReportingClass[] getSessionReportingClasses()
    {
        return new SessionReportingClass[0];
    }

    /**
     * Clones this object by returning another instance of this class.
     */
    public Object clone() throws CloneNotSupportedException
    {
        return new SessionProductClassDefaultImpl(getTradingSessionName());
    }

    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);

        if( isEqual )
        {
            if( obj instanceof SessionProductClassDefaultImpl )
            {
                isEqual = getTradingSessionName().equals(
                        (( SessionProductClassDefaultImpl )obj).getTradingSessionName());
            }
            else
            {
                isEqual = false;
            }
        }
        return isEqual;
    }

    /**
     * Gets the underlying session name
     * @return empty string
     */
    public String getUnderlyingSessionName()
    {
        return "";
    }

    public SessionKeyWrapper getSessionKeyWrapper()
    {
        if (sessionKeyWrapper == null)
        {
            sessionKeyWrapper = new SessionKeyContainer(getTradingSessionName(), getClassKey());
        }
        return sessionKeyWrapper;
    }

    public String toString(String classFormat)
    {
        return CommonFormatFactory.getProductClassFormatStrategy().format(this, classFormat);
    }
}
