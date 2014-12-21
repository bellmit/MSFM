package com.cboe.interfaces.domain.user;

// Source file: d:/sources/com/cboe/interfaces/domain/user/User.java

import com.cboe.idl.cmiQuote.UserQuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiQuote.QuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiUser.AccountStruct;
import com.cboe.idl.user.SessionProfileUserDefinitionStruct;

import java.util.Vector;
import com.cboe.exceptions.*;

/**
 * A person who uses the trading system.  The user may be either a trader or an adminstrative user.
 *
 * @author John Wickberg
 */
public interface UserIdentifier
{
    /**
     * Returns true if this userid is activef.
     *
     * @return user's full name
     */
    public boolean isActive();

	/**
	 * Returns user's login name.
	 *
	 * @return login name of user
	 */
	public String getUserId();

    /**
     * Returns the key of this userid.
     *
     * @return user's key
     */
    public int getUserIdKey();
    
    /**
     * Returns the key of the underyling UserBase.
     *
     * @return user's key
     */
    public int getAcronymUserKey();
}
