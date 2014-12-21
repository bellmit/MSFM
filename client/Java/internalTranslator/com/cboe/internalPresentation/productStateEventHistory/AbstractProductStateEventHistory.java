package com.cboe.internalPresentation.productStateEventHistory;
// -----------------------------------------------------------------------------------
// Source file: AbstractProductStateEventHistory
//
// PACKAGE: com.cboe.internalPresentation.productStateEventHistory
// 
// Created: Mar 13, 2006 2:23:59 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
import com.cboe.interfaces.domain.dateTime.DateTime;

import com.cboe.presentation.common.dateTime.DateTimeImpl;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;

public abstract class AbstractProductStateEventHistory extends AbstractBusinessModel implements ProductStateEventHistory
{
    protected PSEventHistoryStatus status;
    protected DateTime dateTime;
    protected String description;

    private PSEventHistoryError error;
    private String toStringValue;

    protected AbstractProductStateEventHistory()
    {
        description = "";
        status = PSEventHistoryStatus.FIRED;
        this.dateTime = new DateTimeImpl(System.currentTimeMillis());
    }

    protected AbstractProductStateEventHistory(DateTime dateTime, PSEventHistoryStatus status, String description)
    {
        this.status = status;
        this.dateTime = dateTime;
        this.description = description;
    }

    public PSEventHistoryStatus getStatus()
    {
        return status;
    }

    public DateTime getDateTime()
    {
        return dateTime;
    }

    public String getDesription()
    {
        return description;
    }

    public PSEventHistoryError getError()
    {
        return error;
    }

    public void setError(PSEventHistoryError error)
    {
        this.error = error;
    }

    public Object getKey()
    {
        return this;
    }

    public boolean equals(Object obj)
    {
        boolean isEqual = false;

        if (this == obj)
        {
            isEqual = true;
        }
        else if (obj instanceof ProductStateEventHistory)
        {
            ProductStateEventHistory evt = (ProductStateEventHistory) obj;

            if (dateTime.equals(evt.getDateTime()) && status.equals(evt.getStatus()))
            {
                isEqual = true;
            }
        }

        return isEqual;
    }

    public int hashCode()
    {
        return dateTime.hashCode() + status.hashCode();
    }

    public String toString()
    {
        if (toStringValue == null)
        {
            StringBuilder buffer = new StringBuilder(100);

            buffer.append("Time: ");
            buffer.append(dateTime.toString());
            buffer.append(" Status: ");
            buffer.append(status.toString());
            buffer.append(" Description: ");
            buffer.append(description);

            toStringValue = buffer.toString();
        }

        return toStringValue;
    }
}
