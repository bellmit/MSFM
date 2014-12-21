/**
 * @author Jing Chen
 */
package com.cboe.interfaces.application.inprocess;

import com.cboe.interfaces.application.inprocess.UserAccess;

public interface UserAccessHome {

	public final static String HOME_NAME = "UserAccessHome";

    public UserAccess find();

    public UserAccess create();
}
