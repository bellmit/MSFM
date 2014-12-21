package com.cboe.application.session;

import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.delegates.application.TradingClassStatusQueryServiceDelegate;
import com.cboe.exceptions.AuthenticationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiV8.TradingClassStatusQueryHelper;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.SessionManagerV8;
import com.cboe.interfaces.application.TradingClassStatusQueryService;
import com.cboe.interfaces.application.TradingClassStatusQueryServiceHome;
import com.cboe.util.ExceptionBuilder;

public class SessionManagerV8Impl extends SessionManagerV7Impl implements SessionManagerV8 {
	
	protected com.cboe.idl.cmiV8.TradingClassStatusQuery userTradingClassStatusQuery = null;
    protected synchronized void initialize(SessionProfileUserStructV2 validUser, String sessionId, int sessionKey,
                                           boolean ifLazyInitialization, CMIUserSessionAdmin clientListener, short sessionType,
                                           boolean gmdTextMessaging, boolean addUserInterest)
            throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {
        super.initialize(validUser, sessionId, sessionKey, ifLazyInitialization, clientListener, sessionType, gmdTextMessaging, addUserInterest);
    }

    protected void unregisterRemoteObjects()
    {
        String us = this.toString();
        StringBuilder unregister = new StringBuilder(us.length()+30);
        unregister.append("Unregister remote objects for session:").append(us);
        Log.information(this, unregister.toString());
        try
        {
             super.unregisterRemoteObjects();
        }
        catch (Exception e)
        {
            Log.exception(this, "session : " + this, e);
        }
    }


	public com.cboe.idl.cmiV8.TradingClassStatusQuery getTradingClassStatusQuery()
			throws SystemException, CommunicationException,
			AuthorizationException, AuthenticationException, NotFoundException {
		try {
            if (userTradingClassStatusQuery == null )
            {
            	initUserTradingClassStatusQuery();
            }
            return userTradingClassStatusQuery;
        }
        catch (Exception e)
        {
            Log.exception(this, "Could not get TradingClassStatusQuery ", e);
            throw ExceptionBuilder.systemException("Could not get TradingClassStatusQuery " + e.toString(), 0);
        }
	}

	private void initUserTradingClassStatusQuery()throws SystemException {
		try
        {
            TradingClassStatusQueryServiceHome home = ServicesHelper.getTradingClassStatusQueryServiceHome();
            // get POA name from HOME XML definition
            String poaName = getPOA((BOHome) home);
            // create with session manager
            TradingClassStatusQueryService ftm = home.create(this);
            // create servant
            TradingClassStatusQueryServiceDelegate delegate = new TradingClassStatusQueryServiceDelegate(ftm);
            // connect the servant to POA and activate it as a CORBA object
            org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(delegate, poaName);
            userTradingClassStatusQuery = TradingClassStatusQueryHelper.narrow(obj);
        }
        catch (Exception e)
        {
            throw ExceptionBuilder.systemException("Could not bind TradingClassQueryService", 1);
        }
	}
}
