/**
 * 
 */
package com.cboe.delegates.application;

import com.cboe.interfaces.application.UserSessionManagerV9;

/**
 * @author Piyush Patel June 23, 2010
 *
 */
public class UserSessionManagerV9Delegate extends com.cboe.idl.cmiV9.POA_UserSessionManagerV9_tie
{
    public UserSessionManagerV9Delegate(UserSessionManagerV9 delegate)
    {
        super(delegate);
    }
}