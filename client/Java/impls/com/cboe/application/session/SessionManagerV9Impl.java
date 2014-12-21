package com.cboe.application.session;

import com.cboe.exceptions.AuthenticationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiV9.OrderEntry;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.SessionManagerV9;
import com.cboe.util.ExceptionBuilder;

public class SessionManagerV9Impl extends SessionManagerV8Impl implements SessionManagerV9 {

    public OrderEntry getOrderEntryV9() throws SystemException, CommunicationException, AuthorizationException, AuthenticationException, NotFoundException
    {
        try {
            if ( userOrderEntryCorba == null )
            {
                userOrderEntryCorba = initUserOrderEntry();
            }
            return userOrderEntryCorba;
        }
        catch (Exception e)
        {
            Log.exception(this, "Could not get order entry V9 ", e);
            throw ExceptionBuilder.systemException("Could not get order entry V9 " + e.toString(), 0);
        }
    }


}