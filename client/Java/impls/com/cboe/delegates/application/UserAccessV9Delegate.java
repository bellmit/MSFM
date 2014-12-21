/**
 * 
 */
package com.cboe.delegates.application;

import com.cboe.interfaces.application.UserAccessV9;

/**
 * @author Piyush Patel July 23, 2010
 *
 */
public class UserAccessV9Delegate extends com.cboe.idl.cmiV9.POA_UserAccessV9_tie
{
    public UserAccessV9Delegate(UserAccessV9 delegate)
    {
        super(delegate);
    }
}