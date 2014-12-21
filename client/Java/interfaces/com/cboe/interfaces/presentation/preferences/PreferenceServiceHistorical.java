package com.cboe.interfaces.presentation.preferences;

import java.util.Calendar;

import org.omg.CORBA.UserException;

/**
 * A service for retrieving historical preferences.
 * 
 * @author morrow
 */
public interface PreferenceServiceHistorical extends PreferenceService
{

	/**
	 * Returns dates that preferences are available for.
	 * 
	 * @param section
	 *            Preference section
	 * @return dates of preferences
	 * @throws UserException
	 */
	Calendar[] getPreferencesDates(String section) throws UserException;

	/**
	 * Gets the preferences for the date specified. The date provided must be a date that has been
	 * returned by {@link #getPreferencesDates(String)}.
	 * 
	 * @param section
	 *            Preference section
	 * @param calendar
	 *            the date to retrieve preferences for
	 * @return preferences
	 * @throws UserException
	 */
	PreferenceCollection getPreferences(String section, Calendar date) throws UserException;
}
