package com.cboe.application.tmsSession;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.delegates.application.TradeMaintenanceServiceDelegate;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.expressApplication.session.SessionManagerV4Impl;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiTradeMaintenanceService.TradeMaintenanceServiceHelper;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.ExternalTradeMaintenanceServiceHome;
import com.cboe.interfaces.application.SessionManagerTMS;
import com.cboe.interfaces.application.ExternalTradeMaintenanceService;
import com.cboe.util.ExceptionBuilder;


/**
 * Extends from SessionManagerV4Impl and implements SessionManagerTMS
 * @author zhuw
 *
 */
public class SessionManagerTMSImpl  
			extends SessionManagerV4Impl
			implements 	SessionManagerTMS {
	protected com.cboe.idl.cmiTradeMaintenanceService.TradeMaintenanceService tmsCorba; 
	
	protected synchronized void initialize(SessionProfileUserStructV2 validUser, String sessionId, int sessionKey,
                                           boolean ifLazyInitialization,
                                           CMIUserSessionAdmin clientListener, short sessionType,
                                           boolean gmdTextMessaging, boolean addUserInterest)
            throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {
        super.initialize(validUser, sessionId, sessionKey, ifLazyInitialization, clientListener, sessionType, gmdTextMessaging, addUserInterest);
        tmsCorba = initTradeMaintenceService();
    }
    
	public com.cboe.idl.cmiTradeMaintenanceService.TradeMaintenanceService getTradeMaintenanceService() 
			throws SystemException, CommunicationException, AuthorizationException {
		try
        {
            if(tmsCorba == null)
                tmsCorba = initTradeMaintenceService();
            return tmsCorba;
        }
        catch(Exception e)
        {
            throw ExceptionBuilder.systemException("Could not get trade maintenance service " + e.toString(), 0);
        }
	}
	
	private com.cboe.idl.cmiTradeMaintenanceService.TradeMaintenanceService initTradeMaintenceService() 
			throws SystemException {
        try
        {
        	ExternalTradeMaintenanceServiceHome home = ServicesHelper.getExternalTradeMaintenanceServiceHome();
        	// get POA name from HOME XML definition
        	String poaName = getPOA((BOHome) home);
        	// create with session manager
            ExternalTradeMaintenanceService tms = home.create(this);
            // create servant 
            TradeMaintenanceServiceDelegate delegate = new TradeMaintenanceServiceDelegate(tms);
            // connect the servant to POA and activate it as a CORBA object
            org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(delegate, poaName);
            tmsCorba = TradeMaintenanceServiceHelper.narrow(obj);
            
            return tmsCorba;
        }
        catch(Exception poae)
        {
            throw ExceptionBuilder.systemException("Could not bind Trade Maintenance Service", 1);
        }
	}
	
    protected void unregisterRemoteObjects()
    {
        String us = this.toString();
        StringBuilder unregister = new StringBuilder(us.length()+38);
        unregister.append("Unregister remote objects for session:").append(us);
        Log.information(this, unregister.toString());
        try {
        	unregisterTradeMaintenance();
            super.unregisterRemoteObjects();
        } catch (Exception e)
        {
            Log.exception(this, "session : " + us, e);
        }
    }

    protected void unregisterTradeMaintenance()
    {
        try {
            if (tmsCorba != null) {
                RemoteConnectionFactory.find().unregister_object(tmsCorba);
                tmsCorba = null;
            }
        } catch( Exception e ) {
            Log.exception( this, e );
        }
    }

	
}
