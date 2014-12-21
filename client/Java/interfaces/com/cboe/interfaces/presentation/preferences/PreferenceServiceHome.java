package com.cboe.interfaces.presentation.preferences;

/**
 * A home for preference services.
 * 
 * @author morrow
 * 
 */
public interface PreferenceServiceHome
{
	/**
	 * Gets the <tt>PreferenceService</tt>
	 * 
	 * @return the preference service.
	 */
	PreferenceService getPreferenceService();

	/**
	 * Gets the <tt>PreferenceServiceHistorical</tt> used for restoring preferences from an earlier
	 * date.
	 * 
	 * @return the backup preference service.
	 */
	PreferenceServiceHistorical getBackupPreferenceService();
}
