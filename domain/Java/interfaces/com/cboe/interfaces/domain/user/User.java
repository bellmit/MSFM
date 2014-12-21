package com.cboe.interfaces.domain.user;

import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiUser.SessionProfileUserStruct;
import com.cboe.idl.user.SessionProfileUserDefinitionStruct;

/**
 * A User is now a composite of a "UserBase" and a "UserIdentifier", separating
 * the concept of a physical user ("user") from a user login ("userid"), which
 * can now be a 1:n relationship (one user may have multiple logins).
 *
 * @author Steven Sinclair
 */
public interface User extends UserIdentifier, AcronymUser, QRMUser
{
    // (all other methods are inherited)
    
    public void setIsActive(boolean isActive);
}
