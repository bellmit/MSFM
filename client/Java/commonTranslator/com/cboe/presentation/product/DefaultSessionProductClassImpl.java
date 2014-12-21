//
// -----------------------------------------------------------------------------------
// Source file: DefaultSessionProductClassImpl.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.idl.cmiConstants.ClassStates;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;

import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.presentation.product.AllSessionProductClass;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.product.SessionReportingClass;
import com.cboe.interfaces.presentation.tradingSession.DefaultTradingSession;

import com.cboe.domain.util.SessionKeyContainer;

/**
 * Provides an implementation of SessionProductClass, but that wraps a session-less ClassStruct
 */
class DefaultSessionProductClassImpl extends ProductClassImpl implements AllSessionProductClass
{
    private SessionKeyWrapper sessionKeyWrapper;
    private int cachedHash;
    private String sessionName;

    protected DefaultSessionProductClassImpl(ClassStruct classStruct)
    {
        this(DefaultTradingSession.DEFAULT, classStruct);
    }

    protected DefaultSessionProductClassImpl(String sessionName, ClassStruct classStruct)
    {
        super(classStruct);
        setTradingSessionName(sessionName);
        cachedHash = -1;
    }

    public int hashCode()
    {
        if(cachedHash == -1)
        {
            cachedHash = ProductHelper.getHashCode(getTradingSessionName(), getClassKey());
        }
        return cachedHash;
    }

    public Object clone() throws CloneNotSupportedException
    {
        return new DefaultSessionProductClassImpl(sessionName, getClassStruct());
    }

    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);

        if( isEqual )
        {
            if( obj instanceof SessionProductClass )
            {
                isEqual = getTradingSessionName().equals((( SessionProductClass ) obj).getTradingSessionName());
            }
            else
            {
                isEqual = false;
            }
        }

        return isEqual;
    }

    public boolean isDefaultSession()
    {
        return getTradingSessionName().equals(DefaultTradingSession.DEFAULT);
    }

    public short getState()
    {
        return ClassStates.NOT_IMPLEMENTED;
    }

    public void setTradingSessionName(String sessionName)
    {
        if( sessionName == null || sessionName.length() == 0 )
        {
            throw new IllegalArgumentException("Invalid sessionName. May not be null or empty string.");
        }
        this.sessionName = sessionName;
    }

    public String getTradingSessionName()
    {
        return sessionName;
    }

    public int getClassStateTransactionSequenceNumber()
    {
        return 0;
    }

    public void setState(short state)
    {}

    public void setClassStateTransactionSequenceNumber(int sequenceNumber)
    {}

    public SessionClassStruct getSessionClassStruct()
    {
        SessionClassStruct newStruct = new SessionClassStruct();
        newStruct.classState = getState();
        newStruct.classStateTransactionSequenceNumber = getClassStateTransactionSequenceNumber();
        newStruct.classStruct = getClassStruct();
        newStruct.sessionName = getTradingSessionName();
        newStruct.underlyingSessionName = getUnderlyingSessionName();
        return newStruct;
    }

    public void updateProductClass(ProductClass newProductClass)
    {}

    public SessionReportingClass[] getSessionReportingClasses()
    {
        return new SessionReportingClass[0];
    }

    public SessionKeyWrapper getSessionKeyWrapper()
    {
        if( sessionKeyWrapper == null )
        {
            sessionKeyWrapper = new SessionKeyContainer(getTradingSessionName(), getClassKey());
        }
        return sessionKeyWrapper;
    }

    public String getUnderlyingSessionName()
    {
        return "";
    }

    public String toString(String classFormat)
    {
        return formatter.format(this, classFormat);
    }
}