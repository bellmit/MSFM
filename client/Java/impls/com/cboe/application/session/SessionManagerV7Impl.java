package com.cboe.application.session;

import com.cboe.interfaces.application.SessionManagerV7;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.util.ExceptionBuilder;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.idl.cmiErrorCodes.AuthenticationCodes;

public class SessionManagerV7Impl extends SessionManagerV6Impl implements SessionManagerV7
{
    protected synchronized void initialize(SessionProfileUserStructV2 validUser, String sessionId, int sessionKey,
                                           boolean ifLazyInitialization, CMIUserSessionAdmin clientListener, short sessionType,
                                           boolean gmdTextMessaging, boolean addUserInterest)
            throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {
        super.initialize(validUser, sessionId, sessionKey, ifLazyInitialization, clientListener, sessionType, gmdTextMessaging, addUserInterest);
    }

    public com.cboe.idl.cmiV7.OrderEntry getOrderEntryV7()
            throws SystemException, CommunicationException, AuthorizationException
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
            Log.exception(this, "Could not get order entry V7 ", e);
            throw ExceptionBuilder.systemException("Could not get order entry V7 " + e.toString(), 0);
        }
    }

    public com.cboe.idl.cmiV7.Quote getQuoteV7()
            throws SystemException, CommunicationException, AuthorizationException
    {
        try {
            if ( userQuoteQueryCorba == null )
            {
                userQuoteQueryCorba = initUserQuoteQuery();
            }
            return userQuoteQueryCorba;
        }
        catch (Exception e)
        {
            Log.exception(this, "Could not get quote V7", e);
            throw ExceptionBuilder.systemException("Could not get quote V7" + e.toString(), 0);
        }

    }
}
