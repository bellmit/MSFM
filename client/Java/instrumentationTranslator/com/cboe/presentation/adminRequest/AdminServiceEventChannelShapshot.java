//
// -----------------------------------------------------------------------------------
// Source file: AdminServiceEventChannelShapshot.java
//
// PACKAGE: com.cboe.presentation.adminRequest;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.adminRequest;

import com.cboe.interfaces.presentation.api.TimedOutException;

import com.cboe.presentation.api.AbstractSnapshot;
import com.cboe.presentation.api.InstrumentationTranslatorFactory;

public abstract class AdminServiceEventChannelShapshot extends AbstractSnapshot
{
    protected String category;
    protected String destination;
    protected int serviceTimeout;
    protected boolean adminServiceTimedOut;

    protected AdminServiceEventChannelShapshot(String destination, int serviceTimeout,
                                               int snapshotTimeout)
    {
        this();
        setServiceTimeout(serviceTimeout);
        setTimeout(snapshotTimeout);
        setDestination(destination);
    }

    protected AdminServiceEventChannelShapshot()
    {
        category = getClass().getName();
        adminServiceTimedOut = false;
        isChannelUpdated = false;
        initDefaultTimeout();
    }

    protected abstract void processTimeOut() throws TimedOutException;

    protected void setServiceTimeout(int timeout)
    {
        serviceTimeout = timeout;
    }

    /**
     * Sets the destination ORB for this command
     * @param orbName to send command to
     */
    public void setDestination(String orbName)
    {
        destination = orbName;
    }

    /**
     * Sets the amount to wait for the command response
     * @param millis to wait for timeout from command response
     */
    public void setTimeout(int millis)
    {
        if (millis > 0)
        {
            snapshotTimeout = millis;
        }
        else
        {
            // For AdminServiceCommandSnapshot default timeout equals to
            //  AdminService timeout (serviceTimeout) plus 1 second
            if ( serviceTimeout > 0)
            {
                snapshotTimeout = serviceTimeout + DEFAULT_TIME_OUT;
            }
            else
            {
                initDefaultTimeout();
            }
        }
    }

    protected void initDefaultTimeout()
    {
        int defaultServiceTimeout =
                InstrumentationTranslatorFactory.find().getAdminService().getDefaultTimeout();
        snapshotTimeout = defaultServiceTimeout + DEFAULT_TIME_OUT;
    }

    @SuppressWarnings({"ReturnOfNull"})
    protected String getCommunicationExceptionDetail()
    {
        return null;
    }

    public void timedOut()
    {
        adminServiceTimedOut = true;
    }

    protected boolean isTimedOut()
    {
        return (adminServiceTimedOut || super.isTimedOut());
    }

    protected String getTimeoutMessage()
    {
        return "Timed out while waiting for AdminService response";
    }

    protected String getExceptionMessage()
    {
        return "Exception while executing AdminService command";
    }
}
