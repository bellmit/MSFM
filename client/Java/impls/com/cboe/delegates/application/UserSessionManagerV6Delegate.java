package com.cboe.delegates.application;

import com.cboe.interfaces.application.UserSessionManagerV6;

/**
 * Created by IntelliJ IDEA.
 * User: beniwalv
 * Date: Oct 26, 2007
 */
public class UserSessionManagerV6Delegate extends com.cboe.idl.cmiV6.POA_UserSessionManagerV6_tie {
    public UserSessionManagerV6Delegate(UserSessionManagerV6 delegate) {
        super(delegate);
    }
}