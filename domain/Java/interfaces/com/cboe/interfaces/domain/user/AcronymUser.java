package com.cboe.interfaces.domain.user;

// Source file: d:/sources/com/cboe/interfaces/domain/user/User.java

import java.util.List;
import java.util.Vector;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.idl.cmiUser.AccountStruct;
import com.cboe.idl.user.SessionProfileUserDefinitionStruct;

/**
 * A person who uses the trading system.  The user may be either a trader or an adminstrative user.
 *
 * @author John Wickberg
 */
public interface AcronymUser
{
    public int getAcronymUserKey();
    
    /**
     * Gets the user type.
     */
    public short getUserType();

    /**
     * Get the user's role.
     */
    public char getRole();

	/**
	 * Gets the accounts assigned to this user.
	 *
	 * @return account structs describing the joint accounts for this user.
	 */
	public AccountStruct[] getAccounts();

	/**
	 * Gets user's assigned classes.
	 *
	 * @return keys of assigned classes
	 */
	public int[] getAssignedClasses();

	/**
	 * Returns the acronym for the given user (user id)
	 *
	 * @return user's acronym (user id)
	 */
	public String getAcronym();
    
    public String getExchangeAcronym();
}
