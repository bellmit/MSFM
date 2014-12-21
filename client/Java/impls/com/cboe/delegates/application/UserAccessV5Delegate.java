package com.cboe.delegates.application;

import com.cboe.interfaces.application.UserAccessV5;

/**
 * Created by IntelliJ IDEA.
 * User: beniwalv
 * Date: Oct 26, 2007
 */
public class UserAccessV5Delegate extends com.cboe.idl.cmiV5.POA_UserAccessV5_tie {
    public UserAccessV5Delegate(UserAccessV5 delegate) {
        super(delegate);
    }
}
