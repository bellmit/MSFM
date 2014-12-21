/*
 * Created on Mar 30, 2005
 */
package com.cboe.domain.user;

import com.cboe.interfaces.domain.user.AcronymUser;

/**
 * Minimal number of methods required by classes within the com.cboe.domain.user
 * package that cannot be exposed via com.cboe.interfaces.domain.user.User
 * 
 * This is so that the UserCombinedImpl can implement this interface so that we
 * avoid unnecessary casting to AcronymUserImpl.
 * 
 * @author sinclair
 */
interface UserInternal extends AcronymUser
{
    Profile getDefaultProfile();
}
