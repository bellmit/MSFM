package com.cboe.delegates.application;

import com.cboe.interfaces.application.UserSessionManagerV5;

/**
 * Created by IntelliJ IDEA.
 * User: beniwalv
 * Date: Oct 26, 2007
 */
public class UserSessionManagerV5Delegate extends com.cboe.idl.cmiV5.POA_UserSessionManagerV5_tie {
    public UserSessionManagerV5Delegate(UserSessionManagerV5 delegate) {
        super(delegate);
    }
}