package com.cboe.interfaces.presentation.preferences;

import org.omg.CORBA.UserException;

/**
 * A service for storing and retrieving preferences to and from a data store.
 * 
 * @author morrow
 */
public interface PreferenceService
{
	/**
	 * Gets the preferences.
	 * 
	 * @return a preference collection
	 * @throws UserException if the retrieval fails
	 */
	PreferenceCollection getPreferences(String section) throws UserException;

	/**
	 * Sets the preferences.
	 * 
	 * @param preferences
	 *            to be stored
	 * @throws UserException if the store fails
	 */
	void setPreferences(PreferenceCollection preferences, String section) throws UserException;
}
