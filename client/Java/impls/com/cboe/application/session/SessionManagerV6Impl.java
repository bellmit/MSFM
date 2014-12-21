package com.cboe.application.session;

import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.delegates.application.FloorTradeMaintenanceServiceDelegate;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiV6.FloorTradeMaintenanceServiceHelper;
import com.cboe.idl.cmiV6.OrderQuery;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.FloorTradeMaintenanceService;
import com.cboe.interfaces.application.FloorTradeMaintenanceServiceHome;
import com.cboe.interfaces.application.SessionManagerV6;
import com.cboe.util.ExceptionBuilder;


public class SessionManagerV6Impl extends SessionManagerV5Impl implements SessionManagerV6 {
	protected com.cboe.idl.cmiV6.FloorTradeMaintenanceService floorTradeMaintenanceService = null;

    protected synchronized void initialize(SessionProfileUserStructV2 validUser, String sessionId, int sessionKey,
                                           boolean ifLazyInitialization, CMIUserSessionAdmin clientListener, short sessionType,
                                           boolean gmdTextMessaging, boolean addUserInterest)
            throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {
        super.initialize(validUser, sessionId, sessionKey, ifLazyInitialization, clientListener, sessionType, gmdTextMessaging, addUserInterest);
        initFloorTradeMaintenanceService();
    }

    public OrderQuery getOrderQueryV6() throws SystemException, CommunicationException, AuthorizationException {
        try {
            if ( userOrderQueryCorba == null )
            {
                userOrderQueryCorba = initUserOrderQuery();
            }
            return userOrderQueryCorba;
        }
        catch (Exception e)
        {
            Log.exception(this, "Could not get order query V6 ", e);
            throw ExceptionBuilder.systemException("Could not get order query V6 " + e.toString(), 0);
        }
    }
    public com.cboe.idl.cmiV6.FloorTradeMaintenanceService getFloorTradeMaintenanceService () throws SystemException, CommunicationException, AuthorizationException {
        try {
            if ( floorTradeMaintenanceService == null )
            {
                initFloorTradeMaintenanceService();
            }
            return floorTradeMaintenanceService;
        }
        catch (Exception e)
        {
            Log.exception(this, "Could not get floorTradeMaintenanceService ", e);
            throw ExceptionBuilder.systemException("Could not get floorTradeMaintenanceService " + e.toString(), 0);
        }
    	
    }

    private void initFloorTradeMaintenanceService() throws SystemException
    {
        try
        {
            FloorTradeMaintenanceServiceHome home = ServicesHelper.getFloorTradeMaintenanceServiceHome();
            // get POA name from HOME XML definition
            String poaName = getPOA((BOHome) home);
            // create with session manager
            FloorTradeMaintenanceService ftm = home.create(this);
            // create servant
            FloorTradeMaintenanceServiceDelegate delegate = new FloorTradeMaintenanceServiceDelegate(ftm);
            // connect the servant to POA and activate it as a CORBA object
            org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(delegate, poaName);
            floorTradeMaintenanceService = FloorTradeMaintenanceServiceHelper.narrow(obj);
        }
        catch (Exception e)
        {
            throw ExceptionBuilder.systemException("Could not bind FloorTradeMaintenanceService", 1);
        }
    }

    protected void unregisterRemoteObjects()
    {
        String us = this.toString();
        StringBuilder unregister = new StringBuilder(us.length()+38);
        unregister.append("Unregister remote objects for session:").append(us);
        Log.information(this, unregister.toString());
        try {
            unregisterFloorTradeMaintenanceService();
            super.unregisterRemoteObjects();
        } catch (Exception e)
        {
            Log.exception(this, "session : " + this, e);
        }
    }

    protected void unregisterFloorTradeMaintenanceService()
    {
        try {
            if (floorTradeMaintenanceService != null) {
                RemoteConnectionFactory.find().unregister_object(floorTradeMaintenanceService);
                floorTradeMaintenanceService = null;
            }
        } catch( Exception e ) {
            Log.exception( this, e );
        }
    }
}