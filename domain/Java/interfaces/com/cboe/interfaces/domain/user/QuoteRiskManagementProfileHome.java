package com.cboe.interfaces.domain.user;

// Source file: d:/sources/com/cboe/interfaces/domain/quoteRiskManagementProfile/QuoteRiskManagementProfileHome.java

import com.cboe.idl.cmiQuote.*;
import com.cboe.exceptions.*;

/**
 * A manager for <code>QuoteRiskManagementProfile</code> instances.
 *
 * @author Steven Sinclair
 */
public interface QuoteRiskManagementProfileHome
{
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "QuoteRiskManagementProfileHome";

	/**
	 *  Either create a new profile or update an old one, depending on whether or not the userId/classKey combo already exists.
	 *
	 * @param user instance of User that the profile struct's information is to be associated with.
	 * @param quoteRiskManagementProfile CORBA struct containing quoteRiskManagementProfile information
	 * @return created/updated quoteRiskManagementProfile
	 */
	public QuoteRiskManagementProfile update(QRMUser user, QuoteRiskManagementProfileStruct newProfile);

	/**
	 * Creates a new quoteRiskManagementProfile.
	 *
	 * @param user instance of User that the profile struct's information is to be associated with.
	 * @param newQuoteRiskManagementProfile CORBA struct containing quoteRiskManagementProfile information
	 * @return created quoteRiskManagementProfile
	 * @exception AlreadyExistsException if quoteRiskManagementProfile already exists
	 */
	public QuoteRiskManagementProfile create(QRMUser user, QuoteRiskManagementProfileStruct newProfile) throws AlreadyExistsException;

	/**
	 * Searches for all defined quoteRiskManagementProfiles.
	 *
	 * @return all defined quoteRiskManagementProfiles
	 */
	public QuoteRiskManagementProfile[] findAll();

	/**
	 * Searches for requested quoteRiskManagementProfiles.
	 *
	 * @param user instance of User to use for location.
	 * @return found QuoteRiskManagementProfiles
	 */
	public QuoteRiskManagementProfile[] findByUser(QRMUser user);

	/**
	 * Searches for requested quoteRiskManagementProfiles.
	 *
	 * @param classKey key of the requested QuoteRiskManagementProfile
	 * @return found QuoteRiskManagementProfiles
	 */
	public QuoteRiskManagementProfile[] findByClassKey(int classKey);

	/**
	 * Searches for requested quoteRiskManagementProfile
	 * User & classKey should uniquely identify a profile.
	 *
	 * @param user - user to find profile for
	 * @param classKey - classKey to find profile for.
	 * @exception NotFoundException - thrown if the profile is not found.
	 */
	public QuoteRiskManagementProfile findByUserAndClassKey(QRMUser user, int classKey) throws NotFoundException;

	/**
	 * Updates a quoteRiskManagementProfile with values from the given struct.
	 * Updates the profile identified by user and classkey.
	 *
	 * @param user user to locate the profile for
	 * @param classKey classKey to locate the profile for
	 * @param updatedProfile QuoteRiskManagementProfileStruct to be updated
	 * @exception NotFoundException if profile for user/classkey pair not found.
	 */
	public void updateProfile(QRMUser user, QuoteRiskManagementProfileStruct updatedProfile) throws NotFoundException;

	/**
	 * Remove a profile
	 *
	 * @param profile the profile to remove.
	 */
	public void removeProfile(QuoteRiskManagementProfile profile);
}
