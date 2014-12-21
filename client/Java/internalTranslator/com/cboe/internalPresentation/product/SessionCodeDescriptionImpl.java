package com.cboe.internalPresentation.product;

import com.cboe.interfaces.internalPresentation.product.SessionCodeDescription;
import com.cboe.idl.product.SessionCodeDescriptionStruct;

public class SessionCodeDescriptionImpl implements SessionCodeDescription
{
    private SessionCodeDescriptionStruct sessionCodeDescStruct;

    private SessionCodeDescriptionImpl()
    {
    }

    protected SessionCodeDescriptionImpl(SessionCodeDescriptionStruct struct)
    {
        this.sessionCodeDescStruct = struct;
    }

    public String getSessionCode()
    {
        return sessionCodeDescStruct.sessionCode;
    }
        
    public String getSessionCodeDescription()
    {
        return sessionCodeDescStruct.sessionCodeDescription;
    }

    public SessionCodeDescriptionStruct getSessionCodeDescriptionStruct()
    {
        return sessionCodeDescStruct;
    }

    private String strVal;
    public String toString()
    {
        if(strVal == null)
        {
            strVal = this.getSessionCodeDescription() + " (" + this.getSessionCode() + ")";
        }
//        return this.getSessionCodeDescription();
        return strVal;
    }
}

