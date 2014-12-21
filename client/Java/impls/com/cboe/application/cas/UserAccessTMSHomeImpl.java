package com.cboe.application.cas;

import com.cboe.application.shared.POANameHelper;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.delegates.application.UserAccessTMSDelegate;
import com.cboe.idl.cmiTradeMaintenanceService.UserAccessTMSHelper;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.UserAccessTMS;
import com.cboe.interfaces.application.UserAccessTMSHome;


public class UserAccessTMSHomeImpl 
		extends UserAccessBaseHomeImpl
								   implements UserAccessTMSHome {
	
	private UserAccessTMS userAccess;
	private com.cboe.idl.cmiTradeMaintenanceService.UserAccessTMS userAccessCorba;
	
	/*
	 * (non-Javadoc)
	 * @see com.cboe.interfaces.application.UserAccessTMSHome#find()
	 */
	public UserAccessTMS find() {
		return create();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.cboe.interfaces.application.UserAccessTMSHome#create()
	 */
	public UserAccessTMS create() {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating UserAccessTMSImpl");
        }
        if (userAccess == null)
        {
            UserAccessTMSImpl bo = new UserAccessTMSImpl(sessionMode, heartbeatTimeout, cmiVersion);
            bo.create(String.valueOf(bo.hashCode()));
            //Every bo object must be added to the container.
            addToContainer(bo);

            //The addToContainer call MUST occur prior to creation of the interceptor.
            UserAccessTMSInterceptor boi = null;
            try
            {	
            	// create BOInterceptor
                boi = (UserAccessTMSInterceptor) this.createInterceptor(bo);
            } catch (Exception ex)
            {
                Log.exception(this, "Failed to create UserAccessTMS", ex);
                return null;
            }

            //Every BOObject create MUST have a name...if the object is to be a managed object.
            userAccess = boi;
        }
        return userAccess;
	}

	/*
	 * (non-Javadoc)
	 * @see com.cboe.interfaces.application.UserAccessTMSHome#objectToString()
	 */
	public String objectToString() {
		return super.objectToString(userAccessCorba);
	}

	@Override
	public void clientInitialize() throws Exception {
        if (Log.isDebugOn())
        {
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }
        super.clientInitialize();
        create();
	}

	@Override
	public void clientStart() throws Exception {
		userAccess = find();
        String poaName = POANameHelper.getPOAName(this);
        UserAccessTMSDelegate delegate = new UserAccessTMSDelegate(userAccess);
        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                register_object(delegate, poaName);
        userAccessCorba = UserAccessTMSHelper.narrow(obj);
	}

}
