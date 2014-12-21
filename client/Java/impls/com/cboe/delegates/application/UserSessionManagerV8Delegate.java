/**
 * 
 */
package com.cboe.delegates.application;

import com.cboe.interfaces.application.UserSessionManagerV8;

/**
 * @author Arun Ramachandran Nov 15, 2009
 *
 */
public class UserSessionManagerV8Delegate extends com.cboe.idl.cmiV8.POA_UserSessionManagerV8_tie
{
    public UserSessionManagerV8Delegate(UserSessionManagerV8 delegate)
    {
        super(delegate);
    }
}
