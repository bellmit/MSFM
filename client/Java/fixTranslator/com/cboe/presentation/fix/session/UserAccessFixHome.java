/*
 * Created on Jul 14, 2004
 *
 */
package com.cboe.presentation.fix.session;

import com.cboe.idl.cmiV3.UserAccessV3;
import com.cboe.interfaces.application.UserAccessV3Home;

/**
 * Home for FIX user access
 * @author Don Mendelson
 *
 */
public class UserAccessFixHome {

	public final static String HOME_NAME = "UserAccessFixHome";

	// Singleton
	private UserAccessV3 userAccess;
	private com.cboe.idl.cmiV3.UserAccessV3 userAccessCorba;

	/**
	 * Creates a new UserAccessFixHome
	 */
	public UserAccessFixHome() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.cboe.interfaces.application.UserAccessV2Home#create()
	 */
	public UserAccessV3 create() {
        if (userAccess == null)
        {

        	UserAccessFixImpl bo = new UserAccessFixImpl();

            userAccess = bo;
        }
        return userAccess;

	}

	/* (non-Javadoc)
	 * @see com.cboe.interfaces.application.UserAccessV2Home#find()
	 */
	public UserAccessV3 find() {
		return create();
	}

	/* (non-Javadoc)
	 * @see com.cboe.interfaces.application.UserAccessV2Home#objectToString()
	 */
	public String objectToString() {
    return null;
    }

}
