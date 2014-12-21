//
// -----------------------------------------------------------------------------------
// Source file: SessionProductClass.java
//
// PACKAGE: com.cboe.interfaces.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.product;

import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.interfaces.domain.SessionKeyWrapper;

public interface SessionProductClass extends ProductClass
{
    public short getState();
    public String getTradingSessionName();
    public int getClassStateTransactionSequenceNumber();
    public void setState(short state);
    public void setClassStateTransactionSequenceNumber(int sequenceNumber);
    public String getUnderlyingSessionName();
    public SessionKeyWrapper getSessionKeyWrapper();

    /**
     * Allows custom formating of product class
     */
    public String toString(String formatStrategy);

    /**
     * Determines if this trading session is the All Sessions
     */
    public boolean isDefaultSession();

    /**
     * Gets all the reporting classes for this session product class
     * @return an array of reporting classes
     */
    public SessionReportingClass[] getSessionReportingClasses();

    /**
     * @deprecated Use public getters to get struct contents always
     */
    public SessionClassStruct getSessionClassStruct();
    public void updateProductClass(ProductClass newProductClass);
}
