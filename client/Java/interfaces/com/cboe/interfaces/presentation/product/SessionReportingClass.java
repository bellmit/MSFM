//
// -----------------------------------------------------------------------------------
// Source file: SessionReportingClass.java
//
// PACKAGE: com.cboe.interfaces.presentation.product
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.product;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;

/*
 * Defines a contract for a session based reporting class
 */
public interface SessionReportingClass extends ReportingClass
{
    /**
     * Gets the session name for the SessionProductClass that contains this ReportingClass
     */
    public String getTradingSessionName();

    /**
     * Gets the SessionProductClass that contains this reporting class
     * @return SessionProductClass that represents this reporting class
     */
    public SessionProductClass getSessionProductClass();
}
