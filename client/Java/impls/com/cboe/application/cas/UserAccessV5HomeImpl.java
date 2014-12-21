package com.cboe.application.cas;

/**
 * Created by IntelliJ IDEA.
 * User: beniwalv
 * Date: Oct 24, 2007
 * Time: 3:32:36 PM
 * To change this template use File | Settings | File Templates.
 */

import com.cboe.application.shared.POANameHelper;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.delegates.application.UserAccessV5Delegate;
import com.cboe.idl.cmiV5.UserAccessV5Helper;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.UserAccessV5Home;
import com.cboe.interfaces.application.UserAccessV5;

public class UserAccessV5HomeImpl extends UserAccessBaseHomeImpl implements UserAccessV5Home {

	private UserAccessV5 userAccess;
	private com.cboe.idl.cmiV5.UserAccessV5 userAccessCorba;

	/*
	 * (non-Javadoc)
	 * @see com.cboe.interfaces.application.UserAccessTMSHome#find()
	 */
	public UserAccessV5 find() {
		return create();
	}

	/*
	 * (non-Javadoc)
	 * @see com.cboe.interfaces.application.UserAccessTMSHome#create()
	 */
	public UserAccessV5 create() {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating UserAccessV5Impl");
        }
        if (userAccess == null)
        {
            UserAccessV5Impl bo = new UserAccessV5Impl(sessionMode, heartbeatTimeout, cmiVersion);
            bo.create(String.valueOf(bo.hashCode()));
            //Every bo object must be added to the container.
            addToContainer(bo);

            //The addToContainer call MUST occur prior to creation of the interceptor.
            //todo - Figure out the generation of the interceptors
            UserAccessV5Interceptor boi = null;
            try
            {
            	// create BOInterceptor
                boi = (UserAccessV5Interceptor) this.createInterceptor(bo);
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
        UserAccessV5Delegate delegate = new UserAccessV5Delegate(userAccess);
        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                register_object(delegate, poaName);
        userAccessCorba = UserAccessV5Helper.narrow(obj);
	}

}
