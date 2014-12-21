package com.cboe.internalPresentation.product;

import com.cboe.idl.product.SessionCodeDescriptionStruct;
import com.cboe.interfaces.internalPresentation.product.SessionCodeDescription;

public class SessionCodeDescriptionFactory
{
    private SessionCodeDescriptionFactory()
    {
    }

    public static SessionCodeDescription create(SessionCodeDescriptionStruct struct)
    {
        if(struct == null)
        {
            throw new IllegalArgumentException("SessionCodeDescriptionStruct cannot be null");
        }

        return new SessionCodeDescriptionImpl(struct);
    }
}

