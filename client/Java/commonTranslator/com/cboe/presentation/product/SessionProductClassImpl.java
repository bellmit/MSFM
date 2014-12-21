//
// -----------------------------------------------------------------------------------
// Source file: SessionProductClassImpl.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.idl.cmiSession.SessionClassStruct;

import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.product.SessionReportingClass;
import com.cboe.interfaces.presentation.product.ReportingClass;

import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.presentation.util.StringCache;

/**
 * SessionProductClass implementation for a SessionClassStruct from the API.
 */
class SessionProductClassImpl extends ProductClassImpl implements SessionProductClass
{
    private SessionKeyWrapper sessionKeyWrapper;
    private int cachedHash = -1;

    // SessionClassStruct fields
    private String sessionName;
    private String underlyingSessionName;
    private String[] eligibleSessions;
    private short classState;
    private int classStateTransactionSequenceNumber;

    /**
     *  Default constructor.
     */
    protected SessionProductClassImpl()
    {
        super();
    }

    /**
     * Constructor
     * @param sessionClassStruct to represent
     */
    protected SessionProductClassImpl(SessionClassStruct sessionClassStruct)
    {
        this();
        updateFromStruct(sessionClassStruct);
    }

    protected void updateFromStruct(SessionClassStruct struct)
    {
        updateFromStruct(struct.classStruct);

        sessionName = StringCache.get(struct.sessionName);
        underlyingSessionName = StringCache.get(struct.underlyingSessionName);

        eligibleSessions = new String[struct.eligibleSessions.length];
        for(int i=0; i<struct.eligibleSessions.length; i++)
        {
            eligibleSessions[i] = StringCache.get(struct.eligibleSessions[i]);
        }
        classState = struct.classState;
        classStateTransactionSequenceNumber = struct.classStateTransactionSequenceNumber;
    }

    /**
     * Returns a hash code for this Product
     */
    public int hashCode()
    {
        if(cachedHash == -1)
        {
            cachedHash = ProductHelper.getHashCode(getTradingSessionName(), getClassKey());
        }
        return cachedHash;
    }

    public String toString()
    {
        if( cachedToString == null )
        {
            cachedToString = formatter.format(this, formatter.CLASS_TYPE_SESSION_NAME);
        }
        return cachedToString;
    }

    public boolean isDefaultSession()
    {
        return false;
    }

    /**
     * Get the state for this SessionProductClass.
     * @return state from represented struct
     */
    public short getState()
    {
        return classState;
    }

    /**
     * Get the trading session name for this SessionProductClass.
     * @return trading session name from represented struct
     */
    public String getTradingSessionName()
    {
        return sessionName;
    }

    /**
     * Get the class state transaction sequence number for this SessionProductClass.
     * @return class state transaction sequence number from represented struct
     */
    public int getClassStateTransactionSequenceNumber()
    {
        return classStateTransactionSequenceNumber;
    }

    /**
     * Sets the state for this SessionProductClass.
     * @param state to set in the represented struct
     */
    public void setState(short state)
    {
        //should be some sort of parameter protection here and throw and exception
        //when not valid, but type representing constants is not implemented
        classState = state;
    }

    /**
     * Sets the class state transaction sequence number for this SessionProductClass.
     * @param sequenceNumber to set in the represented struct
     */
    public void setClassStateTransactionSequenceNumber(int sequenceNumber)
    {
        classStateTransactionSequenceNumber = sequenceNumber;
    }

    /**
     * Get the SessionClassStruct that this SessionProductClass represents.
     * @return SessionClassStruct
     * @deprecated
     */
    public SessionClassStruct getSessionClassStruct()
    {
        SessionClassStruct retVal = new SessionClassStruct();
        retVal.sessionName = getTradingSessionName();
        retVal.underlyingSessionName = getUnderlyingSessionName();

        String[] sessions = getEligibleSessions();
        retVal.eligibleSessions = new String[sessions.length];
        for (int i = 0; i < sessions.length; i++)
        {
            retVal.eligibleSessions[i] = StringCache.get(sessions[i]);
        }

        retVal.classState = getState();
        retVal.classStruct = getClassStruct();
        retVal.classStateTransactionSequenceNumber = getClassStateTransactionSequenceNumber();

        return retVal;
    }

    public String[] getEligibleSessions()
    {
        return eligibleSessions;
    }

    /**
     * update product class information.
     * @param newProductClass ProductClass
     */
    public void updateProductClass(ProductClass newProductClass)
    {
        updateFromStruct(newProductClass.getClassStruct());
    }

    /**
     * Gets all the reporting classes for this session product class
     * @return an array of reporting classes
     */
    public SessionReportingClass[] getSessionReportingClasses()
    {
        ReportingClass[] sessionlessRepClasses = getReportingClasses();
        SessionReportingClass[] reportingClasses = new SessionReportingClass[sessionlessRepClasses.length];

        for(int i = 0; i < sessionlessRepClasses.length; i++)
        {
            reportingClasses[i] = ReportingClassFactoryHome.find().create(sessionlessRepClasses[i].getClassStruct(), this);
        }

        return reportingClasses;
    }

    public SessionKeyWrapper getSessionKeyWrapper()
    {
        if (sessionKeyWrapper == null)
        {
            sessionKeyWrapper = new SessionKeyContainer(getTradingSessionName(), getClassKey());
        }
        return sessionKeyWrapper;
    }

    /**
     * Clones this class by returning another instance that represents a
     * SessionClassStruct that was also cloned.
     */
    public Object clone() throws CloneNotSupportedException
    {
        return new SessionProductClassImpl(getSessionClassStruct());
    }

    /**
     * If <code>obj</code> is equal according to my super class, then if it is an
     * instance of SessionProductClass and has the same session name true is returned, false otherwise.
     * @param obj to compare
     * @return true if equal, false if not.
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);

        if(isEqual)
        {
            if(obj instanceof SessionProductClass)
            {
                isEqual = getTradingSessionName().equals(((SessionProductClass)obj).getTradingSessionName());
            }
            else
            {
                isEqual = false;
            }
        }

        return isEqual;
    }

    /**
     * Get the underlying session name
     * @return underlying session name
     */
    public String getUnderlyingSessionName()
    {
        return underlyingSessionName;
    }

    public String toString(String classFormat)
    {
        return formatter.format(this, classFormat);
    }
}