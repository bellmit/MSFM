/**
 * 
 */
package com.cboe.delegates.application;

import com.cboe.interfaces.application.UserAccessV8;

/**
 * @author Arun Ramachandran Nov 15, 2009
 *
 */
public class UserAccessV8Delegate extends com.cboe.idl.cmiV8.POA_UserAccessV8_tie
{
    public UserAccessV8Delegate(UserAccessV8 delegate)
    {
        super(delegate);
    }
}
