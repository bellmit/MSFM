package com.cboe.interfaces.domain.user;

// Source file: d:/sources/com/cboe/interfaces/domain/user/PreferenceHome.java

import com.cboe.idl.cmiUser.*;
import com.cboe.exceptions.*;

/**
 * A manager of <code>Preference</code> instances.
 *
 * @author John Wickberg
 */
public interface PreferenceHome
{
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "PreferenceHome";
/**
 * Creates a system default preference.
 *
 * @param name name of system preference
 * @param value value of system preference
 * @return created preference
 * @exception AlreadyExistsException if validation checks fail
 */
public Preference createSystemPreference(String name, String value) throws AlreadyExistsException;
/**
 * Creates a system preference for a particular user.
 *
 * @param userKey user system preference is defined for
 * @param name name of system preference
 * @param value value of system preference
 * @return created preference
 * @exception AlreadyExistsException if validation checks fail
 */
public Preference createSystemPreferenceForUser(int userKey, String name, String value) throws AlreadyExistsException;
/**
 * Creates a user preference for a particular user.
 *
 * @param userKey user preference is defined for
 * @param name name of preference
 * @param value value of preference
 * @return created preference
 * @exception AlreadyExistsException if validation checks fail
 */
public Preference createUserPreference(int userKey, String name, String value) throws AlreadyExistsException;
/**
 * Finds user preferences that have a common prefix.  An empty prefix will find all preferences for the user.
 *
 * @param userKey owner of the preferences
 * @param prefix prefix of preference names
 * @return preferences that were found.  Result may be empty.
 */
public Preference[] findUserPreferencesByPrefix(int userKey, String prefix);
/**
 * Finds system preferences for a user that have a common prefix.  An empty prefix will find all preferences for the user.
 *
 * @param userKey owner of the preferences
 * @param prefix prefix of preference names
 * @return preferences that were found.  Result may be empty.
 */
public Preference[] findSystemPreferencesForUser(int userKey, String prefix);
/**
 * Finds system preferences used for default values.  An empty prefix will find all system preferences.
 *
 * @param prefix prefix of preference names
 * @return preferences that were found.  Result may be empty.
 */
public Preference[] findSystemPreferences(String prefix);
/**
 * Removes a preference.
 *
 * @param oldPreference preference to be removed
 */
public void removePreference(Preference oldPreference);
}

