package com.cboe.application.cas;

/**
 * Created by IntelliJ IDEA.
 * User: josephg
 * Date: Jan 23, 2009
 */

import com.cboe.application.shared.POANameHelper;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.delegates.application.UserAccessV6Delegate;
import com.cboe.idl.cmiV6.UserAccessV6Helper;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.UserAccessV6Home;
import com.cboe.interfaces.application.UserAccessV6;

public class UserAccessV6HomeImpl extends UserAccessBaseHomeImpl implements UserAccessV6Home {

	private UserAccessV6 userAccess;
	private com.cboe.idl.cmiV6.UserAccessV6 userAccessCorba;

	public UserAccessV6 find() {
		return create();
	}

	public UserAccessV6 create() {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating UserAccessV6Impl");
        }
        if (userAccess == null)
        {
            UserAccessV6Impl bo = new UserAccessV6Impl(sessionMode, heartbeatTimeout, cmiVersion);
            bo.create(String.valueOf(bo.hashCode()));
            addToContainer(bo);

            UserAccessV6Interceptor boi = null;
            try
            {
                boi = (UserAccessV6Interceptor) this.createInterceptor(bo);
            } catch (Exception ex)
            {
                Log.exception(this, "Failed to create UserAccessTMS", ex);
                return null;
            }

            userAccess = boi;
        }
        return userAccess;
	}

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
        UserAccessV6Delegate delegate = new UserAccessV6Delegate(userAccess);
        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                register_object(delegate, poaName);
        userAccessCorba = UserAccessV6Helper.narrow(obj);
	}

}
