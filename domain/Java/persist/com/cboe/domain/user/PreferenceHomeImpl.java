package com.cboe.domain.user;

import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.ObjectQuery;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import com.cboe.interfaces.domain.user.PreferenceHome;
import com.cboe.interfaces.domain.user.Preference;
import com.objectwave.persist.constraints.ConstraintCompare;
import java.util.Vector;

/**
 * An implementation of <code>PreferenceHome</code> that uses JavaGrinder
 * for persistence.
 *
 * @author John Wickberg
 */
public class PreferenceHomeImpl extends BOHome implements PreferenceHome {
	/**
	 * User key for system default preferences.
	 */
	private static final int SYSTEM_KEY = -1;
	/**
	 * Type for user preferences.
	 */
	private static final short USER_PREFERENCE = 1;
	/**
	 * Type for system default preferences.
	 */
	private static final short SYSTEM_PREFERENCE = 2;
	/**
	 * Type for system preferences for a user.
	 */
	private static final short SYSTEM_USER_PREFERENCE = 3;

	/**
	 * Creates user preference.
	 *
	 * @see PreferenceHome#createUserPreference
	 */
	public Preference createUserPreference(int userKey, String preferenceName, String value) {
		PreferenceImpl newPreference = new PreferenceImpl();
		addToContainer(newPreference);
		newPreference.create(userKey, USER_PREFERENCE, preferenceName, value);
		return newPreference;
	}

	/**
	 * Creates system preference.
	 *
	 * @see PreferenceHome#createSystemPreference
	 */
	public Preference createSystemPreference(String preferenceName, String value) {
		PreferenceImpl newPreference = new PreferenceImpl();
		addToContainer(newPreference);
		newPreference.create(SYSTEM_KEY, SYSTEM_PREFERENCE, preferenceName, value);
		return newPreference;
	}

	/**
	 * Creates system preference for user.
	 *
	 * @see PreferenceHome#createSystemPreferenceForUser
	 */
	public Preference createSystemPreferenceForUser(int userKey, String preferenceName, String value) {
		PreferenceImpl newPreference = new PreferenceImpl();
		addToContainer(newPreference);
		newPreference.create(userKey, SYSTEM_USER_PREFERENCE, preferenceName, value);
		return newPreference;
	}

	/**
	 * Finds user preferences by prefix.
	 *
	 * @see PreferenceHome#findUserPreferencesByPrefix
	 */
	public Preference[] findUserPreferencesByPrefix(int userKey, String prefix) {
		return findPreferences(userKey, USER_PREFERENCE, prefix);
	}

	/**
	 * Finds system preferences for a user.
	 *
	 * @see PreferenceHome#findSystemPreferencesForUser
	 */
	public Preference[] findSystemPreferencesForUser(int userKey, String prefix) {
		return findPreferences(userKey, SYSTEM_USER_PREFERENCE, prefix);
	}

	/**
	 * Finds system preferences by prefix.
	 *
	 * @see PreferenceHome#findSystemPreferencesx
	 */
	public Preference[] findSystemPreferences(String prefix) {
		return findPreferences(SYSTEM_KEY, SYSTEM_PREFERENCE, prefix);
	}

	/**
	 * Finds preferences having a given prefix.
	 *
	 * @param userKey owner of the preference
	 * @param type type of preference
	 * @param prefix beginning of preference name
	 * @return all preferences matching prefix
	 */
	private Preference[] findPreferences(int userKey, short type, String prefix) {
		PreferenceImpl example = new PreferenceImpl();
		addToContainer(example);
		ObjectQuery query = new ObjectQuery(example);
		example.setUserKey(userKey);
		example.setType(type);
		if (prefix != null && prefix.length() > 0) {
			prefix = prefix + "%";
			ConstraintCompare likeConstraint = new ConstraintCompare();
			likeConstraint.setPersistence(example);
			likeConstraint.setField("preferenceName");
			likeConstraint.setComparison("LIKE");
			likeConstraint.setCompValue(prefix);
			query.addConstraint(likeConstraint);
		}
		query.addOrderByField("preferenceName");
		Preference[] result;
		try {
			Vector queryResult = query.find();
			result = new Preference[queryResult.size()];
			queryResult.copyInto(result);
		}
		catch (Exception e) {
			Log.exception(this, "Preference query failed", e);
			result = new Preference[0];
		}
		return result;
	}

	/**
	 * Removes old preference.
	 *
	 * @see PreferenceHome#removePreference
	 */
	public void removePreference(Preference oldPreference) {
		try {
			((PreferenceImpl) oldPreference).markForDelete();
		}
		catch (PersistenceException e) {
			Log.exception(this, "Unable to delete preference: " + oldPreference, e);
		}
	}

} // end of PreferenceHomeImpl
