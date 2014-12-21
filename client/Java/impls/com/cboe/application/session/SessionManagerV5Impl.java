package com.cboe.application.session;

import com.cboe.application.tmsSession.SessionManagerTMSImpl;
import com.cboe.interfaces.application.*;
import com.cboe.idl.cmiV5.OrderEntry;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ExceptionBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: beniwalv
 * Date: Nov 1, 2007
 */
public class SessionManagerV5Impl extends SessionManagerTMSImpl implements 	SessionManagerV5  {


    protected synchronized void initialize(SessionProfileUserStructV2 validUser, String sessionId, int sessionKey,
                                           boolean ifLazyInitialization, CMIUserSessionAdmin clientListener, short sessionType,
                                           boolean gmdTextMessaging, boolean addUserInterest)
            throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {
        super.initialize(validUser, sessionId, sessionKey, ifLazyInitialization, clientListener, sessionType, gmdTextMessaging, addUserInterest);
    }


    public OrderEntry getOrderEntryV5() throws SystemException, CommunicationException, AuthorizationException {
        try {
            if ( userOrderEntryCorba == null )
            {
                userOrderEntryCorba = initUserOrderEntry();
            }
            return userOrderEntryCorba;
        }
        catch (Exception e)
        {
            Log.exception(this, "Could not get order entry V5 ", e);
            throw ExceptionBuilder.systemException("Could not get order entry V5 " + e.toString(), 0);
        }
    }

    // TODO
    public com.cboe.idl.cmiV5.UserTradingParameters getUserTradingParametersV5()
    	throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException
    {
        try {
            if ( userTradingParametersCorba == null )
            {
                userTradingParametersCorba = initUserTradingParameters();
            }
            return userTradingParametersCorba;
        }
        catch (Exception e)
        {
            Log.exception(this, "Could not get user trading parameters", e);
            throw ExceptionBuilder.systemException("Could not get user trading parameters " + e.toString(), 0);
        }
    }

    // TODO
    public com.cboe.idl.cmiV5.Quote getQuoteV5()
    	throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException
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
            Log.exception(this, "Could not get quote", e);
            throw ExceptionBuilder.systemException("Could not get quote " + e.toString(), 0);
        }
    }

}
