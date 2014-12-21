package com.cboe.delegates.application;

import com.cboe.interfaces.application.UserAccessV6;

/**
 * Created by IntelliJ IDEA.
 * User: josephg
 * Date: Jan 23, 2009
 */
public class UserAccessV6Delegate extends com.cboe.idl.cmiV6.POA_UserAccessV6_tie {
    public UserAccessV6Delegate(UserAccessV6 delegate) {
        super(delegate);
    }
}
